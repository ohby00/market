package com.osio.orderservice.service;

import com.osio.orderservice.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    @Value("${activemq.queue.name}")
    private String queueName;

    // jmsTemplate 을 통해 메세지 송신 가능
    private final JmsTemplate jmsTemplate;

    /**
     * Queue 로 메세지를 발행
     * messageDto -> Producer 가 Queue 발행한 메세지 Class
     */
    public void sendMessage(MessageDto messageDto) {
        log.info("message sent : {}", messageDto.toString());
        // queueName(Sample-queue) 에 메세지 전송
        jmsTemplate.convertAndSend(queueName,messageDto);
    }

    @JmsListener(destination = "${activemq.queue.name}")
    public void receiveMessage(MessageDto messageDto) {
        log.info("Received message : {}",messageDto.toString());
    }
}