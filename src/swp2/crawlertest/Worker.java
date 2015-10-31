/*
 * This class will be the base of a thread-object.
 * My aim is to crawl multible web site at once with different threads.
 * Class is under construction.
 */
package swp2.crawlertest;

import java.util.concurrent.Callable;
import javax.net.ssl.*;


/**
 *
 * @author MyMindMyBodyMySoul
 */
public class Worker implements Callable<Result> {
    private String workUrl;
    
    public Worker(String urlInput){
        this.workUrl = urlInput;
    }

    @Override
    public Result call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
    
}
