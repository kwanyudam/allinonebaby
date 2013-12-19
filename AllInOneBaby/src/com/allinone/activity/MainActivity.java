package com.allinone.activity;

import java.util.ArrayList;

import com.allinone.naver.NaverSearchFuck;
import com.allinone.naver.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ArrayList<MyContact> list, showlist;
	ListView lv;
	EditText searchEdit;
	ContactAdapter contactAdapter;
	Button searchbtn;
	Button dialbtn, addbtn;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		lv = (ListView)findViewById(R.id.main_list);
		searchbtn = new Button(getApplicationContext());
		dialbtn = (Button)findViewById(R.id.main_dialbtn);
		addbtn = (Button)findViewById(R.id.main_addbtn);
		searchbtn.setText("추가검색!");
		lv.addFooterView(searchbtn);
		searchEdit = (EditText)findViewById(R.id.main_search);
		list = new ArrayList<MyContact>();
		showlist = new ArrayList<MyContact>();
		getNumber();
		contactAdapter = new ContactAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, showlist);
		lv.setAdapter(contactAdapter);
		
		searchbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "추가 검색을 실행합니다!", Toast.LENGTH_LONG).show();
				doNaverSearch();
			}
		});
		dialbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:"));
				startActivity(intent);
			}
		});
		addbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.setContentView(R.layout.dialog_add);
				dialog.setTitle("연락처 추가");
				Button okbtn, cancelbtn;
				final EditText nametext;
				final EditText numbertext;
				nametext = (EditText) dialog.findViewById(R.id.adddialog_name);
				numbertext = (EditText) dialog.findViewById(R.id.adddialog_number);
				okbtn = (Button) dialog.findViewById(R.id.adddialog_ok);
				cancelbtn = (Button) dialog.findViewById(R.id.adddialog_cancel);
				
				okbtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "번호가 추가되었습니다", Toast.LENGTH_LONG).show();
						addContact(nametext.getText()+"", numbertext.getText()+"");
						dialog.dismiss();
					}
				});
				cancelbtn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				
				dialog.show();
			}
		});
		searchEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if("".equals(s)){
					showlist.clear();
					for(int i=0;i<list.size();i++){
						showlist.add(list.get(i));
					}
				}else{
					showlist.clear();
					for(int i=0;i<list.size();i++){
						if(SoundSearcher.matchString(list.get(i).getName(), searchEdit.getText()+"") || SoundSearcher.matchString(list.get(i).getNumber(), searchEdit.getText()+"")){
							showlist.add(list.get(i));
						}
					}
				}
				contactAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void addContact(String name, String number){
		Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT);
		intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
		 
		// 번호
		intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
		intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
		intent.setData(Uri.fromParts("tel", number, null));
		
		startActivity(intent);
	}
	private void doNaverSearch(){
		FantasticHandler hndler = new FantasticHandler();
		
		NaverSearchFuck nsf = new NaverSearchFuck(searchEdit.getText()+"", hndler, showlist);
		
		nsf.execute();
		
		//hndler.sendEmptyMessageDelayed(0, 2000);
		
	}
	
	private class FantasticHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//pd.dismiss();
			Toast.makeText(getApplicationContext(), "검색이 완료되었습니다", Toast.LENGTH_LONG).show();
			contactAdapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	}
	
	void getNumber(){
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID -> 사진 정보 가져오는데 사용
				ContactsContract.CommonDataKinds.Phone.NUMBER,        // 연락처
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }; // 연락처 이름.

		String[] selectionArgs = null;

		String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor contactCursor = managedQuery(uri, projection, null,
				selectionArgs, sortOrder);
		
		if (contactCursor.moveToFirst()) {
			do {
				String phonenumber = contactCursor.getString(1).replaceAll("-",
						"");
				if (phonenumber.length() == 10) {
					phonenumber = phonenumber.substring(0, 3) + "-"
							+ phonenumber.substring(3, 6) + "-"
							+ phonenumber.substring(6);
				} else if (phonenumber.length() > 8) {
					phonenumber = phonenumber.substring(0, 3) + "-"
							+ phonenumber.substring(3, 7) + "-"
							+ phonenumber.substring(7);
				}
				
				MyContact con;
				con = new MyContact(contactCursor.getString(2),phonenumber);
				list.add(con);
			}while (contactCursor.moveToNext());
		}
		
/*		list.add(new MyContact("내가 추가한 경찰서", "02-114"));
		list.add(new MyContact("내가 추가한 소방서", "02-119"));
		list.add(new MyContact("내가 추가한 번호", "010-1234-5678"));*/
		for(int i=0;i<list.size();i++){
			showlist.add(list.get(i));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}