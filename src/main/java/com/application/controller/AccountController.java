package com.application.controller;

import com.application.entity.Account;
import com.application.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    AccountService accountService;

    @GetMapping("/getReadAccountInfo")
    public String getAccountInfo()  {
        return accountService.getReadAccountInfo().get(0).getName();
    }

    @GetMapping("/getWriteAccountInfo")
    public String getWriteAccountInfo() throws JsonProcessingException {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper.writeValueAsString(accountService.getWriteAccountInfo());
    }


    @PostMapping("/insertBatchAccount")
    public String insertBatchAccount(@RequestBody List<Account> accounts) throws JsonProcessingException {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return accountService.batchInsertAccounts(accounts)?"added batch done！":"failed";
    }
}
