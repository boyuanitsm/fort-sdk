package com.boyuanitsm.fortsdk.websocket;


import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghua on 5/11/16.
 */
public class StompWebsocketClient {

    public static void main(String[] args) throws Exception {
        List<Transport> transports = new ArrayList<Transport>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Cookie", "JSESSIONID=AEB9202EB69D8A6188C18838BD51920C; remember-me=VjZlUU13NWlVMXdYQTFIcFFyNURGdz09Ojh1TU1RbFR2RUx2bmkrOEJvUTdMWEE9PQ; NG_TRANSLATE_LANG_KEY=%22en%22; CSRF-TOKEN=ad6e7b2a-a848-49a6-a5c4-774715bc0df4");

        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        ListenableFuture<StompSession> future = stompClient.connect("ws://localhost:8080/websocket/greetings", headers, new MyWebSocketHandler());

        future.addCallback(new SuccessCallback<StompSession>() {
            public void onSuccess(StompSession stompSession) {
                System.out.println("on Success!");
            }
        }, new FailureCallback() {
            public void onFailure(Throwable throwable) {
                System.out.println("on Failure!");
            }
        });

        Thread.sleep(100000000);
    }
}
