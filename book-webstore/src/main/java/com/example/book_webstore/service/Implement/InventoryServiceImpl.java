package com.example.book_webstore.service.Implement;

import org.springframework.stereotype.Service;

import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.Inventory;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.InventoryRepository;
import com.example.book_webstore.service.InventoryService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BookRepository bookRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
            BookRepository bookRepository) {
        this.inventoryRepository = inventoryRepository;
        this.bookRepository = bookRepository;
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
                .orElseGet(() -> {
                    Inventory inv = new Inventory();

                    Book book = bookRepository.findById(bookId)
                            .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

                    inv.setBook(book);
                    inv.setQuantity(0);

                    return inv;
                });

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public int getStockLevel(Long bookId) {
        return inventoryRepository.findByBookId(bookId)
                .map(Inventory::getQuantity)
                .orElse(0);
    }

    @Override
    public void setStockLevel(Long bookId, int quantity) {
        Inventory inventory = inventoryRepository.findByBookId(bookId)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();

                    Book book = bookRepository.findById(bookId)
                            .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

                    inv.setBook(book);

                    return inv;
                });

        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }
}