package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.workload.dao.AssignWorkloadDao;
import us.tx.state.dfps.service.workload.dto.AssignWorkloadDto;
import us.tx.state.dfps.service.workload.dto.GetSijsStatusFormDto;
import us.tx.state.dfps.service.workload.dto.MrefCssContactDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefSecondaryDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsEventContactDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsEventIdDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsLegalDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsSecondaryDtlsDto;

/**
 *
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN14S Tuxedo
 * DAM Name: CCMN37D Class Description: This Method extends BaseDao and
 * implements AssignWorkloadDao. This is used to retrieve workload details from
 * database. Mar 23, 2017 - 3:50:30 PM
 *  * **********  Change History *********************************
 * 10/15/2019 kanakas artf128837 : DEV - Assigned Workload Enhancements
 */

@Repository
public class AssignWorkloadDaoImpl implements AssignWorkloadDao {

	//changed the variable names for artf128837
	/**
	 *
	 */
	private static final String A_NM_WKLD_CASE = "wkldCaseName";

	/**
	 *
	 */
	private static final String A_CD_WKLD_STAGE_PROGRAM = "wkldStageProgram";

	/**
	 *
	 */
	private static final String A_DT_WKLD_STAGE_PERS_LINK = "wkldStagePersLink";

	/**
	 *
	 */
	private static final String S_DT_STAGE_START = "dtStageStart";

	/**
	 *
	 */
	private static final String A_CD_WKLD_STAGE = "wkldStage";

	/**
	 *
	 */
	private static final String A_ID_WKLD_CASE = "idWkldCase";

	/**
	 *
	 */
	private static final String A_NM_WKLD_STAGE = "wkldStageName";

	/**
	 *
	 */
	private static final String A_CD_WKLD_STAGE_PERS_ROLE = "wkldStagePersRole";

	//end of artf128837
	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN18 = "assigned_Workload_Column18";

	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN13 = "assigned_Workload_Column13";

	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN12 = "assigned_Workload_Column12";

	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN10 = "assigned_Workload_Column10";

	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN17 = "assigned_Workload_Column17";

	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN08 = "assigned_Workload_Column08";

