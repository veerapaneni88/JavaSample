/**
 *common-exception- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 21, 2017- 1:21:43 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.utility.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.exception.BaseServiceException;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * common-exception- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 21, 2017- 1:21:43 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Configuration
@EnableAspectJAutoProxy
@Aspect
@Component
public class ServiceExceptionAdvice {

	private static final String COLON = ":";

	private static final String TRANSACTION_ID = "transactionID";

	private static final String GET_USER_ID = "getUserId";

	private static final Logger DAO_EXCEPTION_LOGGER = Logger.getLogger("daoLayerExceptionLogger");

	private static final Logger SRVCS_EXCEPTION_LOGGER = Logger.getLogger("srvcsLayerExceptionLogger");

	private static final Logger RESTC_EXCEPTION_LOGGER = Logger.getLogger("restControllerExcpnLogger");

	private static final String DAO_LAYER_EXCPTN_BYPASS = "Datalayer Exception detected. No Action taken in After throwing advice for Service/Rest Controller Exceptions";

	private static final String SRVCS_LAYER_EXCPTN_BYPASS = "ServiceLayer Exception detected. No Action taken in After throwing advice for Rest Controller Exceptions";

	private static final String EXCPTN_CAUGHT = "The exception that was caught is: ";

	private static final Long EC_OTHER_EXCEPTION = 9999l;

	/**
	 * 
	 * Method Name: catchDaoException Method Description: AfterThrowing Advice
	 * for all the Dao Layer method calls
	 * 
	 * @param joinPoint
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@AfterThrowing(pointcut = "execution(* us.tx.state.dfps.service.*.daoimpl.*.*(..))", throwing = "e")
	public void catchDaoException(final JoinPoint joinPoint, Throwable e)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String loggerInfo = getClassAndMethodName(joinPoint);
		String exceptionThrown = e.getClass().getName();
		Long errorCode = 0l;
		HttpStatus status;
		String getTransactionId = getTransactionId(e, joinPoint);
		MDC.put(TRANSACTION_ID, getTransactionId);

		DAO_EXCEPTION_LOGGER.info(EXCPTN_CAUGHT + exceptionThrown);
		DAO_EXCEPTION_LOGGER.error(loggerInfo, e);
		if (BaseServiceException.class.isAssignableFrom(e.getClass())) {
			BaseServiceException baseImpactException = (BaseServiceException) e;
			errorCode = baseImpactException.getErrorCode();
			status = baseImpactException.getStatus();
		} else {
			errorCode = EC_OTHER_EXCEPTION;
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		if (e.getMessage() == null || !e.getMessage().contains(TRANSACTION_ID)) {
			throw new DataLayerException(
					TRANSACTION_ID.concat(COLON).concat(getTransactionId).concat(COLON).concat(e.getMessage()),
					errorCode, status);
		} else {
			throw new DataLayerException(e.getMessage(), errorCode, status);
		}

	}

	/**
	 * 
	 * Method Name: catchControllerException Method Description: AfterThrowing
	 * Advice for all the Rest Controller method calls
	 * 
	 * @param joinPoint
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	@AfterThrowing(pointcut = "execution(* us.tx.state.dfps.service.*.controller.*.*(..))", throwing = "e")
	public void catchControllerException(final JoinPoint joinPoint, Throwable e)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		Signature signature = joinPoint.getSignature();
		Class<? extends ServiceResHeaderDto> returnType = ((MethodSignature) signature).getReturnType();
		ErrorDto errorDto = new ErrorDto();
		String transactionId = getTransactionId(e, joinPoint);
		String transactionIdString = TRANSACTION_ID.concat(COLON).concat(transactionId).concat(COLON);
		String errorMessage = e.getMessage();
		if (!ObjectUtils.isEmpty(errorMessage) && errorMessage.contains(transactionIdString)){
			errorMessage = errorMessage.replace(transactionIdString, "");
		}
		errorDto.setErrorMsg(errorMessage);
		ServiceResHeaderDto serviceResHeaderDto = returnType.newInstance();
		// To generate transaction id
		
		serviceResHeaderDto.setTransactionId(transactionId);
		MDC.put(TRANSACTION_ID, transactionId);
		String loggerInfo = getClassAndMethodName(joinPoint);
		String exceptionThrown = e.getClass().getName();
		if (e.getClass().isAssignableFrom(DataLayerException.class)) {
			RESTC_EXCEPTION_LOGGER.error(DAO_LAYER_EXCPTN_BYPASS);
		} else if (e.getClass().isAssignableFrom(ServiceLayerException.class)) {
			RESTC_EXCEPTION_LOGGER.error(SRVCS_LAYER_EXCPTN_BYPASS);
		} else {
			RESTC_EXCEPTION_LOGGER.info(EXCPTN_CAUGHT + exceptionThrown);
			RESTC_EXCEPTION_LOGGER.error(loggerInfo, e);
		}
		if (BaseServiceException.class.isAssignableFrom(e.getClass())) {
			BaseServiceException baseServiceException = (BaseServiceException) e;
			if (!ObjectUtils.isEmpty(baseServiceException.getErrorCode()))
				errorDto.setErrorCode(baseServiceException.getErrorCode().intValue());
			serviceResHeaderDto.setErrorDto(errorDto);
			throw new BaseServiceException(errorMessage, serviceResHeaderDto, baseServiceException.getStatus());
		} else {
			errorDto.setErrorCode(EC_OTHER_EXCEPTION.intValue());
			serviceResHeaderDto.setErrorDto(errorDto);
			throw new BaseServiceException(errorMessage, serviceResHeaderDto);
		}
	}

	/**
	 * 
	 * Method Name: catchSrvcsException Method Description: After throwing
	 * advice for all the Service Layer method Calls.
	 * 
	 * @param joinPoint
	 * @param e
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@AfterThrowing(pointcut = "execution(* us.tx.state.dfps.service.*.serviceimpl.*.*(..))", throwing = "e")
	public void catchSrvcsException(final JoinPoint joinPoint, Throwable e)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String loggerInfo = getClassAndMethodName(joinPoint);
		SRVCS_EXCEPTION_LOGGER.info(e.getClass());
		Long errorCode = 0l;
		HttpStatus status;
		String getTransactionId = getTransactionId(e, joinPoint);
		MDC.put(TRANSACTION_ID, getTransactionId);
		if (BaseServiceException.class.isAssignableFrom(e.getClass())) {
			BaseServiceException baseImpactException = (BaseServiceException) e;
			errorCode = baseImpactException.getErrorCode();
			status = baseImpactException.getStatus();
		} else {
			errorCode = EC_OTHER_EXCEPTION;
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		String exceptionThrown = e.getClass().getName();
		SRVCS_EXCEPTION_LOGGER.info(EXCPTN_CAUGHT + exceptionThrown);
		if (e.getMessage() == null || !e.getMessage().contains(TRANSACTION_ID)) {
			SRVCS_EXCEPTION_LOGGER.error(loggerInfo, e);
			String message = e.getMessage() == null ? "null" : e.getMessage();
			throw new ServiceLayerException(
					TRANSACTION_ID.concat(COLON).concat(getTransactionId == null ? "" : getTransactionId).concat(COLON).concat(message),
					errorCode, status);
		} else {
			SRVCS_EXCEPTION_LOGGER.error(loggerInfo, e);
			throw new ServiceLayerException(e.getMessage(), errorCode, status);
		}
	}

	/**
	 * 
	 * Method Name: getClassAndMethodName Method Description: Return the
	 * combination of Class name and Method name using the passed Join Point
	 * 
	 * @param joinPoint
	 * @return String
	 */
	public String getClassAndMethodName(JoinPoint joinPoint) {
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		String loggerInfo = className + "." + methodName;
		return loggerInfo;
	}

	/**
	 * 
	 * Method Name: generateTransactionId Method Description:to generate
	 * transaction id
	 * 
	 * @param joinPoint
	 * @return String transaction id
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public String generateTransactionId(JoinPoint joinPoint)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String transactionId = null;
		String userId = null;
		Object methodArguments = null;
		// Enhance null handling so errors are not lost. This happened with a host not found error for the DB
		if (joinPoint.getArgs() != null && joinPoint.getArgs().length != 0) {
			methodArguments = joinPoint.getArgs()[0];
		}
		SecureRandom rand = new SecureRandom();
		// Generate random integers in range 0 to 999
		if (!ObjectUtils.isEmpty(methodArguments) && 
				!ObjectUtils.isEmpty(methodArguments.getClass()) && 
				ServiceReqHeaderDto.class.isAssignableFrom(methodArguments.getClass())) {
			Method methodName = methodArguments.getClass().getMethod(GET_USER_ID, null);
			Object obj = methodName.invoke(methodArguments, null);
			if (obj instanceof String) {
				userId = (String) obj;
			}
		}
		StringBuilder transactionIdBuilder = new StringBuilder();
		if (ObjectUtils.isEmpty(userId)) {
			transactionIdBuilder.append(System.currentTimeMillis()).append((long) rand.nextInt(1000000));
		} else {
			transactionIdBuilder.append(System.currentTimeMillis()).append(userId).append((long) rand.nextInt(1000000));
		}

		transactionId = transactionIdBuilder.toString();
		return transactionId;

	}

	/**
	 * Method Name: getTransactionId Method Description: get TransactionId
	 * 
	 * @param e
	 * @param joinPoint
	 * @return transaction id
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public String getTransactionId(Throwable e, JoinPoint joinPoint)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if (e.getMessage() != null && e.getMessage().contains(TRANSACTION_ID)) {
			String transactionId = StringUtils.substringBetween(e.getMessage(), COLON, COLON);
			return transactionId.trim();
		} else {
			return generateTransactionId(joinPoint);
		}
	}
}
