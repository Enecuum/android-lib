package com.enecuum.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 Framgia, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by Pham Quy Hai on 5/16/16.
 */


public final class EmulatorDetector {

    public interface OnEmulatorDetectorListener {
        void onResult(String result);
    }

   /* private static final String[] PHONE_NUMBERS = {
            "15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564",
            "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576",
            "15555215578", "15555215580", "15555215582", "15555215584"
    };

    private static final String[] DEVICE_IDS = {
            "000000000000000",
            "e21833235b6eef10",
            "012345678912345"
    };

    private static final String[] IMSI_IDS = {
            "310260000000000"
    };*/

    private static final String[] GENY_FILES = {
            "/dev/socket/genyd",
            "/dev/socket/baseband_genyd"
    };

    private static final String[] QEMU_DRIVERS = {"goldfish"};

    private static final String[] PIPES = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };

    private static final String[] X86_FILES = {
            "ueventd.android_x86.rc",
            "x86.prop",
            "ueventd.ttVM_x86.rc",
            "init.ttVM_x86.rc",
            "fstab.ttVM_x86",
            "fstab.vbox86",
            "init.vbox86.rc",
            "ueventd.vbox86.rc"
    };

    private static final String[] ANDY_FILES = {
            "fstab.andy",
            "ueventd.andy.rc"
    };

    private static final String[] NOX_FILES = {
            "fstab.nox",
            "init.nox.rc",
            "ueventd.nox.rc"
    };

    private static final Property[] PROPERTIES = {
            new Property("init.svc.qemud", null),
            new Property("init.svc.qemu-props", null),
            new Property("qemu.hw.mainkeys", null),
            new Property("qemu.sf.fake_camera", null),
            new Property("qemu.sf.lcd_density", null),
            new Property("ro.bootloader", "unknown"),
            new Property("ro.bootmode", "unknown"),
            new Property("ro.hardware", "goldfish"),
            new Property("ro.kernel.android.qemud", null),
            new Property("ro.kernel.qemu.gles", null),
            new Property("ro.kernel.qemu", "1"),
            new Property("ro.product.device", "generic"),
            new Property("ro.product.model", "sdk"),
            new Property("ro.product.name", "sdk"),
            new Property("ro.serialno", null)
    };

    private static final String IP = "10.0.2.15";

    private static final int MIN_PROPERTIES_THRESHOLD = 0x5;

    private final Context mContext;
    private boolean isDebug = false;
    private boolean isTelephony = false;
    private boolean isCheckPackage = true;
    private List<String> mListPackageName = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    //Since we use application context now this won't leak memory anymore. This is only to please Lint
    private static EmulatorDetector mEmulatorDetector;

    public static EmulatorDetector with(Context pContext) {
        if (pContext == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        if (mEmulatorDetector == null)
            mEmulatorDetector = new EmulatorDetector(pContext.getApplicationContext());
        return mEmulatorDetector;
    }

    private EmulatorDetector(Context pContext) {
        mContext = pContext;
        mListPackageName.add("com.google.android.launcher.layouts.genymotion");
        mListPackageName.add("com.bluestacks");
        mListPackageName.add("com.bignox.app");
    }

    public EmulatorDetector setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isCheckTelephony() {
        return isTelephony;
    }

    public boolean isCheckPackage() {
        return isCheckPackage;
    }

    public EmulatorDetector setCheckTelephony(boolean telephony) {
        this.isTelephony = telephony;
        return this;
    }

    public EmulatorDetector setCheckPackage(boolean chkPackage) {
        this.isCheckPackage = chkPackage;
        return this;
    }

    public EmulatorDetector addPackageName(String pPackageName) {
        this.mListPackageName.add(pPackageName);
        return this;
    }

    public EmulatorDetector addPackageName(List<String> pListPackageName) {
        this.mListPackageName.addAll(pListPackageName);
        return this;
    }

    public List<String> getPackageNameList() {
        return this.mListPackageName;
    }

    /*public void detect(final OnEmulatorDetectorListener pOnEmulatorDetectorListener) {
        new Thread(() -> {
            log(getDeviceInfo());
            log("This System is Emulator: " + isEmulator);
            if (pOnEmulatorDetectorListener != null) {
                pOnEmulatorDetectorListener.onResult(isEmulator);
            }
        }).start();
    }*/

    public String checkBasic() {
        if (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) {
            return reasonDescription("BRAND", String.format("%s %s", Build.BRAND, Build.DEVICE));
        } else if (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")) {
            return reasonDescription("FINGERPRINT", Build.FINGERPRINT);
        } else if (Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.HARDWARE.equals("vbox86")
                || Build.HARDWARE.toLowerCase().contains("nox")) {
            return reasonDescription("HARDWARE", Build.HARDWARE);
        } else if (Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MODEL.toLowerCase().contains("droid4x")
        ) {
            return reasonDescription("MODEL", Build.MODEL);
        } else if (Build.MANUFACTURER.contains("Genymotion")) {
            return reasonDescription("MANUFACTURER", Build.MANUFACTURER);
        } else if (Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
                || Build.PRODUCT.equals("google_sdk")
                || Build.PRODUCT.toLowerCase().contains("nox")
        ) {
            return reasonDescription("PRODUCT", Build.PRODUCT);
        } else if (Build.BOARD.toLowerCase().contains("nox")) {
            return reasonDescription("BOARD", Build.BOARD);
        } else if (Build.BOOTLOADER.toLowerCase().contains("nox")) {
            return reasonDescription("BOOTLOADER", Build.BOOTLOADER);
        } else if (Build.SERIAL.toLowerCase().contains("nox")) {
            return reasonDescription("SERIAL", Build.SERIAL);
        }
        return null;
    }

    public String checkEmulatorFiles() {
        if (checkFiles(GENY_FILES, "Geny")) {
            return reasonDescription("Has emulator files", "geny");
        } else if (checkFiles(ANDY_FILES, "Andy")) {
            return reasonDescription("Has emulator files", "Andy");
        } else if (checkFiles(NOX_FILES, "Nox")) {
            return reasonDescription("Has emulator files", "Nox");
        } else if (checkFiles(X86_FILES, "X86")) {
            return reasonDescription("Has emulator files", "x86 emulation");
        }
        return null;
    }

    public String checkQemuFiles() {
        if (checkQEmuDrivers()) {
            return reasonDescription("Has emulator files", "Qemu drivers");
        } else if (checkFiles(PIPES, "Pipes")) {
            return reasonDescription("Has emulator files", "Qemu pipe files");
        } else if (checkQEmuProps()) {
            return reasonDescription("Has emulator properties", "Qemu system props");
        }
        return null;
    }

    public String checkIP() {
        if (checkIp()) {
            return reasonDescription("Has suspicious IP", IP);
        }
        return null;
    }

    private String checkPackageName() {
        if (!isCheckPackage || mListPackageName.isEmpty()) {
            return null;
        }
        final PackageManager packageManager = mContext.getPackageManager();
        for (final String pkgName : mListPackageName) {
            final Intent tryIntent = packageManager.getLaunchIntentForPackage(pkgName);
            if (tryIntent != null) {
                final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(tryIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!resolveInfos.isEmpty()) {
                    return reasonDescription("Has suspicious package name", pkgName);
                }
            }
        }
        return null;
    }

    //needs additional permissions
    /*
    private boolean checkTelephony() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED && this.isTelephony && isSupportTelePhony()) {
            return checkPhoneNumber()
                    || checkDeviceId()
                    || checkImsi()
                    || checkOperatorNameAndroid();
        }
        return false;
    }

    private boolean checkPhoneNumber() {
        TelephonyManager telephonyManager =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        @SuppressLint("HardwareIds") String phoneNumber = telephonyManager.getLine1Number();

        for (String number : PHONE_NUMBERS) {
            if (number.equalsIgnoreCase(phoneNumber)) {
                log(" check phone number is detected");
                return true;
            }

        }
        return false;
    }

    private boolean checkDeviceId() {
        TelephonyManager telephonyManager =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        @SuppressLint("HardwareIds") String deviceId = telephonyManager.getDeviceId();

        for (String known_deviceId : DEVICE_IDS) {
            if (known_deviceId.equalsIgnoreCase(deviceId)) {
                log("Check device id is detected");
                return true;
            }

        }
        return false;
    }

    private boolean checkImsi() {
        TelephonyManager telephonyManager =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("HardwareIds") String imsi = telephonyManager.getSubscriberId();

        for (String known_imsi : IMSI_IDS) {
            if (known_imsi.equalsIgnoreCase(imsi)) {
                log("Check imsi is detected");
                return true;
            }
        }
        return false;
    }

    private boolean checkOperatorNameAndroid() {
        String operatorName = ((TelephonyManager)
                mContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
        if (operatorName.equalsIgnoreCase("android")) {
            log("Check operator name android is detected");
            return true;
        }
        return false;
    }
     */

    private boolean checkQEmuDrivers() {
        for (File drivers_file : new File[]{new File("/proc/tty/drivers"), new File("/proc/cpuinfo")}) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                String driver_data = new String(data);
                for (String known_qemu_driver : QEMU_DRIVERS) {
                    if (driver_data.contains(known_qemu_driver)) {
                        log("Check QEmuDrivers is detected");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkFiles(String[] targets, String type) {
        for (String pipe : targets) {
            File qemu_file = new File(pipe);
            if (qemu_file.exists()) {
                log("Check " + type + " is detected");
                return true;
            }
        }
        return false;
    }

    private boolean checkQEmuProps() {
        int found_props = 0;

        for (Property property : PROPERTIES) {
            String property_value = getProp(mContext, property.name);
            if ((property.seek_value == null) && (property_value != null && !property_value.isEmpty())) {
                found_props++;
            }
            if ((property.seek_value != null) && (property_value.contains(property.seek_value))) {
                found_props++;
            }
        }

        if (found_props >= MIN_PROPERTIES_THRESHOLD) {
            log("Check QEmuProps is detected");
            return true;
        }
        return false;
    }

    private boolean checkIp() {
        boolean ipDetected = false;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            String[] args = {"/system/bin/netcfg"};
            StringBuilder stringBuilder = new StringBuilder();
            try {
                ProcessBuilder builder = new ProcessBuilder(args);
                builder.directory(new File("/system/bin/"));
                builder.redirectErrorStream(true);
                Process process = builder.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    stringBuilder.append(new String(re));
                }
                in.close();

            } catch (Exception ex) {
                // empty catch
            }

            String netData = stringBuilder.toString();
            log("netcfg data -> " + netData);

            if (!TextUtils.isEmpty(netData)) {
                String[] array = netData.split("\n");

                for (String lan :
                        array) {
                    if ((lan.contains("wlan0") || lan.contains("tunl0") || lan.contains("eth0"))
                            && lan.contains(IP)) {
                        ipDetected = true;
                        log("Check IP is detected");
                        break;
                    }
                }

            }
        }
        return ipDetected;
    }

    private String getProp(Context context, String property) {
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemProperties = classLoader.loadClass("android.os.SystemProperties");

            Method get = systemProperties.getMethod("get", String.class);

            Object[] params = new Object[1];
            params[0] = property;

            return (String) get.invoke(systemProperties, params);
        } catch (Exception exception) {
            // empty catch
        }
        return null;
    }

    private boolean isSupportTelePhony() {
        PackageManager packageManager = mContext.getPackageManager();
        boolean isSupport = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        log("Supported TelePhony: " + isSupport);
        return isSupport;
    }

    private void log(String str) {
        if (this.isDebug) {
            Log.d(getClass().getName(), str);
        }
    }

    private static String getDeviceInfo() {
        return "Build.PRODUCT: " + Build.PRODUCT + "\n" +
                "Build.MANUFACTURER: " + Build.MANUFACTURER + "\n" +
                "Build.BRAND: " + Build.BRAND + "\n" +
                "Build.DEVICE: " + Build.DEVICE + "\n" +
                "Build.MODEL: " + Build.MODEL + "\n" +
                "Build.HARDWARE: " + Build.HARDWARE + "\n" +
                "Build.FINGERPRINT: " + Build.FINGERPRINT;
    }

    private String reasonDescription(String param, String value) {
        return String.format("%s: %s", param, value);
    }

    static class Property {
        String name;
        String seek_value;

        Property(String name, String seek_value) {
            this.name = name;
            this.seek_value = seek_value;
        }
    }
}
