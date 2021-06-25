package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoFilterTest {

    List<String> names = Arrays.asList("Pedro", "Pablo", "Jacinto", "Jose");

    @Test
    public void filterTest(){
        final Flux<String> fluxString = Flux.fromIterable(names)
            .filter(s -> s.startsWith("P"))
            .log();
        StepVerifier.create(fluxString)
            .expectNext("Pedro", "Pablo")
            .verifyComplete();
    }
}
