package us.tx.state.dfps.service.permplanspan.serviceimpl;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PpmDto;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PpmPrefillData;
import us.tx.state.dfps.service.permplanspan.service.PermPlanSpanService;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.ppm.dao.PPMDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service for
 * Permanency Planning Feb 21, 2018- 2:52:31 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PermPlanSpanServiceImpl implements PermPlanSpanService {

	@Autowired
	private PpmPrefillData ppmPrefillData;

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	PopulateLetterDao populateLetterDao;

	@Autowired
	PPMDao ppmDao;

	@Autowired
	PopulateFormDao populateFormDao;

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	EmployeeDao employeeDao;

	private static final Logger LOG = Logger.getLogger(PermPlanSpanServiceImpl.class);

	@Override
	public PreFillDataServiceDto getPermPlan(PpmReq ppmReq) {
		PpmDto ppmDto = new PpmDto();
		long idPerson = 0l;

		// CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(ppmReq.getIdStage(),
				ServiceConstants.PRIMARY_CHILD);
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkCaseDto)) {
			idPerson = stagePersonLinkCaseDto.getIdPerson();
			long nbrPersonAge = DateUtils.calculatePersonsAgeInYears(stagePersonLinkCaseDto.getDtPersonBirth(),
					Calendar.getInstance().getTime());
			stagePersonLinkCaseDto.setNbrPersonAge((short) nbrPersonAge);
			if (16 <= nbrPersonAge) {
				stagePersonLinkCaseDto.setCdPersonDeath(ServiceConstants.SIXTEEN_AND_OVER);
			} else {
				stagePersonLinkCaseDto.setCdPersonDeath(ServiceConstants.UNDER_SIXTEEN);
			}
			ppmDto.setStagePersonLinkCase(stagePersonLinkCaseDto);

			// CSEC35D
			ppmDto.setNameDetail(commonApplicationDao.getNameDetails(idPerson));
		}

		// CLSC03D
		ppmDto.setCodesTables(
				populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0));

		// CSES14D
		PptDetailsOutDto pptDetailsOutDto = ppmDao.getPptAddress(ppmReq.getIdPptEvent());
		pptDetailsOutDto.setTmScrTmGeneric1(DateUtils.getTime(pptDetailsOutDto.getDtPptDate()));
		ppmDto.setpPtDetailsOut(pptDetailsOutDto);

		// CSES40D
		PPTParticipantDto pptParticipantDto = ppmDao.getParticipantData(ppmReq.getIdPptPart());
		if (!TypeConvUtil.isNullOrEmpty(pptParticipantDto)) {
			if (!TypeConvUtil.isNullOrEmpty(ppmReq.getIdPerson()) && ServiceConstants.ZERO != ppmReq.getIdPerson()) {
				// CINV81D
				idPerson = ppmReq.getIdPerson();
				ppmDto.setPersonGenderSpanish(populateFormDao.isSpanGender(idPerson));

				// CSEC35D for name parts
				ppmDto.setPartsNameDetail(commonApplicationDao.getNameDetails(idPerson));
				pptParticipantDto.setNmPptPartFull(ppmDto.getPartsNameDetail().getNmNameFirst());

				// CCMN96D
				AddressDtlReq addressDtlReq = new AddressDtlReq();
				addressDtlReq.setUlIdPerson(idPerson);
				ppmDto.setAddressList(personAddressDao.getAddressList(addressDtlReq));
			}
			ppmDto.setpPTParticipant(pptParticipantDto);
		}

		// CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(ppmReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		if (!TypeConvUtil.isNullOrEmpty(stagePersonDto)) {
			// CSEC01D
			idPerson = stagePersonDto.getIdTodoPersWorker();
			EmployeePersPhNameDto empPersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!TypeConvUtil.isNullOrEmpty(empPersPhNameDto)
					&& (ServiceConstants.BUSINESS_PHONE.equalsIgnoreCase(empPersPhNameDto.getCdPhoneType())
							|| ServiceConstants.BUSINESS_CELL.equalsIgnoreCase(empPersPhNameDto.getCdPhoneType()))) {
				empPersPhNameDto.setNbrMailCodePhone(empPersPhNameDto.getNbrPhone());
				empPersPhNameDto.setNbrMailCodePhoneExt(empPersPhNameDto.getNbrPhoneExtension());
			}
			ppmDto.setEmployeePersPhName(empPersPhNameDto);
		}

		return ppmPrefillData.returnPrefillData(ppmDto);
	}

}
