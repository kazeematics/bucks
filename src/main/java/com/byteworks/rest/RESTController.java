/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.rest;


import com.byteworks.bucks.AESCrypter;
import com.byteworks.bucks.Configure;
import com.byteworks.bucks.NIBSS;
import com.byteworks.bucks.Notification;
import com.byteworks.bucks.Transaction;
import com.byteworks.bucks.TransactionRequery;
import com.byteworks.bucks.Transfer;
import com.byteworks.model.epms.Epms;
import com.byteworks.model.terminal.Terminal;
import static com.byteworks.utils.Constants.CORE_URL;
import static com.byteworks.utils.Constants.LIVE_HOST;
import static com.byteworks.utils.Constants.LIVE_KEY1;
import static com.byteworks.utils.Constants.LIVE_KEY2;
import static com.byteworks.utils.Constants.LIVE_PORT;
import static com.byteworks.utils.Constants.LIVE_PROTOCOL;
//import static com.byteworks.utils.Constants.LIVE_URL;
import static com.byteworks.utils.Constants.LOCALHOST_URL;
import static com.byteworks.utils.Constants.TEST_HOST;
import static com.byteworks.utils.Constants.TEST_KEY1;
import static com.byteworks.utils.Constants.TEST_KEY2;
import static com.byteworks.utils.Constants.TEST_PORT;
import static com.byteworks.utils.Constants.TEST_PROTOCOL;
import static com.byteworks.utils.Constants.TRAN_TYPE_BALANCE;
import com.byteworks.utils.ResponseCodes;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author SoloFoundation
 */
@Controller
@RequestMapping("api")
public class RESTController {
    
    private static final Logger LOG = Logger.getLogger(RESTController.class);
    
