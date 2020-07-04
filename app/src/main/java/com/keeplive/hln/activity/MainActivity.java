package com.keeplive.hln.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.keeplive.hln.R;
import com.keeplive.hln.adpater.CustomListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ListView testModuleListView;
    private ImageView more;
    private FrameLayout frame_layout;
    private String mString = "12.x";
    CustomListViewAdapter adapter;
    int position = 0;

    private String[][] itemStrings = new String[][]{
            {"1.xxxx", "A"},
            {"2.xxxx", "B"},
            {"3.xxxx", "C"},
            {"4.xxxxx", "D"},
            {"5.xxxxx", "E"},
            {"6.xxxxx", "M"},
            {"7.xxxxx", "F"},
            {"8.xxxx", "G"},
            {"9.xxxxx", "H"},
            {"10.xxxxx", "I"},
            {"11.xxxx", "G"},
            {"12.x", "QW"},
            {"13.x", "EE"},
            {"14.x", "DD"},
            {"15.x", "SS"},
            {"16.x", "AD"},
            {"17.x", "ADC"},
            {"18.x", "ABV"},
            {"19.x", "VB"},
            {"120.x", "CX"},
            {"121.x", "XA"},
            {"1211.x", "WQA"},
            {"121111.x", "XZA"}};
    private List<Map<String, Object>> mList = new ArrayList<>();

    private void bindEvents() {
        testModuleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.setSelectItem(position);
                adapter.notifyDataSetInvalidated();
            }
        });
    }

    private List<Map<String, Object>> initialModuleItem() {
        Map<String, Object> map;
        for (int i = 0; i < itemStrings.length; i++) {
            map = new HashMap<>();
            map.put("itemName", itemStrings[i][0]);
            map.put("action", itemStrings[i][1]);
            if (map.get("action").equals("DD")) {
                mList.add(0, map);
            } else {
                mList.add(map);
            }

        }

        return mList;

    }

    private void bindViews() {
        testModuleListView = findViewById(R.id.testModuleListView);
        frame_layout = findViewById(R.id.frame_layout);
        more = findViewById(R.id.more);
        more.setOnClickListener(this);
    }

    private void initListView() {
        List<Map<String, Object>> maps = initialModuleItem();
        adapter = new CustomListViewAdapter(this, maps, mString);
        testModuleListView.setAdapter(adapter);
        adapter.setSelectItem(0);

    }


    @Override
    protected void initView() {
        bindViews();
        initListView();
        bindEvents();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void showPopBottom() {
        PopupWindow popupWindow = new PopupWindow(this);
        View content_view = LayoutInflater.from(this).inflate(R.layout.popwindow_topright, null);
        popupWindow.setContentView(content_view);
        TextView meun_one = content_view.findViewById(R.id.meun_one);
        TextView meun_two = content_view.findViewById(R.id.meun_two);
        meun_one.setOnClickListener(this);
        meun_two.setOnClickListener(this);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        content_view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //  popupWindow.showAsDropDown(more, -content_view.getMeasuredWidth() + more.getWidth(), -content_view.getMeasuredHeight() / 3);
        popupWindow.showAtLocation(more, Gravity.TOP | Gravity.END, more.getWidth(), frame_layout.getHeight());
        popupWindow.update();

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
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more:
                showPopupMenu(more);
                break;
            case R.id.meun_one:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.meun_two:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }

    }
}
