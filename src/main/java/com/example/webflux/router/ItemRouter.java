package com.example.webflux.router;

import com.example.webflux.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.example.webflux.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static com.example.webflux.constants.ItemConstants.ITEM_FUNCTIONAL_STREAM_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(final ItemHandler itemHandler){
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemHandler::getItemById)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRouter(ItemHandler itemHandler) {
        return RouterFunctions.route(GET("/fun/runtimeException").and(accept(MediaType.APPLICATION_JSON)), itemHandler::itemsException);
    }

    @Bean
    public RouterFunction<ServerResponse> itemsStreamRouter(ItemHandler itemHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_STREAM_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON)), itemHandler::itemsStream);
    }
}
