package us.tx.state.dfps.service.casepackage.daoimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;


import us.tx.state.dfps.common.domain.ChildSafetyPlacement;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.common.domain.PcspPlcmnt;
import us.tx.state.dfps.common.domain.PcspStageVerfctn;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.PcspExtnDtl;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.PcspExtnDtlDto;
import us.tx.state.dfps.service.casepackage.dto.PcspPlcmntDto;
import us.tx.state.dfps.service.casepackage.dto.PcspStageVerfctnDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.PriorStageInRevRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataMismatchException;
import us.tx.state.dfps.service.pcsp.dto.PcspValueDto;

@Repository
public class PcspListPlacmtDaoImpl implements PcspListPlacmtDao {

	public static final String EMPTY_STRING = "";

	@Value("${pcsp.getPlacements}")
	private transient String pcspPlacementsSql;

	@Value("${pcsp.getAssessments}")
	private transient String pcspAssessmentsSql;

	@Value("${pcsp.getPcspExtnDtl}")
	private transient String pcspExtnDtlSql;

	@Value("${PcspDaoImpl.getSubStageStartDate}")
	private transient String subStageStartDate;

	@Value("${PcspDaoImpl.getPrimaryAsmntEvent}")
	private transient String getPrimaryAsmntEventSql;

	@Value("${PcspDaoImpl.getMethIndSql}")
	private transient String getMethIndSql;

	@Value("${PcspDaoImpl.getPriorStageInReverseSql}")
	private transient String getPriorStageInReverseSql;

	@Value("${PcspDaoImpl.hasOpenPCSPAsmt}")
	private transient String hasOpenPCSPAsmt;

	@Value("${PcspListPlacmtDaoImpl.hasOpenPCSPlacementSql}")
	private transient String hasOpenPCSPlacementSql;

	@Value("${PcspDaoImpl.hasOpenPCSPlacementNotVerifySql}")
	private transient String hasOpenPCSPlacementNotVerifySql;

	@Value("${PcspDaoImpl.hasContactPurposeSql}")
	private transient String hasContactPurposeSql;

	@Value("${PcspDaoImpl.hasCntctPurposeInitiationSql}")
	private transient String hasCntctPurposeInitiationSql;

	@Value("${PcspDaoImpl.hasSubmitForApproval}")
	private transient String hasSubmitForApproval;

	@Value("${PcspDaoImpl.hasOtherStagesOpen}")
	private String hasOtherStagesOpen;

	@Value("${PcspDaoImpl.hasOpenPCSPlcmntNotVrfd}")
	private transient String hasOpenPCSPlcmntNotVrfd;

	@Value("${PcspDaoImpl.displayPCSPList}")
	private String displayPCSPListSql;

	@Value("${PcspDaoImpl.hasFPRStagesOpen}")
	private String hasFPRStagesOpen;

	@Value("${PcspDaoImpl.getPriorStage}")
	private String getPriorStage;

	@Value("${PcspDaoImpl.getPriorStageId}")
	private String getPriorStageId;

	private static final String AND_CD_INV_STAGE = " AND STG.CD_STAGE IN ( 'INV', 'A-R')";
	private static final String AND_CD_AR_STAGE = " AND STG.CD_STAGE = 'A-R'";
	private static final String ORDER_BY = "ORDER BY CS.DT_END DESC, P1.NM_PERSON_FULL ASC ";

	@Autowired
	private SessionFactory sessionFactory;

	public PcspListPlacmtDaoImpl() {
	}

	/**
	 *
	 * Method Description: This Method will retrieve list of all PCSP placements
	 * in a case
	 *
	 * @param caseId
	 * @return List<PcspDto>
	 *
	 */
	@SuppressWarnings("unchecked")
	public List<PcspDto> getPcspPlacemnts(Long caseId) {
		List<PcspDto> beanList = new ArrayList<>();
		beanList = (List<PcspDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(pcspPlacementsSql.toString()).setParameter("caseId", caseId))
				.addScalar("idPlacement", StandardBasicTypes.LONG)
				.addScalar("childName", StandardBasicTypes.STRING)
				.addScalar("caregiverName", StandardBasicTypes.STRING)
				.addScalar("dtStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("endReason", StandardBasicTypes.STRING)
				.addScalar("pcspGoal", StandardBasicTypes.STRING)
				.addScalar("stageCreated", StandardBasicTypes.STRING)
				.addScalar("stageEnded", StandardBasicTypes.STRING)
				.addScalar("idAssessment", StandardBasicTypes.LONG).addScalar("type", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PcspDto.class)).list();
		return beanList;
	}

