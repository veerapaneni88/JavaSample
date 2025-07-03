package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CpsInvConclAudiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclAudoDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.SaveEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.StageInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.StageUnitOutDto;
import us.tx.state.dfps.service.common.request.CpsInvNoticesClosureReq;
import us.tx.state.dfps.service.common.request.CpsInvSubstanceReq;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 2:12:26 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CpsInvConclAudService {

	/**
	 * 
	 * Method Name: callCpsInvConclAudService Method Description: This service
	 * updates information modified on the CPS Investigation Conclusion window.
	 * 
	 * @param pInputMsg
	 * @return CpsInvConclAudoDto @
	 */
	public CpsInvConclAudoDto callCpsInvConclAudService(CpsInvConclAudiDto pInputMsg);

	/**
	 * 
	 * Method Name: CallCCMN01U Method Description:
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusInDto @
	 */
	public PostEventStageStatusInDto CallCCMN01U(CpsInvConclAudiDto pInputMsg);

	/**
	 * 
	 * Method Name: CallCINV12D Method Description:
	 * 
	 * @param pInputMsg
	 * @return CpsInvstDetailInsUpdDelInDto @
	 */
	public CpsInvstDetailInsUpdDelInDto CallCINV12D(CpsInvConclAudiDto pInputMsg);

	/**
	 * 
	 * Method Name: CallCSVC18D Method Description:
	 * 
	 * @param pInputMsg
	 * @return StageInsUpdDelInDto @
	 */
	public StageInsUpdDelInDto CallCSVC18D(CpsInvConclAudiDto pInputMsg);

	/**
	 * 
	 * Method Name: CallCLSC59D Method Description:
	 * 
	 * @param pInputMsg
	 * @param list
	 * @return String @
	 */
	public String CallCLSC59D(CpsInvConclAudiDto pInputMsg, List<StageUnitOutDto> list);

	/**
	 * Method Name: saveCpsInvestigationDetails Method Description: Method Saves
	 * the Emergency Assistance Details, CPS Investigation Conclusion Details
	 * and Conclusion Notification Details in single Transaction.
	 * 
	 * @param cpsInvConclAudiDto
	 * @param saveEmergAsssistiDto
	 * @param cpsInvNoticeClosureReq
	 * @return CpsInvConclAudoDto @
	 */
	public CpsInvConclAudoDto saveCpsInvestigationDetails(CpsInvConclAudiDto cpsInvConclAudiDto,
			SaveEmergAssistiDto saveEmergAsssistiDto, CpsInvNoticesClosureReq cpsInvNoticeClosureReq,
														  CpsInvSubstanceReq cpsInvSubstanceReq	);
}
