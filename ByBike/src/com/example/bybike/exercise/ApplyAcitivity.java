/*
 * @(#) ApplyAcitivity.java
 * @Author:tangliu(mail) 2014-12-29
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.exercise;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ab.activity.AbActivity;
import com.example.bybike.R;

/**
  * @author tangliu(mail) 2014-12-29
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class ApplyAcitivity extends AbActivity {

    Button hasBike;
    Button hasHelmet;
    EditText realNameText;
    EditText phoneNumberText;
    EditText cardNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyfor_exercise);
        getTitleBar().setVisibility(View.GONE);

        hasBike = (Button) findViewById(R.id.hasBike);
        hasHelmet = (Button) findViewById(R.id.hasHelmet);

        realNameText = (EditText) findViewById(R.id.realName);
        phoneNumberText = (EditText) findViewById(R.id.phoneNumber);
        cardNumberText = (EditText) findViewById(R.id.cardNumber);

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
                if (realName.equals("")) {
                    showDialog("温馨提示", "请输入真实姓名");
                } else if (phoneNumber.equals("")) {
                    showDialog("温馨提示", "请输入电话号码");
                } else if (cardNumber.equals("")) {
                    showDialog("温馨提示", "请输入身份证号");
                } else {

                }
            }
        });

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
