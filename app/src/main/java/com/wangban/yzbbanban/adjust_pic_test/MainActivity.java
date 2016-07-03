package com.wangban.yzbbanban.adjust_pic_test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import android.app.WallpaperManager;

import com.wangban.yzbbanban.adjust_pic_test.ui.DoubleScaleImageView;
import com.wangban.yzbbanban.adjust_pic_test.ui.TouchImageView;
import com.wangban.yzbbanban.adjust_pic_test.ui.ZoomImageView;

public class MainActivity extends Activity {

    private DoubleScaleImageView imgview;
    private TouchImageView tiv;
    private ImageView iv;
    private String TAG = "supergirl";
    private ZoomImageView ziv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WallpaperManager wpManager =WallpaperManager.getInstance(this);



        //imgview = (DoubleScaleImageView) findViewById(R.id.iv_test_pic);
        tiv = (TouchImageView) findViewById(R.id.iv_test2);
        //imgview.setImageResource(R.drawable.test_picture);
        ziv= (ZoomImageView) findViewById(R.id.iv_test_pic);
//        imgview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "这是单击", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "onClick: 单击");
//            }
//        });
//        imgview.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(MainActivity.this, "这是长按", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, "onClick: 长按");
//
//                return false;
//            }
//        });
        ziv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "这是单击", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: 单击");
            }
        });
        ziv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "这是长按", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: 长按");

                return false;
            }
        });
        tiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "这是单击", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: 单击");
            }
        });
        tiv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "这是长按", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: 长按");

                return false;
            }
        });


    }


}
