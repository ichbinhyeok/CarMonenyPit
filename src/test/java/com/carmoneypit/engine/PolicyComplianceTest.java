package com.carmoneypit.engine;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

class PolicyComplianceTest {

    private static final List<String> BANNED_PATTERNS = List.of(
            "based on nada",
            "based on kbb",
            "nada/kbb",
            "actual repair databases",
            "thousands of owners",
            "team of data analysts",
            "team of automotive experts",
            "signal freshness",
            "freshness to google",
            "get 2-3 offers today",
            "peddle, carmax, and local dealers",
            "get offers from peddle, carmax",
            "highest bid",
            "exclusive discounts");

    @Test
    void noBannedPatternsInSourceAndTemplates() throws IOException {
        Path srcMain = Path.of("src/main");
        if (!Files.exists(srcMain)) {
            return;
        }

        try (Stream<Path> files = Files.walk(srcMain)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> {
                        String lower = path.toString().toLowerCase();
                        return lower.endsWith(".java") || lower.endsWith(".jte");
                    })
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path).toLowerCase();
                            for (String banned : BANNED_PATTERNS) {
                                if (content.contains(banned)) {
                                    fail("Banned pattern \"" + banned + "\" found in " + path);
                                }
                            }
                        } catch (IOException e) {
                            fail("Could not read " + path);
                        }
                    });
        }
    }
}
