package us.tx.state.dfps.service.caregiver.service;

import us.tx.state.dfps.service.casepackage.dto.MuleEsbResponseDto;
import us.tx.state.dfps.service.common.request.CaregiverAckReq;
import us.tx.state.dfps.service.common.request.CaretakerInformationReq;
import us.tx.state.dfps.service.common.request.CaseSummaryReq;

public interface CaregiverNotificationService {

	public MuleEsbResponseDto muleEsbUploadFile(CaseSummaryReq careTakerInfoReq);

	public MuleEsbResponseDto muleEsbDownloadFile(String nuId, String documentId);

	public MuleEsbResponseDto muleEsbSendAckRequest(CaregiverAckReq caregiverAckReq);

}
