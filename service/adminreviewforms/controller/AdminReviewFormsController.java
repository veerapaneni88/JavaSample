package us.tx.state.dfps.service.adminreviewforms.controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.adminreviewforms.service.AdminReviewFormsService;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.NotificationsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@RestController
@RequestMapping("adminreviewforms")
public class AdminReviewFormsController {

    @Autowired
    private AdminReviewFormsService adminReviewFormsService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger log = Logger.getLogger(AdminReviewFormsController.class);

    @RequestMapping(value = "/getNotificationRequest", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public NotificationsRes getNotificationRequest(@RequestBody NotificationsReq notificationsReq) {
        return adminReviewFormsService.getNotificationRequest(notificationsReq);
    }

    /**
     *
     * Method Description: This Method getNotifParentProfReporterAps calls the AdminReviewFormsService
     * to retrieve the data for Notification to Parent Professional Reporter English  to generate the form
     * by passing the input request object(NotificationsReq)
     * Form Name: ccf20o00
     *
     * @param NotificationsReq
     * @return commonFormRes
     * @throws Exception
     */

    @RequestMapping(value = "/getNotifParentProfReporterCps", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getNotifParentProfReporterCps(@RequestBody NotificationsReq notificationsReq) {
        if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getNotifParentProfReporterCps(notificationsReq);
    }

    /**
     *
     * Method Description: This Method getNotifParentProfReporterAps calls the AdminReviewFormsService
     * to retrieve the data for Notification to Parent Professional Reporter English  to generate the form
     * by passing the input request object(NotificationsReq)
     * Form Name: ccf10o00
     *
     * @param NotificationsReq
     * @return commonFormRes
     * @throws Exception
     */
    @RequestMapping(value = "/getNotifParentProfReporterAps", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getNotifParentProfReporterAps(@RequestBody NotificationsReq notificationsReq) {
        if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getNotifParentProfReporterAps(notificationsReq);
    }

    /**
     *
     * Method Description: This Method getApsNotifToParentProfSp calls the AdminReviewFormsService
     * to retrieve the data for Notification to Parent Professional Reporter Spanish  to generate the form
     * by passing the input request object(NotifToParentProfSpReq)
     * Form Name: ccf14o00
     *
     * @param notifToParentProfSpReq
     * @return commonFormRes
     * @throws Exception
     */

    @RequestMapping(value = "/getApsNotifToParentProfSp", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getApsNotifToParentProfSp(@RequestBody @NotNull @Valid NotifToParentProfSpReq notifToParentProfSpReq) {
        if (TypeConvUtil.isNullOrEmpty(notifToParentProfSpReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(notifToParentProfSpReq.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(adminReviewFormsService.getApsNotifToParentProfSp(notifToParentProfSpReq)));
        return commonFormRes;
    }

    /**
     *
     * Method Description: This Method getCpsNotifToParentProfSp calls the AdminReviewFormsService
     * to retrieve the data for Notification to Parent Professional Reporter Spanish CPS version to generate the form
     * by passing the input request object(NotifToParentProfSpCpsReq)
     * Form Name: ccf24o00
     *
     * @param notifToParentProfSpCpsReq
     * @return commonFormRes
     * @throws Exception
     */
    @RequestMapping(value = "/getCpsNotifToParentProfSp", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getCpsNotifToParentProfSp(@RequestBody @NotNull @Valid NotifToParentProfSpCpsReq notifToParentProfSpCpsReq) {
        if (TypeConvUtil.isNullOrEmpty(notifToParentProfSpCpsReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(notifToParentProfSpCpsReq.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(adminReviewFormsService.getCpsNotifToParentProfSp(notifToParentProfSpCpsReq)));
        return commonFormRes;
    }

    @RequestMapping(value = "/getReleaseHrngLicANUpheld", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getReleaseHrngLicANUpheld(@RequestBody PopulateFormReq populateFormReq) {
        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getFormName())) {
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getReleaseHrngLicANUpheld(populateFormReq);
    }

    @RequestMapping(value = "/getReleaseHrngLicANDOverTurned", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getReleaseHrngLicANDOverTurned(@RequestBody RHOverturnedReq rhOverturnedReq) {
        if (TypeConvUtil.isNullOrEmpty(rhOverturnedReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(rhOverturnedReq.getFormName())) {
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getReleaseHrngLicANDOverTurned(rhOverturnedReq);
    }

    /**
     * Method Name: callAdminReveiwFinding
     * Method Description: pulls data from DAO and set in Dto
     * ccf09o00
     * @param adminReviewFindingsReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/adminreviewfindings", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes adminReviewFindings(@RequestBody AdminReviewFindingsReq adminReviewFindingsReq) {

        if (TypeConvUtil.isNullOrEmpty(adminReviewFindingsReq.getStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(adminReviewFindingsReq.getEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.callAdminReveiwFinding(adminReviewFindingsReq);

    }

    /**
     * Method Name: getNotifcationToRequestor Method Description: Calls service and sends
     * prefill data to forms
     *
     * @param notificationsReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getNotifcationToRequestor", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getNotifcationToRequestor(@RequestBody NotificationsReq notificationsReq) {
        if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getNotificationToRequestor(notificationsReq);
    }
    /**
     * Method Name: getNotifToRequestorSpanish Method Description: Calls service and sends
     * prefill data to forms. CCF16O00
     *
     * @param notificationsReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getNotifToRequestorSpanish", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getNotifToRequestorSpanish (@RequestBody NotificationsReq notificationsReq){
        if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }

        return adminReviewFormsService.getNotifToRequestorSpanish(notificationsReq);
    }

    /**
     * Method Name: getNotifToRequestorServiceSp Method Description: Calls service and sends
     * prefill data to forms
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getNotifToRequestorServiceSp", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getNotifToRequestorServiceSp(@RequestBody PopulateFormReq  populateFormReq) {
        log.info("Printing the incoming - getNotifToRequestorServiceSp");


        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getFormName())) {
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
        }
        CommonFormRes notifRequestorSp = adminReviewFormsService.getNotifRequestorSp(populateFormReq);
        log.info("Printing the outgoing - notifRequestorSp ");

        return notifRequestorSp;
    }

    /**
     * Method Name: getAdminReviewLicANUpheld Method Description: Calls service and sends
     * prefill data to forms
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getAdminReviewLicANUpheld", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getAdminReviewLicANUpheld(@RequestBody PopulateFormReq populateFormReq) {
        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getAdminReviewLicANUpheld(populateFormReq);
    }

    /**
     * Method Name: getCoverLetterReqCps Method Description: Calls service and sends
     * prefill data to forms
     *
     * @param coverLetterCpsReq
     * @return CommonFormRes
     */

  @RequestMapping(value = "/getCoverLetterReqCps", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getCoverLetterReqCps(@RequestBody CoverLetterCpsReq coverLetterCpsReq) {
        if (TypeConvUtil.isNullOrEmpty(coverLetterCpsReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(coverLetterCpsReq.getFormName())) {
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getCoverLetterReqCps(coverLetterCpsReq);
    }

    /**
     * Method Name: getCPSCoverLetterServiceSp Method Description: Calls service and sends
     * prefill data to forms
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getCpsCoverLetterToSpRequestor", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getCPSCoverLetterServiceSp(@RequestBody PopulateFormReq populateFormReq) {
        CommonFormRes commonFormRes = new CommonFormRes();

        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getFormName())) {
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(adminReviewFormsService.getCoverLetterReqCpsSp(populateFormReq)));
        return commonFormRes;
    }

 /**
     * Method Name: getNoticeOfAdminReviewForLicensing Method Description: Calls service and sends
     * prefill data to forms. CLF02O00
     *
     * @param notificationsReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getNoticeOfAdminReviewForLicensing", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getNoticeOfAdminReviewForLicensing(@RequestBody PopulateFormReq  populateFormReq) {


        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(populateFormReq.getFormName())) {
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));
        }
        CommonFormRes notifRequestorSp = adminReviewFormsService.getNoticeOfAdminReviewForLicensing(populateFormReq);
       return notifRequestorSp;
   }

    @RequestMapping(value = "/getNotifToRequestorAdminReviewFindings", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getReqestgetNotifToRequestorAdminReviewFindings(@RequestBody NotifToRequestorReq notifToRequestorReq) {

        if (TypeConvUtil.isNullOrEmpty(notifToRequestorReq.getIdStage()))  {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        return adminReviewFormsService.getNotifToRequestorAdminReviewFindings(notifToRequestorReq);

    }


}
