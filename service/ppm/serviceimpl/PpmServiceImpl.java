package us.tx.state.dfps.service.ppm.serviceimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.PRIMARY_CHILD;
import static us.tx.state.dfps.service.common.ServiceConstants.SIXTEEN_AND_OVER;
import static us.tx.state.dfps.service.common.ServiceConstants.UNDER_SIXTEEN;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.PpmDto;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.ppm.dao.PPMDao;
import us.tx.state.dfps.service.ppm.service.PpmService;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: To
 * implement the operations of Permanency Planning Meeting Jan 27, 2018- 1:34:16
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PpmServiceImpl implements PpmService {

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private PPMDao ppmDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private PersonAddressDao personAddressDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	private static final Logger LOG = Logger.getLogger(PpmServiceImpl.class);

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PpmDto getPpm(PpmReq ppmReq) {
		PpmDto ppmDto = new PpmDto();
		NameDetailDto nameDetailDto = null;
		NameDetailDto partsNameDetailDto = null;
		PersonGenderSpanishDto personGenderSpanishDto = null;
		PptDetailsOutDto pPtDetailsOutDto = null;
		List<AddressDto> addressList = null;
		PPTParticipantDto pPTParticipantDto = null;
		StagePersonDto stagePersonDto = null;
		EmployeePersPhNameDto employeePersPhName = null;
		CodesTablesDto codesTablesDto = null;
		Long idPerson = null;
		// Call CSEC15D call to retrieve date field
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(ppmReq.getIdStage(),
				PRIMARY_CHILD);
		if (Objects.nonNull(stagePersonLinkCaseDto) && Objects.nonNull(stagePersonLinkCaseDto.getDtPersonBirth())) {

			idPerson = stagePersonLinkCaseDto.getIdPerson();
			int nbrPersonAge = DateUtils.calculatePersonsAgeInYears(stagePersonLinkCaseDto.getDtPersonBirth(),
					Calendar.getInstance().getTime());
			String cdPersonDeath = UNDER_SIXTEEN;
			if (nbrPersonAge >= 16) {
				cdPersonDeath = SIXTEEN_AND_OVER;
			}
			stagePersonLinkCaseDto.setCdPersonDeath(cdPersonDeath);
			if (LOG.isDebugEnabled()) {
				LOG.debug("cdPersonDeath is : " + cdPersonDeath);
			}
			// Call CSEC35D /* DAM call to retrieve child's name */
			if (Objects.nonNull(ppmReq.getIdPerson())) {
				nameDetailDto = commonApplicationDao.getNameDetails(idPerson);
			}
			nameDetailDto = commonApplicationDao.getNameDetails(idPerson);
		}

		/*
		 ** retrieves letter head info CLSC03D to retrieve the board members and
		 * the executive director information for the header.
		 */
		/* DAM call to retrieve letter head information */
		codesTablesDto = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);

		// Call CSES14D retrieves date, time, and address
		pPtDetailsOutDto = ppmDao.getPptAddress(ppmReq.getIdPptEvent());

		// Call CSES40D retrieves full name
		pPTParticipantDto = ppmDao.getParticipantData(ppmReq.getIdPptPart());

		if (Objects.nonNull(pPTParticipantDto) && Objects.nonNull(idPerson)) {
			// Call CINV81D is used to retrieves gender for spanish translation.
			personGenderSpanishDto = populateFormDao.isSpanGender(idPerson);
			// Call CSEC35D retrieve parts of name
			partsNameDetailDto = commonApplicationDao.getNameDetails(idPerson);

			// Call CCMN96D used to retrieves home address
			AddressDtlReq addressDtlReq = new AddressDtlReq();
			addressDtlReq.setUlIdPerson(idPerson);
			addressList = personAddressDao.getAddressList(addressDtlReq);

		}
		// Call CCMN19D is used to retrieves primary worker for stage
		stagePersonDto = stageDao.getStagePersonLinkDetails(ppmReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

		// Call CSEC01D is used to retrieves name and phone for primary worker
		if (Objects.nonNull(stagePersonDto) && Objects.nonNull(stagePersonDto.getIdTodoPersWorker())) {
			idPerson = stagePersonDto.getIdTodoPersWorker();
			employeePersPhName = employeeDao.searchPersonPhoneName(idPerson);
		}
		ppmDto.setStagePersonLinkCase(stagePersonLinkCaseDto);
		ppmDto.setNameDetail(nameDetailDto);
		ppmDto.setCodesTables(codesTablesDto);
		ppmDto.setpPtDetailsOut(pPtDetailsOutDto);
		ppmDto.setpPTParticipant(pPTParticipantDto);
		ppmDto.setPersonGenderSpanish(personGenderSpanishDto);
		ppmDto.setNameDetail(partsNameDetailDto);
		ppmDto.setAddressList(addressList);
		ppmDto.setStagePerson(stagePersonDto);
		ppmDto.setEmployeePersPhName(employeePersPhName);
		return ppmDto;
	}

}