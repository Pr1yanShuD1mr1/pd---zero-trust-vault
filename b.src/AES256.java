package com.myapp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/*
 * AES256 : CLASS
 * a class for performing basic encryption & decryption
*/

public class AES256 {
    private byte[] currentKey;

    // generate, set, return a new key.
    public byte[] generateKey() throws Exception {
        System.out.println("o-eng--> generating a 256 bit key");
        KeyGenerator kG = KeyGenerator.getInstance("AES");
        kG.init(256);;
        this.currentKey = kG.generateKey().getEncoded();
        return this.currentKey;
    }



    // getter && setter function for key
    public void setKey(byte[] key) { this.currentKey = key; }
    public byte[] getKey() {  return this.currentKey; }



    // converting function btw key and string
    public String key2String(byte[] key)    { return Base64.getEncoder().encodeToString(key); }
    public byte[] String2Key(String string) { return Base64.getDecoder().decode(string);      }



    // basic function for encrytion
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.currentKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }


        
    // basic function for decryption
    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.currentKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }
}









