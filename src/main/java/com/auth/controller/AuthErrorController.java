package com.auth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthErrorController implements ErrorController {

	private static final String ERROR_PATH = "/error";
	 
    public String getErrorPath() {
        return ERROR_PATH;
    }
    
    @CrossOrigin("*")
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        return "error/error";
    }

}
