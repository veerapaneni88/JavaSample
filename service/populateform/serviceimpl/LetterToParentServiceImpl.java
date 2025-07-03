package us.tx.state.dfps.service.populateform.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ConclusionNotifctnInfo;
import us.tx.state.dfps.common.domain.SchoolInvNotifctnVctm;
import us.tx.state.dfps.common.dto.ClosureNoticeListDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.casemanagement.dao.CPSInvCnlsnDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CpsInvNoticesRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinv.dto.StagePersonValueDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.cpsinvstsummary.dao.CpsInvstSummaryDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LetterToParentPrefillData;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonDetailDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.LetterToParentDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.populateform.dao.LetterToParentDao;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateform.service.LetterToParentService;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Modernized
 * service CINV32S May 29, 2018- 1:35:56 PM Â© 2017 Texas Department of Family
 * and Protective Services
 * ********Change History**********
 * 01/02/2023 thompswa artf238090 PPM 73576 add getAllegationsList, getConclusionNotifctnInfoList. Dev defects:artf244344
 */
@Service
@Transactional
public class LetterToParentServiceImpl implements LetterToParentService {

	private static final Logger logger = Logger.getLogger(LetterToParentServiceImpl.class.getName());

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private NameDao nameDao;

	@Autowired
	private StageProgDao stageProgDao;

	@Autowired
	private StageSituationDao stageSituationDao;

	@Autowired
	private PersonAddressDao personAddressDao;

	@Autowired
	private CpsInvstSummaryDao cpsInvstSummaryDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private LetterToParentDao letterToParentDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private LetterToParentPrefillData prefillData;

	@Autowired
	CPSInvCnlsnDao cnlsnDao;

	@Autowired
	CpsInvReportDao cpsInvReportDao;

	@Autowired
	PersonDetailDao personDetailDao;


	/**
	 * Method Name: getParentLetter Method Description: Makes DAO calls and
	 * returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
public PreFillDataServiceDto getParentLetter(PopulateFormReq req) {
		// Initialize main DTO and global variables
		LetterToParentDto prefillDto = new LetterToParentDto();
		Long idCase = ServiceConstants.ZERO;
		Boolean openStage = ServiceConstants.FALSEVAL;
		Long idPerson = ServiceConstants.ZERO;
		List<Long> possiblePerpsList = new ArrayList<>();
		List<CpsInvAllegDto> getAllegationsList = new ArrayList<CpsInvAllegDto>(); // artf238090
		Boolean cfiv3000 = false;
		if (CodesConstant.CCNTCTYP_APGN.equalsIgnoreCase(req.getFormName())
			|| CodesConstant.CONNOTTY_CFIV3000.equalsIgnoreCase(req.getFormName())) cfiv3000 = true;

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(req.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			idCase = genericCaseInfoDto.getIdCase();
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		}

		// CLSC03D
		List<CodesTablesDto> codesTablesList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
				ServiceConstants.NAME);
		prefillDto.setCodesTablesList(codesTablesList);

		// CCMN40D
		EmpNameDto empNameDto = nameDao.getNameByPersonId(req.getIdPerson());
		prefillDto.setEmpNameDto(empNameDto);

		if (StringUtils.isNotBlank(genericCaseInfoDto.getCdStageReasonClosed())) {
			// CCMNB8D
			List<StageProgDto> stageProgList = stageProgDao.getStgProgroession(genericCaseInfoDto.getCdStage(),
					genericCaseInfoDto.getCdStageProgram(), genericCaseInfoDto.getCdStageReasonClosed());

			if (!ObjectUtils.isEmpty(stageProgList)
					&& (ServiceConstants.SUBCAREPP.equals(stageProgList.get(0).getCdStageProgRsnClose())
							|| ServiceConstants.RSUBCARE.equals(stageProgList.get(0).getCdStageProgRsnClose()))) {
				// CLSS30D
				StageSituationInDto inputDto = new StageSituationInDto();
				inputDto.setIdCase(idCase);
				List<StageSituationOutDto> stageDetailsList = stageSituationDao.getStageDetails(inputDto);
				if (!ObjectUtils.isEmpty(stageDetailsList)) {
					for (StageSituationOutDto stageDetailDto : stageDetailsList) {
						if (!ServiceConstants.WILL_NOT_STAGE.equals(stageDetailDto.getCdStage())
								&& ObjectUtils.isEmpty(stageDetailDto.getDtStageClose())) {
							openStage = ServiceConstants.TRUEVAL;
							break;
						}
					}
					prefillDto.setStageDetailsList(stageDetailsList);

					if (openStage) {
						stageProgList.get(0).setIndStageProgClose(ServiceConstants.STR_ONE_VAL);
					} else {
						stageProgList.get(0).setIndStageProgClose(ServiceConstants.STR_ZERO_VAL);
					}
				}
			}
			prefillDto.setStageProgList(stageProgList);
		}

		// CCMN96D
		AddressDtlReq addressReq = new AddressDtlReq();
		addressReq.setUlIdPerson(req.getIdPerson());
		List<AddressDto> addressList = personAddressDao.getAddressList(addressReq);
		if (!ObjectUtils.isEmpty(addressList)) {
			prefillDto.setAddressDto(addressList.get(0));
		}

		// CINV95D
		List<CpsInvstDetailDto> cpsInvstDetailList = cpsInvstSummaryDao.getCpsInvstDetail(req.getIdStage());
		if (!ObjectUtils.isEmpty(cpsInvstDetailList)) {
			prefillDto.setCpsInvstDetailDto(cpsInvstDetailList.get(0));
		}

		/**
		 * populate Allegation Detail section.
		 * all the allegations are being shown irrespective of alleged perpetrator present or not.
		 * artf238090 getting from cpsInvReportDao for consistency
		 */

