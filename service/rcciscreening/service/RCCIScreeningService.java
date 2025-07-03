package us.tx.state.dfps.service.rcciscreening.service;

import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface RCCIScreeningService {

    /**
     * Method Name: getIntakeData Method Description:This method is used to
     * retrieve the intake data.
     *
     * @param idCase
     *
     * @return RCCIScreeningDto
     */
     PreFillDataServiceDto getIntakeData(long idCase);
}
