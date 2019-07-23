package dc.sg.zncard.com.camerademo2.model;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Model implements IModel{
    @Override
    public void getDataFromNet(final String url, final IModel.OnLoadCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

//
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url(url)
//                            .build();
//                    Response response = null;
//                    response = client.newCall(request).execute();
//                    StringUtils responseData = response.body().string();
//                    listener.success(responseData);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    listener.failed(e);
//                }
            }
        }).start();
    }
}
