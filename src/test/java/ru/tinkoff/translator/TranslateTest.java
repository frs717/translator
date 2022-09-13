package ru.tinkoff.translator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.translator.yandex_translator_api.YandexTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TranslateTest {

    @Autowired
    private YandexTranslator translator;


    @Test
    public void test() {
        List<String> text = new ArrayList<>(Arrays.asList("Cat", "Dog", "Tree"));
        List<String> correctTranslatedText = new ArrayList<>(Arrays.asList("Katze", "Hund", "Baum"));
        List<String> translatedText = translator.translate("en", "de", text);
        Assertions.assertEquals(correctTranslatedText, translatedText);
    }
}
