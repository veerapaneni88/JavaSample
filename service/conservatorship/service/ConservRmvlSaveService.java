package us.tx.state.dfps.service.conservatorship.service;

import java.util.Date;

import us.tx.state.dfps.service.common.request.ConservRmvlSaveReq;
import us.tx.state.dfps.service.common.response.ConservRmvlSaveRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ConservRmvlSaveService Aug 15, 2017- 4:26:27 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ConservRmvlSaveService {

	/**
	 * 
	 * Method Name: conservatorshipRemovalSave
	 * 
	 * Method Description : This method is for Conservatorship Removal which
	 * includes creating a new Conservatorship Removal Detail, Removal Reason
	 * record, Child Removal Characteristic record & Adult Removal
	 * Characteristic record.
	 * 
	 * @param pInputMsg
	 * @return ConservRmvlSaveoDto
	 */
	public ConservRmvlSaveRes conservatorshipRemovalSave(ConservRmvlSaveReq conservRmvlSaveReq);

	/**
	 * Method Name: createCalendarEventForFSU Method Description:
	 * 
	 * @param hostName
	 * @param dtRemoval
	 * @param idStage
	 */
	public void createCalendarEventForFSU(String hostName, Date dtRemoval, Long idStage);

}
