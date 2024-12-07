package com.pskwiercz.springkafka.service;

import com.pskwiercz.springkafka.message.OrderCreated;
import com.pskwiercz.springkafka.message.OrderDispatched;
import com.pskwiercz.springkafka.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DispatchServiceTest {

    private DispatchService service;
    @Mock
    private KafkaTemplate kafkaProducerMock;
    @Mock
    private CompletableFuture completableFutureMock;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new DispatchService(kafkaProducerMock);
    }

    @Test
    void processSuccessTest() throws Exception{
        when(kafkaProducerMock.send(anyString(), any(OrderDispatched.class)))
                .thenReturn(completableFutureMock);

        OrderCreated event = TestEventData.buildOrderCreatedEvent(UUID.randomUUID(), UUID.randomUUID().toString());
        service.process(event);

        verify(kafkaProducerMock, times(1))
                .send(eq("order.dispatched"), any(OrderDispatched.class));

    }

    @Test
    public void processThrowsExceptionTest() {
        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        doThrow(new RuntimeException("Producer failure")).when(kafkaProducerMock).send(eq("order.dispatched"), any(OrderDispatched.class));

        Exception exception = assertThrows(RuntimeException.class, () -> service.process(testEvent));

        verify(kafkaProducerMock, times(1))
                .send(eq("order.dispatched"), any(OrderDispatched.class));
        assertThat(exception.getMessage(), equalTo("Producer failure"));
    }

}