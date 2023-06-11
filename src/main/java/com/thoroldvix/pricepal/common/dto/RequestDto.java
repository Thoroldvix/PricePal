package com.thoroldvix.pricepal.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Request body for filtering")
public record RequestDto(
        @Schema(description = "Search criteria")
        List<SearchCriteria> searchCriteria,

        @Schema(description = "Global operator for combining search criteria", allowableValues = {"AND", "OR"})
        GlobalOperator globalOperator

) {
   public enum GlobalOperator {
        AND, OR, NOT
    }
}
