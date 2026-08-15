package com.tonbiattack.unicodevalidation;

/**
 * 表示名登録の可否と、判定に使った測定値を表します。
 */
public record RegistrationResult(boolean accepted, int measuredCharacterCount, String message) {
    public static RegistrationResult accepted(int measuredCharacterCount) {
        return new RegistrationResult(true, measuredCharacterCount, "登録しました");
    }

    public static RegistrationResult rejected(int measuredCharacterCount, String message) {
        return new RegistrationResult(false, measuredCharacterCount, message);
    }
}
