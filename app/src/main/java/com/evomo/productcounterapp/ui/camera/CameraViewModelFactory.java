package com.evomo.productcounterapp.ui.camera;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CameraViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile CameraViewModelFactory INSTANCE;
    private final Application mApplication;
    private final String mToken;

    CameraViewModelFactory(Application application, String token) {
        mApplication = application;
        mToken = token;
    }
    public static CameraViewModelFactory getInstance(Application application, String token) {
        if (INSTANCE == null) {
            synchronized (CameraViewModelFactory.class) {
                INSTANCE = new CameraViewModelFactory(application, token);
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CameraViewModel.class)) {
            return (T) new CameraViewModel(mApplication, mToken);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}