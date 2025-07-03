package us.tx.state.dfps.service.security.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.MaintainDesigneeDto;
import us.tx.state.dfps.service.admin.dto.EmployeeSecurityClassLinkDto;
import us.tx.state.dfps.service.admin.dto.EmployeeTempAssignDto;
import us.tx.state.dfps.service.admin.dto.SecurityClassInfoDto;
import us.tx.state.dfps.service.admin.dto.StaffSecurityRtrvoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.StaffSecurityMaintenanceReq;
import us.tx.state.dfps.service.common.response.StaffSecurityMaintenanceRes;
import us.tx.state.dfps.service.maintaindesignee.dao.MaintainDesigneeDao;
import us.tx.state.dfps.service.security.dao.StaffSecurityMaintenanceDao;
import us.tx.state.dfps.service.security.service.StaffSecurityMaintenaceService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * will have staff security maintenance page business as well as interacts with
 * DAO layer Sept 25, 2018- 04:18:28 PM © 2018 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class StaffSecurityMaintenaceServiceImpl implements StaffSecurityMaintenaceService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StaffSecurityMaintenanceDao staffSecurityMaintenanceDao;

	@Autowired
	MaintainDesigneeDao maintainDesigneeDao;

	private static final Logger log = Logger.getLogger("ServiceBusiness-StaffSecurityAudService");

	/**
	 * Method Name;callStaffSecurityRtrvService Method Description: This Method
	 * retrieves security information for a given employee from the Employee
	 * Table if it exists. It also retrieves any temporary assignments of the
	 * employee, which can be up to five assignments.
	 * 
	 * @param idPerson
	 * @param externalUser
	 * @return StaffSecurityRtrvoDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StaffSecurityRtrvoDto staffSecurityRtrvService(Long idPerson, Boolean externalUser) {
		log.debug("Entering method staffSecurityRtrvService in StaffSecurityRtrvServiceImpl");
		StaffSecurityRtrvoDto staffSecurityRtrvoDto = new StaffSecurityRtrvoDto();

		// Call CLSCB3D dam functionality to select all rows from the
		// EMP_SEC_LINK table and will ultimately indicate which capabilities a
		// selected user has
		List<EmployeeSecurityClassLinkDto> employeeSecurityClassLinkDtos = new ArrayList<EmployeeSecurityClassLinkDto>();
		employeeSecurityClassLinkDtos = staffSecurityMaintenanceDao.fetchEmpSecClassLinkDtl(idPerson);

		if (!CollectionUtils.isEmpty(employeeSecurityClassLinkDtos) && employeeSecurityClassLinkDtos.size() > 0) {
			staffSecurityRtrvoDto.setEmployeeSecurityClassLinkDtos(employeeSecurityClassLinkDtos);
		}

		// Call CSES00D dam functionality to perform full row retrieval on the
		// Employee table
		staffSecurityRtrvoDto = staffSecurityMaintenanceDao.fetchEmployeeDtl(staffSecurityRtrvoDto, idPerson,
				externalUser);

		// Call CLSS12D Dam functionality to fetch Security class details
		List<SecurityClassInfoDto> securityClassInfoDtos = new ArrayList<SecurityClassInfoDto>();
		securityClassInfoDtos = staffSecurityMaintenanceDao.fetchStaffSecurityDtl();
		if (!CollectionUtils.isEmpty(securityClassInfoDtos) && securityClassInfoDtos.size() > 0) {
			staffSecurityRtrvoDto.setSecurityClassInfoDtos(securityClassInfoDtos);
		}

		// Call CLSS15D Dam functionality to fetch Security class details
		List<EmployeeTempAssignDto> empTempAssignDtos = new ArrayList<EmployeeTempAssignDto>();
		empTempAssignDtos = staffSecurityMaintenanceDao.fetchEmpTempAssignDtl(idPerson);
		if (!CollectionUtils.isEmpty(empTempAssignDtos) && empTempAssignDtos.size() > 0) {
			staffSecurityRtrvoDto.setEmployeeTempAssignDtos(empTempAssignDtos);
		}

		log.debug("Exiting method staffSecurityRtrvService in StaffSecurityRtrvServiceImpl");
		return staffSecurityRtrvoDto;
	}

	/**
	 * Method : staffSecurityAudService Method Description : This method will
	 * add or update staff security maintenance details
	 * 
	 * @param StaffSecurityAudiDto
	 * @return StaffSecurityAudoDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public StaffSecurityMaintenanceRes staffSecurityAudService(
			StaffSecurityMaintenanceReq staffSecurityMaintenanceReq) {
		log.debug("Entering method staffSecurityAudService in StaffSecurityMaintenaceServiceImpl");
		ErrorDto errorDto = null;
		StaffSecurityMaintenanceRes staffSecurityMaintenanceRes = new StaffSecurityMaintenanceRes();
		String reqFuncCd = staffSecurityMaintenanceReq.getReqFuncCd();
		String idEmployeeLogon = staffSecurityMaintenanceReq.getIdEmployeeLogon();
		Long idPerson = staffSecurityMaintenanceReq.getIdPerson();
		Date dtLastUpdate = staffSecurityMaintenanceReq.getDtLastUpdate();
		boolean externalUser = staffSecurityMaintenanceReq.isExternalUser();
		String idUser = staffSecurityMaintenanceReq.getUserId();
		// CAUD23D -This performs an update on the Employee table.
		errorDto = staffSecurityMaintenanceDao.updateEmpLogonDtl(reqFuncCd, idEmployeeLogon, idPerson, dtLastUpdate,
				externalUser, idUser);
		if (!externalUser) {
			if (!CollectionUtils.isEmpty(staffSecurityMaintenanceReq.getDesigneeDtlDtoList())) {
				staffSecurityMaintenanceReq.getDesigneeDtlDtoList().stream().forEach(designeeDtlList -> {
					String cdDataActionOutcome = designeeDtlList.getCdDataActionOutcome();
					Long idEmpTempAssign = designeeDtlList.getIdEmpTempAssign();
					Long idEmpPerson = designeeDtlList.getIdPerson();
					Long idPersonDesignee = designeeDtlList.getIdPersonDesignee();
					Date dtAssignExpiration = designeeDtlList.getDtAssignExpiration();
					Date dtLastUpdateEmp = designeeDtlList.getDtLastUpdate();
					// CAUD22D - This performs Add/Update/Delete from table
					// EMP_TEMP_ASSIGN.
					staffSecurityMaintenanceDao.saveUpdateEmpTempAssignDtl(cdDataActionOutcome, idEmpTempAssign,
							idEmpPerson, idPersonDesignee, dtAssignExpiration, dtLastUpdateEmp);
				});
			}
		}

		if (!CollectionUtils.isEmpty(staffSecurityMaintenanceReq.getSecurityProfilesInputDtoList())) {
			staffSecurityMaintenanceReq.getSecurityProfilesInputDtoList().stream().forEach(securityProfileList -> {
				String cdDataActionOutcome = securityProfileList.getCdDataActionOutcome();
				Long idEmpSecLink = securityProfileList.getIdEmpSecLink();
				Long idSecurityPerson = securityProfileList.getIdPerson();
				String nmSecurityClass = securityProfileList.getNmSecurityClass();
				Date dtLastUpdateSecurity = securityProfileList.getDtLastUpdate();
				//artf252942
				Long idCreatedPerson = securityProfileList.getIdCreatedPerson();
				Date dtCreated = securityProfileList.getDtCreated();
				Long idLastUpdatePerson = securityProfileList.getIdLastUpdatePerson();
				if (!ServiceConstants.REQ_FUNC_CD_NO_ACTION
						.equalsIgnoreCase(staffSecurityMaintenanceReq.getReqFuncCd())) {
					// CAUDE1D - DAM that will add or delete rows on the
					// EMP_SEC_CLASS_LINK table for a selected user.
					staffSecurityMaintenanceDao.insertDeleteEmpClassLinkDtl(cdDataActionOutcome, idEmpSecLink,
							idSecurityPerson, nmSecurityClass, dtLastUpdateSecurity,idCreatedPerson,dtCreated,idLastUpdatePerson);

				}
			});
		}

		staffSecurityMaintenanceRes.setErrorDto(errorDto);
		log.debug("Exiting method staffSecurityAudService in StaffSecurityMaintenaceServiceImpl");
		return staffSecurityMaintenanceRes;
	}

	/**
	 * Method Name: getStaffSecurityDesigneeDtls Method Description: This
	 * service retrieves all designees for a given employee. It performs a full
	 * row retrieval of the EMP_TEMP_ASSIGN table for a given ID PERSON.
	 * 
	 * @param idPerson
	 * @return List<EmployeeTempAssignDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EmployeeTempAssignDto> getStaffSecurityDesigneeDtls(Long idPerson) {
		List<EmployeeTempAssignDto> employeeTempAssignDtoList = new ArrayList<EmployeeTempAssignDto>();
		List<MaintainDesigneeDto> maintainDesigneeDtoList = maintainDesigneeDao.getDesigneeDtls(idPerson);
		if (!ObjectUtils.isEmpty(maintainDesigneeDtoList)) {
			maintainDesigneeDtoList.stream().forEach(maintainDesigneeDto -> {
				EmployeeTempAssignDto employeeTempAssignDto = new EmployeeTempAssignDto();
				employeeTempAssignDto.setIdEmpTempAssign(maintainDesigneeDto.getIdEmpTempAssign());
				employeeTempAssignDto.setIdPersonDesignee(maintainDesigneeDto.getIdPersonDesignee());
				employeeTempAssignDto.setNmPersonFull(maintainDesigneeDto.getNmPersonFull());
				employeeTempAssignDto.setDtAssignExpiration(maintainDesigneeDto.getDtAssignExpiration());
				employeeTempAssignDto.setDtLastUpdate(maintainDesigneeDto.getDtLastUpdate());
				employeeTempAssignDtoList.add(employeeTempAssignDto);
			});
		}
		return employeeTempAssignDtoList;
	}
	
}
