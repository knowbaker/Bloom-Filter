package com.singh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BloomFilter<E extends Serializable> {
	private final int m, k;
	private final boolean[] filter;
	
	/**
	 * Constructs a Bloom filter with provided values of m and k
	 * @param m size of the boolean array (bit array in the original Bloom filter definition)
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
	 * @see https://www.eecs.harvard.edu/~michaelm/postscripts/rsa2008.pdf
	 */
	public void put(E e) {
		byte[] digest = Util.getMd5DigestOf(e);//128 bit MD5 digest; take first 64 bit part and break it into two 32-bit ints as two hashes
		int hash1 = Util.convertToInt(digest, 0);
		int hash2 = Util.convertToInt(digest, 4);
		for(int i = 0; i < k; i++) {
			int x = hash1 + i*hash2;
			if(x < 0)
				x &= 0x7fffffff;
			filter[x % m] = true;
		}
	}
	
	public boolean probablyContains(E e) {
		byte[] digest = Util.getMd5DigestOf(e);//128 bit MD5 digest; take first 64 bit part and break it into two 32-bit ints as two hashes
		int hash1 = Util.convertToInt(digest, 0);
		int hash2 = Util.convertToInt(digest, 4);
		for(int i = 0; i < k; i++) {
			int x = hash1 + i*hash2;
			if(x < 0)
				x &= 0x7fffffff;
			if(!filter[x % m])
				return false;
		}
		return true;
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
			return (bytes[startIdx] & 0x7f) << 24 | (bytes[startIdx+1] & 0xff) << 16 | (bytes[startIdx+2] & 0xff) << 8 | bytes[startIdx+3] & 0xff; 
		}
		
		private static <E extends Serializable> byte[] getMd5DigestOf(E e) {
			byte[] bytes = Util.getBytes(e);
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e1) {
				throw new RuntimeException("Unable to create digest", e1);
			}
			md.update(bytes);
			byte[] digest = md.digest();
			return digest;
		}
	}
}
