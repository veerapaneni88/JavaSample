package us.tx.state.dfps.service.adminreviewforms.service;


import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.NotificationsRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AdminReviewForms Dec 13, 2023- 4.30.12 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface AdminReviewFormsService {

    public NotificationsRes getNotificationRequest(NotificationsReq notificationsReq);


    /**
     * Method Description: This Method getNotificationToRequestor calls multiple DAOs to populate
     * the data for Notification to Requester English to generate the form
     * by passing the input request object(NotificationsReq)
     * Service Name:CCFC52S
     *
     * @param notificationsReq
     * @return prefillDto
     **/
    public CommonFormRes getNotificationToRequestor(NotificationsReq notificationsReq);

    public CommonFormRes getNotifParentProfReporterCps(NotificationsReq notificationsReq);

    public CommonFormRes getNotifParentProfReporterAps(NotificationsReq notificationsReq);

    public PreFillDataServiceDto getApsNotifToParentProfSp(NotifToParentProfSpReq notifToParentProfSpReq);

    public PreFillDataServiceDto getCpsNotifToParentProfSp(NotifToParentProfSpCpsReq notifToParentProfSpCpsReq);

    public CommonFormRes getReleaseHrngLicANUpheld(PopulateFormReq populateFormReq);
    public CommonFormRes getNotifRequestorSp(PopulateFormReq populateFormReq);

    public CommonFormRes callAdminReveiwFinding(AdminReviewFindingsReq adminReviewFindingsReq);

    /**
     * Method Description: This Method getNotifToRequestorSpanish calls multiple DAOs to populate
     * the data for Notification to Requester Spanish to generate the form
     * by passing the input request object(NotificationsReq)
     * Service Name:CCFC53S
     *
     * @param notificationsReq
     * @return CommonFormRes
     **/
    public CommonFormRes getNotifToRequestorSpanish(NotificationsReq notificationsReq);
    public CommonFormRes getCoverLetterReqCps(CoverLetterCpsReq coverLetterCpsReq);

    public PreFillDataServiceDto getCoverLetterReqCpsSp(PopulateFormReq populateFormReq);
    /**
     * Method Name: getAdminReviewLicANUpheld
     * Method Description: pulls data from DAO and set in Dto
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    public CommonFormRes getAdminReviewLicANUpheld(PopulateFormReq populateFormReq);
    public CommonFormRes getReleaseHrngLicANDOverTurned(RHOverturnedReq rhOverturnedReq);


    public CommonFormRes getNoticeOfAdminReviewForLicensing(PopulateFormReq  populateFormReq);


    /**
     * Method Name: getNoticeOfAdminReviewForLicensing, for form Notification to Requestor Form Letter of Admin Review Findings CCF11o00
     * Method Description: pulls data from DAO and set in Dto
     *
     * @param NotifToRequestorReq
     * @return CommonFormRes
     */
    public CommonFormRes getNotifToRequestorAdminReviewFindings(NotifToRequestorReq notifToRequestorReq);

}
