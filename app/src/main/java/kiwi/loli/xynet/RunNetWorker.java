package kiwi.loli.xynet;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RunNetWorker extends Worker {
    Context context;
    GetTime getTime;
    WifiManager wifiManager;
    String login_url;

//    有关错误：
    SharedPreferences last_failure;
    SharedPreferences.Editor editor;

    public RunNetWorker(Context context, WorkerParameters workerParameters){
        super(context,workerParameters);
        this.context=context;
        getTime=new GetTime(context);
        wifiManager=getWifiManager(context);

        SharedPreferences account=context.getSharedPreferences("account",0);
        login_url=account.getString("login_url","");

        last_failure=context.getSharedPreferences("last_failure",0);
        editor=last_failure.edit();
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.e("ooo","start_work!");
        //判断wifi是否连接
        if(!wifiManager.isWifiEnabled()){
            Log.e("ooo","err00");
            editor.putString("reason","错误代码[00]:WiFi未打开。");
            editor.commit();
            return Result.retry();
        }
//        登入！
        Http http0=new Http();
        String login_res=http0.login(login_url);
        Log.e("ooo",login_res);
        if (login_res.equals("login_error")){
            Log.e("ooo","err01");
            editor.putString("reason","错误代码[01]:访问登入网失败。");
            editor.commit();
            return Result.retry();
        }
        //检测是否连上网
        if (!isNetworkOnline()){
            Log.e("ooo","err02");
            editor.putString("reason","错误代码[02]:"+"校园网认证错误。\n"+login_res);
            editor.commit();
            return Result.retry();
        }else {
            Log.e("ooo","success");
            editor.remove("reason");
            editor.commit();

            skip_work();

            return Result.success();
        }


    }


    void skip_work(){


        WorkManager.getInstance(context).cancelAllWork();
        Long diff=getTime.time_diff_next_work();//计算到明天6点的时间
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) //为wifi
                .build();
        OneTimeWorkRequest run_net=new OneTimeWorkRequest.Builder(RunNetWorker.class)
                .setInitialDelay(diff,TimeUnit.MILLISECONDS)
                .addTag("next_wifi_work")
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(run_net);
    }

    public static WifiManager getWifiManager(Context context) {
        return context == null ? null : (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
    public boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Log.i("Avalible", "Process:"+exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
