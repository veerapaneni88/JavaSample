package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AllegationInDto;
import us.tx.state.dfps.service.admin.dto.AllegationOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 3:29:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface AllegationSelIdStageDao {

	/**
	 * 
	 * Method Name: havAllegationsSID Method Description: This method will get
	 * ID_ALLEGATION_STAGE from ALLEGATION table.
	 * 
	 * @param allegationInDto
	 * @return List<AllegationOutDto>
	 */
	public List<AllegationOutDto> havAllegationsSID(AllegationInDto allegationInDto);

	List<Long> intakeAllegationsSID(AllegationInDto allegationInDto);

}
