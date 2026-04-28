package com.example.librarymanagment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookDTO {
    private Long id;
    private String titre;
    private String isbn;
    private LocalDate datePublication;
    private String genre;
    private Integer nombreExemplaires;
    private Integer disponibles;
    private List<Long> authorIds;
}