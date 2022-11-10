/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import static com.byteworks.utils.Constants.LIVE_TRANSACTION_URL;
import com.byteworks.utils.OpenCellID;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class Transaction {
    
    private static final Logger LOG = Logger.getLogger(Transaction.class);
    
    public static String post(String terminalid, String merchantid, String merchantlocation, String cardseqno, String amountAuthorized, String amountOther, String aip, String atc, String cryptogram, String cryptograminfo, String cvmresults, String iad, String tvr, String terminalcap, String transactiondate, String transactiontype, String unpredictablenumber, String aid, String pan, String expirymonth, String expiryyear, String track2, String stan, String accountype, String pinblock, String ksn, String serialno, String cardlabel, String cellinfo, String currencycode, String countrycode, String version){
        
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
        
        String reference = "ISW|"+terminalid+"|"+stan+"|"+datenow.replace("-", "")+timenow.replace(":", "");
        
        /*String token = TokenRequest.post(terminalid, merchantid);
        if(token.isEmpty()){
            JSONObject obj = new JSONObject();
            obj.put("response", "05");
            obj.put("description", "Token Error");
            responsedata = obj.toJSONString();
            return responsedata;
        }*/
        
        Set <DayOfWeek> dows = EnumSet.of(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String today = sdf.format(new Date()); 
        LocalDate dt = LocalDate.parse(today);
        DayOfWeek dow = dt.getDayOfWeek();
        Boolean hit = dows.contains(dow);
        
        String account = "";
        if(hit == true){//globus
            account = "<receivingInstitutionId>506122</receivingInstitutionId>\n" + 
                      "<destinationAccountNumber>1000086209</destinationAccountNumber>\n";
        }
        else{//heritage
            account = "<receivingInstitutionId>506150</receivingInstitutionId>\n" + 
                      "<destinationAccountNumber>5100388431</destinationAccountNumber>\n";
        }
        
        String pindata = "";
        if(!pinblock.isEmpty() && !ksn.isEmpty()){
            pindata = "<pinData>\n" +
                
                              "<ksnd>605</ksnd>\n" +
                
                              "<ksn>"+ksn.substring(4)+"</ksn>\n" +
                
                              "<pinType>Dukpt</pinType>\n" +
                
                              "<pinBlock>"+pinblock+"</pinBlock>\n" +
                
                          "</pinData>\n" ;
        }
        
        String data = "<transferRequest>\n" +
                
                          "<terminalInformation>\n" +
                
                          "<batteryInformation>100</batteryInformation>\n" + //100
                
                          "<version>"+version+"</version>\n" +
                
                          "<currencyCode>"+((currencycode.isEmpty()) ? "566" : currencycode)+"</currencyCode>\n" +
                
                          "<languageInfo>EN</languageInfo>\n" +
                
                          "<merchantId>"+merchantid+"</merchantId>\n" +

                          "<merhcantLocation>"+merchantlocation+"</merhcantLocation>\n" +//ABCD STATE GOVERNMENT ON LANG
                
                          "<posConditionCode>00</posConditionCode>\n" +
                
                          "<posDataCode>510101511344101</posDataCode>\n" +
                
                          "<posEntryMode>051</posEntryMode>\n" +
                
                          "<posGeoCode>00234000000000566</posGeoCode>\n" +
                
                          "<printerStatus>0</printerStatus>\n" + //1
                
                          "<terminalId>"+terminalid+"</terminalId>\n" +
                
                          "<terminalType>22</terminalType>\n" +
                
                          "<transmissionDate>"+datenow+"T"+timenow+"</transmissionDate>\n" +
                
                          "<uniqueId>"+serialno+"</uniqueId>\n" +
                
                          "</terminalInformation>\n" +
                
                          "<cardData>\n" +
                
                             "<cardSequenceNumber>"+cardseqno+"</cardSequenceNumber>\n" +//01
                
                                "<emvData>\n" +
                
                                    "<AmountAuthorized>"+amountAuthorized+"</AmountAuthorized>\n" +//000000000001
                
                                    "<AmountOther>"+amountOther+"</AmountOther>\n" + //000000000000
                
                                    "<ApplicationInterchangeProfile>"+aip+"</ApplicationInterchangeProfile>\n" +//3900
                
                                    "<atc>"+atc+"</atc>\n" + //04A0
                
                                    "<Cryptogram>"+cryptogram+"</Cryptogram>\n" + //5F928661B1A2ECDD
                
                                    "<CryptogramInformationData>"+cryptograminfo+"</CryptogramInformationData>\n" + //80
                
                                    "<CvmResults>"+cvmresults+"</CvmResults>\n" + //440302
                
                                    "<iad>"+iad+"</iad>\n" + //0110A7C003020000E87C00000000000000FF
                
                                    "<TransactionCurrencyCode>"+((currencycode.isEmpty()) ? "566" : currencycode)+"</TransactionCurrencyCode>\n" +
                
                                    "<TerminalVerificationResult>"+tvr+"</TerminalVerificationResult>\n" + //0000008000
                
                                    "<TerminalCountryCode>"+((countrycode.isEmpty()) ? "566" : countrycode)+"</TerminalCountryCode>\n" +
                
                                    "<TerminalType>22</TerminalType>\n" +
                
                                    "<TerminalCapabilities>"+terminalcap+"</TerminalCapabilities>\n" + //E0F8C8
                
                                    "<TransactionDate>"+transactiondate+"</TransactionDate>\n" + //200806
                
                                    "<TransactionType>"+transactiontype+"</TransactionType>\n" + //00
                
                                    "<UnpredictableNumber>"+unpredictablenumber+"</UnpredictableNumber>\n" + //2E170407
                
                                    "<DedicatedFileName>"+aid+"</DedicatedFileName>\n" +  //A0000000041010
                
                                "</emvData>\n" +
                
                                "<track2>\n" +
                
                                   "<pan>"+pan+"</pan>\n" + //53994100000000005
                
                                   "<expiryMonth>"+expirymonth+"</expiryMonth>\n" + //05
                
                                   "<expiryYear>"+expiryyear+"</expiryYear>\n" + //22
                
                                   "<track2>"+track2+"</track2>\n" + //53994100000000005D2205201000000000
                
                                "</track2>\n" +
                
                          "</cardData>\n" +
                
                          "<originalTransmissionDateTime>"+datenow+"T"+timenow+"</originalTransmissionDateTime>\n" +
                
                          "<stan>"+stan+"</stan>\n" + //000018
                
                          "<fromAccount>"+accountype+"</fromAccount>\n" + //Default
                
                          "<toAccount></toAccount>\n" +
                
                          "<minorAmount>"+Long.parseLong(amountAuthorized)+"</minorAmount>\n" +
                
                          //"<surcharge>2200</surcharge>\n" + 
                
                          /*"<pinData>\n" +
                
                              "<ksnd>605</ksnd>\n" +
                
                              "<ksn>"+((ksn.isEmpty()) ? "" : ksn.substring(4))+"</ksn>\n" +
                
                              "<pinType>Dukpt</pinType>\n" +
                
                              "<pinBlock>"+pinblock+"</pinBlock>\n" +
                
                          "</pinData>\n" +*/
                
                          pindata +
                
                          "<keyLabel>000002</keyLabel>\n" +//000006
                
                          //"<receivingInstitutionId>506150</receivingInstitutionId>\n" + //639563
                
                          //"<destinationAccountNumber>5100388431</destinationAccountNumber>\n" +//5100388431 
                
                          account +
                
                          "<extendedTransactionType>6103</extendedTransactionType>\n" +
                
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
            
            URL url = new URL (LIVE_TRANSACTION_URL);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(60 * 1000);
            con.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");//charset=UTF-8
            con.setRequestProperty("Accept", "application/xml");
            //con.setRequestProperty("Authorization", "Bearer "+token);
            con.setRequestProperty("card_type", cardlabel);
            con.setRequestProperty("tran_reference", reference);
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
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(response.toString()));

                Document doc = db.parse(is);
                NodeList nodes = null;
                
                String field39 = "";
                String description = "";
                String stanx = "";
                String authId = "";
                String referenceNumber = "";
                String transactionChannelName = "";
                String wasReceive = "";
                String wasSend = "";
                
                NodeList nodes1 = doc.getElementsByTagName("response");
                NodeList nodes2 = doc.getElementsByTagName("transferResponse");
                
                if(nodes1 != null && nodes1.getLength() > 0){
                    nodes = nodes1;
                }
                else if(nodes2 != null && nodes2.getLength() > 0){
                    nodes = nodes2;
                }
                
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    try {
                        NodeList field39Node = element.getElementsByTagName("field39");
                        Element field39Element = (Element) field39Node.item(0);
                        LOG.info("field39: " + getCharacterDataFromElement(field39Element));
                        field39 = getCharacterDataFromElement(field39Element);
                    } catch (Exception e) {
                        try {
                            NodeList field39Node = element.getElementsByTagName("responseCode");
                            Element field39Element = (Element) field39Node.item(0);
                            LOG.info("responseCode: " + getCharacterDataFromElement(field39Element));
                            field39 = getCharacterDataFromElement(field39Element);
                        } catch (Exception ex) {
                        }
                    }

                    try {
                        NodeList descriptionNode = element.getElementsByTagName("description");
                        Element descriptionElement = (Element) descriptionNode.item(0);
                        LOG.info("description: " + getCharacterDataFromElement(descriptionElement));
                        description = getCharacterDataFromElement(descriptionElement);
                    } catch (Exception e) {
                        try {
                            NodeList descriptionNode = element.getElementsByTagName("responseMessage");
                            Element descriptionElement = (Element) descriptionNode.item(0);
                            LOG.info("responseMessage: " + getCharacterDataFromElement(descriptionElement));
                            description = getCharacterDataFromElement(descriptionElement);
                        } catch (Exception ex) {
                        }
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
                //obj.put("reference", reference);
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
            obj.put("description", ""+ex.getMessage());
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
        Transaction pn = new Transaction();
        //pn.post("Debit Card","765432345432", "876543", "3765.00", "2ISW0001","2ISW1234567TEST", "KAZEEM AKINBOLA", "08034893236", "MOTORCYCLE DAILY TICKET", "MDT0876", "NIGER STATE COLLECTION", "Bosso", "MAK", "Garatu");
    }
}
