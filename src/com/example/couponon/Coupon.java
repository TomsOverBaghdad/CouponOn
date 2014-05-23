package com.example.couponon;

import org.json.JSONException;
import org.json.JSONObject;

public class Coupon {
	private final String name, id;
	private final double discount, finalPrice, savings;
	private final String startdate, enddate;
	private final Product product;

	Coupon(String id, String name, double discount, String startdate, String enddate, Product product){
		this.id = id;
		this.name = name;		
		this.product = product;
		this.finalPrice = product.getPrice() * (1 - discount);
		this.discount = discount;
		this.savings = product.getPrice() - finalPrice;
		this.startdate = startdate;
		this.enddate = enddate;

	}

	public Coupon(JSONObject jObject) throws JSONException {
		this.id = jObject.getString("coup_id");
		this.name = jObject.getString("name");
		this.startdate = jObject.getString("start_date");
		this.enddate = jObject.getString("end_date");
		this.discount = Float.parseFloat(jObject.get("discount").toString());
		//
		//int prod_id = Integer.parseInt(jObject.getString("prod_id"));
		String prod_id = jObject.getString("prod_id");
		this.product = AllProducts.findProductById(prod_id);
		this.finalPrice = product.getPrice() * (1 - discount);
		this.savings = product.getPrice() - finalPrice;		
	}

	public String getId(){
		return id;
	}
	
	public String getName() {
		return name;
	}

	public double getDiscount() {
		return discount;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public double getSavings() {
		return savings;
	}

	public String getStartdate() {
		return startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public Product getProduct() {
		return product;
	}

}
