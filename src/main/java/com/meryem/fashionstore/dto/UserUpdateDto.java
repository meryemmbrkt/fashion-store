package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {

    private String firstName;
    private String lastName;

    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır!")
    private String password;
}