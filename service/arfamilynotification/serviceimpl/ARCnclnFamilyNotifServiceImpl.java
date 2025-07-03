package us.tx.state.dfps.service.arfamilynotification.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ARCnclnFamilyDto;
import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyFamilyAssmtFormDto;
import us.tx.state.dfps.service.arfamilynotification.dao.ARCnclnFamilyNotifDao;
import us.tx.state.dfps.service.arfamilynotification.service.ARCnclnFamilyNotifService;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.casepackage.dao.CaseDao;
import us.tx.state.dfps.service.casepackage.dto.CaseInfoDto;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ARCnclnFamilyNotifPrefillData;
import us.tx.state.dfps.service.forms.util.ARSafetyFamilyAssmtFormPrefillData;
import us.tx.state.dfps.service.person.dao.AbcsRecordsCheckDao;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.workload.dao.AddressDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<ServiceImpl
 * class for ARCnclnFamilyNotifService> Apr 5, 2018- 11:28:42 AM © 2017 Texas
 * Department of Family and Protective Services
 * ********Change History**********
 * 02/07/2023 thompswa artf238090 PPM 73576 add getConclusionNotifctnInfo.
 */
@Service
@Transactional
public class ARCnclnFamilyNotifServiceImpl implements ARCnclnFamilyNotifService {

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	EmployeeDao empolyeeDao;

	@Autowired
	AddressDao addressDao;

	@Autowired
	CaseDao caseDao;

	@Autowired
	ARCnclnFamilyNotifPrefillData aRCnclnFamilyNotifPrefillData;

	@Autowired
	ARCnclnFamilyNotifDao aRCnclnFamilyNotifDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	AbcsRecordsCheckDao abcsRecordsCheckDao;

	@Autowired
	ARSafetyFamilyAssmtFormPrefillData aRSafetyFamilyAssmtFormPrefillData;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	ArReportDao arSafetyAssmtDao;
	
	@Autowired StageDao stageDao;

	private static final String INITIAL = "INITIAL";
	
	private static final String CLOSURE = "CLOSURE";

	public ARCnclnFamilyNotifServiceImpl() {

	}

	/**
	 * Service Name: arfanot Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then populate
	 * arfanot/arfanots(spanish version) form on person detail/contact detail
	 * page
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getARCnclnFamilyNotif(PopulateFormReq populateFormReq) {

		// fetch person addresses
		ARCnclnFamilyDto arDto = new ARCnclnFamilyDto();
		PersonDto personDto = aRCnclnFamilyNotifDao.getPersonAddrInfo((populateFormReq.getIdPerson()));
		arDto.setPerson(personDto);

		// fetch Addresses address
		List<AddressValueDto> addressDtoList = addressDao.fetchCurrentPrimaryAddressList(populateFormReq.getIdPerson());
		// Fetching AddressList - Warranty Defect 10792
		if(!ObjectUtils.isEmpty(addressDtoList))
		{
			arDto.setAddressValueDto(addressDtoList.get(0));
		}
		

		// retrieves staff mail code and phone and adds business getStaffAddress
		EmployeePersPhNameDto employeeDto = empolyeeDao.searchPersonPhoneName(populateFormReq.getIdWorker());
		arDto.setEmployeePersPhNameDto(employeeDto);

		// retrieves stage start date
		StageDto stageDto = stageDao.getStageById(populateFormReq.getIdStage());
		
		arDto.setStageDto(stageDto);

		// set request idCase into arDto
		arDto.setIdCase(populateFormReq.getIdCase());

		// set closureReason into arDto
		arDto.setClosureReason(populateFormReq.getArClosureReason());

		// get worker title
		WorkerDetailDto workerDto = disasterPlanDao.getWorkerInfoById(populateFormReq.getIdPerson());
		arDto.setWorkerDto(workerDto);

		// EmployeePersonDto getStaffContactInfo
		EmployeePersonDto employeePersonDto = abcsRecordsCheckDao.getStaffContactInfo(populateFormReq.getIdWorker());
		arDto.setEmployeePersonDto(employeePersonDto);

		// get ind involved parent
		ConclusionNotifctnInfo conclusionNotifctnInfo =  aRCnclnFamilyNotifDao.getConclusionNotifctnInfo(populateFormReq.getIdStage(), populateFormReq.getIdPerson());
		arDto.setConclusionNotifctnInfo(conclusionNotifctnInfo);

		return aRCnclnFamilyNotifPrefillData.returnPrefillData(arDto);
	}

	/**
	 * Service Name: arsafna Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then populate
	 * arsafna form on A-R Safety Assessment detail page
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getARSafetyFamilyAssmtForm(PopulateFormReq populateFormReq) {

		ARSafetyFamilyAssmtFormDto arFormDto = new ARSafetyFamilyAssmtFormDto();
		if (!ObjectUtils.isEmpty(populateFormReq.getArClosureReason()) && populateFormReq.getArClosureReason().equalsIgnoreCase("I")) {
			arFormDto.setIndAssmentStatus(INITIAL);
		} else {
			arFormDto.setIndAssmentStatus(CLOSURE);
		}

		CaseInfoDto caseInfo;
		caseInfo = getCaseDetails(populateFormReq.getIdStage());
		arFormDto.setCaseInfoDto(caseInfo);

		// Populate Values for the TITLE_CASE_NAME and TITLE_CASE_NUMBER
		arFormDto.setIdCase(populateFormReq.getIdCase());
		arFormDto.setNmCase(caseUtils.getNmCase(populateFormReq.getIdCase()));

		/**
		 * GET BOTH ASSESSMENTS AND DISPLAY INITIAL LEGACY SAFETY ASSESSMENT
		 */
		List<ARSafetyAssmtValueDto> arSaVb = getSafetyAssessments(populateFormReq.getIdStage());

