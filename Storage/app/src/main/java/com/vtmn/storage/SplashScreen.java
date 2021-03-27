package com.vtmn.storage;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.lang.reflect.Method;

public class SplashScreen extends AppCompatActivity {
    private File storage;
    private String[] allPath;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        // Load data here
        // Get path of all storage areas (Phone and SD card)
        allPath = StorageUtil.getStorageDirectories(this);

        for (String path : allPath) {
            storage = new File(path);
        // Method.load_Directory_files(storage);
        }

        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
    }
}
