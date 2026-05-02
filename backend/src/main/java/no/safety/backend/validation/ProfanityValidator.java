package no.safety.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A ConstraintValidator implementation that invalidates strings that contain profanities listed in
 * a local profanity list text file.
 */
@Slf4j
public class ProfanityValidator implements ConstraintValidator<NoProfanity, String> {

    private static final String LIST_RESOURCE = "/profanity-list.txt";
    private static final List<String> BLOCKED = loadList();

    private static final Map<Character, Character> LEET_SUBSTITUTIONS = Map.of(
            '0', 'o',
            '1', 'i',
            '3', 'e',
            '4', 'a',
            '5', 's',
            '7', 't',
            '@', 'a',
            '$', 's'
    );

    private static List<String> loadList() {
        try (InputStream in = ProfanityValidator.class.getResourceAsStream(LIST_RESOURCE)) {
            if (in == null) {
                log.warn("Profanity list resource '{}' not found; profanity filter is effectively disabled.", LIST_RESOURCE);
                return List.of();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .map(line -> line.toLowerCase(Locale.ROOT))
                        .toList();
            }
        } catch (IOException e) {
            log.error("Failed to load profanity list from '{}'", LIST_RESOURCE, e);
            return List.of();
        }
    }

    private static String normalize(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            sb.append(LEET_SUBSTITUTIONS.getOrDefault(c, c));
        }
        return sb.toString();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String normalized = normalize(value);
        return BLOCKED.stream().noneMatch(normalized::contains);
    }
}
