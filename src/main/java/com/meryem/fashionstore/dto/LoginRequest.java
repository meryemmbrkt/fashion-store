package com.meryem.fashionstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-posta alanı boş bırakılamaz!")
        @Email(message = "Lütfen geçerli bir e-posta adresi giriniz!")
        String email,

        @NotBlank(message = "Şifre alanı boş bırakılamaz!")
        String password
) {}