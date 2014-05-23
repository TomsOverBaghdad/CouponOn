package com.example.couponon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONfunctions {

	// public static JSONObject getJSONfromURL(String url) {
	//namevaluepairs is null if its a select, else it has information for insert or delete query
	public static JSONArray getJSONfromURL(String url, ArrayList<NameValuePair> nameValuePairs) {
		InputStream is = null;
		String result = "";
		JSONArray jArray = null;
		String LOG = "BUTT";

		//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();//http://androidprogramz.blogspot.com/2012/07/connect-mysql-database-from-android.html
		// nameValuePairs.add(new BasicNameValuePair("year","1980"));////http://www.helloandroid.com/tutorials/connecting-mysql-database

		// Download JSON data from URL
		try {
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			if(nameValuePairs != null){
			  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));//http://androidprogramz.blogspot.com/2012/07/connect-mysql-database-from-android.html
			  Log.e(LOG, nameValuePairs.toString());
			}
			Log.e(LOG, "pre url: " + url);
			HttpResponse response = httpclient.execute(httppost);
			Log.e(LOG, "post");
			HttpEntity entity = response.getEntity();
			Log.e(LOG, "got entity");
			is = entity.getContent();
			Log.e(LOG, "got content");

		} catch (Exception e) {
			Log.e(LOG, "Error in http connection " + e.toString());
		}

		// Convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			Log.e(LOG, "in reader");
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.e(LOG, "result \n"+result +" end result");
		} catch (Exception e) {
			Log.e(LOG, "Error converting result " + e.toString());
		}

		try {
			Log.e(LOG, "converting result to JSONArray");
			jArray = new JSONArray(result);
		} catch (JSONException e) {
			Log.e(LOG, "Error parsing data " + e.toString());			
			return new JSONArray();
		}

		return jArray;//.toString();
	}
}
