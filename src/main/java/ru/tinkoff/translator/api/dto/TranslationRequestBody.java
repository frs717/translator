package ru.tinkoff.translator.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationRequestBody {

    @NotBlank
    @Size(min = 2, max = 2, message = "Incorrect language code")
    private String sourceLanguage;

    @NotBlank
    @Size(min = 2, max = 2, message = "Incorrect language code")
    private String targetLanguage;

    @NotBlank(message = "Text must be not blank")
    private String text;
}
