package com.example.librarymanagment.dto;

import com.example.librarymanagment.entity.Book; // Changé de 'model.Book' à 'entity.Book'
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
// @AllArgsConstructor // Supprimé pour éviter le conflit de constructeurs avec @Data
public class ChatbotResponse {
  private String message;
  private List<Book> recommendedBooks;

  // Ajout manuel d'un constructeur si @AllArgsConstructor est vraiment nécessaire et @Data ne suffit pas
  // Mais généralement, @Data génère un constructeur avec tous les champs si aucun autre n'est défini.
  // Pour être explicite et éviter le conflit, on peut le définir manuellement si besoin.
  public ChatbotResponse(String message, List<Book> recommendedBooks) {
    this.message = message;
    this.recommendedBooks = recommendedBooks;
  }
}
