package us.tx.state.dfps.service.pal.service;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PalInformationReq;
import us.tx.state.dfps.service.common.request.UpdtPalServiceTrainingReq;
import us.tx.state.dfps.service.common.response.PalInformationRes;
import us.tx.state.dfps.service.pal.dto.PalInformationDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 20, 2018- 4:49:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PalInformationService {
	/**
	 * 
	 * Method Name: getPalInfo Method Description:Fetches the Closure
	 * date,Living Arrangement and Closure Reason
	 * 
	 * @param idCase
	 * @param idStage
	 * @param idUser
	 * @return @
	 */
	public PalInformationDto getPalInfo(Long idCase, Long idStage, Long idUser);

	/**
	 * This service is AUD service for PAL record Services/Training
	 * 
	 * @param updtPalServiceTrainingReq
	 * @return ServiceResHeaderDto
	 * @throws InvalidRequestException
	 * @
	 */
	ServiceResHeaderDto updtPalServiceTraining(UpdtPalServiceTrainingReq updtPalServiceTrainingReq);

	/**
	 * Method Name: reopenPALStage Method Description:This service will update
	 * the close date ofr the Stage, Situation and Case tables to null. It will
	 * also set the stage closure reason to null on the STAGE table.
	 * Additionally, the new primary worker will be added t the stage along with
	 * a link to the primary child. Finally, the ILS Assessment and PAL Services
	 * event statuses will be set back to "PROC". When the case is reopened, the
	 * records retention recored must be deleted and the case file management
	 * recored must be updated with the appropriate information.
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException
	 * @
	 */
	public PalInformationDto reopenPALStage(Long idCase, Long idStage, String userLogon, Long idUser);

	/**
	 * Method Name: savePalInformation Method Description:
	 * 
	 * @param palInformationDto
	 * @param palSummaryDto
	 * @param idPerson
	 * @param reqFunctionCd
	 * @return @
	 */

	PalInformationRes savePalInformation(PalInformationReq palInformationReq);

}
