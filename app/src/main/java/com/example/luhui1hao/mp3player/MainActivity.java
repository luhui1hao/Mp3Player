package com.example.luhui1hao.mp3player;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.luhui1hao.R;

public class MainActivity extends FragmentActivity {
    public static final String TAG = "MainActivity";
    //定义FragmentTabHost
    private FragmentTabHost mTabHost;
    //定义布局填充器
    private LayoutInflater mInflater;
    //初始化Fragment界面数组
    private Class[] mFragmentArray = {RemoteMp3ListFragment.class,ObtainedMp3ListFragment.class,LocalMp3ListFragment.class};
    //初始化存放图片数组
    private int[] mImageArray = {R.drawable.remote_music,R.drawable.obtained,R.drawable.local_music};
    //初始化选择按钮的文字
    private String[] mTextArray = {"网络音乐","已下载","本地音乐"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        //创建LayoutInflater对象
        mInflater = LayoutInflater.from(this);
        //找到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        //得到Fragment的个数
        int count = mFragmentArray.length;
        for(int i = 0; i < count; i++){
            //给每个Tab按钮设置内容，图标和文字
            TabHost.TabSpec tabspec = mTabHost.newTabSpec(mTextArray[i])
                    .setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项中
            mTabHost.addTab(tabspec,mFragmentArray[i],null);
            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundColor(getResources().getColor(R.color.orange));
        }

    }

    private View getTabItemView(int index) {
        View view = mInflater.inflate(R.layout.tab_item_view,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageArray[index]);
        TextView textView = (TextView)view.findViewById(R.id.tv1);//草！！！这里有个view这是你的自定义控件
        textView.setText(mTextArray[index]);

        return view;
    }

}
