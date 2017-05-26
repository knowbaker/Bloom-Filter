package com.singh;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBloomFilter {

	@Test
	public void test() {
		BloomFilter<String> bf = new BloomFilter<>(10, 2);
		bf.put("Hello");
		bf.put("world");
		assertTrue(bf.probablyContains("Hello"));
		assertTrue(bf.probablyContains("world"));
	}

}
