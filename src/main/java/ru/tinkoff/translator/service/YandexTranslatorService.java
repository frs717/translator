package ru.tinkoff.translator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tinkoff.translator.data.RequestRepository;
import ru.tinkoff.translator.data.TranslationRepository;
import ru.tinkoff.translator.service.dto.YandexTranslatorRequestBody;
import ru.tinkoff.translator.service.dto.YandexTranslatorResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class YandexTranslatorService implements Translator {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private TranslationRepository translationRepository;
    private final String apiKey;
    private final String apiUrl;
    private final String apiFolderId;
    private final RestTemplate rest;
    private final HttpHeaders headers;

    public YandexTranslatorService(@Value("${translator.yandex.api-key}") String apiKey,
                                   @Value("${translator.yandex.api-url}") String apiUrl,
                                   @Value("${translator.yandex.api-folder-id}") String apiFolderId,
                                   @Autowired RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.apiFolderId = apiFolderId;
        this.rest = restTemplate;
        headers = new HttpHeaders();
        initHeaders();
    }

    private void initHeaders() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Api-Key " + this.apiKey);
    }

    public String translate(String sourceLanguage, String targetLanguage, String text) {
        List<String> words = Arrays.asList(text.split(" "));

        Set<String> uniqueWords = new HashSet<>(words);
        Map<String, String> translations = new HashMap<>();

        // mt
        for (String word : uniqueWords) {
            YandexTranslatorRequestBody requestBody =
                    new YandexTranslatorRequestBody(this.apiFolderId, sourceLanguage, targetLanguage, Collections.singletonList(word));
            log.info("REQUEST: {}", requestBody);
            HttpEntity<YandexTranslatorRequestBody> entity = new HttpEntity<>(requestBody, headers);

            YandexTranslatorResponseBody responseBody =
                    rest.postForObject(apiUrl, entity, YandexTranslatorResponseBody.class);
            String translatedWord = responseBody.getTranslations().get(0).getText();
            translations.put(word, translatedWord);
        }

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(translations.get(word)).append(" ");
        }
        log.info("res: {}", sb);
        return sb.toString();
    }


}
