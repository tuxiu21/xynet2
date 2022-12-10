package kiwi.loli.xynet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetTime {
    Calendar currentDate;
    Calendar dueDate;
    SimpleDateFormat simpleDateFormat;
    Context context;
    //    构造函数
    GetTime(Context context){
        this.context=context;
        refresh();
    }
    void refresh(){
        currentDate = Calendar.getInstance();
        dueDate = Calendar.getInstance();
        dueDate.set(Calendar.MINUTE,0);
        dueDate.set(Calendar.SECOND,0);
    }


    long time_diff_first_work(){
        refresh();
//        判断日期前后
        dueDate.set(Calendar.HOUR_OF_DAY,6);
        long diff=dueDate.getTimeInMillis()-currentDate.getTimeInMillis();
        if(diff<=0){
            return 0;
        }
        return diff;
    }
    long time_diff_next_work(){
        refresh();
//        判断日期前后
        dueDate.set(Calendar.HOUR_OF_DAY,6);
        long diff=dueDate.getTimeInMillis()-currentDate.getTimeInMillis();
        if(diff<=0){
            dueDate.add(Calendar.DATE,1);
            return dueDate.getTimeInMillis()-currentDate.getTimeInMillis();
        }
        return diff;
    }

}
