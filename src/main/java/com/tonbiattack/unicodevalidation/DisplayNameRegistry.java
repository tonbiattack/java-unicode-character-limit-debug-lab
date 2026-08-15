package com.tonbiattack.unicodevalidation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 表示名を登録する小さな境界です。
 */
public final class DisplayNameRegistry {
    private final int maximumCharacterCount;
    private final Set<String> registeredNames = new LinkedHashSet<>();

    public DisplayNameRegistry(int maximumCharacterCount) {
        this.maximumCharacterCount = maximumCharacterCount;
    }

    /**
     * 表示名を文字数上限に照らして登録します。
     */
    public RegistrationResult register(String displayName) {
        // Unicodeコードポイント数で数え、補助文字を2文字として扱わない。
        int measuredCharacterCount = displayName.codePointCount(0, displayName.length());
        if (measuredCharacterCount > maximumCharacterCount) {
            return RegistrationResult.rejected(
                    measuredCharacterCount,
                    "表示名は%d文字以内で入力してください".formatted(maximumCharacterCount)
            );
        }

        registeredNames.add(displayName);
        return RegistrationResult.accepted(measuredCharacterCount);
    }

    /**
     * 指定した表示名が登録済みかを返します。
     */
    public boolean contains(String displayName) {
        return registeredNames.contains(displayName);
    }

    /**
     * 登録済みの表示名を読み取り専用で返します。
     */
    public Set<String> registeredNames() {
        return Collections.unmodifiableSet(registeredNames);
    }
}
