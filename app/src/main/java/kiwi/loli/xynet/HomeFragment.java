package kiwi.loli.xynet;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


public class HomeFragment extends Fragment {
    FragmentActivity activity;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        activity=getActivity();
        SharedPreferences account=activity.getSharedPreferences("account",0);
        SharedPreferences.Editor editor=account.edit();

        SwitchCompat run_sw=view.findViewById(R.id.run_sw);
//        初始化开关状态
        run_sw.setChecked(account.getBoolean("isRun",false));

        run_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    if(!account.getString("username","").equals("")){
                        editor.putBoolean("isRun",true);
                        editor.commit();
                        start_service();
                    }else {
                        Toast.makeText(activity, "请先登入！", Toast.LENGTH_SHORT).show();
                        run_sw.setChecked(false);
                    }
                }else {
                    editor.putBoolean("isRun",false);
                    editor.commit();
                    WorkManager.getInstance(activity).cancelAllWork();
                }
            }
        });


        return view;
    }

    void start_service(){
        GetTime getTime=new GetTime(activity);
        Long diff=getTime.time_diff_first_work();//计算到明天6点的时间

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) //为wifi
                .build();
        OneTimeWorkRequest run_net=new OneTimeWorkRequest.Builder(RunNetWorker.class)
                .setInitialDelay(diff,TimeUnit.MILLISECONDS)
                .addTag("first_wifi_work")
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(activity).cancelAllWork();
        WorkManager.getInstance(activity).enqueue(run_net);

    }


}