	/**
	 *
	 * Method Description: This method is used to retrieve assessment details of
	 * a caseid from PCSP_ASMNT table.
	 *
	 * @param caseId
	 * @param PcspResponse
	 */
	@SuppressWarnings("unchecked")
	public List<PcspDto> getPcspAssessmnt(Long caseId) {
		List<PcspDto> valueBeanList = null;
		valueBeanList = (List<PcspDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(pcspAssessmentsSql.toString()).setParameter("idCase", caseId))
				.addScalar("dtEntered", StandardBasicTypes.TIMESTAMP)
				.addScalar("status", StandardBasicTypes.STRING).addScalar("type", StandardBasicTypes.STRING)
				.addScalar("description", StandardBasicTypes.STRING)
				.addScalar("stageEntered", StandardBasicTypes.STRING)
				.addScalar("caregiverName", StandardBasicTypes.STRING)
				.addScalar("decision", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("enteredBy", StandardBasicTypes.STRING)
				.addScalar("idAssessment", StandardBasicTypes.LONG)
				.addScalar("idPrimaryAssmt", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PcspDto.class)).list();
		return valueBeanList;
	}

	/**
	 *
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 * @param caseId
	 *
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PcspPlcmntDto getPcspPlacemetInfo(Long placemnetId) {
		PcspPlcmntDto pscpPlcmntList = null;
		pscpPlcmntList = (PcspPlcmntDto) sessionFactory.getCurrentSession()
				.createCriteria(PcspPlcmnt.class, "pcspPlcmnt").createAlias("pcspPlcmnt.pcspAsmnt", "pcspAsmnt")
				.add(Restrictions.eq("pcspPlcmnt.idPcspPlcmnt", placemnetId))
				.setProjection(Projections.projectionList()
						.add(Projections.property("pcspPlcmnt.person.idPerson").as("idPerson"))
						.add(Projections.property("pcspAsmnt.person.idPerson").as("idPrsnCrgvr"))
						.add(Projections.property("pcspAsmnt.dtDecsn").as("decisionDate"))
						.add(Projections.property("pcspAsmnt.dtPlcmnt").as("placementDate"))
						.add(Projections.property("pcspAsmnt.dtCreated").as("dtAsmntCreated"))
						.add(Projections.property("pcspPlcmnt.dtStart").as("dtStart"))
						.add(Projections.property("pcspPlcmnt.dtEnd").as("dtEnd"))
						.add(Projections.property("pcspPlcmnt.cdEndRsn").as("cdEndRsn"))
						.add(Projections.property("pcspPlcmnt.cdGoal").as("cdGoal"))
						.add(Projections.property("pcspPlcmnt.dtLastUpdate").as("dtLastUpdate"))
						.add(Projections.property("pcspPlcmnt.idStageFinal").as("idStageFinal"))
						.add(Projections.property("pcspPlcmnt.idEvent").as("idEvent"))
						.add(Projections.property("pcspPlcmnt.idPcspPlcmnt").as("idPcspPlcmnt"))
						.add(Projections.property("pcspPlcmnt.txtEndRsnOther").as("endRsnOther")))
				.setResultTransformer(Transformers.aliasToBean(PcspPlcmntDto.class)).uniqueResult();
		return pscpPlcmntList;
	}

	/**
	 *
	 * @param placemnetId
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PcspExtnDtlDto> getPcspPlacementExtDtl(Long placemnetId) {
		List<PcspExtnDtlDto> pcspExtnDtlDtos = null;
		pcspExtnDtlDtos = (List<PcspExtnDtlDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(pcspExtnDtlSql.toString()).setParameter("idPcspPlcmnt", placemnetId))
				.addScalar("idPcspExtnDtlDto", StandardBasicTypes.LONG)
				.addScalar("idPcspPlcmnt", StandardBasicTypes.LONG)
				.addScalar("renewalDate", StandardBasicTypes.DATE)
				.addScalar("extensionExpiryDate", StandardBasicTypes.DATE)
				.addScalar("cdGoal", StandardBasicTypes.STRING)
				.addScalar("extensionNumber", StandardBasicTypes.INTEGER)
				.addScalar("indToContPcsp", StandardBasicTypes.STRING)
				.addScalar("indAtrnyParentAgrmnt", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("legalActionEventId", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PcspExtnDtlDto.class)).list();
		return pcspExtnDtlDtos;
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve child, caregiver from
	 * NAME table.
	 * 
	 * @param Id
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String getNameDetail(Long id) {
		List<NameDto> childList = (List<NameDto>) sessionFactory.getCurrentSession().createCriteria(Name.class, "name")
				.createAlias("name.person", "person").add(Restrictions.eq("person.idPerson", id))
				.setProjection(Projections.projectionList().add(Projections.property("name.nmNameLast").as("lastName"))
						.add(Projections.property("name.nmNameMiddle").as("middleName"))
						.add(Projections.property("name.nmNameFirst").as("firstName")))
				.setResultTransformer(Transformers.aliasToBean(NameDto.class)).list();
		if (!CollectionUtils.isEmpty(childList)){ //Defect 11028 - Add null check on collections
			NameDto nameDB = childList.get(0);
			StringBuilder fullName = new StringBuilder();
			if (null != nameDB) {
				if (isValid(nameDB.getLastName())) {
					fullName.append(getNonNullString(nameDB.getLastName()));
				}
				if (isValid(nameDB.getFirstName())) {
					if (isValid(fullName.toString()))
						fullName.append(',');
					fullName.append(getNonNullString(nameDB.getFirstName()));
				}
				if (isValid(nameDB.getMiddleName())) {
					if (isValid(fullName.toString()))
						fullName.append(' ');
					fullName.append(nameDB.getMiddleName().substring(0, 0));
				}
				if (isValid(nameDB.getNameSuffix())) {
					if (isValid(fullName.toString()))
						fullName.append(' ');
					fullName.append(nameDB.getNameSuffix());
				}
			}
			return fullName.toString();
		}else{
			return ServiceConstants.BLANK;
		}
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_STAGE_VERFCTN table.
	 * 
	 * @param caseId
	 * @throws ServiceExceptio
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PcspStageVerfctnDto> getPcspStageVerfctn(Long placementId) {
		List<PcspStageVerfctnDto> PcspStageVerList = (List<PcspStageVerfctnDto>) sessionFactory.getCurrentSession()
				.createCriteria(PcspStageVerfctn.class, "stageVer").createAlias("stageVer.pcspPlcmnt", "placement")
				.createAlias("stageVer.stage", "stage")
				.setProjection(Projections.projectionList()
						.add(Projections.property("stageVer.dtCreated").as("dtCreated"))
						.add(Projections.property("stageVer.personByIdCreatedPerson.idPerson").as("IdCreatedPerson"))
						.add(Projections.property("stage.idStage").as("idStageVerfd"))
						.add(Projections.property("stage.cdStage").as("cdStage"))
						.add(Projections.property("placement.idPcspPlcmnt").as("idPcspPlcmnt")))
				.addOrder(Order.desc("stageVer.dtCreated")).add(Restrictions.eq("placement.idPcspPlcmnt", placementId))
				.setResultTransformer(Transformers.aliasToBean(PcspStageVerfctnDto.class)).list();
		return PcspStageVerList;
	}

	/**
	 *
	 * Method Description: This method is used to update placements details in
	 * EVENT table.
	 *
	 * @param pcspPlcmntDto
	 *
	 */
	@Override
	public void updatePcspPlcmntDet(PcspPlcmntDto pcspPlcmntDto) {
		PcspPlcmnt pcspPlcmntEntity = (PcspPlcmnt) sessionFactory.getCurrentSession().load(PcspPlcmnt.class,
				pcspPlcmntDto.getIdPcspPlcmnt());
		Timestamp existingLastUpdatedTime = new Timestamp(pcspPlcmntDto.getDtLastUpdate().getTime());
		SimpleDateFormat _12HourSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String _12HourDate = _12HourSDF.format(pcspPlcmntEntity.getDtLastUpdate().getTime());
		Timestamp newLastUpdatedTime = Timestamp.valueOf(_12HourDate);
		if (existingLastUpdatedTime.compareTo(newLastUpdatedTime) != 0) {
			throw new DataMismatchException(
					"PCSP  updated failed - " + "Dt Last updated is not equal to the value in table");
		}
		Date startDate = new Date();
		Date endDate = null;

		if (null != pcspPlcmntDto.getDtStart()) {
			startDate = pcspPlcmntDto.getDtStart();
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			// cal.add(Calendar.DATE, 1);
			startDate = cal.getTime();
		}
		if (TypeConvUtil.isNullOrEmpty(pcspPlcmntDto.getDtEnd())
				|| ServiceConstants.GENERIC_END_DATE.compareTo(pcspPlcmntDto.getDtEnd()) == 0) {
			endDate = ServiceConstants.GENERIC_END_DATE;
		} else {
			endDate = pcspPlcmntDto.getDtEnd();
			Calendar cal = Calendar.getInstance();
			cal.setTime(endDate);
			// cal.add(Calendar.DATE, 1);
			endDate = cal.getTime();
		}
		pcspPlcmntEntity.setDtStart(startDate);
		pcspPlcmntEntity.setDtEnd(endDate);
		pcspPlcmntEntity.setIdStageFinal(pcspPlcmntDto.getIdStageFinal());
		pcspPlcmntEntity.setCdEndRsn(pcspPlcmntDto.getCdEndRsn());
		pcspPlcmntEntity.setCdGoal(pcspPlcmntDto.getCdGoal());
		pcspPlcmntEntity.setTxtEndRsnOther(pcspPlcmntDto.getEndRsnOther());
		pcspPlcmntEntity.setIdLastUpdatePerson(pcspPlcmntDto.getIdLastUpdatePerson());
		sessionFactory.getCurrentSession().saveOrUpdate(pcspPlcmntEntity);
	}

