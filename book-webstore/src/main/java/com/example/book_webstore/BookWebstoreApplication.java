package com.example.book_webstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BookWebstoreApplication {

	public static void main(String[] args) {
		//System.out.println("MÃ BCRYPT CHUẨN CỦA 123456 LÀ: " + new BCryptPasswordEncoder().encode("123456"));
		SpringApplication.run(BookWebstoreApplication.class, args);
	}

}
