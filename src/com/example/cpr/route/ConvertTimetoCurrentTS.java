package com.example.cpr.route;

import java.util.Calendar;

public class ConvertTimetoCurrentTS {
	
	private final Calendar calendar;
	private int currentTS;
	
	public ConvertTimetoCurrentTS(Calendar calendar){
		this.calendar = calendar;
	}

	public int getCurrentTS() {
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY); 
		if(0 < hour && hour <=6 ) { 
			currentTS = 1;
		} else if (6 < hour && hour <= 8) { 
			currentTS = 2;
		} else if (8 < hour && hour <= 12) { 
			currentTS = 3;
		} else if (12 < hour && hour < 14) { 
			currentTS = 4;
		} else if (14 < hour && hour <= 18) { 
			currentTS = 5;
		} else if (18 < hour && hour <= 20) { 
			currentTS = 6;
		} else if (20 < hour && hour <= 23) { 
			currentTS = 7;
		}		
		
		return currentTS;
	}
}
