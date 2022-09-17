package ru.tinkoff.translator.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.translator.api.dto.TranslationRequestBody;
import ru.tinkoff.translator.api.dto.TranslationResponseBody;
import ru.tinkoff.translator.service.TranslatorService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/api/translator", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
public class TranslatorController {
    private final TranslatorService translatorService;

    public TranslatorController(TranslatorService translatorService) {
        this.translatorService = translatorService;
    }

    @PostMapping(path= "/translate")
    public TranslationResponseBody translate(@RequestBody @Valid TranslationRequestBody requestBody) {
        String res = this.translatorService.translate(requestBody.getSourceLanguage(),
                requestBody.getTargetLanguage(), requestBody.getText());

        return new TranslationResponseBody(res);
    }

}
