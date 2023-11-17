package ca.gbc.orderservice.service;

import ca.gbc.orderservice.dto.OrderRequest;

public interface IOrderService {
    void placeOrder(OrderRequest orderRequest);
}
