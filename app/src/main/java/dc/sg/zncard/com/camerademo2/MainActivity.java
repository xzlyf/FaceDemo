package dc.sg.zncard.com.camerademo2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dc.sg.zncard.com.camerademo2.constant.Local;
import dc.sg.zncard.com.camerademo2.presenter.FacePresenter;
import dc.sg.zncard.com.camerademo2.utils.ImageUtil;
import dc.sg.zncard.com.camerademo2.view.IView;

/**
 * 依据 android.hardware.Camera;来开发
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, IView {

    private Camera camera;
    private CameraPreview preview;
    private FrameLayout preview_layout;
    private Button capture;
    private CameraInfo info;
    private Button check_photo;
    private ImageView target;
    private EditText imageInfo;
    private TextView target_msg;
    private FacePresenter presenter;

    @Override
    protected void onResume() {
        super.onResume();

        openCamera();

    }

    /**
     * 打开相机
     */
    private void openCamera() {
        //相机信息
        try {
            //获取相机对象
            camera = info.openCamera();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "相机实例获取异常", Toast.LENGTH_SHORT).show();
        }
        preview = new CameraPreview(this, camera);
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> sizes =parameters.getSupportedPreviewSizes();
        for(int i = 0 ;i<sizes.size();i++){
            Log.d("xz", "支持的分辨率:: "+sizes.get(i).width+":"+sizes.get(i).height);
        }
        Camera.Size previewSize = parameters.getPreviewSize();    // 当前 Camera 分辨率
        //添加至视图
        preview_layout.addView(preview, new FrameLayout.LayoutParams(previewSize.width,previewSize.height, Gravity.START));


    }

    @Override
    protected void onPause() {
        super.onPause();

        closeCamera();
    }

    /**
     * 关闭相机
     */
    private void closeCamera() {
        preview.releaseCamera();
        preview_layout.removeView(preview);
        preview = null;
        camera.release();
        camera = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取权限
        getPermisson();

        //初始化
        initView();

        info = new CameraInfo();

    }

    private String photoPath = "";

    /**
     * 初始化view
     */
    private void initView() {
        //相机信息类
        info = new CameraInfo();

        preview_layout = findViewById(R.id.camera_preview);
        capture = findViewById(R.id.capture);
        target_msg = findViewById(R.id.target_msg);
        check_photo = findViewById(R.id.check_photo);
        capture.setOnClickListener(this);
        check_photo.setOnClickListener(this);
        target = findViewById(R.id.target_image);
        imageInfo = findViewById(R.id.image_info);

        presenter = new FacePresenter(this);

    }

    /**
     * 回调接口用来供应从一张照片捕捉图像数据。
     */
    private Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            closeCamera();
            //保存图片操作
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            //开始匹配相似图片
            startComparison(bmp);
            //路径及文件名
            String fileName = Environment.getExternalStorageDirectory()
                    + File.separator
                    + "CameraDemo"
                    + File.separator
                    + "CameraDemo" + System.currentTimeMillis() + ".jpg";
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();//创建文件夹
            }

            //写出操作
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);//向缓冲区压缩图片
                bos.flush();
                bos.close();

                //显示按钮控件
                photoPath = fileName;
                openCamera();
//                Toast.makeText(MainActivity.this, "拍照成功，照片保存在" + fileName + "文件之中！", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                //e.printStackTrace();
//                Toast.makeText(MainActivity.this, "拍照失败！" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };


    /**
     * 遍历本地图片库
     *
     * @param bmp
     */
    public void startComparison(final Bitmap bmp) {
        //恢复标识
        Local.isover = false;
        default_num =1;
        final String fileName = Environment.getExternalStorageDirectory()
                + File.separator
                + "CameraDemo"
                + File.separator
                + "ImageLib";
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(fileName);
                File[] filelist = file.listFiles();
                for (int i = 0; i < filelist.length; i++) {

                    presenter.getImageInfo(ImageUtil.Bitmap2StrByBase64(bmp),
                            ImageUtil.lcoalImage2Base64(filelist[i].toString()), filelist[i].toString());
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Local.isover = true;
            }
        }).start();
    }


    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意
                    Toast.makeText(MainActivity.this, "相机已授权", Toast.LENGTH_SHORT).show();
                } else {
                    //不同意
                    Toast.makeText(MainActivity.this, "相机权限未获取，请前往设置手动打开", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意
                    Toast.makeText(MainActivity.this, "存储已授权", Toast.LENGTH_SHORT).show();
                } else {
                    //不同意
                    Toast.makeText(MainActivity.this, "存储权限未获取，请前往设置手动打开", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;


        }
    }

    /**
     * 获取权限
     */
    public void getPermisson() {
        //andoird 6.0动态获取权限
        if (Build.VERSION.SDK_INT >= 23) {
            //判断用户是否授权权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //开始授权
                ActivityCompat
                        .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

            }
            //判断用户是否授权权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //开始授权
                ActivityCompat
                        .requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

            }


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capture:
                //清空数据
                imageInfo.setText("");
                map.clear();

                //捕获照片
                camera.takePicture(null, null, callback);
                break;
            case R.id.check_photo:

//                Toast.makeText(this, ""+photoPath, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                startActivity(intent);
                break;
        }
    }


    private int default_num = 1;
    @Override
    public void showInfo(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageInfo.append("正在匹配第"+default_num+"张,相似度"+msg + "\n");
                default_num++;
            }
        });
    }

    Map<Double, String> map = new HashMap<>();

    @Override
    public void addList(Double confidence, String path) {
        map.put(confidence, path);
    }

    private void showTargetInfo(final Double d, final String photoPath) {
        final String realPath = "file://" + photoPath;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(Uri.parse(realPath)).into(target);
                target_msg.setText("相似度:"+d+'\n');

                if (d>80){
                    target_msg.append("是同一个人");
                }else if (d>=60){
                    target_msg.append("可能是同一个人");
                }else if (d<60){
                    target_msg.append("不是同一个人");

                }
            }
        });
    }

    public void showSimPho() {

        double d = 0;
        ///找到集合map最大的key
        for (Double key : map.keySet()) {

            if (key>d){
                d = key;
            }

        }
        showTargetInfo(d,map.get(d));

    }
}
