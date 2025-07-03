/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 14, 2017- 2:28:37 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.pcaappandbackground.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PcaAppAndBackgroundReq;
import us.tx.state.dfps.service.common.request.PcaApplAndDetermReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.common.response.PcaAppAndBackgroundRes;
import us.tx.state.dfps.service.common.response.StageRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;
import us.tx.state.dfps.service.pcaappandbackground.service.PcaAppAndBackgroundService;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * class is used to create new PCA Eligibility Application (with Background
 * information), display existing Eligibility Application List, saving
 * Application and Background. Nov 14, 2017- 2:28:37 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@RestController
@RequestMapping("/pcaAppBackground")
public class PcaAppAndBackgroundController {

	private static final String PCA_APP_AND_BACKGROUND_ID_PCA_ELIG_APPLICATION_MANDATORY = "pcaAppAndBackground.idPcaEligApplication.mandatory";

	@Autowired
	PcaAppAndBackgroundService pcaAppAndBackgroundService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * Method Name: fetchApplicationDetails Method Description:This method
	 * returns Pca Application And Background information including - Event,
	 * Person, Placement Details.
	 * 
	 * In case of New Using Retrieve PCA Application Details from the database.
	 * Retrieve Child's Most Recent Permanency Goal from Child Plan. Retrieve
	 * Child's Other Details. In case of New Application Retrieve Child's Most
	 * Recent Permanency Goal from Child Plan. Retrieve Child's Other Details.
	 * In case of Existing Application Retrieve PCA Application Details from the
	 * database. Retrieve PCA Application Event from the database. Retrieve
	 * Child's Other Details.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */

