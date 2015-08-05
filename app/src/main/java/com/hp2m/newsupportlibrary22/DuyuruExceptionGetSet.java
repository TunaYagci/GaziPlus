package com.hp2m.newsupportlibrary22;

/**
 * Created by Tuna on 7/30/2015.
 */
public class DuyuruExceptionGetSet {

    private int _id;
    private String header;
    private String link;

    public DuyuruExceptionGetSet(String header, String link) {
        this.header = header;
        this.link = link;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
