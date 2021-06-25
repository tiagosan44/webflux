package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxMonoFactoryTest {

    List<String> names = Arrays.asList("Pedro", "Pablo", "Jacinto", "Jose");

    @Test
    public void fluxUsingIterable() {
        final Flux<String> namesFlux = Flux.fromIterable(names).log();
        StepVerifier.create(namesFlux)
            .expectNext("Pedro", "Pablo", "Jacinto", "Jose")
            .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] namesArray = {"Pedro", "Pablo", "Jacinto", "Jose"};
        final Flux<String> namesFlux = Flux.fromArray(namesArray).log();
        StepVerifier.create(namesFlux)
            .expectNext("Pedro", "Pablo", "Jacinto", "Jose")
            .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        final Flux<String> namesFlux = Flux.fromStream(names.stream()).log();
        StepVerifier.create(namesFlux)
                .expectNext("Pedro", "Pablo", "Jacinto", "Jose")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        final Mono<String> mono = Mono.justOrEmpty(null);
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        final Supplier<String> supplier = () -> "Pedro";
        final Mono<String> mono = Mono.fromSupplier(supplier);
        StepVerifier.create(mono)
                .expectNext("Pedro")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        final Flux<Integer> mono = Flux.range(1, 5);
        StepVerifier.create(mono)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }
}
