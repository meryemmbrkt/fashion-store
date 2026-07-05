package com.meryem.fashionstore.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequest(
        @NotBlank(message = "Mağaza adı boş bırakılamaz!")
        @Size(min = 2, max = 50, message = "Mağaza adı 2 ile 50 karakter arasında olmalıdır!")
        String storeName,
        String link)
{

}