		getAllegationsList = cpsInvReportDao.getAllegationsSafe(req.getIdStage(), cfiv3000);
		List<ConclusionNotifctnInfo> conclusionNotifctnInfoList
				= letterToParentDao.getConclusionNotifctnInfoList(req.getIdStage(), req.getIdPerson());
		for (ConclusionNotifctnInfo info : conclusionNotifctnInfoList) {
			//In Person merge get the forward person id, in case if we have a closed person id
			info.getSchoolInvNotifctnVctmCollection().forEach(sc -> {
				Long fwdPersonId = personDetailDao.getForwardPersonInMerge(sc.getIdVctm().longValue());
				if (fwdPersonId != 0L) {
					sc.setIdVctm(BigDecimal.valueOf(fwdPersonId));
				}
			});
		}
		// victim list isEmpty implies approved prior to ppm73576 ==> show all
		if (!conclusionNotifctnInfoList.isEmpty()
				&& !conclusionNotifctnInfoList.get(0).getSchoolInvNotifctnVctmCollection().isEmpty()) {

			/**
			 * artf133215 - 13747 If there are more than 2 allegation victims present in allegation section,
			 * and if only one victim is selected while generating the letters,
			 * then in the form we should display only selected victim
			 * artf238090 no need to filter when victim list is not > 1.
			 */
			boolean bVictim = false;
			if (1 < getAllegationsList.size()) {
				if (!conclusionNotifctnInfoList.isEmpty()
						&& ! conclusionNotifctnInfoList.get(0).getSchoolInvNotifctnVctmCollection().isEmpty())
					bVictim = true;
			} //  artf238090 filter when bVictim=true for letter recipient even if not school inv
			if (bVictim && !cpsInvstDetailList.isEmpty() && !getAllegationsList.isEmpty() && 1 < getAllegationsList.size()) {
				List<CpsInvAllegDto> allegationsList = new ArrayList<CpsInvAllegDto>();
               // artf244344 get the children selected for req.getIdPerson, hpalm21776
				for (ConclusionNotifctnInfo closureInfo : conclusionNotifctnInfoList) {
					// get the Collection into List
					List<Object> listObject = Arrays.asList(closureInfo.getSchoolInvNotifctnVctmCollection().toArray());
					List<SchoolInvNotifctnVctm> vctmList = new ArrayList<SchoolInvNotifctnVctm>();
					for (Object x : listObject) {
						vctmList.add((SchoolInvNotifctnVctm) x);
					}
					for (SchoolInvNotifctnVctm victim : vctmList) {
						for (CpsInvAllegDto cpsInvAllegDto : getAllegationsList) {
							if (cpsInvAllegDto.getIdVictim().equals(victim.getIdVctm().longValue())) {
								allegationsList.add(cpsInvAllegDto);
							}
						}
					}
				}
				getAllegationsList = allegationsList;
			}
		}
		for( CpsInvAllegDto allegDto : getAllegationsList) {
			if (!cfiv3000) // convert spanish characters to html
				allegDto.setDecodeAllegType(TypeConvUtil.formatSpanish(allegDto.getDecodeAllegType()));
		}
		prefillDto.setAllegationsList(getAllegationsList);

