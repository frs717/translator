package ru.tinkoff.translator.data.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.tinkoff.translator.data.RequestRepository;
import ru.tinkoff.translator.data.dto.RequestInfo;

import java.sql.Types;
import java.util.Arrays;

@Repository
public class JdbcRequestRepository implements RequestRepository {

    private static final String SAVE_QUERY = "insert into request (source_text, source_language, target_language, " +
            "translated_text, ip, request_time) values (?, ?, ?, ?, ?, ?)";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RequestInfo save(RequestInfo requestInfo) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(SAVE_QUERY,
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP);

        pscf.setReturnGeneratedKeys(true);

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Arrays.asList(requestInfo.getSourceText(),
                requestInfo.getSourceLanguage(), requestInfo.getTargetLanguage(), requestInfo.getTranslatedText(),
                requestInfo.getIp(), requestInfo.getRequestTime()));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(psc, keyHolder);
        requestInfo.setId(keyHolder.getKey().longValue());
        return requestInfo;
    }

}
