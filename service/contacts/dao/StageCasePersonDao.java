package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.StageCasePersonInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.StageCasePersonOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageCasePersonDao Oct 31, 2017- 11:02:05 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageCasePersonDao {

	/**
	 * Method Name: getPrincipalsForStage Method Description:Gets PRIMARY
	 * CASEWORKER from STAGE_PERSON_LINK.
	 * 
	 * @param stageCasePersonInDto
	 * @return StageCasePersonOutDto
	 */
	public StageCasePersonOutDto getPrincipalsForStage(StageCasePersonInDto stageCasePersonInDto);

}
