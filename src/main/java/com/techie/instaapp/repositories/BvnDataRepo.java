package com.techie.instaapp.repositories;

import com.techie.instaapp.models.BvnData;

public interface BvnDataRepo {
    void save(BvnData data, String bvn);
}
