package com.osio.quantityservice.dto;

import com.esotericsoftware.kryo.util.Null;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuantityDTO {
    private Long productId;
    private Long quantity;
    private Long userId;
    private Long productPrice;

    @Nullable
    private Long orderId;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
