package us.tx.state.dfps.service.intake.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.IncmgDetermFactors;

public interface IncmgDtrmFactorsDao {
	/**
	 * 
	 * Method Description: legacy service name - CINT15D
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<IncmgDetermFactors> getincmgDetermFactorsById(Long idStage);
}
