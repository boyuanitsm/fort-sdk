package com.boyuanitsm.fortsdk.websocket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhanghua on 5/11/16.
 */
@ClientEndpoint(configurator = MyClientConfigurator.class)
public class SecurityWebsocketClient {

    private static CountDownLatch latch;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Unscramble the word ...." + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("on close websocket");
        latch.countDown();
    }

    @OnError
    public void error(Session session, Throwable t) {
        System.err.println("Error on session " + session.getId());
    }

    public static void main(String[] args) {
        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();
        try {
            // URI uri =  new URI("ws://localhost:8080/websocket/tracker/255/5oizsgys/websocket");
            URI uri =  new URI("ws://localhost:8080/websocket/greetings");
            Session session = client.connectToServer(SecurityWebsocketClient.class, uri);
            session.getAsyncRemote().sendText("{\"page\": \"websocket\"}");
            latch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}