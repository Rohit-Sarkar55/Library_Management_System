package com.example.librarymanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("library-management")
public class LibraryController {

    @GetMapping("/test")
    public String test(){
        return "test completed";
    }
}
