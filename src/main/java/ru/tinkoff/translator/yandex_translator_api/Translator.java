package ru.tinkoff.translator.yandex_translator_api;

import java.util.List;

public interface Translator {
    List<String> translate(String sourceLanguage, String targetLanguage, List<String> texts);
}
