package com.news.newsdata.tmp.service;

import com.news.newsdata.tmp.entity.Tmp;
import com.news.newsdata.tmp.repository.TmpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TmpService {

    private final TmpRepository tmpRepository;

    public List<Tmp> selectTmpList() {
        return tmpRepository.findAll();
    }
}
