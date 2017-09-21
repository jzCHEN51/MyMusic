package com.example.jianzhongchen.mymusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.jianzhongchen.mymusic.R;
import com.example.jianzhongchen.mymusic.bean.Music;
import com.example.jianzhongchen.mymusic.utils.MediaUtils;

/**
 * Created by jianzhong.chen on 2017/9/19.
 */
public class MyAdapter extends BaseAdapter {

    Context context;

    public MyAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        if (MediaUtils.songList != null )
            return MediaUtils.songList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (MediaUtils.songList != null )
            return MediaUtils.songList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_music, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_artist = (TextView) convertView.findViewById(R.id.tv_artist);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Music music = MediaUtils.songList.get(position);
        holder.tv_title.setText(music.title);
        holder.tv_artist.setText(music.artist);
        if (MediaUtils.CURPOSITION == position) {

            holder.tv_title.setTextColor(Color.GREEN);
        } else {
            holder.tv_title.setTextColor(Color.WHITE);
        }
        holder.tv_title.setTag(position);
        return convertView;
    }

    class ViewHolder{
        TextView tv_title;
        TextView tv_artist;
    }
}
