package com.example.webflux.handler;

import com.example.webflux.document.ItemCapped;
import com.example.webflux.repository.ItemCappedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.example.webflux.constants.ItemConstants.ITEM_FUNCTIONAL_STREAM_END_POINT_V1;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemCappedRepository itemCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @BeforeEach
    public void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(1))
                .map(i -> new ItemCapped(null, "RandomItem " + i, 100.00 + i))
                .take(5);
        itemCappedRepository
                .insert(itemCappedFlux)
                .doOnNext(item -> {
                    System.out.println("Inserted item is: " + item);
                }).blockLast();
    }

    @Test
    void getItemsStream() {
        final Flux<ItemCapped> itemsFlux = webTestClient.get().uri(ITEM_FUNCTIONAL_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);
        StepVerifier.create(itemsFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
