package com.certifyingcenter.certifyingcenter.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
    @RequestMapping("/404.html")
    public String render404() {
        return "error/404";
    }

    @RequestMapping("/403.html")
    public String render403() {
        return "error/403";
    }
}
