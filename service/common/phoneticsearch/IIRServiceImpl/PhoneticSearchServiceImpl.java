package us.tx.state.dfps.service.common.phoneticsearch.IIRServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.EmpJobHistory;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.web.WebConstants;
import us.tx.state.dfps.phoneticsearch.Dto.PhoneticSearchPersonResDto;
import us.tx.state.dfps.phoneticsearch.Dto.PhoneticSearchStaffResDto;
import us.tx.state.dfps.phoneticsearch.Dto.PrsnSearchOutRecDto;
import us.tx.state.dfps.phoneticsearch.exception.PhoneticSearchServiceException;
import us.tx.state.dfps.phoneticsearch.util.PersonPhoneticSearch;
import us.tx.state.dfps.phoneticsearch.util.StaffPhoneticSearch;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmployeeSearchDto;
import us.tx.state.dfps.service.common.phoneticsearch.IIRService.PhoneticSearchService;
import us.tx.state.dfps.service.common.request.PhoneticSearchReq;
import us.tx.state.dfps.service.common.response.PhoneticSearchRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dto.ActBSandRSPhDto;
import us.tx.state.dfps.service.person.dto.SupervisorDOBDto;

@Service
@Transactional
public class PhoneticSearchServiceImpl implements PhoneticSearchService {

	/**
	 * Method Description: This method is used to performs search operations for
	 * staff and person and retrieval of results based on the request Service
	 * Name: Phonetic Search
	 * 
	 * @param phoneticSearchReq
	 * @param searchFilter
	 * @return PhoneticSearchRes @
	 */
	@Autowired
	PersonPhoneticSearch personPhoneticSearch;

	@Autowired
	StaffPhoneticSearch staffPhoneticSearch;

