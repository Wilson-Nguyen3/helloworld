package com.wilson.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);

    // Constructors
    public Student() {
    }

    public Student(String studentName, String studentId) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.dateOfBirth = LocalDate.of(2000, 1, 1);
    }

    public Student(String studentName, String studentId, LocalDate dateOfBirth) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.dateOfBirth = dateOfBirth != null ? dateOfBirth : LocalDate.of(2000, 1, 1);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", studentId='" + studentId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
