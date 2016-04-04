package com.example.luhui1hao.mp3player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.luhui1hao.R;
import com.example.luhui1hao.ioutils.FileUtils;
import com.example.luhui1hao.model.Mp3Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luhui1hao on 2015/12/4.
 */
public class TitleFragment extends Fragment {
    private Context mContext;
    private Button btn;
    private PopupMenu popupMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title_layout, container, false);
        btn = (Button) view.findViewById(R.id.ibtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(mContext, btn);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ip_config:
                                Intent intent = new Intent(mContext, IPActivity.class);
                                startActivity(intent);
                                break;
                            //执行删除所有已下载歌曲操作
                            case R.id.del_all:
                                List<Mp3Info> mp3Infos = FileUtils.getMp3Files("aMp3");
                                for(Mp3Info mp3Info : mp3Infos){
                                    try{
                                        FileUtils.deleteFile(FileUtils.getSDCARD_ROOT() + "aMp3/" + mp3Info.getMp3Name());
                                        FileUtils.deleteFile(FileUtils.getSDCARD_ROOT() + "aMp3/" + mp3Info.getLrcName());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(mContext, "删除已完成", Toast.LENGTH_SHORT).show();
                                //发送删除完成广播
                                Intent delOkIntent = new Intent("com.example.luhui1hao.DEL_OK");
                                mContext.sendBroadcast(delOkIntent);
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        return view;
    }
}
