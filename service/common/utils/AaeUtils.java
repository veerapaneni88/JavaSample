package us.tx.state.dfps.service.common.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.EligibilityDeterminationDto;
import us.tx.state.dfps.service.admin.dto.AaeEligDetermMessgDto;
import us.tx.state.dfps.service.adoptionasstnc.AaeApplAndDetermDBDto;
import us.tx.state.dfps.service.adoptionasstnc.ApplicationBackgroundDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;

public class AaeUtils {

	public static String createApplEventDescription(String cdEventStatus) {
		String eventDescription = ServiceConstants.EMPTY_STRING;
		if (ServiceConstants.CEVTSTAT_NEW.equals(cdEventStatus)) {
			eventDescription = ServiceConstants.APPLICATION_NEW_EVENT_DESC;
		} else if (ServiceConstants.CEVTSTAT_PROC.equals(cdEventStatus)) {
			eventDescription = ServiceConstants.APPLICATION_PROC_EVENT_DESC;
		} else if (ServiceConstants.CEVTSTAT_PEND.equals(cdEventStatus)) {
			eventDescription = ServiceConstants.APPLICATION_PEND_EVENT_DESC;
		} else if (ServiceConstants.CEVTSTAT_COMP.equals(cdEventStatus)) {
			eventDescription = ServiceConstants.APPLICATION_COMP_EVENT_DESC;
		} else if (ServiceConstants.CEVTSTAT_APRV.equals(cdEventStatus)) {
			eventDescription = ServiceConstants.APPLICATION_APRV_EVENT_DESC;
		}
		return eventDescription;
	}

	public static Long[] messageListToIntegerArray(List<Long> messageList) {
		Long[] messages = new Long[messageList.size()];
		if (messageList.isEmpty()) {
			return null;
		}
		for (int i = ServiceConstants.Zero; i < messageList.size(); i++) {
			messages[i] = messageList.get(i);
		}
		return messages;
	}

	public static AaeEligDetermMessgDto determineQualification(AaeApplAndDetermDBDto aaeApplAndDetermDB) {
		ApplicationBackgroundDto applicationBackgroundDto = aaeApplAndDetermDB.getApplicationBackgroundDto();
		AaeEligDetermMessgDto aaeEligDetermMessgDto = new AaeEligDetermMessgDto();
		if (ServiceConstants.Y.equals(aaeApplAndDetermDB.getApplicationBackgroundDto().getIndDoctorLetter())) {
			aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
			List<Long> qualMessage = new ArrayList<>();
			qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
			aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			aaeEligDetermMessgDto.setEligMessages(qualMessage);
		} else {

			if (isCVSInDFPSOrAuthEntity(applicationBackgroundDto)) {
				aaeEligDetermMessgDto = calculateQualCVSDFPSorAuthEntity(aaeApplAndDetermDB);
			} else if (isCVSInLCPA(applicationBackgroundDto)) {
				aaeEligDetermMessgDto = calculateQualCVSLCPA(aaeApplAndDetermDB);
			} else if (isCVSNone(applicationBackgroundDto)) {
				aaeEligDetermMessgDto = calculateQualCVSNone(aaeApplAndDetermDB);
			} else {

				aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
				List<Long> qualMessage = new ArrayList<>();
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
				aaeEligDetermMessgDto.setEligMessages(qualMessage);
			}
		}

		return aaeEligDetermMessgDto;
	}

