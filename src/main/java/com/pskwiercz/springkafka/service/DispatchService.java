package com.pskwiercz.springkafka.service;

import com.pskwiercz.springkafka.message.OrderCreated;
import com.pskwiercz.springkafka.message.OrderDispatched;
import com.pskwiercz.springkafka.message.DispatchPreparing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";
    private final static String ORDER_DISPATCHED_TOPIC = "order.dispatched";
    private static final UUID APPLICATION_ID = UUID.randomUUID();
    private final KafkaTemplate<String, Object> kafkaProducer;


    public void process(OrderCreated orderCreated) throws Exception {
            DispatchPreparing dispatchPreparing = DispatchPreparing.builder()
                    .orderId(orderCreated.getOrderId())
                    .build();
            kafkaProducer.send(DISPATCH_TRACKING_TOPIC, dispatchPreparing).get();

            OrderDispatched orderDispatched = OrderDispatched.builder()
                    .orderId(orderCreated.getOrderId())
                    .processedById(APPLICATION_ID)
                    .notes("Dispatched: " + orderCreated.getItem())
                    .build();
            kafkaProducer.send(ORDER_DISPATCHED_TOPIC, orderDispatched).get();

            log.info("Msg Id: {} processedById: {}", orderCreated.getOrderId(), APPLICATION_ID);
        }
}
