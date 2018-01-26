package com.vendor.social.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 将res转换
 * Created by ljfan on 2017/8/18.
 */
public class ResConvert {

    /**
     * res目录下面的一张图片转存bitmap
     * @param context  context
     * @param id  图片的id
     */
    public static Bitmap resToBitmap(Context context, int id) {
        //使用BitmapFactory把res下的图片转换成Bitmap对象
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * res目录下面的一张图片保存到本地
     * @param context  context
     * @param id  图片的id
     */
    public static String resToFile(Context context, int id) {
        // getFilesDir().getAbsolutePath()+"/image"\
        //在本地创建一个文件夹
        File file = new File(context.getFilesDir() + "/" + id +".png");
        // File absoluteFile = getFilesDir().getAbsoluteFile();
        //判断本地是否存在，防止每次启动App都要创建
        if (!file.exists()) {
            //使用BitmapFactory把res下的图片转换成Bitmap对象
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
            FileOutputStream fos = null;
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //获得一个可写的输入流
                fos = new FileOutputStream(file);
                fos.write(stream.toByteArray());
                fos.close();
                //使用图片压缩对图片进行处理  压缩的格式  可以是JPEG、PNG、WEBP
                //第二个参数是图片的压缩比例，第三个参数是写入流
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return file.getPath();
    }
}
