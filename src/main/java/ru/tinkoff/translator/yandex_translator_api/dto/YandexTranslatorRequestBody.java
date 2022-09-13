package ru.tinkoff.translator.yandex_translator_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class YandexTranslatorRequestBody {
    private final String folderId;
    private final String sourceLanguageCode;
    private final String targetLanguageCode;
    private final List<String> texts;
}
