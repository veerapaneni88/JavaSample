package us.tx.state.dfps.service.cps.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.cps.service.CpsClosingSummaryService;
import us.tx.state.dfps.service.forms.dto.CpsClosingSummaryDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CpsClosingSummaryPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: To
 * implement the operations of CpsClosingSummaryService Jan 27, 2018- 1:34:16 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class CpsClosingSummaryServiceImpl implements CpsClosingSummaryService {

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private ContactSearchDao contactSearchDao;

	@Autowired
	private CpsClosingSummaryPrefillData cpsClosingSummaryPrefillData;

	/**
	 * Service Name: CSVC22S Method Name: getCpsClosingSummaryData Method
	 * Description: This service will get forms populated by receiving idStage
	 * from controller, then retrieving data from caps_case, stage, contact
	 * tables to get the forms populated.
	 *
	 * @param idStage
	 * @return PreFillDataServiceDto @ the service exception
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getCpsClosingSummaryData(Long idStage) {

		CpsClosingSummaryDto cpsClosingSummaryDto = new CpsClosingSummaryDto();
		ContactDto contactDto = null;
		List<ContactDto> contactDtoList = null;
		// CSEC02D
		StageCaseDtlDto stageCaseDtlDto = pcaDao.getStageAndCaseDtls(idStage);
		if (!ObjectUtils.isEmpty(stageCaseDtlDto)) {
			String cdStage = stageCaseDtlDto.getCdStage();
			String cdContactType = ServiceConstants.CLOSURE_TYPE_ADO_PAD;
			if (ServiceConstants.CSTAGES_FRE.equals(cdStage) || ServiceConstants.CSTAGES_FSU.equals(cdStage)
					|| ServiceConstants.CSTAGES_FPR.equals(cdStage))
				cdContactType = ServiceConstants.CLOSURE_TYPE_FRE_FSU;
			else if (ServiceConstants.CSTAGES_SUB.equals(cdStage))
				cdContactType = ServiceConstants.CLOSURE_TYPE_SUB;

			// CDYN03D
			ContactListSearchDto contactListSearchDto = new ContactListSearchDto();
			contactListSearchDto.setIdStage(idStage);
			List<String> cdContactTypeList = new ArrayList<>();
			cdContactTypeList.add(cdContactType);
			contactListSearchDto.setCdContactTypeList(cdContactTypeList);
			contactDtoList = contactSearchDao.searchContactListCPSClosingSummary(contactListSearchDto);
			if (!ObjectUtils.isEmpty(contactDtoList))
				contactDto = contactDtoList.get(0);

			cpsClosingSummaryDto.setStageCaseDtlDto(stageCaseDtlDto);
			cpsClosingSummaryDto.setContactDto(contactDto);
		}
		return cpsClosingSummaryPrefillData.returnPrefillData(cpsClosingSummaryDto);
	}
}