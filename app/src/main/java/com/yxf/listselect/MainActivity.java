package com.yxf.listselect;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int LIST_STATE_NORMAL = 0;
    public static final int LIST_STATE_MULITI_SELECT = 1;

    public static final int ANIMATION_TIME = 300;

    ListView listView;
    TextView cancel;
    TextView ok;
    LinearLayout linearLayout;
    MyAdapter adapter;
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    int listState = 0;

    MyListHeightAnimation startListHeightAnimation;
    MyListHeightAnimation endListHeightAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initItems();
        init();
    }

    private void initItems() {
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("1544156156116"));
        items.add(new ListItem("1245852331564"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("1544156156116"));
        items.add(new ListItem("1245852331564"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("1544156156116"));
        items.add(new ListItem("1245852331564"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("1544156156116"));
        items.add(new ListItem("1245852331564"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("1544156156116"));
        items.add(new ListItem("1245852331564"));
        items.add(new ListItem("125481541515"));
        items.add(new ListItem("1544156156116"));
        items.add(new ListItem("1245852331564"));
        items.add(new ListItem("125481541515"));

    }

    private void init() {
        listView = (ListView) findViewById(R.id.list);
        cancel = (TextView) findViewById(R.id.cancel_button);
        ok = (TextView) findViewById(R.id.ok_button);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        adapter = new MyAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setPadding(-adapter.checkBoxWidth, 0, 0, 0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listState == 1) {
                    items.get(position).isChecked = !items.get(position).isChecked;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (startListHeightAnimation == null) {
                    startListHeightAnimation = new MyListHeightAnimation(listView.getHeight(), listView.getHeight() - 160);
                }
                if (endListHeightAnimation == null) {
                    endListHeightAnimation = new MyListHeightAnimation(listView.getHeight() - 160, listView.getHeight());
                }
                if (listState == 0) {
                    listState = 1;
                    items.get(position).isChecked = true;
                    adapter.notifyDataSetChanged();
                    startAnimation();
                    return true;
                }
                return false;
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMultiSelect();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = items.size() - 1; i >= 0; i--) {
                    if (items.get(i).isChecked) {
                        items.remove(i);
                    }
                }
                clearMultiSelect();
            }
        });
    }

    private void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(startListHeightAnimation, "listHeight", startListHeightAnimation.start, startListHeightAnimation.end);
        animator.setInterpolator(new DecelerateInterpolator(2));
        animator.setDuration(ANIMATION_TIME);
        animator.start();
        animator = ObjectAnimator.ofInt(new ViewPaddingAnimation(listView), "leftPadding", -adapter.checkBoxWidth, 0);
        animator.setDuration(ANIMATION_TIME);
        animator.start();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.delete_select);
        }
    }

    private void cancelAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(endListHeightAnimation, "listHeight", endListHeightAnimation.start, endListHeightAnimation.end);
        animator.setInterpolator(new AccelerateInterpolator(2));
        animator.setDuration(ANIMATION_TIME);
        animator.start();
        animator = ObjectAnimator.ofInt(new ViewPaddingAnimation(listView), "leftPadding", 0, -adapter.checkBoxWidth);
        animator.setDuration(ANIMATION_TIME);
        animator.start();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (clearMultiSelect()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean clearMultiSelect() {
        if (listState == 1) {
            listState = 0;
            for (int i = 0; i < items.size(); i++) {
                items.get(i).isChecked = false;
            }
            cancelAnimation();
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }


    public static class ListItem {
        String text;
        boolean isChecked = false;

        public ListItem(String text) {
            this.text = text;
        }
    }

    public class MyListHeightAnimation {
        int start, end;

        public MyListHeightAnimation(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void setListHeight(int listHeight) {
            ViewGroup.LayoutParams lp = listView.getLayoutParams();
            lp.height = listHeight;
            listView.setLayoutParams(lp);
        }

        public int getListHeight() {
            return listView.getHeight();
        }
    }

    class ViewPaddingAnimation {
        View view;

        public ViewPaddingAnimation(View view) {
            this.view = view;
        }

        public void setLeftPadding(int padding) {
            view.setPadding(padding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            view.requestLayout();
            view.postInvalidate();
        }

        public int getLeftPadding() {
            return view.getPaddingLeft();
        }
    }

    public class MyAdapter extends ArrayAdapter<ListItem> {
        public int checkBoxWidth = 160;

        public MyAdapter(Context context, List<ListItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int padding = 20;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
            }
            TextView text = (TextView) convertView.findViewById(R.id.text);
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            text.setText(getItem(position).text);
            LinearLayout.LayoutParams lp;
            lp = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
            lp.width = checkBoxWidth;
            checkBox.setLayoutParams(lp);
            checkBox.setChecked(getItem(position).isChecked);
            convertView.setPadding(padding, padding, padding, padding);
            return convertView;
        }
    }
}
