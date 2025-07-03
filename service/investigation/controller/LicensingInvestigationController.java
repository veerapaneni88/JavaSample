package us.tx.state.dfps.service.investigation.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CvsNotifLogReq;
import us.tx.state.dfps.service.common.request.LicensingInvCnclusnReq;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.request.NameChangeReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.LicensingInvCnclusnRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.LicensingInvSumService;
import us.tx.state.dfps.service.investigation.service.LicensingInvstCnclusnService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Controller
 * class for sending req to LicensingInvstCnclusnService class,
 * LicensingInvSumService class> Mar 27, 2018- 3:05:39 PM © 2017 Texas
 * Department of Family and Protective Services.
 */
@RestController
@RequestMapping("/licensingInvDtl")
public class LicensingInvestigationController {

	/** The licensing inv sum service. */
	@Autowired
	LicensingInvSumService licensingInvSumService;

	/** The licensing invst cnclusn service. */
	@Autowired
	LicensingInvstCnclusnService licensingInvstCnclusnService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/**
	 * Method Description: Populates the LICENSING INVESTIGATION REPORT form.
	 * Name: cfiv1100
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getLicensingInvSummary", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getfacilityInvSum(@RequestBody PopulateFormReq populateFormReq) {
		if (ObjectUtils.isEmpty(populateFormReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(populateFormReq.getDtLicngInvstDtlBegun()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.dtLicngInvstDtlBegun.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(licensingInvSumService.getLicensingInvSumReport(populateFormReq)));

		return commonFormRes;
	}

	/**
	 * Method Name: getLicensingInvInfo Method Description: This calls the
	 * service to retrieve the licensing investigation conclusion information.
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv info
	 */
	@RequestMapping(value = "/getLicensingInvConclusion", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  LicensingInvCnclusnRes getLicensingInvInfo(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idStage.mandatory", null, Locale.US));
		LicensingInvCnclusnRes licensingInvCnclusnRes = licensingInvstCnclusnService
				.displayLicensingInvConclusion(licensingInvCnclusnReq);
		return licensingInvCnclusnRes;
	}

	/**
	 * Method Name: getClassInfo Method Description: retrieves the class
	 * information based on the input
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the class info
	 */
	@RequestMapping(value = "/getClassInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LicensingInvCnclusnRes getClassInfo(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getNbrRsrcFacilAcclaim()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.nbrRsrcFacilAcclaim.mandatory", null, Locale.US));
		LicensingInvCnclusnRes licensingInvCnclusnRes = licensingInvstCnclusnService
				.validateAndGetClassInfo(licensingInvCnclusnReq);
		return licensingInvCnclusnRes;
	}

	/**
	 * Method name: saveLicensingInvCnclusn Method Description: Service call to
	 * save the licensing Investigation conclusion information
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv cnclusn res
	 */
	@RequestMapping(value = "/saveLicensingInvCnclusn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  LicensingInvCnclusnRes saveLicensingInvCnclusn(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
		LicensingInvCnclusnRes licensingInvCnclusnRes = new LicensingInvCnclusnRes();
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getLicensingInvstDtlDto()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.licensingInvstDtlDto.mandatory", null, Locale.US));
		licensingInvstCnclusnService.saveLicensingInvConclusion(licensingInvCnclusnReq);
		return licensingInvCnclusnRes;
	}

	/**
	 * Method Name: Method Description: This makes the service call to validate
	 * the all events related to stage and saves the information
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv cnclusn res
	 */
	@RequestMapping(value = "/saveAndSubmitLicensingInvCnclusn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  LicensingInvCnclusnRes saveAndSubmitLicensingInvCnclusn(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getLicensingInvstDtlDto()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.licensingInvstDtlDto.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdEvent()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idEvent.mandatory", null, Locale.US));
		LicensingInvCnclusnRes licensingInvCnclusnRes = licensingInvstCnclusnService
				.saveAndSubmitLicensingInvConclusion(licensingInvCnclusnReq);
		return licensingInvCnclusnRes;
	}

	/**
	 * Method Name: getOverallDispositionExists
	 * Method Description: This method is used to query the database and see if a stage has an overall disposition
	 * present.
	 * artf128755 - CCI reporter letter
	 *
	 * @param licensingInvCnclusnReq stage to be searched is passed as idStage in DTO.
	 * @return DTO structure containing the result, LicensingInvCnclusnRes.overallDispositionExists will be set to true
	 * or false depending on result.
	 */
	@RequestMapping(value = "/getOverallDispositionExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  LicensingInvCnclusnRes getOverallDispositionExists(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idStage.mandatory", null, Locale.US));
		return licensingInvstCnclusnService
				.getOverallDispositionExists(licensingInvCnclusnReq);
	}
	
	/**
     * Method Name: saveAllegedBehavior
     * Method Description: This method is used to save in to ALLEGED_SX_VCTMZTN table
     * artf129782: Licensing Investigation Conclusion
     * @param licensingInvCnclusnReq 
     * @return DTO structure containing the result, LicensingInvCnclusnRes
     */
    @RequestMapping(value = "/saveAllegedBehavior", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public  LicensingInvCnclusnRes saveAllegedBehavior(
            @RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
     //   if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdStage()))
      //      throw new InvalidRequestException(
       //             messageSource.getMessage("licensingInvCnclusnReq.idStage.mandatory", null, Locale.US));
        return licensingInvstCnclusnService.saveAllegedBehavior(licensingInvCnclusnReq);
    }

	/**
	 * Method Name: updateResourceName
	 * Method Description: This method is used to update case name and county code in CPAS_CASE Table, stage name and county code in Stage table
	 * if IMPACT Operation Resource ID is updated on Operation Number validation in INV conclusion page
	 * artf233239 - Licensing Investigation Conclusion
	 * @param nameChangeReq
	 * @return CommonHelperRes with eventId created after the update.
	 */
	@RequestMapping(value = "/updateResourceName", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes updateResourceName(
			@RequestBody NameChangeReq nameChangeReq) {
		return licensingInvstCnclusnService.updateResourceNameAndCounty(nameChangeReq);
	}


	@RequestMapping(value = "/insertCVSNotifLog", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes saveCvsNotifLog(
			@RequestBody CvsNotifLogReq cvsNotifLogReq) {
		CommonHelperRes response = new CommonHelperRes();
		response.setRowId(licensingInvstCnclusnService.saveCvsNotifLog(cvsNotifLogReq.getCvsNotifLogDto()));
		return response;
	}
	/**
	 * Method Name: getIntakeDate
	 * Method Description: This method is used to get the Intake Date
	 *
	 * @param licensingInvCnclusnReq
	 *            the licensing inv cnclusn req
	 * @return the licensing inv info
	 */
	@RequestMapping(value = "/getIntakeDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  LicensingInvCnclusnRes getIntakeDate(
			@RequestBody LicensingInvCnclusnReq licensingInvCnclusnReq) {
		if (ObjectUtils.isEmpty(licensingInvCnclusnReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("licensingInvCnclusnReq.idStage.mandatory", null, Locale.US));
		LicensingInvCnclusnRes licensingInvCnclusnRes = licensingInvstCnclusnService
				.getIntakeDate(licensingInvCnclusnReq.getIdStage());
		return licensingInvCnclusnRes;
	}

	/**
	 * Method Name: hasManualNotification
	 * Method Description: This method is used to check if manual notification is sent for the person
	 * @param cvsNotifLogReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/getManualNotification", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes hasManualNotification(
			@RequestBody CvsNotifLogReq cvsNotifLogReq) {
		return licensingInvstCnclusnService.hasManualNotification(cvsNotifLogReq.getCvsNotifLogDto().getIdCase(),cvsNotifLogReq.getCvsNotifLogDto().getIdVictimPerson());
	}


}
