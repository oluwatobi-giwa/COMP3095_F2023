package ca.gbc.inventoryservice.service;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface IInventoryService {
    /*List<InventoryResponse> isInStock(List<InventoryRequest> requests);*/
    boolean isInStock(String skuCode);
}
