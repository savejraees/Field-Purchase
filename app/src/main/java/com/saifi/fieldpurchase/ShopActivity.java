package com.saifi.fieldpurchase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.saifi.fieldpurchase.adapter.ModelAdapter;
import com.saifi.fieldpurchase.constants.SessonManager;
import com.saifi.fieldpurchase.constants.Url;
import com.saifi.fieldpurchase.model.BrandSpinner;
import com.saifi.fieldpurchase.model.Model_Model;
import com.saifi.fieldpurchase.model.SeriesModel;
import com.saifi.fieldpurchase.retrofitmodel.ShopModel;
import com.saifi.fieldpurchase.service.ApiInterface;
import com.saifi.fieldpurchase.util.NoScanResultException;
import com.saifi.fieldpurchase.util.ScanFragment;
import com.saifi.fieldpurchase.util.ScanResultReceiver;
import com.saifi.fieldpurchase.util.Views;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopActivity extends AppCompatActivity  implements
        AdapterView.OnItemSelectedListener, ScanResultReceiver {

    RadioGroup radioShop, radioWarranty;
    RadioButton radioAccesories;
    CheckBox chkExchange;
    EditText editTextExchange, edt_imei, edit_Brand, edt_model, edt_remarks, edt_actualPrice, edt_customer_aadhar;
    EditText editTextOrder, edt_gb, edt_purchase_amount, edt_customer_name, edt_customer_mobile, editTextOtp;
    TextInputLayout textInputExchange;
    LinearLayout layoutAccesother, layoutMobTab;
    Spinner Warranty_spinner, spinnerSeries, spinnerBrandMobile, condition_spinner;
    AutoCompleteTextView model_autocompleteTv;
    Button finalSubmitButton, otpButton, otpSubmitButton;
    ImageView imgBacktoMAin;
    String warrenty = "", warrenty_month = "", productCategory = "", conditon_Mobile = "";
    String brand_id = "", brandName = "", series_id = "", seriesName = "", idmodel = "", modelName = "";
    ModelAdapter modelAdapter;
    Views views = new Views();
    ImageView scanNow;
    TextView txtOtpContact, txtResend, txtName, txtContactShop, txtPhoneId;
    ArrayList<BrandSpinner> brand_list = new ArrayList<>();
    final ArrayList<String> brand_list_datamobile = new ArrayList();
    ArrayList<SeriesModel> series_list = new ArrayList<>();
    final ArrayList<String> series_list_dataSeries = new ArrayList();
    ArrayList<Model_Model> model_list = new ArrayList<>();
    String remark = "";

    String Warranty_data[] = {
            "0 - 1 month old",
            "1 - 3 month old",
            "3 - 6 month old",
            "6 - 9 month old",
            "9 - 12 month old",
    };

    String condition[] = {"Excellent", "Very Good", "Good", "Average"};
    String otp, mesage;

    SessonManager sessonManager;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        getSupportActionBar().hide();

        init();
        click();
    }

    private void click() {
        radioShop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = radioGroup.findViewById(i);
                boolean checked = checkedRadioButton.isChecked();
                if (checked) {
                    if (checkedRadioButton.getText().toString().equals("Mobile")) {
                        layoutMobTab.setVisibility(View.VISIBLE);
                        layoutAccesother.setVisibility(View.GONE);
                        productCategory = "Mobile";
                        hitSpinnerBrandMobile();
                    }
                    if (checkedRadioButton.getText().toString().equals("Tablet")) {
                        layoutMobTab.setVisibility(View.VISIBLE);
                        layoutAccesother.setVisibility(View.GONE);
                        productCategory = "Tablet";
                        hitSpinnerBrandMobile();

                    }
                    if (checkedRadioButton.getText().toString().equals("Accessories")) {
                        layoutMobTab.setVisibility(View.GONE);
                        layoutAccesother.setVisibility(View.VISIBLE);
                        productCategory = "Accessories";
                        brand_id = "";
                        brandName = "";
                        series_id = "";
                        seriesName = "";
                        modelName = "";
                        idmodel = "";
                    }
                }
            }
        });

        radioWarranty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = radioGroup.findViewById(i);
                boolean checked = checkedRadioButton.isChecked();
                if (checked) {
                    if (checkedRadioButton.getText().toString().equals("In")) {
                        Warranty_spinner.setVisibility(View.VISIBLE);

                        ArrayAdapter ab = new ArrayAdapter(ShopActivity.this, android.R.layout.simple_spinner_dropdown_item, Warranty_data);
                        Warranty_spinner.setAdapter(ab);
                        Warranty_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {

                                warrenty = "In";
                                warrenty_month = Warranty_data[position];
                                Log.d("fasfafas", warrenty_month);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    }
                    if (checkedRadioButton.getText().toString().equals("Out")) {
                        Warranty_spinner.setVisibility(View.GONE);
                        warrenty_month = "";
                        warrenty = "Out";
                    }

                }
            }
        });

        chkExchange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                b = chkExchange.isChecked();
                if (b) {
                    textInputExchange.setVisibility(View.VISIBLE);
                } else {
                    textInputExchange.setVisibility(View.GONE);
                    editTextExchange.setText("");
                }

            }
        });
        imgBacktoMAin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finishAffinity();
            }
        });


        finalSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(), ShopImageActivity.class));
                if (radioAccesories.isChecked()) {
                    brandName = edit_Brand.getText().toString();
                    modelName = edt_model.getText().toString();
                }


                if (editTextOrder.getText().toString().isEmpty()) {
                    editTextOrder.setError("Please enter Order no.");
                    editTextOrder.requestFocus();
                } else if (brandName.equals("") || brandName.isEmpty()) {
                    Toast.makeText(ShopActivity.this, "Please enter brand", Toast.LENGTH_SHORT).show();
                } else if (modelName.equals("") || modelName.isEmpty()) {
                    Toast.makeText(ShopActivity.this, "Please enter Model", Toast.LENGTH_SHORT).show();
                } else if (edt_gb.getText().toString().isEmpty()) {
                    edt_gb.setError("Please enter GB");
                    edt_gb.requestFocus();
                } else if (edt_imei.getText().toString().isEmpty()) {
                    edt_imei.setError("Please enter Imei no.");
                    edt_imei.requestFocus();
                } else if (edt_purchase_amount.getText().toString().isEmpty()) {
                    edt_purchase_amount.setError("Please enter Purchase Amount");
                    edt_purchase_amount.requestFocus();
                } else if (edt_customer_name.getText().toString().isEmpty()) {
                    edt_customer_name.setError("Please enter Customer Name");
                    edt_customer_name.requestFocus();
                } else if (edt_customer_mobile.getText().toString().isEmpty()) {
                    edt_customer_mobile.setError("Please enter Customer Mobile No.");
                    edt_customer_mobile.requestFocus();
                } else if (edt_customer_aadhar.getText().toString().isEmpty()) {
                    edt_customer_aadhar.setError("Please Enter Aadhar No.");
                    edt_customer_aadhar.requestFocus();
                } else if (edt_actualPrice.getText().toString().isEmpty()) {
                    edt_actualPrice.setError("Please Actual Price");
                    edt_actualPrice.requestFocus();
                } else {
                    hitFinalApi();
                }


            }
        });


        spinnerBrandMobile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //int position = adapterView.getSelectedItemPosition();
                brand_id = String.valueOf(brand_list.get(i).getBrandId());
                brandName = brand_list.get(i).getBrand_name();

                Log.d("fsdklj", brand_id + brandName);

                hitSpinnerSeries(brand_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerSeries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int position = adapterView.getSelectedItemPosition();
                series_id = String.valueOf(series_list.get(position).getSeries_id());
                seriesName = series_list.get(i).getSeries_name();
                hitModelApi(series_id);
                Log.d("sepo", seriesName + " " + series_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        model_autocompleteTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Model_Model modelObject = (Model_Model) parent.getItemAtPosition(position);
                modelName = modelObject.getModelName();
                idmodel = modelObject.getModel_id();
            }
        });

        condition_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                conditon_Mobile = condition[position];
                Log.d("sasfzczZ", conditon_Mobile);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScanFragment scanFragment = new ScanFragment();
                fragmentTransaction.add(R.id.scan_fragment, scanFragment);
                fragmentTransaction.commit();
            }
        });

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_customer_mobile.getText().toString().isEmpty()) {
                    edt_customer_mobile.setError("Please enter Customer Mobile No.");
                    edt_customer_mobile.requestFocus();
                } else if (edt_customer_mobile.getText().toString().length() != 10) {
                    edt_customer_mobile.setError("Mobile No. Should be 10 digits");
                    edt_customer_mobile.requestFocus();
                } else {
                    final Dialog dialog = new Dialog(ShopActivity.this);
                    dialog.setContentView(R.layout.otp_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    txtOtpContact = dialog.findViewById(R.id.txtContact);
                    txtResend = dialog.findViewById(R.id.txtResend);
                    editTextOtp = dialog.findViewById(R.id.editTextOtp);
                    otpSubmitButton = dialog.findViewById(R.id.otpSubmitButton);
                    dialog.show();

                    txtOtpContact.setText("+91" + edt_customer_mobile.getText().toString());

                    hitUrlForOtp();

                    txtResend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hitUrlForOtp();
                        }
                    });


                    otpSubmitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editTextOtp.getText().toString().isEmpty()) {
                                editTextOtp.setError("Please enter Otp");
                                editTextOtp.requestFocus();
                            } else if (!(editTextOtp.getText().toString().equals(otp))) {
                                editTextOtp.getText().clear();
                                editTextOtp.requestFocus();
                                Toast.makeText(ShopActivity.this, "Please enter Valid Otp", Toast.LENGTH_SHORT).show();

                            } else {
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }
        });
    }
    private void init() {
        radioShop = findViewById(R.id.radioShop);
        radioWarranty = findViewById(R.id.radioWarranty);
        layoutAccesother = findViewById(R.id.layoutAccesother);
        layoutMobTab = findViewById(R.id.layoutMobTab);
        Warranty_spinner = findViewById(R.id.Warranty_spinner);
        finalSubmitButton = findViewById(R.id.finalSubmitButton);
        otpButton = findViewById(R.id.otpButton);
        chkExchange = findViewById(R.id.chkExchange);
        textInputExchange = findViewById(R.id.textInputExchange);
        editTextExchange = findViewById(R.id.editTextExchange);
        editTextOrder = findViewById(R.id.editTextOrder);
        edt_gb = findViewById(R.id.edt_gb);
        edt_purchase_amount = findViewById(R.id.edt_purchase_amount);
        edt_customer_name = findViewById(R.id.edt_customer_name);
        edt_customer_mobile = findViewById(R.id.edt_customer_mobile);
        edt_remarks = findViewById(R.id.edt_remarks);
        edt_actualPrice = findViewById(R.id.edt_actualPrice);
        edt_customer_aadhar = findViewById(R.id.edt_customer_aadhar);
        edt_imei = findViewById(R.id.edt_imei);
        imgBacktoMAin = findViewById(R.id.imgBacktoMAin);
        spinnerBrandMobile = findViewById(R.id.spinnerBrandMobile);
        spinnerSeries = findViewById(R.id.spinnerSeries);
        radioAccesories = findViewById(R.id.radioAccesories);
        edit_Brand = findViewById(R.id.edit_Brand);
        edt_model = findViewById(R.id.edt_model);
        txtName = findViewById(R.id.txtNameShop);
        txtContactShop = findViewById(R.id.txtContactShop);
        scanNow = findViewById(R.id.scanNow);
        model_autocompleteTv = findViewById(R.id.model_autocompleteTv);
        model_autocompleteTv.setThreshold(1);

        sessonManager = new SessonManager(ShopActivity.this);
        userId = sessonManager.getToken();
        txtName.setText(sessonManager.getUserName());
        txtContactShop.setText("(" + sessonManager.getMobile() + ")");

        //////////////////// condtion spinner //////////////////
        condition_spinner = findViewById(R.id.condition_spinner);
        ArrayAdapter cndnAdapter = new ArrayAdapter(ShopActivity.this, android.R.layout.simple_spinner_dropdown_item, condition);
        condition_spinner.setAdapter(cndnAdapter);
    }

    //////////////////////////////// final Api /////////////////////////////////////////////////

    private void hitFinalApi() {
        views.showProgress(ShopActivity.this);

        if (edt_remarks.getText().toString().isEmpty()) {
            remark = "";
        } else {
            remark = edt_remarks.getText().toString();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Url.BASE_URL)
                .build();
        ApiInterface api = retrofit.create(ApiInterface.class);
        Call<ShopModel> call = api.hitFinalShop(Url.key, editTextOrder.getText().toString(), productCategory,
                edt_gb.getText().toString(), warrenty, warrenty_month, edt_imei.getText().toString(),
                edt_purchase_amount.getText().toString(), edt_customer_name.getText().toString(),
                edt_customer_mobile.getText().toString(), edt_customer_aadhar.getText().toString(),
                remark, edt_actualPrice.getText().toString(),
                brandName, seriesName, modelName, userId, conditon_Mobile, editTextExchange.getText().toString());


        call.enqueue(new Callback<ShopModel>() {
            @Override
            public void onResponse(Call<ShopModel> call, Response<ShopModel> response) {
                views.hideProgress();
                if (response.isSuccessful()) {
                    ShopModel model = response.body();
                    if (model.getCode().equals("200")) {
                        views.showToast(ShopActivity.this, model.getMsg());
                        startActivity(new Intent(ShopActivity.this, ShopImageActivity.class)
                                .putExtra("phoneId", model.getPhoneId()));
                    } else {
                        views.showToast(ShopActivity.this, model.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopModel> call, Throwable t) {
                views.hideProgress();
            }
        });
    }

    //////////////////////////////////////////Brand Spinner /////////////////////////////////////

    private void hitSpinnerBrandMobile() {
        views.showProgress(ShopActivity.this);

        RequestQueue requestQueueMobile = Volley.newRequestQueue(ShopActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, Url.getBrand, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Loginresponse", response);
                views.hideProgress();

                brand_list.clear();
                brand_list_datamobile.clear();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("code").equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            BrandSpinner brandSpinner = new BrandSpinner();
                            brandSpinner.setBrandId(jsonObject1.getInt("id"));
                            brandSpinner.setBrand_name(jsonObject1.getString("brand_name"));
                            brand_list.add(brandSpinner);
                            brand_list_datamobile.add(brand_list.get(i).getBrand_name());
                        }

                        Log.d("jhkljk", brand_list_datamobile.toString());

                        ArrayAdapter brandAdapter = new ArrayAdapter(ShopActivity.this, android.R.layout.simple_spinner_dropdown_item, brand_list_datamobile);
                        spinnerBrandMobile.setAdapter(brandAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                views.hideProgress();

                Toast.makeText(ShopActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                Log.d("errodfr", error.getMessage() + "errorr");

            }
        }) {
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("key", Url.key);
                hashMap.put("type", "Mobile");
                return hashMap;
            }
        };
        requestQueueMobile.getCache().clear();
        requestQueueMobile.add(request);
    }

    ////////////////////////////////////////// Series Spinner //////////////////////////////////

    private void hitSpinnerSeries(final String brand) {

        views.showProgress(ShopActivity.this);
        series_list_dataSeries.clear();
        series_list.clear();

        RequestQueue requestQueueMobile = Volley.newRequestQueue(ShopActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, Url.getseries, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Logdfgonse", response);
                views.hideProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("code").equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            SeriesModel seriesModel = new SeriesModel();
                            seriesModel.setSeries_id(jsonObject1.getString("id"));
                            seriesModel.setSeries_name(jsonObject1.getString("series_name"));
                            series_list.add(seriesModel);
                            series_list_dataSeries.add(series_list.get(i).getSeries_name());
                        }

                        Log.d("jhkljasdk", series_list_dataSeries.toString());

                        ArrayAdapter brandAdapter = new ArrayAdapter(ShopActivity.this, android.R.layout.simple_spinner_dropdown_item, series_list_dataSeries);
                        spinnerSeries.setAdapter(brandAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                views.hideProgress();
                Toast.makeText(ShopActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                Log.d("errodfr", error.getMessage() + "errorr");

            }
        }) {
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("key", Url.key);
                hashMap.put("brand_id", brand);
                Log.d("brandidd", brand_id);

                return hashMap;

            }

        };
        requestQueueMobile.getCache().clear();
        requestQueueMobile.add(request);
    }

    /////////////////////////////////////////// Model Spinner //////////////////////////////////

    void hitModelApi(final String series) {
        views.showProgress(ShopActivity.this);

        RequestQueue requestQueue = Volley.newRequestQueue(ShopActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, Url.getModel, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response_model", response);
                views.hideProgress();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("code").equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        // model_list_dataModel.clear();
                        model_list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Model_Model model = new Model_Model();
                            model.setModel_id(jsonObject1.getString("id"));
                            model.setModelName(jsonObject1.getString("model_name"));
                            model_list.add(model);


                        }

                        modelAdapter = new ModelAdapter(getApplicationContext(), R.layout.activity_shop,
                                R.id.lbl_name, model_list);
                        model_autocompleteTv.setAdapter(modelAdapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                views.hideProgress();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("key", Url.key);
                hashMap.put("series_id", series);
                return hashMap;
            }
        };
        requestQueue.getCache().clear();
        requestQueue.add(request);


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void scanResultData(String codeFormat, String codeContent) {
        edt_imei.setText(codeContent);
    }

    @Override
    public void scanResultData(NoScanResultException noScanData) {

    }

    private void hitUrlForOtp() {
        otp = String.valueOf(randomOtp());
        mesage = "Your verification code is :" + otp;
        Log.d("msg", mesage);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url.SENDOTP, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("chekclohin", response);

                Toast.makeText(getApplicationContext(), "OTP sent your mobile number", Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("authkey", "290873ACnsgu9J5d5fd88f");
                hashMap.put("message", mesage);
                hashMap.put("country", "91");
                hashMap.put("route", "106");
                hashMap.put("sender", "MSGOTP");
                hashMap.put("mobiles", edt_customer_mobile.getText().toString());
                Log.d("checkparams", hashMap.toString());
                return hashMap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private int randomOtp() {
        Random rnd = new Random();
        int n = 1000 + rnd.nextInt(9000);
        return n;
    }

}
