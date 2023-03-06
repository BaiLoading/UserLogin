package com.example.springboot_demo.service.serviceImpl;

import com.example.springboot_demo.domain.SensWord;
import com.example.springboot_demo.mapper.SensWordsMapper;
import com.example.springboot_demo.service.SensWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensWordsServiceImpl implements SensWordsService {
    @Autowired
    SensWordsMapper sensWordsMapper;

    @Override
    public List<SensWord> getAll(){
        return sensWordsMapper.getAll();
    }
}
