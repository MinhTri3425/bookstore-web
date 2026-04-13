package com.example.book_webstore.dto;

import java.util.List;

import com.example.book_webstore.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private User.Role role;
    private List<AddressDTO> addresses;
    private boolean isShipper;

}
