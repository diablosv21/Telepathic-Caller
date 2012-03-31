package com.tincan.telepathiccaller.entities;

import java.util.Comparator;

public class CallStatsComparator implements Comparator<CallStats> {
	private double callCountWeight;
	private double recentCountWeight;
	private double timeOfDayWeight;
	private double dayOfWeekWeight;
	
	public CallStatsComparator(double callCountWeight, double recentCountWeight, double timeOfDayWeight, double dayOfWeekWeight) {
		this.callCountWeight = callCountWeight;
		this.recentCountWeight = recentCountWeight;
		this.timeOfDayWeight = timeOfDayWeight;
		this.dayOfWeekWeight = dayOfWeekWeight;
	}
	
	@Override
	public int compare(CallStats callStats1, CallStats callStats2) {
		double score1 = callStats1.getScore(callCountWeight, recentCountWeight, timeOfDayWeight, dayOfWeekWeight);
		double score2 = callStats2.getScore(callCountWeight, recentCountWeight, timeOfDayWeight, dayOfWeekWeight);

        if (score1 < score2){
            return +1;
        }else if (score1 > score2){
            return -1;
        }else{
            return 0;
        }
	}
}
