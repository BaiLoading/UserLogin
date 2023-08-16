package com.example.springboot_demo.common.utils;

import com.example.springboot_demo.domain.SensWord;
import com.example.springboot_demo.mapper.SensWordsMapper;
import com.example.springboot_demo.service.SensWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Component
public class MyUtils {
    @Autowired
    SensWordsService sensWordsService;
    private static Trie trie = new Trie();
    static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        List<SensWord> sensWords = sensWordsService.getAll();
        for (SensWord sensWord : sensWords) {
            trie.put(sensWord.getWords());
        }
    }

    /**
     * 判断用户名是否合法
     *
     * @param s 用户名
     * @return true表示不合法，false表示合法
     */
    public static boolean isIllegal(String s) {
        return trie.ifContain(s);
    }
    /**
     * 生成加密密码
     *
     * @param password 密码
     * @return 加密密码
     */
    public static String generateCode(String password){
        return passwordEncoder.encode(password);
    }

    public static boolean checkCode(String password, String bcyprt)
    {
        return passwordEncoder.matches(password, bcyprt);
    }

    //生成16位随机数
    public static String getGUID() {
        StringBuilder uid = new StringBuilder();
        //产生16位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            //产生0-2的3位随机数
            int type = rd.nextInt(3);
            switch (type) {
                case 0:
                    //0-9的随机数
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    //ASCII在65-90之间为大写,获取大写随机
                    uid.append((char) (rd.nextInt(25) + 65));
                    break;
                case 2:
                    //ASCII在97-122之间为小写，获取小写随机
                    uid.append((char) (rd.nextInt(25) + 97));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }


}
