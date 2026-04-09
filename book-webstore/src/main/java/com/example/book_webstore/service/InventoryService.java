package com.example.book_webstore.service;

public interface InventoryService {
    void decreaseStock(Long bookId, int quantity);

    void increaseStock(Long bookId, int quantity);

    int getStockLevel(Long bookId);

    void setStockLevel(Long bookId, int quantity);
}
