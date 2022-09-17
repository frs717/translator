package ru.tinkoff.translator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.tinkoff.translator.data.RequestRepository;
import ru.tinkoff.translator.data.TranslationRepository;
import ru.tinkoff.translator.data.dto.RequestInfo;
import ru.tinkoff.translator.data.dto.TranslationInfo;
import ru.tinkoff.translator.service.dto.Translation;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class YandexTranslatorService implements TranslatorService {
    private static final String SPLIT_REGEX = "[\\s,?!;.]+";
    private final HttpServletRequest httpServletRequest;
    private final RequestRepository requestRepository;
    private final TranslationRepository translationRepository;
    private final String apiKey;
    private final String apiUrl;
    private final String apiFolderId;
    private final RestTemplate rest;
    private final HttpHeaders headers;

    private final int threadCount;

    private final int requestLimitInSecond;

    public YandexTranslatorService(@Value("${translator.yandex.api-key}") String apiKey,
                                   @Value("${translator.yandex.api-url}") String apiUrl,
                                   @Value("${translator.yandex.api-folder-id}") String apiFolderId,
                                   @Value("${translator.api.thread-count}") int threadCount,
                                   @Value("${translator.yandex.request-limit-in-second}") int requestLimitInSecond,
                                   @Autowired RestTemplate restTemplate,
                                   @Autowired RequestRepository requestRepository,
                                   @Autowired TranslationRepository translationRepository,
                                   @Autowired HttpServletRequest httpServletRequest) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.apiFolderId = apiFolderId;
        this.rest = restTemplate;
        this.threadCount = threadCount;
        this.requestLimitInSecond = requestLimitInSecond;
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
        Map<String, String> translations = getTranslations(sourceLanguage, targetLanguage, uniqueWords);
        String translatedText = getTranslatedString(words, translations);
        saveTranslations(sourceLanguage, targetLanguage, text, translatedText, translations);
        return translatedText;
    }

    private synchronized Map<String, String> getTranslations(String sourceLanguage, String targetLanguage, Set<String> uniqueWords) {
        Map<String, String> translations = new HashMap<>();
        List<Callable<Translation>> tasks = new LinkedList<>();
        uniqueWords.forEach(word -> tasks.add(
                new TranslatorRequestThread(word, sourceLanguage, targetLanguage, apiFolderId, apiUrl, headers, rest)));

        List<Future<Translation>> futures = translateWords(tasks);
        for (Future<Translation> future : futures) {
            Translation translation;
            try {
                translation = future.get();
                translations.put(translation.getWord(), translation.getTranslation());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return translations;
    }

    private List<Future<Translation>> translateWords(List<Callable<Translation>> tasks) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(this.threadCount);
        List<Future<Translation>> futures = new LinkedList<>();
        int delay = calculateDelay(tasks.size());
        int time = 60;
        for (Callable<Translation> task : tasks) {
            futures.add(executorService.schedule(task, time, TimeUnit.MILLISECONDS));
            time += delay;
        }
        executorService.shutdown();
        return futures;
    }

    private String getTranslatedString(List<String> words, Map<String, String> translations) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            sb.append(translations.get(words.get(i)));
            if (i < words.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private int calculateDelay(int requestCount) {
        int delay = 0;
        if (requestCount > this.requestLimitInSecond) {
            delay = 1000 / this.requestLimitInSecond + 10;
        }
        return delay;
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
