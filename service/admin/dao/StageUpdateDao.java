package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.ContactDiDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

import java.util.Date;

public interface StageUpdateDao {
	public int setStgDetails(ContactDiDto pInputDataRec);

	/**
	 * 
	 * Method Name: updateStage Method Description:This method updates the stage
	 * 
	 * @param contactDiDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long updateStage(ContactDiDto contactDiDto);

	public Date getIntakeStageStartDt(Long cdStage);
}