package com.saifi.fieldpurchase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.saifi.fieldpurchase.constants.SessonManager;
import com.saifi.fieldpurchase.constants.Url;
import com.saifi.fieldpurchase.retrofitmodel.StatusModel;
import com.saifi.fieldpurchase.service.ApiInterface;
import com.saifi.fieldpurchase.util.Views;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    SessonManager sessonManager;
    String token;
    Views views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessonManager = new SessonManager(SplashActivity.this);
        views = new Views();
        token = sessonManager.getToken();

        getSupportActionBar().hide();

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        final Animation animation_1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.antirotate);
        final Animation animation_3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
        imageView.startAnimation(animation_2);
        animation_2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animation_1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation_1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animation_3);
                if(token.isEmpty() || token==null || token.equals("")){
                    finish();
                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(i);
                }else {
                    finish();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }

            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
