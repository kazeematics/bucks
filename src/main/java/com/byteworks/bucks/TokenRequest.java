/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import static com.byteworks.utils.Constants.LIVE_TOKEN_URL;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
public class TokenRequest {
    
    private static final Logger LOG = Logger.getLogger(TokenRequest.class);
    
    public static String post(String terminalid, String merchantid){
        
        String responsedata = "";
        String responseCode = "";
        String token = "";
        
        String data = "<tokenPassportRequest>\n" +
                
                         "<terminalInformation>\n" +
                
                         "<batteryInformation/>\n" +
                
                         "<cellStationId/>\n" +
                
                         "<currencyCode/>\n" +
                
                         "<languageInfo/>\n" +
                
                         "<merchantId>"+merchantid+"</merchantId>\n" +//2ISW1234567TEST
                
                         "<merhcantLocation/>\n" +
                
                         "<posConditionCode/>\n" +
                
                         "<posDataCode/>\n" +
                
                         "<posEntryMode/>\n" +
                
                         "<posGeoCode/>\n" +
                
                         "<printerStatus/>\n" +
                
                         "<terminalId>"+terminalid+"</terminalId>\n" + //2ISW0001
                
                         "<terminalType/>\n" +
                
                         "<transmissionDate/>\n" +
                
                         "<uniqueId/>\n" +
                
                         "</terminalInformation>\n" +
                
                      "</tokenPassportRequest>";
        
        LOG.info("TOKEN REQUEST : "+data);
        
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
            
            URL url = new URL (LIVE_TOKEN_URL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            con.setRequestProperty("Content-Type", "application/xml; utf-8");
            con.setRequestProperty("Accept", "application/xml");
            con.setDoOutput(true);
            
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                LOG.info("TOKEN REPONSE : "+response.toString());
            }
            
            if (!response.toString().isEmpty()) {
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(response.toString()));

                Document doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("tokenPassportResponse");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    NodeList name = element.getElementsByTagName("responseCode");
                    Element line = (Element) name.item(0);
                    //LOG.info("responseCode: " + getCharacterDataFromElement(line));
                    responseCode = getCharacterDataFromElement(line);

                    NodeList title = element.getElementsByTagName("token");
                    line = (Element) title.item(0);
                    LOG.info("token: " + getCharacterDataFromElement(line));
                    token = getCharacterDataFromElement(line);
                }
                if(!responseCode.isEmpty() && responseCode.equals("00")){
                    responsedata = token;//new String(baosx.toByteArray());
                }
            }
	
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
      
        return responsedata;
    }
    
    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
    
    public static void main(String args[]){
        TokenRequest pn = new TokenRequest();
        pn.post("2ISW0001","2ISW1234567TEST");
    }
}
