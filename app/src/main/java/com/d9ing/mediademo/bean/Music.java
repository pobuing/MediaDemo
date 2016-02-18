package com.d9ing.mediademo.bean;

/**
 * Music的java bean
 * Created by wx on 2016/2/16.
 */
public class Music {
    //名字
    public String name;
    //歌手
    public String artist;
    //路径
    public String path;

    public Music(String name, String artist, String path) {
        this.name = name;
        this.artist = artist;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Music{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
