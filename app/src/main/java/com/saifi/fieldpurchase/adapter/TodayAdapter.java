package com.saifi.fieldpurchase.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.saifi.fieldpurchase.R;
import com.saifi.fieldpurchase.retrofitmodel.TodayListDatum;
import com.saifi.fieldpurchase.util.Views;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TotalHolder> {

    Context context;
    ArrayList<TodayListDatum> list;
    Views views = new Views();

    public TodayAdapter(Context context, ArrayList<TodayListDatum> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public TodayAdapter.TotalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_today, parent, false);
        return new TodayAdapter.TotalHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final TodayAdapter.TotalHolder holder, int position) {
        final TodayListDatum totalModel = list.get(position);
        holder.txtBrandAll.setText(totalModel.getBrandName());
        holder.txtModelAll.setText(totalModel.getModelName());
        holder.txtGBAll.setText(totalModel.getGb());
        holder.txtNameAll.setText("Purchased By(" + totalModel.getName() + ")");
        holder.txtBarcodeAll.setText("Barcode no : " + totalModel.getBarcodeScan());
        holder.txtCategoryAll.setText("Purchase Category : " + totalModel.getPurchaseCatName());
        holder.txtPrice.setText("₹ "+totalModel.getPurchaseAmount()+"/-");


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class TotalHolder extends RecyclerView.ViewHolder {

        Button scanButtonAll;
        TextView txtBrandAll, txtModelAll, txtGBAll, txtNameAll, txtBarcodeAll, txtCategoryAll,txtPrice;
        ImageView imgPhone;

        public TotalHolder(@NonNull View itemView) {
            super(itemView);
//            scanButtonAll = itemView.findViewById(R.id.scanButtonAll);
            txtBrandAll = itemView.findViewById(R.id.txtBrandAll);
            txtModelAll = itemView.findViewById(R.id.txtModelAll);
            txtGBAll = itemView.findViewById(R.id.txtGBAll);
            txtNameAll = itemView.findViewById(R.id.txtNameAll);
            imgPhone = itemView.findViewById(R.id.imgPhone);
            txtBarcodeAll = itemView.findViewById(R.id.txtBarcodeAll);
            txtCategoryAll = itemView.findViewById(R.id.txtCategoryAll);
            txtPrice = itemView.findViewById(R.id.txtPrice);

        }
    }
}