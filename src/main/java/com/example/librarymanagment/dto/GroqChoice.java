package com.example.librarymanagment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroqChoice {
  private int index;
  private GroqMessage message;

  @JsonProperty("finish_reason")
  private String finishReason;
}
