package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.StageUpdateDto;

public interface StagePriorDao {
	/**
	 * 
	 * Method Name: updateStage Method Description:this method update stage
	 * table
	 * 
	 * @param stageUpdateDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long updateStage(StageUpdateDto stageUpdateDto);

}
