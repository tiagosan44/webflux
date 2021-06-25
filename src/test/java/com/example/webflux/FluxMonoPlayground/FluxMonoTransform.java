package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxMonoTransform {

    List<String> names = Arrays.asList("Pedro", "Pablo", "Jacinto", "Jose");

    @Test
    public void transformUsingMap() {
        final Flux<String> stringFlux = Flux.fromIterable(names)
            .map(s -> s.toUpperCase(Locale.ROOT))
            .log();
        StepVerifier.create(stringFlux)
            .expectNext("PEDRO", "PABLO", "JACINTO", "JOSE")
            .verifyComplete();
     }

    @Test
    public void transformUsingMap_Repeat() {
        final Flux<String> stringFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase(Locale.ROOT))
                .repeat(1)
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("PEDRO", "PABLO", "JACINTO", "JOSE", "PEDRO", "PABLO", "JACINTO", "JOSE")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        final Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }).log();
        StepVerifier.create(stringFlux)
            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_Parallel() {
        final Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D"))
                .window(2) //Flux<Flux<String>> -> (A, B) (C,D)
                .flatMap((s) ->
                    s.map(this::convertToList).subscribeOn(parallel())) //Flux<List<String>>
                        .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_ParallelSequential() {
        final Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D"))
                .window(2) //Flux<Flux<String>> -> (A, B) (C,D)
                .flatMapSequential((s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) //Flux<String>
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(8)
                .verifyComplete();
    }

    private List<String> convertToList(String s)  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "New item");
    }
}
