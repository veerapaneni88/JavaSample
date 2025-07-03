package us.tx.state.dfps.service.admin.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;
import us.tx.state.dfps.service.common.response.PersonDetailsRes;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Interface for PersonDetailRetrvlController Aug 5, 2017- 9:01:39 AM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PersonDetailRetrvlService {

	/**
	 *
	 * Method Name: callPersonDetailRetrvlService Method Description: This
	 * Service Retrieves all the information for the person detail window.
	 *
	 * @param personDetailsReq
	 * @return PersonDetailRetrvloDto
	 */
	public PersonDetailsRes personDetailRetrvl(PersonDetailsReq personDetailsReq);

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	IncomingPersonMpsRes mpsPersonDetail(PersonDetailsReq personDetailsReq);
}
