package com.zerobase.wishmarket.domain.product.controller;

import com.zerobase.wishmarket.domain.product.model.ProductInputForm;
import com.zerobase.wishmarket.domain.product.service.ProductAddService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductAddController {

    private final ProductAddService productAddService;

    @GetMapping("/admin/addProducts")
    public String addBanner(Model model) {
        ProductInputForm productInputForm = new ProductInputForm();
        model.addAttribute("detail", productInputForm);

        return "admin/addProducts";
    }

    @PostMapping("/admin/addProducts")
    public String upload(@RequestParam MultipartFile productImage,
        ProductInputForm productInputForm) throws IOException {

        productInputForm.setImage(productImage);
        productAddService.addProductData(productInputForm);

        return "redirect:/admin/addProducts";

    }


}
