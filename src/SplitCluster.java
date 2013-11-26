import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;


public class SplitCluster {
	public static void main(String args[]){
		try{
			BufferedReader br=new BufferedReader(new FileReader("OutputRt.txt"));
			String ln=null;
			Writer wt0=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Rt0Data"))));
			Writer wt1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Rt1Data"))));
			Writer wt2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Rt2Data"))));
			Writer wt3=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Rt3Data"))));
			Writer wt4=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Rt4Data"))));
			
			while( (ln=br.readLine())!=null){
				System.out.println("LN cluster : " + ln.charAt(0)  + "ln : " + ln);
				char cluster=ln.charAt(0);
				switch(cluster){
				case '0':
					wt0.write(ln.substring(4)+"\n");
					break;
				case '1':
					wt1.write(ln.substring(4)+"\n");
					break;
				case '2':
					wt2.write(ln.substring(4)+"\n");
					break;
				case '3':
					wt3.write(ln.substring(4)+"\n");
					break;
				case '4':
					wt4.write(ln.substring(4)+"\n");
					break;
				default :
					continue;
				}
				
			}
			wt0.close();wt1.close();wt2.close();wt3.close();wt4.close();
		}catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}

	}
}
