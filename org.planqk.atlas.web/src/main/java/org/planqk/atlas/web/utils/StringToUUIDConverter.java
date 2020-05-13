package org.planqk.atlas.web.utils;

import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

public class StringToUUIDConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(String s) {
        System.out.println("Converter executed");
        return UUID.fromString(s);
    }
}
