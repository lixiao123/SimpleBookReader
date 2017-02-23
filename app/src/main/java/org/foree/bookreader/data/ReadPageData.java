package org.foree.bookreader.data;

import java.io.Serializable;

/**
 * Created by foree on 17-2-23.
 * ReadFragment 使用的数据集
 */

public class ReadPageData implements Serializable {
    private String title;
    private String contents;
    private int pageNum;
    private int index;

    public ReadPageData(String title, String contents, int pageNum, int index) {
        this.title = title;
        this.contents = contents;
        this.pageNum = pageNum;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public String getPageNum() {
        return pageNum + "";
    }

    public String getIndex() {
        return index + "";
    }
}
