package com.wilson.demo.controller;

import com.wilson.demo.service.StudentService;
import com.wilson.demo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class HelloController {

    @Value("${student.name:Unknown}")
    private String defaultName;

    @Value("${student.id:00000}")
    private String defaultId;

    @Autowired
    private StudentService studentService;

    @GetMapping("/hello")
    public String hello(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String id) {
        
        String studentName = name != null ? name : defaultName;
        String studentId = id != null ? id : defaultId;
        
        return String.format("Hello, World! Welcome\nStudent Name: %s\nStudent ID: %s", 
                studentName, studentId);
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping("/student")
    public String addStudent(
            @RequestParam String name,
            @RequestParam String id,
            @RequestParam(required = false) String dob,
            @RequestParam(required = false) String password) {
        
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
            // Use the student ID as the password if none is provided
            String rawPassword = (password != null && !password.isEmpty()) ? password : id;
            Student student = studentService.createStudent(name, id, dateOfBirth, rawPassword);
            return String.format("Student added successfully!\nName: %s\nID: %s\nDate of Birth: %s\nDatabase ID: %d", 
                    student.getStudentName(), student.getStudentId(), student.getDateOfBirth(), student.getId());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String id,
            @RequestParam String password) {
        if (id == null || id.isEmpty() || password == null || password.isEmpty()) {
            return "Error: Student ID and password are required!";
        }
        boolean authenticated = studentService.authenticate(id, password);
        if (authenticated) {
            Student student = studentService.getStudentById(id);
            return String.format("Login successful! Welcome back, %s.", student.getStudentName());
        } else {
            return "Error: Invalid Student ID or password.";
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
