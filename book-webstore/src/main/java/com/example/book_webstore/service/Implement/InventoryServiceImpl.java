package com.example.book_webstore.service.Implement;

import org.springframework.stereotype.Service;

import com.example.book_webstore.model.Inventory;
import com.example.book_webstore.repository.InventoryRepository;
import com.example.book_webstore.service.InventoryService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void decreaseStock(Long bookId, int quantity) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for book id: " + bookId));
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for book id: " + bookId);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void increaseStock(Long bookId, int quantity) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for book id: " + bookId));
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public int getStockLevel(Long bookId) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for book id: " + bookId));
        return inventory.getQuantity();
    }

}
