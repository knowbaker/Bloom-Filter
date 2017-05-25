package com.singh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BloomFilter<E extends Serializable> {
	private static final MessageDigest DIGEST;
	private final int m, k;
	private boolean[] filter;
	static {
		try {
			DIGEST = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not initialize MD5 message digest", e);
		}
	}
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
		this.filter = new boolean[m];
	}
	
	/*
	 * @see http://blog.michaelschmatz.com/2016/04/11/how-to-write-a-bloom-filter-cpp/
	 * @see http://citeseer.ist.psu.edu/viewdoc/download;jsessionid=4060353E67A356EF9528D2C57C064F5A?doi=10.1.1.152.579&rep=rep1&type=pdf
	 */
	public void put(E e) {
		byte[] bytes = Util.getBytes(e);
		DIGEST.reset();
		DIGEST.update(bytes);
		byte[] digest = DIGEST.digest();//128 bit MD5 digest; take first 64 bit part
		int hash1 = Util.convertToInt(digest, 0);
		int hash2 = Util.convertToInt(bytes, 4);
		for(int i = 0; i < k; i++)
			filter[(hash1 + i*hash2) % m] = true;
	}
	
	public boolean probablyContains(E e) {
		throw new UnsupportedOperationException();
	}
	
	public int getM() {
		return m;
	}
	
	public int getK() {
		return k;
	}
	
	private static final class Util {
		private static <E extends Serializable> byte[] getBytes(E e) {
			byte[] bytes = null;
			try (ByteArrayOutputStream b = new ByteArrayOutputStream(); ObjectOutputStream o = new ObjectOutputStream(b)) {
				o.writeObject(e);
				bytes = b.toByteArray();
			} catch (IOException e1) {
				throw new RuntimeException("Could not serialize object into byte array", e1);
			}
			return bytes;
		}
		
		private static int convertToInt(byte[] bytes, int startIdx) {
			if(bytes.length - startIdx < 4) throw new IllegalArgumentException("Insufficient bits to form 32 bit int");
			return bytes[startIdx] << 24 | (bytes[startIdx+1] & 0xff) << 16 | (bytes[startIdx+2] & 0xff) << 8 | bytes[startIdx+3] & 0xff; 
		}
	}
}
