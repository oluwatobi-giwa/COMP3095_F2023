package ca.gbc.inventoryservice.service;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse>isInStock(List<InventoryRequest> requests) {
        /*log.info("Wait Started");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while waiting: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        log.info("Wait Stopped");*/

        List<Inventory> availableInventory = inventoryRepository.findAllByInventoryRequest(requests);

        return requests.stream().map(request -> {

            boolean isInStock = availableInventory.stream()
                    .anyMatch(inventory -> inventory.getSkuCode().equals(request.getSkuCode())
                            && inventory.getQuantity()>=request.getQuantity());

            if(isInStock){
                return InventoryResponse.builder()
                        .skuCode(request.getSkuCode())
                        .sufficientStock(true)
                        .build();
            }else{
                return InventoryResponse.builder()
                        .skuCode(request.getSkuCode())
                        .sufficientStock(false)
                        .build();
            }
        }).toList();

    }
}
