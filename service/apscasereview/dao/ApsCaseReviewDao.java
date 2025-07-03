package us.tx.state.dfps.service.apscasereview.dao;

import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactNamesDto;
import us.tx.state.dfps.service.apscasereview.ApsStagePersonDto;
import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:ApsCaseReviewDao Jan 21, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */
@Repository
public interface ApsCaseReviewDao {

    /**
     * Gets the persons contacted baased on case.
     * @param caseId
     * @return List<ApsCaseReviewContactNamesDto>
     */
    List<ApsCaseReviewContactNamesDto> getPersonsContactedByCase(Long caseId);


    /**
     * Get stage and person information.
     * @param stagePersonLinkId
     * * @param personId
     * @return ApsStagePersonDto
     */
    ApsStagePersonDto getPersonStageInfo(Long stagePersonLinkId, Long personId);

    /**
     * Get the Case primary worker id
     * @param idCase - selected case
     * @param stagePersonRole - stage person role
     * @return returns primary worker id
     */
    Long getPrimaryWorkerOrSupervisorByCaseId(Long idCase, String stagePersonRole);
}
