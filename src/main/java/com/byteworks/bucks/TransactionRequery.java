/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import static com.byteworks.utils.Constants.LIVE_TRANSACTION_URL;
import com.byteworks.utils.SecureKeyProvider;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
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
public class TransactionRequery {
    
    private static final Logger LOG = Logger.getLogger(TransactionRequery.class);
    
    public static String post(String terminalid, String merchantid, String stan, String amount){
        
        String responsedata = "";
        
        SecureKeyProvider sp = new SecureKeyProvider();
        String logid = sp.get(9);
        String uniqueid = sp.get(8);
        String acustomerreference = sp.get(10);
        
        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
        DateFor.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String datenow = DateFor.format(date);
        
        SimpleDateFormat TimeFor = new SimpleDateFormat("HH:mm:ss");
        TimeFor.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        String timenow = TimeFor.format(date);
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        /*String token = TokenRequest.post(terminalid, merchantid);
        if(token.isEmpty()){
            JSONObject obj = new JSONObject();
            obj.put("response", "05");
            obj.put("description", "Token Error");
            responsedata = obj.toJSONString();
            return responsedata;
        }*/
        
        String data = "<transactionRequeryRequest>\n" +
                
                           "<applicationType>gTransfer</applicationType>\n" +
                
                           "<originalTransStan>"+stan+"</originalTransStan>\n" + //000102
                
                           "<originalMinorAmount>"+amount+"</originalMinorAmount>\n" + //1000
                
                           "<terminalInformation>\n" +
                
                                "<terminalId>"+terminalid+"</terminalId>\n" + //2ISW0001
                
                                "<merchantId>"+merchantid+"</merchantId>\n" + //2ISW1234567TEST
                
                                "<transmissionDate>"+datenow+"T"+timenow+"</transmissionDate>\n" + //2020-01-31T18:16:02
                
                           "</terminalInformation>\n" +
                
                      "</transactionRequeryRequest>";

        LOG.info("REQUEST : "+data);
        
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
            
            URL url = new URL (LIVE_TRANSACTION_URL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            con.setRequestProperty("Content-Type", "application/xml; utf-8");
            con.setRequestProperty("Accept", "application/xml");
            //con.setRequestProperty("Authorization", "Bearer "+token);
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
                LOG.info("REPONSE : "+response.toString());
            }
            
            if (!response.toString().isEmpty()) {
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(response.toString()));

                Document doc = db.parse(is);
                
                String field39 = "";
                String description = "";
                String stanx = "";
                String authId = "";
                String referenceNumber = "";
                String transactionChannelName = "";
                String wasReceive = "";
                String wasSend = "";
                
                NodeList nodes = doc.getElementsByTagName("response");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    try {
                       NodeList field39Node = element.getElementsByTagName("field39");
                       Element field39Element = (Element) field39Node.item(0);
                       LOG.info("field39: " + getCharacterDataFromElement(field39Element));
                       field39 = getCharacterDataFromElement(field39Element);
                    } catch (Exception ex) {
                    }

                    try {
                       NodeList descriptionNode = element.getElementsByTagName("description");
                       Element descriptionElement = (Element) descriptionNode.item(0);
                       LOG.info("description: " + getCharacterDataFromElement(descriptionElement));
                       description = getCharacterDataFromElement(descriptionElement);
                    } catch (Exception ex) {
                    }

                    try {
                       NodeList stanNode = element.getElementsByTagName("stan");
                       Element stanElement = (Element) stanNode.item(0);
                       LOG.info("stan: " + getCharacterDataFromElement(stanElement));
                       stanx = getCharacterDataFromElement(stanElement);
                    } catch (Exception ex) {
                    }

                    try {
                        NodeList authIdNode = element.getElementsByTagName("authId");
                        Element authIdElement = (Element) authIdNode.item(0);
                        LOG.info("authId: " + getCharacterDataFromElement(authIdElement));
                        authId = getCharacterDataFromElement(authIdElement);
                    } catch (Exception ex) {
                    }
                    
                    try {
                        NodeList referenceNumberNode = element.getElementsByTagName("referenceNumber");
                        Element referenceNumberElement = (Element) referenceNumberNode.item(0);
                        LOG.info("referenceNumber: " + getCharacterDataFromElement(referenceNumberElement));
                        referenceNumber = getCharacterDataFromElement(referenceNumberElement);
                    } catch (Exception ex) {
                    }
                    
                    try {
                        NodeList transactionChannelNameNode = element.getElementsByTagName("transactionChannelName");
                        Element transactionChannelNameElement = (Element) transactionChannelNameNode.item(0);
                        LOG.info("transactionChannelName: " + getCharacterDataFromElement(transactionChannelNameElement));
                        transactionChannelName = getCharacterDataFromElement(transactionChannelNameElement);
                    } catch (Exception ex) {
                    }

                    try {
                        NodeList wasReceiveNode = element.getElementsByTagName("wasReceive");
                        Element wasReceiveElement = (Element) wasReceiveNode.item(0);
                        LOG.info("wasReceive: " + getCharacterDataFromElement(wasReceiveElement));
                        wasReceive = getCharacterDataFromElement(wasReceiveElement);
                    } catch (Exception ex) {
                    }

                    try {
                        NodeList wasSendNode = element.getElementsByTagName("wasSend");
                        Element wasSendElement = (Element) wasSendNode.item(0);
                        LOG.info("wasSend: " + getCharacterDataFromElement(wasSendElement));
                        wasSend = getCharacterDataFromElement(wasSendElement);
                    } catch (Exception ex) {
                    }
                }
                JSONObject obj = new JSONObject();
                obj.put("response", field39);
                obj.put("description", description);
                if(!stanx.isEmpty()){
                   obj.put("stan", stanx);
                }
                if(!referenceNumber.isEmpty()){
                   obj.put("refno", referenceNumber);
                }
                if(!authId.isEmpty()){
                    obj.put("authid", authId);
                }
                responsedata = obj.toJSONString();
                
            }
	
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            responsedata = obj.toJSONString();
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
        TransactionRequery pn = new TransactionRequery();
        pn.post("2ISW0001", "2ISW1234567TEST", "502131", "800000");
    }
}
