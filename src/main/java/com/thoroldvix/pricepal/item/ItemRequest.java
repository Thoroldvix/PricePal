package com.thoroldvix.pricepal.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ItemRequest(
        @Min(1)
        int id,
        @NotBlank(message = "Item name cannot be blank")
        @NotNull(message = "Item name cannot be null")
        String name,
        @Min(0)
        long vendorPrice,
        @NotBlank(message = "Item type cannot be blank")
        @NotNull(message = "Item type cannot be null")
        String type,
        @NotBlank(message = "Item slot cannot be blank")
        @NotNull(message = "Item slot cannot be null")
        String slot,
        @NotBlank(message = "Item quality cannot be blank")
        @NotNull(message = "Item quality cannot be null")
        String quality
) {

}
