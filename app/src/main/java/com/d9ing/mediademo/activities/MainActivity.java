package com.d9ing.mediademo.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.d9ing.mediademo.R;
import com.d9ing.mediademo.adapter.ListAdapter;
import com.d9ing.mediademo.conf.IConstants;
import com.d9ing.mediademo.service.MusicService;
import com.d9ing.mediademo.utils.MediaUtils;
import com.d9ing.mediademo.view.ScrollableViewGroup;

import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView mTv_curduration;
    private TextView mTv_minilrc;
    private TextView mTv_totalduration;
    //进度条
    private SeekBar mSk_duration;
    //播放按钮
    private ImageView mIv_bottom_model;
    private ImageView mIv_bottom_play;
    //列表
    private ListView mLv_list;
    //可滚动的视图组
    private ScrollableViewGroup mSvg_main;
    private int[] topArr = { R.id.ib_top_play, R.id.ib_top_list, R.id.ib_top_lrc, R.id.ib_top_volumn };
    //接受Service数据的handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IConstants.MSG_ONPREPARED:
                    int currentPosition = msg.arg1;
                    int totalDuration = msg.arg2;
                    //设值
                    mTv_curduration.setText(MediaUtils.duration2String(currentPosition));
                    mTv_totalduration.setText(MediaUtils.duration2String(totalDuration));
                    //设置进度条
                    mSk_duration.setMax(totalDuration);
                    mSk_duration.setProgress(currentPosition);
                    break;
                case IConstants.MSG_ONCOMPLETION:
                    //模式切换
                    if (MediaUtils.CURMODEL == IConstants.MODEL_NORMAL) {
                        if(MediaUtils.CURPOSITION < MediaUtils.songList.size()-1) {
                            changeColor(Color.WHITE);
                            MediaUtils.CURPOSITION++;
                            changeColor(Color.GREEN);
                            startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        }else{
                            //最后一首歌
                            startMediaService("stop");
                        }
                    } else if (MediaUtils.CURMODEL == IConstants.MODEL_RAMDOM) {
                        Random random = new Random();
                        int position = random.nextInt(MediaUtils.songList.size());

                        changeColor(Color.WHITE);
                        MediaUtils.CURPOSITION = position;
                        startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        changeColor(Color.GREEN);
                    } else if (MediaUtils.CURMODEL == IConstants.MODEL_REPEAT) {
                        if(MediaUtils.CURPOSITION < MediaUtils.songList.size()-1) {
                            changeColor(Color.WHITE);
                            MediaUtils.CURPOSITION++;
                            changeColor(Color.GREEN);
                            startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        }else{
                            //最后一首歌
                            changeColor(Color.WHITE);
                            MediaUtils.CURPOSITION = 0;
                            changeColor(Color.GREEN);
                            startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                        }

                    } else if (MediaUtils.CURMODEL == IConstants.MODEL_SINGLE) {
                        startMediaService("play",MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                    }
                    break;
                case IConstants.UPDATELIST:
                    Toast.makeText(MainActivity.this, "刷新", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }

    /**
     * 加载数据
     */
    private void initData() {
        MediaUtils.initSongList(this);
        mAdapter = new ListAdapter(this);
        mLv_list.setAdapter(mAdapter);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        findViewById(R.id.ib_top_play).setOnClickListener(this);
        findViewById(R.id.ib_top_list).setOnClickListener(this);
        findViewById(R.id.ib_top_lrc).setOnClickListener(this);
        findViewById(R.id.ib_top_volumn).setOnClickListener(this);
        findViewById(R.id.ib_bottom_model).setOnClickListener(this);
        findViewById(R.id.ib_bottom_last).setOnClickListener(this);
        findViewById(R.id.ib_bottom_play).setOnClickListener(this);
        findViewById(R.id.ib_bottom_next).setOnClickListener(this);
        findViewById(R.id.ib_bottom_update).setOnClickListener(this);
        mSvg_main.setOnCurrentViewChangedListener(new ScrollableViewGroup.OnCurrentViewChangedListener() {
            @Override
            public void onCurrentViewChanged(View view, int currentview) {
                setTopSelected(topArr[currentview]);
            }
        });
        mLv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //修改颜色
                changeColor(Color.WHITE);
                MediaUtils.CURPOSITION = position;
                changeColor(Color.GREEN);
                startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                //修改图标
                mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
            }
        });
        //进度条变化监听
        mSk_duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条变化回调
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSk_duration.setProgress(seekBar.getProgress());
                startMediaService("seek", seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 初始化界面方法
     */
    private void initView() {

        mTv_curduration = (TextView) findViewById(R.id.tv_curduration);
        mTv_minilrc = (TextView) findViewById(R.id.tv_minilrc);
        mTv_totalduration = (TextView) findViewById(R.id.tv_totalduration);
        mSk_duration = (SeekBar) findViewById(R.id.sk_duration);
        mIv_bottom_model = (ImageView) findViewById(R.id.iv_bottom_model);
        mIv_bottom_play = (ImageView) findViewById(R.id.iv_bottom_play);
        mLv_list = (ListView) findViewById(R.id.lv_list);
        mSvg_main = (ScrollableViewGroup) findViewById(R.id.svg_main);
        //默认选中第一个
        findViewById(R.id.ib_top_play).setSelected(true);
    }

    /**
     * 设置选中效果
     *
     * @param selectedId
     */
    private void setTopSelected(int selectedId) {
        //1.还原所有控件效果
        findViewById(R.id.ib_top_play).setSelected(false);
        findViewById(R.id.ib_top_list).setSelected(false);
        findViewById(R.id.ib_top_lrc).setSelected(false);
        findViewById(R.id.ib_top_volumn).setSelected(false);
        //2.让传递进来的控件有选中效果
        findViewById(selectedId).setSelected(true);
    }
    /**
     * 1.发送特定的广播,让操作系统更新多媒体数据
     * 2.系统扫描完成,会发出一个特定的的广播.我们只需要去监听特定的广播
     */
    MyBroadcastReceiver receiver = new MyBroadcastReceiver();
    public void reflash() {
        /**---------------接收系统扫描完成的广播---------------**/
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        //注册广播
        registerReceiver(receiver, filter);

        /**---------------发送广播,让系统更新媒体数据库---------------**/
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            MediaScannerConnection.scanFile(this, new String[]{ Environment
                    .getExternalStorageDirectory().getAbsolutePath() }, null, null);
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);
        }
    }


    /**
     * 集中处理按钮点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_top_play:
                mSvg_main.setCurrentView(0);//mSvg_main显示第一个孩子
                setTopSelected(R.id.ib_top_play);
                break;
            case R.id.ib_top_list:
                mSvg_main.setCurrentView(1);//mSvg_main显示第二个孩子
                setTopSelected(R.id.ib_top_list);
                break;
            case R.id.ib_top_lrc:
                mSvg_main.setCurrentView(2);//mSvg_main显示第三个孩子
                setTopSelected(R.id.ib_top_lrc);
                break;
            case R.id.ib_bottom_play://播放按钮,点击同一个按钮.有两个操作.需要定义一个变量进行控制
                if (MediaUtils.CURSTATE == IConstants.STATE_STOP) {
                    startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                    //修改图标
                    mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
                } else if (MediaUtils.CURSTATE == IConstants.STATE_play) {
                    startMediaService("pause");
                    mIv_bottom_play.setImageResource(R.drawable.img_playback_bt_play);
                } else if (MediaUtils.CURSTATE == IConstants.STATE_PAUSE) {
                    startMediaService("continue");
                    mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
                }
                break;
            //上一曲
            case R.id.ib_bottom_last:
                if (MediaUtils.CURPOSITION > 0) {

                    //找到当前播放的歌曲条目
                    changeColor(Color.WHITE);

                    MediaUtils.CURPOSITION--;
                    changeColor(Color.GREEN);
                    //2.播放
                    startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
                }

                break;
            //下一曲
            case R.id.ib_bottom_next:
                if (MediaUtils.CURPOSITION < MediaUtils.songList.size() - 1) {

                    //找到当前播放的歌曲条目
                    changeColor(Color.WHITE);

                    MediaUtils.CURPOSITION++;
                    changeColor(Color.GREEN);
                    startMediaService("play", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);

                }
                break;
            case R.id.ib_bottom_model:
                //模式切换
                if (MediaUtils.CURMODEL == IConstants.MODEL_NORMAL) {
                    MediaUtils.CURMODEL = IConstants.MODEL_RAMDOM;
                    mIv_bottom_model.setImageResource(R.drawable.icon_playmode_shuffle);
                } else if (MediaUtils.CURMODEL == IConstants.MODEL_RAMDOM) {
                    MediaUtils.CURMODEL = IConstants.MODEL_REPEAT;
                    mIv_bottom_model.setImageResource(R.drawable.icon_playmode_repeat);
                } else if (MediaUtils.CURMODEL == IConstants.MODEL_REPEAT) {
                    MediaUtils.CURMODEL = IConstants.MODEL_SINGLE;
                    mIv_bottom_model.setImageResource(R.drawable.icon_playmode_single);
                } else if (MediaUtils.CURMODEL == IConstants.MODEL_SINGLE) {
                    MediaUtils.CURMODEL = IConstants.MODEL_NORMAL;
                    mIv_bottom_model.setImageResource(R.drawable.icon_playmode_normal);
                }
                break;
            case R.id.ib_top_volumn:
                //音量设置
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                //最大音量
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                //参数
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxVolume/2,AudioManager.FLAG_PLAY_SOUND);
                break;

            case R.id.ib_bottom_update:
                reflash();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        //后退键回到桌面
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        //显示notification
        showNotification();

    }

    private void showNotification() {
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("music");
        builder.setContentText("正在运行");
        builder.setAutoCancel(true);
        //延迟意图
        Intent intent = new Intent(this,MainActivity.class);
        //延时意图
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(0,notification);
    }

    private void startMediaService(String option) {
        //启动服务.而且让服务播放音乐
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.putExtra("option", option);
        //创建一个装载handler的报信者
        intent.putExtra("messenger", new Messenger(handler));
        //启动服务
        startService(intent);
    }

    private void startMediaService(String option, int progress) {
        //启动服务.而且让服务播放音乐
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.putExtra("option", option);
        //创建一个装载handler的报信者
        intent.putExtra("messenger", new Messenger(handler));
        intent.putExtra("progress", progress);
        //启动服务
        startService(intent);
    }

    private void startMediaService(String option, String path) {
        //启动服务.而且让服务播放音乐
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.putExtra("option", option);
        intent.putExtra("path", path);
        intent.putExtra("messenger", new Messenger(handler));
        //启动服务
        startService(intent);
    }

    /**
     * 修改当前播放条目的颜色
     *
     * @param color
     */
    private void changeColor(int color) {
        TextView tvPreTextView = (TextView) mLv_list.findViewWithTag(MediaUtils.CURPOSITION);
        //修改颜色
        if (tvPreTextView != null) {
            tvPreTextView.setTextColor(color);
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "接收广播");
            //注销广播
            unregisterReceiver(receiver);
            //重新更新列表
            MediaUtils.initSongList(MainActivity.this);
            handler.sendEmptyMessage(IConstants.UPDATELIST);
        }
    }
}
