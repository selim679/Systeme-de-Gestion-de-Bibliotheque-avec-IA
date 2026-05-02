package com.example.librarymanagment.service;


import com.example.librarymanagment.dto.ChatbotRequest;
import com.example.librarymanagment.dto.ChatbotResponse;
import com.example.librarymanagment.dto.GroqMessage;
import com.example.librarymanagment.dto.GroqResponse;
import com.example.librarymanagment.model.Book;
import com.example.librarymanagment.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GroqService {

  @Value("${groq.api.key}")
  private String groqApiKey;

  @Value("${groq.api.url}")
  private String groqApiUrl;

  @Value("${groq.api.model}")
  private String groqApiModel;

  @Autowired
  private BookRepository bookRepository;

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ChatbotResponse getBookRecommendations(ChatbotRequest request) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(groqApiKey);

      // Construire le corps de la requête pour Groq
      List<GroqMessage> messages = new ArrayList<>();
      messages.add(new GroqMessage("system", "You are a helpful assistant for a library. Recommend books based on user queries. If a genre is mentioned, try to find books in that genre. If specific titles are mentioned, acknowledge them. Always try to suggest actual books from the library if possible. If you recommend books, list them clearly. If you don't find specific books, suggest general ideas."));
      messages.add(new GroqMessage("user", request.getQuery() + "\n\nBased on this, can you suggest some book titles or genres? Please list any specific book titles you recommend at the end, separated by commas, e.g., 'Book Title 1, Book Title 2'."));

      // Utilisation d'une Map pour construire le JSON de manière flexible
      java.util.Map<String, Object> groqRequest = new java.util.HashMap<>();
      groqRequest.put("model", groqApiModel);
      groqRequest.put("messages", messages);
      groqRequest.put("temperature", 0.7);
      groqRequest.put("max_tokens", 200);

      HttpEntity<java.util.Map<String, Object>> entity = new HttpEntity<>(groqRequest, headers);

      GroqResponse groqResponse = restTemplate.postForObject(groqApiUrl, entity, GroqResponse.class);

      if (groqResponse != null && groqResponse.getChoices() != null && !groqResponse.getChoices().isEmpty()) {
        String chatbotMessage = groqResponse.getChoices().get(0).getMessage().getContent();
        List<Book> recommendedBooks = new ArrayList<>();

        // Tenter d'extraire les titres de livres du message du chatbot
        // Pattern pour trouver des titres entre guillemets ou après ':', ou séparés par des virgules
        Pattern titlePattern = Pattern.compile("['\"]([^'\"]+)['\"]|: ([^,]+(?:, [^,]+)*)|([^,]+(?:, [^,]+)*)");
        Matcher matcher = titlePattern.matcher(chatbotMessage);
        List<String> extractedTitles = new ArrayList<>();

        while (matcher.find()) {
          if (matcher.group(1) != null) { // Titres entre guillemets
            extractedTitles.add(matcher.group(1).trim());
          } else if (matcher.group(2) != null) { // Titres après ':'
            for (String title : matcher.group(2).split(",")) {
              extractedTitles.add(title.trim());
            }
          } else if (matcher.group(3) != null) { // Titres séparés par des virgules
            for (String title : matcher.group(3).split(",")) {
              extractedTitles.add(title.trim());
            }
          }
        }

        // Si le chatbot mentionne un genre, essayons de le trouver
        Pattern genrePattern = Pattern.compile("genre (\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher genreMatcher = genrePattern.matcher(request.getQuery());
        String genreFromQuery = null;
        if (genreMatcher.find()) {
          genreFromQuery = genreMatcher.group(1);
        }

        if (genreFromQuery != null) {
          List<Book> booksByGenre = bookRepository.findByGenreContainingIgnoreCase(genreFromQuery);
          recommendedBooks.addAll(booksByGenre);
        }

        // Rechercher les livres extraits dans la base de données
        for (String title : extractedTitles) {
          bookRepository.findByTitreContainingIgnoreCase(title).ifPresent(recommendedBooks::add);
        }

        // Éliminer les doublons si un livre est trouvé par genre et par titre
        List<Book> distinctRecommendedBooks = recommendedBooks.stream().distinct().collect(Collectors.toList());

        return new ChatbotResponse(chatbotMessage, distinctRecommendedBooks);
      }
      return new ChatbotResponse("Désolé, je n'ai pas pu obtenir de réponse du chatbot.", Collections.emptyList());

    } catch (Exception e) {
      System.err.println("Erreur lors de la communication avec Groq ou du traitement de la réponse: " + e.getMessage());
      e.printStackTrace();
      return new ChatbotResponse("Désolé, une erreur interne est survenue lors de la communication avec l'IA. Veuillez réessayer plus tard.", Collections.emptyList());
    }
  }
}
