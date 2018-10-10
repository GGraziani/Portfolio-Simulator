package com.mycompany.portfoliosimulator.utils;

import com.mycompany.portfoliosimulator.io.Reader;
import com.mycompany.portfoliosimulator.io.XMLParser;

import java.io.File;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.math.BigDecimal;

public class Utils {


//    INITIALIZATION

    public static Params init(String[] args){
        prints("Running Portfolio Simulator...(run time: "+ getCurrDate()+")");
        return initSettings(args, getDefauts("./params/defaults.xml"));
    }

    private static Params getDefauts(String path) {
        printss("Getting default settings from: "+path);
        return new XMLParser(path).parse();
    }

    private static Params initSettings(String[] args, Params defaults){
        return sanityCheck(defaults, parseArgs(args, defaults.timeframes));
    }

    private static Params parseArgs(String[] args, ArrayList<String> timeframes){
        printssl("Getting user settings ...");
        Params settings = new Params();

        if((settings.assets = getAssetsFiles(args)) == null)
            error("\""+args[0]+"\" is an invalid path for assets!", true);

        settings.timeframes = new ArrayList<>(timeframes);

        for(String s : arrTail(args,1)){
            String[] str = s.split("=");

            switch (str[0].toLowerCase()) {
                case "-t": case "-timeframe":
                    if(!settings.timeframes.contains(str[1]))
                        error("Wrong input for timeframe: \""+s+"\" is an invalid argument." +
                                "\n\tPlease chose between the available options: "+Utils.strArrToString(settings.timeframes), true);
                    settings.timeframe = str[1];
                    break;
                case "-d": case "-day":
                    settings.day = toInt(str[1]);
                    catchIntegerErr(settings.day, s);
                    break;
                case "-p": case "-portfolio":
                    settings.portfolio = toInt(str[1]);
                    catchIntegerErr(settings.portfolio, s);
                    if(settings.cutoff > 0 && settings.portfolio > settings.cutoff)
                        error("Wrong input: portfolio value should be less or equal to cutoff value (e.g. p <= c)", true);
                    break;
                case "-c": case "-cutoff":
                    settings.cutoff = toInt(str[1]);
                    catchIntegerErr(settings.cutoff, s);
                    if(settings.portfolio > 0 && settings.portfolio > settings.cutoff)
                        error("Wrong input: cutoff value should be greater or equal to portfolio value (e.g. c >= p)", true);
                    break;
                case "-b": case "-begin":
                    settings.setBegin(str[1]);
                    if(settings.end != null && settings.begin.after(settings.end))
                        error("Wrong input: begin value should be a date that comes before end value", true);
                    break;
                case "-e": case "-end":
                    settings.setEnd(str[1]);
                    if(settings.begin != null && settings.end.before(settings.begin))
                        error("Wrong input: end value should be a date that comes after begin value", true);
                    break;
                case "-s": case "-selector":
                    settings.selector = str[1];
                    break;
            }
        }
        print("OK");

        return settings;
    }

    private static Params sanityCheck(Params defaults, Params settings) {

        printssl("Running sanity tests ...");

//        prints("SETTINGS START");
//        print(settings.toString());

        settings.timeframe = settings.timeframe == null ? defaults.timeframe : settings.timeframe;

        settings.day = settings.day == 0 ? defaults.day : settings.day;
        int d = settings.timeframe.equals("1w")? 7 : settings.timeframe.equals("2w")? 14 : 28;
        if(settings.day > d)
            error("\""+settings.day+"\" is an invalid value for day!", true);

        settings.cutoff = settings.cutoff == 0 ? defaults.cutoff : settings.cutoff;
        if(settings.cutoff > settings.assets.length)
            error("\""+settings.cutoff+"\" is an invalid value for cutoff: the value can not be greater than the number of assets!", true);

        settings.portfolio = settings.portfolio == 0 ? defaults.portfolio : settings.portfolio;
        if(settings.portfolio > settings.cutoff)
            error("\""+settings.portfolio+"\" is an invalid value for portfolio: the value can not be greater than the cutoff!", true);

        settings.selector = settings.selector == null ? defaults.selector : settings.selector;
//        prints("SETTINGS END");
//        print(settings.toString());

        print("OK");

        return settings;
    }

    public static ArrayList<Asset> setAssetsData(File[] files, Params settings){
        ArrayList<Asset> assets = new ArrayList<>();

        for (File file : files) {
            assets.add(new Asset(file.getName(), Reader.readTextFile(file), settings));
        }

        return assets;
    }


    //    UTILITY METHODS

    public static Date parseDate(String str) throws ParseException {
        return new SimpleDateFormat("dd.MM.yyyy").parse(str);
    }

    private static String getCurrDate(){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return dateFormat.format(new Date());
    }

