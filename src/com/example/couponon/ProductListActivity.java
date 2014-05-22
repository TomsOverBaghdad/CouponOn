package com.example.couponon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
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

public class ProductListActivity extends Activity{
	private final String LOG = "com.example.couponon.PRODUCTLISTACTIVITY";
	private List<Product> prod;
	private ProductAdapter adapter;
	private ListView lv;
	private int list_id, item_id;
	private String name;
	LoadListItemsTask loadItems = new LoadListItemsTask();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lists);		

		TextView userTextView = (TextView) this.findViewById(R.id.userTextView);
		userTextView.setText("Product list for "+ MainActivity.CURRENT_USER);
		
		lv = (ListView) this.findViewById(R.id.prodListView);
		prod = new ArrayList<Product>();
		adapter = new ProductAdapter(this, R.layout.coupon_item, prod);	
		lv.setAdapter(adapter);


		Intent i = this.getIntent();
		list_id = i.getIntExtra("list_id", -1);//by default, -1 means no list was in the intent so a list will be created in the POST request
		item_id = i.getIntExtra("item_id", -1);
		name = i.getStringExtra("name");
		
		String barcodeResult = i.getStringExtra("barcode_result");		
		if(barcodeResult!= null && barcodeResult.equals("scan")) 
			scan();

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
		case R.id.action_scan:
			scan();
			return true;
		case R.id.action_find_savings:
			findSavings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void scan(){
		try{
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");//for Qr code, its "QR_CODE_MODE" instead of "PRODUCT_MODE"
			intent.putExtra("SAVE_HISTORY", false);//this stops saving ur barcode in barcode scanner app's history
			startActivityForResult(intent, 0);
		}catch(Exception e){//the user doesnt have the barcode app, send them to get it (it's free)
			Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
			Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
			startActivity(marketIntent);
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {				
				String contents = data.getStringExtra("SCAN_RESULT");//result will be the product id
				Product p = AllProducts.findProductById(contents);				
				if(p != null){
					//if no list is loaded, this will make a new list and add this product to it
					//if there is a list, this product will be added to it
					AddProductToListTask aplTask = new AddProductToListTask();
					aplTask.execute(p);					
				}else{
					Toast.makeText(this, contents + " -> not found", Toast.LENGTH_LONG).show();
					finish();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}
	private void findSavings() {
		FindSavingsTask savingstask = new FindSavingsTask();
		savingstask.execute();

	}
private class AddToListTask extends AsyncTask<String, Void, JSONArray>{
		
		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE ADD TO LIST");
		}
		
		@Override
		protected JSONArray doInBackground(String... params) {

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
				e.printStackTrace();
			}
		}

	}

	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
	private class FindSavingsTask extends AsyncTask<Void, Void, JSONArray>{

		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE FIND SAVINGS");
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			ArrayList<String> pids = new ArrayList<String>();
			for(Product p : prod){
				pids.add(p.getId());
			}
			//Log.e(LOG, " PIDS -> " + pids);
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			parameters.add(new BasicNameValuePair("list_name", "coupons for " + name));
			parameters.add(new BasicNameValuePair("product_json", pids.toString()));

			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/find_savings.php",parameters);
		}

		protected void onPostExecute(JSONArray p){
			Log.e(LOG, " POST EXECUTE ADD PROD TO LIST");
			int couponlist_id = p.optInt(0);
			if(couponlist_id > 0){
				Log.e(LOG, " NEW COUPON LIST ID => " + couponlist_id);
				Intent showList = new Intent(getBaseContext(), CouponListActivity.class);	
				showList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				showList.putExtra("list_id", couponlist_id);					
				getBaseContext().startActivity(showList);
			}
		}

	}
	
	
	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
	private class AddProductToListTask extends AsyncTask<Product, Void, Product>{

		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE ADD TO PROD LIST");
		}

		@Override
		protected Product doInBackground(Product... params) {
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			parameters.add(new BasicNameValuePair("list_option", "product"));
			parameters.add(new BasicNameValuePair("list_id", Integer.toString(list_id)));
			parameters.add(new BasicNameValuePair("item_id", params[0].getId()));

			JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/add_to_list.php",parameters);
			return params[0];
		}

		protected void onPostExecute(Product p){

			Log.e(LOG, " POST EXECUTE ADD PROD TO LIST");
			prod.add(p);
			adapter.notifyDataSetChanged();
		}

	}

	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
	private class LoadListItemsTask extends AsyncTask<Void, Void, JSONArray>{

		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE LOAD LIST");
		}

		@Override
		protected JSONArray doInBackground(Void... params) {

			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			parameters.add(new BasicNameValuePair("list_option", "product"));
			parameters.add(new BasicNameValuePair("list_id", Integer.toString(list_id)));

			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/get_list_items.php",parameters);
		}

		protected void onPostExecute(JSONArray lists){

			Log.e(LOG, " POST EXECUTE LOAD LIST ITEM");
			try {			
				for(int index = 0; index < lists.length(); index++){
					JSONObject jObject = (JSONObject) lists.get(index);
					prod.add(AllProducts.insertProductFromJSON(jObject));
				}
				adapter.notifyDataSetChanged();
			}catch (JSONException e) {

				Log.e(LOG, "-something wrong with the JSON objects-");
				e.printStackTrace();
			}			
		}

	}
}
