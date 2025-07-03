package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.PersonPhoneReq;
import us.tx.state.dfps.service.common.request.PhoneMassUpdateReq;
import us.tx.state.dfps.service.common.request.PhoneReq;
import us.tx.state.dfps.service.common.response.PhoneMassUpdateRes;
import us.tx.state.dfps.service.common.response.PhoneRes;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN46S Class
 * Description: Service layer to call PersonPhoneServiceImpl. Apr 10, 2017 -
 * 4:04:00 PM
 */

public interface PersonPhoneService {

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * List/Detail window. Service Name : CCMN46S
	 * 
	 * @param phonereq
	 * @return PhoneRes @
	 */
	public PhoneRes getPersonPhoneDetailList(PhoneReq phonereq);

	/**
	 ** Method Description: This Method will perform Add and Update of
	 * PersonPhone table. Tuxedo Service Name : CCMN31S Dam: CCMN95D
	 * 
	 * @param PersonPhoneReq
	 * @return PersonPhoneRes @
	 * 
	 */
	public PhoneRes savePersonPhoneDtls(PersonPhoneReq personPhoneReq);

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * pullback window. Service Name : PhoneList Pullback EJB
	 * 
	 * @param phonereq
	 * @return PhoneRes @,
	 * 
	 */
	public PhoneRes getPersonPhonePullback(PhoneReq phonereq);

	/**
	 * 
	 * Method Name: getPersonPhoneNumber Method Description:
	 * 
	 * @param phoneReq
	 * @return
	 */
	public PhoneRes getPersonPhoneNumber(PhoneReq phoneReq);

	/**
	 * Method Name: updatePersonPhone Method Description:
	 * 
	 * @param request
	 * @return
	 */
	public void updatePersonPhone(PersonPhoneReq request);

	public PhoneRes getPersonFPPhoneDetailList(PhoneReq phonereq);

	/**
	 * Method Description: This method will perform the phone mass update logic.
	 * 
	 * @param phoneMassUpdateReq
	 * @return @
	 */
	PhoneMassUpdateRes phoneMassUpdate(PhoneMassUpdateReq phoneMassUpdateReq);

	/**
	 * Method Name: getPersonPrimaryActivePhone Method Description: This method
	 * gets person primary phone for input person id
	 * 
	 * @param personId
	 * @return List<PersonPhoneRetDto>
	 */
	public PersonPhoneRetDto getPersonPrimaryActivePhone(Long personId);

	/**
	 * 
	 * Method Name: getPersonPrimaryActivePhone Method Description:Reads the
	 * current primary phone for a person from snapshot table (SS_PERSON_PHONE)
	 * ( For example: This method is used for displaying the Select Forward
	 * person details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	public PersonPhoneRetDto getPersonPrimaryActivePhone(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData);

}
