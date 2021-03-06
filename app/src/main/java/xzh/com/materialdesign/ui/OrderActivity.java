package xzh.com.materialdesign.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import xzh.com.materialdesign.R;
import xzh.com.materialdesign.api.ControlUser;
import xzh.com.materialdesign.base.BaseActivity;
import xzh.com.materialdesign.model.Credit;
import xzh.com.materialdesign.model.ModifyPerson;
import xzh.com.materialdesign.model.User;
import xzh.com.materialdesign.proxy.Command;
import xzh.com.materialdesign.proxy.StateCode;
import xzh.com.materialdesign.utils.ActivityHelper;

/**
 * Created by botan on 2017/4/22.
 */
public class OrderActivity extends BaseActivity {

    private Context mContext;
    TextView name,phone,btn_modify,btn_order,deliver_method,myCredit;
    EditText short_info,long_info,des,shop,time,money,deliver;
    RadioButton change_method;
    RadioGroup method;
    ImageView back;
    String selectMethod;
    JSONObject parameter;
    JSONObject parameterCredit;
    private ProgressDialog dialog;
    int code;
    Credit credit;
    Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v("tb","OrderActivity onCreate");
        mContext = OrderActivity.this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);
        ButterKnife.inject(this);

        btn_modify = (TextView)findViewById(R.id.order_layout_modify_custom);
        btn_order = (TextView) findViewById(R.id.order_layout_confirm);
        name = (TextView) findViewById(R.id.order_layout_name);
        phone = (TextView) findViewById(R.id.order_layout_custom_phone);
        short_info = (EditText) findViewById(R.id.order_layout_short_info);
        long_info = (EditText) findViewById(R.id.order_layout_long_info);
        des = (EditText) findViewById(R.id.order_layout_des);
        shop = (EditText) findViewById(R.id.order_layout_shop);
        time = (EditText) findViewById(R.id.order_layout_time);
        money = (EditText) findViewById(R.id.order_layout_money);
        change_method = (RadioButton) findViewById(R.id.order_layout_choose_cash);
        deliver = (EditText) findViewById(R.id.order_layout_deliver);
        deliver_method = (TextView) findViewById(R.id.order_layout_deliver_method);
        back = (ImageView) findViewById(R.id.back_icon);
        method = (RadioGroup) findViewById(R.id.order_layout_choose);
        myCredit = (TextView) findViewById(R.id.order_layout_deliver_credit);
        selectMethod = change_method.getText().toString();

        long_info.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        long_info.setGravity(Gravity.TOP);
        long_info.setSingleLine(false);
        long_info.setHorizontallyScrolling(false);

        init();

    }

    private void init() {
        getCredit();

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检测网络，无法检测wifi
                if (!checkNetwork()) {
                    Toast toast = Toast.makeText(mContext,"网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    if(short_info.getText().length() == 0 || long_info.getText().length() == 0 || time.getText().length() == 0 || money.getText().length() == 0 || deliver.getText().length() == 0){
                        new AlertDialog.Builder(mContext)

                                .setTitle("警告")

                                .setMessage("请将信息填写完整"+"！")

                                .setPositiveButton("确定", null)

                                .show();
                    }
                     else if(credit.getCredit() < Integer.parseInt(deliver.getText().toString()) && selectMethod.equals("1")){
                        new AlertDialog.Builder(mContext)

                                .setTitle("警告")

                                .setMessage("当前积分为"+credit.getCredit()+"！")

                                .setPositiveButton("确定", null)

                                .show();
                    }
                    else if(short_info.getText().length() > 8){
                         new AlertDialog.Builder(mContext)

                                 .setTitle("警告")

                                 .setMessage("订单概述过长"+"！")

                                 .setPositiveButton("确定", null)

                                 .show();
                     }
                     else if(des.getText().length() == 0 ){
                         new AlertDialog.Builder(mContext)

                                 .setTitle("警告")

                                 .setMessage("请输入收货地址"+"！")

                                 .setPositiveButton("确定", null)

                                 .show();
                     }
                     else if(long_info.getText().length() > 20){
                         new AlertDialog.Builder(mContext)

                                 .setTitle("警告")

                                 .setMessage("订单详情过长"+"！")

                                 .setPositiveButton("确定", null)

                                 .show();
                     }
                    else {
                         //if(check())
                         Order();
                     }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group,int checkedId){
                selectRadioBtn();
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(OrderActivity.this,ModifyActivity.class);

            }
        });

        intent = getIntent();
        User user = (User) intent.getSerializableExtra("userInfo");
        name.setText(user.getNickname());
        phone.setText(user.getPhoneNum());

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(StateCode.BROAD_CHANGENAME);
        myIntentFilter.addAction(StateCode.BROAD_CHANGEPHONE);


        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        Log.v("OrderActivity","btn_modify.s");
