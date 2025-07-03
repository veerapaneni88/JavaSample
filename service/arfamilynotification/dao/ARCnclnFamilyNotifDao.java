package us.tx.state.dfps.service.arfamilynotification.dao;

import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<dao
 * interface class for ARCnclnFamilyNotifService> Apr 11, 2018- 9:31:51 AM ©
 * 2017 Texas Department of Family and Protective Services
 * ********Change History**********
 * 02/07/2023 thompswa artf238090 PPM 73576 add getConclusionNotifctnInfo.
 */
public interface ARCnclnFamilyNotifDao {

	/**
	 * 
	 * Method Name: getARStageBegin Method Description: get date stage start
	 * 
	 * @param idCase
	 * @return StageDto
	 */
	StageDto getARStageBegin(Long idCase);

	/**
	 * 
	 * Method Name: getPersonAddrInfo Method Description: fetch person name and
	 * suffix based on person id
	 * 
	 * @param idPerson
	 * @return PersonDto
	 */
	PersonDto getPersonAddrInfo(Long idPerson);

	/**
	 *
	 * Method Name: getConclusionNotifctnInfo Method Description: fetch ConclusionNotifctnInfo and
	 * event info based on stage id and cd_event_type
	 *
	 * @param idStage
	 * @return ConclusionNotifctnInfo
	 */
	ConclusionNotifctnInfo getConclusionNotifctnInfo(Long idStage, Long idPerson);
}
