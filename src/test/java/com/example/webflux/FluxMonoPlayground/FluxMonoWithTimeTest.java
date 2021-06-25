package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        final Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100)).log();
        infiniteFlux.subscribe(e -> System.out.println("Value is " + e));
        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        final Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100)).take(3).log();
        StepVerifier.create(infiniteFlux)
            .expectSubscription()
            .expectNext(0L, 1L, 2L)
            .verifyComplete();
    }
}
