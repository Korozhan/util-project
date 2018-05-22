package com.korozhan.app.map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExpiringHashMapTest {
    private final static int SLEEP_MULTIPLIER = 80;

    @Test
    public void basicGetTest() throws InterruptedException {
        ExpiringMap<String, String> map = new ExpiringHashMap<String, String>();
        map.put("a", "b", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        assertEquals("b", map.get("a"));
    }

    @Test
    public void basicExpireTest() throws InterruptedException {
        ExpiringMap<String, String> map = new ExpiringHashMap<String, String>();
        map.put("a", "b", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(3 * SLEEP_MULTIPLIER);
        assertNull(map.get("a"));
    }

    @Test
    public void basicRenewTest() throws InterruptedException {
        ExpiringMap<String, String> map = new ExpiringHashMap<String, String>();
        map.put("a", "b", 3 * SLEEP_MULTIPLIER);
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        map.renewKey("a");
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals("b", map.get("a"));
    }

    @Test
    public void getRenewTest() throws InterruptedException {
        ExpiringMap<String, String> map = new ExpiringHashMap<String, String>();
        map.put("a", "b", 3 * SLEEP_MULTIPLIER);
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals("b", map.get("a"));
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals("b", map.get("a"));
    }

    @Test
    public void multiplePutThenRemoveTest() throws InterruptedException {
        ExpiringMap<String, String> map = new ExpiringHashMap<String, String>();
        map.put("a", "b", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "c", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "d", 400 * SLEEP_MULTIPLIER);
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals("d", map.remove("a"));
    }

    @Test
    public void multiplePutThenGetTest() throws InterruptedException {
        ExpiringMap<String, String> map = new ExpiringHashMap<String, String>();
        map.put("a", "b", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "c", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "d", 400 * SLEEP_MULTIPLIER);
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals("d", map.get("a"));
    }

    @Test
    public void insertionOrderTest() throws InterruptedException {
        ExpiringMap<String, Integer> map = new ExpiringHashMap<String, Integer>(30000);
        map.put("123456", 999);
        assertEquals(map.get("123456"), Integer.valueOf(999));
        map.put("123456", 123);
        map.put("777456", 333);
        assertEquals(map.get("123456"), Integer.valueOf(123));
        assertEquals(map.get("777456"), Integer.valueOf(333));
        map.put("777456", 123);
        map.put("123456", 321);
        assertEquals(map.get("123456"), Integer.valueOf(321));
        assertEquals(map.get("777456"), Integer.valueOf(123));
    }

    @Test
    public void insertionOrderDefaultTest() throws InterruptedException {
        ExpiringMap<String, Integer> map = new ExpiringHashMap<String, Integer>(3000);
        map.put("123456", 999);
        Thread.sleep(3000);
        assertNull(map.get("123456"));
    }

}