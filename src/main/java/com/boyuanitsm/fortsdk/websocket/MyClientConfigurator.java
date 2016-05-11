package com.boyuanitsm.fortsdk.websocket;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghua on 5/11/16.
 */
public class MyClientConfigurator extends ClientEndpointConfig.Configurator {

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        headers.put("Cookie", Arrays.asList("JSESSIONID=A8E52DA22E2BE9BDD174DF73E0A6237A; remember-me=TGRveXlmMmpSdVRJRmZXSThGQ0tFQT09OnBsRWgxTWVwNFlmOVBDZVd3Mi93ZUE9PQ; NG_TRANSLATE_LANG_KEY=%22en%22; CSRF-TOKEN=4d238150-7adf-4026-8ac1-d998b7b912c5"));
    }

    @Override
    public void afterResponse(HandshakeResponse handshakeResponse) {
        final Map<String, List<String>> headers = handshakeResponse.getHeaders();
        System.out.println(headers);
    }
}
