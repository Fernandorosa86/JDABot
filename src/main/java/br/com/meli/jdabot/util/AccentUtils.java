package br.com.meli.jdabot.util;

import java.text.Normalizer;

public class AccentUtils {
    public static String removeAccents(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
