package us.tx.state.dfps.service.apssafetyassmt.service;

import org.springframework.stereotype.Service;


import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import java.util.List;

/**
 * service-business- IMPACT APS MODERNIZATION Class Description:
 * ApsSafetyAssessmentService.
 * Jan 04, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Service
public interface ApsSafetyAssessmentService {

    /**
     * Method Name: getApsSafetyAssessmentFormInformation Method Description: Aps Safety Assessment -- apssa.
     *
     * @param apssaReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsSafetyAssessmentFormInformation(ApsCommonReq apssaReq);

    /**
     * Method Name: contacts data massage method.
     * @param contactsList
     * @return  List<ApsSafetyAssessmentContactDto>
     */
    List<ApsSafetyAssessmentContactDto> sanitizeContacts(List<ApsSafetyAssessmentContactDto> contactsList);

    /**
     * Method Name: getApsSafetyAssessmentDetails
     * @param apsSafetyAssmtReportReq
     * @return  ApsSafetyAssessmentDto
     */
    ApsSafetyAssessmentDto getApsSafetyAssessmentDetails(ApsCommonReq apsSafetyAssmtReportReq);


    /**
     * Method Name: findShieldServicePlanStage
     * @param caseId
     * @param lookupStageId
     * @return  Long stageId
     */
    Long findShieldServicePlanStage(Long caseId, Long lookupStageId);


}
