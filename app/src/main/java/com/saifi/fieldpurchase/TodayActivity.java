package com.saifi.fieldpurchase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.saifi.fieldpurchase.adapter.TodayAdapter;
import com.saifi.fieldpurchase.constants.SessonManager;
import com.saifi.fieldpurchase.constants.Url;
import com.saifi.fieldpurchase.retrofitmodel.TodayListDatum;
import com.saifi.fieldpurchase.retrofitmodel.TodayListStatusModel;
import com.saifi.fieldpurchase.service.ApiInterface;
import com.saifi.fieldpurchase.util.Views;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TodayActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener {

    RecyclerView rvTodayData;
    LinearLayoutManager layoutManager;
    TodayAdapter adapter;
    ArrayList<TodayListDatum> listData = new ArrayList<>();
    ArrayList<TodayListDatum> listData2 = new ArrayList<>();
    Call<TodayListStatusModel> call;
    int currentPage = 1;
    int totalPage;
    Views views;
    SessonManager sessonManager;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        getSupportActionBar().setTitle("Today Purchase");
        rvTodayData = findViewById(R.id.rvTodayData);
        layoutManager = new GridLayoutManager(TodayActivity.this, 1);
        rvTodayData.setLayoutManager(layoutManager);
        views = new Views();
        sessonManager = new SessonManager(this);

        hitApi();

        rvTodayData.setOnScrollChangeListener(this);
        //initializing our adapter
        adapter = new TodayAdapter(this, listData2);
        //Adding adapter to recyclerview
        rvTodayData.setAdapter(adapter);
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
        if (isLastItemDisplaying(rvTodayData)) {
            if (currentPage < totalPage) {
                hitApi();
            }

        }
    }

    private void hitApi() {
        views.showProgress(this);
        listData.clear();
        //   listDataSearch.clear();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Url.BASE_URL)
                .build();

        ApiInterface api = retrofit.create(ApiInterface.class);

        call = api.hitTodayParms(Url.key, String.valueOf(currentPage), sessonManager.getToken());

        call.enqueue(new Callback<TodayListStatusModel>() {
            @Override
            public void onResponse(Call<TodayListStatusModel> call, Response<TodayListStatusModel> response) {
                views.hideProgress();

                if (response.isSuccessful()) {
                    TodayListStatusModel model = response.body();
                    totalPage = model.getTotalPages();

                    listData = model.getData();
                    listData2.addAll(listData);
                    adapter.notifyDataSetChanged();

                    currentPage = currentPage + 1;
                } else {
                    views.showToast(TodayActivity.this, String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<TodayListStatusModel> call, Throwable t) {
                views.hideProgress();
                views.showToast(TodayActivity.this, t.getMessage());
            }
        });
    }

}
