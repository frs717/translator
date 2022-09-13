package ru.tinkoff.translator.service;

public interface Translator {
    String translate(String sourceLanguage, String targetLanguage, String text);
}
