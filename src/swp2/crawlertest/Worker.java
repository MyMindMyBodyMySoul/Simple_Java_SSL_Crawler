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
        while (true) {
            try {
                urlToTest = mainClass.getURL();
                if (urlToTest == null & !mainClass.areThereMoreUrls()) {
                    System.out.println("Thread number "
                            + Thread.currentThread().getId() + " has joined\n");
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

            } catch (IOException e) {
                mainClass.addResult(new Result(con.getURL().toString(),
                        "Error: " + e.toString()));
                continue;
            } catch (InterruptedException iE) {
                iE.printStackTrace();
            } catch (Throwable t) {
                System.out.println(t.toString());
            }
        }
    }

}
