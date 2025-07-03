/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 17, 2017- 10:45:21 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casepackage.dao.CaseHistoryDao;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryAllegDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryCommonDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryStageDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryStagePCSPDto;
import us.tx.state.dfps.service.casepackage.dto.CaseHistoryUtcStageDto;
import us.tx.state.dfps.service.casepackage.service.CaseHistoryService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryReq;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryRes;
import us.tx.state.dfps.service.cps.service.CpsClosingSummaryService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 17, 2017- 10:45:21 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class CaseHistoryServiceImpl implements CaseHistoryService {

	@Autowired
	CaseHistoryDao caseHistoryDao;

	@Autowired
	CpsClosingSummaryService cpsClosingSummaryService;

	/**
	 * Method Name: getCaseHistory Method Description: Retrieves the Case
	 * History Data for the passed Case Id in the request
	 * 
	 * @param retrvCaseHistoryReq
	 * @return RetrvCaseHistoryRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RetrvCaseHistoryRes getCaseHistory(RetrvCaseHistoryReq retrvCaseHistoryReq) {
		RetrvCaseHistoryRes retrvCaseHistoryRes = new RetrvCaseHistoryRes();
		List<CaseHistoryStagePCSPDto> casePCPSDetails;
		// Get the Case List for the Case
		List<CaseHistoryCaseDto> caseHistoryCaseList = caseHistoryDao.getCaseHistoryCaseList(retrvCaseHistoryReq);
		if (retrvCaseHistoryReq.isCallStageListServ()) {
			List<CaseHistoryCommonDto> caseHistoryCommonDtlsList;
			int i = 0;
			// details
			for (CaseHistoryCaseDto caseHistoryCaseDto : caseHistoryCaseList) {
				caseHistoryCommonDtlsList = new ArrayList<>();
				caseHistoryCommonDtlsList = caseHistoryDao.getCaseHistoryStageCommonList(caseHistoryCaseDto.getIdCase(),
						retrvCaseHistoryReq.getTransactionId());
				if (null != caseHistoryCommonDtlsList && caseHistoryCommonDtlsList.size() > 0) {
					// Call the populateCaseHistory method to split the Stage
					// and
					// Allegation data into their respective Dtos
					casePCPSDetails = new ArrayList<>();
					casePCPSDetails = caseHistoryDao.getPCSPDetails(caseHistoryCaseDto.getIdCase());
					caseHistoryCaseList.get(i).setCaseHistoryStageList(populateCaseHistoryList(
							caseHistoryCommonDtlsList, caseHistoryCaseDto.getIndSafetyCheckList(), casePCPSDetails));
					if (getUTCIndValue(caseHistoryCaseList.get(i).getCaseHistoryStageList())) {
						caseHistoryCaseList.get(i).setUnableToComp(ServiceConstants.YES);
					}
				}
				i++;
			}
		} else {
			List<Long> caseIdList = new ArrayList<>();
			for (CaseHistoryCaseDto caseHistoryCaseDto : caseHistoryCaseList) {
				caseIdList.add(caseHistoryCaseDto.getIdCase());
			}
			if (!ObjectUtils.isEmpty(caseIdList)) {
				if (caseIdList.size() > 1000) {
					caseIdList = caseIdList.stream().limit(1000).collect(Collectors.toList());
				}
				List<CaseHistoryUtcStageDto> caseHistoryUtcStageList = caseHistoryDao
						.getCaseHistoryStageClosedList(caseIdList, retrvCaseHistoryReq.getTransactionId());
				int i = 0;
				for (CaseHistoryCaseDto caseHistoryCaseDto : caseHistoryCaseList) {
					if (getUTCInd(caseHistoryUtcStageList, caseHistoryCaseDto.getIdCase())) {
						caseHistoryCaseList.get(i).setUnableToComp(ServiceConstants.YES);
					}
					i++;
				}
			}
		}
		retrvCaseHistoryRes.setCaseHistoryCaseList(caseHistoryCaseList);
		return retrvCaseHistoryRes;
	}

	/**
	 * 
	 * Method Name: populateCaseHistoryList Method Description: Reformat the
	 * Common Case History Dto to StageDto and Allegation Dto
	 * 
	 * @param caseHistoryCommonDtoList
	 * @return ArrayList<CaseHistoryStageDto>
	 */
	public ArrayList<CaseHistoryStageDto> populateCaseHistoryList(List<CaseHistoryCommonDto> caseHistoryCommonDtoList,
			String indChildSafetyCheckAlert, List<CaseHistoryStagePCSPDto> casePCSPDetails) {
		CaseHistoryStageDto caseHistoryStageDto;
		Map<Long, CaseHistoryStageDto> caseStageHistoryMap = new TreeMap<Long, CaseHistoryStageDto>();
		List<CaseHistoryCommonDto> tempCaseHistCommonDtoList = new ArrayList<>();
		ArrayList<CaseHistoryStageDto> caseStageHistoryList;
		for (CaseHistoryCommonDto caseHistoryCommonDto : caseHistoryCommonDtoList) {
			// corresponding stage details
			if (null != caseHistoryCommonDto.getCdAllegType() || null != caseHistoryCommonDto.getCdIntAllegType()) {
				tempCaseHistCommonDtoList.add(caseHistoryCommonDto);
			}
			// Remove all duplicate stage records
			if (!caseStageHistoryMap.containsKey(caseHistoryCommonDto.getIdStage())) {
				caseHistoryStageDto = new CaseHistoryStageDto();
				if (null != caseHistoryCommonDto.getIdStage()) {
					caseHistoryStageDto.setIdStage(caseHistoryCommonDto.getIdStage());
				}
				if (null != caseHistoryCommonDto.getCdStage()) {
					caseHistoryStageDto.setCdStage(caseHistoryCommonDto.getCdStage());
					if (caseHistoryCommonDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INV)) {
						// This is set to empty string here, so that when this
						// field is updated in the controller by fetching it, it
						// does not throw a null pointer exception
						caseHistoryStageDto.setCodeTypeForRcmmdActn(ServiceConstants.EMPTY_STRING);
						if (null != caseHistoryCommonDto.getCdCpsOverallDisp()) {
							caseHistoryStageDto.setCdOverallDisp(caseHistoryCommonDto.getCdCpsOverallDisp());
						}
						if (null != caseHistoryCommonDto.getReasonInvClosed()) {
							caseHistoryStageDto.setReasonInvClosed(caseHistoryCommonDto.getReasonInvClosed());
						}
						if (null != caseHistoryCommonDto.getReasOpenSrvcs()) {
							caseHistoryStageDto.setReasonOpenServ(caseHistoryCommonDto.getReasOpenSrvcs());
						}
					} else if (caseHistoryCommonDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_AR)) {
						if (null != caseHistoryCommonDto.getArTxtFactor76()) {
							caseHistoryStageDto.setArSafetyFactor76(caseHistoryCommonDto.getArTxtFactor76());
						}
						if (null != caseHistoryCommonDto.getArTxtFactor77()) {
							caseHistoryStageDto.setArSafetyFactor77(caseHistoryCommonDto.getArTxtFactor77());
						}
						if (null != caseHistoryCommonDto.getArTxtFactor78()) {
							caseHistoryStageDto.setArSafetyFactor78(caseHistoryCommonDto.getArTxtFactor78());
						}
						if (null != caseHistoryCommonDto.getArTxtFactor79()) {
							caseHistoryStageDto.setArSafetyFactor79(caseHistoryCommonDto.getArTxtFactor79());
						}
					} else if (ServiceConstants.CSTAGES_FPR.equalsIgnoreCase(caseHistoryCommonDto.getCdStage())) {
						caseHistoryStageDto.setPcspDetails(casePCSPDetails);
					}
				}
				if (null != caseHistoryCommonDto.getCdFinalRiskNew()) {
					caseHistoryStageDto.setCdSdmFinalRiskLvl(caseHistoryCommonDto.getCdFinalRiskNew());
				} else if (null != caseHistoryCommonDto.getCdFinalRiskLvl()) {
					caseHistoryStageDto.setCdSdmFinalRiskLvl(caseHistoryCommonDto.getCdFinalRiskLvl());
				}
				if (null != caseHistoryCommonDto.getCdSdmRiskStatus()) {
					caseHistoryStageDto.setCdSdmRiskStatus(caseHistoryCommonDto.getCdSdmRiskStatus());
				}
				if (null != caseHistoryCommonDto.getCdRiskAssmtFind()) {
					caseHistoryStageDto.setCdRiskFinding(caseHistoryCommonDto.getCdRiskAssmtFind());
				}
				if (null != caseHistoryCommonDto.getCdSafetyDecsnNew()) {
					caseHistoryStageDto.setCdSdmFinalDecision(caseHistoryCommonDto.getCdSafetyDecsnNew());
				} else if (null != caseHistoryCommonDto.getCdSafetyDecision()) {
					caseHistoryStageDto.setCdSdmFinalDecision(caseHistoryCommonDto.getCdSafetyDecision());
				}
				if (null != caseHistoryCommonDto.getNmHousehold()) {
					caseHistoryStageDto.setNmHousehold(caseHistoryCommonDto.getNmHousehold());
				}
				if (null != caseHistoryCommonDto.getNmHouseholdAR()) {
					caseHistoryStageDto.setNmHouseholdAR(caseHistoryCommonDto.getNmHouseholdAR());
				}
				if (null != caseHistoryCommonDto.getDtStageClose()) {
					caseHistoryStageDto.setDtStageClosed(caseHistoryCommonDto.getDtStageClose());
				}
				if (null != caseHistoryCommonDto.getCdStageReasClosed()) {
					caseHistoryStageDto.setCdReasonClosed(caseHistoryCommonDto.getCdStageReasClosed());
				}
				if (null != caseHistoryCommonDto.getAssmtDiscussion()) {
					caseHistoryStageDto.setAssmtDiscussion(caseHistoryCommonDto.getAssmtDiscussion());
				}
				if (null != caseHistoryCommonDto.getDtStageStart()) {
					caseHistoryStageDto.setDtStageOpened(caseHistoryCommonDto.getDtStageStart());
				}
				if (null != caseHistoryCommonDto.getIndFatality()) {
					caseHistoryStageDto.setIndFatality(caseHistoryCommonDto.getIndFatality());
				}
				if (null != indChildSafetyCheckAlert) {
					caseHistoryStageDto.setIndChildSafetyCheckAlert(indChildSafetyCheckAlert);
				}
				if (null != caseHistoryCommonDto.getNmStage()) {
					caseHistoryStageDto.setNmStage(caseHistoryCommonDto.getNmStage());
				}
				if (null != caseHistoryCommonDto.getCdStagePriority()) {
					caseHistoryStageDto.setCdStagePriority(caseHistoryCommonDto.getCdStagePriority());
				}
				if (null != caseHistoryCommonDto.getCdInvCnclsnStatus()) {
					caseHistoryStageDto.setCdInvCnclsnStatus(caseHistoryCommonDto.getCdInvCnclsnStatus());
				}
				if (null != caseHistoryCommonDto.getCdArCnclsnStatus()) {
					caseHistoryStageDto.setCdArCnclsnStatus(caseHistoryCommonDto.getCdArCnclsnStatus());
				}
				if (null != caseHistoryCommonDto.getIntakeNarrative()) {
					caseHistoryStageDto.setIntakeNarrative(caseHistoryCommonDto.getIntakeNarrative());
				}
				if (null != caseHistoryCommonDto.getCdArCnclsnStatus()) {
					caseHistoryStageDto.setCdArCnclsnStatus(caseHistoryCommonDto.getCdArCnclsnStatus());
				}
				if (null != caseHistoryCommonDto.getCdArClosureReason()) {
					caseHistoryStageDto.setCdArClosureReason(caseHistoryCommonDto.getCdArClosureReason());
				}
				if (null != caseHistoryCommonDto.getTxtReasonArClosed()) {
					caseHistoryStageDto.setTxtReasonArClosed(caseHistoryCommonDto.getTxtReasonArClosed());
				}
				if (null != caseHistoryCommonDto.getTxtReasonArOpenServ()) {
					caseHistoryStageDto.setTxtReasonArOpenServ(caseHistoryCommonDto.getTxtReasonArOpenServ());
				}
				/* Legal Status Details for FRE, FSU and SUB Stages */
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getDtLegalStatusEff())) {
					caseHistoryStageDto.setDtLegalStatusEff(caseHistoryCommonDto.getDtLegalStatusEff());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getCdLegalCounty())) {
					caseHistoryStageDto.setCdLegalCounty(caseHistoryCommonDto.getCdLegalCounty());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getTxtCauseNumber())) {
					caseHistoryStageDto.setTxtCauseNumber(caseHistoryCommonDto.getTxtCauseNumber());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getCdLegalStatus())) {
					caseHistoryStageDto.setCdLegalStatus(caseHistoryCommonDto.getCdLegalStatus());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getCdDischargeReas())) {
					caseHistoryStageDto.setCdDischargeReas(caseHistoryCommonDto.getCdDischargeReas());
				}
				/* Placement Information Details */
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getCdPlacementType())) {
					caseHistoryStageDto.setCdPlacementType(caseHistoryCommonDto.getCdPlacementType());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getCdLivingArrangeType())) {
					caseHistoryStageDto.setCdLivingArrangeType(caseHistoryCommonDto.getCdLivingArrangeType());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getDtPlacementStart())) {
					caseHistoryStageDto.setDtPlacementStart(caseHistoryCommonDto.getDtPlacementStart());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getDtPlacementEnd())) {
					caseHistoryStageDto.setDtPlacementEnd(caseHistoryCommonDto.getDtPlacementEnd());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getNmPlacementFacility())) {
					caseHistoryStageDto.setNmPlacementFacility(caseHistoryCommonDto.getNmPlacementFacility());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getNmPlacementPerson())) {
					caseHistoryStageDto.setNmPlacementPerson(caseHistoryCommonDto.getNmPlacementPerson());
				}
				/* For Closure Summary */
				if (null != caseHistoryCommonDto.getContactNarrative()) {
					caseHistoryStageDto.setContactNarrative(caseHistoryCommonDto.getContactNarrative());
				}
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getIdContactEvent())) {
					caseHistoryStageDto.setIdContactEvent(caseHistoryCommonDto.getIdContactEvent());
				}
				/* PAL Living Arrangement */
				if (!ObjectUtils.isEmpty(caseHistoryCommonDto.getCdPALLivingArrange())) {
					caseHistoryStageDto.setCdPALLivingArrange(caseHistoryCommonDto.getCdPALLivingArrange());
				}
				caseStageHistoryMap.put(caseHistoryCommonDto.getIdStage(), caseHistoryStageDto);
			}
		}
		caseStageHistoryList = new ArrayList<CaseHistoryStageDto>(caseStageHistoryMap.values());
		CaseHistoryAllegDto caseHistoryAllegDto;
		List<CaseHistoryAllegDto> caseHistoryAllegationList;
		int i = 0;
		// Extract and set the allegation data into their corresponding stages
		for (CaseHistoryStageDto caseHistoryStgDto : caseStageHistoryList) {
			caseHistoryAllegationList = new ArrayList<>();
			if (null != caseHistoryStgDto.getCdStage()
					&& (caseHistoryStgDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT)
							|| caseHistoryStgDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INV))) {
				for (CaseHistoryCommonDto tempCaseHistCommonDto : tempCaseHistCommonDtoList) {
					if (tempCaseHistCommonDto.getIdStage().equals(caseHistoryStgDto.getIdStage())) {
						caseHistoryAllegDto = new CaseHistoryAllegDto();
						if (tempCaseHistCommonDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INV)) {
							if (null != tempCaseHistCommonDto.getCdAllegDisp()) {
								caseHistoryAllegDto.setCdAllegDisp(tempCaseHistCommonDto.getCdAllegDisp());
							}
							if (null != tempCaseHistCommonDto.getNmAllegPerp()) {
								caseHistoryAllegDto.setNmPerpetrator(tempCaseHistCommonDto.getNmAllegPerp());
							}
							if (null != tempCaseHistCommonDto.getCdAllegType()) {
								caseHistoryAllegDto.setCdAllegType(tempCaseHistCommonDto.getCdAllegType());
							}
							if (null != tempCaseHistCommonDto.getNmAllegVictim()) {
								caseHistoryAllegDto.setNmVictim(tempCaseHistCommonDto.getNmAllegVictim());
							}
							if (null != tempCaseHistCommonDto.getDispSeverity()) {
								caseHistoryAllegDto.setDispSeverity(tempCaseHistCommonDto.getDispSeverity());
							}
						} else {
							if (null != tempCaseHistCommonDto.getNmIntAllegVictim()) {
								caseHistoryAllegDto.setNmVictim(tempCaseHistCommonDto.getNmIntAllegVictim());
							}
							if (null != tempCaseHistCommonDto.getNmIntAllegPerp()) {
								caseHistoryAllegDto.setNmPerpetrator(tempCaseHistCommonDto.getNmIntAllegPerp());
							}
							if (null != tempCaseHistCommonDto.getCdIntAllegType()) {
								caseHistoryAllegDto.setCdAllegType(tempCaseHistCommonDto.getCdIntAllegType());
							}
						}
						caseHistoryAllegationList.add(caseHistoryAllegDto);
					}
				}
				if (null != caseHistoryAllegationList && caseHistoryAllegationList.size() > 0) {
					caseStageHistoryList.get(i).setCaseHistAllegList(caseHistoryAllegationList);
				}
			}
			i++;
		}
		return caseStageHistoryList;
	}

	/**
	 * 
	 * Method Name: getUTCIndValue Method Description: Populated the UTC
	 * indicator for the Case
	 * 
	 * @param caseStageHistoryList
	 * @return boolean
	 */
	public boolean getUTCIndValue(List<CaseHistoryStageDto> caseStageHistoryList) {
		boolean caseHasUTCStage = false;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		Date oneYearBefore = cal.getTime();
		for (CaseHistoryStageDto caseHistoryStageDto : caseStageHistoryList) {
			if (null != caseHistoryStageDto.getDtStageClosed()
					&& caseHistoryStageDto.getDtStageClosed().after(oneYearBefore)
					&& null != caseHistoryStageDto.getCdReasonClosed()
					&& caseHistoryStageDto.getCdReasonClosed().equalsIgnoreCase(ServiceConstants.CCINVCLS_83)) {
				caseHasUTCStage = true;
				break;
			}
		}
		return caseHasUTCStage;
	}

	public boolean getUTCInd(List<CaseHistoryUtcStageDto> caseHistoryUtcStageList, Long caseId) {
		boolean caseHasUTCStage = false;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		Date oneYearBefore = cal.getTime();
		for (CaseHistoryUtcStageDto caseHistoryStageDto : caseHistoryUtcStageList) {
			if (caseHistoryStageDto.getIdCase().equals(caseId) && null != caseHistoryStageDto.getDtStageClosed()
					&& caseHistoryStageDto.getDtStageClosed().after(oneYearBefore)
					&& null != caseHistoryStageDto.getCdReasonClosed()
					&& caseHistoryStageDto.getCdReasonClosed().equalsIgnoreCase(ServiceConstants.CCINVCLS_83)) {
				caseHasUTCStage = true;
				break;
			}
		}
		return caseHasUTCStage;
	}
}
