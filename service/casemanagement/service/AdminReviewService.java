package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.service.common.response.CloseOpenStageRes;

public interface AdminReviewService {

    /**
     * closeOpenStage This archived library function provides the necessary
     * edits and updates required to close a stage and open a new one. It
     * generates all the required events and to-do's related with the closure of
     * a stage and the opening of a new one.
     *
     * Service Name - CCMN03U
     *
     * @param closeOpenStageInputDto
     * @return
     * @ @throws
     *       ParseException
     */
    public CloseOpenStageRes createAdminReview(CloseOpenStageInputDto closeOpenStageInputDto);
}
