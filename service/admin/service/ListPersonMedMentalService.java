package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ListPersonMedMentaliDto;
import us.tx.state.dfps.service.admin.dto.ListPersonMedMentaloDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for fetching person_address,person phone, adress_person_link details Aug 18,
 * 2017- 12:24:13 PM © 2017 Texas Department of Family and Protective Services
 */
public interface ListPersonMedMentalService {
	/**
	 * 
	 * Method Name: getListPersonMedMentalDetail Method Description:Interface
	 * for fetching person_address,person phone, adress_person_link details
	 * 
	 * @param ListPersonMedMentaliDto
	 * @return ListPersonMedMentaloDto @
	 */
	public List<ListPersonMedMentaloDto> getListPersonMedMentalDetail(ListPersonMedMentaliDto pInputMsg);

}
