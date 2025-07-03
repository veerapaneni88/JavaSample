/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 3:12:26 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fostercarereview.dao;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 15, 2017- 3:12:26 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FcePersonDao {

	FcePerson getFcepersonById(Long idFcePerson);

	FcePerson save(Long idFceEligibility, Long idPerson);

	Map<Long, FcePersonDto> getFcePersonDtosbyEligibilityId(Long idFceEligibility);

	List<FcePerson> getFcePersonsbyEligibilityId(Long idFceEligibility);

}
