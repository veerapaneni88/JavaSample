package us.tx.state.dfps.service.dangerindicators.service;

import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;

public interface DangerIndicatorsService {

    /**
     * Method Name: getDangerIndicator Method Description: Fetch data
     * from DNGR_INDCTRS table using Stage ID.
     *
     * @param stageId
     * @return DangerIndicatorsDto
     */
    DangerIndicatorsDto getDangerIndicator(Long stageId);
}
