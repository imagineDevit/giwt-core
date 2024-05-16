package io.github.imagineDevit.giwt.core.utils;

import java.util.Map;

/**
 * An immutable pair of two values.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 * @author imagineDevit
 */
record ImmutablePair<K, V>(K key, V value) implements Map.Entry<K, V> {
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}
