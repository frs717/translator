package ru.tinkoff.translator.yandex_translator_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YandexTranslatorResponseBodyLine {
    private String text;
    private String detectedLanguageCode;
}
