package com.easemob.livedemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.easemob.livedemo.DemoApplication;

public class Utils {
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) DemoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) DemoApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    public static void showToast(final Activity activity, final String toastContent) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showLongToast(final Activity activity, final String toastContent) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, toastContent, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String getStringRandom(int length) {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equalsIgnoreCase(charOrNum)) {
                val.append((char) (random.nextInt(26) + 97));
            } else {
                val.append(String.valueOf(random.nextInt(10)));
            }
        }
        return val.toString();
    }

    public static File getFile(Context context, String fileName) {
        File path = context.getExternalFilesDir(null);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void writeToFile(Context context, String fileName, String content) {
        OutputStreamWriter fos = null;
        try {
            fos = new OutputStreamWriter(new FileOutputStream(getFile(context, fileName)), Charset.forName("gbk"));
            fos.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Typeface getRobotoBlackTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/Roboto-Black.ttf");
    }

    public static Typeface getRobotoBlackItalicTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/Roboto-BlackItalic.ttf");
    }

    public static Typeface getRobotoRegularTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");
    }

    public static Typeface getRobotoBoldTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAgeByBirthday(String birthday) {
        try {
            SimpleDateFormat bDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parseDate = bDate.parse(birthday);
            if (null == parseDate) {
                return 0;
            }
            Calendar c = Calendar.getInstance();
            c.setTime(parseDate);
            return Calendar.getInstance().get(Calendar.YEAR) - c.get(Calendar.YEAR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap getBlurBitmap(Context context, int resId) {
        try {
            Bitmap originBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            int scaleRatio = 10;
            int blurRadius = 8;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                    originBitmap.getWidth() / scaleRatio,
                    originBitmap.getHeight() / scaleRatio,
                    false);
            return FastBlurUtil.doBlur(scaledBitmap, blurRadius, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
