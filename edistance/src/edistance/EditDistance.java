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

public class EditDistance
{
	private HashSet<Integer>[] sortOne, sortTwo;
	private final int NUMB_GROUPS;
	private final int NUMB_ITEMS;
	private boolean labeled = false;
	
	@SuppressWarnings("unchecked")
	EditDistance(String sortOneFilename, String sortTwoFilename, String labeledGroups) throws Exception
	{
		int sortOneGroupCount = EditDistance.countNumberOfGroups(sortOneFilename);
		int sortTwoGroupCount = EditDistance.countNumberOfGroups(sortTwoFilename);
		NUMB_GROUPS = Math.max(sortOneGroupCount, sortTwoGroupCount);
		
		if(labeledGroups.equalsIgnoreCase("true"))
			labeled = true;
		else if(labeledGroups.equalsIgnoreCase("false"))
			labeled = false;
                else {
                        System.err.println("\nERROR: ILLEGAL ARGUMENT PASSED AT COMMAND LINE.\n");
                }
		NUMB_ITEMS = EditDistance.countNumberOfItems(sortOneFilename, labeled);
		if(EditDistance.countNumberOfItems(sortTwoFilename, labeled) != NUMB_ITEMS)
			System.err.println("\nERROR: FILES CONTAIN DIFFERENT NUMBER OF ITEMS!\n");
		
		//create two arrays of sets
		//each array is a different sort, and the sets are groupings made in the sort
		sortOne = new HashSet[NUMB_GROUPS];
		sortTwo = new HashSet[NUMB_GROUPS];
		for(int i=0; i<NUMB_GROUPS; i++){
			sortOne[i] = new HashSet<Integer>();
			sortTwo[i] = new HashSet<Integer>();
		}
		
		Scanner sortOneScanner = new Scanner(new FileReader(sortOneFilename));
		for(int i=0; i<NUMB_GROUPS; i++){
			if(sortOneScanner.hasNextLine()){
				Scanner sortOneGroupScanner = new Scanner(sortOneScanner.nextLine());
				if(labeled) //if first token in a line is a label for that category
					sortOneGroupScanner.next(); //then skip over label
				while(sortOneGroupScanner.hasNext()){
					sortOne[i].add(Integer.valueOf(sortOneGroupScanner.nextInt()));
				}
				sortOneGroupScanner.close();
			}
		}
		sortOneScanner.close();
		
		Scanner sortTwoScanner = new Scanner(new FileReader(sortTwoFilename));
		for(int i=0; i<NUMB_GROUPS; i++){
			if(sortTwoScanner.hasNextLine()){
				Scanner sortTwoGroupScanner = new Scanner(sortTwoScanner.nextLine());
				if(labeled) //if first token in a line is a label for that category
					sortTwoGroupScanner.next(); //then skip over label
				while(sortTwoGroupScanner.hasNext()){
					sortTwo[i].add(Integer.valueOf(sortTwoGroupScanner.nextInt()));
				}
				sortTwoGroupScanner.close();
			}
		}
		sortTwoScanner.close();
	}
	
	static int countNumberOfGroups(String filename) throws Exception
	{
		int count = 0;
		Scanner sc = new Scanner(new FileReader(filename));
		while(sc.hasNextLine()){
			count++;
			sc.nextLine();
		}
		sc.close();
		return count;
	}
	
	static int countNumberOfItems(String filename) throws Exception
	{
		int count = 0;
		Scanner sc = new Scanner(new FileReader(filename));
		while(sc.hasNextInt()){
			count++;
			sc.nextInt();
		}
		sc.close();
		return count;
	}
	
	static int countNumberOfItems(String filename, boolean groupsLabeled) throws Exception
	{
		int count = 0;
		Scanner sc = new Scanner(new FileReader(filename));
		while(sc.hasNextLine()){
			Scanner lineScanner = new Scanner(sc.nextLine());
			if(groupsLabeled)
				lineScanner.next();
			while(lineScanner.hasNextInt()){
				count++;
				lineScanner.nextInt();
			}
			lineScanner.close();
		}
		sc.close();
		return count;
	}
	
	private float[][] buildCostMatrix() throws Exception
	{
		float[][] costMatrix = new float[NUMB_GROUPS][NUMB_GROUPS];
		for(int i=0; i<NUMB_GROUPS; i++){
			for(int j=0; j<NUMB_GROUPS; j++){
				HashSet<Integer> conjSet = new HashSet<Integer>(sortOne[i]);
				conjSet.retainAll(sortTwo[j]);
				costMatrix[i][j] = (float)((sortOne[i].size()-conjSet.size())+(sortTwo[j].size()-conjSet.size()));
			}
		}
		return costMatrix;
	}
	
	public int calcEditDist() throws Exception
	{
		HungarianAlgorithm hungarian = new HungarianAlgorithm();
		float[][] costMatrix = buildCostMatrix();
		int[][] maxWeightMatch = hungarian.computeAssignments(costMatrix);
		
		int ED = NUMB_ITEMS;
		for(int i=0; i<NUMB_GROUPS; i++){
			HashSet<Integer> tempSet = new HashSet<Integer>(sortOne[maxWeightMatch[i][0]]);
			tempSet.retainAll(sortTwo[maxWeightMatch[i][1]]);
			ED -= tempSet.size();
		}
		return ED;
	}
	
	// Accessors
	public int getNumbOfGroups()
	{ return NUMB_GROUPS; }
	public int getNumbItems()
	{ return NUMB_ITEMS; }
	
	/*
	** usage: java EditDistance [sort 1 fname] [sort 2 fname] [optional: 'true'|'false' (for group labels)]
	*/
	public static void main(String args[])
	{
		try{
			String sortOneFilename = args[0];
			String sortTwoFilename = args[1];
			String labelFlag = "false";
			if(args.length == 3)
				labelFlag = args[2].trim();
			
			EditDistance ed = new EditDistance(sortOneFilename, sortTwoFilename, labelFlag);
			
			System.out.println(ed.calcEditDist());
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			System.out.print("\n");
			System.out.println("USAGE: java EditDistance [sort 1 fname] [sort 2 fname] [optional: 'true'|'false' (for group labels)]");
			System.out.println("see ReadMe for more information.");
		}
	}
}