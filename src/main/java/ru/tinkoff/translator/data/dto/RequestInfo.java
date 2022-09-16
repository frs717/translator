package ru.tinkoff.translator.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {
    private Long id;
    private String sourceText;
    private String sourceLanguage;
    private String targetLanguage;
    private String translatedText;
    private String ip;
    private LocalDateTime requestTime;
}
