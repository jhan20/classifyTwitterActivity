import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;


public class Scratch {
	public static void main (String args[]){
		List<String> tweeters = new ArrayList();
		tweeters.add("ninagarcia");
		tweeters.add("tyrabanks");
		tweeters.add("heidiklum");
		tweeters.add("victoriabeckham");
		tweeters.add("MargaretAtwood");
		tweeters.add("SalmanRushdie");
		tweeters.add("stephenfry");
		tweeters.add("paulocoelho");
		tweeters.add("DeepakChopra");
		tweeters.add("JennieGow");
		tweeters.add("NataliePinkham");
		tweeters.add("JeremyClarkson");
		tweeters.add("LewisHamilton");
		tweeters.add("jasonfried");
		tweeters.add("Scobleizer");
		tweeters.add("marissamayer");
		tweeters.add("jack");
		tweeters.add("joshjames");
		tweeters.add("fredwilson");
		tweeters.add("paulg");

		Twitter twitter = TwitterFactory.getSingleton();

		try {
			File fw=new File("ftw");
			FileOutputStream fos=new FileOutputStream(fw);
			OutputStreamWriter osw=new OutputStreamWriter(fos);
			Writer wt=new BufferedWriter(osw);
			
			
			Iterator twIter=tweeters.iterator();
			System.out.println("Rate Limiet Status : " + twitter.getRateLimitStatus().get("/application/rate_limit_status"));
			System.out.println("UserID ScreenName Status_Count isVerified listedCount Followers Freinds TweetId tweetIDCreateTime" );
			while(twIter.hasNext()){
				String userString=twIter.next().toString();
				List<Status> userTimeline= twitter.getUserTimeline(userString);
				String newLine=null;
				for(Status tweet :userTimeline ){
					if (tweet.getInReplyToStatusId()>0) continue;
					User u=tweet.getUser();
					newLine=u.getId() + " "+ 
							u.getScreenName()+" " +
							u.getStatusesCount() +" "+
							u.isVerified()+" "+
							u.getListedCount()+ " "+ 
							u.getFollowersCount() + " "+ 
							u.getFriendsCount() +" "+ 
							tweet.getId() + " " + 
							tweet.getCreatedAt().toString().replace(" ", "_")+"\n";
					System.out.println(newLine);
					wt.write(newLine);
					break;
				}
			}
			wt.close();
		}catch(TwitterException te){
			te.printStackTrace();
		}
		catch(Exception ae){
			ae.printStackTrace();
		}
	}

}
