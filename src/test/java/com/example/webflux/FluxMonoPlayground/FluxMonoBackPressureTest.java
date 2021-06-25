package com.example.webflux.FluxMonoPlayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        final Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(System.out::println,
                error -> System.out.println("Exception is " + error),
                () -> System.out.println("Done"),
                subscription -> subscription.request(2));
    }

    @Test
    public void backPressureCancelTest() {
        final Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(System.out::println,
                error -> System.out.println("Exception is " + error),
                () -> System.out.println("Done"),
                subscription -> subscription.cancel());
    }

    @Test
    public void customizedBackPressure() {
        final Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is " + value);
                if(value == 4) {
                    cancel();
                }
            }
        });
    }
}
