package com.zebra.jamesswinton.ehscradleconfigtoggler;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      startForegroundService(new Intent(this, MonitorCradleStateService.class));
    } else {
      startService(new Intent(this, MonitorCradleStateService.class));
    }

    finish();
  }
}