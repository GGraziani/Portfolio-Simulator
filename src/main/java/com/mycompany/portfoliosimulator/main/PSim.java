package com.mycompany.portfoliosimulator.main;

import com.mycompany.portfoliosimulator.simlogics.Sim;
import com.mycompany.portfoliosimulator.utils.*;
import com.mycompany.portfoliosimulator.io.*;

import java.util.ArrayList;
import java.util.HashMap;

public class PSim {

    public static void main(String[] args) {

        Params settings = Utils.init(args);
        Utils.printReport(settings);

        ArrayList<Asset> assets = Utils.setAssetsData(settings.assets, settings);

        Sim.makeRanking(assets, settings);
    }
}