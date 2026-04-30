package com.example.librarymanagment.dto;



import lombok.Data;
import java.util.List;

@Data
public class ChatbotResponse {
    private String message;
    private List<BookDTO> recommendedBooks;
}