package com.singh;

public class BloomFilter<E> {
	private final int m, k;
	
	/**
	 * Constructs a Bloom filter with provided values of m and k
	 * @param m size of the boolean array (bit array in the original Bloom filter defintion)
	 * @param k number of hash functions
	 * @see <a href="https://en.wikipedia.org/wiki/Bloom_filter"></a> 
	 */
	public BloomFilter(int m, int k) {
		if(m <= 0 || k <= 0) throw new IllegalArgumentException("m and k must both be positive");
		this.m = m;
		this.k = k;
	}
	
	public void put(E e) {
	}
	
	public boolean probablyContains(E e) {
		throw new UnsupportedOperationException();
	}
	
}
