package com.taunt.interview.test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.taunt.interview.TaxRateCache;

/*
 * This ConcurrentTestRunner runs each @Test method 4 times in parallel. 
 * I tested removing certain synchronized keywords from my TaxRateCache implementation and whatched these fail. 
 * 
 */
@RunWith(ConcurrentTestRunner.class)
public class TaxRateCacheConcurrencyTest {
		private TaxRateCache fastTax = new TaxRateCache();
		AtomicInteger counter = new AtomicInteger(0);
		private Map<String, Double> testMap = new ConcurrentHashMap<String, Double>(100000);
	    @Test
	    public void addToTheMap()
	    {
	    	for(int i = 0; i<100000; i++){
	    		counter.incrementAndGet();
	    		testMap.put(i+"", i/100d );
	    		//simulates entries that are commonly requested
	    		fastTax.get(i%3+"");
	    		fastTax.get(i%5+"");
	    	    fastTax.put(i+"", testMap.get(i+""));
	    	    
	    		
	    	}
	    }
	    @Test
	    public void testValues()
	    {
	    	int i = 0;
	    	for(String key: testMap.keySet()){
	    		Double value = fastTax.get(key);
	    		if(value!=null){
	    		//	System.out.println(i++ + ": " + counter.get() + " put calls were made. For key " + key + " value " + testMap.get(key) + " is expected and we got " + value );
	    			assertEquals(counter.get() + " put calls were made. "
	    					+ "For key " + key + " value " + testMap.get(key) + 
	    					" is expected and we got " + value  ,value.doubleValue(), testMap.get(key).doubleValue(),0);
	    		}
	    	}
	    }
}
