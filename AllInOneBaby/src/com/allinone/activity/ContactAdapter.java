package com.allinone.activity;

import java.util.ArrayList;

import com.allinone.naver.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<MyContact>{

	ArrayList<MyContact> list;
	Context context;
	
	public ContactAdapter(Context context, int resource, ArrayList<MyContact> list) {
		super(context, resource);
		// TODO Auto-generated constructor stub
		this.context =context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}
	@Override
	public MyContact getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if(v==null){
			LayoutInflater lv;
			lv = LayoutInflater.from(context);
			v = lv.inflate(R.layout.list_contact, null); 
		}
		TextView t1, t2;
		Button b1;
		
		t1 = (TextView) v.findViewById(R.id.contact_name);
		t2 = (TextView) v.findViewById(R.id.contact_number);
		t1.setTextColor(Color.WHITE);
		t2.setTextColor(Color.WHITE);
		b1 = (Button) v.findViewById(R.id.contact_callbtn);
		b1.setBackgroundColor(Color.rgb(61,  90,  114));
		if(list.get(position).getName()!= null && list.get(position).getNumber() != null){
			//Log.i(list.get(position).getNumber(), "aweg");
			t1.setText(list.get(position).getName());
			t2.setText(list.get(position).getNumber());
		}
		
		b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i;
				i= new Intent("android.intent.action.CALL", Uri.parse("tel:"+list.get(position).getNumber()));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		});
		
		return v;
	}
}
