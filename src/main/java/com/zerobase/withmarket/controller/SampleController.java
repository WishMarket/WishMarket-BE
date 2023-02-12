package com.zerobase.withmarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("sample")
    public String sample(){
        return "안녕하세요!!!~~~성공!!!!";
    }
}
