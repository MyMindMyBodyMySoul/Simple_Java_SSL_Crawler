/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swp2.crawlertest;

import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author MyMindMyBodyMySoul
 */
public class Worker extends Thread {

    private URL urlToTest;
    private SWP2CrawlerTest mainClass;
    private HttpsURLConnection con;

    public Worker() {
        mainClass = SWP2CrawlerTest.getInstance();
    }

    @Override
    public void run() {
        try {
            while (true) {

                urlToTest = mainClass.getURL();
                if (urlToTest == null) {
                    System.out.println("Thread Nummer "
                            +Thread.currentThread().getId()+" ist gestorben,");
                    this.join();
                }
                con = (HttpsURLConnection) urlToTest.openConnection();
                System.out.println(Thread.currentThread().getId()
                        + " Doing: " + con.getURL().toString());
                con.setRequestMethod("HEAD");
                con.connect();
                mainClass.addResult(new Result(con.getURL().toString(),
                        "Cipersuite used: " + con.getCipherSuite()));
                con.disconnect();
            }
        } catch (IOException e) {
            mainClass.addResult(new Result(con.getURL().toString(),
                    "Error: " + e.toString()));
        } catch (InterruptedException iE) {
            iE.printStackTrace();
        }
    }

}
