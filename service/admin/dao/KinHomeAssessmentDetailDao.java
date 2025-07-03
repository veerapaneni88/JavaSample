package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.service.admin.dto.ApsInHomeTasksDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;

import java.util.List;

public interface KinHomeAssessmentDetailDao {

    /**
     *
     * Method Description:legacy service name - CLSS25D
     *
     * @param KinHomeAssessmentDetail
     * @return @
     */

    public Long saveKinHomeAssessmentDetail(KinHomeAssessmentDetail kinHomeAssessmentDetail);


    /**
     *
     * Method Name: getKinHomeAssesmentDtl Method Description: Fetch the incomedetail
     *
     * @param idSvcAuth
     * @return Long
     */
    public Long getKinHomeAssesmentDtl(Long idSvcAuth);
}
