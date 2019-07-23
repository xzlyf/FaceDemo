package dc.sg.zncard.com.camerademo2;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        NodinFaceDetectionListener listener = new NodinFaceDetectionListener();
        camera.setFaceDetectionListener(listener);
        //获取相机设置
        Camera.Parameters  param = camera.getParameters();
//        param.setPreviewFrameRate(24);//帧数
        //设置相机
        camera.setParameters(param);
    }
    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
//            camera.stopFaceDetection();//人脸识别开关
            camera.stopPreview();
            camera.lock();
            camera.release();
            camera = null;
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            //当他报异常代表该摄像头无法支持检测面容
            //invalid face detection type=0  表示支持识别人脸为0
//            camera.startFaceDetection();//人脸识别开关


        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "surfaceCreated: 相机预览异常",e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //当预览界面不存在
        if (surfaceHolder.getSurface() ==null){
            return;
        }
        try {
            camera.stopPreview();
        }catch (Exception e){
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
//            camera.startFaceDetection();//人脸识别开关

        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "surfaceCreated: 相机预览异常",e);

        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        try {
//            camera.stopPreview();
//            camera.release();
//        }catch (Exception e){
//            Log.w(TAG, "surfaceCreated: 相机释放异常",e);
//
//        }

    }
    class NodinFaceDetectionListener implements Camera.FaceDetectionListener {


        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (null == faces || faces.length == 0) {
                Log.d(TAG, "无人脸");
            } else {
                //Face face = faces[0];
                //Rect faceRect = face.rect;
                Log.d(TAG, "发现人脸");
            }
        }
    }
}
