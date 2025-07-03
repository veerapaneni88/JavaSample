package us.tx.state.dfps.service.pal.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.UpdtPalServiceTrainingReq;
import us.tx.state.dfps.service.pal.dto.PALSummaryDto;
import us.tx.state.dfps.service.pal.dto.PalInformationDto;
import us.tx.state.dfps.service.pal.dto.PalServiceTrainingDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 20, 2018- 4:49:48 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PalInformationDao {
	/**
	 * Method Name: getPal Method Description: Fetches the data from Pal Table
	 * 
	 * @param idStage
	 */
	public PalInformationDto getPal(Long idStage);

	/**
	 * Method Name: retrievePalInformation Method Description: Retrieves the
	 * Summary Information from the Pal Table
	 * 
	 * @param idStage
	 * @param idPerson
	 * @param idEvent
	 * @param palSummaryDto
	 */

	public PALSummaryDto retrievePalInformation(Long idStage, Long idPerson, Long idEvent, PALSummaryDto palSummaryDto);

	/**
	 * Method Name: getPalServiceTrainings Method Description: Fetches the
	 * PalServiceTraining Information from the PalService Table
	 * 
	 * @param idStage
	 */
	public List<PalServiceTrainingDto> getPalServiceTrainings(Long idStage);

	/**
	 * Method Name: updatePal Method Description: This dam updates the PAL
	 * table's living arrangment based upon id stage.
	 * 
	 * @param serviceReqHeaderDto
	 * @param idStage
	 * @param cdPalCloseLivArr
	 * @
	 */
	public void updatePal(ServiceReqHeaderDto serviceReqHeaderDto, Long idStage, String cdPalCloseLivArr);

	/**
	 * Method Name: getIdStagePersonLinkData Method Description:
	 * 
	 * @param idStage
	 * @return @
	 */
	public StagePersonLinkDto getIdStagePersonLinkData(Long idStage);

	/**
	 * Method Name: updateEventStatus Method Description: This DAM will update
	 * the CD EVENT STATUS for a row in the EVENT table given CD EVENT TYPE and
	 * the ID STAGE. This DAM will change all events for the stage with the CD
	 * EVENT TYPE specified. It will change the CD EVENT STATUS to the status
	 * specified in the input.
	 * 
	 * @param cdEventType
	 * @param cdEventStatus
	 * @param idStage
	 * @
	 */
	public void updateEventStatus(String cdEventType, String cdEventStatus, Long idStage);

	/**
	 * Method Name: getPALCoordinatorID Method Description: Return the IdPerson
	 * for the PAL Coordinator assigned to a Stage.
	 * 
	 * @param idStage
	 * @return @
	 */
	public Long getPALCoordinatorID(Long idStage);

	/**
	 * Method Name: updatePALSummary Method Description: Updates the value of
	 * cdPalCloseLivArr as null while reopening a stage
	 * 
	 * @param cReqFunc
	 * @param idStage
	 * @param cdPalCloseLivArr
	 */
	public PalInformationDto updatePALSummary(String cReqFunc, Long idStage, String cdPalCloseLivArr,
			PalInformationDto palInformationDto);

	/**
	 * Method Name: saveIlsAssessment Method Description: Updates,Deletes or
	 * Saves the Pal Table based on the reqFunctionCd value
	 * 
	 * @param palInformationDto
	 * @param reqFunctionCd
	 */
	public PalInformationDto saveIlsAssessment(PalInformationDto palInformationDto, String reqFunctionCd);

	/**
	 * Method Name: updtPalService Method Description:Add ,update or delete
	 * PalService entity
	 * 
	 * @param idStage
	 */

	PalServiceTrainingDto updtPalService(UpdtPalServiceTrainingReq updtPalServiceTrainingReq);

}
