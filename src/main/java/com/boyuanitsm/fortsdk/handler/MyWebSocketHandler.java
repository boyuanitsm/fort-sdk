package com.boyuanitsm.fortsdk.handler;

import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

/**
 * @author zhanghua on 5/11/16.
 */
public class MyWebSocketHandler implements StompSessionHandler {


    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        // stompSession.send("/topic/hello", "java client");

        stompSession.subscribe("/topic/admin/greetings", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                System.out.println(stompHeaders);
                System.out.println(o);
            }
        });
    }

    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {

    }

    public void handleTransportError(StompSession stompSession, Throwable throwable) {

    }

    public Type getPayloadType(StompHeaders stompHeaders) {
        return null;
    }

    public void handleFrame(StompHeaders stompHeaders, Object o) {
        // System.out.println(stompHeaders);
        System.out.println(o);
    }
}
