package dc.sg.zncard.com.camerademo2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

public class CameraInfo extends Camera.CameraInfo {

    /**
     * 获取相机对象
     * @return
     * @throws Exception
     */
    public Camera openCamera() throws Exception{
        return Camera.open();
    }

    /**
     * 检测相机是否可用
     * @param context
     * @return
     */
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }
}
