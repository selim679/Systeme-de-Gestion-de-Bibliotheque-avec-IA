package com.example.librarymanagment.dto;



import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateAdhesion;
}