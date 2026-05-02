package com.example.librarymanagment.service;

import com.example.librarymanagment.dto.ChatbotRequest;
import com.example.librarymanagment.dto.ChatbotResponse;
import com.example.librarymanagment.dto.GroqMessage;
import com.example.librarymanagment.dto.GroqResponse;
import com.example.librarymanagment.entity.Book;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

      // Build the request body for Groq
      List<GroqMessage> messages = new ArrayList<>();
      messages.add(new GroqMessage("system",
        "You are a helpful assistant for a library. Recommend books based on user queries. "
          + "If a genre is mentioned, try to find books in that genre. "
          + "If specific titles are mentioned, acknowledge them. "
          + "Always try to suggest actual books. "
          + "If you recommend books, list them clearly. "
          + "If you don't find specific books, suggest general ideas."));
      messages.add(new GroqMessage("user",
        request.getQuery()
          + "\n\nBased on this, can you suggest some book titles or genres? "
          + "Please list any specific book titles you recommend at the end, "
          + "separated by commas, e.g., 'Book Title 1, Book Title 2'."));

      Map<String, Object> groqRequest = new HashMap<>();
      groqRequest.put("model", groqApiModel);
      groqRequest.put("messages", messages);
      groqRequest.put("temperature", 0.7);
      groqRequest.put("max_tokens", 200);

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(groqRequest, headers);

      GroqResponse groqResponse = restTemplate.postForObject(groqApiUrl, entity, GroqResponse.class);

      if (groqResponse != null
        && groqResponse.getChoices() != null
        && !groqResponse.getChoices().isEmpty()) {

        String chatbotMessage = groqResponse.getChoices().get(0).getMessage().getContent();
        List<Book> recommendedBooks = new ArrayList<>();

        // ── 1. Try to match books by genre mentioned in the user query ──────────
        Pattern genrePattern = Pattern.compile(
          "(?:genre|catégorie|category)\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher genreMatcher = genrePattern.matcher(request.getQuery());
        if (genreMatcher.find()) {
          String genre = genreMatcher.group(1);
          List<Book> byGenre = bookRepository.findByGenreContainingIgnoreCase(genre);
          recommendedBooks.addAll(byGenre);
        }

        // ── 2. Extract titles mentioned in the chatbot reply ──────────────────
        // Titles between single or double quotes
        Pattern quotedTitlePattern = Pattern.compile("[\"']([^\"']+)[\"']");
        Matcher quotedMatcher = quotedTitlePattern.matcher(chatbotMessage);
        while (quotedMatcher.find()) {
          String title = quotedMatcher.group(1).trim();
          // findByTitreContainingIgnoreCase returns List<Book>, NOT Optional
          List<Book> found = bookRepository.findByTitreContainingIgnoreCase(title);
          recommendedBooks.addAll(found);
        }

        // Also try to grab comma-separated titles from the last line of the response
        // (as instructed in the prompt: "Book Title 1, Book Title 2")
        String[] lines = chatbotMessage.split("\\n");
        String lastLine = lines[lines.length - 1].trim();
        if (lastLine.contains(",")) {
          for (String part : lastLine.split(",")) {
            String title = part.trim()
              .replaceAll("^['\"]|['\"]$", ""); // strip surrounding quotes
            if (!title.isEmpty()) {
              List<Book> found = bookRepository.findByTitreContainingIgnoreCase(title);
              recommendedBooks.addAll(found);
            }
          }
        }

        // ── 3. De-duplicate ──────────────────────────────────────────────────
        List<Book> distinct = recommendedBooks.stream()
          .distinct()
          .collect(Collectors.toList());

        return new ChatbotResponse(chatbotMessage, distinct);
      }

      return new ChatbotResponse(
        "Désolé, je n'ai pas pu obtenir de réponse du chatbot.",
        Collections.emptyList());

    } catch (Exception e) {
      System.err.println("Erreur Groq : " + e.getMessage());
      e.printStackTrace();
      return new ChatbotResponse(
        "Désolé, une erreur interne est survenue lors de la communication avec l'IA. "
          + "Veuillez réessayer plus tard.",
        Collections.emptyList());
    }
  }
}
