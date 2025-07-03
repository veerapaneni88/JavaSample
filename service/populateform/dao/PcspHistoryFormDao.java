package us.tx.state.dfps.service.populateform.dao;

import us.tx.state.dfps.pcsphistoryform.dto.CareDetailDto;
import us.tx.state.dfps.pcsphistoryform.dto.CareNarrativeInfoDto;
import us.tx.state.dfps.pcsphistoryform.dto.PcspCaseInfoDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:<PcspHistoryFormDao interface> Mar 29, 2018- 12:16:49 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PcspHistoryFormDao {

	/**
	 * DAM Name: CINVD7D Method Description:get ID_EVENT from CARE FACTOR table
	 * to populate CARE Narrative
	 * 
	 * @param idCase
	 * @return List<CareNarrativeInfoDto>
	 */
	List<CareNarrativeInfoDto> getCareNarrativeInfo(Long idCase);

	/**
	 * DAM Name: CINVD6D Method Description:Retrieves CARE details Category,
	 * Sub-Category & Date for Problem, Action and OutCome.
	 * 
	 * @param idCase
	 * @return List<CareDetailDto>
	 * 
	 */
	List<CareDetailDto> getCareDetailInfo(Long idCase);

	/**
	 * DAM Name: CINVD5D Method Description:Retrieves the Case Name,Case
	 * Number,Worker Name
	 * 
	 * @param idCase
	 * @return List<PcspCaseInfoDto>
	 * 
	 */
	List<PcspCaseInfoDto> getPcspCaseInfo(Long idCase);


	/**
	 * DAM Name: CINVD5D Method Description:Retrieves the Case Name,Case
	 * Number,Worker Name
	 *
	 * @param idCase
	 * @return List<PcspCaseInfoDto>
	 *
	 */
	public List<PcspCaseInfoDto> getPcspCase(Long idCase);

	/**
	 * DAM Name: getStageDetails Method Description:Retrieves stage details
	 * 
	 * @param idStage
	 * @return List<stageDto>
	 * 
	 */
	List<StageDto> getStageDetails(Long idStage);

	/**
	 * DAM Name: getPcspasmntDetails Method Description:get pcsp asmnt details
	 * 
	 * @param PCSPAssessmentDto
	 * @return List<PcspAsmntDto>
	 * 
	 */
	PCSPAssessmentDto getPcspasmntDetails(PCSPAssessmentDto pcspAssessmentDto);

	/**
	 * Method Name: getIdStageFromEvent Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	EventDto getIdStageFromEvent(Long idEvent);

}
