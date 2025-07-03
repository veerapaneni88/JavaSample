package us.tx.state.dfps.service.intake.dao;

import us.tx.state.dfps.service.common.response.IntNarrBlobRes;

public interface IncomingNarrativeDao {
	/**
	 * 
	 * Method Description:legacy service name - CINT42DI
	 * 
	 * @param idStage
	 * @return @
	 */
	public IntNarrBlobRes getNarrativeById(Long idStage);
}
