package us.tx.state.dfps.service.personlistbystage.service;

import us.tx.state.dfps.service.common.request.PersonListStageInReq;
import us.tx.state.dfps.service.common.response.PersonListStageOutRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieves
 * Information for the Person List by stage. Implements methods in
 * Csys03sBean.java Oct 10, 2017- 5:58:43 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface PersonListByStageService {

	/**
	 * Method Name: fetchPersonListInfoByStage Method Description:Retrieves
	 * information for the Person List by stage
	 * 
	 * @param personListStageInReq
	 * @return PersonListStageOutRes @
	 */
	public PersonListStageOutRes fetchPersonListInfoByStage(PersonListStageInReq personListStageInReq);
}
