package dc.sg.zncard.com.camerademo2.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

    public static void saveData(Context context,String data){
        SharedPreferences sp = context.getSharedPreferences("face_set_token",
                Context.MODE_PRIVATE);
        sp.edit().putString("token", data).commit();
    }

    public static String getdata(Context context, String defValue){
        SharedPreferences sp = context.getSharedPreferences("face_set_token",
                Context.MODE_PRIVATE);
        return sp.getString("token",defValue);
    }
}
