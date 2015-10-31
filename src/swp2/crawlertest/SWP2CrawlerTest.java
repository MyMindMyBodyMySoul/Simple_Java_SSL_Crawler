/*
 * This ist the main heart of my simple java ssl crawler
 * It will parse a supplied textfile, normalize any urls
 * to https://www.<anydomain.tld>
 * When finished, it will try to do a SSL/TLS handshake with each site
 * first as a data object to a linked list.
 * After finishing crawling, all results will be written to a text file
 *
 * Things to do:
 * Implement a proper threaded producer - consumer schema
 * because bufferedWriter is not thread safe
 */
package swp2.crawlertest;

// Imports for file ops;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

// Imports for managing results
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// Imports for HTTPS ops
import javax.net.ssl.HttpsURLConnection;

// Imports for threaded ops
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author MrZed
 */
public class SWP2CrawlerTest {

    static {
        THEINSTANCE = SWP2CrawlerTest.getInstance();

    }

    private static final File URLFILE = new File("alexa-top-500.txt");
    private static final File OUTPUTFILE = new File("results.txt");
    private static Queue<Result> results = new ConcurrentLinkedQueue<>();
    private static Queue<URL> urlWorkSet = new ConcurrentLinkedQueue<>();
    private static SWP2CrawlerTest THEINSTANCE;
    private static Worker[] threadList = new Worker[10];

    // ExecutorService not yet used.
    private static ExecutorService doWhipping = Executors.newFixedThreadPool(10);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("Parsing file and normalise urls.\n");
        normalizeURLS();

        System.out.println("Finished parsing and normalising.\n");

        System.out.println("Starting crawling urls.");

//        for (int i = 0; i < threadList.length; i++) {
//            threadList[i] = new Worker();
//            threadList[i].start();
//
//        }
//        while (!urlWorkSet.isEmpty()) {
//            for (int i = 0; i < threadList.length;i++) {
//                if(!threadList[i].isAlive() || threadList[i]==null){
//                    threadList[i] = new Worker();
//                    threadList[i].start();
//                }
//       
//            }
//        }
        
      startCrawling();

        System.out.println("Finished crawling\nWriting results to file\n.");

        writeResult();

        System.out.println("Finished writing. Cleaning up");
        
        System.exit(0);

    } // end main

    private static void normalizeURLS() {
        String stuffJustRead;
        FileReader fileIn = null;
        BufferedReader inPut = null;
        try {
            fileIn = new FileReader(URLFILE);
            inPut = new BufferedReader(fileIn);
            // Limited to 500 URLS
            for (int limiter = 0; limiter < 500; limiter++) {
                //while (inPut.ready()) {

                stuffJustRead = inPut.readLine();
                if (stuffJustRead.contains("https://")) {
                    urlWorkSet.add(new URL(stuffJustRead));
                } else if (stuffJustRead.contains("www")) {
                    urlWorkSet.add(new URL("https://" + stuffJustRead));
                } else {
                    urlWorkSet.add(new URL("https://www." + stuffJustRead));
                }
            }
            fileIn.close();
        } catch (IOException ioEx) {
            try {
                if (fileIn != null) {
                    fileIn.close();
                }
            } catch (IOException io) {
                // 
            }
        }

    }

    private static void startCrawling() {
        while (!urlWorkSet.isEmpty()) {
            doWhipping.submit(new Runnable() {
                @Override
                public void run() {
                    // Variable con will be thread local
                    HttpsURLConnection con = null;
                    try {
                        con = (HttpsURLConnection) urlWorkSet.poll().openConnection();
                        System.out.println(Thread.currentThread().getId() + " Doing: " + con.getURL().toString());
                        con.connect();
                        results.add(new Result(con.getURL().toString(),
                                "Cipersuite used: " + con.getCipherSuite()));
                        con.disconnect();
                    } catch (Exception e) {
                        /* 
                         *Every Exception while connecting to a url
                         *will be caught and added to the resultqueue
                         */
                        results.add(new Result(con.getURL().toString(),
                                "Error: " + e.toString()));
                        con.disconnect();
                    }
                }
            }); // Runnable Submit end
        } // for end
        doWhipping.shutdown();
        while (!doWhipping.isTerminated());
    }

    private static void writeResult() {
        FileWriter fileOut = null;
        BufferedWriter output = null;
        try {
            fileOut = new FileWriter(OUTPUTFILE);
            output = new BufferedWriter(fileOut);
            while (!results.isEmpty()) {
                output.write(results.poll().toString());
            }
            output.close();
            fileOut.close();
        } catch (IOException ioEx) {
            try {
                if (output != null) {
                    output.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException ioExinner) {
                //
            }
        }
    }

    public static SWP2CrawlerTest getInstance() {
        if (THEINSTANCE == null) {
            THEINSTANCE = new SWP2CrawlerTest();
        }
        return THEINSTANCE;
    }

    URL getURL() {
        return urlWorkSet.poll();
    }

    void addResult(Result res) {
        this.results.add(res);
    }

    /**
     * Tests if there are any more urls to be testet.
     *
     * @return true if there are more urls. False otherwise.
     */
    boolean areThereMoreUrls() {
        return !urlWorkSet.isEmpty();
    }
} // end class
