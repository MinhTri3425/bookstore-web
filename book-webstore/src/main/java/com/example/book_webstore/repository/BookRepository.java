package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.book_webstore.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
	@Query("select distinct b from Book b left join fetch b.images")
	List<Book> findAllWithImages();

	@Query("select b from Book b left join fetch b.images where b.id = :id")
	Optional<Book> findByIdWithImages(@Param("id") Long id);

}
