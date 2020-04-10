package com.saifi.fieldpurchase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.saifi.fieldpurchase.constants.SessonManager;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity {

    Button shopButton, todayButton;
    RelativeLayout mainLaout;
    long back_pressed = 0;
    SessonManager sessonManager;
    TextView txtLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        sessonManager = new SessonManager(getBaseContext());

        askForPermissioncamera(Manifest.permission.CAMERA, CAMERA);

        shopButton = findViewById(R.id.shopButton);
        todayButton = findViewById(R.id.todayButton);
        txtLogout = findViewById(R.id.txtLogout);
        mainLaout = findViewById(R.id.mainLaout);

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShopActivity.class));
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessonManager.setToken("");
                Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TodayActivity.class));
            }
        });
    }

    private void askForPermissioncamera(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else {
            Snackbar snackbar = Snackbar.make(mainLaout, "Double Tap to Exit!", Snackbar.LENGTH_SHORT);
            View view = snackbar.getView();
            view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
