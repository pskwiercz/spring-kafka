package com.pskwiercz.springkafka.handler;

import com.pskwiercz.springkafka.message.OrderCreated;
import com.pskwiercz.springkafka.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderCreateHandler {

    private final DispatchService dispatchService;

    @KafkaListener(
            id = "orderConsumerClient",
            topics = "order.created",
            groupId = "dispatch.order.created.consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(OrderCreated payload) {
        log.info("Payload: {}", payload);
        dispatchService.process(payload);
    }
}
