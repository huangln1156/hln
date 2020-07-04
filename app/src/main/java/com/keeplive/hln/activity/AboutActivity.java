package com.keeplive.hln.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.keeplive.hln.R;
import com.keeplive.hln.wiget.view.VerifyCodeView;


public class AboutActivity extends AppCompatActivity implements VerifyCodeView.InputCompleteListener {

    private TextView[] mTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        VerifyCodeView verifyCodeView = findViewById(R.id.verify_code_view);
        verifyCodeView.setInputCompleteListener(this);
        mTextViews = verifyCodeView.getTextViews();
    }
    @Override
    public void inputComplete() {
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[0].setText("o");

        }
    }
}
