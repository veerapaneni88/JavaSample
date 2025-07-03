package us.tx.state.dfps.service.apsinhomecontact.service;

import us.tx.state.dfps.service.common.request.ApsInHomeContactSummaryReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation of CINV66S OCTOBER 15, 2021- 2:36:56 PM
 * © 2017 Texas Department of Family and Protective Services
 *
 */
public interface ApsInHomeContactSummaryService {

    /**
     * Method Name: getLawEnforcementDetails Method Description: Makes DAO calls
     * and returns prefill string for form CFIV1430
     *
     * @param request
     * @return PreFillDataServiceDto @
     */
    public PreFillDataServiceDto getLawEnforcementDetails(ApsInHomeContactSummaryReq request);

    /**
     * Method Name: getGuardianshipReferralDetails
     * Method Description: Makes DAO calls and returns prefill string for form CCN07O00 (APS Guardianship Referral Form)
     *
     * @param request
     * @return PreFillDataServiceDto
     */
    public PreFillDataServiceDto getGuardianshipReferralDetails(ApsInHomeContactSummaryReq request);

    /**
     * Method Name: getValidationMessages
     * Method Description: Makes DAO calls and performs business validations for the form , returns error messages list
     *
     * @param request
     * @return errorMessageList
     */
    public List<String> getValidationMessages(ApsInHomeContactSummaryReq request);
}
