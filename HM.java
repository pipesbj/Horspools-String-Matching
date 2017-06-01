/*	Author: Benjamin Pipes

 * 		The purpose of this program is to design and implement the Horspool's
 * 		String Matching algorithm and analyze the execution time for multiple
 * 		input files.
 * 
 *  	Compares and adds/subtracts are accounted for. In "For" loops, there is 
 *  	at minimum 1 compare and 1 add each iteration, except the first add 
 *  	occurs after the first iteration,so the arithmetic operator count 
 *  	must be decremented when the for loop is exited.
 *  
 *  	input file must be formatted as follows:
 *  	<pattern to search>
 *  	<...text to search through
 *  	 ...
 *  	 ...>
 *  
 *  	Program accounts for multiple lines of text, and the 127 character 
 *  	ASCII language
 *  
 *  	Variable Dictionary:
		String fileName = name of file to be used
		Scanner in = for processing the file data
		char pat[] = holds the pattern to search for as char array
		String line = for processing each line of file
		int table = array used as table in algorithm. No collision resolution
		int m = length of pattern
		int n = length of given text
		int pos = position where the given pattern is found
		int alphabet = size of alphabet being used in the text file.
		int key = used to store data in the table via hashFunction
		
		HorspoolStringMatching(pattern array, text array)
			Construct shift table
			Start at the beginning of the text
			Repeat (until a match is found, or the algorithm reaches beyond 
			the last character)
				Compare all corresponding characters in the pattern and 
				text until they're either all found, or a mismatching pair 
				is found. In the latter case, shift over the number of spaces 
				that corresponds with the letter in the shift table.
				Return index where pattern begins.
 			End loop
			If no pattern match, return -1.
		end HorspoolStringMatching

 *  	
 *  	UNIX: javac HM.java
 *			  java HM 
 *	File name is very first line of the main method.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HM {
	//accumulator variables, for execution analysis
static long algCompareCt = 0;
static long algArithCt = 0;
static long algStrCalls = 0;
static long handleCompareCt = 0;
static long handleArithCt = 0;

	public static void main(String[] args) throws Exception {
	
		String fileName = "sm.txt";
		Scanner in = new Scanner(new File(fileName));
		char pat[];
		String line;
		
		//timing variables
		long startTime = 0;
		long stopTime = 0;
		long overTime = 0;
		long runTime = 0;
		long handleStart = 0;
		long handleStop = 0;
		long handleTime = 0;
		startTime = System.nanoTime();
		stopTime = System.nanoTime();
		overTime = stopTime - startTime;

		//load the text to be searched into array
		System.out.println("Loading array...");
		handleStart = System.nanoTime();
		char text[] = loadCharArray(in, fileName);
		handleStop = System.nanoTime();
		
		//calculate and store data handling time
		handleTime = handleStop - handleStart - overTime;
		
		//load the pattern to search for into array
		in = new Scanner(new File(fileName));
		line = in.nextLine();
		pat = line.toCharArray();
		
		//being execution of Horspool's String matching algorithm
		System.out.println("****Starting execution of Horspool's matching****");
		startTime = System.nanoTime();
		int foundInd = horspoolMatching(pat, text);
		stopTime = System.nanoTime();
		System.out.println("****Stopping execution****");
		
		//calculate and store algorithm execution time
		runTime = stopTime - startTime - overTime;
		
		//***************************************************************
		//Program done, print out answer and statistics
		
		if (foundInd != -1){
		System.out.print("Found pattern at index " + foundInd + " near: \n\"");
		for (int i = foundInd - 4; i < foundInd + pat.length; i++){
			System.out.print(text[i]);
		}
		System.out.println("\"");
		}
		else
			System.out.println("Pattern not found in text");
	
		
		String format = "%-20s %10d %25d\n";
		String formatF = "%-20s %10f %25f\n";
		System.out.println("****************Alg. Execution*************Data Handling****");
		System.out.format(format, "Min. Compares:", algCompareCt, handleCompareCt);
		System.out.format(format, "Adds/Subtracts:", algArithCt, handleArithCt);
		System.out.format(format, "Execution Time(ns):", runTime, handleTime);
		System.out.format(formatF, "Execution Time(ms):", runTime / 1000000.0, handleTime / 1000000.0);
	
		in.close();	
	}//main
	
	
	public static int horspoolMatching(char pattern[], char text[]) throws Exception{
		/*	Input: pattern array, text to be searched
		 * 	Output: the index of the text array where the target word begins.
		 * 
		 * 	Horspool's algorithm, where the character check jumps forward 
		 * 	depending on what character the check fails against.
		 */
		int table[] = shiftTable(pattern);	
		int m = pattern.length;
		int n = text.length;
		int i;
		int pos = 0;
		
		while (m + pos <= n){
			algCompareCt++;
			i = m - 1;
			algArithCt = algArithCt + 2;
			while (text[pos + i] == pattern[i]){
				algCompareCt = algCompareCt + 2;
				//1 for the while statement, 1 for the following if statement
				if (i == 0){
					return pos;
				}
				i = i - 1;
				algArithCt = algArithCt + 2;
			}
			pos = pos + table[text[pos + m - 1]];
			algArithCt = algArithCt + 3;
		}
		return -1;
	}
	public static int[] shiftTable(char pattern[]){
		/*	Input: a pattern, as character array
		 * 	Output: a table (array) with numbers representing the shift values
		 * 			for a specific character
		 * 
		 * 	For characters that are not found in the given pattern, the shift
		 * 	length will be the length of the pattern. Otherwise, the shift 
		 * 	length will be the length from the last character in the pattern.
		 */
		int m = pattern.length;
		int alphabet = 127;
		int table[] = new int[alphabet];
		
		for (int i = 0; i < alphabet; i++){
			algArithCt++;
			algCompareCt++;
			//initiates shift table. Loads max pattern 
			// length for every character
			table[i] =  m;
		}
		algArithCt--;
		//decrement 1 arithmetic count, accounts for first pass of "for" loop
		for (int j = 0; j < m - 1; j++){
			//updates shift table for values that are in the pattern.
			table[pattern[j]] =  m  - 1 - j;
			algArithCt = algArithCt + 4;
			algCompareCt++;
		}
		algArithCt--;
		//decrement 1 arithmetic count, accounts for first pass of "for" loop
		return table;

	}

    public static char[] loadCharArray(Scanner in, String fileName) throws FileNotFoundException{ 
    	/*	Input: Scanner that is using the input file, the file name
    	 * 	Output: Text to be searched, as char array.
    	 * 
    	 * 	This particular implementation requires the scanner to skip over
    	 * 	the first line of the text file, hence the 'in.nextLine()' that
    	 * 	occurs prior to the loops.
    	 */
    	String line;
    	int textSize = 0;
    	int carReturn = 0;
    	
    	//skip first line
    	in.nextLine();
    	while (in.hasNextLine()){
    		handleCompareCt++;
			line = in.nextLine();
			textSize = textSize + line.length();
			handleArithCt++;
		}
    	
    	//create empty array with correct size
    	char text[] = new char[textSize];
    	in = new Scanner(new File(fileName));
    	
    	//skip first line
    	in.nextLine();
    	while (in.hasNextLine()){
    		handleCompareCt++;
			//load text into an array. Accounts for multiple lines.
			line = in.nextLine();
			
			handleCompareCt++;
			if (carReturn < 1){
				
				//first line of text loaded in array
				for (int i = 0; i < line.length(); i++){
				text[i] = line.charAt(i);
				handleArithCt++;
				handleCompareCt++;
				}
				handleArithCt--;
				//decrement 1 arithmetic count, accounts for first pass of "for" loop
				carReturn = carReturn + line.length();
				
				handleArithCt++;
			}
			else{
				//any successor lines loaded in array
			for (int j = 0; j < line.length();j++){
			text[j + carReturn] = line.charAt(j);
			handleArithCt = handleArithCt + 2;
			handleCompareCt++;
			}
			handleArithCt--;
			//decrement 1 arithmetic count, accounts for first pass of "for" loop
			
			carReturn = carReturn + line.length();
			
			handleArithCt++;
			}
		}//creating text array
    	
    	in.close();
    	return text;
    }
}
