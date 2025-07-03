package us.tx.state.dfps.service.intake.dao;

import us.tx.state.dfps.service.common.response.RtrvAllegRes;

public interface IntakeActionsCustomDao {
	/**
	 * 
	 * Method Description:legacy service name - CINT76D
	 * 
	 * @param idStage
	 * @return @
	 */
	public RtrvAllegRes getAllegations(Long idStage);
}