    private static Calendar getCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        return cal;
    }

    private static int getYear(Date date){
        return getCalendar(date).get(Calendar.YEAR);
    }

    private static int getMonth(Date date){
        return getCalendar(date).get(Calendar.MONTH);
    }

    private static int getDay(Date date){
        return getCalendar(date).get(Calendar.DAY_OF_MONTH);
    }

    public static Date findMinDate(ArrayList<Asset> assets) {
        Date min = null;
        for (Asset asset : assets)
            if( (min == null) || asset.minDate.before(min)) min = asset.minDate;
        return min;
    }

    public static Date findMaxDate(ArrayList<Asset> assets) {
        Date max = null;
        for (Asset asset : assets)
            if( (max == null) || asset.maxDate.after(max)) max = asset.maxDate;
        return max;
    }

    public static Date fixMinDate(Date date, String timeframe) {
        Calendar cal = getCalendar(date);

        switch (timeframe) {
            case "1w": case "2w":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case "m":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
        }
        return cal.getTime();
    }

    private static boolean hasNextTimeframe(Date a, Date end){
        return a.before(end);
    }

    private static Date getNextTimeframeStart(Date end){
        Calendar cal = getCalendar(end);
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    private static Date getNextTimeframeEnd(Date begin, String timeframe){

        Calendar cal = getCalendar(begin);
        switch (timeframe){
            case "1w":
                // plus 6 days, 23 hours and 59 minutes
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+6);
                break;
            case "2w":
                // plus 13 days, 23 hours and 59 minutes
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+13);
                break;
            case "m":
                // plus 1 month
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-1);
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return cal.getTime();
    }

	public static ArrayList<Interval> getIntervalList(Interval total, Params settings){
    	ArrayList<Interval> t = new ArrayList<>();

		Date a = new Date(total.getStart().getTime()), b;

		while (Utils.hasNextTimeframe(a, total.getEnd())){
			b = Utils.getNextTimeframeEnd(a, settings.timeframe);
			t.add(new Interval(a, b));
			a = Utils.getNextTimeframeStart(b);
		}
		return t;
	}

    private static String delimiter(){
        return "\n--------------------------------------------------------------------------------\n";
    }

    private static int toInt(String s){
        try {
            return Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return -1;
        }
    }

    public static Float round(Float number, int decimalPlace) {
        if(Float.isNaN(number))
        	return null;
        return new BigDecimal(number).setScale(decimalPlace, RoundingMode.HALF_EVEN).floatValue();
    }

    private static void prints(String s){
        System.out.println(delimiter()+"\t"+s+delimiter());
    }

    private static void printss(String s){
        System.out.println("\t- "+s);
    }

    private static void printssl(String s){
        System.out.print("\t- "+s+"\t");
    }

    private static void print(String s){
        System.out.println(s);
    }

    public static void printRanking(Interval interval, ArrayList<Pair> ranking, int iteration){
    	if(iteration != -1){
		    System.out.println("> Interval "+iteration+" ["+interval.getStart()+" --> "+interval.getEnd()+"]");
	    }
	    System.out.println("\t-------------------------");
	    for (Pair pair : ranking) {
		    System.out.println("\t|\t"+pair.val0+"\t"+pair.val1+"\t|");
	    }
	    System.out.println("\t-------------------------");
    }

    public static void printReport(Params settings){
        printss("Settings: "+settings.toString());
    }

    public static void exception(Exception e){
        System.out.println("\n Exception:\n\t caught a " + e.getClass() + "\n\t with message: " + e.getMessage());
    }

    public static void error(String s, Boolean fatal){
        System.out.println("\n Error:\n\t"+s);
        if(fatal)
            System.exit(1);
    }

    private static void catchIntegerErr(Integer i, String s){
        if(i <= 0)
            error("Wrong input for portfolio: \""+s+"\" is an invalid argument." +
                    "\n\tPlease choose a positive integer (e.g. N = {0, 1, 2, ...}).\n", true);
    }

    private static String[] arrTail(String[] arr, int a){
        return Arrays.copyOfRange(arr, a, arr.length);
    }

    public static int arrIndexOf(String[] arr, String i) {
        for (int j = 0; j < arr.length; j++)
            if(arr[j].equals(i))
                return j;
        return -1;
    }

    private static File[] getAssetsFiles(String[] args){

        try {
            String path = args[0];

            if(path.startsWith("-")){
                return null;
            } else if(path.equals("dev"))
                path = "./data/temp";
//                path = "./data/dev-assets";

            File dir = new File(path);
            File[] files = dir.listFiles();

            return dir.exists() && dir.isDirectory() && files != null? files : null;
        } catch (Exception e){
            error("No path has been entered for for assets!", true);
        }
        return null;
    }

    public static String strArrToString(ArrayList<String> arr){
        if(arr == null || arr.isEmpty())
            return null;
        StringBuilder s = new StringBuilder();
        s.append("[").append(arr.get(0));

        for (int i = 1; i < arr.size(); i++) {
            s.append(", ").append(arr.get(i));
        }
        return s.append("]").toString();
    }

    public static String fileArrToString( File[] arr){
        if(arr == null || arr.length == 0)
            return null;
        StringBuilder s = new StringBuilder();
        s.append("\n\t\t\t1) ").append(arr[0].getName());

        for (int i = 1; i < arr.length; i++) {
            s.append("\n\t\t\t").append((i+1)).append(") ").append(arr[i].getName());
        }
        return s.append("\n").toString();
    }
}
