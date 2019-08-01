package com.application.dynamicdatasource;

import com.application.dynamicdatasource.routing.DbContextHolder;
import com.application.dynamicdatasource.routing.DbType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(20)
public class WriteConnectionInterceptor  {
    private static final Logger log = LoggerFactory.getLogger(WriteConnectionInterceptor.class);

	@Around("@annotation(writeConnection)")
	public Object proceed(ProceedingJoinPoint pjp,
			WriteConnection writeConnection) throws Throwable {

		try {
            DbContextHolder.setDbType(DbType.WRITE);
            log.info("DbContextHolder.setDbType={}",DbType.WRITE);
            Object result = pjp.proceed();
            DbContextHolder.clearDbType();
			return result;
		} finally {
            DbContextHolder.clearDbType();
		}
	}
}
