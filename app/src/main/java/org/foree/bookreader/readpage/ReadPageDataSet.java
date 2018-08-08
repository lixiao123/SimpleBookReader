package org.foree.bookreader.readpage;

import java.io.Serializable;

/**
 * Created by foree on 17-2-23.
 * ReadFragment 使用的数据集
 */

public class ReadPageDataSet implements Serializable {
    private String title;
    private String contents;
    private int pageNum;
    private int index;
    private String url;
    private int batteryLevel;

    public ReadPageDataSet(String url, String title, String contents, int pageNum, int index, int batteryLevel) {
        this.url = url;
        this.title = title;
        this.contents = contents;
        this.pageNum = pageNum;
        this.index = index;
        this.batteryLevel = batteryLevel;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getIndex() {
        return index;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public String toString() {
        return "ReadPageDataSet{" +
                "title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", pageNum=" + pageNum +
                ", index=" + index +
                ", url='" + url + '\'' +
                ", batteryLevel=" + batteryLevel +
                '}';
    }
}
