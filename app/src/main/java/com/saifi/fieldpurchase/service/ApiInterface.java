package com.saifi.fieldpurchase.service;

import com.google.gson.JsonObject;
import com.saifi.fieldpurchase.retrofitmodel.LoginModel;
import com.saifi.fieldpurchase.retrofitmodel.ShopModel;
import com.saifi.fieldpurchase.retrofitmodel.StatusModel;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("purchase_login_new")
    Call<LoginModel> hitLogin(@Field("key") String key, @Field("mobile") String mobile, @Field("password") String password,
                              @Field("role") String role,@Field("user_role") String user_role);

    @FormUrlEncoded
    @POST("check_active_user")
    Call<StatusModel> hitStatusApi(@Field("key") String key, @Field("user_id") String id);

    @FormUrlEncoded
    @POST("purchase_booking_new")
    Call<ShopModel> hitFinalShop(@Field("key") String key, @Field("order_no") String order, @Field("product_category") String product,
                                 @Field("storage") String gb, @Field("warrenty") String warranty,
                                 @Field("warrenty_month") String warrantyMonth, @Field("imei_no") String imei,
                                 @Field("purchase_amount") String amount, @Field("customer_name") String name,
                                 @Field("customer_mobile") String mobile, @Field("customer_aadhar") String aadhar,
                                 @Field("remark") String remark, @Field("app_price") String app_Price,
                                 @Field("brand_name") String brand, @Field("series_name") String series,
                                 @Field("model_name") String model, @Field("userid") String userId,
                                 @Field("condition") String condition, @Field("exchange") String exchange,
                                 @Field("purchase_cat_name") String cat_name, @Field("business_location_id") String buisnessID,
                                 @Field("barcode_scan") String barcode);
    @Multipart
    @POST("shop_purchase_uplode_image")
    Call<JsonObject> imageAPi(@Part MultipartBody.Part[] imageArray1, @PartMap() Map<String, RequestBody> partMap);

}
