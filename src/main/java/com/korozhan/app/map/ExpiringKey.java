package com.korozhan.app.map;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ExpiringKey<K> implements Delayed {

    private long startTime = System.currentTimeMillis();
    private final long maxLifeTimeMillis;
    private final K key;

    public ExpiringKey(K key, long maxLifeTimeMillis) {
        this.maxLifeTimeMillis = maxLifeTimeMillis;
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public void renew() {
        startTime = System.currentTimeMillis();
    }

    public void expire() {
        startTime = Long.MIN_VALUE;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        return Long.compare(this.getDelayMillis(), ((ExpiringKey) delayed).getDelayMillis());
    }

    private long getDelayMillis() {
        return (startTime + maxLifeTimeMillis) - System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpiringKey<?> that = (ExpiringKey<?>) o;

        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }
}
