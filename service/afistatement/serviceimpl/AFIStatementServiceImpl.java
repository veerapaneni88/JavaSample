package us.tx.state.dfps.service.afistatement.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.afistatement.dto.AFIStatementDto;
import us.tx.state.dfps.service.afistatement.service.AFIStatementService;
import us.tx.state.dfps.service.common.request.AFIStatementReq;
import us.tx.state.dfps.service.contacts.dao.ContactEventPersonDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.AFIStatementPrefillData;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactDetailsOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.StageProgramDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ApsFacilityInvestigationsStatement for form civ39o00 Mar 14, 2018- 9:37:45 AM
 * © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class AFIStatementServiceImpl implements AFIStatementService {

	@Autowired
	ContactEventPersonDao contactEventPersonDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	AFIStatementPrefillData afISTatementPrefillData;

	public AFIStatementServiceImpl() {

	}

	/**
	 * Method Name: getStatement Method Description: Populates form civ39o00
	 * 
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getStatement(AFIStatementReq aFIStatementReq) {

		AFIStatementDto aFIStatementDto = new AFIStatementDto();

		// getGenericCaseInfo (DAm Name : CallCSEC02D) Method
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(aFIStatementReq.getIdStage());

		// call CSYS11D
		ContactDetailsOutDto contactDetailsOutDto = new ContactDetailsOutDto();
		contactDetailsOutDto.setUlIdEvent(aFIStatementReq.getIdEvent().intValue());
		StageProgramDto stageProgramDto = contactEventPersonDao.getContactDetails(contactDetailsOutDto);

		aFIStatementDto.setGenericCaseInfoDto(genericCaseInfoDto);
		aFIStatementDto.setStageProgramDto(stageProgramDto);

		return afISTatementPrefillData.returnPrefillData(aFIStatementDto);
	}

}
