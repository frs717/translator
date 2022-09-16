package ru.tinkoff.translator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tinkoff.translator.data.dto.RequestInfo;
import ru.tinkoff.translator.data.RequestRepository;
import ru.tinkoff.translator.data.dto.TranslationInfo;
import ru.tinkoff.translator.data.TranslationRepository;
import ru.tinkoff.translator.service.dto.YandexTranslatorRequestBody;
import ru.tinkoff.translator.service.dto.YandexTranslatorResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Component
@Slf4j
public class YandexTranslatorService implements Translator {
    private static final String SPLIT_REGEX = "[\\s,?!;.]+";
    private final HttpServletRequest httpServletRequest;
    private final RequestRepository requestRepository;
    private final TranslationRepository translationRepository;
    private final String apiKey;
    private final String apiUrl;
    private final String apiFolderId;
    private final RestTemplate rest;
    private final HttpHeaders headers;

    public YandexTranslatorService(@Value("${translator.yandex.api-key}") String apiKey,
                                   @Value("${translator.yandex.api-url}") String apiUrl,
                                   @Value("${translator.yandex.api-folder-id}") String apiFolderId,
                                   @Autowired RestTemplate restTemplate,
                                   @Autowired RequestRepository requestRepository,
                                   @Autowired TranslationRepository translationRepository,
                                   @Autowired HttpServletRequest httpServletRequest) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.apiFolderId = apiFolderId;
        this.rest = restTemplate;
        this.requestRepository = requestRepository;
        this.translationRepository = translationRepository;
        this.httpServletRequest = httpServletRequest;
        headers = new HttpHeaders();
        initHeaders();
    }

    private void initHeaders() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Api-Key " + this.apiKey);
    }

    public String translate(String sourceLanguage, String targetLanguage, String text) {
        List<String> words = Arrays.asList(text.split(SPLIT_REGEX));

        Set<String> uniqueWords = new HashSet<>(words);
        Map<String, String> translations = new HashMap<>();

        // TODO: 16.09.2022 mt
        for (String word : uniqueWords) {
            YandexTranslatorRequestBody requestBody =
                    new YandexTranslatorRequestBody(this.apiFolderId, sourceLanguage, targetLanguage, Collections.singletonList(word));
            log.info("REQUEST: {}", requestBody);
            HttpEntity<YandexTranslatorRequestBody> entity = new HttpEntity<>(requestBody, headers);

            YandexTranslatorResponseBody responseBody =
                    rest.postForObject(apiUrl, entity, YandexTranslatorResponseBody.class);
            log.info("RESPONSE: {}", responseBody);
            String translatedWord = responseBody.getTranslations().get(0).getText();
            translations.put(word, translatedWord);
        }
        //

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(translations.get(word)).append(" ");
        }
        String translatedText = sb.toString().trim();
        saveTranslations(sourceLanguage, targetLanguage, text, translatedText, translations);
        return translatedText;
    }

    private void saveTranslations(String sourceLanguage, String targetLanguage, String sourceText,
                                  String TranslatedText, Map<String, String> translations) {

        RequestInfo requestInfo = saveRequest(sourceLanguage, targetLanguage, sourceText, TranslatedText);
        saveWords(translations, requestInfo);
    }

    private RequestInfo saveRequest(String sourceLanguage, String targetLanguage, String sourceText, String TranslatedText) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setSourceText(sourceText);
        requestInfo.setSourceLanguage(sourceLanguage);
        requestInfo.setTargetLanguage(targetLanguage);
        requestInfo.setTranslatedText(TranslatedText);
        requestInfo.setRequestTime(LocalDateTime.now());
        requestInfo.setIp(this.httpServletRequest.getRemoteAddr());
        return this.requestRepository.save(requestInfo);
    }

    private void saveWords(Map<String, String> translations, RequestInfo requestInfo) {
        for (String word : translations.keySet()) {
            TranslationInfo translationInfo = new TranslationInfo();
            translationInfo.setSourceWord(word);
            translationInfo.setTranslatedWord(translations.get(word));
            translationInfo.setRequest(requestInfo.getId());
            this.translationRepository.save(translationInfo);
        }
    }

}
