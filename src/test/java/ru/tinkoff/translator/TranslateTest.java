package ru.tinkoff.translator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.translator.service.YandexTranslatorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TranslateTest {

    @Autowired
    private YandexTranslatorService translator;

    @Test
    public void test() {
        String text = "Cat Dog Tree";
        String correctTranslatedText = "Katze Hund Baum";
        String translatedText = translator.translate("en", "de", text);
        Assertions.assertEquals(correctTranslatedText, translatedText);
    }

}
