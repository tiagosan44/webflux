package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        final Flux<String> stringFlux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Exception occurred")))
            .concatWith(Flux.just("D"))
            .onErrorResume((e) -> {
                System.out.println("Exception is " + e);
                return Flux.just("Default", "Default1");
            });
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("Default", "Default1")
            .verifyComplete();
    }

    @Test
    public void fluxError_OnErrorReturn() {
        final Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("Default");
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("Default")
                .verifyComplete();
    }

    @Test
    public void fluxError_OnErrorMap() {
        final Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxError_OnErrorMapRetry() {
        final Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }
}
