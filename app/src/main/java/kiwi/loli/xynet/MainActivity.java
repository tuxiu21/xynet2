package kiwi.loli.xynet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    NavController navController; //其他地方可能会用到navcontroller 故如此声明
    AppBarConfiguration appBarConfiguration;
    SharedPreferences account;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNav();
        account=getSharedPreferences("account",0);
        set_avatar();

//        配置dialog
        boolean isFirst=account.getBoolean("isFirst",false);
        if (!isFirst) {
            new AlertDialog.Builder(this)
                    .setTitle("请先打开相关权限")
                    .setMessage("完全允许本应用在后台运行\n请找到所有的于后台相关的设置项\n包括网络、电池等等\n并取消所有限制\n否则本应用将无法正常运行！")
                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = account.edit();
                            editor.putBoolean("isFirst", true);
                            editor.commit();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", "kiwi.loli.xynet", null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if(!account.getBoolean("isFirst",false)){
                                finish();
                            }
                        }
                    }).show();
        }

//        配置drawerlayout
        drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);

        NavController.OnDestinationChangedListener setting_listener=new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId()==R.id.settingFragment){
                    if(account.getString("username","").equals("")) {
                        Toast.makeText(MainActivity.this, "请先登入！", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_settingFragment_to_loginFragment);
                    }else {
                        set_avatar();
                    }
                }
                if(navDestination.getId()==R.id.logoutFragment){
                    navController.navigate(R.id.action_logoutFragment_to_homeFragment);

//                    获得开关状态 阻止登出
                    SwitchCompat run_sw=findViewById(R.id.run_sw);
                    if(run_sw.isChecked()){
                        Toast.makeText(MainActivity.this, "请先关闭服务开关！", Toast.LENGTH_SHORT).show();

                        drawerLayout.closeDrawer(GravityCompat.START);

                    }else {
                        SharedPreferences.Editor editor=account.edit();
                        editor.remove("username");editor.commit();
                        set_avatar();
                        Toast.makeText(MainActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        };
        navController.addOnDestinationChangedListener(setting_listener);

    }



    void initNav(){
        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);//获取drawerlayout
        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView); //获取hostfragment

        if (navHostFragment != null) {
            navController=navHostFragment.getNavController(); //获得控制器：用来控制
        }
        appBarConfiguration=new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawerLayout)  //drawerlayout 用途在此！
                .build();  //把bar和drawerlayout 以及navcontroller绑定起来！

        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView,navController); //总之就是绑定！
    }

    void set_avatar(){
        View head_view=navigationView.getHeaderView(0);
        TextView textView_nav=head_view.findViewById(R.id.tv_nav);
        ImageView imageView_nav=head_view.findViewById(R.id.iv_nav);
        String username=account.getString("username","");
        if (username.equals("")){
            textView_nav.setText("未登入");
            imageView_nav.setImageResource(R.mipmap.not_log);
            return;
        }
        textView_nav.setText(username);
        imageView_nav.setImageResource(R.mipmap.logined);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,appBarConfiguration) || super.onSupportNavigateUp();
    }


}