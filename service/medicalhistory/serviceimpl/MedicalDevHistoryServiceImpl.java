package us.tx.state.dfps.service.medicalhistory.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.MedicalDevHistoryReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.MedicalDevHistoryPrefillData;
import us.tx.state.dfps.service.medicalhistory.dto.MedicalDevHistoryDto;
import us.tx.state.dfps.service.medicalhistory.service.MedicalDevHistoryService;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description:
 * MedicalDevHistoryServiceImpl will implemented the operation defined in
 * MedicalDevHistoryService Interface related to Medical and Developmental
 * History. Jan 29, 2018 - 11:40:29 AM
 */
@Service
@Transactional
public class MedicalDevHistoryServiceImpl implements MedicalDevHistoryService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private MedicalDevHistoryPrefillData medicalDevHistoryPrefillData;

	private static final Logger logger = Logger.getLogger(MedicalDevHistoryServiceImpl.class);

	/**
	 * Method Description: This method is used to retrieve the Medical and
	 * Developmental History form. This form fully documents the Medical and
	 * Developmental History of the child by passing IdStage
	 * 
	 * @param MedicalDevHistoryReq
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getMedicalDevHistory(MedicalDevHistoryReq medicalDevHistoryReq) {
		MedicalDevHistoryDto medicalDevHistoryDto = new MedicalDevHistoryDto();
		// Call CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(medicalDevHistoryReq.getIdStage(), ServiceConstants.PRIMARY_CHILD);
		medicalDevHistoryDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		logger.info("TransactionId :" + medicalDevHistoryReq.getTransactionId());
		return medicalDevHistoryPrefillData.returnPrefillData(medicalDevHistoryDto);
	}

}
