package us.tx.state.dfps.service.extreq.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.service.casemanagement.dao.FetchIncomingFacilityDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;
import us.tx.state.dfps.service.common.request.ExtreqReq;
import us.tx.state.dfps.service.extreq.ApsextreqDto;
import us.tx.state.dfps.service.extreq.ExtreqDto;
import us.tx.state.dfps.service.extreq.dao.ExtreqDao;
import us.tx.state.dfps.service.extreq.service.ExtreqService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ExtreqPrefillData;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSVC27s
 * Extension Request Mar 15, 2018- 11:06:51 AM © 2017 Texas Department of Family
 * and Protective Services
 */
@Service
@Transactional
public class ExtreqServiceImpl implements ExtreqService {

	@Autowired
	private ExtreqDao extreqDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private AllegtnDao allegtnDao;

	@Autowired
	private FetchIncomingFacilityDao fetchIncomingFacilityDao;

	@Autowired
	private ExtreqPrefillData extreqPrefillData;
	
	@Autowired
	private CaseSummaryDao caseSummaryDao;

	public ExtreqServiceImpl() {

	}

	/**
	 * Method Name: getExtreq Method Description: CSVC27s Extension Request
	 * populates form extreq
	 * 
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getExtreq(ExtreqReq extreqReq) {

		/*
		 * Creation of Dto Instances for ADULT PROTECTIVE SERVICES EXTENSION
		 * REQUEST FORM
		 */
		ApsextreqDto apsextreqDto = new ApsextreqDto();
		RetreiveIncomingFacilityInputDto retreiveIncomingFacilityInputDto = new RetreiveIncomingFacilityInputDto();
		RetreiveIncomingFacilityOutputDto retreiveIncomingFacilityOutputDto = new RetreiveIncomingFacilityOutputDto();
		PriorStageDto priorStageDto = new PriorStageDto();

		/*
		 * Extracting the Data for CSVC27s
		 */

		ExtreqDto extreqDto = extreqDao.getExtreqInfo(extreqReq.getIdEvent());
		apsextreqDto.setExtreqDto(extreqDto);
		apsextreqDto.setIdCase(extreqReq.getIdCase());

		/*
		 * Extracting the Person Served Data for ADULT PROTECTIVE SERVICES
		 * EXTENSION REQUEST FORM
		 */
		List<AllegationDetailDto> allegationDetailDto = allegtnDao.getInvAdminStageAllegn(extreqReq.getIdStage());
		apsextreqDto.setAllegationDetailDto(allegationDetailDto);

		/*
		 * Extracting the Facility Name Data for ADULT PROTECTIVE SERVICES
		 * EXTENSION REQUEST FORM
		 */

		priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(extreqReq.getIdStage());
		retreiveIncomingFacilityInputDto.setIdStage(priorStageDto.getIdPriorStage());
		fetchIncomingFacilityDao.fetchIncomingFacility(retreiveIncomingFacilityInputDto,
				retreiveIncomingFacilityOutputDto);
		apsextreqDto.setTxtFacilityName(retreiveIncomingFacilityOutputDto.getNmIncmgFacilName());
		
		//To Fetch the List of Facility/Provider's Name
		List<MultiAddressDto> multiAddressDtoList = notifToLawEnforcementDao
				.getMultiAddress(extreqReq.getIdStage(), extreqReq.getIdCase());
		apsextreqDto.setMultiAddressDtoList(multiAddressDtoList);		

		//ALM ID: 93062 : use case in take date for Allegation Received date
		CaseSummaryDto caseSummary = caseSummaryDao.getCaseInfo(extreqReq.getIdCase());
		apsextreqDto.setDtCaseOpened(caseSummary.getDtCaseOpened());
		
		return extreqPrefillData.returnPrefillData(apsextreqDto);
	}

}
