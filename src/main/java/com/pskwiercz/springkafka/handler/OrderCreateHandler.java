package com.pskwiercz.springkafka.handler;

import com.pskwiercz.springkafka.message.OrderCreated;
import com.pskwiercz.springkafka.service.DispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
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
    public void listen(@Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderCreated payload) {

        log.info("Partition: {}, Key: {}, Payload: {}", partition, key, payload);
        try {
            dispatchService.process(key, payload);
        } catch (Exception e) {
            log.error("Error processing payload: {}", payload, e);
        }
    }
}
