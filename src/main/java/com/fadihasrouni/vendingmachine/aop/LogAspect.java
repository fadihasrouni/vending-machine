package com.fadihasrouni.vendingmachine.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

	private Logger log = LogManager.getLogger(getClass().getName());

	// Setup @Pointcut declaration for Controller
	@Pointcut("execution(* com.fadihasrouni.vendingmachine.controller.*.*(..))")
	private void forControllerPackage() {

	}

	// Setup @Pointcut declaration for Service
	@Pointcut("execution(* com.fadihasrouni.vendingmachine.service.*.*(..))")
	private void forServicePackage() {

	}

	@Pointcut("forControllerPackage() || forServicePackage()")
	private void forAppFlow() {

	}

	@Before("forAppFlow()")
	public void before(JoinPoint joinPoint) {
		String theMethod = joinPoint.getSignature().toShortString();
		Object[] args = joinPoint.getArgs();

		log.info("Enter method {}, Params:{}", theMethod, new Object[] { args });
	}

	@AfterReturning(pointcut = "forAppFlow()", returning = "returnValue")
	public void after(JoinPoint joinPoint, Object returnValue) {
		String theMethod = joinPoint.getSignature().toShortString();
		if (returnValue == null) {
			returnValue = "";
		}
		log.info("Return method {}, Result: {}", theMethod, returnValue);
	}
}
