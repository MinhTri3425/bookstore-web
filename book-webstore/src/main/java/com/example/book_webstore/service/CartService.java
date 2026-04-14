package com.example.book_webstore.service;

import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.AddressDTO;
import com.example.book_webstore.model.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    CartDTO getOrCreateCart(Long cartId);

    CartDTO addToCart(Long cartId, Long bookId, int quantity);

    CartDTO removeFromCart(Long cartId, Long bookId);

    CartDTO updateItemQuantity(Long cartId, Long bookId, int quantity);

    Long checkoutSelectedItems(Long cartId,
            List<Long> selectedBookIds,
            String customerEmail,
            Long selectedAddressId,
            String receiverName,
            String phoneNumber,
            String note,
            Payment.PaymentMethod paymentMethod,
            String shippingMethod,
            String couponCode);

    BigDecimal getOrderPaymentAmount(Long orderId);

    void updatePaymentStatus(Long orderId, boolean paid);

    List<AddressDTO> getUserAddresses(String customerEmail);

    AddressDTO addUserAddress(String customerEmail,
            String street,
            String ward,
            String district,
            String city);

    long getItemCount(Long cartId);
}
