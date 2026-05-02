package com.example.librarymanagment.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroqResponse {
  private String id;
  private String object;
  private long created;
  private String model;
  private List<GroqChoice> choices;
  // Vous pouvez ajouter d'autres champs comme 'usage' si nécessaire
}
