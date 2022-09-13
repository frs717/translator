package ru.tinkoff.translator.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YandexTranslatorResponseBodyLine {
    private String text;
    private String detectedLanguageCode;
}
