package com.example.springboot_demo.common.utils;

import java.util.HashMap;

public class Trie {
    private final TrieNode root;
    public Trie() {
        root = new TrieNode();
    }

    /**
     * 向前缀树添加敏感词
     * @param s 敏感词
     */
    public void put(String s) {
        char[] chars = s.toCharArray();
        TrieNode pointer = root;
        for (char aChar : chars) {
            HashMap<Character, TrieNode> map = pointer.getMap();
            if (map.containsKey(aChar)) {
                pointer = map.get(aChar);
            }else {
                TrieNode trieNode = new TrieNode(aChar);
                map.put(aChar, trieNode);
                pointer = trieNode;
            }
        }
    }

    /**
     * 判断语句是否包含敏感词
     * @param s 需要判断的语句
     */
    public boolean ifContain(String s) {
        TrieNode pointer = root;
        for (int i = 0; i < s.length(); i++) {
            int j = i;
            while (j < s.length() && pointer.getMap().containsKey(s.charAt(j)) ) {
                pointer = pointer.getMap().get(s.charAt(j));
                j++;
            }
            //树到结尾且树不为空
            if (pointer.getMap().isEmpty() && pointer != root) {
                return true;
            }
            //树没有到结尾
            pointer = root;
        }
        return false;
    }
}