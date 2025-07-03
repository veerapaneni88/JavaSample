package us.tx.state.dfps.service.placement.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PlacementFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PlacemntAuthFosterAndResidentialCarePrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.placement.dto.PlacementFormDto;
import us.tx.state.dfps.service.placement.service.PlacementFormService;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * Class Name:PlacementFormServiceImpl Class Description:This service is used to
 * launch the CVS Placement forms for Foster/Residential Care,
 * Kinship/Non-Foster Care and Legal Care Oct 30, 2017- 3:29:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PlacementFormServiceImpl implements PlacementFormService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonIdDtlsDao personIdDtlsDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	PlacemntAuthFosterAndResidentialCarePrefillData placemntAuthFosterAndResidentialCare;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private EmployeeDao employeeDao;

	private static final Logger log = Logger.getLogger(PlacementFormServiceImpl.class);

	/**
	 * Method Name:getSubCareLOCAuthorization Method Description:Method to
	 * launch the CVS Placement forms Service Name:CSUB85S
	 * 
	 * @param placementFormReq
	 * @return PreFillDataServiceDto @
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getSubCareLOCAuthorization(PlacementFormReq placementFormReq, String formName) {
		log.debug("Entering method callCsub85sService in PlacementFormServiceImpl");
		PlacementFormDto placementFormDto = new PlacementFormDto();

		// DAM Name:CSECD3D
		PersonDto personDto = personIdDtlsDao.getPersonIdRecordDtls(placementFormReq.getIdCase(),
				placementFormReq.getIdStage(), placementFormReq.getIdPerson());
		placementFormDto.setPersonDto(personDto);

		// DAM Name: CSES34D
		PlacementActPlannedOutDto placementActPlannedDto = personIdDtlsDao
				.getPlacementRecord(placementFormReq.getIdPerson());
		placementFormDto.setPlacementActPlannedOutDto(placementActPlannedDto);

		// DAM Name:CSECD5D
		LegalStatusOutDto legalStatusOutDto = personIdDtlsDao.getLegalStatusRecords(placementFormReq.getIdPerson(),
				placementFormReq.getIdCase());
		if (!ObjectUtils.isEmpty(legalStatusOutDto) && !ObjectUtils.isEmpty(legalStatusOutDto.getCdLegalStatCnty())) {
			legalStatusOutDto.setCdLegalStatCnty(
					lookupDao.decode(ServiceConstants.CCOUNT, legalStatusOutDto.getCdLegalStatCnty()));
		}
		placementFormDto.setLegalStatusOutDto(legalStatusOutDto);

		// Fetch DFPS Case Worker & Supervisor Details - Call CINV51D
		Long idPersonCaseWrkr = workLoadDao.getPersonIdByRole(placementFormReq.getIdStage(),
				ServiceConstants.STAGE_PERS_ROLE_PR);
		if (!ObjectUtils.isEmpty(idPersonCaseWrkr)) {
			// Call CSEC01D
			EmployeePersPhNameDto employeePersCsWrkrDto = employeeDao.searchPersonPhoneName(idPersonCaseWrkr);
			placementFormDto.setEmployeePersCsWrkrDto(employeePersCsWrkrDto);
			if (!ObjectUtils.isEmpty(employeePersCsWrkrDto)
					&& !ObjectUtils.isEmpty(employeePersCsWrkrDto.getIdJobPersSupv())) {
				EmployeePersPhNameDto employeePersSuprvsrDto = employeeDao
						.searchPersonPhoneName(employeePersCsWrkrDto.getIdJobPersSupv());
				placementFormDto.setEmployeePersSuprvsrDto(employeePersSuprvsrDto);
			}
		}
		// calling prefillData method with placementFormDto as I/p parameter
		return placemntAuthFosterAndResidentialCare.returnPrefillData(placementFormDto);
	}

}