package com.example.imagelookpractice;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FAIL = 2;
    private static final int REQUEST_EXCEPTION = 3;
    private static final int REQUEST_SUCCED = 1;
    private EditText etPath;

//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what) {
//                case REQUEST_SUCCED:
//                    Bitmap bitmap = (Bitmap) msg.obj;
//                    iv.setImageBitmap(bitmap);
//                    break;
//                case REQUEST_FAIL:
//                    Toast.makeText(getApplicationContext(), "連線失敗", Toast.LENGTH_SHORT).show();
//                case REQUEST_EXCEPTION:
//                    Toast.makeText(getApplicationContext(), "忙碌中 請稍後", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPath = findViewById(R.id.et_path);
        iv = findViewById(R.id.iv);

    }

    public void find(View view) {
        new Thread() {
            @Override
            public void run() {
                //[1]取得路徑
                String path = etPath.getText().toString().trim();
                //[2]緩存並且加密檔案
                File file = new File(getCacheDir(), Base64.encodeToString(path.getBytes(), Base64.DEFAULT));

                if (file.exists()) {
                    //[3]從緩存裡取圖片
                    System.out.println("使用緩存");
                    final Bitmap Cachebitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageBitmap(Cachebitmap);
                            }
                        });
//                    Message msg = Message.obtain();
//                    msg.obj = Cachebitmap;
//                    handler.sendMessage(msg);

                } else {
                    //[4]從網路上抓圖片
                    try {
                        System.out.println("第一次網路");
                        URL url = new URL(path);
                        HttpURLConnection coon = (HttpURLConnection) url.openConnection();
                        coon.setRequestMethod("GET");
                        coon.setConnectTimeout(5000);
                        int code = coon.getResponseCode();
                        if (code == 200) {
                            InputStream is = coon.getInputStream();

                            FileOutputStream fos = new FileOutputStream(file);

                            int len = -1;
                            byte[] buffer = new byte[1024];
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }

                            fos.close();
                            is.close();

                            final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv.setImageBitmap(bitmap);
                                }
                            });


//                            Message msg = Message.obtain();
//                            msg.obj = bitmap;
//                            msg.what = REQUEST_SUCCED;
//                            handler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = REQUEST_FAIL;
//                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        Message msg = Message.obtain();
                        msg.what = REQUEST_EXCEPTION;
//                        handler.sendMessage(msg);
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }
}
