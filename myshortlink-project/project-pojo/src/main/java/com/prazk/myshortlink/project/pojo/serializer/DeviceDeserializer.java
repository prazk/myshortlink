package com.prazk.myshortlink.project.pojo.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class DeviceDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String device = p.getText();
        if ("PC".equals(device)) {
            return 0;
        } else if ("Mobile".equals(device)) {
            return 1;
        } else {
            return Integer.parseInt(device);
        }
    }
}
