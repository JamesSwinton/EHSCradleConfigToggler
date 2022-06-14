package com.zebra.jamesswinton.ehscradleconfigtoggler.profilemanager;

import android.annotation.SuppressLint;

public class Xml {

  // Enum
  public enum CradleState { IN, OUT }

  // Profile Names
  public static final String ProfileName = "ToggleEHSConfigOnCradleBroadcast";

  // EHS Config Names
  @SuppressLint("SdCardPath")
  public static final String InCradleEHSConfigName = "/sdcard/dt_enterprisehomescreen.xml";
  @SuppressLint("SdCardPath")
  public static final String OutOfCradleEhsConfigName = "/sdcard/hh_enterprisehomescreen.xml";

  // Display Mgr
  public static final String InCradleTimeOutInterval = "1800";
  public static final String OutOfCradleTimeOutInterval = "300";

  // Dev Admin Mgr
  public static final String InCradleScreenLockType = "5";
  public static final String OutOfCradleScreenLockType = "1";

  public static String getXml(CradleState cradleState) {
    String timeoutInterval = cradleState == CradleState.IN ? InCradleTimeOutInterval : OutOfCradleTimeOutInterval;
    String screenLockType = cradleState == CradleState.IN ? InCradleScreenLockType : OutOfCradleScreenLockType;
    String configFileName = cradleState == CradleState.IN ? InCradleEHSConfigName : OutOfCradleEhsConfigName;
    return
        "<wap-provisioningdoc>\n" +
        "  <characteristic type=\"Profile\">\n" +
        "    <parm name=\"ProfileName\" value=" + '"' + ProfileName + '"' + " />\n" +
        "    <!--DISPLAY TIMEOUT 30 mins -->\n" +
        "    <characteristic version=\"8.0\" type=\"DisplayMgr\">\n" +
        "      <parm name=\"emdk_name\" value=\"displayStep\" />" +
        "      <parm name=\"TimeoutInterval\" value=" + '"' + timeoutInterval + '"' + " />\n" +
        "    </characteristic>\n" +
        "    <!-- DISABLE SCREENLOCK -->\n" +
        "    <characteristic version=\"6.0\" type=\"DevAdmin\">\n" +
        "      <parm name=\"emdk_name\" value=\"devStep\" />" +
        "      <parm name=\"ScreenLockType\" value=" + '"' + screenLockType + '"' + " />\n" +
        "    </characteristic>" +
        "    <characteristic version=\"7.0\" type=\"AppMgr\">\n" +
        "      <parm name=\"emdk_name\" value=\"firstStep\" />" +
        "      <parm name=\"Action\" value=\"LaunchApplication\" />\n" +
        "      <parm name=\"ApplicationName\" value=\"Enterprise Home Screen\" />\n" +
        "    </characteristic>\n" +
        "    <characteristic version=\"0.6\" type=\"FileMgr\">\n" +
        "      <parm name=\"emdk_name\" value=\"secondStep\" />" +
        "      <parm name=\"FileAction\" value=\"1\" />\n" +
        "      <characteristic type=\"file-details\">\n" +
        "        <parm name=\"TargetAccessMethod\" value=\"2\" />\n" +
        "        <parm name=\"TargetPathAndFileName\" value=\"/enterprise/usr/enterprisehomescreen.xml\" />\n" +
        "        <parm name=\"SourceAccessMethod\" value=\"2\" />\n" +
        "        <parm name=\"SourcePathAndFileName\" value=" + '"' + configFileName + '"' + " />\n" +
        "      </characteristic>\n" +
        "    </characteristic>\n" +
        "    <characteristic version=\"7.0\" type=\"AppMgr\">\n" +
        "      <parm name=\"emdk_name\" value=\"lastStep\" />" +
        "      <parm name=\"Action\" value=\"LaunchApplication\" />\n" +
        "      <parm name=\"ApplicationName\" value=\"RefreshEHSConfig\" />\n" +
        "    </characteristic>\n" +
        "  </characteristic>\n" +
        "</wap-provisioningdoc>";
  }

}
