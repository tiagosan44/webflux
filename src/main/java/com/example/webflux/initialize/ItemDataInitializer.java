package com.example.webflux.initialize;

import com.example.webflux.document.Item;
import com.example.webflux.document.ItemCapped;
import com.example.webflux.repository.ItemCappedRepository;
import com.example.webflux.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemCappedRepository itemCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
        createCappedCollection();
        cappedData();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    private void cappedData() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "RandomItem " + i, 100.00 + i));
        itemCappedRepository
                .insert(itemCappedFlux)
                .subscribe(item -> {
                   log.info("Inserted item is: " + item);
                });
    }

    private void initialDataSetUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemRepository::save)
                .thenMany(itemRepository.findAll())
                .subscribe(item -> {
                    System.out.println("ITem inserted from commandline runner :" + item);
                });
    }

    private List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 410.0),
                new Item(null, "Apple watch", 470.0),
                new Item(null, "Beats headphones", 325.0),
                new Item("ABC", "Bose headphones", 330.0));
    }
}
