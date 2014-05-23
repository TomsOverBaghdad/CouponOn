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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyListsActivity extends Activity{
	private String LOG = "com.example.couponon.MY_LISTS_ACTIVITY";
	ListView lv;
	MyListAdapter adapter;
	boolean isCoupon;


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lists);

		TextView userTextView = (TextView) this.findViewById(R.id.userTextView);

		lv = (ListView) this.findViewById(R.id.prodListView);
		isCoupon = getIntent().getBooleanExtra("isCoupon", false);//default value set to false

		if(isCoupon)
			userTextView.setText("COUPON Lists for "+ MainActivity.CURRENT_USER);
		else
			userTextView.setText("PRODUCT Lists for "+ MainActivity.CURRENT_USER);
		if(isCoupon)
			adapter = new MyListAdapter(this, R.layout.list, AllMyLists.listcouplist, isCoupon);
		else
			adapter = new MyListAdapter(this, R.layout.list, AllMyLists.listprodlist, isCoupon);
		lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_prod_list, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new_list:
			addList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}			

	}

	private void addList(){
		String type = (isCoupon? "Coupon" : "Product");
		final EditText input = new EditText(this);
		input.setHint("Name your new " + type + " list");
		new AlertDialog.Builder(this)
	    .setTitle("Add new " + type + " list")
	    .setView(input)
	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            Editable value = input.getText(); 
	            AddToListTask makeNewList = new AddToListTask();
	            makeNewList.execute(value.toString());
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        }
	    }).show();
	}
	
	private class AddToListTask extends AsyncTask<String, Void, JSONArray>{
		private String newListName;
		private int newListId;
		
		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE ADD TO LIST");
		}
		
		@Override
		protected JSONArray doInBackground(String... params) {
			newListName = params[0];
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			if(isCoupon){
				parameters.add(new BasicNameValuePair("list_option", "coupon"));
			}else{
				parameters.add(new BasicNameValuePair("list_option", "product"));
			}

			parameters.add(new BasicNameValuePair("list_name", newListName));

			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/add_to_list.php",parameters);
			
		}

		protected void onPostExecute(JSONArray result){
			try {
				Log.e(LOG, " POST EXECUTE ADD NEW LIST");
				// 0 is what the result is, 1 and 2 are the new list id and the item id if it was inserted
				if(result.getString(0).equalsIgnoreCase("new_list"))
					newListId = result.getInt(1);
					Log.e(LOG, "new list name => "+ newListName +", new list id => "+ newListId);
					MyList ml = new MyList(newListName,newListId);				
					if(isCoupon)
						AllMyLists.listcouplist.add(ml);
					else
						AllMyLists.listprodlist.add(ml);
					adapter.notifyDataSetChanged();
					
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
