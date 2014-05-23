package com.example.couponon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


/**
 * Similar to the ALlProducts class, this class stores all the names and ids of the coupon lists that the user has and the product lists
 * again, this is so i dont store information on the phone, this may change in future versions
 * */
public class AllMyLists {
	public final static String LOG = "com.example.couponon.ALLMYLISTS";
	public static List<MyList> listprodlist = new ArrayList<MyList>(), listcouplist = new ArrayList<MyList>();
	public static boolean P_LOADED = false, C_LOADED = false;
	private static ListLoader loaderTask = new ListLoader();
	public static final int PRODUCT = 0, COUPON = 1;
	public static void loadpLists(){
		loaderTask.execute(false);		
	}
	public static void loadcLists(){
		loaderTask.execute(false);		
	}
	public static int findIdByName(int choice, String name) {
		// TODO Auto-generated method stub
		int toreturn = Integer.MIN_VALUE;//= new ArrayList<String>();
		List<MyList> list = new ArrayList<MyList>();
		if(choice == PRODUCT)
			list = listprodlist;
		else if (choice == COUPON)
			list = listcouplist;
		for(MyList m : list ){
			if(m.getName().equals(name))
				return m.getId();
		}
		return toreturn ;
	}
	
	public static String[] getListArray(int choice){
		ArrayList<String> toreturn = new ArrayList<String>();
		List<MyList> list = new ArrayList<MyList>();
		if(choice == PRODUCT)
			list = listprodlist;
		else if (choice == COUPON)
			list = listcouplist;
		for(MyList m : list ){
			toreturn.add(m.getName());
		}
		return (String[]) toreturn.toArray();
	}
	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
	private static class ListLoader extends AsyncTask<Boolean, Void, JSONArray>{

		private boolean isCoup = true;
		
		protected void onPreExecute(){
			Log.e(LOG, " PRE EXECUTE LOAD LIST");
		}

		@Override
		protected JSONArray doInBackground(Boolean... isCoupon) {
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));
			if(isCoupon[0]){//get coponlist
				parameters.add(new BasicNameValuePair("list_option", "coupon"));
			}else{
				isCoup = false;
				parameters.add(new BasicNameValuePair("list_option", "product"));
			}
			return JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/get_lists.php",parameters);
		}

		protected void onPostExecute(JSONArray lists){

			Log.e(LOG, " POST EXECUTE LOAD LISTS");
			try {			
				for(int index = 0; index < lists.length(); index++){
					JSONObject jObject = (JSONObject) lists.get(index);
					Log.e(LOG, " OBJECT NAME: "+ jObject.getString("name"));
					if(!isCoup)
						listprodlist.add(new MyList(jObject.getString("name"), jObject.getInt("list_id")));
					else
						listcouplist.add(new MyList(jObject.getString("name"), jObject.getInt("list_id")));
				}
				if(!isCoup)
					P_LOADED = true;
				else
					C_LOADED = true;
			}catch (JSONException e) {
				Log.e(LOG, "-something wrong with the JSON objects-");
				e.printStackTrace();
			}			
		}
	}

}
