/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 11, 2017- 5:32:37 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.casemanagement.dto.ExternalDocumentationDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.service.casemanagement.dao.ExternalDocumentDao;
import us.tx.state.dfps.service.casemanagement.service.ExternalDocumentService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ExternalDocumentationAUDReq;
import us.tx.state.dfps.service.common.response.ExternalDocumentationRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 11, 2017- 5:32:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class ExternalDocumentServiceImpl implements ExternalDocumentService {

	@Autowired
	CheckStageEventStatusService checkStageEventStatusServiceImpl;

	@Autowired
	ExternalDocumentDao externalDocumentDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.casemanagement.service.ExternalDocumentService#
	 * isRiskIndicated(us.tx.state.dfps.service.common.request.
	 * ExternalDocumentationAUDReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long externaldocumentationAUD(ExternalDocumentationAUDReq externalDocumentationAUDReq) {
		InCheckStageEventStatusDto dtoObj = new InCheckStageEventStatusDto();
		Long result = 0L;
		dtoObj.setCdReqFunction(externalDocumentationAUDReq.getReqFuncCd());
		dtoObj.setIdStage(externalDocumentationAUDReq.getIdStage());
		dtoObj.setCdTask(externalDocumentationAUDReq.getCdTask());
		Boolean eventStatus = checkStageEventStatusServiceImpl.chkStgEventStatus(dtoObj);
		if (eventStatus) {
			result = externalDocumentDao.externaldocumentationAUD(
					externalDocumentationAUDReq.getExternalDocumentationDto(),
					externalDocumentationAUDReq.getReqFuncCd(), externalDocumentationAUDReq.getIdExtDocSelected());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.casemanagement.service.ExternalDocumentService#
	 * fetchExternaldocumentation(us.tx.state.dfps.service.common.request.
	 * CommonHelperReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ExternalDocumentationRes fetchExternaldocumentation(CommonHelperReq commonHelperReq) {
		ExternalDocumentationRes response = new ExternalDocumentationRes();
		List<ExternalDocumentationDto> externalDocList = externalDocumentDao
				.fetchExternaldocumentation(commonHelperReq.getIdCase());
		response.setExternalDocumentationAUDDto(externalDocList);
		response.setDtCaseOpened(externalDocumentDao.getIntakeDate(commonHelperReq.getIdCase()));
		return response;
	}
}
