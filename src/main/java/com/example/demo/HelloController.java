package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${student.name:Unknown}")
    private String defaultName;

    @Value("${student.id:00000}")
    private String defaultId;

    @GetMapping("/")
    public String hello(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String id) {
        
        String studentName = name != null ? name : defaultName;
        String studentId = id != null ? id : defaultId;
        
        return String.format("Hello, World! Welcome\nStudent Name: %s\nStudent ID: %s", 
                studentName, studentId);
    }
}
