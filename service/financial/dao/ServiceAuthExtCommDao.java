package us.tx.state.dfps.service.financial.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dto.ServiceAuthExtCommDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthTWCBaselineDto;

public interface ServiceAuthExtCommDao {

	/**
	 * Method Name: selectOnlineParameterValue Method Description: This method
	 * returns the value for the the given Key Name from ONLINE_PARAMETERS
	 * table.
	 * 
	 * @param onlineParamTwcAutTrans
	 * @return String
	 */
	public String selectOnlineParameterValue(String onlineParamTwcAutTrans);

	/**
	 * Method Name: retrieveDayCareSvcAuthId Method Description:This method
	 * retrieves Service Authorization ID associated with Day Care Request
	 * Event.
	 * 
	 * @param idDayCareEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long retrieveDayCareSvcAuthId(Long idDayCareEvent);

	/**
	 * Method Name: retrieveDayCareSvcAuthEventId Method Description:This method
	 * retrieves Service Authorization Event ID associated with Day Care Request
	 * Event.
	 * 
	 * @param idDayCareEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long retrieveDayCareSvcAuthEventId(Long idDayCareEvent);

	/**
	 * 
	 * Method Name: selectLatestServiceAuthExtComm Method Description:This
	 * method fetches Latest SVCAUTH_EXT_COMM Record for the given Service
	 * Authorization ID.
	 * 
	 * @param idSvcAuth
	 * @return
	 * @throws DataNotFoundException
	 */
	public ServiceAuthExtCommDto selectLatestServiceAuthExtComm(Long idSvcAuth);

	/**
	 * 
	 * Method Name: selectSvcAuthTWCBaselineForSvcAuth Method Description: This
	 * method returns the latest Communication baseline between TWC and DFPS for
	 * the given Service Authorization.
	 * 
	 * @param idSvcAuth
	 * @return
	 * @throws DataNotFoundException
	 */
	public List<ServiceAuthTWCBaselineDto> selectSvcAuthTWCBaselineForSvcAuth(Long idSvcAuth);

}
