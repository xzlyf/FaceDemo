package dc.sg.zncard.com.camerademo2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dc.sg.zncard.com.camerademo2.adapter.PhotoAdapter;
import dc.sg.zncard.com.camerademo2.entity.FaceSetInfo;
import dc.sg.zncard.com.camerademo2.entity.FaceToken;
import dc.sg.zncard.com.camerademo2.sql.LitePalUtils;
import dc.sg.zncard.com.camerademo2.utils.ImageUtil;
import dc.sg.zncard.com.camerademo2.utils.SharedPreferencesUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private final String outer_id = "suprexzlyf";//账号下全局唯一的 FaceSet 自定义标识，可以用来管理 FaceSet 对象。最
    private final String tags = "d1";//FaceSet 自定义标签组成的字符串，用来对 FaceSet 分组。
    private final String faceSec = "test_set_1"; //集合名称
    private String faceSetToken = "";//待返回的集合Token
    //默认人脸目录
    final String fileDir = Environment.getExternalStorageDirectory()
            + File.separator
            + "CameraDemo"
            + File.separator
            + "ImageLib";

    private final String TAG = "xz_FaceSet";

    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private TextView log;
    private TextView face_set_token;
    private TextView face_dir;
    private TextView face_token_total;
    private TextView fece_set_outer_id;
    private TextView fece_set_name;
    private TextView fece_set_tags;
    private ScrollView scroll_view;
    private RecyclerView recycler;
    private PhotoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        scroll_view = findViewById(R.id.scroll_aa);
        log = findViewById(R.id.net_log);
        face_set_token = findViewById(R.id.face_set_token);
        face_dir = findViewById(R.id.face_dir);
        face_token_total = findViewById(R.id.face_token_total);
        fece_set_outer_id = findViewById(R.id.fece_set_outer_id);
        fece_set_name = findViewById(R.id.fece_set_name);
        fece_set_tags = findViewById(R.id.fece_set_tags);
        btn_2.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);


        fece_set_outer_id.setText("唯一标识："+outer_id);
        fece_set_name.setText("集合名称："+faceSec);
        fece_set_tags.setText("组名："+tags);
        init_data();

    }

    private void init_data() {
        //读取上一次的集合Token
        face_set_token.setText(SharedPreferencesUtils.getdata(this, "null"));
        //暂时默认路径
        face_dir.setText(fileDir);
        init_Recycler();
    }

    private void init_Recycler() {
        recycler = findViewById(R.id.recycler_photo);
        adapter= new PhotoAdapter(this);
        recycler.setLayoutManager(new GridLayoutManager(this,2));
        recycler.setAdapter(adapter);

    }

    private Model model = new Model();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                model.getServerSet();//查看集合
                break;
            case R.id.btn_2:
                model.FaceSetCreate();//创建集合
                break;
            case R.id.btn_3:
                model.getFaceToken();//获取人脸token
                break;
            case R.id.btn_4:
                model.deleteAll();//删除所有数据
                break;
            case R.id.btn_5:

                break;
        }
    }

    public void setLog(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append(msg + '\n');
                scroll_view.fullScroll(ScrollView.FOCUS_DOWN);//自动滚动到底部
            }
        });
    }

    private void setTokenText(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                face_set_token.setText(msg);
            }
        });
    }

    private void setTokenTotal(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                face_token_total.setText("已注册人脸：" + msg);
            }
        });
    }
    private void showPhotoList(final List<String> tokenlist) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.refresh(tokenlist);

            }
        });

    }

    class Model extends Thread {
        private boolean isover = false;//标识
        private List<String> tokenList = new ArrayList<>();

        @Override
        public void run() {


        }

        /**
         * 从本地文件路径获取图片的token
         */
        public void getFaceToken() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isover = false;

                    File file = new File(fileDir);
                    File[] filelist = file.listFiles();
                    if (filelist.length == 0) {
                        setLog("文件夹为空！");
                        //空文件夹
                        return;
                    }
                    setLog("开始获取图片Token");
                    tokenList.clear();
                    LitePalUtils.clean();//删除库原有的数据，
                    for (int i = 0; i < filelist.length; i++) {
                        //开始获取照片Token
                        getFaceToken(filelist[i].getAbsolutePath(),ImageUtil.lcoalImage2Base64(filelist[i].getAbsolutePath()));
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    isover = true;
                }
            }).start();
        }

        /**
         * 获取照片的token
         */
        public void getFaceToken(final String imgPath,final String base64) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final String path = "https://api-cn.faceplusplus.com/facepp/v3/detect";

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();

                    FormBody formBody = new FormBody.Builder()
                            .add("api_key", "r8Kq1W8OGefvVE7-C1u1RysBuKfINlE7")
                            .add("api_secret", "phLWOTXum-DKjRH02AnjSOa-woUG6tTH")
                            .add("image_base64", base64)
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
                            setLog("URL连接失败");

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String data = response.body().string();
                            JSONObject obj = null;

                            try {
                                obj = new JSONObject(data);
                                //不等于null表示检测到人脸
                                try {
                                    obj.getString("image_id");
                                    Gson gson = new Gson();
                                    FaceToken token = gson.fromJson(obj.toString(), FaceToken.class);
                                    setLog("人脸token获取成功：" + token.getFaces().get(0).getFace_token());
                                    //加入集合待会循环加入人脸集合
                                    tokenList.add(token.getFaces().get(0).getFace_token());
                                    LitePalUtils.saveData(imgPath,token.getFaces().get(0).getFace_token());//存储数据
                                    //开始加入集合
//                            setFace2Set(token.getFaces().get(0).getFace_token());
                                } catch (JSONException e) {
                                    setLog("人脸token获取成功：" + obj.getString("error_message"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                setLog("解析失败");

                            }

                            //完成标识
                            if (isover) {
                                setLog("人脸token获取完成");
                                addToken2Set();//开始上传人脸数据
                                isover = false;
                            }

                        }
                    });
                }
            }).start();

        }


        /**
         * FaceSetCreate集合的创建
         */
        public void FaceSetCreate() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String path = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();

                    FormBody formBody = new FormBody.Builder()
                            .add("api_key", "r8Kq1W8OGefvVE7-C1u1RysBuKfINlE7")
                            .add("api_secret", "phLWOTXum-DKjRH02AnjSOa-woUG6tTH")
                            .add("display_name", faceSec)
                            .add("outer_id", outer_id)
                            .add("tags", tags)
                            .add("force_merge", "1")
                            .build();

                    Request request = new Request.Builder()
                            .post(formBody)
                            .url(path)
                            .build();

                    Call call = client.newCall(request);

                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            setLog("FaceSet创建连接失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String data = response.body().string();
                            JSONObject obj = null;

                            try {
                                obj = new JSONObject(data);

                                try {
                                    faceSetToken = obj.getString("faceset_token");
                                    setTokenText(obj.getString("faceset_token"));
                                    SharedPreferencesUtils.saveData(PhotoActivity.this, obj.getString("faceset_token"));//存入本地文件
                                    setLog("FaceSet集合创建成功：" + obj.getString("faceset_token"));

                                } catch (JSONException e) {
                                    setLog("FaceSet集合创建失败: " + obj.getString("error_message"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setLog("FaceSet创建解析失败");

                            }

                        }
                    });
                }
            }).start();

        }

        /**
         * 循环将列表的人脸token加入集合
         */
        public void addToken2Set() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setLog("开始向集合加入人脸");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "run: 集合大小"+tokenList.size());
                    for (int i = 0; i < tokenList.size(); i++) {
                        setFace2Set(tokenList.get(i));

                        try {
                            //防止api多线程警告
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        /**
         * 把token加入当前集合
         */
        public void setFace2Set(final String token) {
            final String path = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
            if (SharedPreferencesUtils.getdata(PhotoActivity.this, "null").equals("null")) {
                setLog("请确保当前集合正确且存在");
                return;
            }
            FormBody formBody = new FormBody.Builder()
                    .add("api_key", "r8Kq1W8OGefvVE7-C1u1RysBuKfINlE7")
                    .add("api_secret", "phLWOTXum-DKjRH02AnjSOa-woUG6tTH")
                    .add("faceset_token", SharedPreferencesUtils.getdata(PhotoActivity.this, "null"))
                    .add("face_tokens", token)
                    .build();

            Request request = new Request.Builder()
                    .post(formBody)
                    .url(path)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    setLog("加入集合连接失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String data = response.body().string();
                    JSONObject obj = null;

                    try {
                        obj = new JSONObject(data);

                        try {

                            setLog("已加入集合的token：" + token);
//                            setLog("已加入集合：" + obj.getString("faceset_token"));
                            setTokenTotal(obj.getString("face_count"));

                        } catch (JSONException e) {

                            try {
                                setLog("原因：" + obj.getString("failure_detail"));
                            } catch (JSONException e1) {
                                setLog("加入集合失败: " + obj.getString("error_message"));

                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setLog("加入集合解析失败");

                    }

                }
            });
        }

        /**
         * 删除当前集合所有人脸数据
         */
        public void deleteAll(){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final String path = "https://api-cn.faceplusplus.com/facepp/v3/faceset/removeface";

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();
                    if (SharedPreferencesUtils.getdata(PhotoActivity.this, "null").equals("null")) {
                        setLog("请确保当前集合正确且存在");
                        return;
                    }
                    FormBody formBody = new FormBody.Builder()
                            .add("api_key", "r8Kq1W8OGefvVE7-C1u1RysBuKfINlE7")
                            .add("api_secret", "phLWOTXum-DKjRH02AnjSOa-woUG6tTH")
                            .add("faceset_token", SharedPreferencesUtils.getdata(PhotoActivity.this, "null"))
                            .add("face_tokens", "RemoveAllFaceTokens")
                            .build();

                    Request request = new Request.Builder()
                            .post(formBody)
                            .url(path)
                            .build();

                    Call call = client.newCall(request);

                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            setLog("删除集合连接失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String data = response.body().string();
                            JSONObject obj = null;

                            try {
                                obj = new JSONObject(data);

                                try {
                                    setLog("已删除集合所有人脸数据：" +'\n'+ obj.getString("faceset_token"));
                                    LitePalUtils.clean();//删除本地数据数据，
                                    setTokenTotal(obj.getString("face_count"));

                                } catch (JSONException e) {

                                    try {
                                        setLog("原因：" + obj.getString("failure_detail"));
                                    } catch (JSONException e1) {
                                        setLog("删除集合失败: " + obj.getString("error_message"));

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setLog("删除集合解析失败");

                            }

                        }
                    });

                }
            }).start();

        }


        /**
         * 获取服务端所有人脸集合
         */
        public void getServerSet(){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final String path = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();
                    if (SharedPreferencesUtils.getdata(PhotoActivity.this, "null").equals("null")) {
                        setLog("请确保当前集合正确且存在");
                        return;
                    }
                    FormBody formBody = new FormBody.Builder()
                            .add("api_key", "r8Kq1W8OGefvVE7-C1u1RysBuKfINlE7")
                            .add("api_secret", "phLWOTXum-DKjRH02AnjSOa-woUG6tTH")
                            .add("faceset_token", SharedPreferencesUtils.getdata(PhotoActivity.this, "null"))
                            .build();

                    Request request = new Request.Builder()
                            .post(formBody)
                            .url(path)
                            .build();

                    Call call = client.newCall(request);

                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            setLog("集合查询链接失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String data = response.body().string();
                            JSONObject obj = null;
                            Log.d(TAG, "onResponse: "+data);
                            try {
                                obj = new JSONObject(data);
                                Gson gson = new Gson();
                                FaceSetInfo info = gson.fromJson(obj.toString(),FaceSetInfo.class);

                                if (info.face_tokens != null){

                                    List<String> tokenlist = new ArrayList<>();
                                    for(int i = 0;i<info.getFace_tokens().size();i++){
                                        tokenlist.add(info.getFace_tokens().get(i));
                                    }

                                    showPhotoList(tokenlist);
                                    setTokenTotal(obj.getString("face_count"));
                                }else{
                                    setLog("===当前集合无数据===");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                setLog("集合查询解析失败");

                            }

                        }
                    });

                }
            }).start();

        }


    }


}
