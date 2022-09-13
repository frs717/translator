package ru.tinkoff.translator.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo {
    @Id
    private Long id;
    private String ip;
    private Date requestTime;
    private String inputText;
    private String sourceLanguage;
    private String targetLanguage;
    private String translatedText;

}
