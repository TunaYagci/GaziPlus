package com.hp2m.GaziPlus;

/**
 * Created by Tuna on 7/17/2015.
 */
public class YemekGetSet {
    private int _id;
    private String yemek1, yemek2, yemek3, yemek4, tarih;

    public YemekGetSet() {
    }

    public YemekGetSet(String tarih, String yemek1, String yemek2, String yemek3, String yemek4) {
        this.tarih = tarih;
        this.yemek1 = yemek1;
        this.yemek2 = yemek2;
        this.yemek3 = yemek3;
        this.yemek4 = yemek4;

    }

    public String getYemek1() {
        return yemek1;
    }

    public void setYemek1(String yemek1) {
        this.yemek1 = yemek1;
    }

    public String getYemek2() {
        return yemek2;
    }

    public void setYemek2(String yemek2) {
        this.yemek2 = yemek2;
    }

    public String getYemek3() {
        return yemek3;
    }

    public void setYemek3(String yemek3) {
        this.yemek3 = yemek3;
    }

    public String getYemek4() {
        return yemek4;
    }

    public void setYemek4(String yemek4) {
        this.yemek4 = yemek4;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public int get_id() {
        return _id;
    }


    public void set_id(int _id) {
        this._id = _id;
    }
}
