package com.boyuanitsm.fort.sdk.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Build Jackson ObjectMapper
 *
 * @author hooks on 6/15/16.
 */
public class ObjectMapperBuilder {

    public static ObjectMapper build() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
