package com.d9ing.mediademo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.d9ing.mediademo.R;
import com.d9ing.mediademo.bean.Music;
import com.d9ing.mediademo.utils.MediaUtils;

/**
 * 列表的适配器
 * Created by wx on 2016/2/16.
 */
public class ListAdapter extends BaseAdapter {
    private Context context;

    public ListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return MediaUtils.songList == null ? 0 : MediaUtils.songList.size();
    }

    @Override
    public Object getItem(int position) {
        return MediaUtils.songList == null ? null : MediaUtils.songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_music, null);
            holder = new ViewHolder(convertView);
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /**--------填充数据-------**/
        Music music = MediaUtils.songList.get(position);
        holder.tv_title.setText(music.name);
        holder.tv_artist.setText(music.artist);
        if (MediaUtils.CURPOSITION == position) {
            holder.tv_title.setTextColor(Color.parseColor("#00ff00"));
        }else{
            holder.tv_title.setTextColor(Color.parseColor("#ffffff"));
        }
        //绑定标记为了反查
        holder.tv_title.setTag(position);
        return convertView;
    }

    class ViewHolder {

        private final TextView tv_title;
        private final TextView tv_artist;

        public ViewHolder(View convertView) {
            tv_title = ((TextView) convertView.findViewById(R.id.tv_title));
            tv_artist = ((TextView) convertView.findViewById(R.id.tv_artist));
        }
    }
}
