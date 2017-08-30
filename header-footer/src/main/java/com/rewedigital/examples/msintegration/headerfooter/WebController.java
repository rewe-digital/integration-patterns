package com.rewedigital.examples.msintegration.headerfooter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

	@GetMapping("/header")
	public String header() {
		return "header";
	}
}