	@Autowired
	PersonPhoneDao personPhoneDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	EmployeeDao employeeDao;

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PhoneticSearchRes phoneticSearch(PhoneticSearchReq phoneticSearchReq) {
		PhoneticSearchRes phoneticSearchRes = new PhoneticSearchRes();
		if (null != phoneticSearchReq.getIsStaffSearch() && phoneticSearchReq.getIsStaffSearch().equals(Boolean.TRUE)) {
			PhoneticSearchStaffResDto phoneticSearchStaffResDto;
			try {
				phoneticSearchStaffResDto = staffPhoneticSearch.searchPhonetic(
						phoneticSearchReq.getPhoneticSearchStaffDto(), phoneticSearchReq.getPaginationResultBean());
				Map<Long, EmployeeSearchDto> employeeSearchMap = new HashMap<>();
				phoneticSearchStaffResDto.getStaffList().stream().forEach(o -> {

					Employee employee = employeeDao.getEmployeeEntityById(o.getUlIdPerson());

					// PD-2559 : Added caseAssignable check
					boolean caseAssignable = false;
					if (phoneticSearchReq.getPageName() != null && phoneticSearchReq.getPageName().contains(WebConstants.ASSIGN)) {
						if (employee.getIdEmpJobHistory() != null) {
							EmpJobHistory empJobHistory = employeeDao.getEmpJobHistoryById(employee.getIdEmpJobHistory());
							if (empJobHistory.getIndJobAssignable() != null && empJobHistory.getIndJobAssignable().equalsIgnoreCase("Y")) {
								caseAssignable = true;
							}
						}
					} else {
						caseAssignable = true;
					}

                    if(employee.getIndEmpActiveStatus()!=null && employee.getIndEmpActiveStatus().equalsIgnoreCase("Y") && caseAssignable) {
							EmployeeSearchDto emp = new EmployeeSearchDto();
							emp.setNmPersonFull(o.getSzNmPersonFull());
							emp.setBjnJob(o.getSzBjnJob());
							emp.setCdEmployeeClass(o.getSzCdEmployeeClass());
							emp.setCdUnitRegion(o.getSzCdUnitRegion());
							emp.setUnit(o.getSzNbrUnit());
							emp.setNmOfficeName(o.getSzNmOfficeName());
							emp.setSysNbrPersPhnHome(o.getlSysNbrPersPhnHome());
							emp.setSysNbrPersPhoneWork(o.getlSysNbrPersPhoneWork());
							emp.setCdPhoneType(o.getSzCdPhoneType());
							emp.setNbrPhone(o.getlNbrPhone());
							emp.setNbrPhoneExtension(o.getlNbrPhoneExtension());
							emp.setIndPersonPhonePrimary(o.getbIndPersonPhonePrimary());
							emp.setDtEmpLastAssigned(o.getDtDtEmpLastAssigned());
							emp.setTmScrTmEmpLastAssigned(o.getTmScrTmEmpLastAssigned());
							emp.setCdAddrMail(o.getSzAddrMailCode());
							emp.setIdPerson(o.getUlIdPerson());
							emp.setIdUnit(o.getUlIdUnit());
							emp.setDtPersonBirth(o.getDtDtPersonBirth());
							emp.setIdPersonSupervisor(o.getUlIdPersonSupervisor());
							emp.setNmSupervisorFull(o.getSzNmPersonFull());
							emp.setScrIndScore(o.getUsScrIndScore());
							emp.setNmIncmgPersFull(o.getSzNmIncmgPersFull());
							emp.setEmployeeEmailAddress(o.getSzTxtEmployeeEmailAddress());
							emp.setCdEmpProgram(o.getSzCdEmpProgram());
							emp.setDtEmpTerm(o.getDtDtEmpTerm());
							emp.setTmScrTmEmpTerm(o.getTmScrTmEmpTerm());
							emp.setCdExternalUserType(o.getSzCdExternalUserType());
							emp.setChecked(o.getSzChecked());
							emp.setRbRows(o.getRbRows());
							emp.setCurrentCounty(o.getCurrentCounty());
							ActBSandRSPhDto actBSandRSPhDto = personPhoneDao.getActiveBSandRSPhone(emp.getIdPerson());
							if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto)) {
								if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto.getBSPhone()))
									emp.setSysNbrPersPhoneWork(actBSandRSPhDto.getBSPhone());
								if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto.getBSPhoneExtension()))
									emp.setNbrPhoneExtension(actBSandRSPhDto.getBSPhoneExtension());
							}
							SupervisorDOBDto supervisorDOBDto = personDao.getSupervisorAndDobByPersonId(emp.getIdPerson());
							// set supervisor and DOB
							if (!TypeConvUtil.isNullOrEmpty(supervisorDOBDto.getNmSupervisorFull()))
								emp.setNmSupervisorFull(supervisorDOBDto.getNmSupervisorFull());
							if (!TypeConvUtil.isNullOrEmpty(supervisorDOBDto.getIdsupervisor()))
								emp.setIdPersonSupervisor(supervisorDOBDto.getIdsupervisor());
							if (!TypeConvUtil.isNullOrEmpty(emp.getIdPerson())) {
								boolean isEnabled = employeeDao.getSecAttrEnabled(emp.getIdPerson());
								emp.setPlusCF1050BSecAttrEnabled(isEnabled);
							}
							employeeSearchMap.put(emp.getIdPerson(), emp);
					}
				});
				phoneticSearchRes.setSearchEmployeeRes(employeeSearchMap);

				if (!ObjectUtils.isEmpty(phoneticSearchStaffResDto.getIndMoreData()))
					phoneticSearchRes.setMoreDataInd(phoneticSearchStaffResDto.getIndMoreData());
			} catch (PhoneticSearchServiceException e) {
				ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage(),123456l,null);
				serviceLayerException.initCause(e);
				throw serviceLayerException;
			}
		}
		if (null != phoneticSearchReq.getIsPersonSearch() && phoneticSearchReq.getIsPersonSearch() == true) {
			try {
				PhoneticSearchPersonResDto phoneticSearchPersonResDto = personPhoneticSearch.searchPhonetic(
						phoneticSearchReq.getPhoneticSearchPrsnDto(), phoneticSearchReq.getPaginationResultBean());
				if (!ObjectUtils.isEmpty(phoneticSearchPersonResDto.getPrsnSearchOutRecList())
						&& phoneticSearchPersonResDto.getPrsnSearchOutRecList().size() > 0) {
					List<PrsnSearchOutRecDto> personList = phoneticSearchPersonResDto.getPrsnSearchOutRecList();
					if (personList.size() > phoneticSearchReq.getPaginationResultBean().getResultDetails()
							.getResultsPerPage())
						phoneticSearchRes.setPrsnSearchOutRecList(personList.subList(0,
								phoneticSearchReq.getPaginationResultBean().getResultDetails().getResultsPerPage()));
					else
						phoneticSearchRes.setPrsnSearchOutRecList(personList);
				}
				if (!ObjectUtils.isEmpty(phoneticSearchPersonResDto.getIndMoreData()))
					phoneticSearchRes.setMoreDataInd(phoneticSearchPersonResDto.getIndMoreData());
			} catch (PhoneticSearchServiceException e) {
		    	ServiceLayerException serviceException = new ServiceLayerException(e.getMessage(),123456l,null);
            	serviceException.initCause(e);
    			throw serviceException;
			}
		}
		return phoneticSearchRes;
	}
}
