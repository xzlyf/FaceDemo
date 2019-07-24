package dc.sg.zncard.com.camerademo2.sql;

import org.litepal.crud.DataSupport;

import java.util.List;

import dc.sg.zncard.com.camerademo2.sql.entity.TokenAndImage;

/**
 * 数据库增删改查工具类
 */
public class LitePalUtils {


    public static void saveData(String path, String token) {
        TokenAndImage t = new TokenAndImage();
        t.setToken(token);
        t.setPath(path);
        t.save();
    }

    /**
     * 删除所有数据
     */
    public static void clean() {
        DataSupport.deleteAll(TokenAndImage.class);
    }

    /**
     * 根据token返回本地路径
     *
     * @param token
     * @return
     */
    public static String searchData(String token) {
        List<TokenAndImage> tokens = DataSupport.where("token=?",token ).find(TokenAndImage.class);
        return tokens.get(0).getPath();
    }

}
