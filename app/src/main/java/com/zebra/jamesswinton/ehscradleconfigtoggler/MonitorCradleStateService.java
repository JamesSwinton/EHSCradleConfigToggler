package com.zebra.jamesswinton.ehscradleconfigtoggler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;
import com.zebra.jamesswinton.ehscradleconfigtoggler.profilemanager.ProcessProfileAsync;
import com.zebra.jamesswinton.ehscradleconfigtoggler.profilemanager.ProcessProfileAsync.OnProfileApplied;
import com.zebra.jamesswinton.ehscradleconfigtoggler.profilemanager.Xml;
import com.zebra.jamesswinton.ehscradleconfigtoggler.profilemanager.Xml.CradleState;
import com.zebra.jamesswinton.ehscradleconfigtoggler.profilemanager.XmlParsingError;
import com.zebra.jamesswinton.ehscradleconfigtoggler.service.NotificationHelper;

public class MonitorCradleStateService extends Service implements EMDKListener,
    OnProfileApplied {

  // Notification Button Intents
  public static final String StopServiceAction = "com.zebra.jamesswinton.monitorcradlestate.STOP";

  // Intents
  private static final String PowerConnectedIntentAction = "android.intent.action.ACTION_POWER_CONNECTED";
  private static final String PowerDisconnectedIntentAction = "android.intent.action.ACTION_POWER_DISCONNECTED";

  // EMDK
  private EMDKManager mEmdkManager;
  private ProfileManager mProfileManager;

  @Override
  public void onCreate() {
    super.onCreate();
    // Get EMDK
    EMDKResults emdkManagerResults = EMDKManager.getEMDKManager(getBaseContext(), MonitorCradleStateService.this);
    if (emdkManagerResults == null || emdkManagerResults.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
      Toast.makeText(MonitorCradleStateService.this, "Could not obtain EMDKManager", Toast.LENGTH_LONG).show();
    }

    // Start Service
    startForeground(NotificationHelper.NOTIFICATION_ID,
        NotificationHelper.createNotification(this));
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // Start
    if (intent != null) {
      String intentAction = intent.getAction();
      if (intentAction == null) {
        return START_STICKY;
      } else if (intentAction.equals(StopServiceAction)) {
        stopSelf(startId);
        return START_NOT_STICKY;
      } else {
        return START_NOT_STICKY;
      }
    } else {
      return START_NOT_STICKY;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(PowerConnectionReceiver);
  }

  /**
   * EMDK Callbacks
   */

  @Override
  public void onOpened(EMDKManager emdkManager) {
    // Get Profile Mgr
    mProfileManager = (ProfileManager) emdkManager
        .getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

    // Verify Instance & Register Receiver
    if (mProfileManager != null) {
      Log.i(this.getClass().getName(), "Profile Manager Obtained, listening for intents...");
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(PowerConnectedIntentAction);
      intentFilter.addAction(PowerDisconnectedIntentAction);
      registerReceiver(PowerConnectionReceiver, intentFilter);
    } else {
      Log.e(this.getClass().getName(), "Error Obtaining ProfileManager!");
      Toast.makeText(this, "Error Obtaining ProfileManager!", Toast.LENGTH_LONG).show();
      System.exit(0);
    }
  }

  @Override
  public void onClosed() {
    if (mEmdkManager != null) {
      mEmdkManager.release();
      mEmdkManager = null;
    }
  }

  /**
   * Power connection receiver
   */

  private final BroadcastReceiver PowerConnectionReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get Intent
      CradleState cradleState;
      switch (intent.getAction()){
        case PowerConnectedIntentAction:
          cradleState = CradleState.IN;
          break;
        case PowerDisconnectedIntentAction:
          cradleState = CradleState.OUT;
          break;
        default:
          return;
      }

      // Process
      toggleCradleState(cradleState);
    }
  };

  /**
   * Process Profile
   */

  private void toggleCradleState(CradleState cradleState) {
    new ProcessProfileAsync(Xml.ProfileName, mProfileManager, this)
        .execute(Xml.getXml(cradleState));
  }

  @Override
  public void profileApplied(String xml, EMDKResults emdkResults) {
    Toast.makeText(this, "EHS Config Updated", Toast.LENGTH_LONG).show();
  }

  @Override
  public void profileError(XmlParsingError... parsingErrors) {
    showXmlParsingError(parsingErrors);
  }

  private void showXmlParsingError(XmlParsingError... parsingErrors) {
    StringBuilder errsConcat = new StringBuilder("Error Processing XML!\n\n");
    for (int i = 0; i < parsingErrors.length; i++) {
      XmlParsingError err = parsingErrors[i];
      errsConcat.append(String.format("Error %1$s/%2$s", i + 1, parsingErrors.length));
      errsConcat.append("\n\n");
      errsConcat.append(String.format("Type: %1$s", err.getType()));
      errsConcat.append("\n");
      errsConcat.append(String.format("Desc: %1$s", err.getDescription()));
      errsConcat.append("\n\n");
    } Toast.makeText(this, errsConcat, Toast.LENGTH_LONG).show();
  }

  /**
   * Unsupported
   */

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}