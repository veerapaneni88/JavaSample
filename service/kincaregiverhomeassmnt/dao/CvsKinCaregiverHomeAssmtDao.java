package us.tx.state.dfps.service.kincaregiverhomeassmnt.dao;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetailComments;
import us.tx.state.dfps.common.domain.PlacementAudit;

import java.util.List;

/**
 * service-business - Kinship CareGiver Home Assessment (CVSKINHOMEASSESSMENT)
 * 02/20/2025 thompswa ppm84014 : Prefill Service for CVSKINHOMEASSESSMENT
 */
@Repository
public interface CvsKinCaregiverHomeAssmtDao {


    /**
     * @param idEvent
     * @return
     */
    KinHomeAssessmentDetail getKinCaregiverCaseInfoByKamEvent(Long idEvent);

    /**
     * @param idKinHomeAssessmentDetail
     * @return
     */
    List<PlacementAudit> getKinPlacementInfoById(Long idKinHomeAssessmentDetail);

    /**
     *
     * Method Name: getKinHomeCommentsById Method Description: Retrieves comments
     *  using ID for KinHomeAssessmentDetail as input.
     *
     * @param idKinHomeAssessmentDetail
     * @return
     */
    List<KinHomeAssessmentDetailComments> getKinHomeCommentsById(Long idKinHomeAssessmentDetail);
}
