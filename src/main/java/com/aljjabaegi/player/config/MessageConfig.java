package com.aljjabaegi.player.config;

import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
/**
 * message.properties 에서 message 를 조회하는 메서드
 * @author GEON LEE
 * @since 2024-01-08
 * */
@Configuration
public class MessageConfig {
    private final Properties msgProp;

    public MessageConfig() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream("message/message.properties")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input), StandardCharsets.UTF_8));
            this.msgProp = new Properties();
            msgProp.load(reader);
        }
    }
    /**
     * message.properties 에서 메시지를 리턴하는 메서드
     */
    public String getMsg(String key) {
        return msgProp.getProperty(key);
    }
}