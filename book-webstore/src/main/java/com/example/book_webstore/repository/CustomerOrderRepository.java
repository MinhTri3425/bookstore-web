package com.example.book_webstore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import com.example.book_webstore.model.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = "items")
    Page<CustomerOrder> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "items")
    Page<CustomerOrder> findByStatus(CustomerOrder.OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "items")
    @Query("select o from CustomerOrder o where o.id = :id")
    Optional<CustomerOrder> findListItemById(Long id);

    @EntityGraph(attributePaths = {"items", "items.book"})
    @Query("select o from CustomerOrder o where o.id = :id")
    Optional<CustomerOrder> findDetailById(Long id);
}
