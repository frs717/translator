package ru.tinkoff.translator.data;

import ru.tinkoff.translator.data.dto.TranslationInfo;

public interface TranslationRepository {
    TranslationInfo save(TranslationInfo translationInfo);
}
