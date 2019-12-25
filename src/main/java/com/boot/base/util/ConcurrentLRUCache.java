package com.boot.base.util;


import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;

import java.util.Map;
import java.util.Set;

/**
 * 		支持并发的，超过固定大小自动淘汰最老数据的缓存（LRU 即 Least Rencetly Used（最近最少使用）缓存替换策略），
 * 		缓存中的数据是有序的
 * Created by mengdexuan on 2016/12/16 15:47.
 */
public class ConcurrentLRUCache<K, V> {

	public static final int DEFAULT_CONCURENCY_LEVEL = 32;

	private final ConcurrentLinkedHashMap<K, V> map;


	public ConcurrentLRUCache(int capacity) {
		this(capacity, DEFAULT_CONCURENCY_LEVEL);
	}

	public ConcurrentLRUCache(int capacity, int concurrency) {
		map = new ConcurrentLinkedHashMap.Builder<K, V>().weigher(Weighers.<V> singleton())
				.initialCapacity(capacity).maximumWeightedCapacity(capacity)
				.concurrencyLevel(concurrency).build();
	}

	public void put(K key, V value) {
		map.put(key, value);
	}

	public V get(K key) {
		V v = map.get(key);
		return v;
	}


	public V remove(K key) {
		return map.remove(key);
	}

	public long getCapacity() {
		return map.capacity();
	}


	public void updateCapacity(int capacity) {
		map.setCapacity(capacity);
	}


	public int getSize() {
		return map.size();
	}


	public void clear() {
		map.clear();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Set<Map.Entry<K, V>> entrySet(){
		return map.entrySet();
	}
}
