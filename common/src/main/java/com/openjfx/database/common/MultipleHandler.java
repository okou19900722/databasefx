package com.openjfx.database.common;

/**
 * @param <T1>
 * @param <T2>
 * @author yangkui
 * @since 1.0
 */
@FunctionalInterface
public interface MultipleHandler<T1, T2, T3> {
    void handler(T1 t1, T2 t2, T3 t3);
}
