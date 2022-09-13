package ru.tinkoff.translator.yandex_translator_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YandexTranslatorResponseBody {

    private  List<YandexTranslatorResponseBodyLine> translations;

}

