package com.inman.restcontroller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {
    @RequestMapping( "/status" )
    public String index() {
	return "{ \"status\" : \"Inman Lives\" }" ;
    }
}

