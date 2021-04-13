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
                    messageGet("战斗结束！！！");
                    messageGet(my[0] <= 0 ? "战斗失败！" : "战斗胜利！");

                    //战斗胜利
                    if (monster[0] <= 0){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.qianghua)
                            .setTitle("第"+level[0]+"关战斗胜利，选择强化属性")
                            .setMessage("1.自身血量增加1000    2.自身攻击增加50")
                            .setNeutralButton("取消 - 观看数据",null)
                            .setPositiveButton("血量", (dialog, which) -> {
                                resetDate(new int[]{1000, 0,1,1});
                                h.postDelayed(this,0);
                                level[0]++;
                            })
                            .setNegativeButton("攻击", (dialog, which) -> {
                                resetDate(new int[]{0, 50,1,1});
                                h.postDelayed(this,0);
                                level[0]++;
                            })
                            .show();
                    }

                    //战斗失败
                    if (my[0] <= 0){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.dao)
                            .setTitle("第"+level[0]+"关战斗失败")
                            .setMessage("还要搏一搏吗，说不定变帅了！！！")
                            .setNeutralButton("取消 - 查看数据",null)
                            .setPositiveButton("重开关卡", (dialog, which) -> {
                                resetDate(new int[]{0, 0,0,0});
                                h.postDelayed(this,0);
                            })
                            .setNegativeButton("游戏重开", (dialog, which) -> {
                                finish();
                                startActivity(getIntent());
                            })
                            .show();
                    }
                }

                //渲染当前数据 and 血量渲染设置
                myLife.setTextColor(setColor(my[0],0));
                myBlood.setProgressTintList(ColorStateList.valueOf(setColor(my[0],0)));
                myLife.setText("生命值："+my[0]+"    攻击力："+my[1]);

                monsterLife.setTextColor(setColor(monster[0],1));
                monsterBlood.setProgressTintList(ColorStateList.valueOf(setColor(monster[0],1)));
                monsterLife.setText("生命值："+monster[0]+"    攻击力："+monster[1]);
            }
        };

        //开局提示
        AlertDialog ad = new AlertDialog.Builder(this)
                .setIcon(R.drawable.dao)
                .setTitle("回合制战斗模拟器")
                .setMessage("你一刀我一刀，伤害全看脸！")
                .setCancelable(false)
                .setView(Wv)
                .setPositiveButton("开始战斗",
                    (dialog, which) -> {
                        messageGet("战斗开始！！！");
                        h.postDelayed(runnable, 0);
                    }).create();

        messageGet("启动设备！！！");
        ad.show();

        //游戏继续
        cBtn.setOnClickListener((view)->{
            if (!fight()){
                h.postDelayed(runnable,0);
            }
        });
    }

    //血量颜色反馈
    public int setColor(int item, int i){
        return item > cs[i]*0.8? Color.rgb(0,255,0) :
                item < cs[i]*0.8 && item > cs[i]*0.3 ? Color.rgb(255,255,0) :
                        Color.rgb(255,0,0);
    }

    //人物受伤动作
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

    //重置血量 and 提升血量战斗力计算
    public void resetDate(int[] arr){
        fightDate.removeAllViews();
        my[0] = cs[0]+arr[0];
        cs[0] = my[0];
        my[1] = my[1]+arr[1];

        monster[0] = cs[1]+(500*arr[2]);
        cs[1] = monster[0];
        monster[1] = monster[1]+(20*arr[3]);

        messageGet("战斗开始！！！");
        newHp();
        fight();
    }

    public void newHp(){
        myBlood.setMax(cs[0]);
        myBlood.setProgress(cs[0]);
        monsterBlood.setMax(cs[1]);
        monsterBlood.setProgress(cs[1]);
    }

    //攻击轮换 and 胜负判断
    public boolean fight(){
        if (sw && monster[0] > 0 && my[0] > 0){
            bruise(monsterImg);
            messageGet("自身输出伤害："+myHurt()+"  怪物剩余血量："+monster[0]);
            sw = false;
        }else if (sw == false && my[0] > 0 && monster[0] > 0){
            bruise(myImg);
            messageGet("怪物输出伤害："+monsterHurt()+"  自身剩余血量："+my[0]);
            sw = true;
        }
        return my[0] <= 0 || monster[0] <= 0 ? false : true;
    }

    //自身输出伤害计算 血量条变化
    public int myHurt(){
        Random ran = new Random();
        int hurt = ran.nextInt(my[1]);
        monster[0] = monster[0]-hurt;
        monsterBlood.setProgress(monsterBlood.getProgress()-hurt);
        double dg = monsterBlood.getWidth()*((hurt+0.0)/cs[1]);
        return hurt;
    }

    //怪物输出伤害计算 血量条变化
    public int monsterHurt(){
        Random ran = new Random();
        int hurt = ran.nextInt(monster[1]);
        my[0] = my[0]-hurt;
        double dg = myBlood.getWidth()*((hurt+0.0)/cs[0]);
        myBlood.setProgress(myBlood.getProgress()-hurt);
        return hurt;
    }

    //战斗数据信息渲染
    public void messageGet(String s){
        TextView tv = new TextView(this);
        tv.setText(getTime()+": "+s);
        tv.setTextColor(Color.rgb(0,255,0));
        fightDate.addView(tv,0);
    }

    //获取时间
    private String getTime() {
        Date date = new Date();
        return date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
}