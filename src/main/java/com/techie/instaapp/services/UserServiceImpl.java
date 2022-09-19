package com.techie.instaapp.services;

import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.dtos.responses.BvnDataResponse;
import com.techie.instaapp.dtos.responses.BvnResponse;
import com.techie.instaapp.dtos.responses.UserResponse;
import com.techie.instaapp.exceptions.InstaAppException;
import com.techie.instaapp.models.AccountType;
import com.techie.instaapp.models.BvnData;
import com.techie.instaapp.models.User;
import com.techie.instaapp.models.Verification;
import com.techie.instaapp.repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{


    private final BvnDataImpl bvnData;
    private final UserRepo userRepo;


    @Override
    public UserResponse createInstantAccount(UserRequest request) {
        User user = new User();

        BvnResponse response = new BvnResponse();
        UserResponse userResponse = new UserResponse();


       BvnData data= verifyBvn(request.getBVN());
       user.setBvn(data.getBvn());
       user.setPhoneNumber(data.getPhoneNumber());
       user.setFirstName(data.getFirstName());
       user.setLastName(data.getLastName());

       user.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()).name());
       boolean isValid = isValidEmail(request.getEmail());
       if(isValid) {
           user.setEmail(request.getEmail());
       }
       else throw new InstaAppException("Enter a valid email");
        user.setAccountNumber(generateAccountNumber());

       User savedUser = userRepo.save(user);

       BvnDataResponse dataResponse = new BvnDataResponse();
       dataResponse.setDateOfBirth(data.getDateOfBirth());
       dataResponse.setFirstName(savedUser.getFirstName());
       dataResponse.setLastName(savedUser.getLastName());
       dataResponse.setPhoneNumber(savedUser.getPhoneNumber());

        BvnResponse bvnResponse= getBvnResponse(response, dataResponse);

         if(bvnResponse.isStatus() && bvnResponse.getVerification().getStatus().equals("verified")){
             userResponse.setAccountNumber(savedUser.getAccountNumber());
             userResponse.setAccountType(savedUser.getAccountType());
            return userResponse;
         }
         else throw new InstaAppException("invalid bvn");

    }

    private BvnResponse getBvnResponse(BvnResponse response, BvnDataResponse dataResponse) {
        Verification verification = new Verification();
        verification.setStatus("verified");
        verification.setReference("Passed");


        response.setStatus(true);
        response.setData(dataResponse);
        response.setResponseCode("200");
        response.setDetail("verification successful");
        response.setVerification(verification);


        return response;
    }

    private BvnData verifyBvn(String bvn){
            BvnData data = new BvnData();

        if(bvn.length()== 11) {
            data.setDateOfBirth("12/12/2012");
            data.setFirstName("testFirstName");
            data.setLastName("testLastName");
            data.setPhoneNumber("08090909090");
            data.setBvn(bvn);

            bvnData.save(data, bvn);
            return data;
        }
        else throw new InstaAppException("Bvn is not correct");
    }

    private String generateAccountNumber(){
        String [] accountNumber = {"0","1","2","3","4","5","6","7","8","9"};
        String [] userAccountNumber = new String [10];
        Random randomNumbers = new Random();
        StringBuilder numberGenerator = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            userAccountNumber[i] = accountNumber[randomNumbers.nextInt(10)];
        }

        for (int i = 0; i < userAccountNumber.length; i++) {
            numberGenerator.append(userAccountNumber[randomNumbers.nextInt(10)]);
        }
        numberGenerator.append("-01");
        return numberGenerator.toString();
    }

    private boolean isValidEmail(String email){
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        boolean isValid;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        isValid = matcher.matches();

        return isValid;
    }
}
