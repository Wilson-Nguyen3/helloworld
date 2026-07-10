package com.wilson.demo.service;

import com.wilson.demo.entity.Student;
import com.wilson.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student createStudent(String studentName, String studentId) {
        Student student = new Student(studentName, studentId);
        return studentRepository.save(student);
    }

    public Student createStudent(String studentName, String studentId, java.time.LocalDate dateOfBirth) {
        Student student = new Student(studentName, studentId, dateOfBirth);
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
