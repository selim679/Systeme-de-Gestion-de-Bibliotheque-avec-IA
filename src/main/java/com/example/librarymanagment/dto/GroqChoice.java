package com.example.librarymanagment.dto;

import lombok.Data;

@Data
public class GroqChoice {
  private int index;
  private GroqMessage message;
  private String finish_reason;
}
