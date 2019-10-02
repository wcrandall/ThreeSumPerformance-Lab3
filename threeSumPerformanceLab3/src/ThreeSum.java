import java.lang.management.ManagementFactory;

import java.lang.management.ThreadMXBean;

import java.io.*;
import java.util.HashSet;
import java.util.Random;


public class ThreeSum {







    /* define constants */

    static int MAXVALUE =  2000000000;

    static int MINVALUE = -2000000000;

    static int numberOfTrials = 100;

    static int MAXINPUTSIZE  = (int) Math.pow(2,12);

    static int MININPUTSIZE  =  1;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time



    static String ResultsFolderPath = "/home/wyatt/Results/lab3/"; // pathname to results folder

    static FileWriter resultsFile;

    static PrintWriter resultsWriter;
    private Random random;


    public static void main(String[] args) {
        //verifying that all threeSum functions work
//       verifyThreeSum();
//       System.exit(0);
        // running experiment three times to get accurate results
        System.out.println("starting THROWAWAY EXP1");

        runFullExperiment("ThreeSumFaster-Exp1-ThrowAway.txt");
        System.out.println("done \n now starting EXP2");

        runFullExperiment("ThreeSumFaster-Exp2.txt");
        System.out.println("done \n now starting EXP3");
        runFullExperiment("ThreeSumFaster-Exp3.txt");
        System.out.println("done");

    }


    //function to run full experiment
    static void runFullExperiment(String resultsFileName){


        //trying to write to file and displaying error message if it fails
        try {

            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);

            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {

            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);

            return; // not very foolproof... but we do expect to be able to create/open the file...

        }


        // instantiating stopwatch class
        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials

        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial


        //printing to file
        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        //flushing so it immediately goes to file and not a queue
        resultsWriter.flush();

        /* for each size of input we want to test: in this case starting small and doubling the size each time */

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {

            // progress message...

            System.out.println("Running test for input size "+inputSize+" ... ");




            long batchElapsedTime = 0;


            //forcing garbage collection
            System.gc();



            // run the trials

            for (int trial = 0; trial < numberOfTrials; trial++) {

                long[] testList = createRandomIntegerList(inputSize);

                /* run the function we're testing on the trial input */
                TrialStopwatch.start();
                threeSumFaster(testList);
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();

            }

            // calculate the average time per trial in this batch
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;



            /* print data for this size of input */

            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch);
            //using flush so it immediately writes to file and does not go to queue
            resultsWriter.flush();

