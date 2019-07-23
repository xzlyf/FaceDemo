package dc.sg.zncard.com.camerademo2.presenter;

import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import dc.sg.zncard.com.camerademo2.MainActivity;
import dc.sg.zncard.com.camerademo2.constant.Local;
import dc.sg.zncard.com.camerademo2.entity.Compare;
import dc.sg.zncard.com.camerademo2.model.Model;
import dc.sg.zncard.com.camerademo2.utils.SignMD5Util;
import dc.sg.zncard.com.camerademo2.utils.StringUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FacePresenter {
    private Model model;
    private MainActivity view;
    final String TAG = "xztest";


    public FacePresenter(MainActivity view) {
        this.view = view;
        model = new Model();
    }

    public void TencentAI(){
        String a = StringUtils.getRandomString(16);
        String b = System.currentTimeMillis()+"";


        String path = "https://api.ai.qq.com/fcgi-bin/face/face_detectface";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("app_id", "2118424516")
                .add("time_stamp",b )
                .add("nonce_str", a)
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(path)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String data = call.toString();
                Log.d(TAG, "onFailure: " + data);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                Log.d(TAG, "onResponse: " + data);

                JSONObject obj = null;

            }
        });
    }

    /**
     * 获取
     */
    public void getImageInfo(String people1, String people2, final String Localpath) {

        final String path = "https://api-cn.faceplusplus.com/facepp/v3/compare";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("api_key", "r8Kq1W8OGefvVE7-C1u1RysBuKfINlE7")
                .add("api_secret", "phLWOTXum-DKjRH02AnjSOa-woUG6tTH")
                .add("image_base64_1",people1)
                .add("image_base64_2",people2)

                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(path)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String data = call.toString();
                Log.d(TAG, "onFailure: " + data);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
//                Log.d(TAG, "onResponse: " + data);

                JSONObject obj = null;

                try {
                    obj = new JSONObject(data);
                    Gson gson = new Gson();
                    Compare compare = gson.fromJson(obj.toString(),Compare.class);
                    if (compare.confidence != 0){
//                        Log.d(TAG, "相似度： "+compare.getConfidence());
                        view.showInfo(""+compare.getConfidence());
                        view.addList(compare.getConfidence(),Localpath);
                    }else{
                        //调用api失败
                        view.showInfo(obj.getString("error_message"));

                    }
                    //判断是否循环结束了
                    if (Local.isover){
                        //输出相似的照片
                        view.showSimPho();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
