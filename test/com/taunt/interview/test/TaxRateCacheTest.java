package com.taunt.interview.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.*;

import com.taunt.interview.TaxRateCache;

public class TaxRateCacheTest {
	TaxRateCache taxCache;
	Random generator = new Random();
	
	@Before
	public void setup(){
		taxCache = new TaxRateCache();
	}
	
	@Test
	public void testTaxRateCacheStoreAndRetrieve(){
		assertNotNull(taxCache);
		assertNull(taxCache.get("Cache Miss"));
		taxCache.put("test", .05d);
		assertEquals(taxCache.get("test").doubleValue(), .05d, 0);
	}
	
	@Test 
	public void testTaxRateCacheToCapacity(){
		Map<String, Double> test = new HashMap<String, Double>(50000);

		for(int i = 0; i< 50000; i++) {
			String key = "" +i;
			test.put(key, generator.nextDouble() * .25d);
			taxCache.put(key, test.get(key));
		}
		for(String key: test.keySet()){
			assertEquals(taxCache.get(key).doubleValue(), taxCache.get(key).doubleValue(), 0);
		}
	}
	
	@Test
	public void testTaxRateCacheEviction(){
		String shouldBeEvicted = "evicted";
		double value = .05d;	
		String shouldNotBeEvicted = "notEvicted";
		double value2 = .04d;
		
		taxCache.put(shouldBeEvicted, value);
		taxCache.put(shouldNotBeEvicted, value2);
		for(int i = 0; i< 50000; i++) {
			String key = "" +i;
			//constantly read this one to make it Most recently used.
			taxCache.get(shouldNotBeEvicted);
			taxCache.put(key, .01d);			
		}
		
		assertEquals(taxCache.get(shouldNotBeEvicted).doubleValue(), value2, 0);
		assertNull(taxCache.get(shouldBeEvicted));
	}
	
	
}
