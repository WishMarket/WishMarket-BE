package com.zerobase.wishmarket.domain.product.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInputForm {

    @NotBlank
    private String name;

    private MultipartFile image;

    private int categoryCode;

    private int price;

    private MultipartFile description;


}
