package com.application.service;

import com.application.dynamicdatasource.ReadOnlyConnection;
import com.application.dynamicdatasource.WriteConnection;
import com.application.entity.Account;
import com.application.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {


    @Autowired
    UserMapper userMapper;

    @ReadOnlyConnection
    public List<Account> getReadAccountInfo(){
        return  userMapper.selectAllUserInfo();
    }




    @WriteConnection
    public List<Account> getWriteAccountInfo(){
        return  userMapper.selectAllUserInfo();
    }

    public Boolean batchInsertAccounts(List<Account> accounts){
        return  userMapper.insertBatch(accounts)==accounts.size();
    }
}
