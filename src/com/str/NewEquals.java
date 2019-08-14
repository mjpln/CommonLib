package com.str;

public class NewEquals {

	public static Boolean equals(String a, String b){
		if(a==null && b==null)
			return true;
		if(a==null || b==null)
			return false;
		if(a.matches("[0-9\\.]+")){
			try{
				String[] aArrays = a.split("\\.", -1);
				String[] bArrays = b.split("\\.", -1);
				return aArrays[0].equals(bArrays[0]);
			}catch(Exception e){
				return false;
			}
		}
		return a.equals(b);
	}
	public static void main(String[] args) {
		System.out.println(NewEquals.equals("890.3", "890.4"));
		System.out.println(NewEquals.equals("890", "890.4"));
		System.out.println(NewEquals.equals("890.3", "890."));
		System.out.println(NewEquals.equals("89U.3", "89U."));
		System.out.println(NewEquals.equals("ABC", "ABC"));
	}
}
