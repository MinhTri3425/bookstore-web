package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.CartItemDTO;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.BookImage;
import com.example.book_webstore.model.Cart;
import com.example.book_webstore.model.CartItem;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.CartItemRepository;
import com.example.book_webstore.repository.CartRepository;
import com.example.book_webstore.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CartDTO getOrCreateCart(Long cartId) {
        Cart cart;
        if (cartId == null) {
            cart = cartRepository.save(new Cart());
        } else {
            cart = cartRepository.findById(cartId).orElseGet(() -> cartRepository.save(new Cart()));
        }
        return toCartDTO(cart);
    }

    @Override
    public CartDTO addToCart(Long cartId, Long bookId, int quantity) {
        int safeQuantity = Math.max(quantity, 1);
        Cart cart = getOrCreateEntity(cartId);
        Book book = bookRepository.findByIdWithImages(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setBook(book);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + safeQuantity);
        cartItemRepository.save(item);
        return toCartDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    public CartDTO removeFromCart(Long cartId, Long bookId) {
        Cart cart = getOrCreateEntity(cartId);
        cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).ifPresent(cartItemRepository::delete);
        return toCartDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public long getItemCount(Long cartId) {
        if (cartId == null) {
            return 0;
        }
        return cartItemRepository.findByCartId(cartId).stream().mapToLong(CartItem::getQuantity).sum();
    }

    private Cart getOrCreateEntity(Long cartId) {
        if (cartId == null) {
            return cartRepository.save(new Cart());
        }
        return cartRepository.findById(cartId).orElseGet(() -> cartRepository.save(new Cart()));
    }

    @Transactional(readOnly = true)
    private CartDTO toCartDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        List<CartItemDTO> itemDTOs = cartItemRepository.findByCartId(cart.getId()).stream().map(item -> {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setBook(toBookDTO(item.getBook()));
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

    private BookDTO toBookDTO(Book book) {
        Book hydrated = bookRepository.findByIdWithImages(book.getId()).orElse(book);
        BookDTO dto = new BookDTO();
        dto.setId(hydrated.getId());
        dto.setTitle(hydrated.getTitle());
        dto.setPrice(hydrated.getPrice());
        dto.setDescription(hydrated.getDescription());
        dto.setImages(hydrated.getImages().stream()
            .sorted(Comparator.comparingInt(BookImage::getSortOrder))
                .map(img -> {
                    BookImageDTO imageDTO = new BookImageDTO();
                    imageDTO.setId(img.getId());
                    imageDTO.setUrl(img.getUrl());
                    imageDTO.setAltText(img.getAltText());
                    imageDTO.setSortOrder(img.getSortOrder());
                    return imageDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
