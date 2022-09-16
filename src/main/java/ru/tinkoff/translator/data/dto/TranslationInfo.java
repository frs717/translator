package ru.tinkoff.translator.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationInfo {
    private Long id;
    private Long request;
    private String sourceWord;
    private String translatedWord;
}
