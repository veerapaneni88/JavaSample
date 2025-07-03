package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.StageInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.StageInsUpdDelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 4:18:58 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StageInsUpdDelDao {

	/**
	 * 
	 * Method Name: saveStageDetail Method Description: This method will perform
	 * SAVE operation on Stage table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto
	 */
	public StageInsUpdDelOutDto saveStageDetail(StageInsUpdDelInDto pInputDataRec);

	/**
	 * 
	 * Method Name: updateStageDetail Method Description: This method will
	 * perform UPDATE operation on Stage table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto
	 */
	public StageInsUpdDelOutDto updateStageDetail(StageInsUpdDelInDto pInputDataRec);

	/**
	 * 
	 * Method Name: updateIncomingDetail Method Description: This method will
	 * perform UPDATE operation on Incoming Detail table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto
	 */
	public StageInsUpdDelOutDto updateIncomingDetail(StageInsUpdDelInDto pInputDataRec);

	/**
	 * 
	 * Method Name: deleteStageDetails Method Description: This method will
	 * perform DELETE operation on Stage table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto
	 */
	public StageInsUpdDelOutDto deleteStageDetails(StageInsUpdDelInDto pInputDataRec);
}