	@RequestMapping(value = "/fetchApplicationDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes fetchApplicationDetails(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		/*
		 * if
		 * (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdAppEvent()))
		 * { throw new InvalidRequestException(messageSource.getMessage(
		 * "common.eventid.mandatory", null, Locale.US)); }
		 */
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.fetchApplicationDetails(pcaAppAndBackgroundReq);
	}

	/**
	 * Method Name: saveApplAndBackgroundInfo Method Description:This method
	 * saves Pca Application And Background information. It checks if the
	 * primary key is 0, if it is creates new Application, if not updates
	 * existing Application.
	 * 
	 * @param pcaApplAndDetermReq
	 * @return
	 */
	@RequestMapping(value = "/saveApplication", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonIdRes saveApplAndBackgroundInfo(@RequestBody PcaApplAndDetermReq pcaApplAndDetermReq) {

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermReq.getPcaApplAndDetermDBDto())
				|| TypeConvUtil.isNullOrEmpty(pcaApplAndDetermReq.getPcaApplAndDetermDBDto().getAppEvent())
				|| TypeConvUtil.isNullOrEmpty(pcaApplAndDetermReq.getPcaApplAndDetermDBDto().getAppValueBean())) {
			throw new InvalidRequestException(
					messageSource.getMessage("PcaAppAndBackground.invalidRequest", null, Locale.US));
		}
		return pcaAppAndBackgroundService.saveApplAndBackgroundInfo(pcaApplAndDetermReq);
	}

	/**
	 * 
	 * Method Name: createPCACICAStage Method Description:This method creates
	 * new PCA Stage for C-ICA Stage Type.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/createPCACICAStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageRes createPCACICAStage(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getUserID())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.userId.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.createPCACICAStage(commonHelperReq);
	}

	/**
	 * 
	 * Method Name: findPriorPlcmtIds Method Description:This method returns
	 * prior placement ids for the same child and facility as the currently
	 * selected placement.
	 * 
	 * @param commonEventIdReq
	 * @return
	 */
	@RequestMapping(value = "/findPriorPlcmtIds", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonIdRes findPriorPlcmtIds(@RequestBody CommonEventIdReq commonEventIdReq) {
		if (TypeConvUtil.isNullOrEmpty(commonEventIdReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.findPriorPlcmtIds(commonEventIdReq);
	}

	/**
	 * 
	 * Method Name: fetchPcaAppEvents Method Description: This method returns
	 * all the Events associated with PCA Application. (There can have two
	 * Application events one is SUB stage and another one in PCA stage for the
	 * same PCA Application.)
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	@RequestMapping(value = "/fetchPcaAppEvents", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes fetchPcaAppEvents(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {

		PcaAppAndBackgroundRes pcaAppAndBackgroundRes = new PcaAppAndBackgroundRes();
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdPcaEligApplication())) {
			throw new InvalidRequestException(messageSource
					.getMessage(PCA_APP_AND_BACKGROUND_ID_PCA_ELIG_APPLICATION_MANDATORY, null, Locale.US));
		}
		pcaAppAndBackgroundRes
				.setEventValueDtoList(pcaAppAndBackgroundService.fetchPcaAppEvents(pcaAppAndBackgroundReq));
		return pcaAppAndBackgroundRes;
	}

	/**
	 * 
	 * Method Name: fetchAppAndBackgound Method Description: This method returns
	 * Pca Application And Background information with Date of Birth.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return pcaAppAndBackgroundRes
	 */
	@RequestMapping(value = "/fetchAppAndBackgound", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes fetchAppAndBackgound(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdPcaEligApplication())) {
			throw new InvalidRequestException(messageSource
					.getMessage(PCA_APP_AND_BACKGROUND_ID_PCA_ELIG_APPLICATION_MANDATORY, null, Locale.US));
		}
		PcaAppAndBackgroundRes resp = pcaAppAndBackgroundService.fetchAppAndBackgound(pcaAppAndBackgroundReq);
		return resp;
	}

	/**
	 * 
	 * Method Name: selectLatestAppForStage Method Description: This method
	 * retrieves latest PCA Application that is not withdrawn. Application could
	 * be in Disqualified or Not Qualified.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	@RequestMapping(value = "/selectLatestAppForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes selectLatestAppForStage(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.selectLatestAppForStage(pcaAppAndBackgroundReq);
	}

	/**
	 * 
	 * Method Name: selectLatestAppForStage Method Description:This method
	 * retrieves Previous Pca Application for the given Pca Application.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	@RequestMapping(value = "/selectPrevApplication", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes selectPrevApplication(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdPcaEligApplication())) {
			throw new InvalidRequestException(messageSource
					.getMessage(PCA_APP_AND_BACKGROUND_ID_PCA_ELIG_APPLICATION_MANDATORY, null, Locale.US));
		}
		return pcaAppAndBackgroundService.selectPrevApplication(pcaAppAndBackgroundReq);
	}

	/**
	 * 
	 * Method Name: selectLatestValidApplication Method Description:This method
	 * retrieves latest Valid Pca Application for the Child. (Application Status
	 * in ('PEND', 'COMP', 'APRV') and Eligibility Determination is not Child
	 * Disqualified or Child Not Qualified).
	 * 
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	@RequestMapping(value = "/selectLatestValidApplication", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes selectLatestValidApplication(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getStatusArray())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.status.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.selectLatestValidApplication(pcaAppAndBackgroundReq);
	}

	/**
	 * 
	 * Method Name: selectLatestApplication Method Description:This method
	 * retrieves latest Pca Application for the Child.
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	/*
	 * @RequestMapping(value = "/selectLatestApplication", headers =
	 * {"Accept=application/json"}, method = RequestMethod.POST)
	 * public  PcaAppAndBackgroundRes
	 * selectLatestApplication(@RequestBody PcaAppAndBackgroundReq
	 * pcaAppAndBackgroundReq) { if
	 * (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdPerson())) {
	 * throw new InvalidRequestException(messageSource.getMessage(
	 * "common.personid.mandatory", null, Locale.US)); }
	 * 
	 * if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getStatusArray()))
	 * { throw new InvalidRequestException(messageSource.getMessage(
	 * "pcaAppAndBackground.status.mandatory", null, Locale.US)); } return
	 * pcaAppAndBackgroundService.selectLatestApplication(pcaAppAndBackgroundReq
	 * ); }
	 */
	/**
	 * 
	 * Method Name: determineQualification Method Description:This method will
	 * be called from Todo Page to Submit PCA Application to Eligibility
	 * Specialist.
	 * 
	 * @param pcaApplAndDetermReq
	 * @return
	 */
	@RequestMapping(value = "/determineQualification", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes determineQualification(@RequestBody PcaApplAndDetermReq pcaApplAndDetermReq) {
		PcaApplAndDetermDBDto pcaApplAndDetermDBDto = pcaApplAndDetermReq.getPcaApplAndDetermDBDto();

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermDBDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.invalidRequest", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermDBDto.getAppEvent().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermDBDto.getAppValueBean())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.invalidRequest", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermDBDto.getAppValueBean().getIdPcaEligApplication())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.idPcaEligApplication.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermDBDto.getAppEvent().getCdEventStatus())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.status.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaApplAndDetermDBDto.getIdLastUpdatePerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.idLastUpdatePerson.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.determineQualification(pcaApplAndDetermReq);
	}

	/**
	 * 
	 * Method Name: submitPCAApplication Method Description:
	 * 
	 * @param pcaAppAndBackgroundReq
	 * @return
	 */
	@RequestMapping(value = "/submitPCAApplication", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaAppAndBackgroundRes submitPCAApplication(
			@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {

		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdAppEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.satgeid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.userId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdAssignedEligWorker())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcaAppAndBackground.idAssignedEligWorker.mandatory", null, Locale.US));
		}
		return pcaAppAndBackgroundService.submitPCAApplication(pcaAppAndBackgroundReq);
	}

	// ---------------------------------------------------PCA UTILITY
	// METHODS-----------------------------------------------------------------------

	@RequestMapping(value = "/findPriorSubStageId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonIdRes findPriorSubStageId(@RequestBody PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		CommonIdRes resp = new CommonIdRes();
		if (TypeConvUtil.isNullOrEmpty(pcaAppAndBackgroundReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		resp.setResultId(pcaAppAndBackgroundService.findPriorSubStageId(pcaAppAndBackgroundReq));
		return resp;
	}

}
