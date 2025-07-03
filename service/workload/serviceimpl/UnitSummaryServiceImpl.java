package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.TempAssignmentDto;
import us.tx.state.dfps.service.admin.dao.EmpTempAssignDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.common.response.UnitSummaryRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.workload.dao.UnitSummaryDao;
import us.tx.state.dfps.service.workload.dto.UnitSummaryDto;
import us.tx.state.dfps.service.workload.service.UnitSummaryService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN29S Class
 * Description: Unit Summary Page
 */
@Service
@Transactional
public class UnitSummaryServiceImpl implements UnitSummaryService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	LookupService lookupService;

	@Autowired
	UnitSummaryDao unitSummaryDao;

	@Autowired
	EmpTempAssignDao empTempAssignDao;

	private static final Logger log = Logger.getLogger(UnitSummaryServiceImpl.class);

	public UnitSummaryServiceImpl() {
	}

	/**
	 * 
	 * Method Description: This method is called, when a user searches for a
	 * unit. The user profile is used to populate the search parameters
	 * therefore no service is called here.
	 * 
	 * @param assignWorkloadReq
	 * @return AssignWorkloadReq @ Tuxedo Service Name: CCMN29S
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public AssignWorkloadReq getUnitSummaryDtls(AssignWorkloadReq assignWorkloadReq) {
		HashMap<String, TreeMap<String, CodeAttributes>> codeCategoryMap = lookupService.getCodes();
		TreeMap<String, CodeAttributes> pgmMap = codeCategoryMap.get(ServiceConstants.CUNITPGM);
		TreeMap<String, CodeAttributes> regDiv = codeCategoryMap.get(ServiceConstants.CREGDIV);
		LinkedHashMap<String, String> finalPgm = new LinkedHashMap<>();
		LinkedHashMap<String, String> finalRegDiv = new LinkedHashMap<>();
		Map<String, String> unitPgmMap = pgmMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDecode()));
		unitPgmMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByKey())
				.forEachOrdered(x -> finalPgm.put(x.getKey(), x.getValue()));
		Map<String, String> regDivMap = regDiv.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDecode()));
		regDivMap.entrySet().stream().sorted(Map.Entry.<String, String>comparingByKey())
				.forEachOrdered(x -> finalRegDiv.put(x.getKey(), x.getValue()));
		assignWorkloadReq.setCodesPGMOptions(finalPgm);
		assignWorkloadReq.setCodesRegDivOptions(finalRegDiv);
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		return assignWorkloadReq;
	}

	/**
	 * 
	 * Method Description: This method Searches the database for program -
	 * region - unit combination that match the specified search criteria.
	 * 
	 * @param assignWorkloadReq
	 * @return UnitSummaryRes
	 * @, DataNotFoundException Tuxedo Service Name: CCMN29S
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public UnitSummaryRes searchUnitSummary(AssignWorkloadReq assignWorkloadReq) {
		/*
		 ** Determine Unit Validity CallCCMN33D
		 */
		UnitSummaryRes res = new UnitSummaryRes();
		ArrayList<Long> ulIdPersonArray = new ArrayList<>();
		List<UnitSummaryDto> unitList = unitSummaryDao.unitValidity(assignWorkloadReq);
		if (unitList.isEmpty()) {
			res.setRetMsg(messageSource.getMessage("msg.cmn.invalid.unit", null, Locale.US));
			return res;
		}
		List<UnitSummaryDto> unitSummaryDetails = new ArrayList<>();
		List<TempAssignmentDto> empDesignees = new ArrayList<>();
		if (!unitList.isEmpty()) {
			/*
			 ** Unit exists, so check for Approval Responsibility and retrieve
			 ** Unit Summary
			 **
			 ** Check to see if the user or the user's designees is the approver
			 */
			UnitSummaryDto unitDet = new UnitSummaryDto();
			BeanUtils.copyProperties(unitList.get(0), unitDet);
			Boolean bIsApprover = Boolean.FALSE;

			try {
				// FiX for PROD Def# 3809 - Start
				empDesignees = assignWorkloadReq.getTempAssignments();
				// FiX for PROD Def# 3809 - End
				ulIdPersonArray.add(assignWorkloadReq.getUlIdPerson());
				for (TempAssignmentDto empTempAssign : empDesignees) {
					ulIdPersonArray.add(empTempAssign.getIdTempDesignator());
				}
			} catch (DataNotFoundException ex) {
				ulIdPersonArray.add(assignWorkloadReq.getUlIdPerson());
			}

			Iterator<Long> idPersonsItr = ulIdPersonArray.iterator();
			while (idPersonsItr.hasNext() && bIsApprover == false) {
				Long ulIdPerson = idPersonsItr.next();
				if (ulIdPerson.equals(unitDet.getIdPerson())) {
					bIsApprover = Boolean.TRUE;
				}
			}
			if (!bIsApprover) {
				/*
				 * if (!bIsApprover) { /* the user is not the approver, check
				 * for access to the Unit
				 */
				// FiX for PROD Def# 3809 - Start
				unitDet.setIdPerson(assignWorkloadReq.getUlIdPerson());
				Long idperson = unitSummaryDao.checkAcessForUnit(unitDet);
				// check for asa role
				if ((idperson==null)&&(assignWorkloadReq.isMaintainAllUnits())) {
					idperson=assignWorkloadReq.getUlIdPerson();
				}
				if (ObjectUtils.isEmpty(idperson)) {
					for (TempAssignmentDto empTempAssign : empDesignees) {
						unitDet.setIdPerson(empTempAssign.getIdTempDesignator());
						idperson = unitSummaryDao.checkAcessForUnit(unitDet);
						if (!ObjectUtils.isEmpty(idperson)) {
							break;
						}
					}
				}
				if (ObjectUtils.isEmpty(idperson)) {
					res.setRetMsg(messageSource.getMessage("msg.cmn.invalid.access", null, Locale.US));
					return res;
				}
				// FiX for PROD Def# 3809 - End
			}
			/*
			 ** The user has access to the Unit, so get Unit Summary call CCMN67D
			 */
			unitDet.setOrderBy(assignWorkloadReq.getOrderByColumn() == null ? ServiceConstants.SORT_BY_NAME
					: assignWorkloadReq.getOrderByColumn());
			unitSummaryDetails = unitSummaryDao.getUnitSummary(unitDet, assignWorkloadReq);
			prepareFullName(unitSummaryDetails);
			res.setUntSummaryDet(unitSummaryDetails);
			if (!ObjectUtils.isEmpty(assignWorkloadReq.getRbSameName())) {
				if (assignWorkloadReq.getRbSameName().equals(ServiceConstants.RB_CHAR_ONE)) {
					// CSEC79D
					for (UnitSummaryDto dto : unitSummaryDetails) {
						dto.setUsScrNbrTotalAssignments(
								unitSummaryDao.getTotalAssignments(dto.getIdPerson(), ServiceConstants.TA));
						if (assignWorkloadReq.getSzCdUnitProgram().equals(ServiceConstants.CUNITPGM_APS)) {
							// CSECC3D
							dto.setScrNbrTotInv30(
									unitSummaryDao.getInvSvcAssignmentsTa(dto.getIdPerson(), ServiceConstants.TA_30));
							// CSECC4D
							dto.setScrNbrTotSvc60(
									unitSummaryDao.getInvSvcAssignmentsTa(dto.getIdPerson(), ServiceConstants.TA_60));
						}
					}
				} else if (assignWorkloadReq.getRbSameName().equals(ServiceConstants.RB_CHAR_TWO)) {
					// CallCSEC80D
					for (UnitSummaryDto dto : unitSummaryDetails) {
						dto.setUsScrNbrTotalAssignments(
								unitSummaryDao.getTotalAssignments(dto.getIdPerson(), ServiceConstants.TPA));
						if (assignWorkloadReq.getSzCdUnitProgram().equals(ServiceConstants.CUNITPGM_APS)) {
							// CSECC5D
							dto.setScrNbrTotInv30(
									unitSummaryDao.getInvSvcAssignmentsTpa(dto.getIdPerson(), ServiceConstants.TA_30));
							// CSECC6D
							dto.setScrNbrTotSvc60(
									unitSummaryDao.getInvSvcAssignmentsTpa(dto.getIdPerson(), ServiceConstants.TA_60));
						}
					}
				}
			}
		}
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		return res;

	}

	/**
	 * 
	 * Method Description: This method is used to give full name.
	 * 
	 * @param unitSummaryDetails
	 * @return void
	 * 
	 */
	private void prepareFullName(List<UnitSummaryDto> unitSummaryDetails) {
		for (UnitSummaryDto dto : unitSummaryDetails) {
			String lastNm = dto.getNmEmployeeLast() == null ? "" : dto.getNmEmployeeLast();
			String firstNm = dto.getNmEmployeeFirst() == null ? "" : dto.getNmEmployeeFirst();
			String middleNm = dto.getNmEmployeeMiddle() == null ? "" : dto.getNmEmployeeMiddle();
			dto.setNmPersonFull(lastNm + "," + firstNm + middleNm);
		}
	}
}
