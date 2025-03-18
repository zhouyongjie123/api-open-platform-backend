package com.zyj.core.unit;

import java.util.Optional;

public class ThrowUtil {
    public static <T extends Throwable> void throwIf(Boolean condition, T throwable) throws T {
        Optional.ofNullable(condition) // 将 condition 包装为 Optional
                .filter(c -> !c)      // 如果 condition 为 false，保留
                .orElseThrow(() -> throwable); // 否则抛出异常
    }

    public static <T extends RuntimeException> void throwIf(Boolean condition, T runtimeException) {
        Optional.ofNullable(condition)
                .filter(c -> !c)
                .orElseThrow(() -> runtimeException);
    }

    public static <T extends RuntimeException> void throwNotIf(Boolean condition, T runtimeException) {
        throwIf(!condition, runtimeException);
    }
}
