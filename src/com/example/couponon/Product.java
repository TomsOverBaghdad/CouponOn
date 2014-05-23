package com.example.couponon;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
	final private String id;
	final private String name;
	final private double price;
	final private String dept;
	final private boolean isMic;
	
	Product(String id, String name, String dept, double price, boolean isMic){
		this.id = id;
		this.name = name;
		this.dept = dept;
		this.price = price;
		this.isMic = isMic;
	}
	
	public Product(JSONObject jObject) throws JSONException {
		this.name = jObject.getString("name");
		this.dept = jObject.getString("dept");
		this.id = jObject.getString("prod_id");
		this.price = Double.parseDouble(jObject.getString("price"));
		this.isMic = false;//jObject.get("isMic");
	}

	public boolean getMic(){		
		return isMic;
	}
	
	public String getId() {
		return id;
	}

	public String getDept() {
		return dept;
	}

	public String getName() {
		return name;
	}
	public double getPrice() {
		return price;
	}
	
}
