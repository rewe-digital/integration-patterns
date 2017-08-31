package com.rewedigital.examples.msintegration.headerfooter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/header")
    public String header() {
        return "header";
    }

    @GetMapping("/footer")
    public String footer(final HttpServletResponse response) {
        response.addHeader("x-uic-stylesheet", "/footer/css/core.css");
        return "footer";
    }
}
