package us.tx.state.dfps.service.populateform.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
import us.tx.state.dfps.service.cpsinv.dto.StagePersonValueDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * methods that make database calls May 29, 2018- 10:13:33 AM © 2017 Texas
 * Department of Family and Protective Services
 * ********Change History**********
 * 01/02/2023 thompswa artf238090 PPM 73576 add getAllegationsList,getConclusionNotifctnInfoList
 */
public interface LetterToParentDao {

	/**
	 * Method Name: victimIndicator Method Description: Victim Indicator, 'Y',
	 * if victim age < 3 years. (DAM: CLSCE6D)
	 * 
	 * @param idStage
	 * @return String
	 */
	public String victimIndicator(Long idStage);

	/**
	 * Method Name: getPerpsInStage Method Description: Gets ids of perps to
	 * check for ruled out allegations (DAM: CLSCE8D)
	 * 
	 * @param idPerson
	 * @param idAllegationStage
	 * @return List<FacilityAllegationInfoDto>
	 */
	public List<FacilityAllegationInfoDto> getPerpsInStage(Long idPerson, Long idAllegationStage);

	/**
	 * Method Name: getParentInfo Method Description: Returns data for an adult
	 * or child parent (DAM: CSECE4D)
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return List<StagePersonValueDto>
	 */
	public List<StagePersonValueDto> getParentInfo(Long idPerson, Long idStage);

	/**
	 * Method Name: getDispositions Method Description: Returns a list of
	 * dispositions (DAM: CLSSA8D)
	 *
	 * @param idAllegationStage
	 * @param b		artf238090
	 * @return List<FacilityAllegationInfoDto>
	 */
	public List<FacilityAllegationInfoDto> getDispositions(List<String> dispositions, boolean b);

	/**
	 * Method Name: getAllegTypes Method Description: Get distinct list of
	 * allegation records for a stage. (DAM: CLSSA9D)
	 *
	 * @param idAllegationStage
	 * @param b		artf238090
	 * @return List<FacilityAllegationInfoDto>
	 */
	public List<FacilityAllegationInfoDto> getAllegTypes(Long idAllegationStage, boolean b);
	/**
	 *
	 * Method Name: getConclusionNotifctnInfo Method Description: fetch ConclusionNotifctnInfo and
	 * event info based on stage id and person id and cd_event_type
	 *
	 * @param idStage
	 * @param idPerson
	 * @return anonymous ConclusionNotifctnInfoList
	 */
	public List<ConclusionNotifctnInfo> getConclusionNotifctnInfoList(Long idStage, Long idPerson);
}
