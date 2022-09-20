package com.techie.instaapp.services;

import com.techie.instaapp.dtos.requests.BankDeposit;
import com.techie.instaapp.dtos.requests.BankTransfer;
import com.techie.instaapp.dtos.requests.UserRequest;
import com.techie.instaapp.dtos.responses.BvnDataResponse;
import com.techie.instaapp.dtos.responses.BvnResponse;
import com.techie.instaapp.dtos.responses.DepositResponse;
import com.techie.instaapp.dtos.responses.UserResponse;
import com.techie.instaapp.exceptions.InstaAppException;
import com.techie.instaapp.models.*;
import com.techie.instaapp.repositories.AccountRepo;
import com.techie.instaapp.repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{


    private final BvnDataImpl bvnData;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;


    @Override
    public UserResponse createInstantAccount(UserRequest request) {
        User user = new User();

        BvnResponse response = new BvnResponse();
        UserResponse userResponse = new UserResponse();


       BvnData data= verifyBvn(request.getBvn());
       user.setBvn(data.getBvn());
       user.setPhoneNumber(data.getPhoneNumber());
       user.setFirstName(data.getFirstName());
       user.setLastName(data.getLastName());

        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(AccountType.valueOf(request.getAccountType().toUpperCase()).name());
        account.setAccountBalance(BigDecimal.valueOf(0.00));
        Account savedAccount = accountRepo.save(account);


       boolean isValid = isValidEmail(request.getEmail());
       if(isValid) {
           user.setEmail(request.getEmail());
           user.getUserAccounts().add(savedAccount);
       }
       else throw new InstaAppException("Enter a valid email");


       User savedUser = userRepo.save(user);

       BvnDataResponse dataResponse = new BvnDataResponse();

       dataResponse.setDateOfBirth(data.getDateOfBirth());
       dataResponse.setFirstName(savedUser.getFirstName());
       dataResponse.setLastName(savedUser.getLastName());
       dataResponse.setPhoneNumber(savedUser.getPhoneNumber());

        BvnResponse bvnResponse= getBvnResponse(response, dataResponse);

         if(bvnResponse.isStatus() && bvnResponse.getVerification().getStatus().equals("verified")){
             userResponse.setAccountNumber(savedAccount.getAccountNumber());
             userResponse.setAccountType(savedAccount.getAccountType());
             userResponse.setUserId(savedUser.getUserId());
            return userResponse;
         }
         else throw new InstaAppException("Invalid BVN Details");

    }

    @Override
    public DepositResponse depositTransactionToOwnersAccount(BankDeposit request, String userId) {
        DepositResponse depositResponse = new DepositResponse();
        Account userAccount =accountRepo.findByAccountNumber(request.getAccountNumber()).orElseThrow(
                ()->new InstaAppException("Account not found"));

        Optional<User> user = userRepo.findById(userId);
        if(user.isEmpty()) {
            throw new InstaAppException("Invalid details");
        }

            for (int i = 0; i < user.get().getUserAccounts().size(); i++) {
                if (user.get().getUserAccounts().get(i)
                        .getAccountNumber().equals(userAccount.getAccountNumber())) {

                    if (request.getAmount() > 0.00) {
                        userAccount.setAccountBalance(BigDecimal.valueOf(request.getAmount()));
                        Account savedAccount = accountRepo.save(userAccount);

                        user.get().getUserAccounts().get(i).setAccountBalance(savedAccount.getAccountBalance());
                       userRepo.save(user.get());


                        depositResponse.setAccountBalance(savedAccount.getAccountBalance().doubleValue());


                    }
                    else throw new InstaAppException("Please Deposit more than 0.00");
                }
            }
                        return depositResponse;


    }

    @Override
    public List<DepositResponse> makeTransfer(String userId, BankTransfer transfer) {
        DepositResponse senderResponse = new DepositResponse();
        DepositResponse receiverResponse = new DepositResponse();

        User sender = userRepo.findById(userId).orElseThrow();

       Account receiverAccount = accountRepo.findByAccountNumber(transfer.getAccountNumber()).orElseThrow();


       List<User> users =userRepo.findAll();

        for (User receiver : users) {
            for (int j = 0; j < receiver.getUserAccounts().size(); j++) {
       Account senderAccount = accountRepo.findByAccountNumber(sender.getUserAccounts().get(j)
               .getAccountNumber()).orElseThrow();

                if (receiver.getUserAccounts().get(j).getAccountNumber().equals(receiverAccount.getAccountNumber())) {

                        if(transfer.getAmount() > 0 && sender.getUserAccounts().
                                get(j).getAccountBalance().doubleValue() > transfer.getAmount()){


                            receiver.getUserAccounts().get(j).setAccountBalance
                                    (receiver.getUserAccounts().get(j).getAccountBalance()
                                            .add(BigDecimal.valueOf( transfer.getAmount())));
                            receiverAccount.setAccountBalance(receiver.getUserAccounts().get(j).getAccountBalance());


                            BigDecimal value = sender.getUserAccounts().get(j).getAccountBalance()
                                    .subtract(BigDecimal.valueOf(transfer.getAmount()));
                            sender.getUserAccounts().get(j).setAccountBalance(value);

                            senderAccount.setAccountBalance(value);


                            accountRepo.save(receiverAccount);
                            accountRepo.save(senderAccount);

                            User savedSender =userRepo.save(sender);
                            senderResponse.setAccountBalance(savedSender.getUserAccounts()
                                    .get(j).getAccountBalance().doubleValue());

                            User savedReceiver = userRepo.save(receiver);
                            receiverResponse.setAccountBalance(savedReceiver.getUserAccounts()
                                    .get(j).getAccountBalance().doubleValue());
                        }
                else throw new InstaAppException("account balance exceeds withdrawal");
                }

            }
        }
        List <DepositResponse> depositResponses = new ArrayList<>();
        depositResponses.add(senderResponse);
        depositResponses.add(receiverResponse);
        return depositResponses;
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
