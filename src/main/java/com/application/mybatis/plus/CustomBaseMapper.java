package com.application.mybatis.plus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 拓展了字段为空的时候一个更新所有字段的方法
 * @param <T>
 */
public interface CustomBaseMapper<T> extends BaseMapper<T> {

    int updateAllColumnById(@Param("et") T var1);
}
