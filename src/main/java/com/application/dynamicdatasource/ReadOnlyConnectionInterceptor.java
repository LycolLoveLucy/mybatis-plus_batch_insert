package com.application.dynamicdatasource;

import com.application.dynamicdatasource.routing.DbContextHolder;
import com.application.dynamicdatasource.routing.DbType;
import lombok.extern.slf4j.Slf4j;
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
public class ReadOnlyConnectionInterceptor  {

	private static final Logger log = LoggerFactory.getLogger(ReadOnlyConnectionInterceptor.class);

	@Around("@annotation(readOnlyConnection)")
	public Object proceed(ProceedingJoinPoint pjp,
			ReadOnlyConnection readOnlyConnection) throws Throwable {

		try {
			log.info("DbContextHolder.setDbType={}",DbType.READ);
			DbContextHolder.setDbType(DbType.READ);
            Object result = pjp.proceed();
            DbContextHolder.clearDbType();
			return result;
		} finally {
            DbContextHolder.clearDbType();
		}
	}
}