		arFormDto.setaRSafetyAssmtValueDto(arSaVb);

		return aRSafetyFamilyAssmtFormPrefillData.returnPrefillData(arFormDto);

	}

	/**
	 * get case details.
	 */
	private CaseInfoDto getCaseDetails(Long idStage) {
		CaseInfoDto caseInfo;

		caseInfo = caseDao.getCaseInfo(idStage);
		if (!ObjectUtils.isEmpty(caseInfo)) {
			// SIR 1032586 if form was launched from FPR...
			if ("FPR".equals(caseInfo.getCdStage())) {

				Long priorStage = caseUtils.fetchPriorStage(idStage);
				if (0 < priorStage) {
					// Get case details for the A-R stage prior to the FPR.
					caseInfo = caseDao.getCaseInfo(priorStage);
				} // end if ( priorStage != null )
			}
		}
		return caseInfo;
	}

	/**
	 * Returns safety assessments list from ARSafetyAssmtDAO.
	 *
	 * @param input
	 *            Long stageId
	 * @return output ArrayList<ARSafetyAssmtValueBean> arSafetyAssmts
	 */
	@SuppressWarnings("null")
	private List<ARSafetyAssmtValueDto> getSafetyAssessments(Long idStage) {
		List<ARSafetyAssmtValueDto> arSafetyAssmts = new ArrayList<ARSafetyAssmtValueDto>();
		List<ARSafetyAssmtAreaValueDto> safety_areas = new ArrayList<ARSafetyAssmtAreaValueDto>();
		List<ARSafetyAssmtFactorValueDto> arSaFactorVb = new ArrayList<ARSafetyAssmtFactorValueDto>();
		String INITIAL = "I";
		String CLOSURE = "C";

		arSafetyAssmts = arSafetyAssmtDao.getArSafetyAssmtsByStage(idStage);
		safety_areas = arSafetyAssmtDao.getArSafetyAssmtAreasAll(idStage);
		arSaFactorVb = arSafetyAssmtDao.getArSafetyFactorsByStage(idStage);

		if (null != arSafetyAssmts && 0 < arSafetyAssmts.size()) {
			String assmtType = INITIAL; // Initial type is first of up to 2
										// assmts
			for (ARSafetyAssmtValueDto safetyAssmt : arSafetyAssmts) {

				// add the area list to the assmt bean
				List<ARSafetyAssmtAreaValueDto> areaList = new ArrayList<ARSafetyAssmtAreaValueDto>();

				if (null != safety_areas && 0 < safety_areas.size()) {
					for (ARSafetyAssmtAreaValueDto safetyArea : safety_areas) {
						if (safetyAssmt.getIndAssmtType().equals(assmtType)
								&& safetyAssmt.getIndAssmtType().equals(safetyArea.getIndAssmtType())) {
							// SIR 1037776 add the factor list to the safety
							// area bean to be used in getFactor()
							List<ARSafetyAssmtFactorValueDto> factorList = new ArrayList<ARSafetyAssmtFactorValueDto>();
							if (null != arSaFactorVb && 0 < arSaFactorVb.size()) {
								for (ARSafetyAssmtFactorValueDto factor : arSaFactorVb) {
									if (safetyAssmt.getIndAssmtType().equals(assmtType)
											&& safetyAssmt.getIndAssmtType().equals(factor.getIndAssmtType())
											&& safetyArea.getIdArea().equals(factor.getIdArea())) {
										factorList.add(factor);
									}
								}
								safetyArea.setaRSafetyAssmtFactors(factorList);
								areaList.add(safetyArea);
							}
						}
					}
				}
				safetyAssmt.setaRSafetyAssmtAreas(areaList);

				// add the factor list to the assmt bean
				ArrayList<ARSafetyAssmtFactorValueDto> factorList = new ArrayList<ARSafetyAssmtFactorValueDto>();

				if (null != arSaFactorVb && 0 < arSaFactorVb.size()) {
					for (ARSafetyAssmtFactorValueDto factor : arSaFactorVb) {
						if (safetyAssmt.getIndAssmtType().equals(assmtType)
								&& safetyAssmt.getIndAssmtType().equals(factor.getIndAssmtType())) {
							factorList.add(factor);
						}
					}
				}
				safetyAssmt.setaRSafetyAssmtFactors(factorList);
				assmtType = CLOSURE; // Second assmt is Closure type
			}
		}
		return arSafetyAssmts;
	}

}
