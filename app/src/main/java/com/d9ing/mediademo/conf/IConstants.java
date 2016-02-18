package com.d9ing.mediademo.conf;

/**
 * 控制常量接口
 * Created by wx on 2016/2/17.
 */
public interface IConstants {
    int STATE_STOP = 1001;//停止状态
    int STATE_play = 1002;//播放状态
    int STATE_PAUSE = 1003;//暂停状态
    //暂停状态
    int MSG_ONPREPARED = 1004;
    int MSG_ONCOMPLETION = 1005;
    int MODEL_NORMAL = 1006;
    int MODEL_REPEAT = 1007;
    int MODEL_SINGLE = 1008;
    int MODEL_RAMDOM =1009;
    int UPDATELIST = 1010;
}
