package com.appunite.debugutils.debug;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class DebugDrawerUtils {


    public static String checkSDKNamme(int sdkInt) {
        switch (sdkInt) {
            case Build.VERSION_CODES.BASE: // API level 1
                return "Base";
            case Build.VERSION_CODES.BASE_1_1: // API level 2
                return "Base update";
            case Build.VERSION_CODES.CUPCAKE: // API level 3
                return  "Cupcake";
            case Build.VERSION_CODES.CUR_DEVELOPMENT: // API level 4
                return "Cur development";
            case Build.VERSION_CODES.DONUT: // API level 5
                return "Donut";
            case Build.VERSION_CODES.ECLAIR: // API level 6
                return "Eclair";
            case Build.VERSION_CODES.ECLAIR_0_1: // API level 7
                return "Eclair 0 1";
            case Build.VERSION_CODES.ECLAIR_MR1: // API level 8
                return  "Eclair MR1";
            case Build.VERSION_CODES.FROYO: // API level 9
                return  "Froyoooo";
            case Build.VERSION_CODES.GINGERBREAD: // API level 10
                return "Gingerbread";
            case Build.VERSION_CODES.GINGERBREAD_MR1: // API level 11
                return  "Gingerbread MR1";
            case Build.VERSION_CODES.HONEYCOMB: // API level 12
                return "Honeycomb";
            case Build.VERSION_CODES.HONEYCOMB_MR1: // API level 13
                return "Homecomb MR1";
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                return "Honeycomb MR2";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH: // API level 14
                return "Ice Cream Sandwich";
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1: // API level 15
                return  "Ice Cream Sandwich MR1";
            case Build.VERSION_CODES.JELLY_BEAN: // API level 16
                return  "Jelly Bean";
            case Build.VERSION_CODES.JELLY_BEAN_MR1: // API level 17
                return  "Jelly Bean MR1";
            case Build.VERSION_CODES.JELLY_BEAN_MR2: // API level 18
                return "Jelly Bean MR2";
            case Build.VERSION_CODES.KITKAT: // API level 19
                return  "Kitkat";
            case Build.VERSION_CODES.KITKAT_WATCH: //API 20
                return  "Kitkat Watch";
            case Build.VERSION_CODES.LOLLIPOP: //API 21
                return "Lolipop";
            case Build.VERSION_CODES.LOLLIPOP_MR1: //API 22
                return "Lolipop MR1";
            case 23:
                return "Marshmallow";
            default:
                return "Unknown";
        }
    }

    public static String getBuildVersion(Context context) {
        try {
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            version = version.replace(".debug","");
            version = version.replace(".relase", "");

            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public static String getBuildType(Context context) {
        try {
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            if(version.contains("debug")) {
                return "debug";
            }
            else if (version.contains(".relase")) {
               return "relase";
            }
            else {
                return "Unknown";
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }
}
