/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 27, 2018- 9:19:55 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.daycareabsencereview.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DayCareAbsReviewReq;
import us.tx.state.dfps.service.common.response.DayCareAbsReviewRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.daycareabsencereview.service.DayCareAbsenceReviewService;
import us.tx.state.dfps.service.person.dto.DayCareAbsReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 27, 2018- 9:19:55 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/dayCareAbsenceReview")
public class DayCareAbsenceReviewController {

	@Autowired
	private DayCareAbsenceReviewService dayCareAbsenceReviewService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-DayCareAbsenceReviewControllerLog");
	private static final String DAY_CARE_IDTODO_MANDATORY = "dayCareAbs.idtodo.mandatory";
	private static final String DAY_CARE_DTO_MANDATORY = "dayCareAbs.dayCareAbsReviewDto.mandatory";

	/**
	 * Method Name: getRdccPerson Method Description:This method retrieves RDCC
	 * Person using idTodo
	 * 
	 * @param daycareAbsRewVB
	 * @return DayCareAbsReviewRes
	 */
	@RequestMapping(value = "/getRdccPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes getRdccPerson(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		dayCareAbsReviewRes.setIdRdccPerson(dayCareAbsenceReviewService.getRdccPerson(daycareAbsRewVB.getIdTodo()));
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: getDaycareSvcAuthInfo Method Description: This method
	 * retrieves DayCare Service Auth detail information using idTodo
	 * 
	 * @param daycareAbsRewVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/getDaycareSvcAuthInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes getDaycareSvcAuthInfo(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes
				.setDayCareAbsReviewDto(dayCareAbsenceReviewService.getDaycareSvcAuthInfo(daycareAbsRewVB.getIdTodo()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: getDaycareSvcAuthPerson Method Description:This method
	 * retrieves DayCare Service Auth person using idPerson and idEvent
	 * 
	 * @param daycareAbsRewVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/getDaycareSvcAuthPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes getDaycareSvcAuthPerson(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(dayCareAbsenceReviewService
				.getDaycareSvcAuthPerson(daycareAbsRewVB.getIdPerson(), daycareAbsRewVB.getIdEvent()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: createRDCCAlert Method Description:This method to create an
	 * alert to do to regional day care staff
	 * 
	 * @param daycareAbsRewVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/createRDCCAlert", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes createRDCCAlert(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getDayCareAbsReviewDto())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_DTO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(new DayCareAbsReviewDto());
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		dayCareAbsenceReviewService.createRDCCAlert(daycareAbsRewVB.getIdTodo(),
				daycareAbsRewVB.getDayCareAbsReviewDto());
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: createDaycareSupervisorAlert Method Description:This method
	 * to create an alert to do to regional day care staff
	 * 
	 * @param daycareAbsRevVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/createDaycareSupervisorAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes createDaycareSupervisorAlert(
			@RequestBody DayCareAbsReviewReq daycareAbsRevVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRevVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRevVB.getDayCareAbsReviewDto())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_DTO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(dayCareAbsenceReviewService.getDaycareSvcAuthPerson(
				daycareAbsRevVB.getDayCareAbsReviewDto().getIdPerson(),
				daycareAbsRevVB.getDayCareAbsReviewDto().getIdEvent()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRevVB.getDayCareAbsReviewDto().getIdTodo());
		dayCareAbsReviewRes.setTotalRecCount(dayCareAbsenceReviewService.createDaycareSupervisorAlert(
				daycareAbsRevVB.getDayCareAbsReviewDto().getIdTodo(), daycareAbsRevVB.getDayCareAbsReviewDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRevVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: getChildDayCareRsrcName Method Description:Get daycare
	 * facility name and facility id.
	 * 
	 * @param daycareAbsRevVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/getChildDayCareRsrcName", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes getChildDayCareRsrcName(@RequestBody DayCareAbsReviewReq daycareAbsRevVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRevVB.getDayCareAbsReviewDto())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_DTO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(dayCareAbsenceReviewService
				.getDaycareSvcAuthPerson(daycareAbsRevVB.getIdPerson(), daycareAbsRevVB.getIdEvent()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRevVB.getIdTodo());
		dayCareAbsReviewRes.setDayCareRsrcName(
				dayCareAbsenceReviewService.getChildDayCareRsrcName(daycareAbsRevVB.getDayCareAbsReviewDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRevVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: updateTwcAbsTrans Method Description:this method Updates
	 * TWC_ABSENT_TRANS table using idTodo
	 * 
	 * @param daycareAbsRewVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/updateTwcAbsTrans", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes updateTwcAbsTrans(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getDayCareAbsReviewDto())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_DTO_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(new DayCareAbsReviewDto());
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		dayCareAbsReviewRes.setTotalRecCount(dayCareAbsenceReviewService
				.updateTwcAbsTrans(daycareAbsRewVB.getDayCareAbsReviewDto(), daycareAbsRewVB.getIdTodo()));
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: markDaycareTodoCompleted Method Description: this method
	 * marks day care to do completed per idTodo.
	 * 
	 * @param daycareAbsRewVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/markDaycareTodoCompleted", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes markDaycareTodoCompleted(
			@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(dayCareAbsenceReviewService.getDaycareSvcAuthPerson(
				daycareAbsRewVB.getDayCareAbsReviewDto().getIdPerson(),
				daycareAbsRewVB.getDayCareAbsReviewDto().getIdEvent()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		dayCareAbsReviewRes.getDayCareAbsReviewDto()
				.setDtTodoCompleted(daycareAbsRewVB.getDayCareAbsReviewDto().getDtDaycareTermDate());
		dayCareAbsReviewRes.setTotalRecCount(
				dayCareAbsenceReviewService.markDaycareTodoCompleted(daycareAbsRewVB.getDayCareAbsReviewDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: getDaycareTodo Method Description: this method Gets day care
	 * incomplete day care to do.
	 * 
	 * @param daycareAbsRewVB
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/getDaycareTodo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes getDaycareTodo(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getIdTodo())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_IDTODO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes
				.setDayCareAbsReviewDto(dayCareAbsenceReviewService.getDaycareTodo(daycareAbsRewVB.getIdTodo()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}

	/**
	 * Method Name: isTodoCreatedForStage Method Description:This method checks
	 * to see if there is a day care absent to do created by the Batch process
	 * per stage
	 * 
	 * @param daycareAbsRewVB
	 * @param requestDto
	 * @return dayCareAbsReviewRes
	 */
	@RequestMapping(value = "/isTodoCreatedForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DayCareAbsReviewRes isTodoCreatedForStage(@RequestBody DayCareAbsReviewReq daycareAbsRewVB) {
		if (TypeConvUtil.isNullOrEmpty(daycareAbsRewVB.getDayCareAbsReviewDto())) {
			throw new InvalidRequestException(messageSource.getMessage(DAY_CARE_DTO_MANDATORY, null, Locale.US));
		}
		DayCareAbsReviewRes dayCareAbsReviewRes = new DayCareAbsReviewRes();
		dayCareAbsReviewRes.setDayCareAbsReviewDto(dayCareAbsenceReviewService
				.getDaycareSvcAuthPerson(daycareAbsRewVB.getIdPerson(), daycareAbsRewVB.getIdEvent()));
		dayCareAbsReviewRes.getDayCareAbsReviewDto().setIdTodo(daycareAbsRewVB.getIdTodo());
		dayCareAbsReviewRes
				.setResult(dayCareAbsenceReviewService.isTodoCreatedForStage(daycareAbsRewVB.getDayCareAbsReviewDto()));
		LOG.info(ServiceConstants.TRANSACTION_ID + daycareAbsRewVB.getTransactionId());
		return dayCareAbsReviewRes;
	}
}
