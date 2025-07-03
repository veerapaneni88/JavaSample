/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 17, 2017- 8:05:58 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.EmailDetailReq;
import us.tx.state.dfps.service.common.request.EmailReq;
import us.tx.state.dfps.service.common.request.PersonEmailReq;
import us.tx.state.dfps.service.common.response.EmailRes;
import us.tx.state.dfps.service.common.response.PersonEmailRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jun 17, 2017- 8:05:58 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonEmailService {

	/**
	 * Method Name: getPersonEmailAddress Method Description:
	 * 
	 * @param emailReq
	 * @return
	 */
	public EmailRes getPersonEmailAddress(EmailReq emailReq);

	/**
	 * Method Name: fetchEmailList Method Description:
	 * 
	 * @param request
	 * @return
	 */
	public EmailRes fetchEmailList(EmailReq request);

	/**
	 * Method Name: updatePersonEmail Method Description:
	 * 
	 * @param request
	 */
	public void updatePersonEmail(EmailDetailReq request);

	/**
	 * Method Name: updateEmail Method Description: this method will update
	 * email address
	 * 
	 * @param request
	 */
	public void updateEmail(EmailDetailReq request);

	/**
	 * Method Name: getEmailList Method Description: this method will return
	 * list of email address for a giving person id
	 * 
	 * @param request
	 * @return PersonEmailRes
	 */
	public PersonEmailRes getEmailList(PersonEmailReq request);
}
