package com.example.librarymanagment.service;

import com.example.librarymanagment.dto.BookDTO;
import com.example.librarymanagment.dto.ChatbotRequest;
import com.example.librarymanagment.dto.ChatbotResponse;
import com.example.librarymanagment.entity.Book;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GroqService {

    private final WebClient webClient;
    private final BookService bookService;
    private final ObjectMapper objectMapper;

    @Value("${groq.model}")
    private String groqModel;

    @Autowired
    public GroqService(WebClient.Builder webClientBuilder,
                       @Value("${groq.api.url}") String groqApiUrl,
                       @Value("${groq.api.key}") String groqApiKey,
                       BookService bookService,
                       ObjectMapper objectMapper) {

        this.webClient = webClientBuilder.baseUrl(groqApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.bookService = bookService;
        this.objectMapper = objectMapper;
    }

    public ChatbotResponse getBookRecommendations(ChatbotRequest request) {
        String userQuery = request.getQuery();

        // 1. Call Groq API
        String groqResponseContent = callGroqApi(userQuery);

        // 2. Parse response
        Map<String, String> searchCriteria = parseGroqResponse(groqResponseContent);

        // 3. Search books
        List<Book> foundBooks = bookService.searchBooks(
                searchCriteria.get("titre"),
                searchCriteria.get("genre"),
                searchCriteria.get("isbn"),
                searchCriteria.containsKey("disponibles")
                        ? Boolean.parseBoolean(searchCriteria.get("disponibles"))
                        : null
        );

        // 4. Build response
        ChatbotResponse response = new ChatbotResponse();

        if (!foundBooks.isEmpty()) {
            response.setMessage("Voici quelques suggestions de livres basées sur votre demande :");
            response.setRecommendedBooks(
                    foundBooks.stream()
                            .map(this::convertToBookDTO)
                            .collect(Collectors.toList())
            );
        } else {
            response.setMessage("Désolé, je n'ai pas trouvé de livres correspondant à votre demande.");
            response.setRecommendedBooks(new ArrayList<>());
        }

        return response;
    }

    private String callGroqApi(String userQuery) {

        String systemPrompt = "Tu es un assistant de bibliothèque. " +
                "Extrais les informations clés (titre, genre, auteur, ISBN, disponibilité). " +
                "Réponds uniquement en JSON sans texte. Exemple: " +
                "{\"genre\": \"science-fiction\", \"auteur\": \"Frank Herbert\"}";

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userQuery));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqModel);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 150);

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Map<String, String> parseGroqResponse(String groqResponseContent) {
        Map<String, String> criteria = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(groqResponseContent);
            JsonNode choicesNode = rootNode.path("choices");

            if (choicesNode.isArray() && choicesNode.size() > 0) {
                String content = choicesNode.get(0)
                        .path("message")
                        .path("content")
                        .asText();

                try {
                    // Try real JSON parsing
                    JsonNode jsonContent = objectMapper.readTree(content);
                    jsonContent.fields().forEachRemaining(entry ->
                            criteria.put(entry.getKey(), entry.getValue().asText())
                    );

                } catch (Exception e) {
                    // FIXED REGEX HERE ✅
                    Pattern pattern = Pattern.compile("\"(.*?)\":\"(.*?)\"");
                    Matcher matcher = pattern.matcher(content);

                    while (matcher.find()) {
                        criteria.put(matcher.group(1), matcher.group(2));
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur parsing Groq: " + e.getMessage());
        }

        return criteria;
    }

    private BookDTO convertToBookDTO(Book book) {
        BookDTO dto = new BookDTO();

        dto.setId(book.getId());
        dto.setTitre(book.getTitre());
        dto.setIsbn(book.getIsbn());
        dto.setDatePublication(book.getDatePublication());
        dto.setGenre(book.getGenre());
        dto.setNombreExemplaires(book.getNombreExemplaires());
        dto.setDisponibles(book.getDisponibles());

        dto.setAuthorIds(
                book.getAuthors()
                        .stream()
                        .map(author -> author.getId())
                        .collect(Collectors.toList())
        );

        return dto;
    }
}