package com.taunt.interview;

import java.util.Random;

public class FastTaxLookup {

	private TaxRateCache taxCache;	
	
	public FastTaxLookup(){
		taxCache = new TaxRateCache();
	}
	
	public double fast_rate_lookup(String address){
		
		//this will move the node containing address to the front of the doubly linked list maintained by LinkedHashMap indicating it was most recently used.
		Double taxRate = taxCache.get(address);
		if( null != taxRate){
			return taxRate;
		} else {
			//do slow lookup and put it in cache
			double slowTax = sales_tax_lookup(address);
			taxCache.put(address, slowTax);
			return slowTax;
		}
	}
	
	
	private double sales_tax_lookup(String address) {
		//mimics slowness
		//Thread.sleep(1000);
		Random generator = new Random();
		double number = generator.nextDouble() * .25;
		return number;
		
		
	}
	
}
