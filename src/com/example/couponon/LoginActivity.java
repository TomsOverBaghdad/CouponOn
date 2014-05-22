package com.example.couponon;

import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{
	private String currentUser = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user_signup);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_user, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_login:
			doLogin();
			return true;
		case R.id.action_register:
			setupAndRegisterPost();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("unchecked")
	public void register(ArrayList<NameValuePair> nameValuePairs){//also logs in
		RegisterCustomerTask regCustTask = new RegisterCustomerTask();
		regCustTask.execute(nameValuePairs);

	}

	private void setupAndRegisterPost() {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//setup registration POST
		TextView customer = (TextView) findViewById(R.id.new_username);
		TextView password = (TextView) findViewById(R.id.new_user_password);
		TextView age = (TextView) findViewById(R.id.age);
		TextView zip = (TextView) findViewById(R.id.zipcode);
		String gender = "M";//TODO get the spinner to work for m/f

		if(!customer.getText().toString().isEmpty()){//TODO validate string is an email
			currentUser =  customer.getText().toString();
			nameValuePairs.add(new BasicNameValuePair("customer",currentUser));
		}
		if(!password.getText().toString().isEmpty())
			nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));		
		if(!age.getText().toString().isEmpty())//TODO
			nameValuePairs.add(new BasicNameValuePair("age", age.getText().toString()));
		if(!zip.getText().toString().isEmpty())//TODO validate length of 5
			nameValuePairs.add(new BasicNameValuePair("zip", zip.getText().toString()));
		//if(!gender.getText().toString().isEmpty())//TODO validate string is an email
		nameValuePairs.add(new BasicNameValuePair("gender", gender)); 
		
		register(nameValuePairs);
	}

	public void doLogin(){
		LayoutInflater inflater = LayoutInflater.from(this);
		final View dialogView = inflater.inflate(R.layout.dialog_signin, null);

		AlertDialog dialog = new AlertDialog.Builder(this)
		.setView(dialogView)
		.setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String customer= ((TextView) dialogView.findViewById(R.id.username)).getText().toString();
				String password = ((TextView) dialogView.findViewById(R.id.password)).getText().toString();
				login(customer, password);
			}

		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(getApplicationContext(), "CANCEL", Toast.LENGTH_LONG).show();
				dialog.cancel();
			}
		}).create();
		dialog.show();
	}

	protected void login(String customer, String password) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		currentUser =  customer;
		nameValuePairs.add(new BasicNameValuePair("customer", currentUser));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		register(nameValuePairs);
	}

	public void sendSuccess(){		
		SharedPreferences sharedPrefs;	     
		sharedPrefs = getApplicationContext().getSharedPreferences(MainActivity.APP_SHARED_PREFS, Context.MODE_PRIVATE);	   
		Editor editor;
		editor = sharedPrefs.edit();
		editor.putBoolean("userLoggedInState", true);
		editor.putString("currentLoggedInUser", currentUser);
		editor.commit();

		Intent successlogIntent = new Intent(this, MainActivity.class);
		startActivity(successlogIntent); 
		finish();
	}	



	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>	
	public class RegisterCustomerTask extends AsyncTask<ArrayList<NameValuePair>, Void, JSONArray>{ 

		protected void onPreExecute(){	
			findViewById(R.id.progress).setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONArray doInBackground(ArrayList<NameValuePair>... params) {
			for(ArrayList<NameValuePair> pairlist : params){//is length of one
				return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/register.php", pairlist);	
			}
			return null;			
		}

		protected void onPostExecute(JSONArray bg_result){
			findViewById(R.id.progress).setVisibility(View.GONE);
			try {
				//options for results (error, success, exists, params)
				TextView status = (TextView) findViewById(R.id.login_status);
				String result = (String) bg_result.get(0);

				if(result.equalsIgnoreCase("insert_success")){
					status.setText("insert_success");
					sendSuccess();
				}
				else if(result.equalsIgnoreCase("exists_success")){
					status.setText("login_success");
					sendSuccess();
				}
				else if(result.equalsIgnoreCase("exists")){
					status.setText(currentUser + " already exists");
				}
				else if(result.equalsIgnoreCase("params")){
					status.setText("please fill out all the feilds");
				}
				else if(result.equalsIgnoreCase("error")){
					status.setText("not sure what the fuck happened here");
				}
				else if(result.equalsIgnoreCase("exists_failure")){
					status.setText("wrong login / password");
				}else{
					status.setText(result);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}


}
