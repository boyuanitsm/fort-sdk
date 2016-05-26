package com.boyuanitsm.fort.sdk.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.boyuanitsm.fort.sdk.bean.OnUpdateSecurityResource;
import com.boyuanitsm.fort.sdk.cache.FortResourceCache;
import com.boyuanitsm.fort.sdk.config.FortConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * fort stomp client. subscribe on update security resource.
 *
 * @author zhanghua on 5/22/16.
 */
@Component
public class FortStompClient {

    private final Logger log = LoggerFactory.getLogger(FortClient.class);

    private FortConfiguration configuration;
    private FortClient client;
    // is connecting web socket
    private boolean connecting = false;

    @Autowired
    private FortResourceCache cache;

    @Autowired
    public FortStompClient(FortClient client, FortConfiguration configuration) {
        this.configuration = configuration;
        this.client = client;
        connect();
    }

    private void connect() {
        connecting = true;

        final WebSocketClient transport = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new StringMessageConverter());

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Cookie", client.getCookieString());

        stompClient.connect(String.format("%s/websocket/sa", configuration.getApp().getWebsocketServerBase()), headers, new MyWebSocketHandler())
                .addCallback(new SuccessCallback<StompSession>() {
                    public void onSuccess(StompSession stompSession) {
                        log.info("Connection fort cache update service success!");
                        connecting = false;
                    }
                }, new FailureCallback() {
                    public void onFailure(Throwable throwable) {
                        log.warn("Connection fort cache update service failure! {}", throwable.getMessage());
                        try {
                            // sleep 5s
                            Thread.sleep(5000);
                            // reconnect
                            connect();
                        } catch (InterruptedException e) {
                            log.error("sleep error", e);
                        }
                    }
                });
    }

    private class MyWebSocketHandler implements StompSessionHandler {

        @Override
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            stompSession.subscribe(String.format("/topic/%s/onUpdateSecurityResource", configuration.getApp().getAppKey()), new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders stompHeaders) {
                    return String.class;
                }
                @Override
                public void handleFrame(StompHeaders stompHeaders, Object o) {
                    try {
                        OnUpdateSecurityResource onUpdateSecurityResource = JSON.toJavaObject(JSONObject.parseObject(o.toString()), OnUpdateSecurityResource.class);
                        cache.updateResource(onUpdateSecurityResource);
                    } catch (Exception e) {
                        log.error("Update security resource error! {}", o, e);
                    }
                }
            });
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
            log.error("The fort cache update service handle exception!", throwable);
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
            if (throwable instanceof ConnectionLostException && !connecting) {
                log.warn("The fort cache update service connection lost!!! Reconnection...");
                connect();
            }

            if (!connecting) {
                log.error("The fort cache update service handle transport error! {}", throwable.getMessage());
            }
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return null;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
        }
    }
}
