package com.example.luhui1hao.mp3player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.luhui1hao.R;
import com.example.luhui1hao.ioutils.FileUtils;
import com.example.luhui1hao.model.Mp3Info;
import com.example.luhui1hao.service.PlayerService;

import java.io.File;
import java.io.Serializable;
import java.util.List;


public class ObtainedMp3ListFragment extends Fragment {
    public static final String TAG = "ObtainedMp3ListFragment";
    private List<Mp3Info> mp3Infos = null;
    private Context mContext;
    private ViewHolder holder;
    private MyAdapter myAdapter;
    private IntentFilter intentFilter;
    private ObtainedReceiver obtainedReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = ObtainedMp3ListFragment.this.getActivity();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.obtained_mp3_list, container, false);
        Log.e(TAG, "ObtainedMp3ListFragment------onCreateView()");
        // 如果存在aMp3文件夹，再执行查找操作
        if (new File(FileUtils.getSDCARD_ROOT() + "aMp3").exists()) {
            FileUtils fileUtils = new FileUtils();
            mp3Infos = fileUtils.getMp3Files("aMp3/");

            myAdapter = new MyAdapter(mContext);
            ListView listView = (ListView) view.findViewById(R.id.local_lv);
            listView.setAdapter(myAdapter);
            // 给ListView的每个Item绑定监听器
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (mp3Infos != null) {
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
        }
        return view;
    }

    /*
    * 这一步是为了让Fragment每次启动的时候都重新扫描一遍歌曲
    * */
    @Override
    public void onStart() {
        super.onStart();
        //注册监听器
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.luhui1hao.OBTAINED");
        intentFilter.addAction("com.example.luhui1hao.DEL_OK");
        obtainedReceiver = new ObtainedReceiver();
        mContext.registerReceiver(obtainedReceiver, intentFilter);
        Log.e(TAG, "ObtainedMp3ListFragment------onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        //只要次Fragment不可见，就解绑广播监听器
        mContext.unregisterReceiver(obtainedReceiver);
        Log.e(TAG, "ObtainedMp3ListFragment------onStop()");
    }

    class ObtainedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //更新列表
            FileUtils fileUtils = new FileUtils();
            mp3Infos = fileUtils.getMp3Files("aMp3/");
            myAdapter.notifyDataSetChanged();
            Log.e(TAG, "ObtainedReceiver has run!!!!!!");
        }
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;

        MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mp3Infos.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Mp3Info mp3Info = mp3Infos.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.obtained_mp3info_item, null);
                holder.mp3_name = (TextView) convertView.findViewById(R.id.mp3_name);
                holder.mp3_size = (TextView) convertView.findViewById(R.id.mp3_size);
                // holder.itemMenuBtn = (Button) convertView.findViewById(R.id.item_menu_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mp3_name.setText(mp3Info.getMp3Name());
            holder.mp3_size.setText(mp3Info.getMp3Size());

            final Button itemMenuBtn = (Button) convertView.findViewById(R.id.item_menu_btn);
            itemMenuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, itemMenuBtn);
                    popupMenu.getMenuInflater().inflate(R.menu.item_btn_popupmenu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            try {
                                //如果歌曲文件存在就执行删除
                                if (FileUtils.isFileExist2(mp3Info.getMp3Path())) {
                                    boolean issucceed = FileUtils.deleteFile(mp3Info.getMp3Path());
                                    if (issucceed) {
                                        FileUtils fileUtils = new FileUtils();
                                        mp3Infos = fileUtils.getMp3Files("aMp3/");
                                        myAdapter.notifyDataSetChanged();
                                    }
                                }
                                //如果歌词文件存在就执行删除
                                if (FileUtils.isFileExist2(mp3Info.getLrcPath())) {
                                    FileUtils.deleteFile(mp3Info.getLrcPath());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView mp3_name;
        public TextView mp3_size;
        //public Button itemMenuBtn;
    }
}
