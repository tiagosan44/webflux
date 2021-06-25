package com.example.webflux.handler;

import com.example.webflux.document.Item;
import com.example.webflux.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.example.webflux.constants.ItemConstants.ITEM_END_POINT_V1;
import static com.example.webflux.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemRepository itemRepository;

    private List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 410.0),
                new Item(null, "Apple watch", 470.0),
                new Item(null, "Beats headphones", 325.0),
                new Item("ABC", "Bose headphones", 330.0));
    }

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemRepository::save)
                .doOnNext(item -> {
                    System.out.println("ITem inserted to test :" + item);
                })
                .blockLast();
    }

    @Test
    void getAllItems() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    void getAllItemsApproach2() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    response.getResponseBody().forEach(item -> {
                        assertNotNull(item.getId());
                    });
                });
    }

    @Test
    void getAllItemsApproach3() {
        final Flux<Item> responseBody = webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .returnResult(Item.class)
                .getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void getItemById() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 330.0);
    }

    @Test
    void getItemByIdNotFound() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        final Item item = new Item(null, "Iphone X", 999.99);
        webTestClient.post().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo("999.99")
        ;
    }

    @Test
    void deleteITem() {
        webTestClient.delete().uri(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "ABC")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        double newPrice = 430.0;
        final Item item = new Item(null, "Bose headphones", newPrice);
        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}", "ABC")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    public void runtimeException() {
        webTestClient.get().uri("/fun/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message", "Runtime exception occurred");
    }
}