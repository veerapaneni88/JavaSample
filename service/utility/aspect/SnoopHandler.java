package us.tx.state.dfps.service.utility.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.request.PersonListReq;
import us.tx.state.dfps.service.common.request.SnoopLogReq;
import us.tx.state.dfps.service.common.response.PersonDetailsRes;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.snooplog.service.SnoopMappingService;

/**
 * IMPACT PHASE 2 MODERNIZATION Class Description: Aspect to log the user's
 * access to important Services/tables that may need to to be snooped on later
 * July 25, 2018- 1:21:43 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@EnableAspectJAutoProxy
@Aspect
@Component
public class SnoopHandler {

	private static final String ID_USER = "idUser";
	private static final String METHOD_DESC = "methodDesc";
	private static final String GET_USER_LOGON_ID_METHOD = "getUserLogonId";

	private static final Logger snoopLogger = Logger.getLogger("snoopLogger");

	private static final HashMap<String, String> snoopProperties = new HashMap<String, String>();
	private static final String USER_LOGON_ID = "userLogonId";

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	SnoopMappingService snoopMappingService;

	public SnoopHandler() {
		Properties properties = new Properties();
		try {
			// load snoop description properties to hashmap
			properties.load(getClass().getClassLoader().getResourceAsStream("snooplog.properties"));
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				snoopProperties.put(key, value);
			}
			// configure JSON object mapper properties
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			dateFormat.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
			objectMapper.setDateFormat(dateFormat);
		} catch (Exception e) {
			snoopLogger.error("Error while loading the properties " + e.getMessage());
		}
	}

	// ccmn16d: Retrieves a full row of the ON_CALL table based on dynamic
	// input.
	@Pointcut("execution(* us.tx.state.dfps..*.controller.RetrieveOnCallListController.RetriveonCallList(..))")
	public void onCallList() {
	}

	// ccmn20d: Checks for Overlaps and to save the on call schedule details
	// from ON_CALL table
	@Pointcut("execution(* us.tx.state.dfps..*.controller.SaveOnCallDetailController.SaveOnCallDetails(..))")
	public void saveOnCallDetails() {
	}

	// caud45d :Add on PLACEMENT and select count for the number of rows STAGE
	// S, EVENT E from table for given ID_PLCMT_EVENT
	@Pointcut("execution(* us.tx.state.dfps..*.controller.PlacementController.SaveorUpdatePlacmentDtl(..))")
	public void saveandUpdatePlacement() {
	}

	/*
	 * ccmn48d:Performs UPDATE/DELETE functions for the UNIT_EMP_LINK table,
	 * ccmn49d:Performs ADD/UPDATE/DELETE functions for the UNIT_EMP_LINK
	 * table., ccmne0d:Deletes all rows from the UNIT_EMP_LINK table,
	 * ccmnf3d:Performs an UPDATE of the UNIT table, setting ID UNIT PARENT to
	 * NULL for all occurences of the given ID UNIT PARENT , ccmnf4d:UPDATE
	 * functions for the UNIT_EMP_LINK table., ccmnh8d:Performs DELETE functions
	 * for the UNIT_EMP_LINK table.
	 */

	@Pointcut("execution(* us.tx.state.dfps..*.controller.AdminController.unitUpdate(..)) || "
			+ "execution(* us.tx.state.dfps..*.controller.AdminController.employeeEdit(..))")
	public void unitUpdateMethods() {
	}

	// ccmn13d:Search Cases in table CAPS_CASE with parameters
	@Pointcut("execution(* us.tx.state.dfps..*.controller.CasePackageController.searchCase(..))")
	public void dynamicCaseSearch() {
	}

	// cdyn09d :Returns the list of topics applicable for current selected plan
	// in CODES_TABLES table
	@Pointcut("execution(* us.tx.state.dfps..*.controller.ChildPlanLegacyServiceController.getChildPlan(..))")
	public void selectedPlanList() {
	}

	// cdyn25d:Inserts the sscc child plan topic data into the corresponding
	// child plan topic table on approval from BLOB table and ID_EVENT
	@Pointcut("execution(* us.tx.state.dfps..*.controller.SSCCChildPlanController.updateChildPlanTopic(..))")
	public void insertChildPlanTopic() {
	}

	// cinv77d: Adds, Updates, and Deletes a full row in the ALLEGATION table
	// cinv07d: ADD/UPDATE Allegation details This Service updates ALLEGATION
	// table.
	// cinvb4d:UPDATE Allegation details. This Service updates ALLEGATION table

	@Pointcut("execution(* us.tx.state.dfps..*.controller.AllegtnController.allegationAUD(..))"
			+ "execution(* us.tx.state.dfps..*.controller.AllegtnController.saveFacilAlleg(..))")
	public void allegationAUD() {
	}

	// cinv25d: Perform AUD operation in EXT_DOCUMENTATION table
	// cinv26d:Perform AUD operation in EXT_DOCUMENTATION table

	@Pointcut("execution(* us.tx.state.dfps..*.controller.ExternalDocumentController.externaldocumentationAUD(..)) ||"
			+ "execution(* us.tx.state.dfps..*.controller.ExternalDocumentController.fetchExternaldocumentation(..))")
	public void externalDocumentationAUDF() {
	}

	// cinv41d : UPDATE/DELETE all tables associated with the Investigation
	// Person Detail window.
	@Pointcut("execution(* us.tx.state.dfps..*.controller.PersonController.savePersonChar(..))")
	public void investigationPersonDetails() {
	}

	// cinv57d :ADD/UPDATE/RETRIEVES Utc letter details
	@Pointcut("execution(* us.tx.state.dfps..*.controller.LetterWhenUtcController.getUtcLetter(..))")
	public void utcLetterAUD() {
	}

	// cinv71d: ADD/UPDATE/RETRIEVE/DELETE details from EVENT_PERSON_LINK,
	// PLACEMENT, STAGE, STAGE_PERSON_LINK tables
	@Pointcut("execution(* us.tx.state.dfps..*.controller.FacilityAbuseInvReportController.getAbuseReport(..))")
	public void abuseReportAUDR() {
	}

	// cinv71d:This Perform AUD operation on CARE_TAKER table
	@Pointcut("execution(* us.tx.state.dfps..*.controller.CareTakerInfoController.deleteCareTakerInfo(..))")
	public void careTakerAUD() {
	}

	// csec68d: perform a search given input Id Stage to retrieve basic case
	// information, and using input Id Resource
	@Pointcut("execution(* us.tx.state.dfps..*.controller.CpsIntakeNotificationController.getIncomingDetails(..))")
	public void searchIncomingDetails() {
	}

	// csec68d: Retrieves Service Plan records from SERVICE_PLAN_ITEM table
	@Pointcut("execution(* us.tx.state.dfps..*.controller.FamilyServicePlanController.getServicePlan(..))")
	public void retrieveSearchPlan() {
	}

	// csys04d: Selects the Event Status from the EVENT
	@Pointcut("execution(* us.tx.state.dfps..*.controller.ContactDetailsController.contactInfo\r\n(..))")
	public void searchContactInfo() {
	}

	// csvc19d:Searches and Retrieve the data for contacts
	@Pointcut("execution(* us.tx.state.dfps..*.controller.ServiceDlvyClosureController.saveAndSubmitServiceDlvryClosure(..))")
	public void saveAndSubmitServiceDlvryClosure() {
	}

	// csvc19d: Update/Delete/Add the contact table
	@Pointcut("execution(* us.tx.state.dfps..*.controller.NotificationsController.rtvNotifications(..))")
	public void ContactAUD() {
	}

	// csys13d: Retrieves a DT_LAST_UPDATE from a given NARRATIVE table
	@Pointcut("execution(* us.tx.state.dfps..*.controller.ListProfAssmtController.getProfessionalAssesment(..))")
	public void retrieveNarratives() {
	}

	/*
	 * cint08d :-Performs search functions for the PHONETIC_SEARCH,PERSON,NAME
	 * tables, cint11d Performs search functions for the
	 * PHONETIC_SEARCH,PERSON,NAME tables, cint23d: Performs search functions
	 * for the PHONETIC_NAME,PERSON,NAME tables
	 */
	@Pointcut("execution(* us.tx.state.dfps..*.controller.PhoneticSearchController.getPhoneticSearchDtls(..))")
	public void phoneticDetails() {
	}
	
	// Defect 11123 - Log person search information to snoop log
	@Pointcut("execution(* us.tx.state.dfps..*.controller.PersonSearchController.getPersonSearchView(..))")
	public void personSearch() {
	}

	@Pointcut("execution(* us.tx.state.dfps..*.controller.FacilAllgDtlController.updateFacilAlleg(..))")
	public void facilityInjuryAUD() {
	}

	/*
	 * CAUD22D:Performs ADD/UPDATE/DELETE for EMP_ASSIGN table.
	 * 
	 */
	@Pointcut("execution(* us.tx.state.dfps..*.controller.MaintainDesigneeController.updateEmpTempAssign(..)) ||"
			+ "execution(* us.tx.state.dfps..*.controller.StaffSecurityMaintenanceController.StaffSecurityAud(..))")
	public void empAssign_AUD() {
	}

	@Pointcut("execution(* us.tx.state.dfps..*.controller.PersonController.getPersonList(..))")
	public void personList() {
	}

	@Pointcut("execution(* us.tx.state.dfps..*.controller.PersonDetailController.PersonDetailRetrieve(..))")
	public void personDtl() {
	}

	@Autowired
	MobileUtil mobileUtil;
	/**
	 * 
	 * Method Name: snoopLogging
	 * Metehod Description: log the request details for all sensitive data access
	 *
	 * @param joinPoint
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@AfterReturning("onCallList() ||saveOnCallDetails()||saveandUpdatePlacement()||unitUpdateMethods() || dynamicCaseSearch() || selectedPlanList() "
			+ "||insertChildPlanTopic()||allegationAUD()||externalDocumentationAUDF()||investigationPersonDetails()||utcLetterAUD()||abuseReportAUDR()||"
			+ "careTakerAUD()||searchIncomingDetails()||retrieveSearchPlan()||searchContactInfo()||saveAndSubmitServiceDlvryClosure()||ContactAUD()||"
			+ "retrieveNarratives() ||phoneticDetails()||facilityInjuryAUD()||empAssign_AUD()||personSearch()")
	public void snoopLogging(final JoinPoint joinPoint) {
		doSnoopLogging(joinPoint, null);
	}

	public void doSnoopLogging(final JoinPoint joinPoint, String message) {
		if(mobileUtil.isMPSEnvironment())
			return;
		String idUser = ServiceConstants.EMPTY_STRING;

		// Get userlogon id from request header if available
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		if (!ObjectUtils.isEmpty(request)){
			idUser = request.getHeader(USER_LOGON_ID);
		}
		// if logged on user isnt available in the header, fetch it from the request
		if (StringUtils.isEmpty(idUser)){
			idUser = getIdUser(joinPoint);
		}
		String methodName = getClassAndMethodName(joinPoint);
		String methodDescription = snoopProperties.get(methodName);
		if (methodDescription != null && message != null) {
			// Since description is now dynamic length, we could face overrun errors in theory but full name is capped
			// at 25 characters, the message is 51 characters, and the field we're saving to is 1000 characters.
			methodDescription = String.format(methodDescription, message);
		}
		MDC.put(ID_USER, idUser);
		MDC.put(METHOD_DESC, (null != methodDescription ? methodDescription : methodName));
		String parameters = logInputParameters(joinPoint);
		String parameterstrim;
		if (parameters.length() < 4000) {
			parameterstrim = parameters;
		} else {
			parameterstrim = parameters.substring(0, 4000);
		}

		if (StringUtils.isNotEmpty(idUser)) { // log the table only if iduser is present
			SnoopLogReq snoopLogReq = new SnoopLogReq(new Date(), new Date(), idUser, methodDescription, parameterstrim);
			snoopMappingService.storeSnoopMappingLog(snoopLogReq);
		}

	}

	@AfterReturning("personList()")
	public void snoopLoggingPersonListSearch(final JoinPoint joinPoint) {
		if (readPersonReporterSnoop(joinPoint)) {
			doSnoopLogging(joinPoint, null);
		}
	}
	@AfterReturning(pointcut = "personDtl()", returning = "personDetailsRes")
	public void snoopLoggingPersonDetailSearch(final JoinPoint joinPoint, PersonDetailsRes personDetailsRes) {
		if ("Y".equals(personDetailsRes.getPersonDto().getIndStagePersReporter()) &&  readPersonReporterSnoop(joinPoint)) {
			doSnoopLogging(joinPoint, personDetailsRes.getPersonDto().getNmPersonFull());
		}
	}

	private String getIdUser(JoinPoint joinPoint) {
		String idUser = ServiceConstants.EMPTY_STRING;
		try {

			Object methodArguments = joinPoint.getArgs()[0];
			if (ServiceReqHeaderDto.class.isAssignableFrom(methodArguments.getClass())) {
				Method methodName = methodArguments.getClass().getMethod(GET_USER_LOGON_ID_METHOD, null);
				Object obj = methodName.invoke(methodArguments, null);
				if (obj instanceof String) {
					idUser = (String) obj;
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException nse) {

		}
		return idUser;
	}

	private String logInputParameters(JoinPoint jp) {

		String parameters = ServiceConstants.EMPTY_STRING;
		try {
			Object requestObj = jp.getArgs()[0];
			/*
			 * Class requestClass = requestObj.getClass(); BeanInfo reqBean;
			 * StringBuilder paramLogBuilder = new StringBuilder();
			 * 
			 * reqBean = Introspector.getBeanInfo(requestClass, Object.class);
			 * 
			 * for (PropertyDescriptor propertyDesc :
			 * reqBean.getPropertyDescriptors()) { String propertyName =
			 * propertyDesc.getName(); Object value =
			 * propertyDesc.getReadMethod().invoke(requestObj);
			 * paramLogBuilder.append(propertyName + " - " + (null == value ? ""
			 * : value.toString()) + " , "); }
			 */
			parameters = objectMapper.writeValueAsString(requestObj);
			snoopLogger.info(objectMapper.writeValueAsString(requestObj));

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parameters;

	}

	private Boolean readPersonReporterSnoop(JoinPoint jp) {
		Boolean doSnoop = false;
		Object requestObj = jp.getArgs()[0];

		if (requestObj instanceof PersonListReq) {
			doSnoop = ((PersonListReq) requestObj).isPersonReporterSnoop();
		} else if (requestObj instanceof PersonDetailsReq) {
				doSnoop = ((PersonDetailsReq) requestObj).isPersonReporterSnoop();
		}
		return doSnoop;
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
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String loggerInfo = className + "." + methodName;
		return loggerInfo;
	}
}
