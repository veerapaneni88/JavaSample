/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 9, 2018- 11:14:56 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.person.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.contacts.dao.PersonMPSDao;
import us.tx.state.dfps.service.person.service.PersonMPSService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 9, 2018- 11:14:56 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PersonMPSServiceImpl implements PersonMPSService {

	@Autowired
	private PersonMPSDao personMPSDao;

	/**
	 * This Method is used for checking if a person is related to particular
	 * stage of not in the MPS person tables.
	 * 
	 * @param idStage
	 * @param stagePerRelType
	 * @return
	 */
	@Override
	public boolean isStagePersReltd(Long idStage, String stagePerRelType) {
		return personMPSDao.getNbrMPSPersStage(idStage, stagePerRelType);
	}
}
