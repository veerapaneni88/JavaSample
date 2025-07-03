/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 19, 2018- 4:11:48 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedInDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.SubcareLOCFormReq;
import us.tx.state.dfps.service.forms.dao.SubcareLOCFormDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.SubcareLOCFormDto;
import us.tx.state.dfps.service.forms.service.SubcareLOCFormService;
import us.tx.state.dfps.service.forms.util.AuthRequestPrefillData;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.PersonLocPersonDao;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.PersonLocPersonOutDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 19, 2018- 4:11:48 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class SubcareLOCFormServiceImpl implements SubcareLOCFormService {

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private PlacementActPlannedDao placementActPlannedDao;

	@Autowired
	private LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private PersonLocPersonDao personLocPersonDao;

	@Autowired
	private SubcareLOCFormDao subcareLOCFormDao;

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private AuthRequestPrefillData authRequestPrefillData;

	/**
	 * 
	 * Method Name: getSubcareLOCAuthReqReport Method Description: service
	 * implementation for CSUB44S and and implements the DAMS
	 * 
	 * @param placementFormReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getSubcareLOCAuthReqReport(SubcareLOCFormReq subcareLOCFromReq) {
		SubcareLOCFormDto subcareLOCFormDto = new SubcareLOCFormDto();

		Long idPersonWorker = ServiceConstants.NULL_VAL;
		Long idPersonChild = ServiceConstants.NULL_VAL;
		// Dam Name: CSEC02D - This method is used to retrieve the information
		// for stage
		// and capscase table by passing idStage as input request.
		StageCaseDtlDto stageCaseDtl = pcaDao.getStageAndCaseDtls(subcareLOCFromReq.getIdStage());
		subcareLOCFormDto.setStageCaseDtlDto(stageCaseDtl);
		// Dam Name: CCMN19D - CCMN19D Method Description: This Method is used
		// to retrieve
		// the STAGE NM and PRIMARY NM from STAGE,PERSON and STAGE PERSON LINK
		// tables.
		StagePersonDto stagePerson = stageDao.getStagePersonLinkDetails(subcareLOCFromReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		subcareLOCFormDto.setStagePersonDto(stagePerson);
		idPersonWorker = stagePerson.getIdTodoPersWorker();
		if (!ObjectUtils.isEmpty(idPersonWorker)) {
			// Dam Name: CSEC01D - This Method will retrieve the active primary
			// address,
			// phone number, and name for an employee
			EmployeePersPhNameDto employeePersPhName = employeeDao.searchPersonPhoneName(idPersonWorker);
			subcareLOCFormDto.setEmployeePersPhNameDto(employeePersPhName);
			// Dam Name: CSES29D - This DAM will perform a full row retrieval on
			// the
			// person phone table given ID_PERSON and the type of phone number
			// to retrieve
			PersonPhoneDto personPhone = subcareLOCFormDao.getPhnNbrbyPersonId(idPersonWorker,
					ServiceConstants.FAX_NUMBER);
			subcareLOCFormDto.setPersonPhoneDto(personPhone);
			// Dam Name: CSES15D - cses15dQUERYdam Method Description: It
			// retrieves a
			// single row from the Person_Loc table
			List<PersonLocPersonOutDto> personListLoc = personLocPersonDao
					.fetchPersonLOCByIdPlocEvent(subcareLOCFromReq.getIdEvent());
			PersonLocPersonOutDto idChildPerson = personListLoc.get(0);
			subcareLOCFormDto.setPersonLocPersonOutDto(idChildPerson);
			idPersonChild = subcareLOCFormDto.getPersonLocPersonOutDto().getIdPerson();
			if (!ObjectUtils.isEmpty(idPersonChild)) {
				// DAM: CSEC74D - This dam will retrieve all child info
				// based upon an Id Person.
				PersonDto person = subcareLOCFormDao.getChildInfoByPersonId(idPersonChild);
				subcareLOCFormDto.setPersonDto(person);
				// DAM: CCMN72D - This dam will retrieve the child
				// Medicaid Number and the Medicaid id number.
				Date phoneend = subcareLOCFormDto.getPersonPhoneDto() != null
						? subcareLOCFormDto.getPersonPhoneDto().getDtPersonPhoneEnd()
						: ServiceConstants.GENERIC_END_DATE;
				PersonIdDto personId = subcareLOCFormDao.getMedicaidNbrByPersonId(idPersonChild,
						ServiceConstants.CNUMTYPE_MEDICAID_NUMBER, ServiceConstants.STRING_IND_N, phoneend);
				subcareLOCFormDto.setPersonIdDto(personId);
				// Dam Name: CSES32D - getRecentLegelStatusRecord Method
				// Description:
				// Fetch recent legal status for the person
				LegalStatusPersonMaxStatusDtInDto legalStatusPersonMaxStatusDtInDto = new LegalStatusPersonMaxStatusDtInDto();
				legalStatusPersonMaxStatusDtInDto.setIdPerson(idPersonChild);
				List<LegalStatusPersonMaxStatusDtOutDto> legalStatusPersonMaxList = legalStatusPersonMaxStatusDtDao
						.getRecentLegelStatusRecord(legalStatusPersonMaxStatusDtInDto);
				subcareLOCFormDto.setLegalStatusPersonMaxList(legalStatusPersonMaxList);
				// Dam Name: CSES34D - getPlacementRecord Method Description:
				// Get
				// placement record for given person.
				PlacementActPlannedInDto placementActPlannedInDto = new PlacementActPlannedInDto();
				placementActPlannedInDto.setIdPlcmtChild(idPersonChild);
				List<PlacementActPlannedOutDto> placementActPlanned = placementActPlannedDao
						.getPlacementRecord(placementActPlannedInDto);
				subcareLOCFormDto.setPlacementActPlannedList(placementActPlanned);
				// Dam Name: CSES35D - This method is used to retrieve the
				// child's most
				// recent authorized level of care by passing idPerson and
				// cdPlocType as input request.
				if (!ObjectUtils.isEmpty(
						commonApplicationDao.getPersonLocDtls(idPersonChild, ServiceConstants.LOC_TYPE_BLOC))) {
					PersonLocDto personLocDto = commonApplicationDao
							.getPersonLocDtls(idPersonChild, ServiceConstants.LOC_TYPE_BLOC).get(0);
					subcareLOCFormDto.setPersonLocDto(personLocDto);
				}

			}
		}
		return authRequestPrefillData.returnPrefillData(subcareLOCFormDto);
	}

}
