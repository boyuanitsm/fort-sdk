package com.boyuanitsm.fortsdk.client;


import com.boyuanitsm.fortsdk.handler.MyWebSocketHandler;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghua on 5/11/16.
 */
public class WebSocketSTOMPClient {

    public static void main(String[] args) throws Exception {
        List<Transport> transports = new ArrayList<Transport>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Cookie", "JSESSIONID=26D343966A35EA2DD183A63CED65F344; remember-me=QnFCM0tHU09CQlpudzg1aU1EbEY3UT09OnRqTVlrQUNIbEF4UjNvdTRoaXNmQXc9PQ; NG_TRANSLATE_LANG_KEY=%22en%22; CSRF-TOKEN=fcfa7528-094f-4e86-aa03-2a5745fce2f6");

        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        ListenableFuture<StompSession> future = stompClient.connect("ws://localhost:8080/client/greetings", headers, new MyWebSocketHandler());

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
