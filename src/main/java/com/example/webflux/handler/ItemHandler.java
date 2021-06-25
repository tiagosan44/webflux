package com.example.webflux.handler;

import com.example.webflux.document.Item;
import com.example.webflux.document.ItemCapped;
import com.example.webflux.repository.ItemCappedRepository;
import com.example.webflux.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemHandler {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemCappedRepository itemCappedRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest request) {
        final Mono<Item> itemMono = itemRepository.findById(request.pathVariable("id"));
        return itemMono.flatMap(item ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(item)))
                .switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> createItem(ServerRequest request) {
        final Mono<Item> itemMono = request.bodyToMono(Item.class);
        return itemMono.flatMap(item -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(itemRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        final Mono<Void> deletedItem = itemRepository.deleteById(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deletedItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest request) {
        final Mono<Item> updatedItem = request.bodyToMono(Item.class)
                .flatMap(newItem -> itemRepository.findById(request.pathVariable("id"))
                        .flatMap(currentItem -> {
                            currentItem.setDescription(newItem.getDescription());
                            currentItem.setPrice(newItem.getPrice());
                            return itemRepository.save(currentItem);
                        }));
        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(item)))
            .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> itemsException(ServerRequest request) {
        throw new RuntimeException("Runtime exception occurred");
    }

    public Mono<ServerResponse> itemsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
