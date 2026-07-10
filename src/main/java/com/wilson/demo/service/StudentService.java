package com.wilson.demo.service;

import com.wilson.demo.entity.Student;
import com.wilson.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student createStudent(String studentName, String studentId) {
        return createStudent(studentName, studentId, null, studentId);
    }

    public Student createStudent(String studentName, String studentId, java.time.LocalDate dateOfBirth) {
        return createStudent(studentName, studentId, dateOfBirth, studentId);
    }

    public Student createStudent(String studentName, String studentId, java.time.LocalDate dateOfBirth, String rawPassword) {
        String passwordHash = rawPassword != null ? hashPassword(rawPassword) : null;
        Student student = new Student(studentName, studentId, dateOfBirth, passwordHash);
        return studentRepository.save(student);
    }

    public boolean authenticate(String studentId, String rawPassword) {
        Student student = getStudentById(studentId);
        if (student == null || rawPassword == null) {
            return false;
        }
        if (student.getPasswordHash() == null) {
            // Fallback for legacy students created before password hashes were introduced
            return rawPassword.equals(studentId);
        }
        return student.getPasswordHash().equals(hashPassword(rawPassword));
    }

    public Student getStudentById(String studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }

    public void deleteStudent(String studentId) {
        studentRepository.findByStudentId(studentId).ifPresent(student -> 
            studentRepository.delete(student)
        );
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
