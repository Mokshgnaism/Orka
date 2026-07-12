package com.Orka.config;

import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
public class ProtobufConfiguration {

    @Bean
    ServerHttpMessageConvertersCustomizer protobufCustomizer() {

        return converters -> converters.addCustomConverter(
                new ProtobufJsonFormatHttpMessageConverter()
        );

    }

}