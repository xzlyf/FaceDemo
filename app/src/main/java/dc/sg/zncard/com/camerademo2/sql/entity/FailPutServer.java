package dc.sg.zncard.com.camerademo2.sql.entity;

import org.litepal.crud.DataSupport;

public class FailPutServer extends DataSupport {
    private String token;
    private String path;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
