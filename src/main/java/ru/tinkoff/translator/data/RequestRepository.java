package ru.tinkoff.translator.data;

import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<RequestInfo, Long>{
}
