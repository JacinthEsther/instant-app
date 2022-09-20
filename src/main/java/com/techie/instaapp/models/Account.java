package com.techie.instaapp.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document
public class Account {

    private String accountType;
    @Id
    private String accountNumber;
    private BigDecimal accountBalance;

}
