package com.keeplive.hln.activity.privacy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.keeplive.hln.R;
import com.keeplive.hln.activity.AboutActivity;
import com.keeplive.hln.activity.PrivacyPolicyActivity;
import com.keeplive.hln.network.NetType;
import com.keeplive.hln.network.annotation.NetworkAnnotation;
import com.keeplive.hln.network.NetworkManager;
import com.keeplive.hln.receiver.NetStateReceiver;
import com.keeplive.hln.utils.Constants;
import com.keeplive.hln.utils.SPUtil;
import com.keeplive.hln.wiget.KeyboardNum;
import com.keeplive.hln.wiget.dialog.PrivacyDialog;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String SP_PRIVACY = "sp_privacy";
    private String SP_VERSION_CODE = "sp_version_code";
    private boolean isCheckPrivacy = false;
    private long versionCode;
    private long currentVersionCode;
    private KeyboardNum mMKeyboardView;
    private NetStateReceiver mNetStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.more);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(imageView);
            }
        });
         NetworkManager.getInstance().init().registerObserver(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @NetworkAnnotation(netType = NetType.AUTO)
    public void onNetChanged(NetType netType) {
        switch (netType) {
            case WIFI:
                Log.e(Constants.TAG, "：WIFI CONNECT");
                Toast.makeText(this, "：WIFI CONNECT", Toast.LENGTH_SHORT).show();
                break;
            case MOBILE:
                Toast.makeText(this, "：MOBILE CONNECT", Toast.LENGTH_SHORT).show();
                Log.e(Constants.TAG, "：MOBILE CONNECT");
                break;
            case AUTO:
                Toast.makeText(this, "：AUTO CONNECT", Toast.LENGTH_SHORT).show();
                Log.e(Constants.TAG, "：AUTO CONNECT");
                break;
            case NONE:
                Log.e(Constants.TAG, "：NONE CONNECT");
                Toast.makeText(this, "：NONE CONNECT", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 显示用户协议和隐私政策
     */
    private void showPrivacy() {

        final PrivacyDialog dialog = new PrivacyDialog(MainActivity.this);
        TextView tv_privacy_tips = dialog.findViewById(R.id.tv_privacy_tips);
        TextView btn_exit = dialog.findViewById(R.id.btn_exit);
        TextView btn_enter = dialog.findViewById(R.id.btn_enter);
        dialog.show();

        String string = getResources().getString(R.string.privacy_tips);
        String key1 = getResources().getString(R.string.privacy_tips_key1);
        String key2 = getResources().getString(R.string.privacy_tips_key2);
        int index1 = string.indexOf(key1);
        int index2 = string.indexOf(key2);

        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));
        spannedString.setSpan(colorSpan1, index1, index1 + key1.length() + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //
        //
        // ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));
        // spannedString.setSpan(colorSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击字体大小
        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(18, true);
        spannedString.setSpan(sizeSpan1, index1, index1 + key1.length() + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //  AbsoluteSizeSpan sizeSpan2 = new AbsoluteSizeSpan(18, true);
        // spannedString.setSpan(sizeSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击事件
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan2, index2, index2 + key2.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //  int SPAN_EXCLUSIVE_EXCLUSIVE = 33; //在Span前后输入的字符都不应用Span效果
        // int SPAN_EXCLUSIVE_INCLUSIVE = 34; //在Span前面输入的字符不应用Span效果，后面输入的字符应用Span效果
        // int SPAN_INCLUSIVE_EXCLUSIVE = 17; //在Span前面输入的字符应用Span效果，后面输入的字符不应用Span效果
        // int SPAN_INCLUSIVE_INCLUSIVE = 18; //在Span前后输入的字符都应用Span效果


        //设置点击后的颜色为透明，否则会一直出现高亮
        tv_privacy_tips.setHighlightColor(Color.TRANSPARENT);
        //开始响应点击事件
        tv_privacy_tips.setMovementMethod(LinkMovementMethod.getInstance());

        tv_privacy_tips.setText(spannedString);

        //设置弹框宽度占屏幕的80%
        WindowManager m = getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (defaultDisplay.getWidth() * 0.80);
        dialog.getWindow().setAttributes(params);

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(MainActivity.this, SP_VERSION_CODE, currentVersionCode);
                SPUtil.put(MainActivity.this, SP_PRIVACY, false);
                finish();
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SPUtil.put(MainActivity.this, SP_VERSION_CODE, currentVersionCode);
                SPUtil.put(MainActivity.this, SP_PRIVACY, true);

                Toast.makeText(MainActivity.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);

        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_layout, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actiosettings:
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                }
                return true;
            }
        });

        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }

//                <EditText
//    android:id="@+id/et_qqcp_edittext"
//    android:layout_width="match_parent"
//    android:layout_height="@dimen/dp45"
//    android:background="@drawable/qqcp_windowd_maincolor_shape"
//    android:digits="1234567890"
//    android:hint="@string/qqcp_edittext_hint"
//    android:inputType="number"
//    android:lines="1"
//    android:maxLength="10"
//    android:paddingStart="@dimen/dp10"
//    android:textColor="@color/hint"
//    android:textColorHint="@color/login_exittext_notfocus_textcolor"
//    android:textCursorDrawable="@drawable/color_cursor"
//    android:textSize="@dimen/dp15"
//    android:textStyle="normal"
//    android:typeface="normal" />normal

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getInstance().init().unRegisterObserver(this);
    }
}
