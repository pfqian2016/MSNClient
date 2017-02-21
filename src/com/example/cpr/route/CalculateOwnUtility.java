package com.example.cpr.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CalculateOwnUtility {
	private Context context; 

	private Map<String, Map<String, ArrayList<Double>>> contactMap = new HashMap<String, Map<String, ArrayList<Double>>>();//last week contact log
	private Map<String, Map<String, ArrayList<Double>>> contactWeightMap = new HashMap<String, Map<String, ArrayList<Double>>>();
	private Map<String, ArrayList<Double>> nijMap = new HashMap<String, ArrayList<Double>>();
	private Map<String, ArrayList<Double>> wnijMap = new HashMap<String, ArrayList<Double>>();
	private Map<String, ArrayList<Double>> probMap = new HashMap<String, ArrayList<Double>>();	//every day contact log 
	
	private Cursor weekCursor;
	private Cursor dayCursor; 
	public static final int TS_NUMBER = 7;
	public static final double[] w = {0, 0.2, 0.12, 0.12, 0.12, 0.12, 0.12, 0.2}; 
	
	public CalculateOwnUtility(Context context) {
		this.context = context; 
	}
	
	private void initOwnContactMap() {
		OneWeekContactLog oneWeek = new OneWeekContactLog(context, "oneWeek.db");
		SQLiteDatabase db1 = oneWeek.getReadableDatabase();
		weekCursor = db1.rawQuery("select * from oneWeekContactLogTable", null); 
		if (weekCursor.getCount() == 0) {
			return;
		}
 
		while (weekCursor.moveToNext()) { 
			if ((weekCursor.getCount() - weekCursor.getPosition()) < 7) {
				double value = 0.12;
				String date = weekCursor.getString(weekCursor.getColumnIndex("n_dayago"));
				
				OneDayContactLog oneDayContact = new OneDayContactLog(context, date + ".db");
				SQLiteDatabase db2 = oneDayContact.getWritableDatabase();
				dayCursor = db2.rawQuery("select * from oneDayContactLogTable", null);
				if (dayCursor.getCount() == 0) {
					return;
				}
				Map<String, ArrayList<Double>> lableContactMap = new HashMap<String, ArrayList<Double>>();
				Map<String, ArrayList<Double>> lableContactWeightMap = new HashMap<String, ArrayList<Double>>();
				while (dayCursor.moveToNext()) {
					String lable = dayCursor.getString(dayCursor.getColumnIndex("contactLable")); 
					ArrayList<Double> contactList = new ArrayList<Double>();
					ArrayList<Double> contactWeightList = new ArrayList<Double>();
					double TS1 = dayCursor.getDouble(dayCursor.getColumnIndex("TS1"));
					double TS2 = dayCursor.getDouble(dayCursor.getColumnIndex("TS2"));
					double TS3 = dayCursor.getDouble(dayCursor.getColumnIndex("TS3"));
					double TS4 = dayCursor.getDouble(dayCursor.getColumnIndex("TS4"));
					double TS5 = dayCursor.getDouble(dayCursor.getColumnIndex("TS5"));
					double TS6 = dayCursor.getDouble(dayCursor.getColumnIndex("TS6"));
					double TS7 = dayCursor.getDouble(dayCursor.getColumnIndex("TS7")); 
					contactList.add(TS1);
					contactList.add(TS2);
					contactList.add(TS3);
					contactList.add(TS4);
					contactList.add(TS5);
					contactList.add(TS6);
					contactList.add(TS7);
					lableContactMap.put(lable, contactList);
					if (weekCursor.isLast()) {
						value = 0.2;
					} else if (weekCursor.getCount() == 8 && weekCursor.isFirst()) {
						value = 0.2;
					}
//					contactWeightList.add(w[day] * TS1);
//					contactWeightList.add(w[day] * TS2);
//					contactWeightList.add(w[day] * TS3);
//					contactWeightList.add(w[day] * TS4);
//					contactWeightList.add(w[day] * TS5);
//					contactWeightList.add(w[day] * TS6);
//					contactWeightList.add(w[day] * TS7);
					
					contactWeightList.add(value * TS1);
					contactWeightList.add(value * TS2);
					contactWeightList.add(value * TS3);
					contactWeightList.add(value * TS4);
					contactWeightList.add(value * TS5);
					contactWeightList.add(value * TS6);
					contactWeightList.add(value * TS7);
					lableContactWeightMap.put(lable, contactWeightList);
				}
				contactMap.put(date, lableContactMap);
				contactWeightMap.put(date, lableContactWeightMap); 
			} 
		} 
	}
	
//	public void testOwnContactMap() {
//		initOwnContactMap();
//		System.out.println("contactMap size:" + contactMap.size());
//		for (String date : contactMap.keySet()) {
//			System.out.println("date:" + date);
//			Map<String, ArrayList<Double>> dayMap = contactMap.get(date);
//			Map<String, ArrayList<Double>> dayWeuMap = contactWeightMap.get(date);
//			for (String lable : dayMap.keySet()) {
//				System.out.println("testcontactMap lable : " + lable);
//				ArrayList<Double> contactList = dayMap.get(lable);
//				ArrayList<Double> contactWeList = dayWeuMap.get(lable);
//				for (int i = 0; i < contactList.size(); i++) {
//					System.out.println("contact number:" + contactList.get(i));
//					System.out.println("contact weight:" + contactWeList.get(i));
//				}
//			}
//		} 
//	}
	
	private void calOwnContactnijMap() {
		initOwnContactMap();
//		for (String date : contactMap.keySet()) {
//			Map<String, ArrayList<Double>> daycontactMap = contactMap.get(date);
//			Map<String, ArrayList<Double>> daycontactWeightMap = contactWeightMap.get(date);
//			for (String lable : daycontactMap.keySet()) {
//				ArrayList<Double> contactList = daycontactMap.get(lable);
//				ArrayList<Double> contactWeiList = daycontactWeightMap.get(lable);
//
//				ArrayList<Double> contactNumberList = new ArrayList<Double>();
//				ArrayList<Double> contactWeightList = new ArrayList<Double>(); 
//				
//				if (!nijMap.containsKey(lable)) {
//					for (int j = 0; j < contactList.size(); j++) { 
//						double contactNum = contactList.get(j);
//						double contactWeight = contactWeiList.get(j); 
//						contactNumberList.add(contactNum);
//						contactWeightList.add(contactWeight);
//					}  
//				} else {
//					ArrayList<Double> contactNum = nijMap.get(lable);
//					ArrayList<Double> contactWei = wnijMap.get(lable);
//					for (int j = 0; j < contactList.size(); j++) {
//						
//						double contactNum1 = contactList.get(j);
//						double contactNum2 = contactNum.get(j);
//						contactNumberList.add(contactNum1 + contactNum2);
//						
//						double contactWeight1 = contactWeiList.get(j); 
//						double contactWeight2 = contactWei.get(j);
//						contactWeightList.add(contactWeight1 + contactWeight2);
//					} 
//				}
//				
//				//nijMap.remove(lable);
//				nijMap.put(lable, contactNumberList);
//				wnijMap.put(lable, contactWeightList);
//			} 
//		}
		for (String date : contactMap.keySet()) {
			Map<String, ArrayList<Double>> daycontactMap = contactMap.get(date);
			Map<String, ArrayList<Double>> daycontactWeightMap = contactWeightMap.get(date);
			for (String lable : daycontactMap.keySet()) {
				ArrayList<Double> contactList = daycontactMap.get(lable);
				ArrayList<Double> contactWeiList = daycontactWeightMap.get(lable);

				ArrayList<Double> contactNumberList = new ArrayList<Double>();
				ArrayList<Double> contactWeightList = new ArrayList<Double>(); 
				
				if (!nijMap.containsKey(lable)) {
					for (int j = 0; j < contactList.size(); j++) { 
						double contactNum = contactList.get(j);
						double contactWeight = contactWeiList.get(j); 
						contactNumberList.add(contactNum);
						contactWeightList.add(contactWeight);
					}  
				} else {
					ArrayList<Double> contactNum = nijMap.get(lable);
					ArrayList<Double> contactWei = wnijMap.get(lable);
					for (int j = 0; j < contactList.size(); j++) {
						
						double contactNum1 = contactList.get(j);
						double contactNum2 = contactNum.get(j);
						contactNumberList.add(contactNum1 + contactNum2);
						
						double contactWeight1 = contactWeiList.get(j); 
						double contactWeight2 = contactWei.get(j);
						contactWeightList.add(contactWeight1 + contactWeight2);
					} 
				}
				 
				nijMap.put(lable, contactNumberList);
				wnijMap.put(lable, contactWeightList);
			} 
		}
	}
	
	
//	public void testcalOwnContactnijMap() {
//		calOwnContactnijMap();
//		System.out.println("test testcalOwnContactnijMap(): " + nijMap.size());
//		 
//		for (String lable : nijMap.keySet()) {
//			ArrayList<Double> contactList = nijMap.get(lable);
//			ArrayList<Double> weightList = wnijMap.get(lable);
//			System.out.println("test lable:" + lable);
//			for (int i = 0; i < contactList.size(); i++) {
//				System.out.println("contact number:" + contactList.get(i));
//				System.out.println("contact weight:" + weightList.get(i));
//			}
//		} 
//	}
	
	private void calOwnContactProbability() {
		calOwnContactnijMap();
//		for (String lable : nijMap.keySet()) {
//			ArrayList<Double> prob = new ArrayList<Double>(); 
//			ArrayList<Double> contactList = nijMap.get(lable);
//			ArrayList<Double> weightList = wnijMap.get(lable);
//			for (int i = 0; i < weightList.size(); i++) {
//
//				double p = 0.0;
//				if (contactList.get(i) == 0) {
//					p += 0.0;
//				} else {
//					p += weightList.get(i) / contactList.get(i);
//				}
//				prob.add(p);
//			}
//			
//			probMap.put(lable, prob);
//		}
		for (String lable : nijMap.keySet()) {
			ArrayList<Double> prob = new ArrayList<Double>(); 
			ArrayList<Double> contactList = nijMap.get(lable);
			ArrayList<Double> weightList = wnijMap.get(lable);
//			System.out.println("test lable:" + lable);			
//			System.out.println("test weightList.size():" + weightList.size());

			
			for (int i = 0; i < weightList.size(); i++) {
//				System.out.println("calContactProbability number:" + contactList.get(i));
//				System.out.println("calContactProbability weight:" + weightList.get(i));
				double p = 0.0;
				if (contactList.get(i) == 0) {
					p = 0.0;
				} else {
					p = (weightList.get(i) / contactList.get(i));
				}
				prob.add(p);
			}
			
			probMap.put(lable, prob);
		}
	}
	
//	public void testProbMap() {
//		calOwnContactProbability();
//		for (String lable : probMap.keySet()) {
//			ArrayList<Double> p = probMap.get(lable);
//			System.out.println("lable : " + lable);
//			for (int i = 0; i < p.size(); i++) {
//				System.out.println("p:" + p.get(i));
//			}
//		}
//	}
	
	private Map<String, Double> calculateUtility(int current) {
		calOwnContactProbability(); 
		current = current - 1;
		Map<String, Double> utility = new HashMap<String, Double>();
//		System.out.println("current:" + current);
		for (String lable : probMap.keySet()) {
			ArrayList<Double> probList = probMap.get(lable);
//			System.out.println("probList.size():" + probList.size());
			double uti = 0.0; 
			for (int j = 0; j < probList.size(); j++) {
//				System.out.println("lable:" + lable + " prob:" + probList.get(j) + " current:" + current);
				if (Math.abs(j - current) < 4) {
					if (Math.abs(j - current) == 0) {
						uti += probList.get(j);
					} else { 
						uti += probList.get(j) / Math.abs(j -  current);
					}
				}
			}  
			 
//			System.out.println("uti:" + uti);
			utility.put(lable, Double.valueOf(uti));
		}
		return utility;
	}
	
//	public void testcalculateUtility(int current) {
//		Map<String, Double> utilityMap = calculateUtility(current);
//		for (String lable : utilityMap.keySet()) {
//			System.out.println("testcalculateUtility lable:" + lable);
//			System.out.println("testcalculateUtility utility:" + utilityMap.get(lable)); 
//		}
//	}
	
	public Map<String, Double> getOwnUtility(int current) { 
		Map<String, Double> ownUtilityMap = calculateUtility(current);
//		System.out.println("ownUtilityMap size:" + ownUtilityMap.size());
		return ownUtilityMap;
	}
	
//	private void init() {
//		OneWeekContactLog oneWeek = new OneWeekContactLog(context, "oneWeek.db");
//		SQLiteDatabase db = oneWeek.getReadableDatabase();
//		weekCursor = db.rawQuery("select * from oneWeekContactLogTable", null);   
//		weekCursor.moveToFirst();
//		String lables = weekCursor.getString(weekCursor.getColumnIndex("totalLB"));
//		String[] lable = lables.split(DealOneWeekContactLog.LB_TAG);
//		lableNumber = lable.length;
//		 
//		utility = new double[lableNumber];
//		prob = new double[lableNumber][TS_NUMBER];
//		contactNumber = new int[lableNumber][TS_NUMBER];
//		contactWeight = new double[lableNumber][TS_NUMBER];
//		 
//	}
//	
//	private void calContactProbability() {
//		
//		calContactValue();
//		for (int i = 1; i <= lableNumber; i++) {
//			
//			for (int j = 1; j < TS_NUMBER; j++) {
//				
//				if (contactNumber[i][j] != 0) {
//					prob[i][j] = contactWeight[i][j] / contactNumber[i][j];
//				}
//			}
//		}
//	}
//	 
//	private void calContactValue() { 
//		
//		int k = 1;
//		while (weekCursor.moveToNext()) {
//			
//			String date = weekCursor.getString(weekCursor.getColumnIndex("n_dayago"));
//			
//			OneDayContactLog oneDay = new OneDayContactLog(context, date + ".db");
//			SQLiteDatabase dbDatabase = oneDay.getReadableDatabase();
//			dayCursor = dbDatabase.rawQuery("select * from oneDayContactLogTable", null);
//			int i = 1;//every day has same lables
//			 
//			while (dayCursor.moveToNext()) {
//				if (k == 1) {
//					contactLable[i] = dayCursor.getString(dayCursor.getColumnIndex("contactLable"));
//				}
//				for (int j = 1; j <= TS_NUMBER; j++) {
//					contactNumber[i][j] = dayCursor.getInt(j);		
//					contactWeight[i][j] = w[k] * dayCursor.getInt(j);
//				}				
//				i++;//i indicate different lable
//			}
//			
//			k++;	//k days ago
//		} 
//	}
//	
//	// calculate utility
//	private void calculateUtility(int tsCurrent) {
//		calContactProbability();
//		getDataLable();
//		for (int i = 1; i < lableNumber; i++) {
//			for (String lab : dataLable) {
//				if (lab.equals(contactLable[i])) {	//节点通信过的lable等于数据的lable，则计算其utility�?
//					
//					for (int j = 1; j < TS_NUMBER; j++) {
//						if (Math.abs(j - tsCurrent) < 4) {
//							utility[i] = prob[i][j] / (Math.abs(j - tsCurrent));
//						}
//					}
//				}
//			} 
//		}
//	}
//	
//	//get own utility
//	public double getUtility(int tsCurrent) {
//		calculateUtility(tsCurrent);
//		
//		double Ui = 0;
//		for (int i = 1; i < lableNumber; i++) {
//			Ui += utility[i]; 
//		}
//		
//		return Ui;
//	}
//	
//	private void getDataLable() {
//		SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
//		SQLiteDatabase db = scFdataLable.getWritableDatabase();
//		Cursor cursor = db.rawQuery("select * from dataLableTable", null);
//		while (cursor.moveToNext()) {
//			String lables = cursor.getString(cursor.getColumnIndex("dataLable"));
//			dataLable = lables.split(ParseReceiveNodeContactInfomation.LABLE_TAG);	//获取描述数据的lables，将对应的utility加起�?
//			break; 
//		} 
//	}
}
