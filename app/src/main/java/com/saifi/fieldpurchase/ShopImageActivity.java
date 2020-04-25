package com.saifi.fieldpurchase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saifi.fieldpurchase.adapter.MobileImageAdapter;
import com.saifi.fieldpurchase.constants.Url;
import com.saifi.fieldpurchase.model.ImageModel;
import com.saifi.fieldpurchase.service.ApiInterface;
import com.saifi.fieldpurchase.util.ApiFactory;
import com.saifi.fieldpurchase.util.Helper;
import com.saifi.fieldpurchase.util.NoScanResultException;
import com.saifi.fieldpurchase.util.ScanFragment;
import com.saifi.fieldpurchase.util.ScanResultReceiver;
import com.saifi.fieldpurchase.util.Views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopImageActivity extends AppCompatActivity implements ScanResultReceiver {

    String phoneId = "",imageCAtegory="";;
    Button btnBarCodeScan, selectInVoiceButton, selectMobileButton, selectCustomerButton;
    Button uploadInVoiceButton, uploadMobileButton, uploadCustomerButton,finalImageButton;
    TextView txtBarcodeId;
    LinearLayout linearLayout;
    int PICK_IMAGE_MULTIPLE = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    int count;
    File photoInvoiceFile = null;
    File photoMobileFile = null;
    File photoCustomerFile = null;
    Uri photoUriInvoice,photoUriMobile,photoUriCustomer;
    String mCurrentPhotoPathInvoice,mCurrentPhotoPathMobile,mCurrentPhotoPathCustomer;
    private String photoPathMobile, photoPathCustomer, photoPathInVoice;
    ArrayList<String> imagePathListInvoice = new ArrayList<>();
    ArrayList<String> imagePathListMobile = new ArrayList<>();
    ArrayList<String> imagePathListCustomer = new ArrayList<>();
    ArrayList<ImageModel> Invoicelist = new ArrayList<>();
    ArrayList<ImageModel> Mobilelist = new ArrayList<>();
    ArrayList<ImageModel> Customerlist = new ArrayList<>();
    String imageEncodedInvoice,imageEncodedMobile,imageEncodedCustomer;
    RecyclerView rv_Invoice,rv_Mobile,rv_Customer;
    public MobileImageAdapter mIMGAdapter;
    Bitmap bitmapInVoice,bitmapMobile,bitmapCustomer;
    Views views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_image);
        requestStoragePermission();
        requestMultiplePermissions();

        phoneId = getIntent().getStringExtra("phoneId");

        views = new Views();
        btnBarCodeScan = findViewById(R.id.btnBarCodeScan);
        txtBarcodeId = findViewById(R.id.txtBArcodeId);
        linearLayout = findViewById(R.id.linearLayout);
        rv_Invoice = findViewById(R.id.rv_Invoice);
        rv_Mobile = findViewById(R.id.rv_Mobile);
        rv_Customer = findViewById(R.id.rv_Customer);

        selectInVoiceButton = findViewById(R.id.selectInVoiceButton);
        selectMobileButton = findViewById(R.id.selectMobileButton);
        selectCustomerButton = findViewById(R.id.selectCustomerButton);
        uploadMobileButton = findViewById(R.id.uploadMobileButton);
        uploadCustomerButton = findViewById(R.id.uploadCustomerButton);
        uploadInVoiceButton = findViewById(R.id.uploadInVoiceButton);
        finalImageButton = findViewById(R.id.finalImageButton);

        btnBarCodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScanFragment scanFragment = new ScanFragment();
                fragmentTransaction.add(R.id.linearLayout, scanFragment);
                fragmentTransaction.commit();
            }
        });

        selectClick();
        upLoadClick();
        finalImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopImageActivity.this,MainActivity.class));
                Toast.makeText(ShopImageActivity.this, "Ok Done!", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        });

    }

    private void selectClick() {
        selectInVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtBarcodeId.getText().toString().equals("0")) {
                    Toast.makeText(ShopImageActivity.this, "Please Scan BarCode", Toast.LENGTH_SHORT).show();
                } else {
                    count = 1;
                    imageCAtegory = "invoice";
                    startDialogInvoice();
                }

            }
        });

        selectMobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtBarcodeId.getText().toString().equals("0")) {
                    Toast.makeText(ShopImageActivity.this, "Please Scan BarCode", Toast.LENGTH_SHORT).show();
                } else {
                    count = 2;
                    imageCAtegory = "mobile";
                    startDialogMobile();
                }

            }
        });

        selectCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtBarcodeId.getText().toString().equals("0")) {
                    Toast.makeText(ShopImageActivity.this, "Please Scan BarCode", Toast.LENGTH_SHORT).show();
                } else {
                    count = 3;
                    imageCAtegory = "customer";
                    startDialogCustomer();
                }

            }
        });
    }

    private void upLoadClick() {
        uploadInVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitApiUploadInvoice();
            }
        });

        uploadMobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitApiUploadMobile();
            }
        });

        uploadCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitApiUploadCustomer();
            }
        });
    }

    private void hitApiUploadInvoice() {

        views.showProgress(ShopImageActivity.this);
        HashMap<String, RequestBody> partMap = new HashMap<>();
        partMap.put("key", ApiFactory.getRequestBodyFromString(Url.key));
        partMap.put("shop_purchase_id", ApiFactory.getRequestBodyFromString(phoneId));
        partMap.put("barcode_scan", ApiFactory.getRequestBodyFromString(txtBarcodeId.getText().toString()));
        partMap.put("image_category", ApiFactory.getRequestBodyFromString(imageCAtegory));

        MultipartBody.Part[] imageArrayInvoice = new MultipartBody.Part[imagePathListInvoice.size()];

        for (int i = 0; i < imageArrayInvoice.length; i++) {
            File file = new File(imagePathListInvoice.get(i));
            try {
                File compressedfile = new Compressor(ShopImageActivity.this).compressToFile(file);
                RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                imageArrayInvoice[i] = MultipartBody.Part.createFormData("image[]", compressedfile.getName(), requestBodyArray);

                Log.d("astysawe", String.valueOf(imageArrayInvoice[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ApiInterface iApiServices = ApiFactory.createRetrofitInstance(Url.BASE_URL).create(ApiInterface.class);
        iApiServices.imageAPi(imageArrayInvoice, partMap)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        //Toast.makeText(ClaimAssistanceActivity_form.this, "" + response, Toast.LENGTH_SHORT).show();
                        views.hideProgress();

                        Log.d("resposed_data", response.toString());
                        try {

                            JsonObject jsonObject = response.body();

                            if (jsonObject.get("code").getAsString().equals("200")) {
                                views.showToast(ShopImageActivity.this, jsonObject.get("msg").getAsString());
                            } else {
                                views.showToast(ShopImageActivity.this, jsonObject.get("msg").getAsString());
                            }
                        } catch (Exception e) {

                            Log.d("error_message", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                        Toast.makeText(ShopImageActivity.this, "fail" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        views.hideProgress();
                        Log.d("data_error", t.getMessage());

                    }
                });

    }
    private void hitApiUploadMobile() {

        views.showProgress(ShopImageActivity.this);
        HashMap<String, RequestBody> partMap = new HashMap<>();
        partMap.put("key", ApiFactory.getRequestBodyFromString(Url.key));
        partMap.put("shop_purchase_id", ApiFactory.getRequestBodyFromString(phoneId));
        partMap.put("barcode_scan", ApiFactory.getRequestBodyFromString(txtBarcodeId.getText().toString()));
        partMap.put("image_category", ApiFactory.getRequestBodyFromString(imageCAtegory));

        MultipartBody.Part[] imageArrayInvoice = new MultipartBody.Part[imagePathListMobile.size()];

        for (int i = 0; i < imageArrayInvoice.length; i++) {
            File file = new File(imagePathListMobile.get(i));
            try {
                File compressedfile = new Compressor(ShopImageActivity.this).compressToFile(file);
                RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                imageArrayInvoice[i] = MultipartBody.Part.createFormData("image[]", compressedfile.getName(), requestBodyArray);

                Log.d("astysawe", String.valueOf(imageArrayInvoice[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ApiInterface iApiServices = ApiFactory.createRetrofitInstance(Url.BASE_URL).create(ApiInterface.class);
        iApiServices.imageAPi(imageArrayInvoice, partMap)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        //Toast.makeText(ClaimAssistanceActivity_form.this, "" + response, Toast.LENGTH_SHORT).show();
                        views.hideProgress();

                        Log.d("resposed_data", response.toString());
                        try {

                            JsonObject jsonObject = response.body();

                            if (jsonObject.get("code").getAsString().equals("200")) {
                                views.showToast(ShopImageActivity.this, jsonObject.get("msg").getAsString());
                            } else {
                                views.showToast(ShopImageActivity.this, jsonObject.get("msg").getAsString());
                            }
                        } catch (Exception e) {

                            Log.d("error_message", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                        Toast.makeText(ShopImageActivity.this, "fail" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        views.hideProgress();
                        Log.d("data_error", t.getMessage());

                    }
                });

    }
    private void hitApiUploadCustomer() {

        views.showProgress(ShopImageActivity.this);
        HashMap<String, RequestBody> partMap = new HashMap<>();
        partMap.put("key", ApiFactory.getRequestBodyFromString(Url.key));
        partMap.put("shop_purchase_id", ApiFactory.getRequestBodyFromString(phoneId));
        partMap.put("barcode_scan", ApiFactory.getRequestBodyFromString(txtBarcodeId.getText().toString()));
        partMap.put("image_category", ApiFactory.getRequestBodyFromString(imageCAtegory));

        MultipartBody.Part[] imageArrayInvoice = new MultipartBody.Part[imagePathListCustomer.size()];

        for (int i = 0; i < imageArrayInvoice.length; i++) {
            File file = new File(imagePathListCustomer.get(i));
            try {
                File compressedfile = new Compressor(ShopImageActivity.this).compressToFile(file);
                RequestBody requestBodyArray = RequestBody.create(MediaType.parse("image/*"), compressedfile);
                imageArrayInvoice[i] = MultipartBody.Part.createFormData("image[]", compressedfile.getName(), requestBodyArray);

                Log.d("astysawe", String.valueOf(imageArrayInvoice[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ApiInterface iApiServices = ApiFactory.createRetrofitInstance(Url.BASE_URL).create(ApiInterface.class);
        iApiServices.imageAPi(imageArrayInvoice, partMap)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        //Toast.makeText(ClaimAssistanceActivity_form.this, "" + response, Toast.LENGTH_SHORT).show();
                        views.hideProgress();

                        Log.d("resposed_data", response.toString());
                        try {

                            JsonObject jsonObject = response.body();

                            if (jsonObject.get("code").getAsString().equals("200")) {
                                views.showToast(ShopImageActivity.this, jsonObject.get("msg").getAsString());
                            } else {
                                views.showToast(ShopImageActivity.this, jsonObject.get("msg").getAsString());
                            }
                        } catch (Exception e) {

                            Log.d("error_message", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                        Toast.makeText(ShopImageActivity.this, "fail" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        views.hideProgress();
                        Log.d("data_error", t.getMessage());

                    }
                });

    }


    private void startDialogInvoice() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShopImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShopImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeInvoiceCameraImg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }
    private void startDialogMobile() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShopImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShopImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeMobileCameraImg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }
    private void startDialogCustomer() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShopImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }


            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShopImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_MULTIPLE);

                } else {
                    try {
                        takeCustomerCameraImg();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        myAlertDialog.show();
    }

    private void takeInvoiceCameraImg() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoInvoiceFile = createImageFileInVOice();
                Log.d("checkexcesdp", String.valueOf(photoInvoiceFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("checkexcep", ex.getMessage());
            }

            photoInvoiceFile = createImageFileInVOice();
            //lk changes here
            photoUriInvoice = FileProvider.getUriForFile(ShopImageActivity.this, getPackageName() + ".provider", photoInvoiceFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUriInvoice);
            startActivityForResult(takePictureIntent, 2);
        }

    }
    private void takeMobileCameraImg() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoMobileFile = createImageFileMobile();
                Log.d("checkexcesdp", String.valueOf(photoMobileFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("checkexcep", ex.getMessage());
            }

            photoMobileFile = createImageFileMobile();
            //lk changes here
            photoUriMobile = FileProvider.getUriForFile(ShopImageActivity.this, getPackageName() + ".provider", photoMobileFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUriMobile);
            startActivityForResult(takePictureIntent, 2);
        }

    }
    private void takeCustomerCameraImg() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoCustomerFile = createImageFileCustomer();
                Log.d("checkexcesdp", String.valueOf(photoCustomerFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("checkexcep", ex.getMessage());
            }
            photoCustomerFile = createImageFileCustomer();
            //lk changes here
            photoUriCustomer = FileProvider.getUriForFile(ShopImageActivity.this, getPackageName() + ".provider", photoCustomerFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUriCustomer);
            startActivityForResult(takePictureIntent, 2);
        }

    }

    private File createImageFileInVOice() throws IOException {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPathInvoice = image.getAbsolutePath();

        Log.d("lsdplo", mCurrentPhotoPathInvoice);
        return image;
    }
    private File createImageFileMobile() throws IOException {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPathMobile = image.getAbsolutePath();

        Log.d("lsdplo", mCurrentPhotoPathMobile);
        return image;
    }
    private File createImageFileCustomer() throws IOException {
        String imageFileName = "GOOGLES" + System.currentTimeMillis();
        String storageDir = Environment.getExternalStorageDirectory() + "/skImages";
        Log.d("storagepath===", storageDir);
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPathCustomer = image.getAbsolutePath();

        Log.d("lsdplo", mCurrentPhotoPathCustomer);
        return image;
    }

    public void rotateImageInvoice() throws IOException {

        try {
            String photoPath = photoInvoiceFile.getAbsolutePath();
            imagePathListInvoice.add(photoPath);
            bitmapInVoice = MediaStore.Images.Media.getBitmap(ShopImageActivity.this.getContentResolver(), photoUriInvoice);
            bitmapInVoice = Bitmap.createScaledBitmap(bitmapInVoice, 800, 800, false);
            ImageModel imageModel = new ImageModel();
            imageModel.setImageMobile(bitmapInVoice);
            if (Invoicelist.size() < 5) {
                Invoicelist.add(imageModel);
            }


            if (Invoicelist.size() > 5) {
                //Toast.makeText(getApplicationContext(), "Max Limit Only 10", Toast.LENGTH_SHORT).show();
            }

            rv_Invoice.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            mIMGAdapter = new MobileImageAdapter(Invoicelist, ShopImageActivity.this);
            rv_Invoice.setAdapter(mIMGAdapter);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void rotateImageMobile() throws IOException {

        try {
            String photoPath = photoMobileFile.getAbsolutePath();
            imagePathListMobile.add(photoPath);
            bitmapMobile = MediaStore.Images.Media.getBitmap(ShopImageActivity.this.getContentResolver(), photoUriMobile);
            bitmapMobile = Bitmap.createScaledBitmap(bitmapMobile, 800, 800, false);
            ImageModel imageModel = new ImageModel();
            imageModel.setImageMobile(bitmapMobile);
            if (Mobilelist.size() < 5) {
                Mobilelist.add(imageModel);
            }


            if (Mobilelist.size() > 5) {
                //Toast.makeText(getApplicationContext(), "Max Limit Only 10", Toast.LENGTH_SHORT).show();
            }

            rv_Mobile.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            mIMGAdapter = new MobileImageAdapter(Mobilelist, ShopImageActivity.this);
            rv_Mobile.setAdapter(mIMGAdapter);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public void rotateImageCustomer() throws IOException {

        try {
            String photoPath = photoCustomerFile.getAbsolutePath();
            imagePathListCustomer.add(photoPath);
            bitmapCustomer = MediaStore.Images.Media.getBitmap(ShopImageActivity.this.getContentResolver(), photoUriCustomer);
            bitmapCustomer = Bitmap.createScaledBitmap(bitmapCustomer, 800, 800, false);
            ImageModel imageModel = new ImageModel();
            imageModel.setImageMobile(bitmapCustomer);
            if (Customerlist.size() < 5) {
                Customerlist.add(imageModel);
            }

            if (Customerlist.size() > 5) {
                //Toast.makeText(getApplicationContext(), "Max Limit Only 10", Toast.LENGTH_SHORT).show();
            }
            rv_Customer.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            mIMGAdapter = new MobileImageAdapter(Customerlist, ShopImageActivity.this);
            rv_Customer.setAdapter(mIMGAdapter);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1 && count == 1) {
                selectFromGalleryInvoice(data);

            }
            if (requestCode == 2 && count == 1) {
                rotateImageInvoice();
            }
            if(requestCode==1 && count==2){
                selectFromGalleryMobile(data);
            } if(requestCode==2 && count==2){
                rotateImageMobile();
            }
            if(requestCode==1 && count==3){
                selectFromGalleryCustomer(data);
            } if(requestCode==2 && count==3){
                rotateImageCustomer();
            }
        } catch (Exception e) {
        }
    }

    private void selectFromGalleryInvoice(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};


                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPathInVoice = Helper.pathFromUri(ShopImageActivity.this, mImageUri);
                        imagePathListInvoice.add(photoPathInVoice);

                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncodedInvoice = cursor1.getString(columnIndex1);
                        bitmapInVoice = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();
                        ImageModel imageModel = new ImageModel();
                        imageModel.setImageMobile(bitmapInVoice);

                        if (Invoicelist.size() < 5) {
                            Invoicelist.add(imageModel);
                        }
                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPathInVoice = Helper.pathFromUri(ShopImageActivity.this, mImageUri);
                    imagePathListInvoice.add(photoPathInVoice);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncodedInvoice = cursor1.getString(columnIndex1);
                    bitmapInVoice = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    cursor1.close();
                    ImageModel imageModel = new ImageModel();
                    imageModel.setImageMobile(bitmapInVoice);
                    if (Invoicelist.size() < 5) {
                        Invoicelist.add(imageModel);
                    }
                }

                if (Invoicelist.size() > 5) {
                    //Toast.makeText(getApplicationContext(), "Max Limit Only 10", Toast.LENGTH_SHORT).show();
                }

                rv_Invoice.setLayoutManager(new LinearLayoutManager(ShopImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mIMGAdapter = new MobileImageAdapter(Invoicelist, ShopImageActivity.this);
                rv_Invoice.setAdapter(mIMGAdapter);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }
    private void selectFromGalleryMobile(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPathMobile = Helper.pathFromUri(ShopImageActivity.this, mImageUri);
                        imagePathListMobile.add(photoPathMobile);

                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncodedMobile = cursor1.getString(columnIndex1);
                        bitmapMobile = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();
                        ImageModel imageModel = new ImageModel();
                        imageModel.setImageMobile(bitmapMobile);

                        if (Mobilelist.size() < 5) {
                            Mobilelist.add(imageModel);
                        }
                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPathMobile = Helper.pathFromUri(ShopImageActivity.this, mImageUri);
                    imagePathListMobile.add(photoPathMobile);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncodedMobile = cursor1.getString(columnIndex1);
                    bitmapMobile = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    cursor1.close();
                    ImageModel imageModel = new ImageModel();
                    imageModel.setImageMobile(bitmapMobile);
                    if (Mobilelist.size() < 5) {
                        Mobilelist.add(imageModel);
                    }
                }

                if (Mobilelist.size() > 5) {
                    //Toast.makeText(getApplicationContext(), "Max Limit Only 10", Toast.LENGTH_SHORT).show();
                }

                rv_Mobile.setLayoutManager(new LinearLayoutManager(ShopImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mIMGAdapter = new MobileImageAdapter(Mobilelist, ShopImageActivity.this);
                rv_Mobile.setAdapter(mIMGAdapter);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }
    private void selectFromGalleryCustomer(Intent data) {
        if (data != null) {
            try {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if (data.getClipData() != null) {
                    int imageCount = data.getClipData().getItemCount();
                    for (int i = 0; i < imageCount; i++) {
                        Uri mImageUri = data.getClipData().getItemAt(i).getUri();
                        photoPathCustomer = Helper.pathFromUri(ShopImageActivity.this, mImageUri);
                        imagePathListCustomer.add(photoPathCustomer);

                        // Get the cursor
                        Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor1.moveToFirst();

                        int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                        imageEncodedCustomer = cursor1.getString(columnIndex1);
                        bitmapCustomer = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                        cursor1.close();
                        ImageModel imageModel = new ImageModel();
                        imageModel.setImageMobile(bitmapCustomer);

                        if (Customerlist.size() < 5) {
                            Customerlist.add(imageModel);
                        }
                    }
                } else {
                    Uri mImageUri = data.getData();
                    photoPathCustomer = Helper.pathFromUri(ShopImageActivity.this, mImageUri);
                    imagePathListCustomer.add(photoPathCustomer);

                    // Get the cursor
                    Cursor cursor1 = getApplicationContext().getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor1.moveToFirst();

                    int columnIndex1 = cursor1.getColumnIndex(filePathColumn[0]);
                    imageEncodedCustomer = cursor1.getString(columnIndex1);
                    bitmapCustomer = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    cursor1.close();
                    ImageModel imageModel = new ImageModel();
                    imageModel.setImageMobile(bitmapCustomer);
                    if (Customerlist.size() < 5) {
                        Customerlist.add(imageModel);
                    }
                }

                if (Customerlist.size() > 5) {
                    //Toast.makeText(getApplicationContext(), "Max Limit Only 10", Toast.LENGTH_SHORT).show();
                }

                rv_Customer.setLayoutManager(new LinearLayoutManager(ShopImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mIMGAdapter = new MobileImageAdapter(Customerlist, ShopImageActivity.this);
                rv_Customer.setAdapter(mIMGAdapter);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void scanResultData(String codeFormat, String codeContent) {
        txtBarcodeId.setText(codeContent);
    }

    @Override
    public void scanResultData(NoScanResultException noScanData) {

    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

}
