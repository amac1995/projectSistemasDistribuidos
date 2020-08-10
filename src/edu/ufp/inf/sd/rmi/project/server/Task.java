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

//todas as infos da tarefa
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

    public Task(String name, Integer creditos, String hash) {       //envia passe
        Random rand = new Random();
        this.taskID = rand.nextInt(100000);
        securePassword = SCryptUtil.scrypt(hash, 16, 16, 16);   //metodo hashing (SCRYPT)
        //vou encripta-la
        System.out.println(securePassword); //imprimo para o user ver como ficou encriptada
        this.name=name;
        this.creditos = creditos;
        readFromFile();     //ler todas as conbinacoes de passes no darkc0de
        subHashMissing = subHash.size() - indexHash;
        dividInListSubHash();       //1 milhao e 500 linhas, divide em grupos de 5 mil palavras
        //quando o cliente pede trabalho ao servidor, o servidor envia um grupo de 5 mil palavras
        done = false;
    }

    //tentamos este mas a de cima e mais acessivel
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

    //devolve o grupo de 5 mil hash ao dividINLIST
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

    //devolve o ficheiro tuodo e devolve em grupos de 5 mil
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
