package com.prazk.myshortlink.project.pojo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 序列化展示给前端的device
 */
public class DeviceSerializer extends JsonSerializer<Integer> {
    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        // 0 - PC, 1 - Mobile
        if (Integer.valueOf(0).equals(value)) {
            gen.writeString("PC");
        } else if (Integer.valueOf(1).equals(value)){
            gen.writeString("Mobile");
        } else {
            gen.writeString(value.toString());
        }
    }
}