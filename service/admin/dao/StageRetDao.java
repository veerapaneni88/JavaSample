package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StagediDto;
import us.tx.state.dfps.service.admin.dto.StagedoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cses71dDao
 * Aug 11, 2017- 1:38:46 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StageRetDao {
	public List<StagedoDto> getStageValues(StagediDto pInputDataRec);

	/**
	 * 
	 * Method Name: getStageDtails Method Description:Gets stage details.
	 * 
	 * @param cses71di
	 * @return
	 * @throws DataNotFoundException
	 */
	public StagedoDto getStageDtails(StagediDto cses71di);

}
