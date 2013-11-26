import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;


public class Process {

	public static void main(String args[]){
		try {

			Set<Long> tweetList = new HashSet<Long>();
			Map<Long, ArrayList> RTData = new HashMap<Long,ArrayList>(); 
			Map<Long, ArrayList> FvData = new HashMap<Long,ArrayList>(); 

			BufferedReader br= new BufferedReader(new FileReader("twDataCounts"));
			String line=null;
			while((line=br.readLine())!=null){
				String tweetData=line.substring(line.lastIndexOf(":")+1, line.length());
				Scanner lnScan=new Scanner(tweetData);
				lnScan.useDelimiter(" ");
				Long twId=lnScan.nextLong();
				int RtCount=lnScan.nextInt();
				int FavCount=lnScan.nextInt();

				tweetList.add(twId);

				List<Integer> tp=new ArrayList<Integer>();
				if(RTData.containsKey(twId)) tp=RTData.get(twId);
				tp.add(RtCount);
				RTData.put(twId, (ArrayList) tp);

				tp=new ArrayList<Integer>();
				if(FvData.containsKey(twId))
					tp=FvData.get(twId);
				tp.add(FavCount);
				FvData.put(twId, (ArrayList) tp);
			}


			BufferedReader brR= new BufferedReader(new FileReader("ftw"));
			String ln=null;
			while((ln=brR.readLine()) != null){
				Scanner sn=new Scanner(ln);
				sn.useDelimiter(" ");
				
				String userIdString = sn.next();
				String userString = sn.next();
				String userTweetCount=sn.next();
				String userisVerified=sn.next();
				String userListCount=sn.next();
				String userFollowersCount=sn.next();
				String userFreindsCount=sn.next();
				Long tweetLong=sn.nextLong();
				
				tweetList.remove(tweetLong);
			}
			brR.close();
			
			Writer wt=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("RTData"))));
			List<Long> newtweetList=new ArrayList<Long>();
			for(Long id : tweetList){
				try{
					if (RTData.get(id).size()<250) continue;
					newtweetList.add(id);
					List<Integer> RTlist = RTData.get(id).subList(0, 200);
					String listLine = RTlist.toString().replaceAll("\\[", " ");
					listLine=listLine.replaceAll("\\]", " ");
					System.out.println("RT data "  + id + " "+ listLine);
					wt.write(id + " " + listLine + "\n");
				}catch(IndexOutOfBoundsException ioe){
					;//keep calm, continue.
				}
			}
			wt.close();

			wt=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("FvData"))));
			for(Long id : newtweetList){
				try{
					List<Integer> Fvlist = FvData.get(id).subList(0, 200);
					String listLine = Fvlist.toString().replaceAll("\\[", " ");
					listLine=listLine.replaceAll("\\]", " ");
					System.out.println("Fv data "  + id + " "+ listLine);
					wt.write(id + " " + listLine + "\n");
				}catch(IndexOutOfBoundsException ioe){
					;//keep calm, continue.
				}
			}
			wt.close();

			wt=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("RTFvData"))));
			for(Long id : newtweetList){
				int dataIter= FvData.get(id).size();
				List<Integer> rtFv = new ArrayList<Integer>();
				for (int i=0;i<dataIter;i++){
					rtFv.add((Integer) FvData.get(id).get(i) + (Integer) RTData.get(id).get(i) );
				}
				try{
					String listLine = rtFv.subList(0, 200).toString().replaceAll("\\[", " ");
					listLine=listLine.replaceAll("\\]", " ");
					System.out.println("RTFv data "  + id + " "+ listLine);
					wt.write(id + " " + listLine + "\n");
				}catch(IndexOutOfBoundsException ioe){
					;//keep calm, continue.
				}
			}
			wt.close();
			br.close();

			System.out.println("The total tweet count : " + tweetList.size());
			System.out.println("Countable tweet count : " + newtweetList.size());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