		// CLSCE6D ECI indicator early childhood intervention
		String indVictim = letterToParentDao.victimIndicator(req.getIdStage());
		prefillDto.setIndVictim(indVictim);
		List<FacilityAllegationInfoDto> perpsList = new ArrayList<>();
		// CLSCE8D - for TMPLAT_ROLE_REMOVAL
		List<FacilityAllegationInfoDto> perpsRemovalList = letterToParentDao.getPerpsInStage(req.getIdPerson(),
				req.getIdStage());
		if (!ObjectUtils.isEmpty(perpsRemovalList)) {
			for (FacilityAllegationInfoDto perpDto : perpsRemovalList) {
				// to be passed to CLSCE7D
				possiblePerpsList.add(perpDto.getIdPerson());

				// concat suffix to last name
				String lastName = StringUtils.isNotBlank(perpDto.getNmPersonLast()) ? perpDto.getNmPersonLast()
						: ServiceConstants.EMPTY_STR;
				if (StringUtils.isNotBlank(perpDto.getCdPersonSuffix())) {
					lastName += ServiceConstants.SPACE;
					String suffix;
					switch (perpDto.getCdPersonSuffix()) {
					case ServiceConstants.CSUFFIX_2N:
						suffix = ServiceConstants.SECOND;
						break;
					case ServiceConstants.CSUFFIX_3R:
						suffix = ServiceConstants.THIRD;
						break;
					case ServiceConstants.CSUFFIX_4T:
						suffix = ServiceConstants.FOURTH;
						break;
					case ServiceConstants.CSUFFIX_5T:
						suffix = ServiceConstants.FIFTH;
						break;
					case ServiceConstants.JR:
						suffix = ServiceConstants.JUN;
						break;
					case ServiceConstants.SR:
						suffix = ServiceConstants.SEN;
						break;
					case ServiceConstants.CSUFFIX_MD:
						suffix = ServiceConstants.CSUFFIX_MD;
						break;
					case ServiceConstants.CSUFFIX_PHD:
						suffix = ServiceConstants.PHD;
						break;
					default:
						suffix = ServiceConstants.EMPTY_STR;
					}
					lastName += suffix;
				}
				perpDto.setNmPersonLast(lastName);

			}

			// CLSCE7D
			List<AllegationWithVicDto> ruledOutStageList = new ArrayList<>();
			//artf189470: Right to Request Role Removal in letter to Parent/Guardian: the Right to Request Role description will only be
			// displayed when all allegations against the person for which the letter is being generated are Ruled Out on the Allegation List
			// of the stage.

			for (FacilityAllegationInfoDto perpDto : perpsRemovalList) {
				// Changed the Return Type to List - Warranty Defect 10787
				if(perpDto.getIdPerson().equals(req.getIdPerson())) {
					List<AllegationWithVicDto> allegDtoList = populateFormDao.getUqStageById(req.getIdStage(), perpDto.getIdPerson());

					//artf251969 : Role Removal Paragraph should be shown
					// only when all allegations are ruled out for this perpetrator
					if (!CollectionUtils.isEmpty(allegDtoList)) {
						AllegationWithVicDto allegationWithVicDto = allegDtoList.get(0);
						if (allegDtoList.stream().allMatch(d -> ServiceConstants.RULED_OUT.equals(d.getAlgCdAllegDisposition()))) {
							allegationWithVicDto.setCdOverAllDisposition(ServiceConstants.RULED_OUT);
							perpDto.setCdAllegDisposition(ServiceConstants.RULED_OUT);
							ruledOutStageList.add(allegationWithVicDto);
						}
					}
					perpsList.add(perpDto);
					break;
				}
			}
			prefillDto.setPerpsList(perpsList);
			prefillDto.setPerpsRemovalList(perpsList);
			prefillDto.setRuledOutStageList(ruledOutStageList);
		}

