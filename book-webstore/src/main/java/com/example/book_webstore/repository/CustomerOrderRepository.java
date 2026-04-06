package com.example.book_webstore.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.book_webstore.model.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = {"items", "customer", "payment", "shipping"})
    Page<CustomerOrder> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"items", "customer", "payment", "shipping"})
    Page<CustomerOrder> findByStatus(CustomerOrder.OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "customer", "payment", "shipping"})
    @Query("select o from CustomerOrder o where o.id = :id")
    Optional<CustomerOrder> findListItemById(Long id);

    @EntityGraph(attributePaths = {"items", "items.book", "customer", "payment", "shipping", "shipping.shipper"})
    @Query("select o from CustomerOrder o where o.id = :id")
    Optional<CustomerOrder> findDetailById(Long id);

    @EntityGraph(attributePaths = {"items", "customer", "payment", "shipping"})
    @Query("""
            select distinct o
            from CustomerOrder o
            left join o.customer c
            left join o.payment p
            left join p.user pu
            where coalesce(c.id, pu.id) = :customerId
              and (:status is null or o.status = :status)
            """)
    Page<CustomerOrder> findCustomerOrders(
            @Param("customerId") Long customerId,
            @Param("status") CustomerOrder.OrderStatus status,
            Pageable pageable);

    @EntityGraph(attributePaths = {"items", "customer", "payment", "shipping"})
    @Query("""
            select o
            from CustomerOrder o
            left join o.customer c
            left join o.payment p
            left join p.user pu
            where o.id = :id
              and coalesce(c.id, pu.id) = :customerId
            """)
    Optional<CustomerOrder> findCustomerListItemById(@Param("customerId") Long customerId, @Param("id") Long id);

    @EntityGraph(attributePaths = {"items", "items.book", "customer", "payment", "shipping", "shipping.shipper"})
    @Query("""
            select o
            from CustomerOrder o
            left join o.customer c
            left join o.payment p
            left join p.user pu
            where o.id = :id
              and coalesce(c.id, pu.id) = :customerId
            """)
    Optional<CustomerOrder> findCustomerDetailById(@Param("customerId") Long customerId, @Param("id") Long id);
}
