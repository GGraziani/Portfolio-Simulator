package com.mycompany.portfoliosimulator.simlogics;


import com.mycompany.portfoliosimulator.utils.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class Sim {

	public static void makeRanking(ArrayList<Asset> assets, Params settings) {

		Interval tInterval = new Interval(
				Utils.fixMinDate(settings.begin == null? Utils.findMinDate(assets) : settings.begin, settings.timeframe),
				settings.end == null? Utils.findMaxDate(assets) : settings.end);

//      Absolute ranking and weighted ranking (based on previous wRanking)
		HashMap<Interval, ArrayList<Pair>> ranking = new HashMap<>();
		ArrayList<Interval> Intervals = Utils.getIntervalList(tInterval, settings);

		for (Asset asset : assets) {
			int i = 0;
			for (Interval interval : Intervals) {
				if(!ranking.containsKey(interval)) ranking.put(interval, new ArrayList<>());

				int count = 0;
				float acc = 0f;

//				System.out.println("- INTERVAL ("+asset.name+"): "+interval.getStart()+" - "+interval.getEnd());

				for(; i < asset.data.size(); i++){
					Date d = (Date) asset.data.get(i).get("date");
					String indicator = asset.data.get(i).get(settings.selector).toString();

					if(interval.contains(d, true)){
						acc+=Float.parseFloat(indicator);
						count++;
//						System.out.println("\tTRUE --> "+d+" : "+indicator+"; count:"+count);
					} else {
//						System.out.println("\tFALSE --> "+d);
						if(interval.after(d)){
//							System.out.println("\tBREAK: "+count+": "+acc);
//							System.out.println("\t\t ratio: "+Utils.round(acc/count, 2));

							ranking.get(interval).add(new Pair(asset.name,Utils.round(acc/count, 2)));

							break;
						}
					}
				}
			}
		}

		int c = 1;
		Interval previous = null;
		for (Interval interval : Intervals) {

			Collections.sort(ranking.get(interval));


			Utils.printRanking(interval, ranking.get(interval),c);
			if(previous != null){
//				weighted ranking

				Utils.printRanking(previous, ranking.get(previous),-1);
			}

			previous = interval;

//			System.out.println("> Interval "+c+" ["+interval.getStart()+" --> "+interval.getEnd()+"]");
//			System.out.println("\t-------------------------");
//			for (Pair pair : ranking.get(interval)) {
//				System.out.println("\t|\t"+pair.val0+"\t"+pair.val1+"\t|");
//			}
//			System.out.println("\t-------------------------");
			c++;
		}
	}
}