package com.example.webflux.controller.v1;

import com.example.webflux.document.ItemCapped;
import com.example.webflux.repository.ItemCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.example.webflux.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

@RestController
public class ItemsStreamController {

    @Autowired
    ItemCappedRepository itemCappedRepository;

    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream(){
        return itemCappedRepository.findItemsBy();
    }
}
