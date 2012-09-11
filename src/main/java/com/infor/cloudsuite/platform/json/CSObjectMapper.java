package com.infor.cloudsuite.platform.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Json ObjectMapper extension.
 * Used to set some default serializaion settings for our JSON transport.
 * User: bcrow
 * Date: 10/27/11 1:44 PM
 */
public class CSObjectMapper extends ObjectMapper {
    public CSObjectMapper() {
        //setting FAIL_ON_EMPTY_BEANS to false allows us to send hibernate entity beans that
        //have lazily fetched items, the lazy item will be null.
        configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //Serializaion setting of NON_NULL serializes only NON-NULL properties.
        getSerializationConfig().withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
}
