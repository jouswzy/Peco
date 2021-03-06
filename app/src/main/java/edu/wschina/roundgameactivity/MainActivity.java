package edu.wschina.roundgameactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    int[] my = {1000,100},monster = {1500,50},level = {1},cs = {1000,1500};
    TextView monsterLife,monsterName,myName,myLife;
    ImageView myImg,monsterImg;
    LinearLayout fightDate;
    Button cBtn;
    WebView Wv;
    ProgressBar myBlood,monsterBlood;
    boolean sw = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        monsterLife = findViewById(R.id.monsterLife);
        monsterName = findViewById(R.id.monsterName);
        myName = findViewById(R.id.myName);
        myLife = findViewById(R.id.myLife);
        fightDate = findViewById(R.id.fightDate);
        cBtn = findViewById(R.id.cBtn);
        myImg = findViewById(R.id.myImg);
        monsterImg = findViewById(R.id.monsterImg);
        monsterBlood = findViewById(R.id.monsterBlood);
        myBlood = findViewById(R.id.myBlood);

        newHp();

        Wv = new WebView(this);
        Wv.getSettings().setJavaScriptEnabled(true);
        Wv.setBackgroundColor(Color.TRANSPARENT);
        Wv.loadUrl("file:///");


        final Handler h = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run(){

                if (fight()){
                    h.postDelayed(this,500);
                }else {
                    messageGet("?????????????????????");
                    messageGet(my[0] <= 0 ? "???????????????" : "???????????????");

                    //????????????
                    if (monster[0] <= 0){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.qianghua)
                            .setTitle("???"+level[0]+"????????????????????????????????????")
                            .setMessage("1.??????????????????1000    2.??????????????????50")
                            .setNeutralButton("?????? - ????????????",null)
                            .setPositiveButton("??????", (dialog, which) -> {
                                resetDate(new int[]{1000, 0,1,1});
                                h.postDelayed(this,0);
                                level[0]++;
                            })
                            .setNegativeButton("??????", (dialog, which) -> {
                                resetDate(new int[]{0, 50,1,1});
                                h.postDelayed(this,0);
                                level[0]++;
                            })
                            .show();
                    }

                    //????????????
                    if (my[0] <= 0){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.dao)
                            .setTitle("???"+level[0]+"???????????????")
                            .setMessage("????????????????????????????????????????????????")
                            .setNeutralButton("?????? - ????????????",null)
                            .setPositiveButton("????????????", (dialog, which) -> {
                                resetDate(new int[]{0, 0,0,0});
                                h.postDelayed(this,0);
                            })
                            .setNegativeButton("????????????", (dialog, which) -> {
                                finish();
                                startActivity(getIntent());
                            })
                            .show();
                    }
                }

                //?????????????????? and ??????????????????
                myLife.setTextColor(setColor(my[0],0));
                myBlood.setProgressTintList(ColorStateList.valueOf(setColor(my[0],0)));
                myLife.setText("????????????"+my[0]+"    ????????????"+my[1]);

                monsterLife.setTextColor(setColor(monster[0],1));
                monsterBlood.setProgressTintList(ColorStateList.valueOf(setColor(monster[0],1)));
                monsterLife.setText("????????????"+monster[0]+"    ????????????"+monster[1]);
            }
        };

        //????????????
        AlertDialog ad = new AlertDialog.Builder(this)
                .setIcon(R.drawable.dao)
                .setTitle("????????????????????????")
                .setMessage("???????????????????????????????????????")
                .setCancelable(false)
                .setView(Wv)
                .setPositiveButton("????????????",
                    (dialog, which) -> {
                        messageGet("?????????????????????");
                        h.postDelayed(runnable, 0);
                    }).create();

        messageGet("?????????????????????");
        ad.show();

        //????????????
        cBtn.setOnClickListener((view)->{
            if (!fight()){
                h.postDelayed(runnable,0);
            }
        });
    }

    //??????????????????
    public int setColor(int item, int i){
        return item > cs[i]*0.8? Color.rgb(0,255,0) :
                item < cs[i]*0.8 && item > cs[i]*0.3 ? Color.rgb(255,255,0) :
                        Color.rgb(255,0,0);
    }

    //??????????????????
    public void bruise(View iv){
        final int[] num1 = {1,0};
        iv.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv.setTranslationX(3*num1[0]);
                iv.setTranslationY(3*num1[0]);
                num1[0] *= -1;
                num1[1]++;
                if (num1[1] <= 25)
                    iv.postDelayed(this,17);
            }
        }, 0);
    }

    //???????????? and ???????????????????????????
    public void resetDate(int[] arr){
        fightDate.removeAllViews();
        my[0] = cs[0]+arr[0];
        cs[0] = my[0];
        my[1] = my[1]+arr[1];

        monster[0] = cs[1]+(500*arr[2]);
        cs[1] = monster[0];
        monster[1] = monster[1]+(20*arr[3]);

        messageGet("?????????????????????");
        newHp();
        fight();
    }

    public void newHp(){
        myBlood.setMax(cs[0]);
        myBlood.setProgress(cs[0]);
        monsterBlood.setMax(cs[1]);
        monsterBlood.setProgress(cs[1]);
    }

    //???????????? and ????????????
    public boolean fight(){
        if (sw && monster[0] > 0 && my[0] > 0){
            bruise(monsterImg);
            messageGet("?????????????????????"+myHurt()+"  ?????????????????????"+monster[0]);
            sw = false;
        }else if (sw == false && my[0] > 0 && monster[0] > 0){
            bruise(myImg);
            messageGet("?????????????????????"+monsterHurt()+"  ?????????????????????"+my[0]);
            sw = true;
        }
        return my[0] <= 0 || monster[0] <= 0 ? false : true;
    }

    //???????????????????????? ???????????????
    public int myHurt(){
        Random ran = new Random();
        int hurt = ran.nextInt(my[1]);
        monster[0] = monster[0]-hurt;
        monsterBlood.setProgress(monsterBlood.getProgress()-hurt);
        double dg = monsterBlood.getWidth()*((hurt+0.0)/cs[1]);
        return hurt;
    }

    //???????????????????????? ???????????????
    public int monsterHurt(){
        Random ran = new Random();
        int hurt = ran.nextInt(monster[1]);
        my[0] = my[0]-hurt;
        double dg = myBlood.getWidth()*((hurt+0.0)/cs[0]);
        myBlood.setProgress(myBlood.getProgress()-hurt);
        return hurt;
    }

    //????????????????????????
    public void messageGet(String s){
        TextView tv = new TextView(this);
        tv.setText(getTime()+": "+s);
        tv.setTextColor(Color.rgb(0,255,0));
        fightDate.addView(tv,0);
    }

    //????????????
    private String getTime() {
        Date date = new Date();
        return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
}