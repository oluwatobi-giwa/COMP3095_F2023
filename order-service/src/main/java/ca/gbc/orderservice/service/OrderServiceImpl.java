package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.InventoryRequest;
import ca.gbc.orderservice.dto.InventoryResponse;
import ca.gbc.orderservice.dto.OrderLineItemDTO;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.model.OrderLineItem;
import ca.gbc.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional


public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Value("${inventory.service.url}")
    private String inventoryApiUri;

    @Override
    public String placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems =
                orderRequest.getOrderLineItemDTOList()
                        .stream()
                        .map(this::mapToDto)
                        .toList();

        order.setOrderLineItemList(orderLineItems);

        List<InventoryRequest> inventoryRequests = order.getOrderLineItemList()
                .stream().map(orderLineItem -> InventoryRequest
                        .builder()
                        .skuCode(orderLineItem.getSkuCode())
                        .quantity(orderLineItem.getQuantity())
                        .build())
                .toList();

        List<InventoryResponse> inventoryResponseList = webClient
                .post()
                .uri(inventoryApiUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryRequests)
                .retrieve()
                .bodyToFlux(InventoryResponse.class)
                .collectList()
                .block();

        assert inventoryResponseList != null;
        boolean allProductsInStock = inventoryResponseList
                .stream()
                .allMatch(InventoryResponse::isSufficientStock);

        if(Boolean.TRUE.equals(allProductsInStock)){
            orderRepository.save(order);
            return "Order Placed Successfully";
        }else{
            throw new RuntimeException("Not all products are in stock,order cannot be placed");
        }

    }

    private OrderLineItem mapToDto(OrderLineItemDTO orderLineItemDTO){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDTO.getPrice());
        orderLineItem.setQuantity(orderLineItemDTO.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDTO.getSkuCode());
        return orderLineItem;
    }

}
