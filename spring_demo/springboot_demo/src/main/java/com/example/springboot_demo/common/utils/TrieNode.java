package com.example.springboot_demo.common.utils;

import java.util.HashMap;


public class TrieNode {
    private char aChar;
    private HashMap<Character, TrieNode> map;

    public TrieNode() {
        map = new HashMap<>();
    }

    public TrieNode(char aChar) {
        this.aChar = aChar;
        map = new HashMap<>();

    }

    public HashMap<Character, TrieNode> getMap() {
        return map;
    }
}
