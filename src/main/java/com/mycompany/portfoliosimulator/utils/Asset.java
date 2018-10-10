package com.mycompany.portfoliosimulator.utils;

import java.util.*;

public class Asset {

    public String name; // asset's name
    public String[] headers; // column's headers
    public ArrayList<HashMap<String,Object>> data;
    public Date minDate;
    public Date maxDate;

    public Asset(String filename, String strData, Params settings){
        this.data = parseData(filename, strData, settings);
    }

    private ArrayList<HashMap<String,Object>> parseData(String filename, String strData, Params settings){

        ArrayList<HashMap<String,Object>> data = new ArrayList<>();

        Scanner scanner = new Scanner(strData);
        this.name = getName(scanner.nextLine());
        this.headers = checkIndicator(getContents(scanner.nextLine()), settings);

        if(name == null || headers == null)
            Utils.error("Error while reading "+filename+": name and/or headers may be null.", true);

        HashMap<String,Object> rec;
        int l = 3;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] arr = getContents(line);

            if(arr.length != headers.length)
                Utils.error("while reading file \""+filename+"\" at line "+l+": \n\t"+line, false);

            try {
                rec = new HashMap<>();

                for (String key: headers) {
                    String val = arr[Utils.arrIndexOf(headers, key)];

                    if(key.equals("Date")) { // is a date
                        Date d = Utils.parseDate(val);
                        rec.put(key.toLowerCase(), d);
                        if( (minDate == null) || d.before(minDate)) minDate = d;
                        if( (maxDate == null) || d.after(maxDate)) maxDate = d;
                    }
                    else {
                        if (val.contains("%"))
                            val = val.replace("%", "");
                        rec.put(key.toLowerCase(), Float.parseFloat(val));
                    }
                }
                data.add(rec);

            } catch (Exception e){
                Utils.exception(e);
                System.out.println("\t while reading file \""+filename+"\" at line "+l+".");
            }

            l++;
        }

        scanner.close();
        return data;
    }

    private String getName(String line){
        String[] s = line.split("\t");
        return s.length == 1? s[0]: null;
    }

    private String[] getContents(String line){
        return line.split("\t");
    }

    private String[] checkIndicator(String[] contents, Params settings){
        for (String s: contents)
            if(s.toLowerCase().equals(settings.selector))
                return contents;
        return null;
    }

}
