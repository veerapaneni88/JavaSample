package us.tx.state.dfps.service.cpsintakereport.daoImpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ApprovalDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.common.dto.PriorityChangeInfoDto;
import us.tx.state.dfps.common.dto.WorkerInfoDto;
import us.tx.state.dfps.common.dto.AgencyHomeInfoDto;
import us.tx.state.dfps.common.dto.ClassIntakeDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dto.MessageAttribute;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CpsIntakeReportDaolmpl will implemented all operation defined in
 * CpsIntakeReportDao Interface related CpsIntakeReport module.. Feb 9, 2018-
 * 2:02:51 PM © 2017 Texas Department of Family and Protective Services
 * * **********  Change History *********************************
 * 12/15/2020 thompswa artf169810 : get txtRelatedCalls in getStageIncomingDetails.
 * 05/55/2022 kurmav artf220333 add getAgencyHomeInfoSql, getIncidentDateSql,getSelfReportSql.
 * 10/05/2023 thompswa artf251139 : INTAKE Report Form Child Death Indicator
 * 04/09/2024 thompswa artf261922 : Reporter Email to display on the Intake Report Form
 * 08/26/2024 thompswa artf268014 : BR 40.2 Intake Report Form Question
 */
@Repository
public class CpsIntakeReportDaolmpl implements CpsIntakeReportDao {

