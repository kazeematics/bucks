/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.byteworks.bucks;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author developer
 */
public class AESCrypter {

    private String iv = "5OMTZPbytOmFlCAs";
    private String secretkey = "6gTISUYvekDcgDwO";
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    public static void main(String args[]) throws UnsupportedEncodingException, Exception {
        AESCrypter crypta = new AESCrypter();
        String toenc = "1";
        String enc = "";
        String dec = "";
        //encrypt the value
        enc = crypta.encrypt(toenc);
        //encrypted value print out
        System.out.println(" ENCED : " + enc);
        //decrypt the value
        dec = crypta.decrypt(enc);
        //decrypted value print out
        System.out.println(" DECED : " + dec);
    }

    public AESCrypter(String keyz, String ivStr) {
        ivspec = new IvParameterSpec(ivStr.getBytes());
        keyspec = new SecretKeySpec(keyz.getBytes(), "AES");
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public AESCrypter() {
        ivspec = new IvParameterSpec(iv.getBytes());
        keyspec = new SecretKeySpec(secretkey.getBytes(), "AES");
        //System.out.println("this ivspec = " + new String (ivspec.getIV()));
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String text) throws Exception {
        //System.out.println("text = " + text);
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }
        byte[] encrypted = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
        return bytesToHex(encrypted);
    }

    public String decrypt(String code) throws Exception,
            UnsupportedEncodingException {
        if (code == null || code.length() == 0) {
            throw new Exception("Empty string");
        }
        byte[] decrypted = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            decrypted = cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return new String(decrypted, "UTF-8");
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }
        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 0xFF) < 16) {
                str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
            } else {
                str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
            }
        }
        return str;
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }
}
