package com.hp2m.GaziPlus;

/**
 * Created by Tuna on 7/23/2015.
 */
public class NotInformation {

    String name, ogrNo, imageLink, genelOrtNumber; // HEADER
    String donemAdi; // SUB HEADER
    String dersKodu, dersAdi, vizeNotu, finalNotu, butNotu, basariNotu, kredi, sinifOrt; // ITEMS

    @Override
    public String toString() {
//        Log.i("tuna", donemAdi);
        try {
            return donemAdi;
        } catch (NullPointerException e) {
            return "zort";
        }
    }
}
