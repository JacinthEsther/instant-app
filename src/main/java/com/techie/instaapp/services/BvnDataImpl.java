package com.techie.instaapp.services;

import com.techie.instaapp.models.BvnData;
import com.techie.instaapp.repositories.BvnDataRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BvnDataImpl implements BvnDataRepo {

    Map<String, BvnData> bvnData = new HashMap<>();

    @Override
    public void save(BvnData data, String bvn) {
        bvnData.put(bvn, data);
    }
}
