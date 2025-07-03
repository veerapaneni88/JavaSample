package us.tx.state.dfps.service.kinhomeinfo.service;

import us.tx.state.dfps.common.domain.ResourceAddress;
import us.tx.state.dfps.common.domain.ResourcePhone;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:KinHomeInfoService Oct 31, 2017- 12:50:34 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface KinHomeInfoService {

	public KinHomeInfoDto getKinHomeInfo(Long stageId);

	public int updateKinHomeStatus(KinHomeInfoDto kinHomeInfoDto);

	public int updateKinHomeInfrmtnStatus(KinHomeInfoDto kinHomeInfoDto);

	public void closeKinHome(KinHomeInfoDto kinHomeInfoDto);

	public int rejectKinHome(KinHomeInfoDto kinHomeInfoDto);

	/**
	 * Method Name: getResServiceInfo Method Description:Fetches the Resource
	 * information
	 * 
	 * @param kinHomeInfoDto
	 * @return KinHomeInfoDto
	 * 
	 */
	public KinHomeInfoDto getResServiceInfo(KinHomeInfoDto kinHomeInfoDto);

	public void deleteResourcePhone(ResourcePhone resourcePhone);

	public void deleteResourceAddress(ResourceAddress resourceAddress) throws DataNotFoundException;

}