//        ModifyPerson modifyInfo = (ModifyPerson) intent.getSerializableExtra("modify_info");
//        if(modifyInfo != null){
//            Log.v("ys","g更改联系人啦啦啦啦啦啦： " + modifyInfo.getModifyname());
//            name.setText(modifyInfo.getModifyname());
//            phone.setText(modifyInfo.getModifyphone());}
//    }
    // 检测网络
    private boolean checkNetwork() {

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return true;
    }

    private void getCredit(){
        Log.v("tb","getCredit");

        parameterCredit = new JSONObject();
        try {
            parameterCredit.put("type", "getCredit");
            parameterCredit.put("userId", 1);
        }catch (JSONException e){
            e.printStackTrace();
        }

        new Thread(){
            public void run() {
//                credit = (Credit) Proxy.getWebData(StateCode.GetCredit,parameterCredit);
                credit=(Credit)new Command().getCredit(parameterCredit);

            };
        }.start();

    }

    private void Order(){
        Log.v("tb","order");

        //完成对用户密码的包装
        parameter = new JSONObject();
        try {
            parameter.put("userId",ControlUser.getUser(mContext).getUserId());
            parameter.put("type", "OrderPublish");
            parameter.put("name", name.getText());
            parameter.put("phone", phone.getText());
            parameter.put("short",short_info.getText());
            parameter.put("long",long_info.getText());
            parameter.put("des",des.getText());
            parameter.put("shop",shop.getText());
            parameter.put("time",time.getText());
            parameter.put("money",Float.parseFloat(money.getText().toString()));
            parameter.put("deliver",Float.parseFloat(deliver.getText().toString()));
            parameter.put("deliverMethod",Integer.parseInt(selectMethod));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = new ProgressDialog(mContext);
        //弹出processdialog,必须要使用线程，不然无法显示
        dialog.setTitle("提示");
        dialog.setMessage("正在提交，请稍后...");
        dialog.setCancelable(false);
        dialog.show();

//        具体运行方法在handle属性中
        connect();

    }

    private void connect() {
        new Thread(){
            public void run() {
//                code = (int) Proxy.getWebData(StateCode.OrderPublish,parameter);
                code=(int)new Command().orderPublish(parameter);

                OrderActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectFinish();
                    }
                });

            };
        }.start();
    }

    //连接完网络请求后需要做的事情
    private void connectFinish() {
        dialog.dismiss();
        if (code == 0) {

            new AlertDialog.Builder(mContext)

                    .setTitle("提示")

                    .setMessage("订单发布失败")

                    .setPositiveButton("确定", null)

                    .show();
        } else {

            //写入sharedPreferences
            if (code == -1) {
                new AlertDialog.Builder(mContext)

                        .setTitle("警告")

                        .setMessage("无法提交")

                        .setPositiveButton("确定", null)

                        .show();
            } else {
                if(credit.getCredit() >= Integer.parseInt(selectMethod)){
                ActivityHelper.startActivity(mContext, MainActivity.class);

                finish();
                }


            }
        }
    }

    private void selectRadioBtn(){
        change_method = (RadioButton) findViewById(method.getCheckedRadioButtonId());
        selectMethod = change_method.getText().toString();
        if(selectMethod.equals("2")){
            deliver_method.setText("元");
            myCredit.setText(" ");
        }
        else {
            deliver_method.setText("分");
            myCredit.setText("(积分："+credit.getCredit()+"）");
        }
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        //个人信息改变的接收器
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            switch (action){
                case StateCode.BROAD_CHANGENAME:
                    Log.v("OrderActivity","change name");
                    String nickName=intent.getStringExtra(StateCode.BROAD_CHANGENAME);
                    Log.v("dz","接收到的nickname为："+nickName);
                    name.setText(nickName);
                    break;
                case StateCode.BROAD_CHANGEPHONE:
                    Log.v("OrderActivity","change phone");
                    String changePhone=intent.getStringExtra(StateCode.BROAD_CHANGEPHONE);
                    Log.v("dz","接收到的phone为："+changePhone);
                    phone.setText(changePhone);
                    break;





            }

        }

    };

}
