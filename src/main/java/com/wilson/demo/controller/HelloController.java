package com.wilson.demo.controller;

import com.wilson.demo.service.StudentService;
import com.wilson.demo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${student.name:Unknown}")
    private String defaultName;

    @Value("${student.id:00000}")
    private String defaultId;

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String hello(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String id) {
        
        String studentName = name != null ? name : defaultName;
        String studentId = id != null ? id : defaultId;
        
        return String.format("Hello, World! Welcome\nStudent Name: %s\nStudent ID: %s", 
                studentName, studentId);
    }

    @PostMapping("/student")
    public String addStudent(
            @RequestParam String name,
            @RequestParam String id,
            @RequestParam(required = false) String dob) {
        
        if (name == null || name.isEmpty() || id == null || id.isEmpty()) {
            return "Error: Student name and ID are required!";
        }
        
        try {
            java.time.LocalDate dateOfBirth = null;
            if (dob != null && !dob.isEmpty()) {
                try {
                    dateOfBirth = java.time.LocalDate.parse(dob);
                } catch (Exception ex) {
                    return "Error: Date of birth must be in YYYY-MM-DD format!";
                }
            }
            Student student = studentService.createStudent(name, id, dateOfBirth);
            return String.format("Student added successfully!\nName: %s\nID: %s\nDate of Birth: %s\nDatabase ID: %d", 
                    student.getStudentName(), student.getStudentId(), student.getDateOfBirth(), student.getId());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/student")
    public String getStudent(@RequestParam String id) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "Student not found with ID: " + id;
        }
        return String.format("Found Student:\nName: %s\nID: %s\nDate of Birth: %s", 
                student.getStudentName(), student.getStudentId(), student.getDateOfBirth());
    }
}
