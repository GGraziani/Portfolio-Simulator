package com.mycompany.portfoliosimulator.utils;

import java.util.ArrayList;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

public class  Params {

    public File[] assets;
    public ArrayList<String> timeframes;
    public String timeframe;
    public int day;
    public int portfolio;
    public int cutoff;
    public String selector;
    public Date begin;
    public Date end;
    private SimpleDateFormat sdf;

    Params(){
        sdf = new SimpleDateFormat("dd.MM.yyyy");
    }

    public Params(ArrayList<String> timeframes, String timeframe, int day, int portfolio, int cutoff, String selector){
        this.timeframes = timeframes;
        this.timeframe = timeframe;
        this.day = day;
        this.portfolio = portfolio;
        this.cutoff = cutoff;
        this.selector = selector;
    }

    void setBegin(String str) {

        try {
            this.begin = sdf.parse(str);
        } catch (Exception e) {
            Utils.exception(e);
            System.exit(1);
        }
    }

    void setEnd(String str) {
        try {
            this.end = sdf.parse(str);
        } catch (Exception e) {
            Utils.exception(e);
            System.exit(1);
        }
    }


    @Override
    public String toString() {

        return "\n\t\t\u25CF assets: "+Utils.fileArrToString(assets)+
                "\n\t\t\u25CF timeframes: "+Utils.strArrToString(timeframes)+
                "\n\t\t\u25CF timeframe: "+timeframe+
                "\n\t\t\u25CF day: "+day+
                "\n\t\t\u25CF portfolio: "+portfolio+
                "\n\t\t\u25CF cutoff: "+cutoff+
                "\n\t\t\u25CF begin: "+begin+
                "\n\t\t\u25CF end: "+end+
                "\n\t\t\u25CF selector: "+selector+
                "\n";
    }
}