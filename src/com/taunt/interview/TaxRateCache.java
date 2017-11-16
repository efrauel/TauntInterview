package com.taunt.interview;


import java.util.concurrent.ConcurrentHashMap;

/**
 * This is our LRU tax rate cache. It has a concurrentHashMap for storing the tax code objects. 
 * It also has a doubly linked list that is used to maintain a list of keys in order of most recently used.
 * So if we're at capacity we know we can remove the tail.prev.key from the cache.
 * 
 * I chose not to directly extend a ConcurrentHashMap so that the cache object itself could easily be swapped
 *  out with something like reddis or memcached without effecting the code using this class.
 * 
 * The methods are all threadsafe, and this could easily be added to an API or servlet to handle multiple requests concurrent request.
 */
public class TaxRateCache {
	
	private static final int CAPACITY = 50000;
	
	private ConcurrentHashMap<String, TaxRate> cache;
	private Node head;
	private Node tail;
	
	public TaxRateCache(){
		//Build linked list;
		head = new Node("head");
		tail = new Node("tail");
		//Head and tail point to eachother bc the list is empty.
		head.next = tail;
		tail.prev = head;
		
		cache = new ConcurrentHashMap<String, TaxRate>(CAPACITY);
	}
	
	/**
	 * Checks the cache for the key value. If it isn't present, it returns null.
	 * If it is present, it moves the corresponding node to the front of the list indicating it was recently accessed.
	 * @param key - an address
	 * @return the tax rate for that address or null if not in cache.
	 */
	public Double get(String key){
		if(cache.containsKey(key)){
			TaxRate code = cache.get(key);
			//Move it to the front because it was used most recently.
			remove(code.node);
			add(code.node);
			return code.salesTax;
		} else {
			return null;
		}
	}
	
	/**
	 * Stores this tax rate in the cache assocaiated with the given address (key)
	 * 
	 * @param key - an address
	 * @param taxRate - the sales tax associated with that address
	 */
	public synchronized void put(String key, double taxRate) {
	
		//Check just incase its been added since they checked to see if it was in the cache.
			if(!cache.containsKey(key)){
				TaxRate taxCode = new TaxRate(taxRate, new Node(key));
				if(cache.size() < CAPACITY){
					cache.put(key, taxCode);
					add(taxCode.node);
				} else {
					//remove the Least Recently Used key.
					cache.remove(tail.prev.key);
					//shuffle around the nodes so the new one is in front
					remove(tail.prev);
					cache.put(key, taxCode);
					add(taxCode.node);
				}
			}
	}
	
	
	//helper function to remove an element from the linked list
	private synchronized void remove(Node node){
		//make the previous node point forward to the next node
		node.prev.next = node.next;
		//make the next node point backwards to the previous node, thus nothing is pointing to this node now.
		node.next.prev = node.prev;
	}
	
	//helper method to add something to the front of the linked list.
	private synchronized void add(Node node) {
		//point node to head and head.next.
		node.next = head.next;
		node.prev = head;
		//point head.next to new node
		head.next.prev = node;
		//point head.next to node putting it at the front of the list
		head.next = node;
	}
	

	public class TaxRate {
		double salesTax;
		//Contains a Node so we can easily remove it or move it to the front of the list without searching for it in the linked list.
		Node node;
		TaxRate(double salesTax, Node node){
			this.salesTax = salesTax;
			this.node = node;
		}

	}
	
	//doubly linked list implementation
	private class Node {	
		private Node prev;
		private Node next;
		//Maintain key and value for easier readability and cache lookups
		private String key;
		
		Node(String key) {
			prev = null;
			next = null;
			this.key = key;
		}
	}
}
