package com.lz69.tageditview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText etTagInput;

    private ImageView ivShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {
//        Bitmap bitmap = Bitmap.createBitmap(140, 80, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);
//        canvas.drawRoundRect(new RectF(0,0,140,80),20,20,paint);
////        canvas.drawRect(0,0,140,80,paint);
//        paint.setColor(Color.WHITE);
//        paint.setTextSize(50);
//        canvas.drawText("哈哈", 20, 60, paint);
        Bitmap bitmap = getBitmap();
        ivShow = (ImageView) findViewById(R.id.ivShow);
        ivShow.setImageBitmap(bitmap);
//        saveBitmap(bitmap);
    }

    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(dip2px(40 * 2 + 80), dip2px(100), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#22bd7a"));
        canvas.drawRoundRect(new RectF(dip2px(10),dip2px(10),dip2px(40 * 2 + 70),dip2px(90)),dip2px(50),dip2px(50),paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(dip2px(40));
        canvas.drawText("哈哈", dip2px(40), dip2px(65), paint);
        return bitmap;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void saveBitmap(Bitmap bitmap) {
        String path = getFilesDir() +"/revoeye/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + "aa.jpeg");
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
