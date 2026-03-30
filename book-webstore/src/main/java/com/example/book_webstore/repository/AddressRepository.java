package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
