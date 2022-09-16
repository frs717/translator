package ru.tinkoff.translator.data;

import ru.tinkoff.translator.data.dto.RequestInfo;

public interface RequestRepository {
    RequestInfo save(RequestInfo requestInfo);
}