            System.out.println(" ....done.");

        }

    }

    //the slowest threeSum function
    public static long threeSum(long[] a)
    {
        // N is the length of the array passed in
        int N = a.length;

        // cnt is the count of threeSums
        long cnt = 0;

        // triple nested for loop in to find the values that add to three in the passed in array
        for(int i = 0; i < N; i++)
        {
            for(int j = i+1; j < N; j++)
            {
                for(int k = j+1; k < N; k++)
                {
                    if(a[i]+a[j]+a[k] == 0)
                    {
                        cnt++;
                        //System.out.println("ThreeSum: " + a[i] + " " + a[j] + " " + a[k] + " Count: " + cnt);
                    }
                }
            }
        }

        // returning count of threeSums
        return cnt;
    }






    public static long threeSumFaster(long[] a)
    {
        // getting length of the array passed in
        int N = a.length;
        // sort array elements so binary search can be used
        mergeSort(a,0,a.length-1);
        // initializing the count of the number of threeSums
        long cnt = 0;

        // double nested for loop
        // i will be the first value used and j the second
        // the third value of the threeSum is found using a binary search function
        for(int i = 0; i < N; i++)
        {
            for(int j = i+1; j < N; j++)
            {

                // adding first two values together since the third value will need to be opposite of them
                long result = a[i] + a[j];

                //long toPrint = result;
                //subtracting the sum of first two values from 0 to find what the third value must be
                result = 0 - result;

                /* calling a binarySearch that returns the index location
                 * if the value that is being searched for is present in the array */
                int isZeroMaker = binarySearch(result, a);

                /* if the value exists and is greater than j it is a threeSum
                 * it must be greater than j because in the regular we have loops
                 * such that i = 0; j = i + 1 and k = j+1 */
                if (isZeroMaker > j) {
                    //System.out.println("Result is " + toPrint + " what makes it zero " + a[isZeroMaker]);
                    cnt++;
                }

            }
        }
        return cnt;
    }



    static int threeSumFastest(long arr[])
    {
        int cnt = 0;
        int n = arr.length;


        for (int i=0; i<n-1; i++)
        {
            // initialize left and right
            /* imagine an array listed in a horizontal manner [1][2][3]........[n]
            * l = the left most value
            * r = the right most value
            * x = the current value the loop is on in the array */
            int l = i + 1;
            int r = n - 1;
            long x = arr[i];

            // while the left side value is less than or = to the right side value continue looping
            while (l < r)
            {
                /* if current value + right and left side value is equivalent to 0
                * add one to count also move in the right side values by one */
                if (x + arr[l] + arr[r] == 0)
                {
                    // print elements if it's sum is zero
                    System.out.print(x + " ");
                    System.out.print(arr[l]+ " ");
                    System.out.println(arr[r]+ " ");
                    cnt++;
                    l++;
                    r--;

                }

                // If sum of three elements is less
                // than zero then move in left side by one
                else if (x + arr[l] + arr[r] < 0)
                    l++;

                    // if sum is greater than zero than
                    // move in right side by one
                else
                    r--;
            }
        }
        return cnt;

    }

    //binary search function
    public static int binarySearch(long key, long[] list) {

        int i = 0;

        int j= list.length-1;

        if (list[i] == key) return i;

        if (list[j] == key) return j;

        int k = (i+j)/2;

        while(j-i > 1){

            //resultsWriter.printf("%d %d %d %d %d %d\n",i,k,j, list[0], key, list[list.length-1]); resultsWriter.flush();

            if (list[k]== key) return k;

            else if (list[k] < key) i=k;

            else j=k;

            k=(i+j)/2;

        }

        return -1;

    }



    public static long[] createRandomIntegerList(int size) {

        Random random = new Random();
        long[] newList= new long[size];
        for(int i = 0; i < size; i++)
        {
            newList[i] = random.nextLong();
        }
        return newList;

    }
    static void merge(long arr[], int l, int m, int r)
    {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        long L[] = new long [n1];
        long R[] = new long [n2];

        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using
    // merge()
    static void mergeSort(long arr[], int l, int r)
    {
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    public static void verifyThreeSum()
    {
        //must have two values greater than last element that %5 = 0
        int size = 13;

        //initializing list
        long[] list = {0,1,2,3,4,-2};

        //putting elements in list to test
//        for(int l = 0; l < size; l++)
//        {
//            //makes every 5th element a threesum
//            if(l % 10 == 0 && l > 0)
//            {
//                list[l] = l;
//                list[l + 1] = 0 - (list[l]/2);
//                list[l + 2] = 0 - (list[l]/2);
//                l= l + 2;
//            }
//            else
//            {
//                list[l]=l;
//            }
//
//        }
        mergeSort(list,0,list.length-1);

        System.out.println("list generated");
//        for(int j = 0; j < list.length; j++)
//        {
//            System.out.println(list[j]);
//        }

        long countOfThreeSumsFaster = threeSumFaster(list);
        long countOfThreeSums = threeSum(list);
        long countOfThreeSumsFastest = threeSumFastest(list);
        System.out.println("The count of threeSums is " + countOfThreeSums);
        System.out.println("The count of threeSums with threeSumFaster is " + countOfThreeSumsFaster);
        System.out.println("the count of threeSums with threeSumFastest is " + countOfThreeSumsFastest);
    }

} 