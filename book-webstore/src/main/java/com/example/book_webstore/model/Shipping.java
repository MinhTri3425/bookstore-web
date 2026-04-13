package com.example.book_webstore.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shippings")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal cost;

    public enum ShippingStatus {
        PENDING, SHIPPING, DELIVERED, FAILED
    }

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    public enum ShippingMethod {
        STANDARD, FAST, ECONOMY
    }

    @Enumerated(EnumType.STRING)
    private ShippingMethod method;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    @ToString.Exclude
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    @ToString.Exclude
    private Shipper shipper;

    private String deliveryAddress;

    private LocalDateTime createdAt;

    private String CustomerName;
    private String CustomerAddress;
    private String CustomerPhone;
    private String note;
}
