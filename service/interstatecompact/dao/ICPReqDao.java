package us.tx.state.dfps.service.interstatecompact.dao;

import java.util.List;

import us.tx.state.dfps.service.icpforms.dto.IcpPersonDto;
import us.tx.state.dfps.service.icpforms.dto.IcpRsrcEntityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: dao class
 * for Interstate Compact Placement Request May 11, 2018- 9:46:48 AM © 2017
 * Texas Department of Family and Protective Services
 */
public interface ICPReqDao {

	/**
	 * Dam Name: CSESG1D Method Name: getRequest Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idReq
	 * @return IcpRsrcEntityDto
	 */
	public IcpRsrcEntityDto getRequest(Long idReq);

	/**
	 * Dam Name: CLSCH0D Method Name: getPerson Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	public List<IcpPersonDto> getPerson(Long idEvent);

	/**
	 * Dam Name: CLSCH1D Method Name: getEntity Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpRsrcEntityDto
	 */
	public List<IcpRsrcEntityDto> getEntity(Long idEvent);

	/**
	 * Dam Name: CSECF5D Method Name: getPersonRace Method Description:
	 * Retrieves data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	public IcpPersonDto getPersonRace(Long idEvent);

	/**
	 * Dam Name: CSECF6D Method Name: getResource Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpRsrcEntityDto
	 */
	public IcpRsrcEntityDto getResource(Long idEvent);

	/**
	 * Dam Name: CLSSC6D Method Name: getEnclosure Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return String
	 */
	public List<String> getEnclosure(Long idEvent);

	/**
	 * Dam Name: CLSCH5D Method Name: getPersonAddressReq Method Description:
	 * Retrieves data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	public List<IcpPersonDto> getPersonAddressReq(Long idEvent);

	/**
	 * Dam Name: CSECF1D Method Name: getPlacement Method Description: Retrieves
	 * data based on id
	 * 
	 * @param idEvent
	 * @return IcpPersonDto
	 */
	public IcpPersonDto getPlacement(Long idEvent);

}
