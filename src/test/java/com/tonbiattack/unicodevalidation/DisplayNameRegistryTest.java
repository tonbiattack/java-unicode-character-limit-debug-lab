package com.tonbiattack.unicodevalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * JDK標準ツールだけで実行する回帰テストです。
 */
public final class DisplayNameRegistryTest {
    private final List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        DisplayNameRegistryTest test = new DisplayNameRegistryTest();
        test.acceptsThreeUnicodeCodePointsAndStoresTheDisplayName();
        test.rejectsANameThatExceedsTheCodePointLimit();
        test.throwIfAnyFailure();
        System.out.println("PASS DisplayNameRegistryTest");
    }

    void acceptsThreeUnicodeCodePointsAndStoresTheDisplayName() {
        DisplayNameRegistry registry = new DisplayNameRegistry(3);

        RegistrationResult result = registry.register("A😀B");

        assertTrue(result.accepted(), "3コードポイントの表示名を受け入れる");
        assertEquals(3, result.measuredCharacterCount(), "コードポイント数を結果へ返す");
        assertTrue(registry.contains("A😀B"), "受理した表示名を登録する");
        assertEquals(Set.of("A😀B"), registry.registeredNames(), "最終状態は受理した表示名だけを持つ");
    }

    void rejectsANameThatExceedsTheCodePointLimit() {
        DisplayNameRegistry registry = new DisplayNameRegistry(3);

        RegistrationResult result = registry.register("A😀BC");

        assertFalse(result.accepted(), "4コードポイントの表示名を拒否する");
        assertEquals(4, result.measuredCharacterCount(), "超過判定にもコードポイント数を使う");
        assertFalse(registry.contains("A😀BC"), "拒否した表示名は登録しない");
    }

    private void assertTrue(boolean actual, String expectation) {
        if (!actual) {
            failures.add("expected=true actual=false: " + expectation);
        }
    }

    private void assertFalse(boolean actual, String expectation) {
        if (actual) {
            failures.add("expected=false actual=true: " + expectation);
        }
    }

    private void assertEquals(Object expected, Object actual, String expectation) {
        if (!expected.equals(actual)) {
            failures.add("expected=" + expected + " actual=" + actual + ": " + expectation);
        }
    }

    private void throwIfAnyFailure() {
        if (!failures.isEmpty()) {
            throw new AssertionError(String.join(System.lineSeparator(), failures));
        }
    }
}
