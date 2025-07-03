package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AllegationDtlsInDto;
import us.tx.state.dfps.service.admin.dto.AllegationDtlsOutDto;
import us.tx.state.dfps.service.riskassesment.dto.AllegationNameDtlDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * AllegationDtlsDao Aug 6, 2017- 5:46:22 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface AllegationDtlsDao {
	/**
	 * 
	 * Method Name: getAllegationDtls Method Description: Get data from
	 * Allegation table.
	 * 
	 * @param pInputDataRec
	 * @return List<AllegationDtlsOutDto> @
	 */
	public List<AllegationDtlsOutDto> getAllegationDtls(AllegationDtlsInDto pInputDataRec);

	/**
	 * 
	 * Method Name: getAllegationNameDtls Method Description: This method
	 * retrieves data from Allegation and Name Tables. CSES90D
	 * 
	 * @param idStage
	 * @return List<AllegationNameDtlDto>
	 */
	public List<AllegationNameDtlDto> getAllegationNameDtls(Long idStage);

}
