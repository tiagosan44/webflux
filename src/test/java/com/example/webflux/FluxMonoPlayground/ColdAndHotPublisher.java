package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisher {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        final Flux<String> delayElements = Flux.just("A", "B", "C", "D").delayElements(Duration.ofSeconds(1));
        delayElements.subscribe(s -> System.out.println("Subscriber 1 " + s));
        Thread.sleep(2000);
        delayElements.subscribe(s -> System.out.println("Subscriber 2 " + s));
        Thread.sleep(6000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        final Flux<String> delayElements = Flux.just("A", "B", "C", "D").delayElements(Duration.ofSeconds(1));
        final ConnectableFlux<String> connectableFlux = delayElements.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 " + s));
        Thread.sleep(2000);
        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 " + s));
        Thread.sleep(4000);
    }
}
