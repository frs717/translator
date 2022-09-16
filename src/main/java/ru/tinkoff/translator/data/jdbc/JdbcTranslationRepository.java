package ru.tinkoff.translator.data.jdbc;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.tinkoff.translator.data.TranslationRepository;
import ru.tinkoff.translator.data.dto.TranslationInfo;

import java.sql.Types;
import java.util.Arrays;

@Repository
public class JdbcTranslationRepository implements TranslationRepository {

    private static final String SAVE_QUERY = "insert into word (request, source_word, translated_word) values (?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTranslationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TranslationInfo save(TranslationInfo translationInfo) {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(SAVE_QUERY,
                Type.LONG, Types.VARCHAR, Types.VARCHAR);

        pscf.setReturnGeneratedKeys(true);

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(Arrays.asList(translationInfo.getRequest(),
                        translationInfo.getSourceWord(), translationInfo.getTranslatedWord()));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(psc, keyHolder);
        translationInfo.setId(keyHolder.getKey().longValue());
        return translationInfo;
    }

}
