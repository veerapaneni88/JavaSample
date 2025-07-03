/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 9, 2018- 11:58:42 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.person.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.PersonMPSReq;
import us.tx.state.dfps.service.common.response.PersonMPSResponse;
import us.tx.state.dfps.service.contacts.dao.PersonMPSDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 9, 2018- 11:58:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/personMps")
public class PersonMPSController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonMPSDao personMPSDao;

	private static final Logger log = Logger.getLogger("PersonMPSController");

	/**
	 * Method Name: isStagePersReltd Method Description:This Method is used for
	 * checking if a person is related to particular stage of not in the MPS
	 * person tables.
	 * 
	 * @param personMPSReq
	 * @return
	 */
	@RequestMapping(value = "/chkStagePersReltd", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonMPSResponse isStagePersReltd(@RequestBody PersonMPSReq personMPSReq) {
		log.debug("Entering method isStagePersReltd in PersonMPSController");
		PersonMPSResponse personMPSResponse = new PersonMPSResponse();
		boolean isPersStageReltd = personMPSDao.getNbrMPSPersStage(Long.valueOf(personMPSReq.getIdStage()),
				personMPSReq.getStagePerRelType());
		personMPSResponse.setPersStageReltd(isPersStageReltd);
		return personMPSResponse;

	}

}
