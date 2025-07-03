package us.tx.state.dfps.service.pcaeligdetermination.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;
import us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaEligDetermUtils Oct 16, 2017- 12:19:59 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PcaEligDetermUtils {

	@Autowired
	PcaUtility pcaUtility;

	@Autowired
	PcaAppAndBackgroundDao pcaAppAndBackgroundDao;

	public String determinePrelimEligibility(PcaApplAndDetermDBDto pcaAppDb) {
		String cdPrelimEligibility = "";

		PcaAppAndBackgroundDto appValueBean = pcaAppDb.getAppValueBean();
		String indCtznshpUnknown = appValueBean.getIndCtznshpUnknown();

		if (ServiceConstants.Y.equals(indCtznshpUnknown)) {
			cdPrelimEligibility = ServiceConstants.CELIGIBI_020; // State-Paid
		}

		else if (ServiceConstants.Y.equals(appValueBean.getIndChildSibling1())) {
			cdPrelimEligibility = findPrelimEligNoSibling(appValueBean);
		} else {

			cdPrelimEligibility = findPrelimEligWithValidSibling(pcaAppDb);
		}

		return cdPrelimEligibility;
	}

	private String findPrelimEligNoSibling(PcaAppAndBackgroundDto appValueBean) {
		String cdPrelimEligibility = "";

		String indCtznshpUs = appValueBean.getIndCtznshpUs();
		String indCtznshpPerm = appValueBean.getIndCtznshpPerm();
		String indCtznshpOthQualAlien = appValueBean.getIndCtznshpOthQualAlien();
		String indCtznshpUnknown = appValueBean.getIndCtznshpUnknown();
		Date dtChildEnteredUs = appValueBean.getDtChildEnteredUs();

		String cdFCActEligibility = appValueBean.getCdFceEligActual();

		if (ServiceConstants.Y.equals(indCtznshpPerm) || ServiceConstants.Y.equals(indCtznshpOthQualAlien)) {
			int yearsSinceChildEnteredUs = DateUtils.getDateDiffInYears(dtChildEnteredUs, new Date());
			boolean isBefore22Aug1996 = DateUtils.isBefore(dtChildEnteredUs, DateUtils.getJavaDate(1996, 8, 22));

			if (yearsSinceChildEnteredUs >= 5 || isBefore22Aug1996) {
				cdPrelimEligibility = cdFCActEligibility;
			} else {
				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}
		}
		// else if (CTZ a or d is checked)
		else if (ServiceConstants.Y.equals(indCtznshpUs) || ServiceConstants.Y.equals(indCtznshpUnknown)) {
			cdPrelimEligibility = cdFCActEligibility;
		}

		return cdPrelimEligibility;
	}

	private String findPrelimEligWithValidSibling(PcaApplAndDetermDBDto pcaAppDb) {
		String cdPrelimEligibility = "";
		PcaAppAndBackgroundDto appValueBean = pcaAppDb.getAppValueBean();

		String cdEligChild2 = findPrelimEligNoSibling(appValueBean);
		int child2USStayYears = DateUtils.getDateDiffInYears(appValueBean.getDtChildEnteredUs(), new Date());

		boolean isChild2Citizen = (ServiceConstants.Y.equals(appValueBean.getIndCtznshpUs()));
		boolean isChild2PR = (ServiceConstants.Y.equals(appValueBean.getIndCtznshpPerm()));
		boolean isChild2PRFor5Years = isChild2PR && (child2USStayYears >= 5);
		boolean isChild2PRLessThan5Years = isChild2PR && (child2USStayYears < 5);

		boolean isChild2TitleIVE = ServiceConstants.CELIGIBI_010.equals(cdEligChild2);
		boolean isChild2StatePaid = ServiceConstants.CELIGIBI_020.equals(cdEligChild2);

		String cdEligChild1 = pcaAppDb.getCdQualSiblFceEligActual();

		String[] statusArray = { ServiceConstants.CEVTSTAT_PEND, ServiceConstants.CEVTSTAT_COMP,
				ServiceConstants.CEVTSTAT_APRV };
		PcaAppAndBackgroundDto latestChild1App = pcaAppAndBackgroundDao
				.selectLatestApplication(appValueBean.getIdQualSibPerson(), statusArray);
		if (latestChild1App.getIdPcaEligApplication() == 0) {
			throw new DataNotFoundException("Can not find Valid PCA Application for the Sibling.");
		}
		int child1USStayYears = DateUtils.getDateDiffInYears(latestChild1App.getDtChildEnteredUs(), new Date());

		boolean isChild1Citizen = (ServiceConstants.Y.equals(latestChild1App.getIndCtznshpUs()));
		boolean isChild1PR = (ServiceConstants.Y.equals(latestChild1App.getIndCtznshpPerm()));
		boolean isChild1QalifiedAlien = (ServiceConstants.Y.equals(latestChild1App.getIndCtznshpOthQualAlien()));
		boolean isChild1PROrOQAFor5Years = (isChild1PR || isChild1QalifiedAlien) && (child1USStayYears >= 5);
		boolean isChild1PROrOQALessThan5Years = (isChild1PR || isChild1QalifiedAlien) && (child1USStayYears < 5);

		boolean isChild1TitleIVE = ServiceConstants.CELIGIBI_010.equals(cdEligChild1);
		boolean isChild1StatePaid = ServiceConstants.CELIGIBI_020.equals(cdEligChild1);

		if (isChild1TitleIVE && (isChild1Citizen || isChild1PROrOQAFor5Years)) {

			if (isChild2TitleIVE && (isChild2Citizen || isChild2PRFor5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_010;
			}

			else if (isChild2StatePaid && (isChild2Citizen || isChild2PRFor5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_010;
			}

			else if ((isChild2TitleIVE || isChild2StatePaid)
					&& (isChild2Citizen == false || isChild2PRLessThan5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}

			else if (appValueBean.getIdFceEligibility() == 0 && (isChild2Citizen || isChild2PRFor5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_010;
			}

			else if (appValueBean.getIdFceEligibility() == 0
					&& (isChild2Citizen == false || isChild2PRLessThan5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020; // State-Paid
			}
		}

		else if (isChild1StatePaid && (isChild1Citizen || isChild1PROrOQAFor5Years)) {

			if (isChild2TitleIVE && (isChild2Citizen || isChild2PRFor5Years)) {

				int plcmtDuration = pcaUtility.findPlacementDuration(appValueBean.getIdPlcmtEvent());
				if (plcmtDuration < 6) {
					plcmtDuration = pcaUtility.findPlacementDuration(appValueBean.getPriorPlcmtEventList());
				}

				if (plcmtDuration < 6) {
					cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
				} else {
					cdPrelimEligibility = ServiceConstants.CELIGIBI_010;
				}
			}

			else if (isChild2StatePaid && (isChild2Citizen || isChild2PRFor5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}

			else if (isChild2StatePaid && (isChild2Citizen == false || isChild2PRLessThan5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}
		}

		else if ((isChild1TitleIVE || isChild1StatePaid)
				&& (isChild1Citizen == false || isChild1PROrOQALessThan5Years)) {

			if (isChild2TitleIVE && (isChild2Citizen || isChild2PRFor5Years)) {

				int plcmtDuration = pcaUtility.findPlacementDuration(appValueBean.getIdPlcmtEvent());
				if (plcmtDuration < 6) {
					plcmtDuration = pcaUtility.findPlacementDuration(appValueBean.getPriorPlcmtEventList());
				}

				if (plcmtDuration < 6) {
					cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
				} else {
					cdPrelimEligibility = ServiceConstants.CELIGIBI_010;
				}
			}

			else if (isChild2StatePaid && (isChild2Citizen || isChild2PRFor5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}

			else if (isChild2StatePaid && (isChild2Citizen == false || isChild2PRLessThan5Years)) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}

			else if (appValueBean.getIdFceEligibility() == 0) {

				cdPrelimEligibility = ServiceConstants.CELIGIBI_020;
			}
		}
		return cdPrelimEligibility;
	}

	public boolean determineFinalEligibility(PcaApplAndDetermDBDto pcaAppDb) {
		boolean childQualified = false;
		PcaEligDeterminationDto determValueBean = pcaAppDb.getDetermValueBean();
		if (ServiceConstants.Y.equals(pcaAppDb.getAppValueBean().getIndFairHearing())) {
			childQualified = true;
		} else {
			Date dtPcaAgreement = determValueBean.getDtPcaAgreement();
			Date dtPmcLegalStatus = determValueBean.getDtPmcLegalStatus();
			if (!DateUtils.isNull(dtPcaAgreement) && !DateUtils.isNull(dtPmcLegalStatus)) {
				if (dtPcaAgreement.compareTo(dtPmcLegalStatus) <= 0) {
					childQualified = true;
				}
			}
		}

		return childQualified;
	}

	/**
	 * Method Name: determineQualification Method Description:
	 * 
	 * @param appValueBean
	 * @param determValueBean
	 * @return
	 */
	public static boolean determineQualification(PcaAppAndBackgroundDto appValueBean,
			PcaEligDeterminationDto determValueBean) {
		boolean isQualified = true;

		// If Current Child does not have valid Sibling (Child 1)
		if (ServiceConstants.Y.equals(appValueBean.getIndChildSibling1())) {
			// Placement Requirements.
			if (!isPlacementReqMet(appValueBean)) {
				isQualified = false;
				determValueBean.setIndPlcmtReqMetOutcome(ServiceConstants.N);
			} else {
				determValueBean.setIndPlcmtReqMetOutcome(ServiceConstants.Y);
			}

			// PCA Requirements
			// If Fair Hearing Check box is checked, PCA Requirements does not
			// apply
			if (!isFairHearingSelected(appValueBean)) {
				if (!isPCARequirementsMet(appValueBean)) {
					isQualified = false;
					determValueBean.setIndPcaReqMetOutcome(ServiceConstants.N);
				} else {
					determValueBean.setIndPcaReqMetOutcome(ServiceConstants.Y);
				}
			}
		}
		// If Child has valid sibling (Child - 2), Child is automatically
		// Qualified.

		// Qualification of the Child does not depend upon Citizenship status of
		// the Child.
		// But checking the citizenship status to populate the Question on
		// Worksheet Page.
		if (isCitizenshipReqMet(appValueBean) == false) {
			determValueBean.setIndCtznshpOutcome(ServiceConstants.N);
		} else {
			determValueBean.setIndCtznshpOutcome(ServiceConstants.Y);
		}

		return isQualified;
	}

	/**
	 * Method Name: isCitizenshipReqMet Method Description:
	 * 
	 * @param appValueBean
	 * @return
	 */
	private static boolean isCitizenshipReqMet(PcaAppAndBackgroundDto appValueBean) {
		boolean citizenshipRetMet = false;

		String indCtznshpUs = appValueBean.getIndCtznshpUs();
		String indCtznshpPerm = appValueBean.getIndCtznshpPerm();
		String indCtznshpOthQualAlien = appValueBean.getIndCtznshpOthQualAlien();

		// U.S. Citizen
		if (ServiceConstants.Y.equals(indCtznshpUs)) {
			citizenshipRetMet = true;
		}

		// PR or Qualified Alien.
		if (ServiceConstants.Y.equals(indCtznshpPerm) || ServiceConstants.Y.equals(indCtznshpOthQualAlien)) {
			if (isPermResOrQualAlienOver5Yrs(appValueBean)) {
				citizenshipRetMet = true;
			}
		}

		return citizenshipRetMet;
	}

	/**
	 * Method Name: isPermResOrQualAlienOver5Yrs Method Description: Returns if
	 * the child is Permanent Resident or qualified alien over 5 yrs
	 * 
	 * @param appValueBean
	 * @return
	 */
	private static boolean isPermResOrQualAlienOver5Yrs(PcaAppAndBackgroundDto appValueBean) {
		boolean result = false;
		if (isPermResOrQualAlien(appValueBean)) {
			if (!DateUtils.isNull(appValueBean.getDtChildEnteredUs())) {
				// Add 5 years to dtChildEnteredUs.
				Date dtChildEnteredUsPlus5yrs = DateUtils.addToDate(appValueBean.getDtChildEnteredUs(), 5, 0, 0);
				if (new Date().compareTo(dtChildEnteredUsPlus5yrs) >= 0) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Method Name: isPermResOrQualAlien Method Description:Returns if the child
	 * is Permanent Resident or qualified alien.
	 * 
	 * @param appValueBean
	 * @return
	 */
	private static boolean isPermResOrQualAlien(PcaAppAndBackgroundDto appValueBean) {
		return (ServiceConstants.Y.equals(appValueBean.getIndCtznshpPerm())
				|| ServiceConstants.Y.equals(appValueBean.getIndCtznshpOthQualAlien()));
	}

	private static boolean isPlacementReqMet(PcaAppAndBackgroundDto appValueBean) {
		boolean plcmtReqMet = false;

		// Validations are in place for selected Placement. So at this point, it
		// can
		// be assumed that the selected placement is Valid.
		// But user can answer both placement questions No continue to
		// "Determine Qualification"
		// If answers to both Placement questions are No and valid sibling is
		// not selected,
		// then the child is not qualified.
		if (ServiceConstants.Y.equals(appValueBean.getIndEligFcmp6mthsRel())
				|| ServiceConstants.Y.equals(appValueBean.getIndEligFcmp6mthsFicKin())) {
			plcmtReqMet = true;
		}

		return plcmtReqMet;
	}

	private static boolean isFairHearingSelected(PcaAppAndBackgroundDto appValueBean) {
		return (ServiceConstants.Y.equals(appValueBean.getIndFairHearing()));
	}

	private static boolean isPCARequirementsMet(PcaAppAndBackgroundDto appValueBean) {
		boolean pcaReqMet = false;
		Long age = appValueBean.getNbrChildQualifyAge();

		// 2 and 3 must be Yes in all cases.
		if (ServiceConstants.Y.equals(appValueBean.getIndNoReturnHome())
				&& ServiceConstants.Y.equals(appValueBean.getIndRelAttachment())) {
			pcaReqMet = true;
		}

		// If child >= 14 - 2, 3 and 4 must be Yes
		if (age >= 14 && ServiceConstants.Y.equals(appValueBean.getIndChildConsulted())) {
			pcaReqMet = true;

		}

		return pcaReqMet;
	}

}
