package com.techie.instaapp.dtos.responses;

import com.techie.instaapp.models.Verification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BvnResponse {

    private boolean status;
    private String responseCode;
    private String detail;
    private BvnDataResponse data;
    private Verification verification;
}
