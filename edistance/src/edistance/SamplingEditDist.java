package edistance;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * SamplingEditDist  
 * @author Vanessa Matalon
 */
public class SamplingEditDist {

    private Dictionary dictionary = new Hashtable();
    
    /* 
    the constructor sets this.dictionary to a dictionary of editdistances 
    created by calling FillDictionary with the arguments that are given
    */
    SamplingEditDist(String directory1, String directory2) {
        dictionary = FillDictionary(directory1,directory2);
    }
    
    /*
    FillDictionary returns a dictionary where the keys are a string of 
    two filenames and the values are the editdistances between them
    */
    protected static Dictionary FillDictionary(String directory1, String directory2) {
        Dictionary d = new Hashtable();
        File dir1 = new File(directory1), dir2 = new File(directory2);
        String[] children1 = dir1.list(), children2 = dir2.list();
        // check to see if directories are empty and return null if so
        if (children1 == null) {
            System.out.println("Empty Directory for first argument");
            return null;
        } else if (children2 == null) {
            System.out.println("Empty Directory for second argument");
            return null;
        } else {
            //iterate through all of the files in directory1
            for(int i = 0; i < children1.length; i++) {
                String file1 = directory1.concat("\\");
                file1 = file1.concat(children1[i]);
                //iterate through all the files in directory2
                for(int j = 0; j < children2.length; j++) {
                    String file2 = directory2.concat("\\");
                    file2 = file2.concat(children2[j]); 
                    //set the key to a string that contains both filenames 
                    String key = file1.substring(8) + " " + file2.substring(8);
                    int value;
                    try {
                        //make the value the editdistance between them
                        value = new EditDistance(file1, file2, "0").calcEditDist();
                        //and put in dictionary
                        d.put(key, value);
                    } catch (Exception ex) {
                        Logger.getLogger(SamplingEditDist.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return d;
    }

    /*
    RandomSequenc
    e returns an ArrayList of Integers in the range [0,size) in a 
    random order.
    */
    protected static ArrayList RandomSequence(int size) {
        ArrayList<Integer> list = new ArrayList<>(size); 
        for(int i = 0; i < size; i++) { 
            list.add(i); 
        } 
        Random rand = new Random();
        ArrayList<Integer> randomList = new ArrayList<>(size);
        while(list.size() > 0) { 
            int index = rand.nextInt(list.size());
            randomList.add(list.remove(index));
        }
        return randomList;
    }
    
    private Object[] SampleRandomSequence(int n, String directory3) {
        Object[] results = new Object[5];
        String[] names = new String[n],individualMins = new String[n],individualAverages = new String[n];
        int[] min = new int[n];
        double[] av = new double[n];
        
        File dir3 = new File(directory3);
        String[] children3 = dir3.list();
        String file = directory3.concat("\\");
        
        for(int i=0; i<n; i++) {
            ArrayList<Integer> sortedIndeces = RandomSequence(children3.length);
            int minSum = 0;
            double averageSum = 0.0;
            String nameList = new String(),minList = new String(),averageList = new String();
            for(int j = 0; j<7; j++) {
                String file1 = file.concat(children3[sortedIndeces.get(j).intValue()]);
                nameList += file1.substring(8)+" ";
                int minSoFar = 0;
                double average;
                int runningTotal = 0;
                for (int k = 0; k < 22; k++) {
                    String file2 = file.concat(children3[sortedIndeces.get(7+k).intValue()]);
                    String key = file1.substring(8)+" "+file2.substring(8);
                    int current = ((Integer) dictionary.get(key)).intValue();
                    if((k==0) || (current<minSoFar)) {
                        minSoFar = current;
                    }
                    runningTotal += current;
                }
                average = runningTotal/22.0;
                minList += minSoFar+" ";
                averageList += average+" ";
                
                minSum += minSoFar;
                averageSum += average;
            }
            names[i] = nameList;
            individualMins[i] = minList;
            individualAverages[i] = averageList;
            min[i] = minSum;
            av[i] = averageSum;
        }
        results[0] = names;
        results[1] = individualMins;
        results[2] = min;
        results[3] = individualAverages;
        results[4] = av;
        return results;
    }
    
    
    /* MIN VERSION */
    private int[] MinEditDistance(String directory1, String directory2) {
        File dir1 = new File(directory1), dir2 = new File(directory2);
        String[] children1 = dir1.list(), children2 = dir2.list();
        int[] result = new int[children2.length];  
        
        for(int i = 0; i < children2.length; i++) {
            int minSoFar = 0;
            String file2 = directory2.concat("\\");
            file2 = file2.concat(children2[i]);
            for(int j = 0; j < children1.length; j++) {
                String file1 = directory1.concat("\\");
                file1 = file1.concat(children1[j]);
                String key = file1.substring(5) + " " + file2.substring(16);
                int current = ((Integer) dictionary.get(key)).intValue();
                if((j == 0) || (current < minSoFar)) {
                    minSoFar = current;
                }
            }
            result[i] = minSoFar;
        }
        
        return result;
    }
        
    private static double FindMean(int[] dataPoints) {
        int total = 0;
        double average;
        
        for (int current : dataPoints) {
            total += current;
        }
        
        average = total / (double) dataPoints.length;
        
        return average/7.0;
    }
    
    private static double FindStandardDeviation(int[] dataPoints) {
        int total = 0;
        double mean = FindMean(dataPoints);
        double variance;
        
        for (int current : dataPoints) {
            current = current/7;
            total += Math.pow(current-mean, 2);
        }
        
        variance = total / (double) dataPoints.length;
        return Math.sqrt(variance);
    }
    
    /* AVERAGE VERSION */
    private double[] AverageEditDistance(String directory1, String directory2) {
        double[] result = new double[7];
        File dir1 = new File(directory1), dir2 = new File(directory2);
        String[] children1 = dir1.list(), children2 = dir2.list();
            
        for(int i = 0; i < children2.length; i++) {
            int totalSoFar = 0;
            String file2 = directory2.concat("\\");
            file2 = file2.concat(children2[i]);
            for(int j = 0; j < children1.length; j++) {
                String file1 = directory1.concat("\\");
                file1 = file1.concat(children1[j]);
                String key = file1.substring(5) + " " + file2.substring(16);
                int current = ((Integer) dictionary.get(key)).intValue();
                totalSoFar += current;
            }
            result[i] = totalSoFar / (float) children1.length;
        }
        
        return result;
    }
        
    private static double FindMean2(double[] dataPoints) {
        double total = 0, average;
        
        for (double current : dataPoints) {
            total += current;
        }
        
        average = total / (double) dataPoints.length;
        
        return average/7;
    }
    
    private static double FindStandardDeviation2(double[] dataPoints) {
        double total = 0.0;
        double mean = FindMean2(dataPoints);
        double variance;
        
        for (double current : dataPoints) {
            current = current/7;
            total += Math.pow(current-mean, 2.0);
        }
        
        variance = total / (double) dataPoints.length;
        return Math.sqrt(variance);
    }
    
    public static void main(String args[]) throws IOException {
        
        String directory1 = args[0], directory2 = args[1], directory3 = args[2];   
        //File dir1 = new File(directory1), dir2 = new File(directory2), dir3 = new File(directory3);
        //String[] children1 = dir1.list(), children2 = dir2.list(), children3 = dir3.list();
        
        SamplingEditDist sample = new SamplingEditDist(directory3, directory3);
        
        //Min 
        int sumMinEditDistance = 0;
        for(int minEditDistance : sample.MinEditDistance(directory1, directory2)) {
            sumMinEditDistance += minEditDistance;
        }
        System.out.println("Sum of Min Edit Distances: " + sumMinEditDistance);
        System.out.println("Average Min Edit Distance: " + (sumMinEditDistance/sample.MinEditDistance(directory1, directory2).length));
        
        //Average 
        double sumAverageEditDistance = 0.0;
        for(double averageEditDistance : sample.AverageEditDistance(directory1, directory2)) {
            sumAverageEditDistance += averageEditDistance;
        }
        System.out.println("\nSum of Average Edit Distances: " + sumAverageEditDistance);
        System.out.println("Average Average Edit Distance: " + (sumAverageEditDistance/sample.AverageEditDistance(directory1, directory2).length));
        
        Object[] sampleResults = sample.SampleRandomSequence(1000, directory3);
        int[] minEditDistances = (int[]) sampleResults[2];
        double minMean = FindMean(minEditDistances);
        System.out.println("\nMean of Sampled Min Edit Distances: " + minMean);
        double minStandardDeviation = FindStandardDeviation(minEditDistances);
        System.out.println("Standard Deviation of Sampled Min Edit Distances: " + minStandardDeviation);
                
        double[] averageEditDistances = (double[]) sampleResults[4];
        double avMean = FindMean2(averageEditDistances);
        System.out.println("\nMean of Sampled Average Edit Distances: " + avMean);
        double avStandardDeviation = FindStandardDeviation2(averageEditDistances);
        System.out.println("Standard Deviation of Sampled Average Edit Distances: " + avStandardDeviation);     
    
        String[] names = (String[]) sampleResults[0];
        String[] individualMins = (String[]) sampleResults[1];
        String[] individualAverages = (String[]) sampleResults[3];

        for (int i=0; i<1000; i++) {
            Integer min = new Integer(minEditDistances[i]);
            Double av = new Double(averageEditDistances[i]);
            System.out.print("\n"+names[i] + ",");
            System.out.print(individualMins[i] + ",");
            System.out.print(min.toString() + ",");
            System.out.print(individualAverages[i] + ",");
            System.out.print(av.toString());
        }
    }
}
