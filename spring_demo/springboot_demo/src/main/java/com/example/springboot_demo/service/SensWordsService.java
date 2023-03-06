package com.example.springboot_demo.service;

import com.example.springboot_demo.domain.SensWord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SensWordsService {

    List<SensWord> getAll();
}
