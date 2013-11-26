import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class dtw {

	public static void main(String[] args) throws Exception {

		//Dataset data = FileHandler.loadDataset(new File("timeseriesnew.data"), ",");
		//Dataset data = FileHandler.loadDataset(new File("iris.data"),4, ",");
		//Dataset data = FileHandler.loadDataset(new File("iris.data"),4, ",");
		//Dataset data = FileHandler.loadDataset(new File("arrhythmia.data"),1, ",");       
		//System.out.println(data);
		BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("FileTo Read");
		String readFileName= br.readLine();
		Dataset data = FileHandler.loadDataset(new File(readFileName), ",");

		//Instance sample = data.instance(0).copy();
		//System.out.println(sample);
		//data.instance(0).removeAttribute(0);
		//sample = data.instance(0).copy();

		//int id =1;
		//String s = "[123]";
		for (Instance inst : data) {
			inst.removeAttribute(0);
		}

		//double[] att = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		//Instance instance = new DenseInstance(att);

		DTWSimilarity ds = new DTWSimilarity();
		Clusterer km = new KMeans(5,10,ds);
		//Clusterer km = new KMeans(3,10);
		Dataset[] clusters = km.cluster(data);
		//        for(int i=0;i<3;i++)
		//        {
		//        	System.out.println(clusters[i]);
		//        }

		//    ToWekaUtils wekadata = new ToWekaUtils(data);
		//    Instances dataSet = wekadata.getDataset();
		//      
		//      String[] options = new String[2];
		//      options[0] = "-R";                                    // "range"
		//      options[1] = "1";                                     // first attribute
		//      Remove remove = new Remove();                         // new instance of filter
		//      remove.setOptions(options);                           // set options
		//      remove.setInputFormat(dataSet);                          // inform filter about dataset **AFTER** setting options
		//      Instances newData = Filter.useFilter(dataSet, remove);   // apply filter
		//      
		//    ArffSaver saver = new ArffSaver();
		//    saver.setInstances(newData);
		//    saver.setFile(new File("newoutput.arff"));
		//    //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
		//    saver.writeBatch();
		//      
		//      SimpleKMeans skm = new SimpleKMeans();
		//      skm.setPreserveInstancesOrder(true);
		//      skm.setNumClusters(3);
		//      skm.buildClusterer(newData);
		//      int[] assignment = skm.getAssignments();
		//      System.out.println(assignment[0]);

		//        DatasetFilter datafilter =
		//
		double clusterNum = 0;
		for (Dataset clst : clusters) {
			for (Instance inst : clst) {
				inst.setClassValue(clusterNum);
			}
			clusterNum++;
		}
		DatasetTools.merge(clusters);

		//System.out.println(cluster);
		//System.out.println("done");
		//System.out.println("Cluster count: " + clusters.length);
		System.out.println("Enter the output Filename");
		String writeFileName=br.readLine();
		FileHandler.exportDataset(clusters[0], new File(writeFileName));


		//        //data = FileHandler.loadSparseDataset(new File("devtools/data/smallsparse.tsv"), 0, " ", ":");
		//        //System.out.println(data);

		//        ToWekaUtils wekadata = new ToWekaUtils(clusters[0].copy());
		//        Instances dataSet = wekadata.getDataset();
		//        
		//        ArffSaver saver = new ArffSaver();
		//        saver.setInstances(dataSet);
		//        saver.setFile(new File("output.arff"));
		//        //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
		//        saver.writeBatch();

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
