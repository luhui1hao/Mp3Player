package com.example.luhui1hao.mp3player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.luhui1hao.R;
import com.example.luhui1hao.ioutils.FileUtils;
import com.example.luhui1hao.model.Mp3Info;
import com.example.luhui1hao.service.PlayerService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhui1hao on 2015/12/13.
 */
public class LocalMp3ListFragment extends Fragment {
    private Context mContext;
    private List<Mp3Info> mp3Infos = new ArrayList<>();
    private FileUtils fileUtils = new FileUtils();
    private Handler localListHandler;
    private MyAdapter myAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Mp3Info> lists2 = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.local_mp3_list, container, false);
        ListView lv = (ListView) view.findViewById(R.id.local_lv);
        myAdapter = new MyAdapter(mContext);
        lv.setAdapter(myAdapter);

        // 给ListView的每个Item绑定监听器
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (lists2 != null) {
                    //启动ServicePlayer
                    Intent psIntent = new Intent(mContext, PlayerService.class);
                    psIntent.putExtra("mp3Infos", (Serializable)mp3Infos);
                    psIntent.putExtra("position", position);
                    mContext.startService(psIntent);
                    //启动PlayerActivity
                    Intent intent = new Intent(
                            mContext,
                            PlayerActivity.class);
                    startActivity(intent);
                }
            }
        });

        //设置下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.local_list_swipe_container);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //搜索之前把lists清空
                        mp3Infos.clear();
                        mp3Infos = fileUtils.traverseSDCard(FileUtils.getSDCARD_ROOT(), mp3Infos);
                        Message msg = localListHandler.obtainMessage();
                        msg.what = 1;
                        localListHandler.sendMessage(msg);
                    }
                }).start();
            }
        });

        localListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    //复制列表，为了解决刷新的时候拖动列表会崩溃的问题
                    lists2 = new ArrayList<>(mp3Infos);
                    myAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };

        return view;
    }

    class MyAdapter extends BaseAdapter {
        LayoutInflater inflater;

        MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return lists2.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_mp3info_item, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.local_mp3_name);
                holder.tv2 = (TextView) convertView.findViewById(R.id.local_mp3_size);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv1.setText(lists2.get(position).getMp3Name());
            holder.tv2.setText(lists2.get(position).getMp3Size());
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tv1;
        public TextView tv2;
    }
}
