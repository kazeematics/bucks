/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author SoloFoundation
 */
public class OpenCellID {

    public static void main(String[] args) {
        String geopoint = OpenCellID.getLocation("621", "30", "10081", "7910271");
        System.out.println("geopoint = " + geopoint);
    }
    
    public static String getLocation(String mcc, String mnc, String lac, String cid) {
        URL url;
        String geopoint = "";
        try {
            url = new URL("https://us1.unwiredlabs.com/v2/process.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);

            String out = "{"
                    + "    \"token\": \"76058d334f03cd\","
                    + "    \"radio\": \"gsm\","
                    + "    \"mcc\": "+mcc+","
                    + "    \"mnc\": "+mnc+","
                    + "    \"cells\": [{"
                    + "        \"lac\": "+lac+","
                    + "        \"cid\": "+cid+""
                    + "    }]"
                    + "}";

            int length = out.getBytes().length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out.getBytes(StandardCharsets.UTF_8));
            }
            // Do something with http.getInputStream()
            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(http.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            System.out.println(content.toString());
            
            Object obj = new JSONParser().parse(content.toString());
            JSONObject jo = (JSONObject) obj;
            
            String status = (String) jo.get("status");
            if (status.equals("ok")) {
                double lat = (double) jo.get("lat");
                double lon = (double) jo.get("lon");

                System.out.println("lat=" + lat);
                System.out.println("lon=" + lon);
                
                geopoint = ""+lat+","+lon;
            }

        } catch (Exception ex) {
            Logger.getLogger(OpenCellID.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return geopoint;
    }
}
