package com.gnimty.communityapiserver.domain.base.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class Controller {

	@GetMapping
	public void healthCheck() {
	}
}
