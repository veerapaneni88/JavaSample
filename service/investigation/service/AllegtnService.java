package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.common.request.AllegationAUDReq;
import us.tx.state.dfps.service.common.request.CalOverallDispReq;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;
import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.request.DisplayAllegDtlReq;
import us.tx.state.dfps.service.common.request.InvAllegListReq;
import us.tx.state.dfps.service.common.request.SaveAllgtnMultiReq;
import us.tx.state.dfps.service.common.response.InvAllegListRes;
import us.tx.state.dfps.service.common.response.SaveAllgtnMultiRes;
import us.tx.state.dfps.service.common.response.DisplayAllegDtlRes;
import us.tx.state.dfps.service.common.response.AllegationAUDRes;
import us.tx.state.dfps.service.common.response.CalOverallDispRes;
import us.tx.state.dfps.service.common.response.CommonDateRes;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

import java.util.List;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 10:24:14 AM
 */
public interface AllegtnService {
	InvAllegListRes getAllegations(InvAllegListReq invAllegListReq);

	SaveAllgtnMultiRes saveAllgtnMulti(SaveAllgtnMultiReq saveAllgtnMultiReq);

	DisplayAllegDtlRes diaplyAllegtnDetail(DisplayAllegDtlReq displayAllegDtlReq);

	AllegationAUDRes allegationAUD(AllegationAUDReq allegationAUDReq);

	CalOverallDispRes calOverallDisp(CalOverallDispReq calOverallDispReq);

	ServiceResHeaderDto updateChildSexLaborTrafficking(CommonCaseIdReq commonCaseIdReq);

	CommonDateRes fetchDtIntakeForIdStage(CommonStageIdReq commonStageIdReq);

	void handleDeletion(AllegationAUDReq allegationAUDReq);

	void updtVictimPerpRoles(AllegationDetailDto allegationDetail);


	public boolean getValidAllegations(Long idCase);
	
    CommonCountRes getAllegationProblemCount(Long idAllegation);

	void deleteSPSourcesForAllegationRecord(Long idStage, Long idAllegation);

	List<EventDto> getEventIds(Long idStage, String emrTaskCode);



}
