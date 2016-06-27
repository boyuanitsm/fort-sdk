package com.boyuanitsm.fort.sdk;


import com.boyuanitsm.fort.sdk.bean.OnUpdateSecurityResource;
import com.boyuanitsm.fort.sdk.client.ManagerClient;
import com.boyuanitsm.fort.sdk.config.FortProperties;
import com.boyuanitsm.fort.sdk.util.ObjectMapperBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

/**
 * Fort resource synchronize service is an websocket stomp client. subscribe on update security resource.
 *
 * @author zhanghua on 5/22/16.
 */
@Component
public class ResourceSyncService {

    private final Logger log = LoggerFactory.getLogger(ResourceSyncService.class);

    private FortProperties fortProperties;
    private ManagerClient client;
    // is connecting web socket
    private boolean connecting = false;

    @Autowired
    private ResourceManager resourceManager;

    private ObjectMapper mapper;

    @Autowired
    public ResourceSyncService(ManagerClient client, FortProperties fortProperties) {
        mapper = ObjectMapperBuilder.build();
        this.fortProperties = fortProperties;
        this.client = client;

        if (fortProperties.getResourceSync().isEnable()) {
            connect();
        } else {
            log.warn("Disabled fort resource synchronize service!");
        }
    }

    /**
     * Connection fort resourceManager update service. if connection failure, reconnection.
     */
    private void connect() {
        // set connecting
        connecting = true;

        // create web socket client
        final WebSocketClient transport = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new StringMessageConverter());
        // add headers
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Cookie", client.getCookieString());

        // do connection
        stompClient.connect(String.format("%s/websocket/sa", fortProperties.getApp().getWebsocketServerBase()), headers, new MyWebSocketHandler())
                .addCallback(new SuccessCallback<StompSession>() {
                    public void onSuccess(StompSession stompSession) {
                        log.info("Connection fort resource synchronize service success!");
                        // set connecting false
                        connecting = false;
                    }
                }, new FailureCallback() {
                    public void onFailure(Throwable throwable) {
                        log.warn("Connection fort resource synchronize service fail! {}", throwable.getMessage());
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
            // subscribe message on update security resource
            stompSession.subscribe(String.format("/topic/%s/onUpdateSecurityResource", fortProperties.getApp().getAppKey()), new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders stompHeaders) {
                    return String.class;
                }
                @Override
                public void handleFrame(StompHeaders stompHeaders, Object o) {
                    try {
                        // update resourceManager
                        OnUpdateSecurityResource onUpdateSecurityResource = mapper.readValue(o.toString(), OnUpdateSecurityResource.class);
                        resourceManager.updateResource(onUpdateSecurityResource);
                    } catch (Exception e) {
                        log.error("Synchronize resource error! {}", o, e);
                    }
                }
            });
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
            log.error("The fort resource synchronize service handle exception!", throwable);
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
            // connection lost !!!
            if (throwable instanceof ConnectionLostException && !connecting) {
                log.warn("The fort resource synchronize service connection lost!!! Reconnection...");
                connect();
            }

            if (!connecting) {
                log.error("The fort resource synchronize service handle transport error! {}", throwable.getMessage());
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
