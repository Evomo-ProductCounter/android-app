package com.evomo.productcounterapp.ui.camera;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.evomo.productcounterapp.data.db.CountObject;
import com.evomo.productcounterapp.data.repository.CountRepository;

public class CameraViewModel extends AndroidViewModel {
    private final CountRepository mCountRepository;

    public CameraViewModel(@NonNull Application application) {
        super(application);
        mCountRepository = new CountRepository(application);
    }

    public void insert(CountObject countObject) {
        mCountRepository.insert(countObject);
    }
}