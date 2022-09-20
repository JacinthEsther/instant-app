package com.techie.instaapp.controllers;


import com.techie.instaapp.dtos.requests.BankDeposit;
import com.techie.instaapp.dtos.requests.BankTransfer;
import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.exceptions.InstaAppException;
import com.techie.instaapp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/instaApp")
@AllArgsConstructor
public class InstaAppController {

    private final UserService userService;

    @PostMapping("/saveUser")
    public ResponseEntity<?> createInstantAccount(@RequestBody UserRequest request){
        try {
            return new ResponseEntity<>(userService.createInstantAccount(request), HttpStatus.CREATED);
        } catch (InstaAppException ex) {
            return new ResponseEntity<>(new ApiResponse(false, ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }


    @PostMapping("/deposit/{userId}")
        public ResponseEntity<?> deposit(@RequestBody BankDeposit request, @PathVariable String userId){
            try {
                return new ResponseEntity<>(userService.depositTransactionToOwnersAccount(request, userId),
                    HttpStatus.ACCEPTED);
            } catch (InstaAppException ex) {
                return new ResponseEntity<>(new ApiResponse(false, ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

     @PostMapping("/transfer/anotherAccount/{userId}")
        public ResponseEntity<?> transfer(@PathVariable String userId,@RequestBody BankTransfer request){
            try {
                return new ResponseEntity<>(userService.makeTransfer(userId, request),
                    HttpStatus.ACCEPTED);
            } catch (InstaAppException ex) {
                return new ResponseEntity<>(new ApiResponse(false, ex.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }


}
