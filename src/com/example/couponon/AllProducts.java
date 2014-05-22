package com.example.couponon;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

/**
 * This class stores all the products when the app is created, this is an attempt to not store information on the phone
 * this is one of the changes to be made on future versions.
 * */
public class AllProducts {


	private static final HashMap<String, Product> products = new HashMap<String, Product>();
	public static boolean LOADED = false;
	private static final String LOG = "com.example.couponon.ALLProducts";
	
	public static Product findProductById(String id){		
		return products.get(id);
	}
	
	/**
	 *  checks if product exists, then inserts if it doesn't exist and returns the product extracted from the JSON
	 *  if the product is already stored, returns that product
	 */
	public static Product insertProductFromJSON(JSONObject jObject){
		Product p = null;
		try {
			String id = jObject.getString("prod_id");
			p = findProductById(id);
			if(p == null){
				p = new Product(jObject);
				products.put(id, p);
			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}


	public static void LoadProducts() {
		// TODO Auto-generated method stub
		LoadProductsTask loadproducts = new LoadProductsTask();
		loadproducts.execute();
		
	}
	
	
	public static class LoadProductsTask extends AsyncTask<Void, Void, JSONArray>{

		@Override
		protected JSONArray doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e(LOG, " POOP");
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			//parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			
			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/get_all_products.php",parameters);
		}
		
		protected void onPostExecute(JSONArray lists){

			Log.e(LOG, " POST LOAD" + lists.length() + "PRODUCTS");
			try {			
				for(int index = 0; index < lists.length(); index++){
					JSONObject jObject = (JSONObject) lists.get(index);
					Product p = new Product(jObject);
					Log.e(LOG,"p id --> "+ p.getId());
					products.put(p.getId(), p);
				}
				LOADED = true;
			}catch (JSONException e) {
				//something wrong with the objects
				//Log.e(LOG, "-something wrong with the JSON objects-");
				e.printStackTrace();
			}			
		}

	}


}
