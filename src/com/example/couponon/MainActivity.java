/*
 * Author: Tom Dror
 * 
 */

package com.example.couponon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static final String APP_SHARED_PREFS = "com.example.couponon.couponon_preferences";
	private final String LOG = "com.example.couponon.MAIN_ACTIVITY";
	private boolean isUserLoggedIn = false;
	static String CURRENT_USER = null;
	private SharedPreferences sharedPrefs;

	ListView lv;
	List<Coupon> coups;
	CouponAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		this.checkLoginState();

		if(isUserLoggedIn){			
			Log.e(LOG, "user is logged in");			
			loadshit();
			setContentView(R.layout.activity_main);

			TextView userTextView = (TextView) this.findViewById(R.id.userTextView);
			userTextView.setText("Recommended Coupons for "+ CURRENT_USER);
			ListView lv = (ListView) this.findViewById(R.id.listView);
			//final Context context = this;		
			coups = new ArrayList<Coupon>();
			adapter = new CouponAdapter(this, R.layout.coupon_item, coups);		
			lv.setAdapter(adapter);

			Log.e(LOG, "calling execute");
			RecommendedCouponLoader test = new RecommendedCouponLoader();
			test.execute();


		}

	}
	private void loadshit(){
		if(!AllProducts.LOADED){
			Log.e(LOG, "loading products");
			AllProducts.LoadProducts();
		}
		if(!AllMyLists.P_LOADED ){
			Log.e(LOG, "loading p lists");
			AllMyLists.loadpLists();
		}
		if(!AllMyLists.C_LOADED ){
			Log.e(LOG, "loading c lists");
			AllMyLists.loadcLists();
		}
	}
	@Override
	protected void onResume(){
		checkLoginState();
		super.onResume();		
	}

	@Override
	protected void onRestart(){
		checkLoginState();
		super.onRestart();		
	}

	public void checkLoginState(){
		sharedPrefs = getApplicationContext().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
		isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
		if (!isUserLoggedIn) {
			Intent loginIntent = new Intent(this, LoginActivity.class);
			//make sure user can't back here
			// loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(loginIntent);
			finish();
		}
		else{
			CURRENT_USER = sharedPrefs.getString("currentLoggedInUser", "GUEST");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			Toast.makeText(this, "search", Toast.LENGTH_LONG).show();
			// openSearch();
			return true;
		case R.id.action_logout:
			logout();
			return true;
		case R.id.action_mylists:
			chooseList();
			return true;
		case R.id.action_scan:
			Intent listActivityIntentWithScan = new Intent(this, ProductListActivity.class);
			listActivityIntentWithScan.putExtra("barcode_result", "scan");
			startActivity(listActivityIntentWithScan);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private void chooseList() {
		final int COUPON = 0, PRODUCT = 1;
		final String[] listTypes = {"Coupon Lists","Product Lists"};
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setItems(listTypes,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				Intent listActivityIntent = new Intent(getApplicationContext(), MyListsActivity.class);
				switch(which){
				case COUPON:
					listActivityIntent.putExtra("isCoupon", true);     			
					break;
				case PRODUCT:
					listActivityIntent.putExtra("isCoupon", false);
					break;
				}
				startActivity(listActivityIntent);
			}}).create();

		dialog.show();

	}
	public void logout(){
		Editor editor;
		editor = sharedPrefs.edit();
		editor.putBoolean("userLoggedInState", false);
		editor.putString("currentLoggedInUser", null);
		editor.commit();
		finish();
	}

	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
	private class RecommendedCouponLoader extends AsyncTask<Void, Void, JSONArray>{

		//private String result = "meh";
		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE LOAD RECOMMENDED COUPONS");
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", CURRENT_USER));
			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/recommended_coupons.php",parameters);
		}

		protected void onPostExecute(JSONArray coupons){

			Log.e(LOG, " POST EXECUTE LOAD RECOMMENDED COUPONS");//+coupons.toString()+"\n-----------------------\n");
			try {			
				for(int index = 0; index < coupons.length(); index++){
					JSONObject jObject = (JSONObject) coupons.get(index);
					//jOBject contains information for the coupon and the product
					//enddate, price, coupid, prodid, name, dept, startdate, prodname, discount
					//first insert product because adding a new coupon finds the existing product
					//since the product never changes, might consider saving product databse on device
					AllProducts.insertProductFromJSON(jObject);		///TODO might be obselete now, conisder fixing php sql search			
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
