package com.project.G1_T3.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String handleError() {
        // You can add logging here
        return "An error occurred. Please try again or contact support if the problem persists.";
    }

    public String getErrorPath() {
        return "/error";
    }
}