		// CSECE4
		List<StagePersonValueDto> stagePersonValueList = letterToParentDao.getParentInfo(req.getIdPerson(),
				req.getIdStage());
		if (!ObjectUtils.isEmpty(stagePersonValueList)) {
			prefillDto.setStagePersonValueDto(stagePersonValueList.get(0));
		}

		// CLSSA8D
		List<String> uniqueDispositions=prefillDto.getAllegationsList()
				.stream()
				.map(allegation->allegation.getCdAllegDisp())
				.distinct()
				.collect(Collectors.toList());
		List<FacilityAllegationInfoDto> dispositionsList
				= letterToParentDao.getDispositions(uniqueDispositions, cfiv3000);
		for (FacilityAllegationInfoDto infoDto : dispositionsList) {
			if (!cfiv3000) // convert spanish characters to html
				infoDto.setDecodeAllegType(TypeConvUtil.formatSpanish(infoDto.getDecodeAllegType()));
		}
		prefillDto.setDispositionsList(dispositionsList);

		// CLSSA9D
		List<FacilityAllegationInfoDto> allegTypesList
				= letterToParentDao.getAllegTypes(req.getIdStage(), cfiv3000);
		for( FacilityAllegationInfoDto allegDto : allegTypesList) {
			if (!cfiv3000) // convert spanish characters to html
				allegDto.setDecodeAllegType(TypeConvUtil.formatSpanish(allegDto.getDecodeAllegType()));
		}
		prefillDto.setAllegTypesList(allegTypesList);

		// CCMNB9D
		List<StagePersonLinkDto> stagePersonLinkList = stagePersonLinkDao.getStagePersonLinkByIdStage(req.getIdStage());
		if (!ObjectUtils.isEmpty(stagePersonLinkList)) {
			for (StagePersonLinkDto splDto : stagePersonLinkList) {
				if (ServiceConstants.PRIMARY_WORKER_ROLE.equals(splDto.getCdStagePersRole())
						|| ServiceConstants.HISTORICAL_WORKER_ROLE.equals(splDto.getCdStagePersRole())) {
					idPerson = splDto.getIdPerson();
					break;
				}
			}
			prefillDto.setStagePersonLinkList(stagePersonLinkList);

			// CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!ObjectUtils.isEmpty(employeePersPhNameDto)
					&& !(ServiceConstants.BUSINESS_PHONE_CINV63S.equals(employeePersPhNameDto.getCdPhoneType())
							|| ServiceConstants.BUSINESS_CELL_CINV63S.equals(employeePersPhNameDto.getCdPhoneType()))) {
				employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
				employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getNbrMailCodePhoneExt());
			}
			prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}
		//artf251969: CLSCE8D - 2nd call -- removed the code since it is same as first call
		// CINV81D
		PersonGenderSpanishDto personGenderSpanishDto = populateFormDao.isSpanGender(req.getIdPerson());
		prefillDto.setPersonGenderSpanishDto(personGenderSpanishDto);

		// CSES0BD
		List<PersonEmailDto> personEmailList = new ArrayList<>();
		List<PersonEmailDto> perpEmailList = null;
		for(FacilityAllegationInfoDto perpDto : perpsList) {
			if(0l < perpDto.getIdPerson()) perpEmailList = populateFormDao.returnEmailById(perpDto.getIdPerson());
			if(!ObjectUtils.isEmpty(perpEmailList) && 0 < perpEmailList.size()) {
				PersonEmailDto emailDto = perpEmailList.get(0) ;
				emailDto.setIdPerson(perpDto.getIdPerson());
				personEmailList.add(emailDto);
			}
		}
		prefillDto.setPersonEmailList(personEmailList);

		prefillDto.setFormName(req.getFormName());

		return prefillData.returnPrefillData(prefillDto);
	}
}
