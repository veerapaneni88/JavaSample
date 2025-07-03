package us.tx.state.dfps.service.arfamilynotification.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ArConcRepNotifDto;
import us.tx.state.dfps.service.arfamilynotification.service.ArConcRepNotifService;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ArConcRepNotifPrefillData;
import us.tx.state.dfps.service.person.dao.AbcsRecordsCheckDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.workload.dao.AddressDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes DAO
 * calls and returns prefill string for forms ARRENOT and ARRENOTS May 8, 2018-
 * 4:44:33 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ArConcRepNotifServiceImpl implements ArConcRepNotifService {

	@Autowired
	private PersonDao personDao;

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private CpsInvReportDao cpsInvReportDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private AbcsRecordsCheckDao abcsRecordsCheckDao;

	@Autowired
	private ArConcRepNotifPrefillData prefillData;

	/**
	 * Method Name: getReporterNotif Method Description: Gets the prefill string
	 * for the ARRENOT/S forms
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getReporterNotif(PopulateFormReq req) {
		// Initialize main dto and global variables
		ArConcRepNotifDto prefillDto = new ArConcRepNotifDto();
		prefillDto.setIdCase(req.getIdCase());
		prefillDto.setClosureReason(req.getArClosureReason());

		// Get addressee info
		PersonDto personDto = personDao.getPersonById(req.getIdPerson());
		prefillDto.setPersonDto(personDto);

		// Get addressee address info
		AddressValueDto addressValueDto = addressDao.fetchCurrentPrimaryAddress(req.getIdPerson());
		prefillDto.setAddressValueDto(addressValueDto);

		// Get principals info
		List<CpsInvPrincipalDto> cpsInvPrincipalsList = cpsInvReportDao.getPrincipals(req.getIdStage());
		prefillDto.setCpsInvPrincipalsList(cpsInvPrincipalsList);

		// Get worker info
		PersonDto workerDto = personDao.getPersonById(req.getIdWorker());
		prefillDto.setWorkerDto(workerDto);

		// Get worker title
		EmployeeDetailDto employeeDetailDto = employeeDao.getEmployeeById(req.getIdWorker());
		if (!ObjectUtils.isEmpty(employeeDetailDto)) {
			prefillDto.setEmployeeClass(employeeDetailDto.getEmployeeClass());
		}

		// Get worker address info
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(req.getIdWorker());
		prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);

		// Get worker phone info
		EmployeePersonDto employeePersonDto = abcsRecordsCheckDao.getStaffContactInfo(req.getIdWorker());
		prefillDto.setEmployeePersonDto(employeePersonDto);

		return prefillData.returnPrefillData(prefillDto);
	}

}
