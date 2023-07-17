package com.evomo.productcounterapp.ui.camera;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.evomo.productcounterapp.data.db.CountObject;
import com.evomo.productcounterapp.data.model.DataProduct;
import com.evomo.productcounterapp.data.model.MachineProductResponse;
import com.evomo.productcounterapp.data.remote.ApiConfig;
import com.evomo.productcounterapp.data.remote.ApiService;
import com.evomo.productcounterapp.data.repository.CountRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraViewModel extends AndroidViewModel {
    private final CountRepository mCountRepository;
    private String token;
    private MutableLiveData<List<DataProduct>> _listProduct = new MutableLiveData<>();
    public LiveData<List<DataProduct>> listProduct = _listProduct;

    private MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    public CameraViewModel(@NonNull Application application, String token) {
        super(application);
        mCountRepository = new CountRepository(application);
        this.token = token;
    }

    public void insert(CountObject countObject) {
        mCountRepository.insert(countObject);
    }

    public void getProducts(String machineId) {
        _isLoading.setValue(true);
        ApiService client = ApiConfig.Companion.getApiService();
        Call<MachineProductResponse> call = client.getProducts(token, machineId);
        call.enqueue(new Callback<MachineProductResponse>() {
            @Override
            public void onResponse(Call<MachineProductResponse> call, Response<MachineProductResponse> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    _listProduct.setValue(response.body().getData());
                } else {
                    Log.e(TAG, "onFailure: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MachineProductResponse> call, Throwable t) {
                _isLoading.setValue(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private static final String TAG = "CameraViewModel";
}