/*
 * Objects of this class will hold a url string and a ciphersuite string
 * which was used to do the tls handshake
 */
package swp2.crawlertest;

/**
 *
 * @author MyMindMyBodyMySoul
 */
public class Result {
    private String urlString, cipherSuite;
    
    public Result(String urlInput,String cipherSuiteInput){
        this.urlString = urlInput;
        this.cipherSuite = cipherSuiteInput;
    }
    
    @Override
    public String toString(){
        return this.urlString +" Cipher Suite used: "+this.cipherSuite+ "\n";
    }
    
}
