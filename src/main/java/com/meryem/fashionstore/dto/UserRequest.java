package com.meryem.fashionstore.dto;

import com.meryem.fashionstore.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Ad alanı boş bırakılamaz!")
        String firstName,

        @NotBlank(message = "Soyad alanı boş bırakılamaz!")
        String lastName,

        @NotBlank(message = "E-posta alanı boş bırakılamaz!")
        @Email(message = "Lütfen geçerli bir e-posta adresi giriniz!")
        String email,

        @NotBlank(message = "Şifre alanı boş bırakılamaz!")
        @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır!")
        String password,
        @NotNull(message = "Rol seçilmelidir!")
        Role role
) {}