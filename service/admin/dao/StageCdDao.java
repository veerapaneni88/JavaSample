package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageCdInDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for fetching stage details> Aug 4, 2017- 3:14:23 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageCdDao {

	/**
	 * 
	 * Method Name: getStageDtlsByDate Method Description: This method will get
	 * Stage details by Date.
	 * 
	 * @param pInputDataRec
	 * @return List<StageCdOutDto> @
	 */
	public List<StageCdOutDto> getStageDtlsByDate(StageCdInDto pInputDataRec);
}
