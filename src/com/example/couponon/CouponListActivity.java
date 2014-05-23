package com.example.couponon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CouponListActivity extends Activity{
	private final String LOG = "com.example.couponon.COUPONLISTACTIVITY";
	static List<Coupon> coups = new ArrayList<Coupon>();
	static CouponAdapter adapter;
	ListView lv = (ListView) this.findViewById(R.id.prodListView);
	LoadListItemsTask loadItems = new LoadListItemsTask();
	
	int list_id, item_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lists);		
		
		TextView userTextView = (TextView) this.findViewById(R.id.userTextView);
		userTextView.setText("Coupon list for "+ MainActivity.CURRENT_USER);
		
		adapter = new CouponAdapter(this, R.layout.coupon_item, coups);
		lv.setAdapter(adapter);
		
		
		list_id = getIntent().getIntExtra("list_id", -1);
		item_id = getIntent().getIntExtra("item_id", -1);
		if(item_id > -1){
			AddToListTask atlTask = new AddToListTask();
			atlTask.execute();
		}
		else
			loadItems.execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prod_list, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			//scan();
			return true;
		case R.id.action_mylists://TODO change to use all?
			//findSavings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class AddToListTask extends AsyncTask<String, Void, JSONArray>{
		
		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE ADD TO LIST");
		}
		
		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
		
				parameters.add(new BasicNameValuePair("list_option", "coupon"));
			
			parameters.add(new BasicNameValuePair("item_id", String.valueOf(item_id)));
			parameters.add(new BasicNameValuePair("list_id", String.valueOf(list_id)));

			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/add_to_list.php",parameters);
			
		}

		protected void onPostExecute(JSONArray result){
			try {
				Log.e(LOG, " POST EXECUTE ADD NEW ITEM --");
				// 0 is what the result is, 1 and 2 are the new list id and the item id if it was inserted
				if(result.getString(2) != null){
					Log.e(LOG, "new list name");
					loadItems.execute();
				}
					
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	
	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
	private class LoadListItemsTask extends AsyncTask<Void, Void, JSONArray>{
		//int WHICHLIST = 0;

		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE LOAD ITEM LIST");
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			parameters.add(new BasicNameValuePair("list_option", "coupon"));
			Log.e(LOG, "HERE IS THE LIST ID TO FIND COUPNS " + Integer.toString(list_id));
			parameters.add(new BasicNameValuePair("list_id", Integer.toString(list_id)));
			
			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/get_list_items.php",parameters);
		}

		protected void onPostExecute(JSONArray lists){

			coups.clear();
			Log.e(LOG, " POST EXECUTE LOAD LIST ITEM");//+coupons.toString()+"\n-----------------------\n");
			try {			
				for(int index = 0; index < lists.length(); index++){
					JSONObject jObject = (JSONObject) lists.get(index);
					//jOBject contains information for the coupon and the product
					//enddate, price, coupid, prodid, name, dept, startdate, prodname, discount
					//first insert product because adding a new coupon finds the existing product
					//since the product never changes, might consider saving product databse on device
					//AllProducts.insertProductFromJSON(jObject);	//redundant?
					coups.add(new Coupon(jObject));
				}
				adapter.notifyDataSetChanged();
			}catch (JSONException e) {
				//something wrong with the objects
				Log.e(LOG, "-something wrong with the JSON objects-");
				e.printStackTrace();
			}			
		}

	}
}