    @ResponseBody
    @RequestMapping(value = "/pos-notification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String notification(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
           
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String merchantaddress = (String) jsonObject.get("merchantaddress"); 
            String terminalId = (String) jsonObject.get("terminalId");
            String datetime = (String) jsonObject.get("datetime");
            String transactiontype = (String) jsonObject.get("transactiontype");
            String transactionstatus = (String) jsonObject.get("transactionstatus");
            String card = (String) jsonObject.get("card");
            String aid = (String) jsonObject.get("aid");
            String pan = (String) jsonObject.get("pan");
            String expirydate = (String) jsonObject.get("expirydate");
            String client = (String) jsonObject.get("client");
            String rrn = (String) jsonObject.get("rrn");
            String accounttype = (String) jsonObject.get("accounttype");
            String amount = (String) jsonObject.get("amount");
            String responsecode = (String) jsonObject.get("responsecode");
            String message = (String) jsonObject.get("message");
            String merchantid = (String) jsonObject.get("merchantid");
            String serialno = (String) jsonObject.get("serialno");
            String cellinfo = (jsonObject.get("cellinfo") == null) ? "" : (String)jsonObject.get("cellinfo");//String cellinfo = (String) jsonObject.get("cellinfo");
            String version = (jsonObject.get("version") == null) ? "" : (String)jsonObject.get("version");
            
            String ussdresponse = Notification.post(merchantaddress, terminalId, merchantid, datetime, transactiontype, transactionstatus, card, aid, pan, expirydate, client, rrn, accounttype, amount, responsecode, message, serialno, cellinfo, version);
            
            if(ussdresponse.isEmpty()){
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            }
            else{
                response = ussdresponse;
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/transfer-notification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String notifytransfer(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
           
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String merchantaddress = (String) jsonObject.get("merchantaddress"); 
            String terminalId = (String) jsonObject.get("terminalId");
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            String serialno = (String) jsonObject.get("serialno");
            String bankcode = (String) jsonObject.get("bankcode");
            String accountNumber = (String) jsonObject.get("accountNumber");
            String accountName = (String) jsonObject.get("accountName");
            String phoneNumber = (String) jsonObject.get("phoneNumber");
            String requestId = (String) jsonObject.get("requestId");
            String pin = (String) jsonObject.get("pin");
            String cellinfo = (jsonObject.get("cellinfo") == null) ? "" : (String)jsonObject.get("cellinfo");
            String version = (jsonObject.get("version") == null) ? "" : (String)jsonObject.get("version");
            
            String ussdresponse = Transfer.post(merchantaddress, terminalId, merchantid, amount, serialno, bankcode, accountNumber, accountName, phoneNumber, requestId, pin, cellinfo, version);
        
            if(ussdresponse.isEmpty()){
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            }
            else{
                response = ussdresponse;
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    /*public String generateReference(@RequestBody String transaction) {

        String response = null;

        LOG.info("Request => " + transaction);

        try {

            StringBuilder responseBuilder = new StringBuilder();

            URL url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/reset");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(120 * 1000);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject objreset = new JSONObject();
            objreset.put("ux", "pos-integrator@3GeePay.com");
            String reset = objreset.toJSONString();
            
            LOG.info("Reset Request = " + reset);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = reset.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }

            response = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                if (responseBuilder != null) {
                    response = responseBuilder.toString();
                    LOG.info("Reset Response = " + response);
                }
            }

            if (response == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
                return response;
            } else {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response);

                String iv = (String) jsonObject.get("iv");
                String key = (String) jsonObject.get("key");

                responseBuilder = new StringBuilder();

                url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/sys-login");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(120 * 1000);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject objlogin = new JSONObject();
                objlogin.put("ux", "pos-integrator@3GeePay.com");
                objlogin.put("iv", iv);
                objlogin.put("key", key);
                String login = objlogin.toJSONString();
                
                LOG.info("Login Request = " + login);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = login.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                }

                response = null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    if (responseBuilder != null) {
                        response = responseBuilder.toString();
                        LOG.info("Login Response = " + response);
                    }
                }

                if (response == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "99");
                    obj.put("description", "No response from server");
                    response = obj.toJSONString();
                    return response;
                } else {
                    jsonObject = (JSONObject) JSONValue.parse(response);
                    String authorization = (String) jsonObject.get("Authorization");

                    responseBuilder = new StringBuilder();

                    url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/pos-notification");
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(30 * 1000);
                    con.setReadTimeout(120 * 1000);
                    con.setRequestProperty("Authorization", authorization);
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    AESCrypter crypta = new AESCrypter(key,iv);
                    String notify = crypta.encrypt(transaction);
                    
                    LOG.info("Encrypted Push Request = " + notify);

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = notify.getBytes("utf-8");
                        os.write(input, 0, input.length);
                        os.flush();
                        os.close();
                    }

                    response = null;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        if (responseBuilder != null) {
                            response = responseBuilder.toString();
                            LOG.info("Push Response = " + response);
                        }
                    }

                    if (response == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "No response from server");
                        response = obj.toJSONString();
                        return response;
                    } else {
                        //String dpush = crypta.decrypt(response);
                        //LOG.info("Decrypted Push Response = " + dpush);
                        jsonObject = (JSONObject) JSONValue.parse(response);
                        String teminalid = (String) jsonObject.get("terminalId");
                        String rrn = (String) jsonObject.get("rrn");
                        JSONObject obj = new JSONObject();
                        obj.put("response", "00");
                        obj.put("description", "Successful");
                        response = obj.toJSONString();
                    }

                }
                           
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = ", ex);
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }*/
    
    @ResponseBody
    @RequestMapping(value = "/pos-transaction", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String transaction(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
           
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String merchantid = (String) jsonObject.get("merchantid");
            String merchantlocation = (String) jsonObject.get("merchantlocation");
            String cardseqno = (String) jsonObject.get("cardseqno");
            
            String amountAuthorized = (String) jsonObject.get("amountAuthorized");
            String amountOther = (String) jsonObject.get("amountOther");
            String aip = (String) jsonObject.get("aip");
            String atc = (String) jsonObject.get("atc");
            String cryptogram = (String) jsonObject.get("cryptogram");
            String cryptograminfo = (String) jsonObject.get("cryptograminfo");
            String cvmresults = (String) jsonObject.get("cvmresults");
            String iad = (String) jsonObject.get("iad");
            String tvr = (String) jsonObject.get("tvr");
            String terminalcap = (String) jsonObject.get("terminalcap");
            String transactiondate = (String) jsonObject.get("transactiondate");
            String transactiontype = (String) jsonObject.get("transactiontype");
            String unpredictablenumber = (String) jsonObject.get("unpredictablenumber");
            String aid = (String) jsonObject.get("aid");
            
            String pan = (String) jsonObject.get("pan");
            String expirymonth = (String) jsonObject.get("expirymonth");
            String expiryyear = (String) jsonObject.get("expiryyear");
            String track2 = (String) jsonObject.get("track2");
            String stan = (String) jsonObject.get("stan");
            String accountype = (String) jsonObject.get("accountype");
            String pinblock = (String) jsonObject.get("pinblock");
            String ksn = (String) jsonObject.get("ksn");
            String serialno = (String) jsonObject.get("serialno");
            String cardlabel = (String) jsonObject.get("cardlabel");
            String cellinfo = (String) jsonObject.get("cellinfo");
            String currencycode = (jsonObject.get("currencycode") == null) ? "" : (String)jsonObject.get("currencycode"); //(String) jsonObject.get("currencycode");
            String countrycode = (jsonObject.get("countrycode") == null) ? "" : (String)jsonObject.get("countrycode");//(String) jsonObject.get("countrycode");
            String version = (jsonObject.get("version") == null) ? "" : (String)jsonObject.get("version");
            
            String ussdresponse = Transaction.post(terminalid, merchantid, merchantlocation, cardseqno, amountAuthorized, amountOther, aip, atc, cryptogram, cryptograminfo, cvmresults, iad, tvr, terminalcap, transactiondate, transactiontype, unpredictablenumber, aid, pan, expirymonth, expiryyear, track2, stan, accountype, pinblock, ksn, serialno, cardlabel, cellinfo, currencycode, countrycode, version);
            
            if(ussdresponse.isEmpty()){
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            }
            else{
                response = ussdresponse;
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/pos-requery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String requery(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
           
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String merchantid = (String) jsonObject.get("merchantid");
            String stan = (String) jsonObject.get("stan");
            String amount = (String) jsonObject.get("amount");
            
            
            String ussdresponse = TransactionRequery.post(terminalid, merchantid, stan, amount);
            
            if(ussdresponse.isEmpty()){
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            }
            else{
                response = ussdresponse;
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/pos-configure", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String configure(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
           
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String version = (String) jsonObject.get("version"); 
            String serialno = (String) jsonObject.get("serialno");
            
            String ussdresponse = Configure.post(version, serialno);
            
            if(ussdresponse.isEmpty()){
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            }
            else{
                response = ussdresponse;
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/wallet-balance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String balance(@RequestBody String transaction) {

        String response = null;

        LOG.info("Request => " + transaction);

        try {

            StringBuilder responseBuilder = new StringBuilder();

            URL url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/reset");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(120 * 1000);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject objreset = new JSONObject();
            objreset.put("ux", "pos-integrator@3GeePay.com");
            String reset = objreset.toJSONString();
            
            LOG.info("Reset Request = " + reset);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = reset.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }

            response = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                if (responseBuilder != null) {
                    response = responseBuilder.toString();
                    LOG.info("Reset Response = " + response);
                }
            }

            if (response == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
                return response;
            } else {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response);

                String iv = (String) jsonObject.get("iv");
                String key = (String) jsonObject.get("key");

                responseBuilder = new StringBuilder();

                url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/sys-login");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(120 * 1000);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject objlogin = new JSONObject();
                objlogin.put("ux", "pos-integrator@3GeePay.com");
                objlogin.put("iv", iv);
                objlogin.put("key", key);
                String login = objlogin.toJSONString();
                
                LOG.info("Login Request = " + login);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = login.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                }

                response = null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    if (responseBuilder != null) {
                        response = responseBuilder.toString();
                        LOG.info("Login Response = " + response);
                    }
                }

                if (response == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "99");
                    obj.put("description", "No response from server");
                    response = obj.toJSONString();
                    return response;
                } else {
                    jsonObject = (JSONObject) JSONValue.parse(response);
                    String authorization = (String) jsonObject.get("Authorization");

                    responseBuilder = new StringBuilder();

                    url = new URL(CORE_URL + "/3geepay/api/processor/balance-enquiry-ext");
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(30 * 1000);
                    con.setReadTimeout(120 * 1000);
                    con.setRequestProperty("Authorization", authorization);
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    AESCrypter crypta = new AESCrypter(key,iv);
                    String notify = crypta.encrypt(transaction);
                    
                    LOG.info("Encrypted Push Request = " + notify);

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = notify.getBytes("utf-8");
                        os.write(input, 0, input.length);
                        os.flush();
                        os.close();
                    }

                    response = null;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        if (responseBuilder != null) {
                            response = responseBuilder.toString();
                            LOG.info("Push Response = " + response);
                        }
                    }

                    if (response == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "No response from server");
                        response = obj.toJSONString();
                        return response;
                    } else {
                        //String dpush = crypta.decrypt(response);
                        //LOG.info("Decrypted Push Response = " + dpush);
                        jsonObject = (JSONObject) JSONValue.parse(response);
                        String availableBalance = (String) jsonObject.get("availableBalance");
                        String ledgerBalance = (String) jsonObject.get("ledgerBalance");
                        JSONObject obj = new JSONObject();
                        obj.put("response", "00");
                        obj.put("description", "Successful");
                        obj.put("availableBalance", availableBalance);
                        obj.put("ledgerBalance", ledgerBalance);
                        response = obj.toJSONString();
                    }

                }
                           
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = ", ex);
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/wallet-transfer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String transfer(@RequestBody String transaction) {

        String response = null;

        LOG.info("Request => " + transaction);

        try {

            StringBuilder responseBuilder = new StringBuilder();

            URL url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/reset");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(120 * 1000);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject objreset = new JSONObject();
            objreset.put("ux", "pos-integrator@3GeePay.com");
            String reset = objreset.toJSONString();
            
            LOG.info("Reset Request = " + reset);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = reset.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }

            response = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                if (responseBuilder != null) {
                    response = responseBuilder.toString();
                    LOG.info("Reset Response = " + response);
                }
            }

            if (response == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
                return response;
            } else {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response);

                String iv = (String) jsonObject.get("iv");
                String key = (String) jsonObject.get("key");

                responseBuilder = new StringBuilder();

                url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/sys-login");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(120 * 1000);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject objlogin = new JSONObject();
                objlogin.put("ux", "pos-integrator@3GeePay.com");
                objlogin.put("iv", iv);
                objlogin.put("key", key);
                String login = objlogin.toJSONString();
                
                LOG.info("Login Request = " + login);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = login.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                }

                response = null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    if (responseBuilder != null) {
                        response = responseBuilder.toString();
                        LOG.info("Login Response = " + response);
                    }
                }

                if (response == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "99");
                    obj.put("description", "No response from server");
                    response = obj.toJSONString();
                    return response;
                } else {
                    jsonObject = (JSONObject) JSONValue.parse(response);
                    String authorization = (String) jsonObject.get("Authorization");

                    responseBuilder = new StringBuilder();

                    url = new URL(CORE_URL + "/3geepay/api/processor/wallet-2-bank-ext");
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(30 * 1000);
                    con.setReadTimeout(120 * 1000);
                    con.setRequestProperty("Authorization", authorization);
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    AESCrypter crypta = new AESCrypter(key,iv);
                    String notify = crypta.encrypt(transaction);
                    
                    LOG.info("Encrypted Push Request = " + notify);

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = notify.getBytes("utf-8");
                        os.write(input, 0, input.length);
                        os.flush();
                        os.close();
                    }

                    response = null;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        if (responseBuilder != null) {
                            response = responseBuilder.toString();
                            LOG.info("Push Response = " + response);
                        }
                    }

