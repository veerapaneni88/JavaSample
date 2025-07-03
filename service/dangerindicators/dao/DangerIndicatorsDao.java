package us.tx.state.dfps.service.dangerindicators.dao;

import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * This class is used for intraction with DNGR_INDCTRS table.
 */
public interface DangerIndicatorsDao {

    /**
     * Method Name: getDangerIndicator Method Description: Fetch data
     * from DNGR_INDCTRS table using Stage ID.
     *
     * @param stageId
     * @return DangerIndicatorsDto
     * @throws DataNotFoundException
     */
    DangerIndicatorsDto getDangerIndicator(Long stageId) throws DataNotFoundException;
}
