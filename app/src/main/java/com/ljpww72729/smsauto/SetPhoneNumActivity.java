package com.ljpww72729.smsauto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetPhoneNumActivity extends AppCompatActivity {

    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_phone_num);
        phoneNumber = findViewById(R.id.phone_num);
        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())) {
                    Toast.makeText(SetPhoneNumActivity.this, "请输入本机号码后再确认!", Toast.LENGTH_LONG).show();
                } else {
                    //检查手机号是否正确
                    if (VerifyUtils.isPhoneNumber(phoneNumber.getText().toString().trim())) {
                        setResult(RESULT_OK, getIntent().putExtra(ConstantsStr.PHONE_NUMBER, phoneNumber.getText().toString().trim()));
                        finish();
                    } else {
                        Toast.makeText(SetPhoneNumActivity.this, "请输入正确的手机号!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

}
