package com.example.couponon;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<MyList> {
	private final String LOG = "com.example.couponon.MYLISTADAPTER";
	private List<MyList> items;
	private final boolean isCouponList;
	private final int RESOURCE;
	
	public MyListAdapter(Context context, int resource, List<MyList> mylists, boolean isCouponList) {
		super(context, resource, mylists);
		this.items = mylists;
		this.isCouponList = isCouponList;
		RESOURCE = resource;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final MyList curr = items.get(position);
	    if (v == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(RESOURCE, null);
	    }
	    if (curr != null){
	    	final TextView textView1 = (TextView) v.findViewById(R.id.textView1);
	    	final TextView id = (TextView) v.findViewById(R.id.customer_list_item_id);
	    	Log.e(LOG, " my list adapter, curr list id  -> " + curr.getId());
	    	id.setText(Integer.toString(curr.getId()));	    	
	    	//CharSequence cs = ;
	    	textView1.setText(curr.getName());
	        final Button deleteList = (Button) v.findViewById(R.id.button1);
	        deleteList.setText("delete?");
	        deleteList.setOnClickListener(new OnClickListener() {
	        	MyList bloob = curr;
				@Override
				public void onClick(View self) {
					//String list_id = ((TextView) self.findViewById(R.id.customer_list_item_id)).getText().toString();
					//items.remove(bloob);
					DeleteListTask deleteListTask = new DeleteListTask();
					deleteListTask.execute(bloob);//still not sure if i can just use the curr item
					//notifyDataSetChanged();
				}               
            });
	        
	        v.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent showList;
					if(isCouponList){
						showList = new Intent(getContext(), CouponListActivity.class);
					}
					else{
						showList = new Intent(getContext(), ProductListActivity.class);
						showList.putExtra("name", ((TextView) v.findViewById(R.id.textView1)).getText().toString());
					}
					int list_id = Integer.parseInt(((TextView) v.findViewById(R.id.customer_list_item_id)).getText().toString());
					Log.e(LOG, " my list adapter, ON CLICK list_id -> " + list_id);
					showList.putExtra("list_id", list_id);					
					getContext().startActivity(showList);					
				}	    	
		    });
	    }
	   
	    return v;
    }

	//<INPUT PARAMS, PROGRESS(for progressbar), RETURNED PARAMS(to be evaluated once connections are done)>
		private class DeleteListTask extends AsyncTask<MyList, Void, MyList>{

			@Override
			protected MyList doInBackground(MyList... params) {
				Log.e(LOG, "ID TO BE DELETED => "+ params[0].getId());
				ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
				parameters.add(new BasicNameValuePair("customer", MainActivity.CURRENT_USER));				
				
				if(isCouponList)
					parameters.add(new BasicNameValuePair("list_option", "coupon"));
				else
					parameters.add(new BasicNameValuePair("list_option", "product"));
				parameters.add(new BasicNameValuePair("list_id", String.valueOf(params[0].getId())));
			
				JSONfunctions.getJSONfromURL("http://www.couponon.uphero.com/delete_list.php",parameters);
				
				return params[0];
			}
			
			protected void onPostExecute(MyList item){
				items.remove(item);
				notifyDataSetChanged();
			} 

		}
	
}
