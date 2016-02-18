package com.d9ing.mediademo.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.d9ing.mediademo.bean.Music;
import com.d9ing.mediademo.conf.IConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 * Created by wx on 2016/2/16.
 */
public class MediaUtils {
    public static List<Music> songList = new ArrayList<Music>();
    public static int CURSTATE = IConstants.STATE_STOP;
    public static int CURPOSITION = 1;
    //播放模式
    public static int CURMODEL = IConstants.MODEL_NORMAL;
    //加载手机里面的本地音乐-->sqlite-->contentProvider

    /**
     * 加载本地的音乐
     *
     * @param context
     */
    public static void initSongList(Context context) {
        songList.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//uri
        /**
         * public String name;//歌曲名
         public String author;//歌手
         public String path;//路径
         */
        String[] projection = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            Music music = new Music(title, artist, path);
            songList.add(music);
        }
    }

    /**
     * 毫秒值格式化转换
     *
     * @param duration
     * @return
     */
    public static String duration2String(int duration) {
        String result = "";
        int i = duration / 1000;
        int min = i / 60;
        int sec = i % 60;
        if (min > 9) {
            if (sec > 9) {
                result = min + ":" + sec;
            } else {
                result = min + ":0" + sec;
            }
        } else {
            if (sec > 9) {
                result = "0" + min + ":" + sec;
            } else {
                result = "0" + min + ":0" + sec;
            }
        }

        return result;
    }


}
