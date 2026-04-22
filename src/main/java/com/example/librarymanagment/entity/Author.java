package com.example.librarymanagment.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String nationalite;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books = new ArrayList<>();
}