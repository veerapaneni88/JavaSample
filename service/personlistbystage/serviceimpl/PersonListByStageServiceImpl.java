package us.tx.state.dfps.service.personlistbystage.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.PersonListStageInReq;
import us.tx.state.dfps.service.common.response.PersonListStageOutRes;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;
import us.tx.state.dfps.service.personlistbystage.service.PersonListByStageService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieves
 * Information for the Person List by stage. Implements methods in
 * Csys03sBean.java Oct 10, 2017- 5:59:27 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Service
@Transactional
public class PersonListByStageServiceImpl implements PersonListByStageService {

	@Autowired
	private PersonListByStageDao personListByStageDao;

	private static final Logger log = Logger.getLogger(PersonListByStageServiceImpl.class);

	/**
	 * Method Name: fetchPersonListInfoByStage Method Description:Retrieves
	 * information for the Person List by stage
	 * 
	 * @param personListStageInReq
	 * @return PersonListStageOutRes @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonListStageOutRes fetchPersonListInfoByStage(PersonListStageInReq personListStageInReq) {
		log.debug("Entering method fetchPersonListInfoByStage in PersonListByStageServiceImpl");
		PersonListStageOutRes personListStageOutRes = new PersonListStageOutRes();
		personListStageOutRes.setPersonListStageOutDto(personListByStageDao.getPersonDetailsForStage(
				personListStageInReq.getIdStage(), personListStageInReq.getCdStagePersType()));
		return personListStageOutRes;
	}
}
