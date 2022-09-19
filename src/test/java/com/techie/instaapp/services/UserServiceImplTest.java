package com.techie.instaapp.services;

import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.dtos.responses.BvnResponse;
import com.techie.instaapp.dtos.responses.UserResponse;
import com.techie.instaapp.exceptions.InstaAppException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;


    @Test
    void userCanCreateAnAccountWithCorrectBVNOfLength_11(){
        UserRequest request = UserRequest.builder()
                .BVN("12345678901").accountType("Savings").email("agbonirojacinta@gmail.com").build();
       UserResponse response= userService.createInstantAccount(request);

        assertEquals(response.getAccountType(),"Savings".toUpperCase());
    }

    @Test
    void bvnOfLengthLessThan11ThrowsException(){
        UserRequest request = UserRequest.builder()
                .BVN("123456").build();
        assertThrows(InstaAppException.class,()->userService.createInstantAccount(request));
    }

   @Test
    void bvnOfLengthGreaterThan11ThrowsException(){
        UserRequest request = UserRequest.builder()
                .BVN("123456789012").build();
        assertThrows(InstaAppException.class,()->userService.createInstantAccount(request));
    }

    @Test
    void depositCanBeMadeByVerifiedCustomer(){

    }

}