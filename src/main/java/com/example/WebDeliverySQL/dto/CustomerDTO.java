package com.example.WebDeliverySQL.dto;

import com.example.WebDeliverySQL.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerDTO {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @ValidEmail  // Використовуємо кастомну валідацію
    private String email;
}
