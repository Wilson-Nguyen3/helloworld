package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Student createStudent(String studentName, String studentId) {
        Student student = new Student(studentName, studentId);
        return studentRepository.save(student);
    }

    public Student getStudentById(String studentId) {
        return studentRepository.findByStudentId(studentId).orElse(null);
    }

    public void deleteStudent(String studentId) {
        studentRepository.findByStudentId(studentId).ifPresent(student -> 
            studentRepository.delete(student)
        );
    }
}
