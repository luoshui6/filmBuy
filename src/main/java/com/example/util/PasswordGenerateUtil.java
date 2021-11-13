package com.example.util;

import org.apache.shiro.crypto.hash.Md5Hash;

public class PasswordGenerateUtil {

    public static String getPassword(String username,String password,String salt,int hashTimes){
        Md5Hash md5Hash = new Md5Hash(password,username+salt,hashTimes);
        return md5Hash.toString();
    }

}
