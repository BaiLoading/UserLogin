package com.example.springboot_demo.mapper;

import com.example.springboot_demo.domain.SensWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SensWordsMapper {
    @Select("select * from sensitivewords")
    List<SensWord> getAll();
}
