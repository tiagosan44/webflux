 package com.example.webflux.repository;

 import com.example.webflux.document.Item;
 import org.junit.Before;
 import org.junit.jupiter.api.BeforeAll;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.junit.runner.RunWith;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
 import org.springframework.test.context.junit4.SpringRunner;
 import reactor.core.publisher.Flux;
 import reactor.core.publisher.Mono;
 import reactor.test.StepVerifier;

 import java.util.Arrays;
 import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    List<Item> itemsList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 410.0),
            new Item(null, "Apple watch", 470.0),
            new Item(null, "Beats headphones", 325.0),
            new Item("ABC", "Bose headphones", 330.0));

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll()
            .thenMany(Flux.fromIterable(itemsList))
            .flatMap(itemRepository::save)
            .doOnNext(System.out::println)
            .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(i -> i.getDescription().equals("Bose headphones"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(itemRepository.findByDescription("Bose headphones"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        final Item item = new Item(null, "Xbox ond series X", 500.0);
        final Mono<Item> savedItem = itemRepository.save(item);
        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches(i -> i.getId() != null && i.getDescription().equals("Xbox ond series X"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        Double newPrice = 520.0;
        final Flux<Item> updatedItem = itemRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> itemRepository.save(item));
        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        final Mono<Void> deletedItem = itemRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> itemRepository.deleteById(id));
        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}