	/**
	 *
	 */
	private static final String ASSIGNED_WORKLOAD_COLUMN06 = "assigned_Workload_Column06";

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	@Value("${AssignWorkloadDaoImpl.getAssignWorkloadDtls}")
	private String getAssignWorkloadDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getTempAssignWorkloadDtls}")
	private String getTempAssignWorkloadDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getMrefDtls}")
	private String getMrefDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getMrefSecondaryDtls}")
	private String getMrefSecondaryDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getMrefCssContactDtls}")
	private String getMrefCssContactDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getInRegionNotAllCitizenSijsDtls}")
	private String getInRegionNotAllCitizenSijsDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getInRegionAllCitizenSijsDtls}")
	private String getInRegionAllCitizenSijsDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getSijsSecondaryDtls}")
	private String getSijsSecondaryDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getSijsLegalDtls}")
	private String getSijsLegalDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getSijsEventIdDtls}")
	private String getSijsEventIdDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getSijsEventContactDtls}")
	private String getSijsEventContactDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getOutRegionNotAllCitizenSijsDtls}")
	private String getOutRegionNotAllCitizenSijsDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getOutRegionAllCitizenSijsDtls}")
	private String getOutRegionAllCitizenSijsDtlsSql;

	@Value("${AssignWorkloadDaoImpl.getSijsStatus}")
	private String getSijsStatusSql;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(AssignWorkloadDaoImpl.class);

	@Autowired
	private MobileUtil mobileUtil;

	public AssignWorkloadDaoImpl() {
	}

	/**
	 *
	 * Method Description: This Method is used to retrieve Assign Workload List
	 * by giving input as id_person. Tuxedo Servive Name:CCMN14S Tuxedo DAM
	 * Name: CCMN37D
	 *
	 * @param assignWorkloadInputStruct
	 * @return assignWorkloadDtlsList
	 */

	@SuppressWarnings("unchecked")
	public List<AssignWorkloadDto> getAssignWorkloadDetails(AssignWorkloadReq assignWorkloadReq) {

		List<AssignWorkloadDto> assignWorkloadDtlsList = new ArrayList<>();
		long totalRecordCount = 0l; // Defect 10767 - Initialize to 0 instead of null
		String orderByColumn = "";
		if (null == assignWorkloadReq.getOrderByColumn()) {
			assignWorkloadReq.setOrderByColumn("");
		}
		switch (assignWorkloadReq.getOrderByColumn()) {
			case ASSIGNED_WORKLOAD_COLUMN06:
				orderByColumn = A_CD_WKLD_STAGE_PERS_ROLE;
				break;
			case ASSIGNED_WORKLOAD_COLUMN08:
				orderByColumn = A_NM_WKLD_STAGE;
				break;
			case ASSIGNED_WORKLOAD_COLUMN17:
				orderByColumn = A_ID_WKLD_CASE;
				break;
			case ASSIGNED_WORKLOAD_COLUMN10:
				orderByColumn = A_CD_WKLD_STAGE;
				break;
			case ASSIGNED_WORKLOAD_COLUMN12:
				orderByColumn = S_DT_STAGE_START;
				break;
			case ASSIGNED_WORKLOAD_COLUMN13:
				orderByColumn = A_DT_WKLD_STAGE_PERS_LINK;
				break;
			case ASSIGNED_WORKLOAD_COLUMN18:
				orderByColumn = A_CD_WKLD_STAGE_PROGRAM;
				break;
			default:
				orderByColumn = A_NM_WKLD_CASE;
				break;
		}
		if (!ObjectUtils.isEmpty(assignWorkloadReq.getOrderByColumn())) {
			if (assignWorkloadReq.isColumnDesc()) {
				orderByColumn = orderByColumn.concat(" DESC NULLS LAST");
			}else {
				orderByColumn = orderByColumn.concat(" NULLS FIRST");
			}
		}
		if(mobileUtil.isMPSEnvironment() && ObjectUtils.isEmpty(assignWorkloadReq.getOrderByColumn())) {
			orderByColumn = " (CASE WHEN mobileStatus = 'AI' THEN 1 " +
					"WHEN mobileStatus = 'AO' THEN 2 " +
					"WHEN mobileStatus = 'OT' THEN 3 " +
					"WHEN mobileStatus = 'IN' THEN 4 " +
					"WHEN mobileStatus is null THEN 4 " +
					"WHEN mobileStatus = 'ER' THEN 5  END), wkldStageName ASC ";

		}

		StringBuilder queryString = new StringBuilder(getAssignWorkloadDtlsSql);
		queryString.append(ServiceConstants.SPACE);
		queryString.append(orderByColumn);

		Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryString.toString())
												.setParameter("id_Person", assignWorkloadReq.getUlIdPerson()))
		//.setParameter("sortableColumn", orderByColumn))
		.addScalar("stageCreated", StandardBasicTypes.TIMESTAMP)
		.addScalar("multiRef", StandardBasicTypes.TIMESTAMP)
		.addScalar("idWkldStage", StandardBasicTypes.LONG)
		.addScalar("wkldStageName", StandardBasicTypes.STRING)
		.addScalar("wkldStageCounty", StandardBasicTypes.STRING)
		.addScalar("wkldStage", StandardBasicTypes.STRING)
		.addScalar("wkldStageType", StandardBasicTypes.STRING)
		.addScalar("wkldStageProgram", StandardBasicTypes.STRING)
		.addScalar("wkldStageRegion", StandardBasicTypes.STRING)
		.addScalar("recidivism", StandardBasicTypes.STRING)
		.addScalar("idWkldCase", StandardBasicTypes.LONG)
		.addScalar("indCaseAlert", StandardBasicTypes.STRING)
		.addScalar("indRCI", StandardBasicTypes.STRING) //artf128837
		.addScalar("indCaseWorkerSafety", StandardBasicTypes.STRING)
		.addScalar("indWkldCaseSensitive", StandardBasicTypes.STRING)
		.addScalar("wkldStagePersLink", StandardBasicTypes.TIMESTAMP)
		.addScalar("indWkldSuperintNotif", StandardBasicTypes.STRING)
		.addScalar("wkldUnit", StandardBasicTypes.STRING)
		.addScalar("wkldStagePersRole", StandardBasicTypes.STRING)
		.addScalar("wkldStageRsnCls", StandardBasicTypes.STRING)
		.addScalar("dtLastUpdated", StandardBasicTypes.TIMESTAMP)
		.addScalar("mobileStatus", StandardBasicTypes.STRING)
		.addScalar("wkldStagePersNew", StandardBasicTypes.STRING)
		.addScalar("stageCurrPriority", StandardBasicTypes.STRING)
		.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
		.addScalar("indScreened", StandardBasicTypes.STRING)
		.addScalar("incomingCall", StandardBasicTypes.TIMESTAMP)
		.addScalar("formallyScreened", StandardBasicTypes.STRING)
		.addScalar("indCSA", StandardBasicTypes.STRING)//artf128837
		.addScalar("indSVH", StandardBasicTypes.STRING)//artf128837
		.addScalar("legalStatus", StandardBasicTypes.STRING)//artf128837
		.addScalar("legalStatusDescr", StandardBasicTypes.STRING)//artf128837
		.addScalar("wkldCaseName", StandardBasicTypes.STRING)
		.addScalar("idPriorStage", StandardBasicTypes.LONG)
		.addScalar("caseWorkerSafetyTxt", StandardBasicTypes.STRING)
		.addScalar("indScreenEligible", StandardBasicTypes.STRING)
		.addScalar("indScreenedFrmPrvStage", StandardBasicTypes.STRING)
		.addScalar("totalRecCount", StandardBasicTypes.LONG)
		.addScalar("emrStatus", StandardBasicTypes.STRING)
		.addScalar("rcclCallTime", StandardBasicTypes.TIMESTAMP)
		.addScalar("rcclStageCurrPriority", StandardBasicTypes.STRING)
		.addScalar("eventStatus", StandardBasicTypes.STRING)//artfxxxxx
		.setResultTransformer(Transformers.aliasToBean(AssignWorkloadDto.class));
		List<AssignWorkloadDto> asgnWrkldDtlsList = (List<AssignWorkloadDto>) query.list();

		Query tempQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTempAssignWorkloadDtlsSql)
													.setParameter("idPerson", assignWorkloadReq.getUlIdPerson()))
		.addScalar("idWkldStage", StandardBasicTypes.LONG)
		.addScalar("dtLastUpdated", StandardBasicTypes.TIMESTAMP)
		.addScalar("wkldStage", StandardBasicTypes.STRING)
		.addScalar("wkldStagePersRole", StandardBasicTypes.STRING)
		.setResultTransformer(Transformers.aliasToBean(AssignWorkloadDto.class));

		List<AssignWorkloadDto> tempAssignWorkloadDtls = (List<AssignWorkloadDto>) tempQuery.list();

		if (!ObjectUtils.isEmpty(tempAssignWorkloadDtls) && !tempAssignWorkloadDtls.isEmpty()) {
			for (AssignWorkloadDto temp : tempAssignWorkloadDtls) {
				temp.setWkldCaseName(ServiceConstants.CRASH_RECOVERY_STAGE);
			}
			assignWorkloadDtlsList.addAll(tempAssignWorkloadDtls);
		}
		assignWorkloadDtlsList.addAll(asgnWrkldDtlsList);

		if (!ObjectUtils.isEmpty(asgnWrkldDtlsList) ) {
			for (AssignWorkloadDto wload : asgnWrkldDtlsList) {

				//artf176648:PD 67878 : RCI intake icon for SUB.
				//IndRCI should be Y if there are rci task(s).if Legacy system is used to remove task, then note that it do not set IndRCI to N.
				//Later , if user access this application, then user see IndRCI as Y which had no RCI tasks.
				//Additional check to verify Y records and set flag back to N in UI ( Not in DB) if there are no RCI tasks
				//For performance, additional check only if flag is set to Y.
				if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(wload.getIndRCI())) {
					if (!stageDao.getRCIAlertExists(wload.getIdWkldStage())) {
						Stage stage =stageDao.getStageEntityById(wload.getIdWkldStage());
						stage.setIndVictimNotifStatus(ServiceConstants.STRING_IND_N);
						sessionFactory.getCurrentSession().persist(stage);
						sessionFactory.getCurrentSession().flush();
						wload.setIndRCI(ServiceConstants.STRING_IND_N);
					}
				}
			}
		}
		if (!ObjectUtils.isEmpty(asgnWrkldDtlsList)) {
			//totalRecordCount = asgnWrkldDtlsList.get(0).getTotalRecCount(); // Commented for artf146794 ALM ID : 14140 : PD 60327
			totalRecordCount = asgnWrkldDtlsList.size(); //Added for artf146794 ALM ID : 14140 : PD 60327
		}
		if (!ObjectUtils.isEmpty(tempAssignWorkloadDtls)) {
			totalRecordCount = totalRecordCount + tempAssignWorkloadDtls.size();
		}
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		if (ObjectUtils.isEmpty(assignWorkloadReq.getOrderByColumn()) && !mobileUtil.isMPSEnvironment()) {
			order(assignWorkloadDtlsList);
		}
		if (!ObjectUtils.isEmpty(assignWorkloadDtlsList)
			&& ObjectUtils.isEmpty(assignWorkloadDtlsList.get(0).getTotalRecCount())) {
			assignWorkloadDtlsList.get(0).setTotalRecCount(totalRecordCount);
		}

		int firstResult = 0;
		int endResults = 100;
		if (!ObjectUtils.isEmpty(assignWorkloadReq.getTotalRecCount()) && !assignWorkloadReq.isCallForSummaryWorkload()) {
			if (assignWorkloadReq.getPageNbr() == 0) {
				assignWorkloadReq.setPageNbr(1);
			}
			firstResult = ((assignWorkloadReq.getPageNbr() - 1) * assignWorkloadReq.getPageSizeNbr());
			endResults = firstResult + assignWorkloadReq.getPageSizeNbr();
		}
		int i = firstResult;
		List<AssignWorkloadDto> assignWorkloadDtlsTempList = new ArrayList<>();
		//Modified the code the check the list size >= 250 for warranty defect 12127
		if (!ObjectUtils.isEmpty(assignWorkloadDtlsList) && assignWorkloadDtlsList.size() >= 250 &&
			!assignWorkloadReq.isCallForSummaryWorkload()) {
			while (i < endResults && i < assignWorkloadDtlsList.size()) {
				assignWorkloadDtlsTempList.add(assignWorkloadDtlsList.get(i));
				i++;
			}
		} else {
			assignWorkloadDtlsTempList = assignWorkloadDtlsList;
		}
		//Added for artf146794 ALM ID : 14140 : PD 60327
		if (!ObjectUtils.isEmpty(asgnWrkldDtlsList)) {
			assignWorkloadDtlsTempList.get(0).setTotalRecCount(totalRecordCount);
		}
			return assignWorkloadDtlsTempList;
		}

		/**
		 * this method to sort cases push new cases to top and then aplhabetic order
		 *
		 * @param cases
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static void order(List<AssignWorkloadDto> cases) {

			Collections.sort(cases, new Comparator() {

				public int compare(Object o1, Object o2) {

					String newIndicator1 = ((AssignWorkloadDto) o1).getWkldStagePersNew();
					String newIndicator2 = ((AssignWorkloadDto) o2).getWkldStagePersNew();
					int retVal=2;
					if (StringUtils.isEmpty(newIndicator1) && StringUtils.isEmpty(newIndicator2)) {
						retVal = 0;
					}else if (StringUtils.isEmpty(newIndicator1) && !StringUtils.isEmpty(newIndicator2)) {
						retVal = -1;
					}else if (!StringUtils.isEmpty(newIndicator1) && StringUtils.isEmpty(newIndicator2)) {
						retVal = 1;
					}
					if(retVal==2){
						int indicator1 = Integer.valueOf(newIndicator1);
						int indicator2 = Integer.valueOf(newIndicator2);
						int result = 0;
						if (indicator1 > indicator2) {
							result = -1;
						} else if (indicator1 < indicator2) {
							result = 1;
						} else {
							result = 0;
						}

						if (result == 0) {

							final String name1 = ((AssignWorkloadDto) o1).getWkldStageName();
							final String name2 = ((AssignWorkloadDto) o2).getWkldStageName();

							if (StringUtils.isEmpty(name1) && StringUtils.isEmpty(name2)) {
								result = 0;
							} else if (StringUtils.isEmpty(name1) && !StringUtils.isEmpty(name2)) {
								result = -1;
							} else if (!StringUtils.isEmpty(name1) && StringUtils.isEmpty(name2)) {
								result = 1;
							} else {
								result = name1.compareTo(name2);
							}
						}
						retVal = result;
					}
					return retVal;
				}
			});
		}

		/**
		 *
		 * Method Description: This Method is used to retrieve M-Ref details by
		 * giving input as id_person. Tuxedo Servive Name:CINV69SS Tuxedo DAM Name:
		 * CLSC0AD
		 *
		 * @param ulIdPerson
		 * @param yesIndicator
		 * @return MrefDtlsDto
		 */

		@SuppressWarnings("unchecked")

		public List<MrefDtlsDto> getMrefDtls(Long ulIdPerson, String yesIndicator) {

			List<MrefDtlsDto> mrefDtlsList = null;

			Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMrefDtlsSql)
													.setParameter("id_Person", ulIdPerson).setParameter("yes_Indicator", yesIndicator))
			.addScalar("multiRef", StandardBasicTypes.STRING)
			.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
			.addScalar("idCase", StandardBasicTypes.LONG)
			.addScalar("dtMrefDue", StandardBasicTypes.TIMESTAMP)
			.setResultTransformer(Transformers.aliasToBean(MrefDtlsDto.class));
			mrefDtlsList = (List<MrefDtlsDto>) query.list();

			if (!ObjectUtils.isEmpty(mrefDtlsList)) {
				return mrefDtlsList;
			} else {
				throw new FormsException(messageSource.getMessage("assignworkload.nomref", null, Locale.US));

			}

		}

		/**
		 *
		 * Method Description: This Method is used to retrieve Secondary details of
		 * M-Ref stages by giving input as id_stage. Tuxedo Servive Name:CINV69SS
		 * Tuxedo DAM Name: CLSC0BD
		 *
		 * @param uIIdStage
		 * @param szCdRole
		 * @return MrefSecondaryDtlsDto
		 */

		@SuppressWarnings("unchecked")

		public List<MrefSecondaryDtlsDto> getMrefSecondaryDtls(Long uIIdStage, String cdRole) {

			List<MrefSecondaryDtlsDto> mrefSecondaryDtlsList = null;

			Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMrefSecondaryDtlsSql)
													.setParameter("id_Stage", uIIdStage).setParameter("cd_Role", cdRole))
			.addScalar("idStage", StandardBasicTypes.LONG)
			.addScalar("nmPersonFull", StandardBasicTypes.STRING)
			.addScalar("cdJobClassDecode", StandardBasicTypes.STRING)
			.addScalar("dtSecondaryAssgnd", StandardBasicTypes.TIMESTAMP)
			.addScalar("dtSecondaryUnassgnd", StandardBasicTypes.TIMESTAMP)
			.setResultTransformer(Transformers.aliasToBean(MrefSecondaryDtlsDto.class));

			mrefSecondaryDtlsList = (List<MrefSecondaryDtlsDto>) query.list();
			return mrefSecondaryDtlsList;

		}

		/**
		 *
		 * Method Description: This Method is used to retrieve CSS contact details
		 * of M-Ref stages by giving input as id_stage. Tuxedo Servive Name:CINV69SS
		 * Tuxedo DAM Name: CLSC0CD
		 *
		 * @param uIIdStage
		 * @param szCdCssReviewFull
		 * @param szCdCssReviewOther
		 * @param szCdCssReviewScreened
		 * @return MrefCssContactDtlsDto
		 */

		@SuppressWarnings("unchecked")
		public List<MrefCssContactDtlsDto> getMrefCssContactDtls(Long uIIdStage, String cdCssReviewFull,
		String cdCssReviewOther, String cdCssReviewScreened) {

			List<MrefCssContactDtlsDto> mrefCssContactDtlsList = null;

			Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMrefCssContactDtlsSql)
													.setParameter("cd_CssReviewFull", cdCssReviewFull).setParameter("cd_CssReviewOther", cdCssReviewOther)
													.setParameter("cd_CssReviewScreened", cdCssReviewScreened).setParameter("id_Stage", uIIdStage))
			.addScalar("idContactStage", StandardBasicTypes.LONG)
			.addScalar("cdContactPurposeDecode", StandardBasicTypes.STRING)
			.addScalar("cdContactType", StandardBasicTypes.STRING)
			.addScalar("dtContactOccured", StandardBasicTypes.TIMESTAMP)
			.addScalar("nmPersonFull", StandardBasicTypes.STRING)
			.setResultTransformer(Transformers.aliasToBean(MrefCssContactDtlsDto.class));

			mrefCssContactDtlsList = (List<MrefCssContactDtlsDto>) query.list();
			return mrefCssContactDtlsList;

		}

		@SuppressWarnings("unchecked")
		public List<SijsDtlsDto> getSijsDtls(Long ulIdPerson, String szCdRegion, String szCdPersonCitizenship) {

			List<SijsDtlsDto> SijsDtlsList = null;

			if (szCdRegion.equalsIgnoreCase("01") || szCdRegion.equalsIgnoreCase("02") || szCdRegion.equalsIgnoreCase("03")
				|| szCdRegion.equalsIgnoreCase("04") || szCdRegion.equalsIgnoreCase("05")
				|| szCdRegion.equalsIgnoreCase("06") || szCdRegion.equalsIgnoreCase("07")
				|| szCdRegion.equalsIgnoreCase("08") || szCdRegion.equalsIgnoreCase("09")
				|| szCdRegion.equalsIgnoreCase("10") || szCdRegion.equalsIgnoreCase("11")) {
				if (szCdPersonCitizenship.contains("AMR") || szCdPersonCitizenship.contains("PTR")
					|| szCdPersonCitizenship.contains("TMR") || szCdPersonCitizenship.contains("VIS")) {
					Query query = ((SQLQuery) sessionFactory.getCurrentSession()
															.createSQLQuery(getInRegionNotAllCitizenSijsDtlsSql).setParameter("idPerson", ulIdPerson)
															.setParameter("cdRegion", szCdRegion)
															.setParameter("cdPersonCitizenship", szCdPersonCitizenship))
					.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
					.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
					.addScalar("nmPersonLast", StandardBasicTypes.STRING)
					.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
					.addScalar("dt_PersonBirth", StandardBasicTypes.DATE)
					.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("cdAppPersonCitizenship", StandardBasicTypes.STRING)
					.addScalar("cdElgPersonCitizenship", StandardBasicTypes.STRING)
					.addScalar("idCase", StandardBasicTypes.LONG)
					.addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("nbrDaysToMajority", StandardBasicTypes.LONG)
					.addScalar("dtSystemDate", StandardBasicTypes.DATE)
					.addScalar("cdStageCnty", StandardBasicTypes.STRING)
					.addScalar("idUnit", StandardBasicTypes.LONG)
					.addScalar("cdStageRegion", StandardBasicTypes.STRING)
					.addScalar("indEvaluationConclusion", StandardBasicTypes.STRING)
					.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
					.addScalar("idPrimary", StandardBasicTypes.LONG)
					.addScalar("nmStage", StandardBasicTypes.STRING)
					.addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
					.addScalar("nbrUnit", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(SijsDtlsDto.class));

					SijsDtlsList = (List<SijsDtlsDto>) query.list();
				} else {
					Query query = ((SQLQuery) sessionFactory.getCurrentSession()
															.createSQLQuery(getInRegionAllCitizenSijsDtlsSql).setParameter("idPerson", ulIdPerson)
															.setParameter("cdRegion", szCdRegion)).addScalar("nmPersonFirst", StandardBasicTypes.STRING)
																								  .addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
																								  .addScalar("nmPersonLast", StandardBasicTypes.STRING)
																								  .addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
																								  .addScalar("dt_PersonBirth", StandardBasicTypes.DATE)
																								  .addScalar("nbrPersonAge", StandardBasicTypes.LONG)
																								  .addScalar("idPerson", StandardBasicTypes.LONG)
																								  .addScalar("cdAppPersonCitizenship", StandardBasicTypes.STRING)
																								  .addScalar("cdElgPersonCitizenship", StandardBasicTypes.STRING)
																								  .addScalar("idCase", StandardBasicTypes.LONG)
																								  .addScalar("idStage", StandardBasicTypes.LONG)
																								  .addScalar("nbrDaysToMajority", StandardBasicTypes.LONG)
																								  .addScalar("dtSystemDate", StandardBasicTypes.DATE)
																								  .addScalar("cdStageCnty", StandardBasicTypes.STRING)
																								  .addScalar("idUnit", StandardBasicTypes.LONG)
																								  .addScalar("cdStageRegion", StandardBasicTypes.STRING)
																								  .addScalar("indEvaluationConclusion", StandardBasicTypes.STRING)
																								  .addScalar("cdStagePersRole", StandardBasicTypes.STRING)
																								  .addScalar("idPrimary", StandardBasicTypes.LONG)
																								  .addScalar("nmStage", StandardBasicTypes.STRING)
																								  .addScalar("nmPersonFull", StandardBasicTypes.STRING)
																								  .addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
																								  .addScalar("nbrUnit", StandardBasicTypes.STRING)
																								  .setResultTransformer(Transformers.aliasToBean(SijsDtlsDto.class));
					SijsDtlsList = (List<SijsDtlsDto>) query.list();

				}
			} else {
				if (szCdPersonCitizenship.contains("AMR") || szCdPersonCitizenship.contains("PTR")
					|| szCdPersonCitizenship.contains("TMR") || szCdPersonCitizenship.contains("VIS")) {
					Query query = ((SQLQuery) sessionFactory.getCurrentSession()
															.createSQLQuery(getOutRegionNotAllCitizenSijsDtlsSql).setParameter("idPerson", ulIdPerson)
															.setParameter("cdPersonCitizenship", szCdPersonCitizenship))
					.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
					.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
					.addScalar("nmPersonLast", StandardBasicTypes.STRING)
					.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
					.addScalar("dt_PersonBirth", StandardBasicTypes.DATE)
					.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("cdAppPersonCitizenship", StandardBasicTypes.STRING)
					.addScalar("cdElgPersonCitizenship", StandardBasicTypes.STRING)
					.addScalar("idCase", StandardBasicTypes.LONG)
					.addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("nbrDaysToMajority", StandardBasicTypes.LONG)
					.addScalar("dtSystemDate", StandardBasicTypes.DATE)
					.addScalar("cdStageCnty", StandardBasicTypes.STRING)
					.addScalar("idUnit", StandardBasicTypes.LONG)
					.addScalar("cdStageRegion", StandardBasicTypes.STRING)
					.addScalar("indEvaluationConclusion", StandardBasicTypes.STRING)
					.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
					.addScalar("idPrimary", StandardBasicTypes.LONG)
					.addScalar("nmStage", StandardBasicTypes.STRING)
					.addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
					.addScalar("nbrUnit", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(SijsDtlsDto.class));
					SijsDtlsList = (List<SijsDtlsDto>) query.list();
				} else {
					Query query = ((SQLQuery) sessionFactory.getCurrentSession()
															.createSQLQuery(getOutRegionAllCitizenSijsDtlsSql).setParameter("idPerson", ulIdPerson))
					.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
					.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
					.addScalar("nmPersonLast", StandardBasicTypes.STRING)
					.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
					.addScalar("dt_PersonBirth", StandardBasicTypes.DATE)
					.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("cdAppPersonCitizenship", StandardBasicTypes.STRING)
					.addScalar("cdElgPersonCitizenship", StandardBasicTypes.STRING)
					.addScalar("idCase", StandardBasicTypes.LONG)
					.addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("nbrDaysToMajority", StandardBasicTypes.LONG)
					.addScalar("dtSystemDate", StandardBasicTypes.DATE)
					.addScalar("cdStageCnty", StandardBasicTypes.STRING)
					.addScalar("idUnit", StandardBasicTypes.LONG)
					.addScalar("cdStageRegion", StandardBasicTypes.STRING)
					.addScalar("indEvaluationConclusion", StandardBasicTypes.STRING)
					.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
					.addScalar("idPrimary", StandardBasicTypes.LONG)
					.addScalar("nmStage", StandardBasicTypes.STRING)
					.addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
					.addScalar("nbrUnit", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(SijsDtlsDto.class));
					SijsDtlsList = (List<SijsDtlsDto>) query.list();

				}
			}

			if (!ObjectUtils.isEmpty(SijsDtlsList)) {
				return SijsDtlsList;
			} else {
				throw new FormsException(messageSource.getMessage("assignworkload.nosijsstatus", null, Locale.US));

			}
		}

		/**
		 *
		 * Method Description: Method is implemented to get Assignees and their job
		 * class for the SIJS rows Tuxedo Servive Name:CINV88S Tuxedo DAM Name:
		 * CLSC1DD
		 *
		 * @param ulIdStage
		 * @param szCdRole
		 * @return SijsSecondaryDtlsDto
		 * @throws Exception
		 */

		@SuppressWarnings("unchecked")
		@Override
		public List<SijsSecondaryDtlsDto> getSijsSecondaryDtls(Long ulIdStage, String szCdRole) {

			List<SijsSecondaryDtlsDto> sijsSecondaryDtlsList = null;

			Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSijsSecondaryDtlsSql)
													.setParameter("cdRole", szCdRole).setParameter("idStage", ulIdStage))
			.addScalar("idStage", StandardBasicTypes.LONG)
			.addScalar("nmPersonFull", StandardBasicTypes.STRING)
			.addScalar("cdJobClassDecode", StandardBasicTypes.STRING)
			.addScalar("dtAssgnd", StandardBasicTypes.DATE).addScalar("dtUnAssgnd", StandardBasicTypes.DATE)
			.addScalar("nbrDaysAssigned", StandardBasicTypes.LONG)
			.setResultTransformer(Transformers.aliasToBean(SijsSecondaryDtlsDto.class));

			sijsSecondaryDtlsList = (List<SijsSecondaryDtlsDto>) query.list();
			return sijsSecondaryDtlsList;

		}

		/**
		 *
		 * Method Description: Method is implemented to get eventIDs for the contact
		 * type Tuxedo Servive Name:CINV88S Tuxedo DAM Name: CLSS1AD
		 *
		 * @param ulIdPerson
		 * @param contactStage
		 * @param contactPurpose
		 * @return SijsEventIdDtlsDto
		 * @throws Exception
		 */

		@SuppressWarnings("unchecked")
		@Override
		public List<SijsEventIdDtlsDto> getSijsEventIdDtls(Long ulIdStage, String contactStage, String contactPurpose) {

			List<SijsEventIdDtlsDto> sijsEventIdDtlsDtoList = null;

			Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSijsEventIdDtlsSql)
													.setParameter("idStage", ulIdStage).setParameter("cdContactType", contactStage)
													.setParameter("cdContactPurpose", contactPurpose)).addScalar("idEvent", StandardBasicTypes.LONG)
																									  .addScalar("idContactStage", StandardBasicTypes.LONG)
																									  .addScalar("dtContactOccured", StandardBasicTypes.DATE)
																									  .addScalar("idContactWorker", StandardBasicTypes.LONG)
																									  .setResultTransformer(Transformers.aliasToBean(SijsEventIdDtlsDto.class));

			sijsEventIdDtlsDtoList = (List<SijsEventIdDtlsDto>) query.list();
			return sijsEventIdDtlsDtoList;

		}

		/**
		 *
		 * Method Description: Method is implemented to get Names and Others
		 * Contacted for all the events in a particular stage Tuxedo Servive
		 * Name:CINV88S Tuxedo DAM Name: CLSCDCD
		 *
		 * @param ulIdPerson
		 * @param contactStage
		 * @param contactPurpose
		 * @return SijsEventContactDtlsDto
		 * @throws Exception
		 */

		@SuppressWarnings("unchecked")
		@Override
		public List<SijsEventContactDtlsDto> getSijsEventContactDtls(Long ulIdStage, String contactStage,
		String contactPurpose) {

			List<SijsEventContactDtlsDto> sijsEventContactDtlsList = null;

			Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSijsEventContactDtlsSql)
													.setParameter("idStage", ulIdStage).setParameter("cdContactType", contactStage)
													.setParameter("cdContactPurpose", contactPurpose)).addScalar("idEvent", StandardBasicTypes.LONG)
																									  .addScalar("decodeName", StandardBasicTypes.STRING)
																									  .addScalar("fullName", StandardBasicTypes.STRING)
																									  .setResultTransformer(Transformers.aliasToBean(SijsEventContactDtlsDto.class));

			sijsEventContactDtlsList = (List<SijsEventContactDtlsDto>) query.list();
			return sijsEventContactDtlsList;

		}

		/**
		 *
		 * Method Description: Method is implemented to get legal status information
		 * Tuxedo Servive Name:CINV88S Tuxedo DAM Name: CLSC1CD
		 *
		 * @param ulIdPerson
		 * @return SijsLegalDtlsDto
		 * @throws Exception
		 */
		@Override
		public SijsLegalDtlsDto getSijsLegalDtls(Long ulIdPerson) {

			SijsLegalDtlsDto sijsLegalDtlsDto = new SijsLegalDtlsDto();
			sijsLegalDtlsDto = (SijsLegalDtlsDto) ((SQLQuery) sessionFactory.getCurrentSession()
																			.createSQLQuery(getSijsLegalDtlsSql).setParameter("idPerson", ulIdPerson))
			.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
			.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
			.addScalar("idPerson", StandardBasicTypes.LONG)
			.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
			.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
			.addScalar("dtLegalStatStatus", StandardBasicTypes.DATE)
			.addScalar("txtLegalStatCause", StandardBasicTypes.STRING)
			.addScalar("txtLegalStatCourt", StandardBasicTypes.STRING)
			.setResultTransformer(Transformers.aliasToBean(SijsLegalDtlsDto.class)).uniqueResult();
			return sijsLegalDtlsDto;

		}

		/**
		 *
		 * Method Description: Method is to get Status whether to display SIJS
		 * Status Form Tuxedo Servive Name:CINV88S
		 *
		 * @param ulIdPerson
		 * @return GetSijsStatusFormDto
		 * @throws Exception
		 */
		@Override
		public GetSijsStatusFormDto getSijsStatus(Long ulIdPerson) {

			GetSijsStatusFormDto getSijsStatusFormDto = new GetSijsStatusFormDto();
			List<GetSijsStatusFormDto> getSijsStatusFormDtoList = (List<GetSijsStatusFormDto>) ((SQLQuery) sessionFactory.getCurrentSession()
																														 .createSQLQuery(getSijsStatusSql).setParameter("idPerson", ulIdPerson))
			.addScalar("idWkldStage", StandardBasicTypes.LONG)
			.setResultTransformer(Transformers.aliasToBean(GetSijsStatusFormDto.class)).list();
			if (!ObjectUtils.isEmpty(getSijsStatusFormDtoList)){
				getSijsStatusFormDto = getSijsStatusFormDtoList.get(0);
			}else{
				getSijsStatusFormDto.setIdWkldStage(ServiceConstants.ZERO);
			}
			return getSijsStatusFormDto;

		}
}
