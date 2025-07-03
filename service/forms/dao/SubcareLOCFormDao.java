/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 18, 2018- 10:55:24 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.dao;

import java.util.Date;

import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 18, 2018- 10:55:24 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SubcareLOCFormDao {

	/**
	 * Method Name: getMedicaidNbrByPersonId Method Description: DAM: CCMN72D
	 * This dam will retrieve the child Medicaid Number and the Medicaid id
	 * number.
	 *
	 * @param cdPersonIdType
	 * @param indPersonIdInvalid
	 * @param dtPersonIdEnd
	 * @return @
	 */
	PersonIdDto getMedicaidNbrByPersonId(Long idPerson, String cdPersonIdType, String indPersonIdInvalid,
			Date dtPersonIdEnd);

	/**
	 * Method Name: getChildInfo Method Description: DAM: CSEC74D This dam will
	 * retrieve all child info based upon an Id Person.
	 *
	 * @param idPerson
	 * @return @
	 */
	PersonDto getChildInfoByPersonId(Long idPerson);

	/**
	 * Method Name: getPhnNbrbyPersonId Method Description:
	 * 
	 * @param idPerson
	 * @param cdPersonPhoneType
	 * @return @
	 */
	PersonPhoneDto getPhnNbrbyPersonId(Long idPerson, String cdPersonPhoneType);

}