                    if (response == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "No response from server");
                        response = obj.toJSONString();
                        return response;
                    } else {
                        //String dpush = crypta.decrypt(response);
                        //LOG.info("Decrypted Push Response = " + dpush);
                        jsonObject = (JSONObject) JSONValue.parse(response);
                        String requestId = (String) jsonObject.get("requestId");
                        JSONObject obj = new JSONObject();
                        obj.put("response", "00");
                        obj.put("description", "Successful");
                        obj.put("requestId", requestId);
                        response = obj.toJSONString();
                    }

                }
                           
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = ", ex);
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/name-enquiry", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String nameEnquiry(@RequestBody String transaction) {

        String response = null;

        LOG.info("Request => " + transaction);

        try {

            StringBuilder responseBuilder = new StringBuilder();

            URL url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/reset");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(120 * 1000);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject objreset = new JSONObject();
            objreset.put("ux", "pos-integrator@3GeePay.com");
            String reset = objreset.toJSONString();
            
            LOG.info("Reset Request = " + reset);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = reset.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }

            response = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                if (responseBuilder != null) {
                    response = responseBuilder.toString();
                    LOG.info("Reset Response = " + response);
                }
            }

            if (response == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
                return response;
            } else {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response);

                String iv = (String) jsonObject.get("iv");
                String key = (String) jsonObject.get("key");

                responseBuilder = new StringBuilder();

                url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/sys-login");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(120 * 1000);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject objlogin = new JSONObject();
                objlogin.put("ux", "pos-integrator@3GeePay.com");
                objlogin.put("iv", iv);
                objlogin.put("key", key);
                String login = objlogin.toJSONString();
                
                LOG.info("Login Request = " + login);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = login.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                }

                response = null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    if (responseBuilder != null) {
                        response = responseBuilder.toString();
                        LOG.info("Login Response = " + response);
                    }
                }

                if (response == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "99");
                    obj.put("description", "No response from server");
                    response = obj.toJSONString();
                    return response;
                } else {
                    jsonObject = (JSONObject) JSONValue.parse(response);
                    String authorization = (String) jsonObject.get("Authorization");

                    responseBuilder = new StringBuilder();

                    url = new URL(CORE_URL + "/3geepay/api/processor/account-name-enquiry-ext");
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(30 * 1000);
                    con.setReadTimeout(120 * 1000);
                    con.setRequestProperty("Authorization", authorization);
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    AESCrypter crypta = new AESCrypter(key,iv);
                    String notify = crypta.encrypt(transaction);
                    
                    LOG.info("Encrypted Push Request = " + notify);

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = notify.getBytes("utf-8");
                        os.write(input, 0, input.length);
                        os.flush();
                        os.close();
                    }

                    response = null;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        if (responseBuilder != null) {
                            response = responseBuilder.toString();
                            LOG.info("Push Response = " + response);
                        }
                    }

                    if (response == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "No response from server");
                        response = obj.toJSONString();
                        return response;
                    } else {
                        //String dpush = crypta.decrypt(response);
                        //LOG.info("Decrypted Push Response = " + dpush);
                        jsonObject = (JSONObject) JSONValue.parse(response);
                        String requestId = (String) jsonObject.get("requestId");
                        String accountName = (String) jsonObject.get("accountName");
                        JSONObject obj = new JSONObject();
                        obj.put("response", "00");
                        obj.put("description", "Successful");
                        obj.put("accountName", accountName);
                        obj.put("requestId", requestId);
                        response = obj.toJSONString();
                    }

                }
                           
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = ", ex);
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/list-banks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String listbanks() {

        String response = null;

        //LOG.info("Request => " + transaction);

        try {

            StringBuilder responseBuilder = new StringBuilder();

            URL url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/reset");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30 * 1000);
            con.setReadTimeout(120 * 1000);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JSONObject objreset = new JSONObject();
            objreset.put("ux", "pos-integrator@3GeePay.com");
            String reset = objreset.toJSONString();
            
            LOG.info("Reset Request = " + reset);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = reset.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
                os.close();
            }

            response = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                if (responseBuilder != null) {
                    response = responseBuilder.toString();
                    LOG.info("Reset Response = " + response);
                }
            }

            if (response == null) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
                return response;
            } else {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(response);

                String iv = (String) jsonObject.get("iv");
                String key = (String) jsonObject.get("key");

                responseBuilder = new StringBuilder();

                url = new URL(LOCALHOST_URL + "/3GeePay-3P/api/processor/sys-login");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30 * 1000);
                con.setReadTimeout(120 * 1000);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject objlogin = new JSONObject();
                objlogin.put("ux", "pos-integrator@3GeePay.com");
                objlogin.put("iv", iv);
                objlogin.put("key", key);
                String login = objlogin.toJSONString();
                
                LOG.info("Login Request = " + login);

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = login.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                    os.close();
                }

                response = null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    if (responseBuilder != null) {
                        response = responseBuilder.toString();
                        LOG.info("Login Response = " + response);
                    }
                }

                if (response == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("response", "99");
                    obj.put("description", "No response from server");
                    response = obj.toJSONString();
                    return response;
                } else {
                    jsonObject = (JSONObject) JSONValue.parse(response);
                    String authorization = (String) jsonObject.get("Authorization");

                    responseBuilder = new StringBuilder();

                    url = new URL(CORE_URL + "/3geepay/api/processor/list-banks");
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(30 * 1000);
                    con.setReadTimeout(120 * 1000);
                    con.setRequestProperty("Authorization", authorization);
                    con.setRequestProperty("Content-Type", "application/json; utf-8");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    /*AESCrypter crypta = new AESCrypter(key,iv);
                    String notify = crypta.encrypt(transaction);
                    
                    LOG.info("Encrypted Push Request = " + notify);

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = notify.getBytes("utf-8");
                        os.write(input, 0, input.length);
                        os.flush();
                        os.close();
                    }*/

                    response = null;
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        if (responseBuilder != null) {
                            response = responseBuilder.toString();
                            LOG.info("Push Response = " + response);
                        }
                    }

                    if (response == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "No response from server");
                        response = obj.toJSONString();
                        return response;
                    } else {
                        //String dpush = crypta.decrypt(response);
                        //LOG.info("Decrypted Push Response = " + dpush);
                        /*jsonObject = (JSONObject) JSONValue.parse(response);
                        String requestId = (String) jsonObject.get("requestId");
                        String accountName = (String) jsonObject.get("accountName");
                        JSONObject obj = new JSONObject();
                        obj.put("response", "00");
                        obj.put("description", "Successful");
                        obj.put("accountName", accountName);
                        obj.put("requestId", requestId);
                        response = obj.toJSONString();*/
                    }

                }
                           
            }
            
        } catch (Exception ex) {
            LOG.error("Exception = ", ex);
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/nibss_prep", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prepTreminal(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
            
            response = NIBSS.prep(terminalid, LIVE_HOST, LIVE_PORT, LIVE_PROTOCOL, LIVE_KEY1, LIVE_KEY2, serialno);
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/nibss_transaction", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performTransaction(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            amount = String.format("%012d", Integer.parseInt(amount));
            String cashback = (String) jsonObject.get("cashback");
            cashback = String.format("%012d", Integer.parseInt(cashback));
            //String fromac = (String) jsonObject.get("fromac");
            int ac = 0;
            if(jsonObject.get("fromac") == null){
                ac = 0;
            }
            else{
                try{
                   String fromac = (String) jsonObject.get("fromac");
                   ac = Integer.parseInt(fromac);
                }
                catch(Exception ex){
                    
                }
            }
            //String toac = (String) jsonObject.get("toac");
            String transtype = (String) jsonObject.get("transtype");
            String mcc = (String) jsonObject.get("merchant_category_code");
            String iccdata = (String) jsonObject.get("iccdata");
            String panseqno = (String) jsonObject.get("panseqno");
            panseqno = String.format("%03d", Integer.parseInt(panseqno));
            String track2 = (String) jsonObject.get("track2");
            String merchant_address = (String) jsonObject.get("merchant_address");
            String currencycode = (String) jsonObject.get("currency_code");
            String pinblock = (jsonObject.get("pinblock") == null) ? "" : (String)jsonObject.get("pinblock");
            String rrn = (jsonObject.get("rrn") == null) ? "" : (String)jsonObject.get("rrn");
            
            String session_key = (String) jsonObject.get("session_key");
            String date_time = (String) jsonObject.get("datetime");
            String card = (String) jsonObject.get("card");
            String aid = (String) jsonObject.get("aid");
            String client  = (String) jsonObject.get("client");
            String serialno  = (String) jsonObject.get("serialno");
            String cellinfo  = (String) jsonObject.get("cellinfo");
            String version  = (String) jsonObject.get("version");
           
            String status = NIBSS.transaction(LIVE_HOST, LIVE_PORT, LIVE_PROTOCOL, terminalid, merchantid, Integer.parseInt(transtype), rrn, amount, cashback, mcc, iccdata, panseqno, track2, merchant_address, currencycode, pinblock, ac, session_key, date_time, card, aid, client, serialno, cellinfo, version);
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String datetime = "";
                        try {
                            datetime = jobject.getAsJsonPrimitive("7").getAsString();
                        } catch (Exception ex) {
                        }

                        String refno = "";
                        try {
                            refno = jobject.getAsJsonPrimitive("37").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String stanx = "";
                        try {
                            stanx = jobject.getAsJsonPrimitive("11").getAsString();
                        } catch (Exception ex) {
                        }

                        String authid = "";
                        try {
                            authid = jobject.getAsJsonPrimitive("38").getAsString();
                        } catch (Exception ex) {
                        }

                        String balance = "";
                        if (Integer.parseInt(transtype) == TRAN_TYPE_BALANCE) {
                            try {
                                balance = jobject.getAsJsonPrimitive("54").getAsString();
                                if (balance != null && !balance.isEmpty()) {
                                    if (balance.length() >= 20) {//FIELD54=0002566C 000042795475 0001566C 000042795598
                                        balance = balance.substring(8, 20);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                        String iccresponse = "";
                        try {
                            iccresponse = jobject.getAsJsonPrimitive("55").getAsString();
                        } catch (Exception ex) {
                        }

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "APPROVED");
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("authid", authid);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            if(!balance.isEmpty()){
                                obj.put("balance", balance);
                            }
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/nibss_prep_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String prepTreminalTest(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String serialno = (String) jsonObject.get("serialno");
            if(serialno != null){
                serialno = serialno.replaceAll("[^a-zA-Z0-9]", "");
            }
            
            response = NIBSS.prep(terminalid, TEST_HOST, TEST_PORT, TEST_PROTOCOL, TEST_KEY1, TEST_KEY2, serialno);
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "/nibss_transaction_test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String performTransactionTest(@RequestBody String transaction) {
        
        String response = "";
        
        LOG.info("Request => " + transaction);
        
        try {
            
            JSONObject jsonObject = (JSONObject) JSONValue.parse(transaction);    
            
            String terminalid = (String) jsonObject.get("terminalid"); 
            String merchantid = (String) jsonObject.get("merchantid");
            String amount = (String) jsonObject.get("amount");
            amount = String.format("%012d", Integer.parseInt(amount));
            String cashback = (String) jsonObject.get("cashback");
            cashback = String.format("%012d", Integer.parseInt(cashback));
            //String fromac = (String) jsonObject.get("fromac");
            int ac = 0;
            if(jsonObject.get("fromac") == null){
                ac = 0;
            }
            else{
                try{
                   String fromac = (String) jsonObject.get("fromac");
                   ac = Integer.parseInt(fromac);
                }
                catch(Exception ex){
                    
                }
            }
            //String toac = (String) jsonObject.get("toac");
            String transtype = (String) jsonObject.get("transtype");
            String mcc = (String) jsonObject.get("merchant_category_code");
            String iccdata = (String) jsonObject.get("iccdata");
            String panseqno = (String) jsonObject.get("panseqno");
            panseqno = String.format("%03d", Integer.parseInt(panseqno));
            String track2 = (String) jsonObject.get("track2");
            String merchant_address = (String) jsonObject.get("merchant_address");
            String currencycode = (String) jsonObject.get("currency_code");
            String pinblock = (jsonObject.get("pinblock") == null) ? "" : (String)jsonObject.get("pinblock");
            String rrn = (jsonObject.get("rrn") == null) ? "" : (String)jsonObject.get("rrn");
            
            String session_key = (String) jsonObject.get("session_key");
            String date_time = (String) jsonObject.get("datetime");
            String card = (String) jsonObject.get("card");
            String aid = (String) jsonObject.get("aid");
            String client  = (String) jsonObject.get("client");
            String serialno  = (String) jsonObject.get("serialno");
            String cellinfo  = (String) jsonObject.get("cellinfo");
            String version  = (String) jsonObject.get("version");
           
            String status = NIBSS.transaction(TEST_HOST, TEST_PORT, TEST_PROTOCOL, terminalid, merchantid, Integer.parseInt(transtype), rrn, amount, cashback, mcc, iccdata, panseqno, track2, merchant_address, currencycode, pinblock, ac, session_key, date_time, card, aid, client, serialno, cellinfo, version);
            
            if (status == null || status.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("response", "99");
                obj.put("description", "No response from server");
                response = obj.toJSONString();
            } else {
                //TODO:check if its a correct JSON string here
                JsonObject jobject = null;
                try {
                    JsonElement jsonElement = new JsonParser().parse(status);
                    jobject = jsonElement.getAsJsonObject();

                    if (jobject == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("response", "99");
                        obj.put("description", "Parser Error");
                        response = obj.toJSONString();
                    } else {
                        String f39 = "";
                        try {
                            f39 = jobject.getAsJsonPrimitive("39").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String datetime = "";
                        try {
                            datetime = jobject.getAsJsonPrimitive("7").getAsString();
                        } catch (Exception ex) {
                        }

                        String refno = "";
                        try {
                            refno = jobject.getAsJsonPrimitive("37").getAsString();
                        } catch (Exception ex) {
                        }
                        
                        String stanx = "";
                        try {
                            stanx = jobject.getAsJsonPrimitive("11").getAsString();
                        } catch (Exception ex) {
                        }

                        String authid = "";
                        try {
                            authid = jobject.getAsJsonPrimitive("38").getAsString();
                        } catch (Exception ex) {
                        }

                        String balance = "";
                        if (Integer.parseInt(transtype) == TRAN_TYPE_BALANCE) {
                            try {
                                balance = jobject.getAsJsonPrimitive("54").getAsString();
                                if (balance != null && !balance.isEmpty()) {
                                    if (balance.length() >= 20) {//FIELD54=0002566C 000042795475 0001566C 000042795598
                                        balance = balance.substring(8, 20);
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }

                        String iccresponse = "";
                        try {
                            iccresponse = jobject.getAsJsonPrimitive("55").getAsString();
                        } catch (Exception ex) {
                        }

                        if (f39 != null && f39.equals("00")) {
                            JSONObject obj = new JSONObject();
                            obj.put("response", "00");
                            obj.put("description", "APPROVED");
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("authid", authid);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            if(!balance.isEmpty()){
                                obj.put("balance", balance);
                            }
                            response = obj.toJSONString();
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("response", f39);
                            obj.put("rrn", refno);
                            obj.put("stan", stanx);
                            obj.put("datetime", datetime);
                            if(!iccresponse.isEmpty()){
                                obj.put("iccresponse", iccresponse);
                            }
                            obj.put("description", ResponseCodes.responseCodeErrorMessage(Integer.parseInt(f39)));
                            response = obj.toJSONString();
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Exception = " + ex.getMessage());
                    JSONObject obj = new JSONObject();
                    obj.put("response", "96");
                    obj.put("description", "Exception:"+ex.getMessage());
                    response = obj.toJSONString();
                }

            }
            
        } catch (Exception ex) {
            LOG.error("Exception = " + ex.getMessage());
            JSONObject obj = new JSONObject();
            obj.put("response", "96");
            obj.put("description", "Exception:"+ex.getMessage());
            response = obj.toJSONString();
        }
        
        LOG.info("Response => " + response);

        return response;
    }
    
}
