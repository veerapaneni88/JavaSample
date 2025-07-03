package us.tx.state.dfps.service.recertification.dao;

import us.tx.state.dfps.common.dto.EligibilityDeterminationDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Performs
 * some of the database operations for Adoption Assistance Eligibility Module.
 * Oct 10, 2017- 10:32:55 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface AaeEligDeterminationDao {

	/**
	 * Method Name: selectAdptAsstEligDetermForAppl Method Description:This
	 * method fetches data from ADPT_ELIG_DETERM table using idAdptEligDeterm
	 * 
	 * @param idAdptEligApplication
	 * @return EligibilityDeterminationValueBeanDto
	 * @throws DataNotFoundException
	 */
	public EligibilityDeterminationDto selectAdptAsstEligDetermForAppl(Long idAdptEligApplication);

	public Long updateAdptAsstEligDeterm(EligibilityDeterminationDto eligibilityDeterminationValueBeanDto);

	public EligibilityDeterminationDto selectAdptAsstEligDetermFromEvent(Long idAppEvent);

	public Long insertAdptAsstEligDeterm(EligibilityDeterminationDto eligibilityDeterminationDto);

}
