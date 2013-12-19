package com.allinone.activity;

public class MyContact{
	private String number;
	private String name;
	private String address;
	public MyContact(String name, String number){
		this.name = name;
		this.number = number;
		this.address = "";
	}
	public MyContact(String name, String number, String address){
		this(name, number);
		this.address = address;
	}
	public String getName(){
		return name;
	}
	public String getNumber(){
		return number;
	}
}