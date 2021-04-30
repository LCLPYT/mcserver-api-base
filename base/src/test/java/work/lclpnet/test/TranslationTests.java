/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import org.junit.jupiter.api.Test;
import work.lclpnet.serverapi.translate.ITranslationLoader;
import work.lclpnet.serverapi.translate.ServerTranslation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslationTests {

    @Test
    void mergeTest() throws IOException {
        final String expectedTestFirst = "This is a test",
                expectedTestSecond = "This is the second test", expectedTestOther = "Overwritten.";

        ITranslationLoader first = () -> {
            Map<String, Map<String, String>> languages = new HashMap<>();

            Map<String, String> en = new HashMap<>();
            en.put("test.first", expectedTestFirst);
            en.put("test.other", "This too.");
            languages.put("en_us", en);

            Map<String, String> ger = new HashMap<>();
            ger.put("test.first", "Das ist ein Test");
            ger.put("test.other", "Das auch");
            languages.put("de_de", ger);

            return languages;
        };

        ITranslationLoader second = () -> {
            Map<String, Map<String, String>> languages = new HashMap<>();

            Map<String, String> en = new HashMap<>();
            en.put("test.second", expectedTestSecond);
            en.put("test.other", expectedTestOther);
            languages.put("en_us", en);

            Map<String, String> ger = new HashMap<>();
            ger.put("test.second", "Das ist der zweite Test");
            ger.put("test.other", "Überschieben.");
            languages.put("de_de", ger);

            return languages;
        };

        ServerTranslation.loadFrom(first);
        ServerTranslation.loadFrom(second);

        String testFirst = ServerTranslation.getTranslation("en_us", "test.first");
        assertEquals(expectedTestFirst, testFirst);

        String testSecond = ServerTranslation.getTranslation("en_us", "test.second");
        assertEquals(expectedTestSecond, testSecond);

        String testOther = ServerTranslation.getTranslation("en_us", "test.other");
        assertEquals(expectedTestOther, testOther);
    }

}
