package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Ccmn44 Aug 5, 2017- 11:02:15 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonPortfolioDao {

	/**
	 * 
	 * Method Name: getPersonRecord Method Description: fetch person record
	 * Ccmn44d
	 * 
	 * @param personPortfolioInDto
	 * @return List<PersonPortfolioOutDto>
	 */
	public List<PersonPortfolioOutDto> getPersonRecord(PersonPortfolioInDto personPortfolioInDto);
}