	private static AaeEligDetermMessgDto calculateQualCVSNone(AaeApplAndDetermDBDto aaeApplAndDetermDB) {
		ApplicationBackgroundDto applicationBackgroundDto = aaeApplAndDetermDB.getApplicationBackgroundDto();
		AaeEligDetermMessgDto aaeEligDetermMessgDto = new AaeEligDetermMessgDto();
		List<Long> qualMessage = new ArrayList<>();

		AaeEligDetermMessgDto determChildsIVEOrStateCitznOutcomeValueBean = determineChildsIVEOrStateCitizenshipOutcome(
				aaeApplAndDetermDB, new AaeEligDetermMessgDto());
		if (isChildInternationallyAdptd(applicationBackgroundDto)) {
			if (specialNeedsExists(applicationBackgroundDto) && isAdptParentsTexasRes(applicationBackgroundDto)) {
				aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_ELIG_NR);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NON_RECUR);
			} else {
				aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			}
		} else

		{
			if (specialNeedsExists(applicationBackgroundDto) && isAdptParentsTexasRes(applicationBackgroundDto)) {
				if (ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID == determChildsIVEOrStateCitznOutcomeValueBean
						.getTypeOfElig()) {
					aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
					qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE);
					aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_PAID);
				} else {
					aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
					qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
					aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
				}
			} else {
				aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			}
		}
		aaeEligDetermMessgDto.setEligMessages(qualMessage);
		return aaeEligDetermMessgDto;

	}

	private static AaeEligDetermMessgDto calculateQualCVSLCPA(AaeApplAndDetermDBDto aaeApplAndDetermDB) {
		ApplicationBackgroundDto appBackgroundValueBean = aaeApplAndDetermDB.getApplicationBackgroundDto();
		AaeEligDetermMessgDto aaeEligDetermMessgValueBean = new AaeEligDetermMessgDto();
		List<Long> qualMessage = new ArrayList<>();

		if (adoptiveHmStudyExists(appBackgroundValueBean) && backgroundChkPerfd(appBackgroundValueBean)
				&& specialNeedsExists(appBackgroundValueBean) && isAdptParentsTexasRes(appBackgroundValueBean)) {

			AaeEligDetermMessgDto determChildsIVEOrStateCitznOutcomeValueBean = determineChildsIVEOrStateCitizenshipOutcome(
					aaeApplAndDetermDB, new AaeEligDetermMessgDto());

			if (ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID == determChildsIVEOrStateCitznOutcomeValueBean
					.getTypeOfElig()) {
				aaeEligDetermMessgValueBean.setChildQualified(Boolean.TRUE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE);
				aaeEligDetermMessgValueBean.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_PAID);
			} else {
				aaeEligDetermMessgValueBean.setChildQualified(Boolean.FALSE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
				aaeEligDetermMessgValueBean.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			}
		} else {
			aaeEligDetermMessgValueBean.setChildQualified(Boolean.FALSE);
			qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
			aaeEligDetermMessgValueBean.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
		}
		aaeEligDetermMessgValueBean.setEligMessages(qualMessage);
		return aaeEligDetermMessgValueBean;
	}

	public static boolean isCVSInDFPSOrAuthEntity(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndMngngCvs())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndOtherMngngCvs()));
	}

	public static boolean isCVSInLCPA(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndLcpaMngngCvs()));
	}

	public static boolean isCVSNone(ApplicationBackgroundDto applicationBackgroundDto) {
		return (!ServiceConstants.Y.equals(applicationBackgroundDto.getIndMngngCvs())
				&& !ServiceConstants.Y.equals(applicationBackgroundDto.getIndLcpaMngngCvs())
				&& !ServiceConstants.Y.equals(applicationBackgroundDto.getIndOtherMngngCvs()));
	}

	private static AaeEligDetermMessgDto calculateQualCVSDFPSorAuthEntity(AaeApplAndDetermDBDto aaeApplAndDetermDB) {
		ApplicationBackgroundDto applicationBackgroundDto = aaeApplAndDetermDB.getApplicationBackgroundDto();
		AaeEligDetermMessgDto aaeEligDetermMessgDto = new AaeEligDetermMessgDto();

		if (adoptiveHmStudyExists(applicationBackgroundDto) && backgroundChkPerfd(applicationBackgroundDto)
				&& specialNeedsExists(applicationBackgroundDto) && reasonableEffortsTaken(applicationBackgroundDto)) {
			determineChildsIVEOrStateCitizenshipOutcome(aaeApplAndDetermDB, aaeEligDetermMessgDto);
		} else {
			List<Long> qualMessage = new ArrayList<>();
			aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
			qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
			aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			aaeEligDetermMessgDto.setEligMessages(qualMessage);
		}
		return aaeEligDetermMessgDto;
	}

	public static boolean specialNeedsExists(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndChildSix())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndChildTwoMinority())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndWithSibling())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndSsaMedReq())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndHandicapCond()));
	}

	public static boolean adoptiveHmStudyExists(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndAdptHomeStudy()));
	}

	public static boolean backgroundChkPerfd(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndBkgCheck()));
	}

	public static boolean reasonableEffortsTaken(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndAdptRegExch())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndLoctAdptFam())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndNoAdptPlcRslt())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndEmotBond()));
	}

	private static AaeEligDetermMessgDto determineChildsIVEOrStateCitizenshipOutcome(
			AaeApplAndDetermDBDto aaeApplAndDetermDBDto, AaeEligDetermMessgDto aaeEligDetermMessgDto) {
		ApplicationBackgroundDto applicationBackgroundDto = aaeApplAndDetermDBDto.getApplicationBackgroundDto();
		List<Long> qualMessage = new ArrayList<>();
		if (isChildUSCitizen(aaeApplAndDetermDBDto)) {
			aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
			qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE_OR_STATE);
			aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID);
			aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.TRUE);
		} else if (isPermResOrQualAlien(applicationBackgroundDto)) {
			if (isPermResOrQualAlienOver5Yrs(applicationBackgroundDto)
					&& !DateUtils.isNull(applicationBackgroundDto.getDtChildEnteredUs())) {
				aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE_OR_STATE);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID);
				aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.TRUE);
			} else {
				if (isChildAdptByCitizenOrPROrQA(applicationBackgroundDto)) {
					aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
					qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE_OR_STATE);
					aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID);
					aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.TRUE);
				} else {
					aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
					qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_IVE);
					qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_STATE_PAID);
					aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_STATE_PAID);
					aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.FALSE);
				}
			}
		} else if (isChildsImmigStatusUnknown(applicationBackgroundDto)) {
			if (isChildAdptByCitizenOrPROrQA(applicationBackgroundDto)) {
				aaeEligDetermMessgDto.setChildQualified(Boolean.TRUE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_QUAL_IVE_OR_STATE);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID);
				aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.TRUE);
			} else {
				aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
				qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
				aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
				aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.FALSE);
			}
		} else {
			aaeEligDetermMessgDto.setChildQualified(Boolean.FALSE);
			qualMessage.add(ServiceConstants.MSG_AA_CHLD_NOT_QUAL_BENFT);
			aaeEligDetermMessgDto.setTypeOfElig(ServiceConstants.ELIG_TYPE_NO_ELIG);
			aaeEligDetermMessgDto.setDoesChildMeetsCitizenshipReq(Boolean.FALSE);
		}

		aaeEligDetermMessgDto.setEligMessages(qualMessage);
		return aaeEligDetermMessgDto;
	}

	public static boolean isChildUSCitizen(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
		return (ServiceConstants.Y.equals(aaeApplAndDetermDBDto.getApplicationBackgroundDto().getIndCtznshpUs()));
	}

	public static boolean isChildInternationallyAdptd(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndInternationalPlcmt()));
	}

	/**
	 * Returns if Adoptive parents are Resident of Texas.
	 * 
	 * @param applicationBackgroundDto
	 * @return - true if Adoptive parents are Resident of Iexas.
	 */
	public static boolean isAdptParentsTexasRes(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.CSTATE_TX.equals(applicationBackgroundDto.getCdAdptParentAddrState()));
	}

	/**
	 * Returns if the child is Permanent Resident or qualified alien.
	 * 
	 * @param applicationBackgroundDto
	 * @return - true if the child is Permanent Resident or qualified alien.
	 */
	public static boolean isPermResOrQualAlien(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndCtznshpPerm())
				|| ServiceConstants.Y.equals(applicationBackgroundDto.getIndCtznshpOthQualAlien()));
	}

	/**
	 * Returns if the child is Permanent Resident or qualified alien over 5 yrs
	 * 
	 * @param applicationBackgroundDto
	 * @return - true if the child is Permanent Resident or qualified alien.
	 */
	public static boolean isPermResOrQualAlienOver5Yrs(ApplicationBackgroundDto applicationBackgroundDto) {
		boolean result = false;
		if (isPermResOrQualAlien(applicationBackgroundDto)) {
			if (!DateUtils.isNull(applicationBackgroundDto.getDtChildEnteredUs())) {
				Date dtChildEnteredUsPlus5yrs = DateUtils.addToDate(applicationBackgroundDto.getDtChildEnteredUs(),
						ServiceConstants.FRIDAY.toString(), ServiceConstants.Zero.toString(),
						ServiceConstants.Zero.toString());
				if (new Date().compareTo(dtChildEnteredUsPlus5yrs) >= ServiceConstants.Zero) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Returns if the child is adopted by Citizen or PR or QA returns true
	 * 
	 * @param applicationBackgroundDto
	 * @return - true if the child is adopted by Citizen or PR or QA
	 */
	public static boolean isChildAdptByCitizenOrPROrQA(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndAdoptedByCtzn()));
	}

	/**
	 * Returns if the child is Permanent Resident or qualified alien.
	 * 
	 * @param applicationBackgroundDto
	 * @return - true if the child is Permanent Resident or qualified alien.
	 */
	public static boolean isChildsImmigStatusUnknown(ApplicationBackgroundDto applicationBackgroundDto) {
		return (ServiceConstants.Y.equals(applicationBackgroundDto.getIndCtznshpUnknown()));
	}

	/**
	 * This method will blank out the Determination Indicators so that can be
	 * calculated again.
	 * 
	 * @param eligibilityDeterminationDto
	 * 
	 * @return - determValueBean updated.
	 */
	public static EligibilityDeterminationDto blankOutDeterminationIndicators(
			EligibilityDeterminationDto eligibilityDeterminationDto) {
		eligibilityDeterminationDto.setIdDetermSibPerson(ServiceConstants.ZERO_VAL);
		eligibilityDeterminationDto.setIndWithSibling(null);
		eligibilityDeterminationDto.setInd60MonthsCvs(null);
		eligibilityDeterminationDto.setIndApplicableChildOutcm(null);
		blankOutFinalDeterminationIndicators(eligibilityDeterminationDto);
		return eligibilityDeterminationDto;
	}

	/**
	 * This method will blank out the Determination Indicators so that can be
	 * calculated again.
	 * 
	 * @param eligibilityDeterminationDto
	 * 
	 * @return - determValueBean updated.
	 */
	public static EligibilityDeterminationDto blankOutFinalDeterminationIndicators(
			EligibilityDeterminationDto eligibilityDeterminationDto) {
		eligibilityDeterminationDto.setIdFinalDetermPerson(ServiceConstants.ZERO_VAL);
		eligibilityDeterminationDto.setDtFinalDeterm(null);
		eligibilityDeterminationDto.setCdFinalDeterm(null);
		eligibilityDeterminationDto.setIndSpecialNeedOutcm(null);
		eligibilityDeterminationDto.setIndCtznshpOutcm(null);
		eligibilityDeterminationDto.setIndRsnbleEffortOutcm(null);
		eligibilityDeterminationDto.setIndAddtIVEReqOutcm(null);
		eligibilityDeterminationDto.setIndDfpsMngngCvsOutcm(null);
		eligibilityDeterminationDto.setIndPlcmntReqMetOutcm(null);
		eligibilityDeterminationDto.setIndAdptAgreementOutcm(null);
		eligibilityDeterminationDto.setIdAdoPlcmtEvent(ServiceConstants.ZERO_VAL);
		eligibilityDeterminationDto.setIndAsstDisqualified(null);
		return eligibilityDeterminationDto;
	}

	public static Boolean doesChildsMeetCitizenshipReq(AaeApplAndDetermDBDto aaeApplAndDetermDBDto) {
		AaeEligDetermMessgDto aaeEligDetermMessgDto = determineChildsIVEOrStateCitizenshipOutcome(aaeApplAndDetermDBDto,
				new AaeEligDetermMessgDto());
		return aaeEligDetermMessgDto.getDoesChildMeetsCitizenshipReq();
	}

	public static Boolean doesChildMeetsAdditionalIVEReq(AaeApplAndDetermDBDto aaeApplAndDetermDBDto,
			AaeEligDetermMessgDto aaeEligQualMessgDto) {
		EligibilityDeterminationDto eligDetermValueBean = aaeApplAndDetermDBDto.getEligibilityDeterminationDto();
		ApplicationBackgroundDto appBackgroundValueBean = aaeApplAndDetermDBDto.getApplicationBackgroundDto();

		if (ServiceConstants.ELIG_TYPE_IVE_OR_STATE_PAID == aaeEligQualMessgDto.getTypeOfElig()
				|| ServiceConstants.ELIG_TYPE_IVE_PAID == aaeEligQualMessgDto.getTypeOfElig()) {
			if (ServiceConstants.Y.equals(eligDetermValueBean.getIndApplicableChildOutcm())) {
				if (isInCVS(appBackgroundValueBean) || ServiceConstants.Y.equals(eligDetermValueBean.getIndMedDisSsi())
						|| ServiceConstants.Y.equals(eligDetermValueBean.getIndLivWminorParent())
						|| ServiceConstants.Y.equals(eligDetermValueBean.getInd4ePriorAdo())) {
					return Boolean.TRUE;
				}
			} else {
				if (ServiceConstants.Y.equals(eligDetermValueBean.getIndAfdcEligible())
						|| ServiceConstants.Y.equals(eligDetermValueBean.getIndSsiEligible())
						|| ServiceConstants.Y.equals(eligDetermValueBean.getInd4eMinorParent())
						|| ServiceConstants.Y.equals(eligDetermValueBean.getInd4ePriorAdo())) {
					return Boolean.TRUE;
				}
			}
		} else if (ServiceConstants.ELIG_TYPE_STATE_PAID == aaeEligQualMessgDto.getTypeOfElig()
				&& isInCVS(appBackgroundValueBean)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public static Boolean isInCVS(ApplicationBackgroundDto appBackgroundValueBean) {
		return (ServiceConstants.Y.equals(appBackgroundValueBean.getIndMngngCvs())
				|| ServiceConstants.Y.equals(appBackgroundValueBean.getIndOtherMngngCvs())
				|| ServiceConstants.Y.equals(appBackgroundValueBean.getIndLcpaMngngCvs()));
	}

}
