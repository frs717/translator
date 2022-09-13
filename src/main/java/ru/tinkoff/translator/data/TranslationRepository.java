package ru.tinkoff.translator.data;

import org.springframework.data.repository.CrudRepository;

public interface TranslationRepository extends CrudRepository<TranslationInfo, Long> {
}
