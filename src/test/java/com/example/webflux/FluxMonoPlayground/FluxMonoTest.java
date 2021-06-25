package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxMonoTest {

    @Test
    public void  fluxTest() {
        final Flux<String> stringFlux = Flux.just("Spring", "Springboot", "Reactor spring")
                .concatWith(Flux.error(new RuntimeException("Flux error")))
                .log();
        stringFlux.subscribe(System.out::println,
                System.out::println);
    }

    @Test
    public void fluxTestElementsWithoutError() {
        final Flux<String> stringFlux = Flux.just("Spring", "Springboot", "Reactor spring")
                .log();
        StepVerifier.create(stringFlux)
            .expectNext("Spring")
            .expectNext("Springboot")
            .expectNext("Reactor spring")
            .verifyComplete();
    }

    @Test
    public void fluxTestElementsWithError() {
        final Flux<String> stringFlux = Flux.just("Spring", "Springboot", "Reactor spring")
                .concatWith(Flux.error(new RuntimeException("Flux error")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Springboot")
                .expectNext("Reactor spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxTestElementsCountWithError() {
        final Flux<String> stringFlux = Flux.just("Spring", "Springboot", "Reactor spring")
                .concatWith(Flux.error(new RuntimeException("Flux error")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxTestElementsListWithError() {
        final Flux<String> stringFlux = Flux.just("Spring", "Springboot", "Reactor spring")
                .concatWith(Flux.error(new RuntimeException("Flux error")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Springboot", "Reactor spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier.create(stringMono.log())
            .expectNext("Spring")
            .verifyComplete();
    }

    @Test
    public void monoError() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
