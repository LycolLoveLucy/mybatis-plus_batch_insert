package com.application.mybatis.plus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import oracle.sql.TIMESTAMP;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author:lycol
 * 注入批量插入的方法,方法名师insertBatch
 */
public class BatchInsertByList extends AbstractMethod {



    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, getBatchInsertSql(tableInfo,modelClass), Collections.class);
        return this.addMappedStatement(mapperClass,CustSqlMethod.INSERT_BATCH.getMethod(),sqlSource,
                SqlCommandType.INSERT, Collections.class,null,int.class,keyGenerator,null,null);

    }

    private String getBatchInsertSql(TableInfo tableInfo, Class<?> modelClass){
        String batchInsertSql=CustSqlMethod.INSERT_BATCH.getSql();

        StringBuilder insertColumnBuilder=new StringBuilder();
        StringBuilder itemColumnBuilder=new StringBuilder();
        List<TableFieldInfo> fieldList=tableInfo.getFieldList();
        int size=fieldList.size();

        Field[]fields= modelClass.getDeclaredFields();
        //ID属性
        String idField="";
        //添加主键column
        for(Field f:fields){
            if(f.isAnnotationPresent(TableId.class)){
                TableId tableId=f.getAnnotation(TableId.class);
                insertColumnBuilder.append(tableId.value()).append(",");
                idField=f.getName();
                break;

            }
        }
        //获取父类的属性字段
        if(StringUtils.isEmpty(idField)) {
            Field[] superFields = modelClass.getSuperclass().getDeclaredFields();
            //添加主键column
            for (Field f : superFields) {
                if (f.isAnnotationPresent(TableId.class)) {
                    TableId tableId = f.getAnnotation(TableId.class);
                    insertColumnBuilder.append(tableId.value()).append(",");
                    idField = f.getName();
                }
            }
        }

        for(int i=0;i<size-1;i++){
            TableFieldInfo tableFieldInfo=fieldList.get(i);
            insertColumnBuilder.append(tableFieldInfo.getColumn()).append(",");
            String jdbcType=getJdbcTypeByClassType(tableFieldInfo.getPropertyType());
            itemColumnBuilder.append("#{item." + tableFieldInfo.getProperty() + ",jdbcType="+jdbcType+"},\n");
        }
        TableFieldInfo tableFieldInfo=fieldList.get(size-1);
        insertColumnBuilder.append(tableFieldInfo.getColumn());

        String jdbcType=getJdbcTypeByClassType(tableFieldInfo.getPropertyType());
        itemColumnBuilder.append("#{item." + tableFieldInfo.getProperty() + ",jdbcType="+jdbcType+"}");

        String foreachSql;
        //如果是oracle数据库
        if(tableInfo.getDbType()== DbType.ORACLE) {
            foreachSql = "SELECT RAWTOHEX(SYS_GUID()), record.* FROM (\n" +
                    " <foreach collection=\"items\" item='item' index='index' separator=\"union all\">\n" +
                    " select\n" +
                    itemColumnBuilder.toString() +
                    " FROM dual\n" +
                    " </foreach>\n" +
                    " ) record";
        }

        //如果是非oracle数据库
       else   {
            foreachSql = "values" +
                    " <foreach collection=\"items\" item='item'  open='' index='index' separator=','>\n" +
                    "(%s,%s)</foreach>";
            foreachSql=String.format(foreachSql,"#{item."+idField+",jdbcType=VARCHAR}",itemColumnBuilder);
        }

        return  String.format(batchInsertSql,tableInfo.getTableName(),insertColumnBuilder,foreachSql);
    }

    private String getJdbcTypeByClassType(Class clazz){
        if(clazz.getSuperclass()==Number.class){
            return JdbcType.NUMERIC.name();
        }
        if(clazz==String.class){
            return  JdbcType.VARCHAR.name();
        }
        if(clazz== Date.class||clazz== java.sql.Date.class){
            return  JdbcType.DATE.name();
        }

        if(clazz== TIMESTAMP.class){
            return  JdbcType.TIMESTAMP.name();
        }
        //默认返回JavaObject
        return  JdbcType.JAVA_OBJECT.name();

    }

}
