package com.example.imagelookpractice;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FAIL = 2;
    private static final int REQUEST_EXCEPTION = 3;
    private static final int REQUEST_SUCCED = 1;
    private EditText etPath;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case REQUEST_SUCCED:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv.setImageBitmap(bitmap);
                    break;
                case REQUEST_FAIL:
                    Toast.makeText(getApplicationContext(), "連線失敗", Toast.LENGTH_SHORT).show();
                case REQUEST_EXCEPTION:
                    Toast.makeText(getApplicationContext(), "忙碌中 請稍後", Toast.LENGTH_SHORT).show();
            }
        }
    };
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
                String path = etPath.getText().toString().trim();
                try {
                    URL url = new URL(path);
                    HttpURLConnection coon = (HttpURLConnection) url.openConnection();
                    coon.setRequestMethod("GET");
                    coon.setConnectTimeout(5000);
                    int code = coon.getResponseCode();
                    if (code == 200) {
                        InputStream is = coon.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        msg.what = REQUEST_SUCCED;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = REQUEST_FAIL;
                        handler.sendMessage(msg);
                    }


                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = REQUEST_EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }


            }
        }.start();


    }
}
