package com.mycompany.portfoliosimulator.io;

import com.mycompany.portfoliosimulator.utils.Asset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Reader {

    public static String readTextFile(File file){
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
