/*
 * @(#) ApplyAcitivity.java
 * @Author:tangliu(mail) 2014-12-29
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.exercise;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.R;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

/**
  * @author tangliu(mail) 2014-12-29
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class ApplyAcitivity extends AbActivity {

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;
    
    Button hasBike;
    Button hasHelmet;
    EditText realNameText;
    EditText phoneNumberText;
    EditText cardNumberText;
    EditText remarks;
    TextView exerciseTitle;
    TextView exerciseRouteAddress;
    TextView exerciseTime;
    
    private String activityId;
    private JSONObject activityObj;
    private String needPhone;
    private String needRealName;
    private String needIdentification;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyfor_exercise);
        getTitleBar().setVisibility(View.GONE);
        
        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbHttpUtil.setDebug(false);

        hasBike = (Button) findViewById(R.id.hasBike);
        hasHelmet = (Button) findViewById(R.id.hasHelmet);

        realNameText = (EditText) findViewById(R.id.realName);
        phoneNumberText = (EditText) findViewById(R.id.phoneNumber);
        cardNumberText = (EditText) findViewById(R.id.cardNumber);
        remarks = (EditText)findViewById(R.id.remarks);
        
        exerciseTitle = (TextView) findViewById(R.id.exerciseTitle);
        exerciseRouteAddress = (TextView)findViewById(R.id.exerciseRouteAddress);
        exerciseTime = (TextView)findViewById(R.id.exerciseTime);

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button submitButton = (Button) findViewById(R.id.submitButton);
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog("温馨提示", "确认要取消报名吗？取消后报名信息将不保存。", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        quitThisActivity();
                        
                    }
                });
            }
        });
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 获取数据
                String realName = realNameText.getText().toString().trim();
                String phoneNumber = phoneNumberText.getText().toString().trim();
                String cardNumber = cardNumberText.getText().toString().trim();
                if (realName.equals("") && "true".equalsIgnoreCase(needRealName)) {
                    showDialog("温馨提示", "请输入真实姓名");
                } else if (phoneNumber.equals("") && "true".equalsIgnoreCase(needPhone)) {
                    showDialog("温馨提示", "请输入电话号码");
                } else if (cardNumber.equals("") && "true".equalsIgnoreCase(needIdentification)) {
                    showDialog("温馨提示", "请输入身份证号");
                } else {

                    boolean bikeSelfProvide = hasBike.isSelected();
                    boolean helmetSelfProvide = hasHelmet.isSelected();
                    String remarksString = remarks.getText().toString();
                    
                    applyForActivity(realName, phoneNumber, cardNumber, remarksString, bikeSelfProvide, helmetSelfProvide);
                }
            }
        });
        
        String activityDate = getIntent().getStringExtra("activityData");
        try {
            activityObj = new JSONObject(activityDate);
            exerciseTitle.setText(activityObj.getString("title"));
            exerciseRouteAddress.setText("约" + activityObj.getString("wayLength") + "km  " + activityObj.getString("wayLine"));
            exerciseTime.setText(activityObj.getString("activityStartDate") + "-" + activityObj.getString("activityEndDate"));
     
            needPhone = activityObj.getString("needPhone");
            needRealName = activityObj.getString("needRealName");
            needIdentification = activityObj.getString("needIdentification");
            
            activityId = activityObj.getString("id");
        
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }

    
    
    /**
      * applyForActivity(这里用一句话描述这个方法的作用)
      * @param realName
      * @param phoneNumber
      * @param cardNumber
      * @param remarksString
      * @param bikeSelfProvide
      * @param helmetSelfProvide
      */
    protected void applyForActivity(String realName, String phoneNumber, String cardNumber, String remarksString, boolean bikeSelfProvide,
            boolean helmetSelfProvide) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        if (!NetUtil.isConnnected(this)) {
            showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }
        String urlString = Constant.serverUrl + Constant.applyForActivityUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("activityId", activityId);
        p.put("phone", phoneNumber);
        p.put("credentialNo", cardNumber);
        p.put("name", realName);
        p.put("bikeSelfProvide", bikeSelfProvide?"true":"false");
        p.put("helmetSelfProvide", helmetSelfProvide?"true":"false");
        p.put("remarks", remarksString);
        // 绑定参数
        mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {

                processResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
                showProgressDialog("正在报名，请稍后...");
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                removeProgressDialog();
            };

        });
        
    }



    
    /**
      * processResult(这里用一句话描述这个方法的作用)
      * @param content
      */
    protected void processResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject resultObject = new JSONObject(content);
            String code = resultObject.getString("code");
            if("0".equals(code)){
                showDialog("温馨提示", "报名成功，点击确认退出本页面", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        quitThisActivity();
                        
                    }
                });
            }else{
                showDialog("温馨提示", "报名失败：\n" + resultObject.getString("message") + "\n请稍后重试");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }



    /**
     * 接受页面button调用
     * 
     * @param source
     */
    public void clickHandler(View source) {

        switch (source.getId()) {
        case R.id.goBack:
            quitThisActivity();
            break;
        case R.id.hasBike:
            if (hasBike.isSelected()) {
                hasBike.setSelected(false);
            } else {
                hasBike.setSelected(true);
            }
            break;
        case R.id.hasHelmet:
            if (hasHelmet.isSelected()) {
                hasHelmet.setSelected(false);
            } else {
                hasHelmet.setSelected(true);
            }
            break;
        default:
            break;
        }
    }

    private void quitThisActivity() {
        ApplyAcitivity.this.finish();
        overridePendingTransition(R.anim.fragment_out, R.anim.fragment_down);
    }

}
