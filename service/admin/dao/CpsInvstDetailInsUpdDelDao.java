package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 2:42:10 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CpsInvstDetailInsUpdDelDao {

	/**
	 * 
	 * Method Name: getNewCpsInvstId Method Description: This method will give
	 * the ID_CPS_INVST.
	 * 
	 * @return Long
	 */
	public Long getNewCpsInvstId();

	/**
	 * 
	 * Method Name: saveCpsInvstDetail Method Description: This method will
	 * perform SAVE on CPS_INVST_DTL table.
	 * 
	 * @param pInputDataRec
	 * @return CpsInvstDetailInsUpdDelOutDto
	 */
	public CpsInvstDetailInsUpdDelOutDto saveCpsInvstDetail(CpsInvstDetailInsUpdDelInDto pInputDataRec);;

	/**
	 * 
	 * Method Name: updateCpsInvstDetail Method Description: This method will
	 * perform UPDATE on CPS_INVST_DTL table.
	 * 
	 * @param pInputDataRec
	 * @return CpsInvstDetailInsUpdDelOutDto
	 */
	public CpsInvstDetailInsUpdDelOutDto updateCpsInvstDetail(CpsInvstDetailInsUpdDelInDto pInputDataRec);

}
