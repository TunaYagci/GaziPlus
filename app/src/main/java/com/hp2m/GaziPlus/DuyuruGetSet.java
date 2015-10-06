package com.hp2m.GaziPlus;

/**
 * Created by Tuna on 7/19/2015.
 */
public class DuyuruGetSet {
    private int _id;
    private String title, content, tarih, contentLinks, newsLinks, newORold, imageLinks;

    public DuyuruGetSet(String title, String content, String tarih, String contentLinks, String newsLinks, String newORold, String imageLinks) {
        this.title = title;
        this.content = content;
        this.tarih = tarih;
        this.contentLinks = contentLinks;
        this.newsLinks = newsLinks;
        this.newORold = newORold;
        this.imageLinks = imageLinks;
    }

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getNewORold() {
        return newORold;
    }

    public void setNewORold(String newORold) {
        this.newORold = newORold;
    }

    public String getNewsLinks() {
        return newsLinks;
    }

    public void setNewsLinks(String newsLinks) {
        this.newsLinks = newsLinks;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getContentLinks() {
        return contentLinks;
    }

    public void setContentLinks(String contentLinks) {
        this.contentLinks = contentLinks;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
