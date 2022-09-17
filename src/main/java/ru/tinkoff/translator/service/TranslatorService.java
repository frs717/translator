package ru.tinkoff.translator.service;

public interface TranslatorService {
    String translate(String sourceLanguage, String targetLanguage, String text);
}
