/*
 * This ist the main heart of my simple java ssl crawler
 * It will parse a supplied textfile, normalize any urls
 * to https://www.<anydomain.tld>
 * When finished, it will try to do a SSL/TLS handshake with each site
 * and write the result to a file.
 *
 * Things to do:
 * Implement a proper threaded producer - consumer schema
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

// Imports for HTTPS ops
import javax.net.ssl.HttpsURLConnection;

// Imports for threaded ops
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
/**
 *
 * @author MrZed
 */
public class SWP2CrawlerTest {

    private static File urlFile = new File("alexa-top-500.txt");
    private static File outputFile = new File("results.txt");
    private static Queue<Result> results = new LinkedList<>();
    private static Queue<URL> urlWorkSet = new LinkedList<>();

    // ExecutorService not yet used.
    //private static ExecutorService doWhipping = Executors.newCachedThreadPool();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String stuffJustRead;
        HttpsURLConnection con = null;
        FileReader fileIn = null;
        FileWriter fileOut = null;
        BufferedWriter output = null;
        BufferedReader inPut = null;
        try {
            // Make file operators ready
            fileIn = new FileReader(urlFile);
            fileOut = new FileWriter(outputFile);
            output = new BufferedWriter(fileOut);
            inPut = new BufferedReader(fileIn);

            // Parse source file and normalize urls
            System.out.println("Parsing file and normalise urls.\n");
            while (inPut.ready()) {
                stuffJustRead = inPut.readLine();
                if (stuffJustRead.contains("https://")) {
                    urlWorkSet.add(new URL(stuffJustRead));
                } else if (stuffJustRead.contains("www")) {
                    urlWorkSet.add(new URL("https://" + stuffJustRead));
                } else {
                    urlWorkSet.add(new URL("https://www." + stuffJustRead));
                }
            }
            System.out.println("Finished parsing and normalising.\n");
            System.out.println("Starting crawling urls.");

            // limited urls to 300 
            //while (!urlWorkSet.isEmpty()) {
            for (int limiter = 0; limiter < 300; limiter++) {
                try {
                    con = (HttpsURLConnection) urlWorkSet.poll().openConnection();
                    System.out.println("Doing: " + con.getURL().toString());
                    con.connect();
                    results.add(new Result(con.getURL().toString(), con.getCipherSuite()));

                    con.disconnect();
                } catch (Exception e) {
                    /* 
                     *Every Exception while connecting to a url
                     *will be written in the result file
                     */
                    output.write("Error: " + con.getURL().toString()
                            + ", " + e.toString() + "\n");
                    con.disconnect();
                }
            }
            System.out.println("Finished crawling\n Writing results to file\n.");

            while (!results.isEmpty()) {
                output.write(results.poll().toString());
            }
            System.out.println("Finished writing. Cleaning up");

            // Close input and output File
            fileIn.close();
            output.close();
            fileOut.close();

        } catch (IOException e) {
            if (con != null) {
                con.disconnect();
            }
            try {
                if (fileIn != null) {
                    fileIn.close();
                }
                if (output != null) {
                    output.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException io) {
                // 
            }
        }
    }

}
