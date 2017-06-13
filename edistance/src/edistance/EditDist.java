package edistance;
/*
 * calculates the minimum number of operations required to change one partition 
 * into another, where each operation involves moving a single item from one 
 * group to another (possibly empty) group.
 *
 * see ReadMe.txt for more info
 */

/**
 * @author Naveen M. Khetarpal
 */
import java.io.*;
import java.util.*;

public class EditDist {

    private HashSet<Integer>[] sortOne, sortTwo;
    private final int NUMB_GROUPS;
    private final int NUMB_ITEMS;
    private boolean labeled = false;

    @SuppressWarnings("unchecked")
    EditDist(String sortOneFilename, String sortTwoFilename, String labeledGroups) throws Exception {
        int sortOneGroupCount = EditDist.countNumberOfGroups(sortOneFilename);
        int sortTwoGroupCount = EditDist.countNumberOfGroups(sortTwoFilename);
        NUMB_GROUPS = Math.max(sortOneGroupCount, sortTwoGroupCount);

        if (labeledGroups.equalsIgnoreCase("true")) {
            labeled = true;
        } else if (labeledGroups.equalsIgnoreCase("false")) {
            labeled = false;
        } else {
            System.err.println("\nERROR: ILLEGAL ARGUMENT PASSED AT COMMAND LINE.\n");
        }

        NUMB_ITEMS = EditDist.countNumberOfItems(sortOneFilename, labeled);
        if (EditDist.countNumberOfItems(sortTwoFilename, labeled) != NUMB_ITEMS) {
            System.err.println("\nERROR: FILES CONTAIN DIFFERENT NUMBER OF ITEMS!\n");
        }

//create two arrays of sets
//each array is a different sort, and the sets are groupings made in the sort
        sortOne = new HashSet[NUMB_GROUPS];
        sortTwo = new HashSet[NUMB_GROUPS];
        for (int i = 0; i < NUMB_GROUPS; i++) {
            sortOne[i] = new HashSet<Integer>();
            sortTwo[i] = new HashSet<Integer>();
        }

        Scanner sortOneScanner = new Scanner(new FileReader(sortOneFilename));
        for (int i = 0; i < NUMB_GROUPS; i++) {
            if (sortOneScanner.hasNextLine()) {
                Scanner sortOneGroupScanner = new Scanner(sortOneScanner.nextLine());
                if (labeled) //if first token in a line is a label for that category
                {
                    sortOneGroupScanner.next(); //then skip over label
                }
                while (sortOneGroupScanner.hasNext()) {
                    sortOne[i].add(Integer.valueOf(sortOneGroupScanner.nextInt()));
                }
                sortOneGroupScanner.close();
            }
        }
        sortOneScanner.close();

        Scanner sortTwoScanner = new Scanner(new FileReader(sortTwoFilename));
        for (int i = 0; i < NUMB_GROUPS; i++) {
            if (sortTwoScanner.hasNextLine()) {
                Scanner sortTwoGroupScanner = new Scanner(sortTwoScanner.nextLine());
                if (labeled) //if first token in a line is a label for that category
                {
                    sortTwoGroupScanner.next(); //then skip over label
                }
                while (sortTwoGroupScanner.hasNext()) {
                    sortTwo[i].add(Integer.valueOf(sortTwoGroupScanner.nextInt()));
                }
                sortTwoGroupScanner.close();
            }
        }
        sortTwoScanner.close();
    }

    static int countNumberOfGroups(String filename) throws Exception {
        int count = 0;
        Scanner sc = new Scanner(new FileReader(filename));
        while (sc.hasNextLine()) {
            count++;
            sc.nextLine();
        }
        sc.close();
        return count;
    }

    static int countNumberOfItems(String filename) throws Exception {
        int count = 0;
        Scanner sc = new Scanner(new FileReader(filename));
        while (sc.hasNextInt()) {
            count++;
            sc.nextInt();
        }
        sc.close();
        return count;
    }

    static int countNumberOfItems(String filename, boolean groupsLabeled) throws Exception {
        int count = 0;
        Scanner sc = new Scanner(new FileReader(filename));
        while (sc.hasNextLine()) {
            Scanner lineScanner = new Scanner(sc.nextLine());
            if (groupsLabeled) {
                lineScanner.next();
            }
            while (lineScanner.hasNextInt()) {
                count++;
                lineScanner.nextInt();
            }
            lineScanner.close();
        }
        sc.close();
        return count;
    }

    private float[][] buildCostMatrix() throws Exception {
        float[][] costMatrix = new float[NUMB_GROUPS][NUMB_GROUPS];
        for (int i = 0; i < NUMB_GROUPS; i++) {
            for (int j = 0; j < NUMB_GROUPS; j++) {
                HashSet<Integer> conjSet = new HashSet<Integer>(sortOne[i]);
                conjSet.retainAll(sortTwo[j]);
                costMatrix[i][j] = (float) ((sortOne[i].size() - conjSet.size()) + (sortTwo[j].size() - conjSet.size()));
            }
        }
        return costMatrix;
    }

