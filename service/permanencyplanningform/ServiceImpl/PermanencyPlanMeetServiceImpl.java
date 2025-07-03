package us.tx.state.dfps.service.permanencyplanningform.ServiceImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PermanencyPlanMeetEnglishDto;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PermanencyPlanMeetEnglishPrefillData;
import us.tx.state.dfps.service.permanencyplanningform.Service.PermanencyPlanMeetService;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.ppm.dao.PPMDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PermanencyPlanMeetServiceImpl for Form CSC0600 Permanency
 * Planning Meeting English.Converted CSUB52S Service. Feb 12, 2018- 10:47:35 AM
 * © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PermanencyPlanMeetServiceImpl implements PermanencyPlanMeetService {

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private PPMDao ppmDao;

	@Autowired
	private PersonAddressDao personAddressDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private PermanencyPlanMeetEnglishPrefillData permanencyPlanMeetEnglishPrefillData;

	private static final Logger log = Logger.getLogger(PermanencyPlanMeetServiceImpl.class);

	/**
	 * 
	 * Method Name: getPermanencyPlanTeamMeeting Method Description: Notice for
	 * Permanency Plan Team Meeting Service Service Name : CSUB52S
	 * 
	 * @param PpmReq
	 * @return PreFillDataServiceDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public PreFillDataServiceDto getPermanencyPlanTeamMeeting(PpmReq ppmReq) {
		PermanencyPlanMeetEnglishDto permanencyPlanMeetEnglishDto = new PermanencyPlanMeetEnglishDto();

		// Call CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(ppmReq.getIdStage(),
				ServiceConstants.PRIMARY_CHILD);
		if (null != stagePersonLinkCaseDto && null != stagePersonLinkCaseDto.getNbrPersonAge()) {
			if (stagePersonLinkCaseDto.getNbrPersonAge() >= ServiceConstants.SIXTEEN_NUM) {
				stagePersonLinkCaseDto.setCdPersonDeath(ServiceConstants.SIXTEEN_AND_OVER);
			} else {
				stagePersonLinkCaseDto.setCdPersonDeath(ServiceConstants.UNDER_SIXTEEN);
			}
		}
		permanencyPlanMeetEnglishDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		// *ulIdPerson1 = pCSEC15DOutputRec->ulIdPerson (line 72, output of the
		// CSEC15D is input for CSEC35D)
		// pCSEC35DInputRec->ulIdPerson = ulIdPerson1;

		// Call CSEC35D
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkCaseDto)
				&& !TypeConvUtil.isNullOrEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(stagePersonLinkCaseDto.getIdPerson());
			permanencyPlanMeetEnglishDto.setNameDetailDto(nameDetailDto);
		}

		// Call CLSC03D
		CodesTablesDto codesTablesDto = populateLetterDao
						.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);
		permanencyPlanMeetEnglishDto.setCodesTablesDto(codesTablesDto);
		// call CSES14D
		PptDetailsOutDto pptDetailsOutDto = ppmDao.getPptAddress(ppmReq.getIdPptEvent());
		pptDetailsOutDto.setTmScrTmGeneric1(DateUtils.getTime(pptDetailsOutDto.getDtPptDate()));
		permanencyPlanMeetEnglishDto.setPptDetailsOutDto(pptDetailsOutDto);
		// call CSES40D
		PPTParticipantDto pptParticipantDto = ppmDao.getParticipantData(ppmReq.getIdPptPart());
		setNameDetails(pptParticipantDto);
				
		// Call CSEC35D - Set first name into full name for pptParticipantDto result
		if (!ObjectUtils.isEmpty(ppmReq.getIdPerson()) && ServiceConstants.ZERO != ppmReq.getIdPerson()) {
			NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(ppmReq.getIdPerson());
			permanencyPlanMeetEnglishDto.setNmChangeNameDetailDto(nameDetailDto);
			if (!ObjectUtils.isEmpty(nameDetailDto)) {
				pptParticipantDto.setNmPptPartFull(nameDetailDto.getNmNameFirst());
			}
		}
		permanencyPlanMeetEnglishDto.setPptParticipantDto(pptParticipantDto);
		// Call CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(ppmReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		permanencyPlanMeetEnglishDto.setStagePersonDto(stagePersonDto);

		// Call CCMN96D
		if (!ObjectUtils.isEmpty(ppmReq.getIdPerson()) && ServiceConstants.ZERO != ppmReq.getIdPerson()) {
			AddressDtlReq addressDtlReq = new AddressDtlReq();
			addressDtlReq.setUlIdPerson(ppmReq.getIdPerson());
			List<AddressDto> addressList = personAddressDao.getAddressList(addressDtlReq);
			permanencyPlanMeetEnglishDto.setAddressDtoList(addressList);
		}

		// CALL CSEC01D get the information of the Primary worker. the id person
		// from (*ulIdPerson2 = pCCMN19DOutputRec->ulIdTodoPersWorker)
		// CCMN19D is used as input to this DAM to fetch the info for
		// Primary(pCSEC01DInputRec->ulIdPerson = ulIdPerson2)

		EmployeePersPhNameDto employeePersPhNameDto = employeeDao
				.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
		permanencyPlanMeetEnglishDto.setEmployeePersPhNameDto(employeePersPhNameDto);

		log.info("exiting PermanencyPlanMeetServiceImpl and entering PRefill Data Method");
		return permanencyPlanMeetEnglishPrefillData.returnPrefillData(permanencyPlanMeetEnglishDto);
	}

	private void setNameDetails(PPTParticipantDto pptParticipantDto) {
		String[] nameSplit = pptParticipantDto.getNmPptPartFull().split(ServiceConstants.REGEX_WHITESPACE_COMMA);
		if (!TypeConvUtil.isNullOrEmpty(nameSplit) && nameSplit.length >= ServiceConstants.Three) {
			pptParticipantDto.setNmLast(nameSplit[ServiceConstants.Zero]);
			pptParticipantDto.setNmFirst(nameSplit[ServiceConstants.One]);
			pptParticipantDto.setNmMiddle(nameSplit[ServiceConstants.TWO_INT]);
		}
	}
}
