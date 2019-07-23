package dc.sg.zncard.com.camerademo2.entity;

import java.util.List;

public class FaceToken {

    /**
     * image_id : Dd2xUw9S/7yjr0oDHHSL/Q==
     * request_id : 1470472868,dacf2ff1-ea45-4842-9c07-6e8418cea78b
     * time_used : 752
     * faces : [{"landmark":{"mouth_upper_lip_left_contour2":{"y":185,"x":146},"contour_chin":{"y":231,"x":137},"right_eye_pupil":{"y":146,"x":205},"mouth_upper_lip_bottom":{"y":195,"x":159}},"attributes":{"gender":{"value":"Female"},"age":{"value":21},"glass":{"value":"None"},"headpose":{"yaw_angle":-26.625063,"pitch_angle":12.921974,"roll_angle":22.814377},"smile":{"threshold":30.1,"value":2.566890001296997}},"face_rectangle":{"width":140,"top":89,"left":104,"height":141},"face_token":"ed319e807e039ae669a4d1af0922a0c8"}]
     */

    private String image_id;
    private String request_id;
    private int time_used;
    private java.util.List<FacesBean> faces;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public List<FacesBean> getFaces() {
        return faces;
    }

    public void setFaces(List<FacesBean> faces) {
        this.faces = faces;
    }

    public static class FacesBean{
        private String face_token;

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

    }
}
