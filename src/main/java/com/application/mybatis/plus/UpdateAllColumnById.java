package com.application.mybatis.plus;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 支持空字段更新
 */
@Slf4j
public class UpdateAllColumnById extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(final Class<?> mapperClass, final Class<?> modelClass, final TableInfo tableInfo) {
        final SqlMethod sqlMethod = SqlMethod.LOGIC_UPDATE_ALL_COLUMN_BY_ID;

        final List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (final TableFieldInfo tableFieldInfo : fieldList) {
                final Class<? extends TableFieldInfo> aClass = tableFieldInfo.getClass();
                try {
                    final Field fieldFill = aClass.getDeclaredField("fieldFill");
                    fieldFill.setAccessible(true);
                    fieldFill.set(tableFieldInfo, FieldFill.UPDATE);
                } catch (final NoSuchFieldException e) {
                    log.error("no such filed Exception", e);
                } catch (final IllegalAccessException e) {
                    log.error("unable to access field Exception", e);
                }
            }
        }

        final String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), this.sqlSet(false, false, tableInfo, "et."), tableInfo.getKeyColumn(), "et." + tableInfo.getKeyProperty(), (new StringBuilder("<if test=\"et instanceof java.util.Map\">")).append("<if test=\"et.MP_OPTLOCK_VERSION_ORIGINAL!=null\">").append(" AND ${et.MP_OPTLOCK_VERSION_COLUMN}=#{et.MP_OPTLOCK_VERSION_ORIGINAL}").append("</if></if>"));
        final SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }
}


