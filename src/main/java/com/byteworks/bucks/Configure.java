/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import static com.byteworks.utils.Constants.LIVE_CONFIGURE_URL;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author SoloFoundation
 */
public class Configure {
    
    private static final Logger LOG = Logger.getLogger(Configure.class);
    
    public static String post(String version, String serialno){
        
        String responsedata = "";
        
        JSONObject obj = new JSONObject();
        obj.put("current_version", version);
        obj.put("serial_no", serialno);

        String data = obj.toJSONString();        

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
            
            URL url = new URL (LIVE_CONFIGURE_URL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            //con.setRequestProperty("Authorization", "Bearer "+token);
            con.setDoOutput(true);
            
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
                
                JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                String responseCode = jsonObject.getAsJsonPrimitive("responseCode").getAsString();
                String responseMessage = jsonObject.getAsJsonPrimitive("responseMessage").getAsString();
                boolean forceConfig = jsonObject.getAsJsonObject("data").get("forceConfig").getAsBoolean();
                String agentLocation = jsonObject.getAsJsonObject("data").getAsJsonObject("terminalConfiguration").get("agentLocation").getAsString();
                //String agentName = jsonObject.getAsJsonObject("data").getAsJsonObject("terminalConfiguration").get("agentName").getAsString();
                String agentName = jsonObject.getAsJsonObject("data").getAsJsonObject("terminalConfiguration").get("agentBusinessName").getAsString();
                String agentId = jsonObject.getAsJsonObject("data").getAsJsonObject("terminalConfiguration").get("agentId").getAsString();
                String terminalId = jsonObject.getAsJsonObject("data").getAsJsonObject("terminalConfiguration").get("terminalId").getAsString();
                String nibbsMerchantId = jsonObject.getAsJsonObject("data").getAsJsonObject("terminalConfiguration").get("nibbsMerchantId").getAsString();
                /*System.out.println("responseCode:"+responseCode);
                System.out.println("forceConfig:"+forceConfig);
                System.out.println("responseMessage:"+responseMessage);
                System.out.println("agentLocation:"+agentLocation);
                System.out.println("agentName:"+agentName);
                System.out.println("agentId:"+agentId);*/
                
                if(forceConfig){
                   KeyExchange pn = new KeyExchange();
                   pn.post(terminalId); 
                }
                
                
                JSONObject jobj = new JSONObject();
                jobj.put("response", responseCode);
                jobj.put("description", responseMessage);
                jobj.put("forceConfig", ""+forceConfig);
                jobj.put("agentLocation", agentLocation);
                jobj.put("agentName", agentName);
                jobj.put("agentId", agentId);
                jobj.put("terminalId", terminalId);
                jobj.put("merchantId", nibbsMerchantId);
                
                responsedata = jobj.toJSONString();
                
                //System.out.println("responsedata:"+responsedata);
                
            }
	
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            JSONObject objx = new JSONObject();
            objx.put("response", "96");
            objx.put("description", ex.getMessage());
            responsedata = objx.toJSONString();
        }
      
        return responsedata;
    }
    
    
    public static void main(String args[]){
        Configure pn = new Configure();
        
        pn.post("1.0","3D941330");
        
    }
}
