package com.example.librarymanagment.dto;

import com.example.librarymanagment.entity.Book;
import com.example.librarymanagment.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {
  private String message;
  private List<Book> recommendedBooks;

  public ChatbotResponse(String chatbotMessage, List<Book> distinctRecommendedBooks) {


  }
}
