package com.example.couponon;

import java.text.DecimalFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductAdapter extends ArrayAdapter<Product>{

	List<Product> items;
	public ProductAdapter(Context context, int resource, List<Product> objects) {
		super(context, resource, objects);

		this.items = objects;
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final Product curr = items.get(position);
	    if (v == null) {
	        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.coupon_item, null);
	    }
	    if (curr != null){
	    	final TextView productNameTextView = (TextView) v.findViewById(R.id.productNameTextView);	    	
	        final TextView productPriceTextView = (TextView) v.findViewById(R.id.productPriceTextView);
	        final TextView itemID = (TextView) v.findViewById(R.id.item_id);
	        itemID.setVisibility(View.GONE);
	        productNameTextView.setText("Product: "+ curr.getName());
	        DecimalFormat df = new DecimalFormat("#.##");
	        productPriceTextView.setText("Price: $"+ df.format(curr.getPrice()));
	        
	        v.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
				final String[] listTypes = AllMyLists.getListArray(AllMyLists.PRODUCT);
				AlertDialog dialog = new AlertDialog.Builder(getContext())
				.setItems(listTypes,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						int list_id = AllMyLists.findIdByName(AllMyLists.PRODUCT, listTypes[which]);
						Intent addCouponToProductList = new Intent(getContext(), ProductListActivity.class);
						addCouponToProductList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						addCouponToProductList.putExtra("item_id", curr.getId());
						addCouponToProductList.putExtra("list_id", list_id);
						getContext().startActivity(addCouponToProductList);

						
						
					}}).create();

				dialog.show();					
				}	    	
		    });
	    }
	    return v;
    }
	
}
