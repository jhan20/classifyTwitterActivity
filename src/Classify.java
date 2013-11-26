import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetTools;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.distance.fastdtw.dtw.FastDTW;
import net.sf.javaml.distance.AbstractSimilarity;
import net.sf.javaml.distance.dtw.DTWSimilarity;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeriesPoint;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.filter.DatasetFilter;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.filter.discretize.EqualWidthBinning;
import libsvm.LibSVM;
import net.sf.javaml.tools.weka.WekaClassifier;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import net.sf.javaml.tools.weka.ToWekaUtils;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.weka.WekaClusterer;
import weka.clusterers.SimpleKMeans;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class Classify {

	public static void main(String[] args) throws Exception {

		Dataset data = FileHandler.loadDataset(new File("ClusterInfo")," ");
		//System.out.println(data);
		Set<Integer> alwaysRemove=new HashSet<Integer>(Arrays.asList(2,1,4,21,14,9,23,6));

		Set<Integer> userAt=new HashSet<Integer>(Arrays.asList(0,3,10,19,22));
		Set<Integer> tweetAt=new HashSet<Integer>(Arrays.asList(8));
		Set<Integer> rt3At=new HashSet<Integer>(Arrays.asList(11,25,24));
		Set<Integer> fv3At=new HashSet<Integer>(Arrays.asList(16,13,12));
		Set<Integer> rt3FvAt=new HashSet<Integer>(Arrays.asList(5,18,17));
		Set<Integer> rt2At=new HashSet<Integer>(Arrays.asList(11,25));
		Set<Integer> fv2At=new HashSet<Integer>(Arrays.asList(16,13));
		Set<Integer> rt2FvAt=new HashSet<Integer>(Arrays.asList(5,18));
		Set<Integer> rtAt=new HashSet<Integer>(Arrays.asList(11));
		Set<Integer> fvAt=new HashSet<Integer>(Arrays.asList(16));
		Set<Integer> rtFvAt=new HashSet<Integer>(Arrays.asList(5));

		
		Set<Integer> removeatt = new HashSet<Integer>();        
		removeatt.addAll(alwaysRemove);
//		removeatt.addAll(userAt);
//		removeatt.addAll(tweetAt);
		
		removeatt.addAll(rt3FvAt);
		removeatt.addAll(fv3At);
//		removeatt.addAll(rt3At);

//		removeatt.removeAll(rtAt);
//		removeatt.removeAll(fvAt);
//		removeatt.removeAll(rtFvAt);
//		removeatt.removeAll(rt2At);
//		removeatt.removeAll(fv2At);
//		removeatt.removeAll(rt2FvAt);
//		removeatt.removeAll(rt3At);
//		removeatt.removeAll(fv3At);
//		removeatt.removeAll(rt3FvAt);
		
		removeatt.add(7);
		removeatt.add(15);
//		removeatt.add(20);
		
		int firstFlag = 0;
		String attr=new String("7");

		//System.out.println(removeatt.size());
		for (Instance inst : data) {
			inst.removeAttributes(removeatt);
		}

		//System.out.println(data);

		/*------------------------clustering totrt-----------------------*/
		//        Dataset totrt = FileHandler.loadDataset(new File("totrt.txt"), ",");
		//        System.out.println(totrt);
		//        ToWekaUtils wekatotrt = new ToWekaUtils(totrt.copy());
		//        Instances wekainst = wekatotrt.getDataset();
		//        SimpleKMeans skm = new SimpleKMeans();
		//        skm.setPreserveInstancesOrder(true);
		//        skm.setNumClusters(3);
		//        skm.buildClusterer(wekainst);
		//        int[] assignment = skm.getAssignments();
		//        int i=0;
		//        for(int clusterNum : assignment) {
		//            System.out.print(wekainst.instance(i));
		//            System.out.printf("->Cluster %d \n", clusterNum);
		//            i++;
		//        }

		//        DTWSimilarity ds = new DTWSimilarity();
		//        //Clusterer km = new KMeans(5,3,ds);
		//        Clusterer km = new KMeans(3,10);
		//        Dataset[] clusters = km.cluster(data);
		//        for(int i=0;i<3;i++)
		//        {
		//        	System.out.println(clusters[i]);
		//        }


		//        double clusterNum = 0;
		//        for (Dataset clst : clusters) {
		//        	for (Instance inst : clst) {
		//        		inst.setClassValue(clusterNum);
		//        	}
		//        	clusterNum++;
		//        }
		//        DatasetTools.merge(clusters);
		//        data = clusters[0].copy();
		//        System.out.println(data);

		//        System.out.println("Cluster count: " + clusters.length);
		//        FileHandler.exportDataset(data, new File("outputRT.txt"));

		ToWekaUtils wekadata = new ToWekaUtils(data.copy());
		Instances dataSet = wekadata.getDataset();

		String[] options = new String[2];
		//        options[0] = "-R";                                    // "range"
		//        options[1] = "1";                                     // first attribute
		//        Remove remove = new Remove();                         // new instance of filter
		//        remove.setOptions(options);                           // set options
		//        remove.setInputFormat(dataSet);                          // inform filter about dataset **AFTER** setting options
		//        Instances newData = Filter.useFilter(dataSet, remove);   // apply filter

		options[0] = "-R";
		options[1] = attr;
		NumericToNominal toNominal = new  NumericToNominal();
		if (firstFlag==0) toNominal.setOptions(options);          // set options
		toNominal.setInputFormat(dataSet);                          // inform filter about dataset **AFTER** setting options
		Instances newData = Filter.useFilter(dataSet, toNominal);   // apply filter

		ArffSaver saver = new ArffSaver();
		saver.setInstances(newData);
		saver.setFile(new File("wekadata.arff"));
		saver.writeBatch();

		/*----------------------------NaiveBayes-------------------*/      

		//		data = clusters[0];
		//
		//		EqualWidthBinning eb = new EqualWidthBinning(5);
		//		System.out.println("Start discretisation");
		//		eb.build(data);
		//		Dataset ddata = data.copy();
		//		eb.filter(ddata);
		//		System.out.println(ddata);
		//
		////        ToWekaUtils wekadata = new ToWekaUtils(ddata);
		////        Instances dataSet = wekadata.getDataset();
		////        ArffSaver saver = new ArffSaver();
		////        saver.setInstances(dataSet);
		////        saver.setFile(new File("output.arff"));
		////        //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
		////        saver.writeBatch();
		//		
		//		
		//		boolean useLaplace = true;
		//		boolean useLogs = true;
		//		Classifier nbc = new NaiveBayesClassifier(useLaplace, useLogs, false);
		//		nbc.buildClassifier(FileHandler.loadDataset(new File("output.data"),0, ","));
		//
		//		Dataset dataForClassification = FileHandler.loadDataset(new File("output.data"),0, ",");
		//
		//		int correct = 0, wrong = 0;
		//
		//		for (Instance inst : dataForClassification) {
		//			eb.filter(inst);
		//			Object predictedClassValue = nbc.classify(inst);
		//			Object realClassValue = inst.classValue();
		//			if (predictedClassValue.equals(realClassValue))
		//				correct++;
		//			else {
		//				wrong++;
		//
		//			}
		//		}
		//		System.out.println("correct " + correct);
		//		System.out.println("incorrect " + wrong);


		/*---------------------------LibSVM---------------------------*/ 
		//        data = FileHandler.loadDataset(new File("output.data"), 0, ",");
		//
		//        Classifier svm = new LibSVM();
		//        svm.buildClassifier(data);
		//
		//        Dataset dataForClassification = FileHandler.loadDataset(new File("output.data"), 0, ",");
		//        int correct = 0, wrong = 0;
		//        for (Instance inst : dataForClassification) {
		//            Object predictedClassValue = svm.classify(inst);
		//            Object realClassValue = inst.classValue();
		//            if (predictedClassValue.equals(realClassValue))
		//                correct++;
		//            else
		//                wrong++;
		//        }
		//        System.out.println("Correct predictions  " + correct);
		//        System.out.println("Wrong predictions " + wrong);


		/*------------------------Weka---------------------------*/
		//        /* Load data */
		//        data = FileHandler.loadDataset(new File("output.data"), 0, ",");
		//        /* Create Weka classifier */
		//        SMO smo = new SMO();
		//        /* Wrap Weka classifier in bridge */
		//        Classifier javamlsmo = new WekaClassifier(smo);
		//        
		//        /* Initialize cross-validation */
		//       CrossValidation cv = new CrossValidation(javamlsmo);
		//        /* Perform cross-validation */
		//        Map<Object, PerformanceMeasure> pm = cv.crossValidation(data);
		//        /* Output results */
		//        System.out.println(pm);
	}
}
