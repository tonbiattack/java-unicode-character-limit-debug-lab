package com.tonbiattack.unicodevalidation;

/**
 * 表示名登録の判定結果をまとめた値です。
 *
 * @param accepted 登録を受け入れた場合は {@code true}、拒否した場合は {@code false}
 * @param measuredCharacterCount 判定時に数えたUnicodeコードポイント数
 * @param message 利用者へ返す判定結果のメッセージ
 */
public record RegistrationResult(boolean accepted, int measuredCharacterCount, String message) {
    /**
     * 表示名を登録できた場合の結果を作ります。
     *
     * @param measuredCharacterCount 判定時に数えたUnicodeコードポイント数
     * @return 登録成功を表す結果
     */
    public static RegistrationResult accepted(int measuredCharacterCount) {
        return new RegistrationResult(true, measuredCharacterCount, "登録しました");
    }

    /**
     * 表示名を登録できなかった場合の結果を作ります。
     *
     * @param measuredCharacterCount 判定時に数えたUnicodeコードポイント数
     * @param message 登録できなかった理由を表すメッセージ
     * @return 登録失敗を表す結果
     */
    public static RegistrationResult rejected(int measuredCharacterCount, String message) {
        return new RegistrationResult(false, measuredCharacterCount, message);
    }
}
