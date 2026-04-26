package com.example.librarymanagment.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanReturnDTO {
    private LocalDate dateRetourEffective;
}