	private static final Logger logger = Logger.getLogger(CpsIntakeReportDaolmpl.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	LookupService lookupService;
	
	@Autowired 
	CaseUtils caseUtils;

	@Value("${CpsInvestigationReportDaolmpl.getStageIncomingDetails}")
	private String getStageIncomingDetailsSql;

	@Value("${CpsInvestigationReportDaolmpl.getVictims}")
	private String getVictims;

	@Value("${CpsInvestigationReportDaolmpl.getPhoneInfo}")
	private String getPhoneInfo;

	@Value("${CpsInvestigationReportDaolmpl.getWorkerInfo}")
	private String getWorkerInfoSql;

	@Value("${CpsInvestigationReportDaolmpl.getPriorityChangeInfo}")
	private String getPriorityChangeInfoSql;

	@Value("${CpsInvestigationReportDaolmpl.getApprovalList}")
	private String getApprovalListSql;

	@Value("${CpsInvestigationReportDaolmpl.victimClause}")
	private String victimClauseSql;

	@Value("${CpsInvestigationReportDaolmpl.perpetratorClause}")
	private String perpetratorClauseSql;

	@Value("${CpsInvestigationReportDaolmpl.otherPrnClause}")
	private String otherPrnClauseSql;

	@Value("${CpsInvestigationReportDaolmpl.reporterClause}")
	private String reporterClauseSql;

	@Value("${CpsInvestigationReportDaolmpl.collateralClause}")
	private String collateralClauseSql;

	@Value("${CpsInvestigationReportDaolmpl.allCollateralClause}")
	private String allCollateralClauseSql;

	@Value("${CpsIntakeReportDaolmpl.getDtCallDisposed}")
	private String getDtCallDisposedSql;

	@Value("${CpsInvestigationReportDaolmpl.getAgencyHomeInfoSql}")
	private String getAgencyHomeInfoSql;

	@Value("${CpsInvestigationReportDaolmpl.getResourceInfoSql}")
	private String getResourceInfoSql;

	@Value("${CpsInvestigationReportDaolmpl.getIntakeClassData}")
	private String getIntakeClassDataSql;

	@Value("${CpsInvestigationReportDaolmpl.getCaseIncomingDetails}")
	private String getCaseIncomingDetailsSql;

	@Value("${CpsIntakeReportDaolmpl.getIncomingPersonTxtEmail}")
	private String getIncomingPersonTxtEmailSql;

	@Value("${CpsIntakeReportDaolmpl.getNotSwiRelatedTxtEmail}")
	private String getNotSwiRelatedTxtEmailSql;

	@Value("${CpsInvestigationReportDaolmpl.getVictimsForMPS}")
	private String getVictimsForMPS;

	@Autowired
	private MobileUtil mobileUtil;

	/**
	 * 
	 * Method Name: getStageIncomingDetails Method Description: Retrieves
	 * incoming detail plus BJN number of worker. DAM: CINT65D.
	 * artf169810 add txtRelatedCalls, artf251139 add indChildDeath
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IncomingStageDetailsDto getStageIncomingDetails(Long idStage) {
		List<IncomingStageDetailsDto> incomingStageDetailsDtoList = new ArrayList<>();
		IncomingStageDetailsDto incomingStageDetailsDto = new IncomingStageDetailsDto();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getStageIncomingDetailsSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdIncmgSex", StandardBasicTypes.STRING)
				.addScalar("cdIncmgAllegType", StandardBasicTypes.STRING)
				.addScalar("cdIncmgCallerInt", StandardBasicTypes.STRING)
				.addScalar("cdIncomingProgramType", StandardBasicTypes.STRING)
				.addScalar("dtIncomingCallDisposed", StandardBasicTypes.DATE)
				.addScalar("nmIncmgJurisdiction", StandardBasicTypes.STRING)
				.addScalar("dtIncomingCall", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdIncomingCallType", StandardBasicTypes.STRING)
				.addScalar("nmIncomingCallerFirst", StandardBasicTypes.STRING)
				.addScalar("nmIncomingCallerMiddle", StandardBasicTypes.STRING)
				.addScalar("nmIncomingCallerLast", StandardBasicTypes.STRING)
				.addScalar("cdIncomingCallerSuffix", StandardBasicTypes.STRING)
				.addScalar("cdIncmgCallerPhonType", StandardBasicTypes.STRING)
				.addScalar("nbrIncomingCallerPhone", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgCallerPhonExt", StandardBasicTypes.STRING)
				.addScalar("cdIncmgCallerAddrType", StandardBasicTypes.STRING)
				.addScalar("addrIncmgStreetLn1", StandardBasicTypes.STRING)
				.addScalar("addrIncmgStreetLn2", StandardBasicTypes.STRING)
				.addScalar("addrIncomingCallerCity", StandardBasicTypes.STRING)
				.addScalar("cdIncomingCallerCounty", StandardBasicTypes.STRING)
				.addScalar("cdIncomingCallerState", StandardBasicTypes.STRING)
				.addScalar("addrIncmgZip", StandardBasicTypes.STRING)
				.addScalar("nmIncmgRegardingFirst", StandardBasicTypes.STRING)
				.addScalar("nmIncmgRegardingLast", StandardBasicTypes.STRING)
				.addScalar("cdIncomingDisposition", StandardBasicTypes.STRING)
				.addScalar("cdIncmgSpecHandling", StandardBasicTypes.STRING)
				.addScalar("indIncmgSensitive", StandardBasicTypes.STRING)
				.addScalar("indIncmgSuspMeth", StandardBasicTypes.STRING)
				.addScalar("indIncmgWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtIncmgWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtIncmgSensitive", StandardBasicTypes.STRING)
				.addScalar("txtIncmgSuspMeth", StandardBasicTypes.STRING)
				.addScalar("indIncmgNoFactor", StandardBasicTypes.STRING)
				.addScalar("cdIncmgStatus", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("addrIncmgWorkerCity", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgWorkerPhone", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgWorkerExt", StandardBasicTypes.STRING)
				.addScalar("nmIncmgWorkerName", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgUnit", StandardBasicTypes.STRING)
				.addScalar("cdIncmgRegion", StandardBasicTypes.STRING)
				.addScalar("nbrSecCallerPhonExt", StandardBasicTypes.STRING)
				.addScalar("cdSecCallerPhonType", StandardBasicTypes.STRING)
				.addScalar("txtReporterNotes", StandardBasicTypes.STRING)
				.addScalar("txtRelatedCalls", StandardBasicTypes.STRING)
				.addScalar("indFoundOpenCase", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeLast", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdEmpBjnEmp", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("indFoundOpenCaseAtIntake", StandardBasicTypes.STRING)
				.addScalar("indFormallyScreened", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("txtReporterEmail", StandardBasicTypes.STRING)
				.addScalar("indChildDeath", StandardBasicTypes.STRING)
				.setParameter("cdEventType", ServiceConstants.NOT_EVENT_TYPE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(IncomingStageDetailsDto.class));
		incomingStageDetailsDtoList = (List<IncomingStageDetailsDto>) query.list();
		return incomingStageDetailsDto = !ObjectUtils.isEmpty(incomingStageDetailsDtoList)
				? adjustRelatedDetails(incomingStageDetailsDtoList.get(0)) : defaultDetails(idStage);
	}

	/**
	 * 
	 * Method Name: getPersonList Method Description:List DAM retrieves all
	 * persons in a specific set for a specified stage ID, considering closure
	 * and end dating. The set to retrieve is specified on input.r DAM : CINT66D
	 * 
	 * @param idStage
	 * @param iQueryType
	 * @return
	 * @throws DataNotFoundException
	 **/
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> getPersonList(Long idStage, int iQueryType) {
		StringBuilder sql = new StringBuilder();
		if(!mobileUtil.isMPSEnvironment()) {
			sql.append(getVictims);
		} else {
			sql.append(getVictimsForMPS);
		}
		Query query = sessionFactory.getCurrentSession().createQuery(getDtCallDisposedSql);
		query.setLong(0, idStage);
		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMYYYY);
		Date dtCallDisposed = (Date) query.uniqueResult();

		try {
			dtCallDisposed = dtCallDisposed == null ? format.parse(ServiceConstants.MAX_JAVA_DATE) : dtCallDisposed;
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}

		switch (iQueryType) {
		case ServiceConstants.VICTIM_TYPE:
			sql.append(victimClauseSql);
			break;
		case ServiceConstants.PERPETRATOR_TYPE:
			sql.append(perpetratorClauseSql);
			break;
		case ServiceConstants.OTHER_PRN_TYPE:
			sql.append(otherPrnClauseSql);
			break;
		case ServiceConstants.REPORTER_TYPE:
			sql.append(reporterClauseSql);
			break;

		case ServiceConstants.COLLATERAL_TYPE:
			sql.append(collateralClauseSql);
			break;
		case ServiceConstants.ALL_COLLATERAL_TYPE:
			sql.append(allCollateralClauseSql);
			break;
		default:
			throw new DataLayerException("Invalid query type");

		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dtCallDisposed);

		Query personListQuery = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonIdType", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("personOccupation", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("dtCallDisposed", cal.getTime())
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
        // artf261922
		List<PersonDto> personDtoList = (List<PersonDto>) personListQuery.list();
		List<PersonDto> returnList = new ArrayList<>();
		for (PersonDto personDto : personDtoList) {
			if (!dtCallDisposed.toString().equals(ServiceConstants.MAX_JAVA_DATE)
					&& !TypeConvUtil.isNullOrEmpty(personDto.getDtPersonBirth())) { // artf268014 needs age when disposed
				personDto.setNbrPersonAge((short) DateUtils.getDateDiffInYears(personDto.getDtPersonBirth(), dtCallDisposed));
			}
			if (ServiceConstants.REPORTER_TYPE == iQueryType && !CodesConstant.CPRSNALL_STF.equals(personDto.getCdStagePersType())) {
				Query emailQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getNotSwiRelatedTxtEmailSql)
						.setParameter("dtCallDisposed", cal.getTime())
						.setParameter("dtCallDisposed", cal.getTime())
						.setParameter("dtCallDisposed", cal.getTime())
						.setParameter("idPerson", personDto.getIdPerson());
				String result = (String) emailQuery.uniqueResult();
				if (!ObjectUtils.isEmpty(result)) {
					personDto.setTxtEmail(result);
				}
			}
			returnList.add(personDto);
		}
		return returnList;
	}

	/**
	 * 
	 * Method Name: getPhoneInfo Method Description:List DAM retrieves all phone
	 * info for a specified stage ID, considering closure and end dating. DAM
	 * Name: CallCINT62D
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PhoneInfoDto> getPhoneInfo(Long idStage) {

		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMYYYY);
		Date szMaxDate = null;
		try {
			szMaxDate = format.parse(ServiceConstants.MAX_JAVA_DATE);
		} catch (ParseException e) {

			logger.error(e.getMessage());
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(szMaxDate);

		String cdPrnType = ServiceConstants.PRN_TYPE;
		String cdColType = ServiceConstants.COL_TYPE;

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPhoneInfo)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING)
				.addScalar("txtPersonPhoneComments", StandardBasicTypes.STRING)
				.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
				.addScalar("dtPersonPhoneEnd", StandardBasicTypes.DATE)
				.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING)
				.addScalar("indPersonPhoneInvalid", StandardBasicTypes.STRING)
				.addScalar("idPersonPhone", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage).setParameter("cdPrnType", cdPrnType)
				.setParameter("cdColType", cdColType).setParameter("szMaxDate", cal.getTime())
				.setParameter("indReporter", ServiceConstants.Character_IND_Y)
				.setResultTransformer(Transformers.aliasToBean(PhoneInfoDto.class));
		return query.list();
	}

	/**
	 * 
	 * Method Name: getWorkerInfo Method Description:List DAM retrieves, based
	 * on event type and stage ID, the worker information associated with the
	 * event. DAM Name: CINT67D
	 * 
	 * @param idStage
	 * @param cdEventType
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WorkerInfoDto> getWorkerInfo(Long idStage, String cdEventType) {

		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMYYYY);
		Date maxDate = null;
		try {
			maxDate = format.parse(ServiceConstants.MAX_JAVA_DATE);
		} catch (ParseException e) {

			logger.error(e.getMessage());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(maxDate);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getWorkerInfoSql)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING).addScalar("cdJobBjn", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeCity", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("dtEventModified", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setParameter("cdEventType", cdEventType).setParameter("cdReject", ServiceConstants.CD_REJECT)
				.setParameter("indYes", ServiceConstants.Character_IND_Y)
				.setParameter("cdBSType", ServiceConstants.BS_TYPE).setParameter("maxDate", cal.getTime())
				.setResultTransformer(Transformers.aliasToBean(WorkerInfoDto.class));

		List<WorkerInfoDto> workerInfoList = query.list();

		return workerInfoList;
	}

	/**
	 * DAM Name: CINT68D Method Name: getPriorityChangeInfo Method Description:
	 * List DAM retrieves history of priority change information for a specified
	 * stage ID.
	 * 
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PriorityChangeInfoDto> getPriorityChangeInfo(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorityChangeInfoSql)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("cdPrtType", ServiceConstants.PRT_TYPE)
				.setResultTransformer(Transformers.aliasToBean(PriorityChangeInfoDto.class));
		List<PriorityChangeInfoDto> priorityInfoList = query.list();

		return priorityInfoList;

	}

	/**
	 * DAM Name: CLSC52D Method Name: getApprovalList Method Description: Return
	 * all approvers for a specific intake.
	 * 
	 * @param idEvent
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalDto> getApprovalList(Long idEvent) {
		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMYYYY);
		Date maxDate = null;
		try {
			maxDate = format.parse(ServiceConstants.MAX_JAVA_DATE);
		} catch (ParseException e) {

			logger.error(e.getMessage());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(maxDate);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getApprovalListSql)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING).addScalar("cdJobBjn", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeCity", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("dtApproversDetermination", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setParameter("cdAprv", ServiceConstants.APPROVAL)
				.setParameter("indYes", ServiceConstants.Character_IND_Y)
				.setParameter("cdBSType", ServiceConstants.BS_TYPE).setParameter("maxDate", cal.getTime())
				.setResultTransformer(Transformers.aliasToBean(ApprovalDto.class));

		List<ApprovalDto> approvalList = query.list();

		return approvalList;

	}
	// artf169810 handling null: "Please see the paper file for this intake record."
	private IncomingStageDetailsDto defaultDetails(Long idStage) {
		IncomingStageDetailsDto dto = new IncomingStageDetailsDto();
		Map<Integer, MessageAttribute> messages = lookupService.getMessages();
		Stage stage = caseUtils.getStageDetails(idStage);
		
		dto.setTxtRelatedCalls(messages.get(ServiceConstants.MSG_PAPER_INTAKE).getTxtMessage());
		dto.setIdStage(idStage);
		dto.setNmStage(stage.getCapsCase().getNmCase());
		dto.setIdCase(stage.getCapsCase().getIdCase());
		return dto;
	}

	// artf169810 format txtRelatedCalls, remove trailing comma then add a space after remaining commas.
	private IncomingStageDetailsDto adjustRelatedDetails(IncomingStageDetailsDto dto) {
		String txt = dto.getTxtRelatedCalls();
		if ( !StringUtils.isEmpty(txt) && 1 < txt.length()) {
			if ( ServiceConstants.COMMA.equals(txt.substring(txt.length() - 1))) {
				txt = txt.substring(0,txt.length() - 1);
			}
			txt = txt.replace(ServiceConstants.COMMA, ServiceConstants.CAPSCOMMA_SPACE);
			dto.setTxtRelatedCalls(txt);
		}
		return dto;
	}

	/**
	 * getAgencyHomeInfoDto Method Description: Return
	 * 	agency home information.
	 *
	 * @param facilityId
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public AgencyHomeInfoDto getAgencyHomeInfoDto(Long facilityId) {
		return (AgencyHomeInfoDto) sessionFactory.getCurrentSession().createSQLQuery(getAgencyHomeInfoSql)
				.addScalar("facilityName", StandardBasicTypes.STRING)
				.addScalar("facilityType", StandardBasicTypes.STRING)
				.addScalar("facilityNumber", StandardBasicTypes.LONG)
				.addScalar("agencyNumber", StandardBasicTypes.LONG)
				.addScalar("branchNumber", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("resourceFacilityType", StandardBasicTypes.STRING)
				.addScalar("agencyAddrStreetLn1", StandardBasicTypes.STRING)
				.addScalar("agencyAddrStreetLn2", StandardBasicTypes.STRING)
				.addScalar("agencyAddrCity", StandardBasicTypes.STRING)
				.addScalar("agencyAddrState", StandardBasicTypes.STRING)
				.addScalar("agencyAddrCounty", StandardBasicTypes.STRING)
				.addScalar("agencyAddrZip", StandardBasicTypes.STRING)
				.addScalar("agencyHomePhone", StandardBasicTypes.STRING)
				.setParameter("facilityNumber", facilityId)
				.setResultTransformer(Transformers.aliasToBean(AgencyHomeInfoDto.class)).uniqueResult();
	}

	/**
	 * getResourceInfoDto Method Description: Return
	 * 	agency home information.
	 *
	 * @param resourceId
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public AgencyHomeInfoDto getResourceInfoDto(Long resourceId) {
		return (AgencyHomeInfoDto) sessionFactory.getCurrentSession().createSQLQuery(getResourceInfoSql)
				.addScalar("facilityName", StandardBasicTypes.STRING)
				.addScalar("facilityType", StandardBasicTypes.STRING)
				.addScalar("facilityNumber", StandardBasicTypes.LONG)
				.addScalar("agencyNumber", StandardBasicTypes.LONG)
				.addScalar("branchNumber", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("agencyAddrStreetLn1", StandardBasicTypes.STRING)
				.addScalar("agencyAddrStreetLn2", StandardBasicTypes.STRING)
				.addScalar("agencyAddrCity", StandardBasicTypes.STRING)
				.addScalar("agencyAddrState", StandardBasicTypes.STRING)
				.addScalar("agencyAddrCounty", StandardBasicTypes.STRING)
				.addScalar("agencyAddrZip", StandardBasicTypes.STRING)
				.addScalar("agencyHomePhone", StandardBasicTypes.STRING)
				.setParameter("resourceId", resourceId)
				.setResultTransformer(Transformers.aliasToBean(AgencyHomeInfoDto.class)).uniqueResult();
	}

	/**
	 * Method Name: getIntakeClassData Method Description: Return
	 * incident date, self report, facilityId from Intake table in class application.
	 *
	 * @param idCase
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public ClassIntakeDto getIntakeClassData(Long idCase) {
		return (ClassIntakeDto) sessionFactory.getCurrentSession().createSQLQuery(getIntakeClassDataSql)
				.addScalar("incidentDate", StandardBasicTypes.DATE)
				.addScalar("selfReport", StandardBasicTypes.STRING)
				.addScalar("facilityId",StandardBasicTypes.LONG)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(ClassIntakeDto.class)).uniqueResult();
	}

	@Override
	public List<IncomingStageDetailsDto> getCaseIncomingDetails(Long idCase,Long idStage) {
		List<IncomingStageDetailsDto> incomingStageDetailsDtoList = new ArrayList<>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCaseIncomingDetailsSql)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdIncmgSex", StandardBasicTypes.STRING)
				.addScalar("cdIncmgAllegType", StandardBasicTypes.STRING)
				.addScalar("cdIncmgCallerInt", StandardBasicTypes.STRING)
				.addScalar("cdIncomingProgramType", StandardBasicTypes.STRING)
				.addScalar("dtIncomingCallDisposed", StandardBasicTypes.DATE)
				.addScalar("nmIncmgJurisdiction", StandardBasicTypes.STRING)
				.addScalar("dtIncomingCall", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdIncomingCallType", StandardBasicTypes.STRING)
				.addScalar("nmIncomingCallerFirst", StandardBasicTypes.STRING)
				.addScalar("nmIncomingCallerMiddle", StandardBasicTypes.STRING)
				.addScalar("nmIncomingCallerLast", StandardBasicTypes.STRING)
				.addScalar("cdIncomingCallerSuffix", StandardBasicTypes.STRING)
				.addScalar("cdIncmgCallerPhonType", StandardBasicTypes.STRING)
				.addScalar("nbrIncomingCallerPhone", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgCallerPhonExt", StandardBasicTypes.STRING)
				.addScalar("cdIncmgCallerAddrType", StandardBasicTypes.STRING)
				.addScalar("addrIncmgStreetLn1", StandardBasicTypes.STRING)
				.addScalar("addrIncmgStreetLn2", StandardBasicTypes.STRING)
				.addScalar("addrIncomingCallerCity", StandardBasicTypes.STRING)
				.addScalar("cdIncomingCallerCounty", StandardBasicTypes.STRING)
				.addScalar("cdIncomingCallerState", StandardBasicTypes.STRING)
				.addScalar("addrIncmgZip", StandardBasicTypes.STRING)
				.addScalar("nmIncmgRegardingFirst", StandardBasicTypes.STRING)
				.addScalar("nmIncmgRegardingLast", StandardBasicTypes.STRING)
				.addScalar("cdIncomingDisposition", StandardBasicTypes.STRING)
				.addScalar("cdIncmgSpecHandling", StandardBasicTypes.STRING)
				.addScalar("indIncmgSensitive", StandardBasicTypes.STRING)
				.addScalar("indIncmgSuspMeth", StandardBasicTypes.STRING)
				.addScalar("indIncmgWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtIncmgWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtIncmgSensitive", StandardBasicTypes.STRING)
				.addScalar("txtIncmgSuspMeth", StandardBasicTypes.STRING)
				.addScalar("indIncmgNoFactor", StandardBasicTypes.STRING)
				.addScalar("cdIncmgStatus", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("addrIncmgWorkerCity", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgWorkerPhone", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgWorkerExt", StandardBasicTypes.STRING)
				.addScalar("nmIncmgWorkerName", StandardBasicTypes.STRING)
				.addScalar("nbrIncmgUnit", StandardBasicTypes.STRING)
				.addScalar("cdIncmgRegion", StandardBasicTypes.STRING)
				.addScalar("nbrSecCallerPhonExt", StandardBasicTypes.STRING)
				.addScalar("cdSecCallerPhonType", StandardBasicTypes.STRING)
				.addScalar("txtReporterNotes", StandardBasicTypes.STRING)
				.addScalar("txtRelatedCalls", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeLast", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdEmpBjnEmp", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("indFoundOpenCaseAtIntake", StandardBasicTypes.STRING)
				.addScalar("indFormallyScreened", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.setParameter("cdEventType", ServiceConstants.NOT_EVENT_TYPE)
				.setParameter("idCase", idCase)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(IncomingStageDetailsDto.class));
		incomingStageDetailsDtoList = (List<IncomingStageDetailsDto>) query.list();
		return incomingStageDetailsDtoList;
	}

	/**
	 * Method Name: getIncomingPersonTxtEmail Method Description: If a reporter is NOT a 'DFPS Staff' ,
	 * AND , if a reporter is related ( Relate feature in SWI ) , email data is captured in the
	 * TXT_EMAIL column of the INCOMING_PERSON table. artf261922
	 *
	 * @param idPerson
	 * @param idStage
	 * @return String
	 */
	@Override
	public String getIncomingPersonTxtEmail(Long idPerson, Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getIncomingPersonTxtEmailSql)
				.setParameter("idPerson", idPerson)
				.setParameter("idStage", idStage);
		String result = (String) query.uniqueResult();

		return ObjectUtils.isEmpty(result) ? ServiceConstants.EMPTY_STRING : result;
	}
}
