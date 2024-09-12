package com.project.G1_T3.common.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String handleError() {
        // You can add logging here
        return "walao, 404 bro";
    }

    public String getErrorPath() {
        return "/error";
    }
    
}