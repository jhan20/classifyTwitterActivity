import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;


public class MakeClusterData {
	
	//TODO 
	// Remove or close all unused File Buffers in functions. 
	
	public static void main (String args[]){
		// Below we are collecting tweetID and user Details finalised in the tweetDetail Map.
		//The map handle is the TweetID as Long
		// The List it holds has data in the following manner 
		// [<userName>, <UserID>, <FirstTweetAppearanceTimeString>, <UserId>, <userString>, <TweetCount>, <isVerified>, <ListCount>, <FollowersCount>, <Friends Count>]

		Map<Long,List<String>> tweetDetail =readTwCounts();
		System.out.println("Completed reading twCounts : " + tweetDetail.keySet());

		Map<String,List<String>> mapoList=readScratchDoc();
		System.out.println("Completed reading Scratch Doc" +  mapoList.keySet());

		Set<Long> totalTweetSet = tweetDetail.keySet();

		for(Long id: totalTweetSet){
			List<String> currList=tweetDetail.get(id);
			//System.out.println("currList :" + currList);
			List<String> toAdd=mapoList.get(tweetDetail.get(id).get(1));
			//System.out.println("toAdd : " + toAdd);
			try{
				currList.addAll(toAdd);
			}catch(Exception e){
				e.printStackTrace();
				//Keep Calm. This is not expected to happen.But well, continue.
			}
			tweetDetail.put(id, currList);
		}

		// Below this....., 
		//<tweetID> <RTClusterID> <FvClusterID> <RT+FvClusterID> <RTCount É 8> <FvCount É 8> <RT+FVCount É 8>

		Map<Long,ArrayList<Integer>> timeDataRT = makeTimeSeries("RTData");
		Map<ArrayList<Integer>,Integer> clusterDataRT=makeClusterData("OutputRT.txt");
		
		System.out.println("Completed Parsing RT data");
		Map<Long,ArrayList<Integer>> timeDataFv = makeTimeSeries("FvData");
		Map<ArrayList<Integer>,Integer> clusterDataFv=makeClusterData("OutputFv.txt");
		System.out.println("Completed Parsing FV data");
		Map<Long,ArrayList<Integer>> timeDataRTFv = makeTimeSeries("RTFvData");
		Map<ArrayList<Integer>,Integer> clusterDataRTFv=makeClusterData("OutputRTFv.txt");
		System.out.println("Completed Parsing RT+Fv data");


		List<Map<String,Object>> table= new ArrayList<Map<String,Object>>();
		Set<Long> tweetList=timeDataRT.keySet();
		Iterator twIter=tweetList.iterator();

		while(twIter.hasNext()){
			Long twID=(Long) twIter.next();
			Map<String,Object> row= new HashMap<String,Object>();
			//System.out.println("Row Details : " + twID + " " + clusterDataRT.get(timeDataRT.get(twID)) + " "+ clusterDataFv.get(timeDataFv.get(twID)) +  " "+ clusterDataRTFv.get(timeDataRTFv.get(twID)));
			row.put("tweetID",twID);
			
			row.put("RTClusterID", clusterDataRT.get(timeDataRT.get(twID)));
			row.put("FvClusterID", clusterDataFv.get(timeDataFv.get(twID)));
			row.put("RTFvClusterID", clusterDataRTFv.get(timeDataRTFv.get(twID)));
			
			row.put("RT0", timeDataRT.get(twID).get(0));
			row.put("Fv0", timeDataFv.get(twID).get(0));
			row.put("RTFv0", timeDataRTFv.get(twID).get(0));
			row.put("RT4", timeDataRT.get(twID).get(4));
			row.put("Fv4", timeDataFv.get(twID).get(4));
			row.put("RTFv4", timeDataRTFv.get(twID).get(4));
			row.put("RT8", timeDataRT.get(twID).get(8));
			row.put("Fv8", timeDataFv.get(twID).get(8));
			row.put("RTFv8", timeDataRTFv.get(twID).get(8));
			row.put("RT198", timeDataRT.get(twID).get(198));
			row.put("Fv198", timeDataFv.get(twID).get(198));
			row.put("RTFv198", timeDataRTFv.get(twID).get(198));

			List<String> tweetExtras = tweetDetail.get(twID);
			row.put("UserName", tweetExtras.get(0));
			row.put("UserID", tweetExtras.get(1));
			//row.put("TweetTime", tweetExtras.get(2));
			row.put("TweetDay" , tweetExtras.get(2).substring(0, 3));
			row.put("TweetHour" , tweetExtras.get(2).substring(11, 13));
//			row.put("UserString", tweetExtras.get(4));
			row.put("UserTotalTweetCount", tweetExtras.get(5));
			row.put("isVerfied", tweetExtras.get(6));
			row.put("ListCount", tweetExtras.get(7));
			row.put("Followers", tweetExtras.get(8));
			row.put("Friends", tweetExtras.get(9));
			row.put("friend:followers", Long.parseLong(tweetExtras.get(8))/Long.parseLong(tweetExtras.get(9)));
			table.add(row);
			//System.out.println("ROW : " + row);
		}
		
		System.out.println("table size" + table.size());

		try{
			Writer br= new  BufferedWriter(new FileWriter(new File("ClusterInfo")));
			Set<String> keyList=table.get(0).keySet();
			System.out.println(keyList.toString());
			br.write(keyList.toString() + "\n");
			
			Iterator tableIter=table.iterator();
			int count=1;
			while(tableIter.hasNext()){
				System.out.print("Row Number : " + count++);
				Map<String, Object> mp=(Map<String, Object>) tableIter.next();
				Set<String> mpKeySet=mp.keySet();
				for(String key: mpKeySet){
					System.out.print(key + " : " + mp.get(key) +" ");
					br.write(mp.get(key).toString() + " ");
				}
				System.out.print("\n");
				br.write("\n");
			}
			br.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}


	static Map<Long,List<String>> readTwCounts(){
		Map<Long,List<String>> table=new HashMap<Long,List<String>>();
		try{
			BufferedReader readTwCounts= new BufferedReader(new FileReader("twDataCounts"));
			String twLine=null;
			while((twLine=readTwCounts.readLine())!=null){
				Scanner twLineScan = new Scanner(twLine.substring(twLine.lastIndexOf(":")+1));
				twLineScan.useDelimiter(" ");
				Long tweetIdLong=twLineScan.nextLong();
				if (table.containsKey(tweetIdLong)) {
					//System.out.println("Existing tweet");
					continue;
				}

				twLineScan = new Scanner(twLine.substring(0, twLine.lastIndexOf(":")));
				twLineScan.useDelimiter(" ");
				String userString=null;
				String userIdString=null;
				String tweetTimeString=null;
				while(twLineScan.hasNext()){
					String element = twLineScan.next();
					if (element.equalsIgnoreCase("user")) {
						twLineScan.next();
						userString=twLineScan.next();
					}
					if (element.equalsIgnoreCase("id")) {
						twLineScan.next();
						userIdString=twLineScan.next();
					}
					if (element.equalsIgnoreCase("Time")) {
						twLineScan.next();
						tweetTimeString=twLineScan.next();
					}
				}

				List<String> tweetDetailsList= new ArrayList<String>();
				tweetDetailsList.add(userString);
				tweetDetailsList.add(userIdString);
				tweetDetailsList.add(tweetTimeString);
				//tweetDetailsList.add(tweetTimeString.replaceAll("_", " "));
				table.put(tweetIdLong,tweetDetailsList);
				//System.out.println("Adding to TweetID :  " + tweetIdLong +" List : " +  tweetDetailsList );
			}
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return table;
	}

	static Map<String,List<String>> readScratchDoc(){
		Map<String,List<String>> mapoList=new HashMap<String,List<String>>();
		try {
			BufferedReader ftwReader = new BufferedReader(new FileReader("ftw"));
			String ftwLine=null;
			while((ftwLine=ftwReader.readLine())!=null){
				Scanner sn=new Scanner(ftwLine);
				sn.useDelimiter(" ");
				List<String> row=new ArrayList<String>();
				String userIdString = sn.next();
				row.add(userIdString);
				String userString = sn.next();
				row.add(userString);
				String userTweetCount=sn.next();
				row.add(userTweetCount);
				String userisVerified=sn.next();
				row.add(userisVerified);
				String userListCount=sn.next();
				row.add(userListCount);
				String userFollowersCount=sn.next();
				row.add(userFollowersCount);
				String userFreindsCount=sn.next();
				row.add(userFreindsCount);

				mapoList.put(userIdString, row);
			}
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return mapoList;
	}
	

	static HashMap<ArrayList<Integer>,Integer> makeClusterData(String filename){
		HashMap<ArrayList<Integer>,Integer> clusterData=new HashMap<ArrayList<Integer>,Integer>();
		try{
			BufferedReader brOtData= new BufferedReader(new FileReader(filename));
			String ln=null;
			while((ln=brOtData.readLine())!=null){
				ArrayList<Integer> timeSeriesList = new ArrayList<Integer>();
				Scanner lineScanner=new Scanner(ln);
				/// Do not seperate by non digits but rather by space. ! 
				lineScanner.useDelimiter("([.][0][//w]*)");
				String word=lineScanner.next();
				int clusterID=Integer.parseInt(word);
				while(lineScanner.hasNext()){
					String listword= lineScanner.next();
					if (listword.length()>0) {
						listword=listword.replaceAll("\\D", "");
						timeSeriesList.add(Integer.parseInt(listword));
					}
				}
				clusterData.put(timeSeriesList,clusterID);
			}
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clusterData;
	}


	static HashMap<Long,ArrayList<Integer>> makeTimeSeries(String filename){
		HashMap<Long,ArrayList<Integer>> timeSeriesData = new HashMap<Long,ArrayList<Integer>>();
		try{
			BufferedReader brRTData = new BufferedReader(new FileReader(filename));
			String ln=null;
			while((ln=brRTData.readLine())!=null){	
				try{
					Scanner RTlineScan = new Scanner(ln);
					RTlineScan.useDelimiter("\\D");
					Long tweetId=RTlineScan.nextLong();
					List<Integer> RTlist=new ArrayList<Integer>();
					while(RTlineScan.hasNext()){
						String element=RTlineScan.next();
						if (element.length()>0) RTlist.add(Integer.parseInt(element));
					}
					RTlist.remove(0);
					timeSeriesData.put(tweetId, (ArrayList<Integer>) RTlist);
				}catch(Exception ae){
					ae.printStackTrace();
				}
			}	
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return timeSeriesData;
	}


}
