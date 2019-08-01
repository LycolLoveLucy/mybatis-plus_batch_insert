package com.application.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_table")
public class Account {

    @TableField("name")
private  String name;

    @TableId(value = "id")
    String id;

    public String getName() {
        return name;
    }

}
