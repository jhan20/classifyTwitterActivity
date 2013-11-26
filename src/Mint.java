import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class Mint {
	public static void main(String args[]){
		Twitter twitter = TwitterFactory.getSingleton();

		try{
			BufferedReader br= new BufferedReader(new FileReader("ftw"));
			File fw=new File("twDataCounts");
			FileOutputStream fos=new FileOutputStream(fw,true);
			OutputStreamWriter osw=new OutputStreamWriter(fos);
			Writer wt=new BufferedWriter(osw);
			
			
			String ln=null;
			
			int tCount=0;
			while((ln=br.readLine()) != null){
				System.out.println("Line: " +ln);
				Scanner sn=new Scanner(ln);
				sn.useDelimiter(" ");
				
				System.out.println("user : "+ sn.next());
				String userString = sn.next();
				System.out.println("userString :" + userString);
				String userTweetCount=sn.next();
				//System.out.println("TwCount? :" + sn.next());
				String userisVerified=sn.next();
				//System.out.println("isVerified? :" + sn.next());
				String userListCount=sn.next();
				//System.out.println("ListCount? :" + sn.next());
				String userFollowersCount=sn.next();
				//System.out.println("Followers? :" + sn.next());
				String userFreindsCount=sn.next();
				//System.out.println("Friends? :" + sn.next());
				Long tweetLong=sn.nextLong();
				//System.out.println("isLong? TweetID? : " +  tweetLong);
				
				int page=1;
				boolean nxtPage=true;
				while(nxtPage){
					List<Status> userTimeline=twitter.getUserTimeline(userString,new Paging(page++,tweetLong-1));
					if (userTimeline.isEmpty()) break;
					//nxtPage = userTimeline.isEmpty() ? false : true;
					for(Status tweet : userTimeline){
						if(tweet.getInReplyToUserId()>0) continue;
						String newLine= "User : " + tweet.getUser().getName().replaceAll(" ", "_") + " Id : " + tweet.getUser().getId() + " at Collection Time : "+ Calendar.getInstance().getTime().toString().replace(' ', '_') + " : " + tweet.getId() + " " + tweet.getRetweetCount() + " " + tweet.getFavoriteCount() + "\n" ;
						System.out.println(newLine); 
						wt.write(newLine);
						tCount++;
						nxtPage = (tweet.getId() == tweetLong) ? false : true;
					}
				}
			}
			wt.close();
			br.close();
			System.out.println("total tweet Count :" + tCount);
			System.out.println("Rate Limit Status : " + twitter.getRateLimitStatus().get("/application/rate_limit_status"));
		}catch(TwitterException ae){
			ae.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