    public int calcEditDist() throws Exception {
        HungarianAlgorithm hungarian = new HungarianAlgorithm();
        float[][] costMatrix = buildCostMatrix();
        int[][] maxWeightMatch = hungarian.computeAssignments(costMatrix);

        int EditDistance = NUMB_ITEMS;
        for (int i = 0; i < NUMB_GROUPS; i++) {
            HashSet<Integer> tempSet = new HashSet<Integer>(sortOne[maxWeightMatch[i][0]]);
            tempSet.retainAll(sortTwo[maxWeightMatch[i][1]]);
            EditDistance -= tempSet.size();
        }
        return EditDistance;
    }

// Accessors
    public int getNumbOfGroups() {
        return NUMB_GROUPS;
    }

    public int getNumbItems() {
        return NUMB_ITEMS;
    }

    /*
     ** usage: java EditDist [sort 1 fname] [sort 2 fname] [optional: 'true'|'false' (for group labels)]
     */
    public static void main(String args[]) {
        try {

            String directory1 = args[0];
            String directory2 = args[1];
            String output = args[2];
            double total_avg = 0.0;

            PrintWriter outputwriter = new PrintWriter(new FileWriter(output));

//String sortOneFilename = args[0];
//String sortTwoFilename = args[1];
            String labelFlag = "false";
            if (args.length == 4) {
                labelFlag = args[2].trim();
            }

//EditDist ed = new EditDist(sortOneFilename, sortTwoFilename, labelFlag);
//System.out.println(ed.calcEditDist());
//for a_single_file in the_directory
            File dir1 = new File(directory1);
            File dir2 = new File(directory2);
            String[] children1 = dir1.list();
            String[] children2 = dir2.list();

            if (children1 == null) {
// check to see if directories are empty and return if so
                System.out.println("Empty Directory for first argument");
                return;
            } else if (children2 == null) {
                System.out.println("Empty Directory for second argument");
                return;
            } else {
// now want to enumerate through dir1 and dir2's children and compare
// right now we'll leave "checking directory 1: file 1 against directory 2: file 1,
// but there will be code to easily handle that if you don't want it anymore

                String[][] spreadsheet = new String[dir1.list().length][2];

                System.out.println("children1 length: " + children1.length);
                int ctr = 0;
                for (int dir1_ctr = 0; dir1_ctr < children1.length; dir1_ctr++) {

                    String fname1 = directory1.concat("\\");
                    fname1 = fname1.concat(children1[dir1_ctr]);
                    String fname1_number = children1[dir1_ctr].split("\\.")[0];

// the total for the current item we're looking at in directory 1
                    int cur_total_dir1 = 0;
                    double cur_dir1_average = 0.0;

                    for (int dir2_ctr = 0; dir2_ctr < children2.length; dir2_ctr++) {
                        String fname2 = directory2.concat("\\");
                        fname2 = fname2.concat(children2[dir2_ctr]);

                        System.out.println("Testing " + fname1 + " against " + fname2);
                        EditDist ed = new EditDist(fname1, fname2, labelFlag);

// store this distance in cur_total_dir1 to use for averages;
                        cur_total_dir1 += ed.calcEditDist();

// print out this distance if we want to
                        System.out.println("Edit Distance: " + ed.calcEditDist());
                        outputwriter.println(fname1 + "\t" + fname2 + "\t" + ed.calcEditDist());

//this is just a total count of all the comparisons we've done so far
                        ctr++;
                    }

// now we're done iterating through the items in directory 2, so we want to 
// take the average over total number of these elements
                    cur_dir1_average = cur_total_dir1 / (float) children2.length;

//System.out.println("The average for " + fname1 + " was " + cur_dir1_average);
//this is where we save the information into an array for a spreadsheet
                    spreadsheet[dir1_ctr][0] = fname1_number; // name of dir1
                    spreadsheet[dir1_ctr][1] = Double.toString(cur_dir1_average); // average
                    total_avg = total_avg + cur_dir1_average;
                }

                outputwriter.close();

                total_avg = total_avg / (float) children1.length;

                System.out.println("Counter: " + ctr);
                System.out.println("The total average: " + total_avg);

                System.out.println("Now the spreadsheet:");
                for (int dir1_ctr = 0; dir1_ctr < children1.length; dir1_ctr++) {
                    System.out.println(spreadsheet[dir1_ctr][0] + " " + spreadsheet[dir1_ctr][1]);
                }
            }

//EditDist ed = new EditDist(sortOneFilename, sortTwoFilename, labelFlag);
//System.out.println(ed.calcEditDist());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.out.print("\n");
            System.out.println("USAGE: java EditDist [sort 1 fname] [sort 2 fname] [optional: 'true'|'false' (for group labels)]");
            System.out.println("see ReadMe for more information.");
        }
    }
}
