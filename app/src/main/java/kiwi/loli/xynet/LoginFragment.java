package kiwi.loli.xynet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_login, container, false);
        FragmentActivity activity=getActivity();
        TextInputEditText textInputEditText0=view.findViewById(R.id.username_et);
        TextInputEditText textInputEditText1=view.findViewById(R.id.password_et);
        Spinner isp_sp=view.findViewById(R.id.isp_sp);
        NavHostFragment navHostFragment=(NavHostFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController=navHostFragment.getNavController();

        Button button=view.findViewById(R.id.sub_password_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=textInputEditText0.getText().toString();
                String password=textInputEditText1.getText().toString();
                String isp=isp_sp.getSelectedItem().toString();
                if (!username.equals("") && !password.equals("") && !isp.equals("未选择")){
                    switch (isp){
                        case "移动":
                            isp="cmcc";
                            break;
                        case "电信":
                            isp="telecom";
                            break;
                        case "联通":
                            isp="unicom";
                            break;
                    }
                    String login_url="http://10.1.99.100:801/eportal/portal/login?callback=dr1003&login_method=1&user_account=%2C0%2C"+username+"%40"+isp+"&user_password="+password;
                    SharedPreferences account=activity.getSharedPreferences("account",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=account.edit();
                    editor.putString("username",username);editor.putString("login_url",login_url);
                    editor.commit();
                    Toast.makeText(activity, "账号保存成功", Toast.LENGTH_SHORT).show();


                    navController.navigate(R.id.action_loginFragment_to_settingFragment);
                   // Log.e("ooo",login_url);
                }else {
                    Toast.makeText(activity, "不合法的输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button back_btn=view.findViewById(R.id.back_to_home);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_loginFragment_to_homeFragment);
            }
        });
        //关闭抽屉

        DrawerLayout drawerLayout=activity.findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(Gravity.LEFT);
        return view;
    }
}