package com.android.mobilesocietynetwork.client.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import Decoder.BASE64Decoder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

public class ImgHelper {
	/**
     * 将Bitmap转换成字符串
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 50, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
    
    /**
     * TODO:将以Base64方式编码的字符串解码为byte数组
     * @param encodeStr 待解码的字符串
     * @return 解码后的byte数组
     * @throws IOException 
     * */
    public static byte[] decode(String encodeStr) throws IOException{
        byte[] bt = null;  
        BASE64Decoder decoder = new BASE64Decoder();  
        bt = decoder.decodeBuffer(encodeStr);
        return bt;
    }
    
    /**
     * 把byte数组转化成 bitmap对象
     * @param b
     * @return
     */
    public static Bitmap bytesToBitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
}
