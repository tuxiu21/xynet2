package kiwi.loli.xynet;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;


import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class SettingFragment extends PreferenceFragmentCompat {


    Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();

        Preference last_failure=findPreference("last_failure");
        last_failure.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                now_state();
                return true;
            }
        });
        Preference now_task=findPreference("now_task");
        now_task.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                now_state();
                return true;
            }
        });



    }

    void now_state(){
        WorkQuery workQuery=WorkQuery.Builder
                .fromStates(Arrays.asList(WorkInfo.State.ENQUEUED,WorkInfo.State.RUNNING))
                .build();
        ListenableFuture<List<WorkInfo>> workInfos= WorkManager.getInstance().getWorkInfos(workQuery);
        try {
            List<WorkInfo> workInfoList=workInfos.get();

            View view= View.inflate(context,R.layout.situation_dialog,null);
            TextView number_task= view.findViewById(R.id.number_task);
            String tags = "";
            for (WorkInfo info :
            workInfoList) {
                tags=tags.concat(info.getTags() +";");
            }
            if(workInfoList.size()==0){
                number_task.setText("暂无正在运行的任务");
            }else {
                number_task.setText(String.valueOf(workInfoList.size())+"个  Tags:"+tags);
            }

            TextView last_failure= view.findViewById(R.id.last_failure);
            last_failure.setText(context.getSharedPreferences("last_failure",0).getString("reason","暂时没有问题"));
            new AlertDialog.Builder(context)
                    .setView(view)
                    .show();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }
}