package com.example.cpr.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.cpr.data.SCFdataLable;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CalculateOtherNodeUtility {

	private String contactInfomation;
	private String date; 
	private Context context; 
	private String[] dayContactInfo;
	private String[] weekContactInfo; 
	private String[] dataLables; 
	private Map<String, Map<String, ArrayList<Double>>> contactMap = new HashMap<String, Map<String, ArrayList<Double>>>();//last week contact log
	private Map<String, Map<String, ArrayList<Double>>> contactWeightMap = new HashMap<String, Map<String, ArrayList<Double>>>();
	private Map<String, ArrayList<Double>> nijMap = new HashMap<String, ArrayList<Double>>();
	private Map<String, ArrayList<Double>> wnijMap = new HashMap<String, ArrayList<Double>>();
	private Map<String, ArrayList<Double>> probMap = new HashMap<String, ArrayList<Double>>();	//every day contact log
	private Map<String, Double> utility = new HashMap<String, Double>(); 
	
	public static final int TS_NUMBER = 8;
	public static final double[] w = {0, 0.2, 0.12, 0.12, 0.12, 0.12, 0.12, 0.2}; 
	public CalculateOtherNodeUtility(Context context, String contactInformation) {
		this.context = context;
		this.contactInfomation = contactInformation;
	}
	
	//username USERINFO_TAG ownlable DAY_TAG date_1 LB_TAG LB_1 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_2 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_3 TS_TAG TS_1 TS_TAG TS_2....
	//													..............
	//								 DAY_TAG date_2 LB_TAG LB_1 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_2 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_3 TS_TAG TS_1 TS_TAG TS_2....
	//													..............
	//							     DAY_TAG date_3 LB_TAG LB_1 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_2 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_3 TS_TAG TS_1 TS_TAG TS_2....
	//													..............
	//   							 DAY_TAG date_4 LB_TAG LB_1 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_2 TS_TAG TS_1 TS_TAG TS_2....
	//												LB_TAG LB_3 TS_TAG TS_1 TS_TAG TS_2....
	//													.............. 
 

	private void initContactMap() {
		weekContactInfo = contactInfomation.split(GetContactLogInfomation.DAY_TAG);// 
		 
		
		for (int day = 1; day < weekContactInfo.length; day++) {
			double value = 0.12;
			dayContactInfo = weekContactInfo[day].split(GetContactLogInfomation.LB_TAG);
			date = dayContactInfo[0];
			Map<String, ArrayList<Double>> dayContactMap = new HashMap<String, ArrayList<Double>>();
			Map<String, ArrayList<Double>> dayContactWeightMap = new HashMap<String, ArrayList<Double>>();
			for (int lable = 1; lable < dayContactInfo.length; lable++) {
				String[] contactNumber = dayContactInfo[lable].split(GetContactLogInfomation.TS_TAG);
				String Lable = contactNumber[0]; 
				ArrayList<Double> tSContactList = new ArrayList<Double>();
				ArrayList<Double> tSContactWeightList = new ArrayList<Double>();
				for (int j = 1; j < contactNumber.length; j++) {
					double contactNum = Double.parseDouble(contactNumber[j]);
					
					if (day == (weekContactInfo.length - 1)) { 
						value = 0.2;
					} else if (day == 1 && weekContactInfo.length == 8) {
						value = 0.2;
					}
					double contactWeight = value * contactNum;
					tSContactList.add(contactNum);
					tSContactWeightList.add(contactWeight);
				}
				dayContactMap.put(Lable, tSContactList);
				dayContactWeightMap.put(Lable, tSContactWeightList);
			}
			contactMap.put(date, dayContactMap);
			contactWeightMap.put(date, dayContactWeightMap);
			
		}
	}  
		
	 
	
//	public void testcontactMap() {
//		initContactMap();
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
	
	private void calContactnijMap() {
		initContactMap(); 
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
	
//	public void testGetnijMap() {
//		//System.out.println("test public Map<String, ArrayList<Double>> getnijMap(): " + nijMap.size());
//		calContactnijMap();
//		for (String lable : nijMap.keySet()) {
//			ArrayList<Double> contactList = nijMap.get(lable);
//			ArrayList<Double> weightList = wnijMap.get(lable);	
//			System.out.println("testGetnijMap weightList.size():" + weightList.size());
//			System.out.println("test lable:" + lable);
//			for (int i = 0; i < contactList.size(); i++) {
//				System.out.println("contact number:" + contactList.get(i));
//				System.out.println("contact weight:" + weightList.get(i));
//			}
//		} 
//	}
	
	private void calContactProbability() {
		calContactnijMap();
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
//		calContactProbability();
//		for (String lable : probMap.keySet()) {
//			ArrayList<Double> p = probMap.get(lable);
//			System.out.println("lable : " + lable);
//			for (int i = 0; i < p.size(); i++) {
//				System.out.println("p:" + p.get(i));
//			}
//		}
//	}
	
	private Map<String, Double> calculateUtility(int current) {
		calContactProbability();
		current = current - 1;
//		System.out.println("current:" + current);
		for (String lable : probMap.keySet()) {
			ArrayList<Double> probList = probMap.get(lable);
//			System.out.println("probList.size():" + probList.size());
			double uti = 0.0;
			//current = current - 1;
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
	
	public Map<String, Double> getUtility(int current) { 
		return calculateUtility(current);
	}
	
	public double getUtilityvalue(int current) {
		getDataLable();
		Map<String, Double> utilityMap = calculateUtility(current);
		double value = 0.0;
		System.out.println("test getUtilityvalue utilityMap size:"+ utilityMap.size());
		for (String lable : utilityMap.keySet()) {
			for (String dataLable : dataLables) {
				System.out.println("getUtilityvalue dataLable:" + dataLable);
				System.out.println("getUtilityvalue other utilityMap:" + utilityMap.get(lable));
				if (lable.equals(dataLable)) {
					value += utilityMap.get(lable);
				}
			}
		}
		return value;
	}
	//parse N_ij from received contact information
//	private void calContactValue() {
//		initPro();
//		for (int k = 1; k < weekContactInfo.length; k++) {
//			
//			dayContactInfo = weekContactInfo[k].split(GetContactLogInfomation.LB_TAG);// 
//			//date = dayContactInfo[0];
//			
//			Map<String, ArrayList<Double>> lableContactMap = new HashMap<String, ArrayList<Double>>();
//			Map<String, ArrayList<Double>> lableContactWeightMap = new HashMap<String, ArrayList<Double>>();
//			
//			for (int i = 1; i < dayContactInfo.length; i++) { 
//				
//				String[] contactNum = dayContactInfo[i].split(GetContactLogInfomation.TS_TAG); 
////				if (k == 1) {
////					contactLable[i] = contactNum[0];	//different lable
////				}			
//				ArrayList<Double> contactList = new ArrayList<Double>();
//				ArrayList<Double> weightList = new ArrayList<Double>();
//				for (int j = 1; j < contactNum.length; j++) {
//					
//					contactNumber[i][j] = Integer.parseInt(contactNum[j]);
//					contactWeight[i][j] = w[k] * contactNumber[i-1][j];
//					contactList.add(contactNumber[i][j]);
//					weightList.add(contactWeight[i][j]);
//				}
//				lableContactMap.put(contactNum[0], contactList);
//				lableContactWeightMap.put(contactNum[0], weightList);
//			}
//		 	  
//			contactNumList.add(lableContactMap);
//			contactWeightList.add(lableContactWeightMap);
//		}
//	}
//	
	//contact probability estimating
//	private void calContactProbability(){
//				
//		calContactValue(); 
//		for (int k = 0; k < contactNumList.size(); k++) {
//			Map<String, ArrayList<Double>> lableContactMap = contactNumList.get(k);
//			Map<String, ArrayList<Double>> lableContactWeightMap = contactWeightList.get(k);
//			int i = 1;
//			for (String lable : lableContactMap.keySet()) {
//				if (!probMap.containsKey(lable)) {
//					ArrayList<Double> contactNum = new ArrayList<Double>();
//				}
//				
//				ArrayList<Double> contactList = lableContactMap.get(lable);
//				ArrayList<Double> contactWeightList = lableContactWeightMap.get(lable);
//				int temp = 0;
//				for (int j = 1; j <= contactList.size(); j++) {
//					 
//					if (contactList.get(temp) != 0) {
//						prob[i][j] = contactWeightList.get(temp)/contactList.get(temp);
//					} else {
//						prob[i][j] = 0;
//					}
//					temp++;                                  
//				} 
//			} 
//		} 
//	}
	
	// calculate utility
//	public void calculateUtility(int tsCurrent) {
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
	 
	private void getDataLable() {
		SCFdataLable scFdataLable = new SCFdataLable(context, "data.db");
		SQLiteDatabase db = scFdataLable.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from dataLableTable", null);
		while (cursor.moveToNext()) {
			String lables = cursor.getString(cursor.getColumnIndex("dataLable"));
			dataLables = lables.split("#");	//获取描述数据的lables，将对应的utility加起�?
			break; 
		} 
	}
}
