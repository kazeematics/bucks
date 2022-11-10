/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import static com.byteworks.utils.Constants.LIVE_NOTIFICATION_URL;
import static com.byteworks.utils.Constants.LIVE_TRANSACTION_URL;
import com.byteworks.utils.OpenCellID;
import com.byteworks.utils.SecureKeyProvider;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import org.json.simple.JSONValue;
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
public class Notification {
    
    private static final Logger LOG = Logger.getLogger(Notification.class);
    
    public static String post(String merchantaddress, String terminalId, String merchantid, String datetime, String transactiontype, String transactionstatus, String card, String aid, String pan, String expirydate, String client, String rrn, String accounttype, String amount, String responsecode, String message, String serialno, String cellinfo, String version){
        
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
        
        LOG.info("cellinfo = " + cellinfo);
        String siminfo [] = cellinfo.split("\\|", -1);
        String geopoint = OpenCellID.getLocation(siminfo[0], siminfo[1], siminfo[2], siminfo[3]);
        LOG.info("geopoint = " + geopoint);
        
        /*String token = TokenRequest.post(terminalid, merchantid);
        if(token.isEmpty()){
            JSONObject obj = new JSONObject();
            obj.put("response", "05");
            obj.put("description", "Token Error");
            responsedata = obj.toJSONString();
            return responsedata;
        }*/
        
        String data = "<transferRequest>\n" +
                        
                         "<terminalInformation>\n" +
                               
                             "<batteryInformation>100</batteryInformation>\n" +
                
                             "<version>"+version+"</version>\n" +
           
                             "<currencyCode>566</currencyCode>\n" +
                             
                             "<languageInfo>EN</languageInfo>\n" +
                             
                             "<merchantId>"+merchantid+"</merchantId>\n" +
                             
                             "<merhcantLocation>"+merchantaddress+"</merhcantLocation>\n" +
                             
                             "<posConditionCode>00</posConditionCode>\n" +
                             
                             "<posDataCode>510101511344101</posDataCode>\n" +
                             
                             "<posEntryMode>051</posEntryMode>\n" +
         
                             "<posGeoCode>00234000000000566</posGeoCode>\n" +
 
                             "<printerStatus>1</printerStatus>\n" +
 
                             "<terminalId>"+terminalId+"</terminalId>\n" +

                             "<terminalType>22</terminalType>\n" +
 
                             "<transmissionDate>"+datenow+"T"+timenow+"</transmissionDate>\n" +
                             
                             "<uniqueId>"+serialno+"</uniqueId>\n" +

                         "</terminalInformation>\n" +
                            
                             "<merchantaddress>"+merchantaddress+"</merchantaddress>\n" +

                             "<terminalId>"+terminalId+"</terminalId>\n" +

                             "<datetime>"+datetime+"</datetime>\n" +
         
                             "<transactiontype>"+transactiontype+"</transactiontype>\n" +
                             
                             "<transactionstatus>"+transactionstatus+"</transactionstatus>\n" +
 
                             "<card>"+card+"</card>\n" +
              
                             "<aid>"+aid+"</aid>\n" +
    
                             "<pan>"+pan+"</pan>\n" +
           
                             "<expirydate>"+expirydate+"</expirydate>\n" +

                             "<client>"+client+"</client>\n" +
               
                             "<rrn>"+rrn+"</rrn>\n" +
            
                             "<accounttype>"+accounttype+"</accounttype>\n" +

                             "<amount>"+amount.replace(",", "").replace(".", "")+"</amount>\n" +
                
                             "<responsecode>"+responsecode+"</responsecode>\n" +

                             "<message>"+message+"</message>\n" +
                
                      "</transferRequest>";

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
            
            URL url = new URL (LIVE_NOTIFICATION_URL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            //HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            con.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");//charset=UTF-8
            con.setRequestProperty("Accept", "application/json");
            //con.setRequestProperty("Authorization", "Bearer "+token);
            
            if(!geopoint.isEmpty()){
               con.setRequestProperty("geo_location", geopoint);//"6.545562,3.406048");//geopoint);
            }
            
            //con.setUseCaches(false);
            //con.setDoInput(true);
            
            con.setDoOutput(true);
            
            /*for (Map.Entry<String, List<String>> entries : con.getRequestProperties().entrySet()) {
                String values = "";
                for (String value : entries.getValue()) {
                    values += value + ",";
                }
                LOG.info("Request:"+entries.getKey() + " - " + values);
            }*/
            
            try (OutputStream os = con.getOutputStream()) {
                //byte[] input = data.getBytes("utf-8");
                //os.write(input, 0, input.length);
                os.write(data.getBytes("UTF-8"));
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
                
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response.toString());    
            
                String status = (String) jsonObject.get("status");
                String description = (String) jsonObject.get("message");
                String responseCode = "";
                if(status.equals("success")){
                    responseCode = "00";
                }else{
                    responseCode = "05";
                }
                
                JSONObject obj = new JSONObject();
                obj.put("response", responseCode);
                obj.put("description", description);
                
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
        Notification pn = new Notification();
        //pn.post("Debit Card","765432345432", "876543", "3765.00", "2ISW0001","2ISW1234567TEST", "KAZEEM AKINBOLA", "08034893236", "MOTORCYCLE DAILY TICKET", "MDT0876", "NIGER STATE COLLECTION", "Bosso", "MAK", "Garatu");
    }
}
