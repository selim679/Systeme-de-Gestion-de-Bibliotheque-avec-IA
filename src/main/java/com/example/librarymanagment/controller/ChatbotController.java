package com.example.librarymanagment.controller;



import com.example.librarymanagment.dto.ChatbotRequest;
import com.example.librarymanagment.dto.ChatbotResponse;
import com.example.librarymanagment.service.GroqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot IA", description = "API pour interagir avec le chatbot de recommandation de livres")
public class ChatbotController {

    @Autowired
    private GroqService groqService;

    @Operation(summary = "Obtenir des recommandations de livres via le chatbot IA")
    @PostMapping("/recommend")
    public ChatbotResponse getRecommendations(@RequestBody ChatbotRequest request) {
        return groqService.getBookRecommendations(request);
    }
}