	/**
	 *
	 * Method Description: This method is used to update placements details in
	 * EVENT table.
	 *
	 * @param idEvent
	 */
	@Override
	public void updatePcspPlcmntEvent(Long idEvent) {
		Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, idEvent);
		// EVENTSTATUS_COMPLETE
		event.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
		sessionFactory.getCurrentSession().saveOrUpdate(event);
	}

	/**
	 *
	 * Method Description: This method is used to insert data into
	 * PCSP_STAGE_VERIFICATION table.
	 *
	 * @param verfDto
	 */
	@Override
	public Long insertPcspStageVerfctn(PcspStageVerfctnDto verfDto) {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		PcspStageVerfctn stageVerctn = new PcspStageVerfctn();
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, verfDto.getIdLastUpdatePerson());
		person.getAddrPersonCity();
		stageVerctn.setPersonByIdLastUpdatePerson(person);
		stageVerctn.setPersonByIdCreatedPerson(person);
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,
				Long.valueOf(verfDto.getIdStageVerfd()));
		stageVerctn.setStage(stage);
		PcspPlcmnt pcspplcmnt = (PcspPlcmnt) sessionFactory.getCurrentSession().get(PcspPlcmnt.class,
				Long.valueOf(verfDto.getIdPcspPlcmnt()));
		stageVerctn.setPcspPlcmnt(pcspplcmnt);
		stageVerctn.setPersonByIdPrsnVerfd(person);
		stageVerctn.setDtVerfd(currentDate);
		Long idPcspStageVerfctn = (Long) sessionFactory.getCurrentSession().save(stageVerctn);
		return idPcspStageVerfctn;
	}

	@Override
	public Long insertPcspExtnDtl(PcspExtnDtl extnDtl, Long pcspPlcmntId){
		PcspPlcmnt pcspPlcmnt = (PcspPlcmnt) sessionFactory.getCurrentSession().get(PcspPlcmnt.class, pcspPlcmntId);
		extnDtl.setPcspPlcmnt(pcspPlcmnt);
		return (Long) sessionFactory.getCurrentSession().save(extnDtl);
	}

	/**
	 *
	 * Method Description: This method retrieves the stage start date
	 *
	 * @param personId
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getSubStageStartDate(Long personId) {
		List<PcspPlcmntDto> subStageList = (List<PcspPlcmntDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(subStageStartDate).setParameter("idPerson", personId))
				.addScalar("dtSubStageStartDate", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(PcspPlcmntDto.class)).list();
		if (subStageList.isEmpty())
			return null;
		else
			return subStageList.get(0).getDtSubStageStartDate();
	}

	/**
	 * This method is used to make sure a string is not null. If a non-null
	 * value is received, it is returned as is. If a null value value is
	 * received, a blank ("") String is returned.
	 *
	 * @param value
	 *            - the string that is being evaluated
	 * @return String - either valid value (not null) or blank ("")
	 */
	private static String getNonNullString(String value) {
		if ((value == null) || (value.equals(EMPTY_STRING))) {
			return EMPTY_STRING;
		}
		return value;
	}

	/**
	 * Checks to see if a given string is valid. This includes checking that the
	 * string is not null or empty.
	 *
	 * @param value
	 *            - the string that is being evaluated
	 * @return boolean - whether the string is valid
	 */
	private static boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

	/**
	 * retrieve the primary assessment event id using the id passed in
	 *
	 * @param primaryAssmtId
	 *            - ID
	 *
	 */
	@Override
	public Long getPrimaryAssmntEvent(Long primaryAssmtId) {
		Long idEvent = 0l;
		idEvent = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPrimaryAsmntEventSql.toString()).setParameter("idPrimaryAssmt", primaryAssmtId))
				.addScalar("idEvent", StandardBasicTypes.LONG).uniqueResult();
		return idEvent;
	}

	/**
	 * This method will fetch the the meth indicator flag
	 *
	 * @param idStage
	 *
	 * @return String meth Ind
	 */
	@Override
	public String getMethIndicator(Long idStage) {
		String methInd = "N";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMethIndSql).setParameter("idStage", idStage);
		if (null != query.uniqueResult()) {
			methInd = query.uniqueResult().toString();
		}
		return methInd;
	}

	/**
	 *
	 * Returns any prior stage ID for any given stage ID and a type request.
	 * Example. If a INT stage needs be found for a case thats currently in a
	 * FPR stage. Pass FPR stage ID and 'INT'
	 *
	 * @param CommonHelperReq
	 *            --ulIdStage id of the stage for which to retrieve the
	 *            corresponding prior stage
	 * @param CommonHelperReq
	 *            cdStageType stage type code. Example INT, INV etc
	 * @return PriorStageInRevRes
	 */
	@Override
	public PriorStageInRevRes getPriorStageInReverseChronologicalOrder(Long StageId, String stageType) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorStageInReverseSql)
				.addScalar("idStage", StandardBasicTypes.INTEGER).addScalar("idCase", StandardBasicTypes.INTEGER)
				.addScalar("idSituation", StandardBasicTypes.INTEGER).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("dtStart", StandardBasicTypes.TIMESTAMP).addScalar("dtClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("idUnit", StandardBasicTypes.INTEGER).setParameter("idStage", StageId)
				.setParameter("stageType", stageType)
				.setResultTransformer(Transformers.aliasToBean(PriorStageInRevRes.class));
		PriorStageInRevRes res = new PriorStageInRevRes();
		res = (PriorStageInRevRes) query.uniqueResult();
		return res;
	}

	/**
	 * This method checks to see if there are any open assessments for stage
	 *
	 * @param CommonHelperReq
	 *            --IdStage
	 *
	 * @return CpsInvCnclsnRes -- boolean
	 */
	@Override
	public Boolean hasOpenPCSPAsmntForStage(Long idStage) {
		Boolean hasPCSPAssmnt = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasOpenPCSPAsmt)
				.addScalar("asmnExists", StandardBasicTypes.STRING).setParameter("idStage", idStage);
		if (null != query.uniqueResult()) {
			if (query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.HAS_ASSESS_YES)
					|| query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.YES)) {
				hasPCSPAssmnt = Boolean.TRUE;
			}
		}
		return hasPCSPAssmnt;
	}

	/**
	 * This checks if there any open pcsp placements for case verified
	 *
	 * @param idCase
	 *
	 * @return boolean
	 *
	 */
	@Override
	public Boolean hasOpenPCSPlacement(Long idCase) {
		Boolean hasOpenPCSPlacement = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasOpenPCSPlacementSql)
				.addScalar("plcmntExists", StandardBasicTypes.STRING).setParameter("idCase", idCase);
		if (null != query.uniqueResult()) {
			if (query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.HAS_ASSESS_YES)
					|| query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.YES)) {
				hasOpenPCSPlacement = Boolean.TRUE;
			}
		}
		return hasOpenPCSPlacement;
	}

	/**
	 * This checks if there any open pcsp placements for case Not verified
	 *
	 * @param idCase
	 *
	 * @return boolean
	 *
	 */
	@Override
	public Boolean hasOpenPCSPlacementNotVerify(Long idCase, Long idStage) {
		Boolean isOpenPCSPlacementNotVerify = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasOpenPCSPlacementNotVerifySql)
				.addScalar("plcmntExists", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setParameter("idStage", idStage);
		if (null != query.uniqueResult()) {
			if (query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.Y)) {
				isOpenPCSPlacementNotVerify = Boolean.TRUE;
			}
		}
		return isOpenPCSPlacementNotVerify;
	}

	/**
	 * This method queries the database to find if the contact with the purpose
	 * of initial already exist.
	 *
	 * @param stageId
	 *
	 * @return boolean
	 */
	@Override
	public Boolean getContactPurposeStatus(Long idStage) {
		Boolean isContactMade = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasContactPurposeSql)
				.addScalar("count", StandardBasicTypes.INTEGER).setParameter("idStage", idStage);
		if (null != query.uniqueResult()) {
			if (Integer.valueOf(query.uniqueResult().toString()) > 0) {
				isContactMade = Boolean.TRUE;
			}
		}
		return isContactMade;
	}

	/**
	 * This method queries the database to find if the contact with the purpose
	 * of initiation already exist.
	 *
	 * @param stageId
	 *
	 * @return boolean
	 */
	@Override
	public Boolean getCntctPurposeInitiationStatus(Long idStage) {
		Boolean isContactMade = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasCntctPurposeInitiationSql)
				.addScalar("count", StandardBasicTypes.INTEGER).setParameter("idStage", idStage);
		if (null != query.uniqueResult()) {
			if (Integer.valueOf(query.uniqueResult().toString()) > 0) {
				isContactMade = Boolean.TRUE;
			}
		}
		return isContactMade;
	}

	@Override
	public Boolean setHasBeenSubmittedForApprovalCps(Long idEvent) {
		Boolean approvalStatus = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasSubmitForApproval)
				.addScalar("idApproval", StandardBasicTypes.INTEGER).setParameter("idEvent", idEvent);
		// Defect 11622 - retrieve the list and check the size
		if (!ObjectUtils.isEmpty(query.list()) && query.list().size() > 0) {
			approvalStatus = Boolean.TRUE;
		}
		return approvalStatus;
	}

	/**
	 * Method Description: This Method will retrieve list of all PCSP placements
	 * in a case. This method is not being used.
	 *
	 * @param pcspDTOs
	 *            the pcsp DT os
	 * @param caseId
	 *            the case id
	 * @param pcspDto
	 *            the pcsp dto
	 * @return List<PcspDto>
	 *
	 */
	@SuppressWarnings("unchecked")
	public List<PcspDto> getPcspPlacemnts(List<PcspDto> pcspDTOs, Long caseId, PcspDto pcspDto) {
		List<PcspDto> beanList = new ArrayList<>();
		beanList = (List<PcspDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(pcspPlacementsSql.toString()).setParameter("caseId", caseId))
				.addScalar("idPlacement", StandardBasicTypes.LONG)
				.addScalar("childName", StandardBasicTypes.STRING)
				.addScalar("caregiverName", StandardBasicTypes.STRING)
				.addScalar("dtStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("endReason", StandardBasicTypes.STRING)
				.addScalar("pcspGoal", StandardBasicTypes.STRING)
				.addScalar("stageCreated", StandardBasicTypes.STRING)
				.addScalar("stageEnded", StandardBasicTypes.STRING)
				.addScalar("idAssessment", StandardBasicTypes.LONG).addScalar("type", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PcspDto.class)).list();
		if (beanList != null) {
			for (PcspDto pcsp : beanList)
				pcspDTOs.add(pcsp);
		}
		return beanList;
	}

	/**
	 * Method Name: getChildPCSPEndDate Method Description: Retrieves the PCSP
	 * End date of a particular child from the CHILD_SAFETY_PLACEMENT table
	 *
	 * @param idPerson
	 * @return @
	 */
	@Override
	public List<PcspValueDto> getChildPCSPEndDate(Long idPerson) {
		List<PcspValueDto> pcspValueDtosList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSafetyPlacement.class);
		criteria.add(Restrictions.eq("personByIdPerson.idPerson", idPerson));
		ProjectionList requiredColumns = Projections.projectionList();

		requiredColumns.add(Projections.property("idChildSafetyPlcmt"));
		requiredColumns.add(Projections.property("personByIdPerson.idPerson"));
		requiredColumns.add(Projections.property("dtStart"));
		requiredColumns.add(Projections.property("dtEnd"));
		requiredColumns.add(Projections.property("cdStatus"));
		requiredColumns.add(Projections.property("dtEndRsn"));
		criteria.addOrder(Order.desc("dtEnd"));

		List<ChildSafetyPlacement> childSafetyPlacementsList = criteria.list();
		for (ChildSafetyPlacement childSafetyPlacement : childSafetyPlacementsList) {
			PcspValueDto pcspValueDto = new PcspValueDto();
			pcspValueDto.setIdChildSafetyPlcmt(childSafetyPlacement.getIdChildSafetyPlcmt());
			pcspValueDto.setIdPerson(childSafetyPlacement.getPersonByIdPerson().getIdPerson());
			pcspValueDto.setDtStart(childSafetyPlacement.getDtStart());
			pcspValueDto.setDtEnd(childSafetyPlacement.getDtEnd());
			pcspValueDto.setCdStatus(childSafetyPlacement.getCdStatus());
			pcspValueDto.setCdEndRsn(childSafetyPlacement.getCdEndRsn());
			pcspValueDtosList.add(pcspValueDto);
		}

		return pcspValueDtosList;
	}

	/**
	 *
	 * This method checks if there any open pcsp placements that have not been
	 * stage verified
	 *
	 * @param idCase
	 * @param idStage
	 *
	 * @return boolean
	 *
	 */
	public boolean hasOtherStagesOpen(Long idCase, String cdStage) {

		boolean hasPCSPAssmnt = false;

		StringBuilder modQuery = new StringBuilder(hasOtherStagesOpen);

		int startPos = modQuery.indexOf("'" + cdStage + "'");

		if (startPos > 0) {
			modQuery.replace(startPos, startPos + (cdStage.length() + 2), "''");

			String queryString = modQuery.toString();
			Query query = sessionFactory.getCurrentSession().createSQLQuery(queryString)
					.addScalar("stageExists", StandardBasicTypes.STRING).setParameter("idCase", idCase);
			if (null != query.uniqueResult()) {
				if (query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.HAS_ASSESS_YES)
						|| query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.YES)) {
					hasPCSPAssmnt = Boolean.TRUE;
				}
			}
		}
		return hasPCSPAssmnt;

	}

	/**
	 *
	 * This method checks if there FPR is open. 
	 *
	 * @param idCase
	 * @param idStage
	 *
	 * @return String
	 *
	 */

	//  : BR 44.4 Save Parental Child Safety Placement Detail .
	@Override
	public boolean hasFPRStagesOpen(Long idCase) {

		boolean isFPRStagesOpen = false;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasFPRStagesOpen)
				.addScalar("stageExists", StandardBasicTypes.STRING).setParameter("idCase", idCase);
		if (null != query.uniqueResult()) {
			if (query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.HAS_ASSESS_YES)
					|| query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.YES)) {
				isFPRStagesOpen = Boolean.TRUE;
			}
		}
		return isFPRStagesOpen;
	}


	/**
	 *
	 * This method return Prior stage. 
	 *
	 * @param idCase
	 * @param idStage
	 *
	 * @return String
	 *
	 */

	//  : BR 44.4 Save Parental Child Safety Placement Detail .
	@Override
	public String getPriorStage(Long idCase, Long idStage) {
		String cdStage =  null;
		cdStage = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorStage)
				.setParameter("idStage", idStage)
				.setParameter("idCase", idCase))
				.addScalar("cdStage", StandardBasicTypes.STRING).uniqueResult();
		return cdStage;
	}

	/**
	 *
	 * This method return Prior stage. 
	 *
	 * @param idCase
	 * @param idStage
	 *
	 * @return PriorStage
	 *
	 */

	//  : BR 44.4 Save Parental Child Safety Placement Detail .
	@Override
	public Long getPriorStageId(Long idCase, Long idStage) {
		Long priorStageId =  null;
		priorStageId = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorStageId)
				.setParameter("idStage", idStage)
				.setParameter("idCase", idCase))
				.addScalar("idPriorStage", StandardBasicTypes.LONG).uniqueResult();
		return priorStageId;
	}

	/**
	 * This method checks if there any open pcsp placements that have not been
	 * stage verified
	 *
	 * @param idCase
	 * @param idStage
	 * @return boolean
	 */
	@Override
	public Boolean hasOpenPCSPlcmntNotVrfd(Long idCase, Long idStage) {
		Boolean hasOpenPCSPlacement = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasOpenPCSPlcmntNotVrfd)
				.addScalar("plcmntExists", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setParameter("idCase", idCase);
		if (null != query.uniqueResult()) {
			if (query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.HAS_ASSESS_YES)
					|| query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.YES)) {
				hasOpenPCSPlacement = Boolean.TRUE;
			}
		}
		return hasOpenPCSPlacement;
	}

	/**
	 *
	 * Method Name: displayPCSPList Method Description: Get PCSP List for AR
	 * Conclusion Page Display
	 *
	 * @param idCase
	 * @param cdStage
	 * @return List<PcspDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PcspDto> displayPCSPList(Long idCase, String cdStage) {

		List<PcspDto> pcspDtoList = new ArrayList<PcspDto>();

		StringBuilder queryString = new StringBuilder(displayPCSPListSql.trim());
		if (ServiceConstants.CSTAGES_INV.equals(cdStage)) {
			queryString.append(AND_CD_INV_STAGE);
		} else if (ServiceConstants.CSTAGES_AR.equals(cdStage)) {
			queryString.append(AND_CD_AR_STAGE);
		}
		queryString.append(ORDER_BY);
		Query displayPCSPListquery = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString())
				.addScalar("idChildSafetyPlcmt", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idCaregvrPerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEndRsn", StandardBasicTypes.STRING)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("dtEnd", StandardBasicTypes.DATE)
				//ALM defect :14520 - indCaregvrManual field of PcspDto object is String, so change StandardBasicTypes to STRING
				.addScalar("indCaregvrManual", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("pcspComments", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmCaregvrFull", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(PcspDto.class));

		pcspDtoList = (List<PcspDto>) displayPCSPListquery.list();
		return pcspDtoList;

	}
}
