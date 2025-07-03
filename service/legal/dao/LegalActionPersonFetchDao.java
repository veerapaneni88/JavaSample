package us.tx.state.dfps.service.legal.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonRtrvInDto;
import us.tx.state.dfps.service.legal.dto.LegalActionPersonRtrvOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAO
 * will retrieve full rows from the LEGAL_ACTION table where ID_PERSON equals
 * the host Nov 1, 2017- 3:51:42 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface LegalActionPersonFetchDao {
	/**
	 * Method Name: fetchLegalActionPerson Method Description:Retrieves from the
	 * LEGAL_ACTION table where ID_PERSON equals the host DAM: clscg2d
	 * 
	 * @param legalActionPersonRtrvInDto
	 * @return LegalActionPersonArrDto
	 * @throws DataNotFoundException
	 */
	public LegalActionPersonRtrvOutDto fetchLegalActionPerson(LegalActionPersonRtrvInDto legalActionPersonRtrvInDto)
			throws DataNotFoundException;

}
