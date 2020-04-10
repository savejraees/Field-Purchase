package com.saifi.fieldpurchase.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.saifi.fieldpurchase.R;


public class Views {
    public Dialog dialog;

    public void showProgress(Context context) {
        if (context != null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_layout);
            dialog.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void hideProgress() {
        while (dialog != null && dialog.isShowing()) {
       dialog.dismiss();
        }
    }

    public void showToast(Context context,String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void logData(String msg){
        Log.d("checkData",msg);
    }
}
