package ru.tinkoff.translator.yandex_translator_api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tinkoff.translator.yandex_translator_api.dto.YandexTranslatorRequestBody;
import ru.tinkoff.translator.yandex_translator_api.dto.YandexTranslatorResponseBody;
import ru.tinkoff.translator.yandex_translator_api.dto.YandexTranslatorResponseBodyLine;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class YandexTranslator implements Translator {
    private final String apiKey;
    private final String apiUrl;
    private final String apiFolderId;
    private final RestTemplate rest;
    private final HttpHeaders headers;

    public YandexTranslator(@Value("${translator.yandex.api-key}") String apiKey,
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
        headers.setBearerAuth(this.apiKey);
    }

    public List<String> translate(String sourceLanguage, String targetLanguage, List<String> texts) {
        YandexTranslatorRequestBody requestBody =
                new YandexTranslatorRequestBody(this.apiFolderId, sourceLanguage, targetLanguage, texts);

        HttpEntity<YandexTranslatorRequestBody> entity = new HttpEntity<>(requestBody, headers);
        log.info("request: {}", entity);
        ResponseEntity<YandexTranslatorResponseBody> responseEntity =
                rest.postForEntity(apiUrl, entity, YandexTranslatorResponseBody.class);
        log.info("response: {}", responseEntity.getBody());
        return convert(responseEntity.getBody().getTranslations());
    }

    private List<String> convert(List<YandexTranslatorResponseBodyLine> lines) {
        return lines.stream().map(YandexTranslatorResponseBodyLine::getText).collect(Collectors.toList());
    }

}
