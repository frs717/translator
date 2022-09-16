package ru.tinkoff.translator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import ru.tinkoff.translator.service.dto.Translation;
import ru.tinkoff.translator.service.dto.YandexTranslatorRequestBody;
import ru.tinkoff.translator.service.dto.YandexTranslatorResponseBody;

import java.util.Collections;
import java.util.concurrent.Callable;

@Slf4j
public class TranslatorRequestThread implements Callable<Translation> {

    private final String word;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final String apiFolderId;
    private final String apiUrl;
    private final HttpHeaders headers;
    private final RestTemplate rest;

    public TranslatorRequestThread(String word, String sourceLanguage, String targetLanguage,
                                   String apiFolderId, String apiUrl, HttpHeaders headers, RestTemplate rest) {
        this.word = word;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.rest = rest;
        this.apiFolderId = apiFolderId;
        this.apiUrl = apiUrl;
        this.headers = headers;
    }

    @Override
    public Translation call() throws Exception {
        YandexTranslatorRequestBody requestBody =
                new YandexTranslatorRequestBody(this.apiFolderId, sourceLanguage, targetLanguage, Collections.singletonList(word));
        log.info("REQUEST: {}", requestBody);
        HttpEntity<YandexTranslatorRequestBody> entity = new HttpEntity<>(requestBody, headers);

        YandexTranslatorResponseBody responseBody =
                rest.postForObject(apiUrl, entity, YandexTranslatorResponseBody.class);
        log.info("RESPONSE: {}", responseBody);
        return new Translation(word, responseBody.getTranslations().get(0).getText());
    }
}
