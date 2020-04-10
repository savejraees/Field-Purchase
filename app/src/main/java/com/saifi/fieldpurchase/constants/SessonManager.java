package com.saifi.fieldpurchase.constants;

import android.content.Context;
import android.content.SharedPreferences;

public class SessonManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String token = "Token";
    public static  final String mobile = "mobile";
    public static  final String userName = "Name";
    public static  final String userType = "User";

    public SessonManager(Context context){
     sharedPreferences = context.getSharedPreferences("sharedPrefrence",Context.MODE_PRIVATE);
      editor = sharedPreferences.edit();
    }

 public String getToken(){
        return sharedPreferences.getString(token,"");
 }

public void setToken(String tkn){
        editor.putString(token,tkn);
        editor.commit();
}

public String getMobile(){
        return sharedPreferences.getString(mobile,"");
}
public void setMobile(String mbl){
        editor.putString(mobile,mbl);
        editor.commit();
}

public String getUserName(){
       return sharedPreferences.getString(userName,"");
}

public void setUserName(String nam){
        editor.putString(userName,nam);
        editor.commit();
}

public String getUserType(){
        return sharedPreferences.getString(userType,"");
}

public void setUserType(String type){
        editor.putString(userType,type);
        editor.commit();
}
}
