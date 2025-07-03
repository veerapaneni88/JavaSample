package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for fetching Person details> Aug 4, 2017- 11:57:57 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonStagePersonLinkTypeDao {

	/**
	 * 
	 * Method Name: getPersonDetails Method Description: This method will get
	 * data from PERSON and STAGE_PERSON_LINK table.
	 * 
	 * @param pInputDataRec
	 * @return List<PersonStagePersonLinkTypeOutDto> @
	 */
	public List<PersonStagePersonLinkTypeOutDto> getPersonDetails(PersonStagePersonLinkTypeInDto pInputDataRec);
}
