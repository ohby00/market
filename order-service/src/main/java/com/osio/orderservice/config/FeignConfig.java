package com.osio.orderservice.config;

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Configuration
public class FeignConfig {

    @Bean
    public Decoder feignDecoder() {
        ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters(new StringHttpMessageConverter());
        return new ResponseEntityDecoder(new SpringDecoder(messageConverters));
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {

        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            String message = "Unknown error";
            try {
                if (response.body() != null) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(response.body().asInputStream().readAllBytes());
                    message = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new CustomFeignClientException(response.status(), message);
        }
    }

    public static class CustomFeignClientException extends RuntimeException {
        private final int status;

        public CustomFeignClientException(int status, String message) {
            super(message);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
