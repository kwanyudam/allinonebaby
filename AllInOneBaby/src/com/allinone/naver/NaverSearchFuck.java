package com.allinone.naver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.allinone.activity.MyContact;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class NaverSearchFuck extends AsyncTask<String, Void, String>{

	String searchStr, phpStr;
	Handler hndler;
	InputStream inputStream = null;
	String result;
	ArrayList<MyContact> list;
	
	public NaverSearchFuck(String searchStr, Handler hndler, ArrayList<MyContact> list){
		this.searchStr = searchStr;
		this.hndler = hndler;
		this.list = list;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		searchStr = URLEncoder.encode(searchStr);
		phpStr = "http://openapi.naver.com/search?key=1c3d1c202d50142aa261296ec46bab1b&query="+searchStr+"&target=local&start=1&display=5";
		
		
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

        try {
            // Set up HTTP post

            // HttpClient is more then less deprecated. Need to change to URLConnection
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(phpStr);
            httpPost.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // Read content & Log
            inputStream = httpEntity.getContent();
        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncodingException", e1.toString());
            e1.printStackTrace();
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        
        // Convert response to string using String Builder
        
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            result = sBuilder.toString();
        } catch (Exception e) {
            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
        }
        
        analyzeNumber();
		
		return null;
	}
	
	private void analyzeNumber(){ 
		int cursor=0;
		int endcursor=0;
		String name, add, number;
	    //Log.i("result : " + result.length(), result);
		while(cursor<result.length()){
			Log.i("start ", "curr cursor : " +cursor);
			cursor=skipStr("<item>", cursor);
			Log.i("find <item> ", "curr cursor : " +cursor);
			cursor=skipStr("<title>", cursor);
			Log.i("find <title> ", "curr cursor : " +cursor);
			endcursor = findStr("</title>", cursor);
			Log.i("find </title> ", "curr cursor : " +cursor);
			if(cursor>=result.length())
				break;
			name = result.substring(cursor, endcursor);
			
			cursor = skipStr("<telephone>", cursor);
			Log.i("find telephone", "curr cursor : " +cursor);
			endcursor = findStr("</telephone>", cursor);
			
			if(cursor>=result.length())
				break;
			number = result.substring(cursor, endcursor);
			name = cutString(name);
			list.add(new MyContact(name, number));
			cursor = skipStr("</item>", cursor);
			
			Log.i("ÀúÀå ! Name : " + name, "Number : " + number);
		}
	}
	private String cutString(String a){
		
		a=a.replace("&lt;b&gt;", " ");
		a=a.replace("&lt;/b&gt;", " ");
		return a;
	}
	private int skipStr(String a, int b){
		b = findStr(a, b);
		return b+a.length();
	}
	private int findStr(String a, int b){
		if(b>result.length()-a.length())
			return result.length();
		while(b<result.length()-a.length()){
//			Log.i("substring : "+b , result.substring(b, b+a.length()));
			if(result.substring(b, b+a.length()).equals(a))
				return b;
			b++;
		}
		return b;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		hndler.sendEmptyMessage(0);
		super.onPostExecute(result);
	}
	
}
