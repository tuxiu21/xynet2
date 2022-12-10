package kiwi.loli.xynet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Http {
    CountDownLatch latch;
    OkHttpClient okHttpClient;
    Http(){
        latch=new CountDownLatch(1);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(6000, TimeUnit.MILLISECONDS)
                .readTimeout(6000, TimeUnit.MILLISECONDS)
                .writeTimeout(6000, TimeUnit.MILLISECONDS)
                .build();

    }
    String login(String login_url){

        Request request=new Request.Builder()
                .url(login_url)
                .build();
        final String[] res=new String[1];
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                res[0]="login_error";
                latch.countDown();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                res[0]=response.body().string();
                Log.e("000",res[0]);
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res[0];
    }

}
