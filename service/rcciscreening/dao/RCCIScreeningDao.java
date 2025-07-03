package us.tx.state.dfps.service.rcciscreening.dao;

import us.tx.state.dfps.common.dto.RCCIScreeningDto;

public interface RCCIScreeningDao {

    /**
     * Method Name: getIntakeData Method Description:This method is used to
     * retrieve the intake data.
     *
     * @param idCase
     *
     * @return RCCIScreeningDto
     */
     RCCIScreeningDto getIntakeData(long idCase);
}
