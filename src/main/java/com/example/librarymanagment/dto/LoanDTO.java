package com.example.librarymanagment.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private Double penalite;
    private Long bookId;
    private Long memberId;
}