package ru.tinkoff.translator.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class YandexTranslatorRequestBody {
    private final String folderId;
    private final String sourceLanguageCode;
    private final String targetLanguageCode;
    private final List<String> texts;
}
