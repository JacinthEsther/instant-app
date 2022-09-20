package com.techie.instaapp.services;

import com.techie.instaapp.dtos.requests.BankDeposit;
import com.techie.instaapp.dtos.requests.BankTransfer;
import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.dtos.responses.DepositResponse;
import com.techie.instaapp.dtos.responses.UserResponse;
import com.techie.instaapp.exceptions.InstaAppException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;


    @Test
    void userCanCreateAnAccountWithCorrectBVNOfLength_11Test(){
        UserResponse response = createAccount();

        assertEquals(response.getAccountType(),"Savings".toUpperCase());
    }

    private UserResponse createAccount() {
        UserRequest request = UserRequest.builder()
                .BVN("12345678901").accountType("Savings").email("agbonirojacinta@gmail.com").build();
        return userService.createInstantAccount(request);
    }


    @Test
    void bvnOfLengthLessThan11ThrowsExceptionTest(){
        UserRequest request = UserRequest.builder()
                .BVN("123456").build();
        assertThrows(InstaAppException.class,()->userService.createInstantAccount(request));
    }


   @Test
    void bvnOfLengthGreaterThan11ThrowsExceptionTest(){
        UserRequest request = UserRequest.builder()
                .BVN("123456789012").build();
        assertThrows(InstaAppException.class,()->userService.createInstantAccount(request));
    }


    @Test
    void depositCanBeMadeByVerifiedCustomerTest(){
        UserResponse response = createAccount();
        BankDeposit request = new BankDeposit();
        request.setAmount(12000);
        request.setAccountNumber(response.getAccountNumber());


        DepositResponse depositResponse=  userService.depositTransactionToOwnersAccount
              (request,response.getUserId());

      assertEquals(12_000, depositResponse.getAccountBalance());
    }



    @Test
    void customerCannotDepositZeroAmountOrLessTest(){
        UserResponse response = createAccount();
        BankDeposit request = new BankDeposit();
        request.setAmount(0.00);
        request.setAccountNumber(response.getAccountNumber());

        assertThrows(InstaAppException.class,()-> userService.depositTransactionToOwnersAccount
                (request, response.getUserId()));
    }

    @Test
    void customerCanDepositToAnotherAccount(){
        UserResponse sender = createAccount();

        BankDeposit bankDeposit = new BankDeposit();
        bankDeposit.setAmount(15000);
        bankDeposit.setAccountNumber(sender.getAccountNumber());

         userService.depositTransactionToOwnersAccount
                (bankDeposit,sender.getUserId());

        UserRequest request = UserRequest.builder()
                .BVN("12345678321").accountType("Current").email("agbonirojacinta@gmail.com").build();

        UserResponse receiver = userService.createInstantAccount(request);

        BankTransfer transfer = new BankTransfer();
        transfer.setBankName("GTb");
        transfer.setAmount(14000);

        transfer.setAccountNumber(receiver.getAccountNumber());

       List <DepositResponse> responses = userService.makeTransfer(sender.getUserId(),transfer);

       assertEquals(1000, responses.get(0).getAccountBalance());
       assertEquals(14000, responses.get(1).getAccountBalance());
    }

}