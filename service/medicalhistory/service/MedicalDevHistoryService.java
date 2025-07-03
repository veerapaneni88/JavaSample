package us.tx.state.dfps.service.medicalhistory.service;

import us.tx.state.dfps.service.common.request.MedicalDevHistoryReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description: MedicalDevHistoryService
 * will have the operation which are mapped to Medical and Developmental
 * History. Jan 29, 2018 - 11:40:29 AM
 */
public interface MedicalDevHistoryService {

	/**
	 * Method Description: This method is used to retrieve the Medical and
	 * Developmental History form. This form fully documents the Medical and
	 * Developmental History by passing IdStage as input request
	 * 
	 * @param MedicalDevHistoryReq
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getMedicalDevHistory(MedicalDevHistoryReq medicalDevHistoryReq);
}
