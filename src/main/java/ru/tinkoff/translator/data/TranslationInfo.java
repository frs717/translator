package ru.tinkoff.translator.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class TranslationInfo {
    @Id
    private Long id;
    private Long request;
    private String sourceWord;
    private String translatedWord;
    private String sourceLanguage;
    private String targetLanguage;

}
