package com.example.couponon;

import java.text.DecimalFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CouponAdapter extends ArrayAdapter<Coupon>{

	List<Coupon> items;
	public CouponAdapter(Context context, int resource, List<Coupon> objects) {
		super(context, resource, objects);
		this.items = objects;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final Coupon curr = items.get(position);
	    if (v == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.coupon_item, null);
	    }
	    if (curr != null){
	    	final TextView productNameTextView = (TextView) v.findViewById(R.id.productNameTextView);	    	
	        final TextView productPriceTextView = (TextView) v.findViewById(R.id.productPriceTextView);
	        final TextView savingsTextView = (TextView) v.findViewById(R.id.savingsTextView);
	        productNameTextView.setText("Product: "+ curr.getProduct().getName());
	        DecimalFormat df = new DecimalFormat("#.##");
	        productPriceTextView.setText("Initial Price: $"+df.format(curr.getProduct().getPrice())+"Final Price: $"+ df.format(curr.getFinalPrice()));
	        savingsTextView.setText("Savings: $"+df.format(curr.getSavings())+" 	or	"+df.format(curr.getDiscount() * 100)+"%");
	        
	    }
	    
	    v.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				final String[] listTypes = AllMyLists.getListArray(AllMyLists.COUPON);
				AlertDialog dialog = new AlertDialog.Builder(getContext())
				.setItems(listTypes,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						int list_id = AllMyLists.findIdByName(AllMyLists.COUPON, listTypes[which]);
						Intent addCouponToProductList = new Intent(getContext(), CouponListActivity.class);
						addCouponToProductList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						addCouponToProductList.putExtra("item_id", curr.getId());
						addCouponToProductList.putExtra("list_id", list_id);
						getContext().startActivity(addCouponToProductList);
						
					}}).create();

				dialog.show();		
				
			}
	    });
	    
	    
	    return v;
    }
	
}
