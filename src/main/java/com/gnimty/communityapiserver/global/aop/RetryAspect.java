package com.gnimty.communityapiserver.global.aop;

import javax.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.StaleObjectStateException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Aspect
@Slf4j
@Component
public class RetryAspect {

	@Around("@annotation(retry)")
	public Object retryOptimisticLock(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
		int maxRetry = retry.value();

		Exception exceptionHolder = null;
		for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {
			try {
				return joinPoint.proceed();
			} catch (OptimisticLockException | ObjectOptimisticLockingFailureException | StaleObjectStateException e) {
				log.error("[retry] try count ={}/{}", retryCount, maxRetry);
				exceptionHolder = e;
			}
		}
		assert exceptionHolder != null;
		throw exceptionHolder;
	}
}
