/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import static com.byteworks.utils.Constants.LIVE_KEY_DOWNLOAD_SERVICE_URL;
import static com.byteworks.utils.Constants.LIVE_TRANSACTION_URL;
import com.byteworks.utils.SecureKeyProvider;
import com.google.common.io.ByteStreams;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author SoloFoundation
 */
public class KeyExchange {
    
    private static final Logger LOG = Logger.getLogger(KeyExchange.class);
    
    public static String post(String terminalid){
        
        String responsedata = "";
                
        try {
            
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        // Trust always
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        // Trust always
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        //return null;
                        return new X509Certificate[0];
                    }
                }
            };

            // Install the all-trusting trust manager, TLS is the last SSL protocol and is used by all the CA
            SSLContext sc = SSLContext.getInstance("TLS");
            // Create empty HostnameVerifier
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };

            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            
            URL url = new URL ("https://kimono.interswitchng.com/kmw/keydownloadservice?cmd=key&terminal_id="+terminalid+"&pkmod=yBna1X0hIk%2fhzEKpfdAwgeP08JguNw6Izi3oAUaLAjd1ndVE9hM5vFhm0qrXNpJdJK%2bOc5OAYL2g%2bD%2b3LXTlhBul0wAy9rLpsFwIZ%2fbTUXk2IckJI8fMvYFZTCkNWJO12DGtpGhKMnjYND4o4%2fmFFmB%2fY%2fddk%2baMM5hrZeT8xYU%3d&pkex=AQAB&pkv=1&keyset_id=000002&der_en=1");// LIVE_KEY_DOWNLOAD_SERVICE_URL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            //con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //con.setRequestProperty("Content-Type", "text/plain; utf-8");
            //con.setRequestProperty("Accept", "application/octet-stream");
            //con.setRequestProperty("Authorization", "Bearer "+token);
            //con.setDoOutput(true);
            
            /*Map<String, String> parameters = new HashMap<>();
            parameters.put("cmd", "key");//
            parameters.put("terminal_id", terminalid);
            parameters.put("pkmod", "yBna1X0hIk%2fhzEKpfdAwgeP08JguNw6Izi3oAUaLAjd1ndVE9hM5vFhm0qrXNpJdJK%2bOc5OAYL2g%2bD%2b3LXTlhBul0wAy9rLpsFwIZ%2fbTUXk2IckJI8fMvYFZTCkNWJO12DGtpGhKMnjYND4o4%2fmFFmB%2fY%2fddk%2baMM5hrZeT8xYU%3d");
            parameters.put("pkex", "AQAB");
            parameters.put("pkv", "1");
            parameters.put("keyset_id", "000002");
            parameters.put("der_en", "1");
            
            System.out.println("prams="+ParameterStringBuilder.getParamsString(parameters));
            
            try (DataOutputStream out = new DataOutputStream(con.getOutputStream())) {
                out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                out.flush();
                
            }*/
            
            LOG.info(con.getResponseCode() + " " + con.getResponseMessage());
            
            InputStream is = con.getInputStream();
            byte[] bytes = ByteStreams.toByteArray(is);
            LOG.info("RESPONSE:"+new String(bytes));
            
	
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            responsedata = obj.toJSONString();
        }
      
        return responsedata;
    }
    
    public static void main(String args[]){
        KeyExchange pn = new KeyExchange();
        pn.post("2030LJ99");
    }
}
