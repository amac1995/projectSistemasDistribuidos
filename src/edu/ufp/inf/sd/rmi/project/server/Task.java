package edu.ufp.inf.sd.rmi.project.server;

import com.lambdaworks.crypto.SCryptUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task implements Serializable {
    Integer taskID;
    String name;
    String securePassword;
    List<String> subHash;
    List<List<String>> listSubHash;
    Integer indexHash = 0;
    Integer subHashMissing;
    Integer creditos;
    Boolean pause = false;
    Boolean done;

    public Task(String name, Integer creditos, String hash) {
        Random rand = new Random();
        this.taskID = rand.nextInt(100000);
        securePassword = SCryptUtil.scrypt(hash, 16, 16, 16);
        System.out.println(securePassword);
        this.name=name;
        this.creditos = creditos;
        readFromFile();
        subHashMissing = subHash.size() - indexHash;
        dividInListSubHash();
        done = false;
    }

    private static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public List<String> returnSubHash(){
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 5000; i++){
            if (subHash.size()>indexHash) {
                String temp = subHash.get(indexHash);
                indexHash++;
                subHashMissing = subHash.size() - indexHash;
                stringList.add(temp);
            }
        }
        return stringList;
    }

    public void dividInListSubHash(){
        listSubHash = new ArrayList<>();
        while (subHashMissing!=0){
            listSubHash.add(returnSubHash());
        }
    }

    public Integer getTaskID() {
        return taskID;
    }

    public String getName() {
        return name;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    public String getSecurePassword() {
        return securePassword;
    }

    public void setSecurePassword(String securePassword) {
        this.securePassword = securePassword;
    }

    public void readFromFile(){
        List<String> result = new ArrayList<>();
        BufferedReader br = null;

        try {

            br = new BufferedReader(new FileReader("D:\\Universidade\\Licenciatura\\5Ano\\SistemasDistribuidos\\SD_Project_JWT\\src\\edu\\ufp\\inf\\sd\\rmi\\darkc0de.txt"));

            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            subHash=result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
