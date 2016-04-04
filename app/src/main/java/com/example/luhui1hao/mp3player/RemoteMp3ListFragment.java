package com.example.luhui1hao.mp3player;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.luhui1hao.R;
import com.example.luhui1hao.application.MyApplication;
import com.example.luhui1hao.download.HttpDownloader;
import com.example.luhui1hao.ioutils.FileUtils;
import com.example.luhui1hao.model.Mp3Info;
import com.example.luhui1hao.service.DownloadService;
import com.example.luhui1hao.xml.Mp3ListContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by luhui1hao on 2015/12/4.
 */
public class RemoteMp3ListFragment extends Fragment {
    private ListView listView;
    private int listPosition;
    private List<Mp3Info> mp3Infos = new ArrayList<>();
    private Handler remoteListHandler = null;
    private Context mContext = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "RemoteMp3ListFragment";
    private MyAdapter myAdapter;
    private ObtainedReceiver obtainedReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = RemoteMp3ListFragment.this.getActivity();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remote_mp3_list,container,false);

        // 给ListView的每个Item绑定监听器
        listView = (ListView) view.findViewById(R.id.remote_lv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                listPosition = position;
                AlertDialog dialog = new AlertDialog.Builder(
                        RemoteMp3ListFragment.this.getActivity())
                        .setTitle("提示")
                        .setIcon(R.drawable.download)
                        .setMessage("是否下载本歌曲")
                        .setPositiveButton("是",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        System.out.println("yonghudianjilexiazaianniu==============");
                                        // 根据用户点击列表当中的位置来得到响应的Mp3Info对象
                                        Mp3Info mp3Info = mp3Infos
                                                .get(listPosition);
                                        // 生成Intent对象
                                        Intent intent = new Intent(mContext, DownloadService.class);
                                        // 将Mp3Info对象存入到Intent对象当中
                                        intent.putExtra("mp3Info", mp3Info);
                                        // 启动Service
                                        mContext.startService(intent);
                                    }
                                })
                        .setNegativeButton("否",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO 自动生成的方法存根

                                    }
                                }).show();
            }
        });
        //匹配适配器
        myAdapter = new MyAdapter(mContext);
        listView.setAdapter(myAdapter);

        // 初始化时更新列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                mp3Infos = getMp3Infos();
                //发送Message给UI线程更新界面
                updataUI();
            }
        }).start();

        Log.d(TAG,Thread.currentThread().getName());
        remoteListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                   myAdapter.notifyDataSetChanged();
                }
            }
        };

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mp3Infos = getMp3Infos();
                        updataUI();
                    }
                }).start();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //注册监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.luhui1hao.OBTAINED");
        intentFilter.addAction("com.example.luhui1hao.DEL_OK");
        obtainedReceiver = new ObtainedReceiver();
        mContext.registerReceiver(obtainedReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        //只要次Fragment不可见，就解绑广播监听器
        mContext.unregisterReceiver(obtainedReceiver);
        Log.e(TAG, "RemoteMp3ListFragment------onStop()");
    }

    class ObtainedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //更新列表
            myAdapter.notifyDataSetChanged();
            Log.e(TAG, "ObtainedReceiver has run!!!!!!");
        }
    }

    private List<Mp3Info> getMp3Infos(){
        // 下载包含所有Mp3基本信息的xml文件
        String xml = downloadXML(MyApplication.BASE_URL + "resources.xml");
        System.out.println(xml);
        // 对xml文件进行解析，并将解析的结果放置到Mp3Info对象当中，最后将这些Mp3Info对象放置到List当中
        mp3Infos = parse(xml);
        return mp3Infos;
    }

    private String downloadXML(String urlStr) {
        HttpDownloader httpDownloader = new HttpDownloader();
        String result = httpDownloader.download(urlStr);
        return result;
    }

    private List<Mp3Info> parse(String xmlStr) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        List<Mp3Info> infos = new ArrayList<>();
        try {
            XMLReader xmlReader = saxParserFactory.newSAXParser()
                    .getXMLReader();
            // 这一步至关重要，记得把List传进去，解析出来的内容都是放在这里面的
            Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(
                    infos);
            xmlReader.setContentHandler(mp3ListContentHandler);
            xmlReader.parse(new InputSource(new StringReader(xmlStr)));
            // 在日志中把歌曲信息打印出来
            for (Iterator<Mp3Info> iterator = infos.iterator(); iterator
                    .hasNext();) {
                Mp3Info mp3Info = iterator.next();
                System.out.println(mp3Info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }


    private void updataUI() {
        Message msg = remoteListHandler.obtainMessage();
        Bundle data = new Bundle();
        data.putSerializable("mp3Infos",
                (Serializable) mp3Infos);
        msg.what = 0;
        msg.setData(data);
        remoteListHandler.sendMessage(msg);
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        FileUtils fileUtils = new FileUtils();

        MyAdapter(Context context){
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.mp3info_item, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.mp3_name = (TextView) convertView.findViewById(R.id.mp3_name);
                holder.mp3_size = (TextView) convertView.findViewById(R.id.mp3_size);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(fileUtils.isFileExist("aMp3/",mp3Infos.get(position).getMp3Name())){
                holder.iv.setImageResource(R.drawable.obtained_ok);
            }else{
                holder.iv.setImageResource(R.drawable.obtained_no);
            }
            holder.mp3_name.setText(mp3Infos.get(position).getMp3Name());
            holder.mp3_size.setText(mp3Infos.get(position).getMp3Size());
            return convertView;
        }
    }

    static class ViewHolder{
        public ImageView iv;
        public TextView mp3_name;
        public TextView mp3_size;
    }
}
