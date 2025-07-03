/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:It retrieves
 * a single row from the Person_Loc table Aug 18, 2017- 12:06:30 PM © 2017 Texas
 * Department of Family and Protective Services
 */
package us.tx.state.dfps.service.placement.daoimpl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonLoc;
import us.tx.state.dfps.common.dto.NumberOfRowsDto;
import us.tx.state.dfps.common.dto.PersonAssignedIdToDoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.LevelOfCareRtrvReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.placement.dao.PersonLocPersonDao;
import us.tx.state.dfps.service.placement.dto.*;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.web.todo.bean.ToDoStagePersonDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Repository
@SuppressWarnings("unchecked")
public class PersonLocPersonDaoImpl implements PersonLocPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonLocPersonDaoImpl.getPersonLevelofCare}")
	private transient String getPersonLevelofCare;

	@Value("${PersonLocPersonDaoImpl.getPersonLocById}")
	private String getPersonLocById;

	@Value("${WorkloadStgPerLinkSelDaoImpl.getWorkloadOnRole}")
	private String getWorkloadOnRole;

	@Value("${WorkloadStgPerLinkSelDaoImpl.getWorkload}")
	private String getWorkload;

	// Queries for caud11d dam

	@Value("${PersonLocPersonDaoImpl.getPLOCExistCount}")
	private String getPLOCExistCountSql;

	@Value("${PersonLocPersonDaoImpl.checkIfPlocExistBeforeInsertion}")
	private transient String checkIfPlocExistBeforeInsertionSql;

	@Value("${PersonLocPersonDaoImpl.plocInsertionValidation1}")
	private transient String plocInsertionValidation1Sql;

	@Value("${PersonLocPersonDaoImpl.plocInsertionValidation2}")
	private transient String plocInsertionValidation2Sql;

	@Value("${PersonLocPersonDaoImpl.plocInsertionValidation3}")
	private transient String plocInsertionValidation3Sql;

	@Value("${PersonLocPersonDaoImpl.plocInsertionValidation4}")
	private transient String plocInsertionValidation4Sql;

	@Value("${PersonLocPersonDaoImpl.plocInsertionValidation5}")
	private transient String plocInsertionValidation5Sql;

	@Value("${PersonLocPersonDaoImpl.getNextPlocEventVal}")
	private String getNextPlocEventValSql;

	@Value("${PersonLocPersonDaoImpl.insertNewPlocEventRecord}")
	private transient String insertNewPlocEventRecordSql;

	@Value("${PersonLocPersonDaoImpl.getExistingPlocRecordForUpdate}")
	private transient String getExistingPlocRecordForUpdateSql;

	@Value("${PersonLocPersonDaoImpl.checkPlocUpdateValidation1}")
	private transient String checkPlocUpdateValidation1Sql;

	@Value("${PersonLocPersonDaoImpl.checkPlocUpdateValidation2}")
	private transient String checkPlocUpdateValidation2Sql;

	@Value("${PersonLocPersonDaoImpl.checkPlocUpdateValidation3}")
	private transient String checkPlocUpdateValidation3Sql;

	@Value("${PersonLocPersonDaoImpl.checkPlocUpdateValidation4}")
	private transient String checkPlocUpdateValidation4Sql;

	@Value("${PersonLocPersonDaoImpl.updatePlocRecord}")
	private transient String updatePlocRecordSql;

	// Queries for caude9d dam
	@Value("${PersonLocPersonDaoImpl.checkIfALOCServiceRecordExistsForInsert}")
	private transient String checkIfALOCServiceRecordExistsForInsertSql;

	@Value("${PersonLocPersonDaoImpl.alocServiceInsertValidation1}")
	private transient String alocServiceInsertValidation1Sql;

	@Value("${PersonLocPersonDaoImpl.alocServiceInsertValidation2}")
	private transient String alocServiceInsertValidation2Sql;

	@Value("${PersonLocPersonDaoImpl.alocServiceInsertValidation3}")
	private String alocServiceInsertValidation3Sql;

	@Value("${PersonLocPersonDaoImpl.alocServiceInsertValidation4}")
	private transient String alocServiceInsertValidation4Sql;

	@Value("${PersonLocPersonDaoImpl.alocServiceInsertValidation5}")
	private transient String alocServiceInsertValidation5Sql;
	
	@Value("${placementDaoImpl.updatepersonloc}")
	private transient String updatepersonloc;
	
	@Value("${PlacementDaoImpl.getPersonlocEventId}")
	private String getPersonlocEventId;

	@Value("${PlacementDaoImpl.getPlcmtStartDtForQrtp}")
	private String getPlcmtStartDtForDate;

	private Date maxDate = ServiceConstants.GENERIC_END_DATE;
	public static final String PLOC_TASKCODE = "3140"; 
	private static final String END = " End ";
	private static final String START = " Start ";


	private static final Logger log = Logger.getLogger(PersonLocPersonDaoImpl.class);

	/**
	 * 
	 * Method Name: fetchPersonLOCByIdPlocEvent Method Description: It retrieves
	 * a single row from the Person_Loc table
	 * 
	 * @param pInputDataRec
	 * @return List<PersonLocPersonOutDto>
	 * 
	 */
	@Override
	public List<PersonLocPersonOutDto> fetchPersonLOCByIdPlocEvent(Long idPlocEvent) {
		log.debug("Entering method PersonLocPersonQUERYdam in PersonLocPersonDaoImpl");
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonLevelofCare)
				.addScalar("idPLOCEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdPLOCChild", StandardBasicTypes.STRING).addScalar("cdPLOCType", StandardBasicTypes.STRING)
				.addScalar("dtPLOCEnd", StandardBasicTypes.DATE).addScalar("dtPLOCStart", StandardBasicTypes.DATE)
				.addScalar("indPLOCCsupSend", StandardBasicTypes.STRING)
				.addScalar("indPLOCWriteHistory", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("dtSubmitTPR", StandardBasicTypes.DATE)
				.addScalar("dtReviewCompleted", StandardBasicTypes.DATE)
				.addScalar("nmTPRConsultant", StandardBasicTypes.STRING)
				.addScalar("dtReviewConducted", StandardBasicTypes.DATE)
				.addScalar("cdLvlChange", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtSetting", StandardBasicTypes.STRING)
				.addScalar("cdReviewType", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRecommendation1", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRecommendation2", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRecommendation3", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRecommendation4", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRecommendation5", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRecommendation6", StandardBasicTypes.STRING)
				.addScalar("nmLastUpdatedBy", StandardBasicTypes.STRING)
				.addScalar("txtDfpsComments", StandardBasicTypes.STRING)
				.addScalar("dtQrtpAssessmentCompleted", StandardBasicTypes.DATE)
				.addScalar("qrtpRecommended", StandardBasicTypes.STRING)
				.setParameter("idPLOCEvent", idPlocEvent)
				.setResultTransformer(Transformers.aliasToBean(PersonLocPersonOutDto.class)));
		List<PersonLocPersonOutDto> liCses15doDto = (List<PersonLocPersonOutDto>) sQLQuery.list();
		if (TypeConvUtil.isNullOrEmpty(liCses15doDto) || ObjectUtils.isEmpty(liCses15doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Cses15dDaoImpl.not.found", null, Locale.US));
		}
		log.debug("Exiting method PersonLocPersonQUERYdam in PersonLocPersonDaoImpl");
		return liCses15doDto;
	}

	/**
	 * 
	 * Method Name: getPersonLocById Method Description: It retrieves a single
	 * row from the Person_Loc table by idPersonLoc
	 * 
	 * @param personLocInDto
	 * @return List<PersonLocPersonOutDto>
	 * 
	 */
	@Override
	public PersonLocOutDto getPersonLocById(PersonLocInDto personLocInDto) {
		PersonLocOutDto personLocOutDto = null;
		log.debug("Entering method getPersonLocById in PersonLocPersonDaoImpl");
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonLocById)
				.addScalar("idPlocEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPlocChild", StandardBasicTypes.STRING)
				.addScalar("cdPlocType", StandardBasicTypes.STRING).addScalar("dtPlocEnd", StandardBasicTypes.DATE)
				.addScalar("indPlocCsupSend", StandardBasicTypes.STRING)
				.addScalar("indPlocWriteHistory", StandardBasicTypes.STRING)
				.addScalar("dtPlocStart", StandardBasicTypes.DATE)
				.setParameter("idPerson", personLocInDto.getIdPerson())
				.setParameter("cdPlocType", personLocInDto.getCdPlocType())
				.setParameter("dtPlocStart", personLocInDto.getDtPlocStart())
				.setResultTransformer(Transformers.aliasToBean(PersonLocOutDto.class)));
		List<PersonLocOutDto> personLocOutDtoList = (List<PersonLocOutDto>) sQLQuery.list();
		if (!ObjectUtils.isEmpty(personLocOutDtoList)) {
			personLocOutDto = personLocOutDtoList.get(0);
		}
		log.debug("Exiting method getPersonLocById in PersonLocPersonDaoImpl");
		return personLocOutDto;
	}

	/**
	 * 
	 * Method Name: retrievePersonByRoleAndStage Method Description: This DAM
	 * will retrieve the ID PERSON for a given role, for a given stage. It's
	 * used to find the primary worker for a given stage.
	 * 
	 * @param toDoStagePersonDto
	 * @return List<PersonAssignedIdToDoDto>
	 *
	 */
	@Override
	public List<PersonAssignedIdToDoDto> retrievePersonByRoleAndStage(ToDoStagePersonDto toDoStagePersonDto) {
		log.debug("Entering method CINV51DQUERYdam in PersonLocPersonDaoImpl");
		List<PersonAssignedIdToDoDto> liCinv51doDto;
		if (toDoStagePersonDto.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.PRIMARY_ROLE)
				|| (toDoStagePersonDto.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.SECONDARY_ROLE))) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkloadOnRole)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.setParameter("hI_ulIdStage", toDoStagePersonDto.getIdStage())
					.setParameter("hI_szCdStagePersRole", toDoStagePersonDto.getCdStagePersRole())
					.setResultTransformer(Transformers.aliasToBean(PersonAssignedIdToDoDto.class)));
			liCinv51doDto = (List<PersonAssignedIdToDoDto>) sQLQuery1.list();
		} else {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWorkload)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.setParameter("hI_ulIdStage", toDoStagePersonDto.getIdStage())
					.setParameter("hI_szCdStagePersRole", toDoStagePersonDto.getCdStagePersRole())
					.setResultTransformer(Transformers.aliasToBean(PersonAssignedIdToDoDto.class)));
			liCinv51doDto = (List<PersonAssignedIdToDoDto>) sQLQuery2.list();
		}
		if (TypeConvUtil.isNullOrEmpty(liCinv51doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.role", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(liCinv51doDto) || liCinv51doDto.size() == 0) {
			throw new DataNotFoundException(messageSource.getMessage("Cses15dDaoImpl.not.found", null, Locale.US));
		}
		log.debug("Exiting method CINV51DQUERYdam in PersonLocPersonDaoImpl");

		return liCinv51doDto;
	}

	/**
	 * 
	 * Method Name: checkForAuthorizedPLOC Method Description: This is a Query
	 * Dam that will look in the Person LOC table for Level Care
	 * Type(Authorized) that already exist for the same Person ID and date.
	 * 
	 * @param personLevelOfCareDto
	 * @return List<NumberOfRowsDto> @
	 */
	@Override
	public List<NumberOfRowsDto> checkForAuthorizedPLOC(PersonLevelOfCareDto personLevelOfCareDto) {
		log.debug("Entering method CSUB81DQUERYdam in PersonLocPersonDaoImpl");

		List<NumberOfRowsDto> noOfRowsList = null;

		noOfRowsList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPLOCExistCountSql)
				.setParameter("idPerson", personLevelOfCareDto.getIdPerson())
				.setParameter("cdPlocType", personLevelOfCareDto.getCdPlocType())
				.setParameter("dtPlocStart", personLevelOfCareDto.getDtPlocStart()))
						.addScalar("sysNbrNumberOfRows", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(NumberOfRowsDto.class)).list();

		if (TypeConvUtil.isNullOrEmpty(noOfRowsList)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.role", null, Locale.US));
		}
		log.debug("Exiting method CSUB81DQUERYdam in PersonLocPersonDaoImpl");
		return noOfRowsList;
	}

	/**
	 * 
	 * Method Name: checkIfPlocExistBeforeInsertion Method Description: (DAM
	 * CAUD11D)Check if there's any record of this ID_PERSON and not RLOC. If
	 * none, then everything passed. No need to go through all these validation.
	 * If some, then must go through all checks.
	 * 
	 * @param Long,String
	 *            Long
	 * @return PLOCDetailDto
	 * 
	 */

	public List<PLOCDetailDto> checkIfPlocExistBeforeInsertion(Long idPerson, String plocType) {
		log.debug("Exiting method checkIfPlocExistBeforeInsertion in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(checkIfPlocExistBeforeInsertionSql).setParameter("idPerson", idPerson)
				.setParameter("plocType", plocType)).addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkIfPlocExistBeforeInsertion in PersonLocPersonDaoImpl");

		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: plocInsertionValidation1
	 * 
	 * Method Description: VALIDATE 1: Check if new records overlaps other
	 * records on LEFT (works whether new record overlaps 1 or more existing
	 * records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 *
	 */
	public List<PLOCDetailDto> plocInsertionValidation1(PLOCDetailInDto plocDetailInDto) {
		log.debug("Exiting method plocInsertionValidation1 in PersonLocPersonDaoImpl");
		// remove below date settings after testing
		List<PLOCDetailDto> plocDetailDtoList = null;
		if (!TypeConvUtil.isNullOrEmpty(plocDetailInDto)
				&& TypeConvUtil.isNullOrEmpty(plocDetailInDto.getDtPlocStart())) {
			plocDetailInDto.setDtPlocStart(maxDate);
		}
		if (!TypeConvUtil.isNullOrEmpty(plocDetailInDto)
				&& TypeConvUtil.isNullOrEmpty(plocDetailInDto.getDtPlocEnd())) {
			plocDetailInDto.setDtPlocEnd(maxDate);
		}
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(plocInsertionValidation1Sql)
				.setParameter("idPerson", plocDetailInDto.getIdPerson())
				.setParameter("plocType", plocDetailInDto.getCdPlocType())
				.setParameter("dtEnd", plocDetailInDto.getDtPlocEnd())
				.setParameter("dtStart", plocDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method plocInsertionValidation1 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: plocInsertionValidation2 Method Description: Check if new
	 * records overlaps other records on RIGHT (works whether new record
	 * overlaps 1 or more existing records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> plocInsertionValidation2(PLOCDetailInDto plocDetailInDto) {
		log.debug("Exiting method plocInsertionValidation2 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(plocInsertionValidation2Sql)
				.setParameter("idPerson", plocDetailInDto.getIdPerson())
				.setParameter("plocType", plocDetailInDto.getCdPlocType())
				.setParameter("dtEnd", plocDetailInDto.getDtPlocEnd())
				.setParameter("dtStart", plocDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method plocInsertionValidation2 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: plocInsertionValidation3
	 * 
	 * Method Description: Check if new records is either identical OR within a
	 * record
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 */

	public List<PLOCDetailDto> plocInsertionValidation3(PLOCDetailInDto plocDetailInDto) {
		log.debug("Exiting method plocInsertionValidation3 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(plocInsertionValidation3Sql)
				.setParameter("idPerson", plocDetailInDto.getIdPerson())
				.setParameter("plocType", plocDetailInDto.getCdPlocType())
				.setParameter("dtEnd", plocDetailInDto.getDtPlocEnd())
				.setParameter("dtStart", plocDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method plocInsertionValidation3 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: plocInsertionValidation4
	 * 
	 * Method Description: Check if the gap on LEFT of hI_dtDtPlocStart is
	 * bigger than 1 day. SELECT statement will return record if it finds one,
	 * which means gap is >= 1.0 day ==> ERROR!
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 */
	public List<PLOCDetailDto> plocInsertionValidation4(PLOCDetailInDto pLOCDetailInDto) {
		String dtPlocStartDate = DateUtils.stringDt(pLOCDetailInDto.getDtPlocStart());
		log.debug("Exiting method plocInsertionValidation4 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(plocInsertionValidation4Sql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("plocType", pLOCDetailInDto.getCdPlocType()).setParameter("dtStart", dtPlocStartDate))
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtPlocEnd", StandardBasicTypes.DATE)
						.addScalar("currPlocStartEndDateDiff", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method plocInsertionValidation4 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: plocInsertionValidation5 Method Description: Check if the
	 * gap on RIGHT of hI_dtDtPlocStart is bigger than 1 day. SELECT statement
	 * will return record if it finds one, which means gap is >= 1.0 day ==>
	 * ERROR!
	 * 
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 */
	// I5
	public List<PLOCDetailDto> plocInsertionValidation5(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method plocInsertionValidation5 in PersonLocPersonDaoImpl");
		String dtPlocEndDate = null;
		dtPlocEndDate = DateUtils.stringDt(pLOCDetailInDto.getDtPlocEnd());
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(plocInsertionValidation5Sql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("plocType", pLOCDetailInDto.getCdPlocType()).setParameter("dtEnd", dtPlocEndDate))
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtPlocStart", StandardBasicTypes.DATE)
						.addScalar("currPlocStartEndDateDiff", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method plocInsertionValidation5 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: autoGeneratePlocEventId Method Description: Auto Generate
	 * ploc event id
	 * 
	 * @param
	 * @return Long
	 * 
	 */

	public long autoGeneratePlocEventId() {
		log.debug("Exiting method autoGeneratePlocEventId in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = (List<PLOCDetailDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getNextPlocEventValSql).addScalar("idEvent", StandardBasicTypes.LONG))
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method autoGeneratePlocEventId in PersonLocPersonDaoImpl");
		return plocDetailDtoList.get(0).getIdEvent();
	}

	/**
	 * 
	 * Method Name: newPlocRecordInsertion Method Description: When all
	 * validations are passed insert new record
	 * 
	 * @param PLOCDetailInDto
	 * @return void
	 */

	public void newPlocRecordInsertion(PLOCDetailInDto plocDetailInDto) {
		log.debug("Exiting method newPlocRecordInsertion in PersonLocPersonDaoImpl");

		// If StartDate or EndDate is NULL then set it to MAXDATE
		if (!TypeConvUtil.isNullOrEmpty(plocDetailInDto)
				&& TypeConvUtil.isNullOrEmpty(plocDetailInDto.getDtPlocStart())) {
			plocDetailInDto.setDtPlocStart(maxDate);
		}
		if (!TypeConvUtil.isNullOrEmpty(plocDetailInDto)
				&& TypeConvUtil.isNullOrEmpty(plocDetailInDto.getDtPlocEnd())) {
			plocDetailInDto.setDtPlocEnd(maxDate);
		}

		PersonLoc personLoc = new PersonLoc();
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, plocDetailInDto.getIdPerson());

		personLoc.setIdPlocEvent(plocDetailInDto.getIdPlocEvent());
		personLoc.setPerson(person);
		personLoc.setCdPlocChild(plocDetailInDto.getCdPlocChild());
		personLoc.setIndPlocCsupSend(plocDetailInDto.getcIndPlocCsupSend());
		personLoc.setIndPlocWriteHistory(plocDetailInDto.getcIndPlocWriteHistory());
		personLoc.setIdPlocPersonUpdate(plocDetailInDto.getIdPersUpdt());
		personLoc.setDtPlocEnd(plocDetailInDto.getDtPlocEnd());
		personLoc.setDtPlocStart(plocDetailInDto.getDtPlocStart());
		personLoc.setCdRevType(plocDetailInDto.getCdRevType());
		personLoc.setCdPlocType(plocDetailInDto.getCdPlocType());
		personLoc.setDtLastUpdate(new Date());
		personLoc.setTxtDfpsComments(plocDetailInDto.getTxtDfpsComments());
		personLoc.setDtQrtpAssessmentCompleted(plocDetailInDto.getDtQrtpAssessmentCompleted());
		personLoc.setQrtpRecommended(plocDetailInDto.getQrtpRecommended());
		sessionFactory.getCurrentSession().saveOrUpdate(personLoc);

		log.debug("Exiting method newPlocRecordInsertion in PersonLocPersonDaoImpl");
	}

	// Validation for updating ploc record starts
	/**
	 * 
	 * Method Name: checkIfPlocExistBeforeUpdate Method Description: Check if
	 * there's any record at all. It should already exist in order to do an
	 * update.
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 */

	public List<PLOCDetailDto> checkIfPlocExistBeforeUpdate(PLOCDetailInDto plocDetailInDto) {
		log.debug("Exiting method checkIfPlocExistBeforeUpdate in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getExistingPlocRecordForUpdateSql)
				.setParameter("idEvent", plocDetailInDto.getIdPlocEvent())
				.setParameter("cdPlocType", plocDetailInDto.getCdPlocType())
				.setParameter("idPerson", plocDetailInDto.getIdPerson())
				.setParameter("dtLastUpdate", plocDetailInDto.getDtLastUpdate()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("currPlocStart", StandardBasicTypes.DATE)
						.addScalar("currPlocEnd", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkIfPlocExistBeforeUpdate in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation1 Method Description: check for
	 * LEFT-SIDE OVERLAP If new START_DATE overlaps any of its LEFT record(s)
	 * (If its overlaps some, then it must at least overlaps its immediate
	 * previous record, and that's what we want to know)
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 *
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation1(PLOCDetailInDto pLOCDetailInDto, Date currPlocStart) {
		log.debug("Exiting method plocInsertionValidation1 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		Date dtPlocStart = DateUtils.getDateWithoutTime(pLOCDetailInDto.getDtPlocStart());
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkPlocUpdateValidation1Sql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("cdPlocType", pLOCDetailInDto.getCdPlocType()).setParameter("dtPlocStart", dtPlocStart)
				.setParameter("currPlocStart", currPlocStart)
				.setParameter("idPlocEvent", pLOCDetailInDto.getIdPlocEvent()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkPlocUpdateValidation1 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation2 Method Description: check for
	 * RIGHT-SIDE OVERLAP If new START_DATE overlaps any of its RIGHT record(s)
	 * (If its overlaps some, then it must at least overlaps its immediate next
	 * record, and that's what we want to know)
	 * 
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation2(PLOCDetailInDto pLOCDetailInDto, Date currPlocEnd) {
		log.debug("Exiting method checkPlocUpdateValidation2 in PersonLocPersonDaoImpl");
		if (ObjectUtils.isEmpty(pLOCDetailInDto.getDtPlocEnd())) {
			pLOCDetailInDto.setDtPlocEnd(ServiceConstants.GENERIC_END_DATE);
		}
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkPlocUpdateValidation2Sql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("cdPlocType", pLOCDetailInDto.getCdPlocType()).setParameter("currPlocEnd", currPlocEnd)
				.setParameter("dtPlocEnd", pLOCDetailInDto.getDtPlocEnd())
				.setParameter("idEvent", pLOCDetailInDto.getIdPlocEvent()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkPlocUpdateValidation2 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation3 Method Description: Gap LEFT of
	 * hI_dtDtPlocStart Check this gap ONLY IF hI_dtDtPlocStart <>
	 * curr_ploc_star because: if the 2 are the same, then the user does NOT
	 * want to update that end. Only when the 2 are different does it mean that
	 * the user wants to update that end
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation3(PLOCDetailInDto pLOCDetailInDto, Date currPlocStart) {
		log.debug("Exiting method checkPlocUpdateValidation3 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		String startDate = DateUtils.stringDt(pLOCDetailInDto.getDtPlocStart());
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkPlocUpdateValidation3Sql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("cdPlocType", pLOCDetailInDto.getCdPlocType())
				.setParameter("currPlocStart", currPlocStart).setParameter("dtPlocStart", startDate))
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtPlocEnd", StandardBasicTypes.DATE)
						.addScalar("currPlocStartEndDateDiff", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkPlocUpdateValidation3 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation4 Method Description: Gap RIGHT of
	 * hI_dtDtPlocEnd Check this gap ONLY IF hI_dtDtPlocEnd <> curr_ploc_end
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation4(PLOCDetailInDto pLOCDetailInDto, String dtPlocEndDate,
			Date currPlocEnd) {
		String currPlocEndDt = DateUtils.stringDt(currPlocEnd);
		log.debug("Exiting method checkPlocUpdateValidation4 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkPlocUpdateValidation4Sql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("cdPlocType", pLOCDetailInDto.getCdPlocType()).setParameter("dtPlocEnd", dtPlocEndDate)
				.setParameter("currPlocEnd", currPlocEndDt)).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("currPlocStartEndDateDiff", StandardBasicTypes.LONG)
						.addScalar("dtPlocStart", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkPlocUpdateValidation4 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: updatePlocRecord Method Description: New record could be: 1.
	 * RLOC type: Just update it regardless if time overlaps 2. non-RLOC type:
	 * Pass all validation (supposing it is requeted to do so) Update current
	 * record with information from host input variables DO NOT add CD_PLOC_TYPE
	 * in this UDPATE because it is not updateable
	 * 
	 * @param PLOCDetailInDto
	 * @return void
	 * 
	 */

	public void updatePlocRecord(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method updatePlocRecord in PersonLocPersonDaoImpl");

		PersonLoc personLoc = (PersonLoc) sessionFactory.getCurrentSession().get(PersonLoc.class,
				pLOCDetailInDto.getIdPlocEvent());
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, pLOCDetailInDto.getIdPerson());
		personLoc.setPerson(person);
		personLoc.setCdPlocChild(pLOCDetailInDto.getCdPlocChild());
		personLoc.setIndPlocCsupSend(pLOCDetailInDto.getcIndPlocCsupSend());
		personLoc.setIndPlocWriteHistory(pLOCDetailInDto.getcIndPlocWriteHistory());
		personLoc.setIdPlocPersonUpdate(pLOCDetailInDto.getIdPersUpdt());
		personLoc.setDtPlocStart(pLOCDetailInDto.getDtPlocStart());
		if (!ObjectUtils.isEmpty(pLOCDetailInDto.getDtPlocEnd())) {
			personLoc.setDtPlocEnd(pLOCDetailInDto.getDtPlocEnd());
		} else {
			personLoc.setDtPlocEnd(maxDate);
		}
		if (StringUtils.isNotEmpty(pLOCDetailInDto.getCdRevType())) {
			personLoc.setCdRevType(pLOCDetailInDto.getCdRevType());
		}
		if (!ObjectUtils.isEmpty(pLOCDetailInDto.getDtQrtpAssessmentCompleted())) {
			personLoc.setDtQrtpAssessmentCompleted(pLOCDetailInDto.getDtQrtpAssessmentCompleted());
		}
		if (StringUtils.isNotEmpty(pLOCDetailInDto.getQrtpRecommended())) {
			personLoc.setQrtpRecommended(pLOCDetailInDto.getQrtpRecommended());
		}
		personLoc.setDtLastUpdate(new Date());
		personLoc.setTxtDfpsComments(pLOCDetailInDto.getTxtDfpsComments());
		sessionFactory.getCurrentSession().saveOrUpdate(personLoc);
		log.debug("Exiting method updatePlocRecord in PersonLocPersonDaoImpl");
	}

	/**
	 * 
	 * Method Name: checkIfALOCServiceRecordExistsForInsert Method Description:
	 * Check if there's any record of this ID_PERSON and not RLOC. If none, then
	 * everything passed. No need to go through all these validation. If some,
	 * then must go through all checks.
	 *
	 * @param Long,
	 *            Long
	 * @return PLOCDetailDto
	 * 
	 */

	public List<PLOCDetailDto> checkIfALOCServiceRecordExistsForInsert(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method checkIfALOCServiceRecordExistsForInsert in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(checkIfALOCServiceRecordExistsForInsertSql)
				.setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("idStage", pLOCDetailInDto.getIdStage())).addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method checkIfALOCServiceRecordExistsForInsert in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: alocServiceInsertValidation1
	 * 
	 * Method Description: VALIDATE 1: Check if new records overlaps other
	 * records on LEFT (works whether new record overlaps 1 or more existing
	 * records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation1(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method alocServiceInsertValidation1 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(alocServiceInsertValidation1Sql).setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("idStage", pLOCDetailInDto.getIdStage())
				.setParameter("dtPlocEnd", pLOCDetailInDto.getDtPlocEnd())
				.setParameter("dtPlocStart", pLOCDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method alocServiceInsertValidation1 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: alocServiceInsertValidation2 Method Description: Check if
	 * new records overlaps other records on RIGHT (works whether new record
	 * overlaps 1 or more existing records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation2(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method alocServiceInsertValidation2 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(alocServiceInsertValidation2Sql).setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("idStage", pLOCDetailInDto.getIdStage())
				.setParameter("dtPlocEnd", pLOCDetailInDto.getDtPlocEnd())
				.setParameter("dtPlocStart", pLOCDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method alocServiceInsertValidation2 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: alocServiceInsertValidation3
	 * 
	 * Method Description: Check if new records is either identical OR within a
	 * record
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */

	public List<PLOCDetailDto> alocServiceInsertValidation3(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method alocServiceInsertValidation3 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(alocServiceInsertValidation3Sql).setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("idStage", pLOCDetailInDto.getIdStage())
				.setParameter("dtPlocEnd", pLOCDetailInDto.getDtPlocEnd())
				.setParameter("dtPlocStart", pLOCDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method alocServiceInsertValidation3 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: alocServiceInsertValidation4
	 * 
	 * Method Description: Check if the gap on LEFT of hI_dtDtPlocStart is
	 * bigger than 1 day. SELECT statement will return record if it finds one,
	 * which means gap is >= 1.0 day ==> ERROR!
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation4(PLOCDetailInDto pLOCDetailInDto) {

		log.debug("Exiting method alocServiceInsertValidation4 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(alocServiceInsertValidation4Sql).setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("idStage", pLOCDetailInDto.getIdStage())
				.setParameter("dtPlocStart", pLOCDetailInDto.getDtPlocStart()))
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtPlocEnd", StandardBasicTypes.DATE)
						.addScalar("tsCurrPlocStartEndDateDiff", StandardBasicTypes.FLOAT)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method alocServiceInsertValidation4 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * 
	 * Method Name: alocServiceInsertValidation5 Method Description: Check if
	 * the gap on RIGHT of hI_dtDtPlocStart is bigger than 1 day. SELECT
	 * statement will return record if it finds one, which means gap is >= 1.0
	 * day ==> ERROR!
	 * 
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto
	 * 
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation5(PLOCDetailInDto pLOCDetailInDto) {
		log.debug("Exiting method alocServiceInsertValidation5 in PersonLocPersonDaoImpl");
		List<PLOCDetailDto> plocDetailDtoList = null;
		plocDetailDtoList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(alocServiceInsertValidation5Sql).setParameter("idPerson", pLOCDetailInDto.getIdPerson())
				.setParameter("idStage", pLOCDetailInDto.getIdStage())
				.setParameter("dtPlocEnd", pLOCDetailInDto.getDtPlocEnd()))
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtPlocStart", StandardBasicTypes.DATE)
						.addScalar("currPlocStartEndDateDiff", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PLOCDetailDto.class)).list();
		log.debug("Exiting method alocServiceInsertValidation5 in PersonLocPersonDaoImpl");
		return plocDetailDtoList;
	}

	/**
	 * Method Name: updatePloc 
	 * Method Description:to update ploc for TEP placement
	 * @param startDate
	 * @param idPerson
	 */
	@Override
	public void updatePloc(Date startDate, Long idPlocPerson) {
		SQLQuery query =  (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(updatepersonloc);	
		query.setParameter("idPlocPerson",idPlocPerson);
		query.setParameter("idPlacementStartDate",startDate);
		int result = query.executeUpdate();
		log.debug("Exiting method  updatePloc in PersonLocPersonDaoImpl "+result);
		
	}
	
	/**
	 * Method Name: updateServiceLevel 
	 * Method Description:to update ploc for TFC placement
	 * @param placementReq
	 * @param placementType
	 * @return LevelOfCareRtrvReq
	 */
	@Override
	public LevelOfCareRtrvReq updateServiceLevel(PlacementReq placementReq, String placementType) {
		
		//all get the max bloc and aloc event id
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonlocEventId)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", placementReq.getIdCase())
				.setParameter("cdPlocType", placementType).setParameter("idPlcmtChild", placementReq.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));
		EventDto eventDto = (EventDto) query.uniqueResult();
		Event eventDtl = (Event) sessionFactory.getCurrentSession().get(Event.class, eventDto.getIdEvent());
		eventDto.setEventDescr(eventDtl.getTxtEventDescr());
		LevelOfCareRtrvReq levelOfCareRtrvReq = null;
		if (!ObjectUtils.isEmpty(eventDto) && !TypeConvUtil.isNullOrEmpty(eventDto.getIdEvent())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonLoc.class)
					.add(Restrictions.eq("idPlocEvent", eventDto.getIdEvent()));
			PersonLoc personLoc = (PersonLoc) criteria.uniqueResult();
			personLoc.setDtLastUpdate(new Date());
			personLoc.setDtPlocEnd(placementReq.getDtPlcmtStart());
			sessionFactory.getCurrentSession().update(personLoc);
			levelOfCareRtrvReq = prepareEventServiceStatusObj(placementReq, placementType, eventDto);
		}
		return levelOfCareRtrvReq;
	}

	/**
	 * Method Name: updateServiceLevelForQRTP
	 * Method Description:to update ploc for QRTP placement
	 * @param placementReq
	 * @param placementType
	 * @return LevelOfCareRtrvReq
	 */
	@Override
	public LevelOfCareRtrvReq updateServiceLevelForQRTP(PlacementReq placementReq, String placementType) {

		//all get the max bloc and aloc event id
		LevelOfCareRtrvReq levelOfCareRtrvReq = null;
		if(!TypeConvUtil.isNullOrEmpty(placementReq)) {
			Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonlocEventId)
					.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", placementReq.getIdCase())
					.setParameter("cdPlocType", placementType).setParameter("idPlcmtChild", placementReq.getIdPerson())
					.setResultTransformer(Transformers.aliasToBean(EventDto.class));
			EventDto eventDto = (EventDto) query.uniqueResult();
			Event eventDtl = (Event) sessionFactory.getCurrentSession().get(Event.class, eventDto.getIdEvent());
			eventDto.setEventDescr(eventDtl.getTxtEventDescr());

			if (!ObjectUtils.isEmpty(eventDto) && !TypeConvUtil.isNullOrEmpty(eventDto.getIdEvent())) {
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonLoc.class)
						.add(Restrictions.eq("idPlocEvent", eventDto.getIdEvent()));
				PersonLoc personLoc = (PersonLoc) criteria.uniqueResult();
				personLoc.setDtLastUpdate(new Date());
				if (placementType.equals(ServiceConstants.CPLOCELG_BLOC)) {
					if (placementReq.getBlocEventDesc().contains(ServiceConstants.END))
						personLoc.setDtPlocEnd(placementReq.getDtPlcmtEnd());
					else
						personLoc.setDtPlocEnd(ServiceConstants.MAX_DATE);
				} else {
					personLoc.setDtPlocEnd(placementReq.getDtPlcmtEnd());
				}
				sessionFactory.getCurrentSession().update(personLoc);
				levelOfCareRtrvReq = prepareEventServiceStatusObjForQrtp(placementReq, placementType, eventDto);
			}
		}
		return levelOfCareRtrvReq;
	}

	@Override
	public Date getQrtpPlacementStartDate(Long caseId, Long stageId) {
		Date date = (Date)sessionFactory.getCurrentSession().createSQLQuery(getPlcmtStartDtForDate)
				.addScalar("startDate", StandardBasicTypes.DATE).setParameter("idCase", caseId)
				.setParameter("idStage", stageId)
				.uniqueResult();
		return date;
	}

	@Override
	/**
	 * Method Name: fetchOpenServiceLevels
	 * Method Description:fetch the service level with furture end date and close before adding placement
	 * @param placementReq
	 * @return LevelOfCareRtrvReq
	 */
	public List<LevelOfCareRtrvReq> fetchOpenServiceLevels(PlacementReq placementReq) {

		List<LevelOfCareRtrvReq> LevelOfCareRtrvReqList = new ArrayList<>();
		//fetchPersonLOCByIdPlocEvent getPersonLevelofCare
	if(!TypeConvUtil.isNullOrEmpty(placementReq)) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonLoc.class);
		criteria.add(Restrictions.eq("idCase", placementReq.getIdCase()));
		criteria.add(Restrictions.gt("dtPlocEnd", placementReq.getDtPlcmtStart()));
		criteria.add(Restrictions.in("cdPlocType", new String[]{"ALOC", "BLOC"}));
		criteria.addOrder(Order.desc("dtLastUpdate"));
		List<PersonLoc> personLocList = (List<PersonLoc>)criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(personLocList)) {
			for (PersonLoc personLoc : personLocList) {
				personLoc.setDtLastUpdate(new Date());
				personLoc.setDtPlocEnd(placementReq.getDtPlcmtStart());
				sessionFactory.getCurrentSession().saveOrUpdate(personLoc);
				LevelOfCareRtrvReq levelOfCareRtrvReq = new LevelOfCareRtrvReq();
				levelOfCareRtrvReq.setIdCase(personLoc.getIdCase());
				Criteria eventCriteria = sessionFactory.getCurrentSession().createCriteria(Event.class)
						.add(Restrictions.eq("idEvent", personLoc.getIdPlocEvent()));
				Event event = (Event) eventCriteria.uniqueResult();
				levelOfCareRtrvReq.setIdPlocEvent(event.getIdEvent());
				levelOfCareRtrvReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				levelOfCareRtrvReq.setIdPerson(personLoc.getPerson().getIdPerson());
				levelOfCareRtrvReq.setIdStage(placementReq.getIdStage());
				levelOfCareRtrvReq.setIdPersUpdt(placementReq.getCommonDto().getIdUser());
				levelOfCareRtrvReq.setCdTask(PLOC_TASKCODE);
				levelOfCareRtrvReq.setCdEventType(null);
				String eventDescription = event.getTxtEventDescr();
				SimpleDateFormat simpleDate = new SimpleDateFormat("MM/dd/yyyy");
				String startStr= eventDescription.substring(0,(eventDescription.indexOf("Start")));
				levelOfCareRtrvReq.setEventDescr(startStr + START + simpleDate.format(personLoc.getDtPlocStart()) + END + simpleDate.format(placementReq.getDtPlcmtStart()));

				levelOfCareRtrvReq.setLastUpdate(new Date());
				levelOfCareRtrvReq.setDtPlocEnd(placementReq.getDtPlcmtStart());

				if (ServiceConstants.CPLOCELG_BLOC.equals(personLoc.getCdPlocType())) {
					levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_BLOC);
				} else {
					levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_ALOC);
				}
				LevelOfCareRtrvReqList.add(levelOfCareRtrvReq);
			}
		}
	}
	return LevelOfCareRtrvReqList;
	}

	/**
	 * Method Name: prepareEventServiceStatusObj 
	 * Method Description:to prepare event status for TFC placement
	 * @param placementReq
	 * @param placementType
	 * @return LevelOfCareRtrvReq
	 */
	private LevelOfCareRtrvReq prepareEventServiceStatusObj(PlacementReq placementReq, String placementType,
															EventDto eventDto) {
		LevelOfCareRtrvReq levelOfCareRtrvReq = new LevelOfCareRtrvReq();
		levelOfCareRtrvReq.setIdCase(placementReq.getIdCase());
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonLoc.class)
				.add(Restrictions.eq("idPlocEvent", eventDto.getIdEvent()));
		PersonLoc personLoc = (PersonLoc) criteria.uniqueResult();
		levelOfCareRtrvReq.setIdPlocEvent(personLoc.getIdPlocEvent());
		levelOfCareRtrvReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		levelOfCareRtrvReq.setIdPerson(personLoc.getPerson().getIdPerson());
		levelOfCareRtrvReq.setIdStage(placementReq.getIdCase());
		levelOfCareRtrvReq.setIdPersUpdt(placementReq.getCommonDto().getIdUser());
		levelOfCareRtrvReq.setCdTask(PLOC_TASKCODE);
		levelOfCareRtrvReq.setCdEventType(null);
		SimpleDateFormat simpleDate = new SimpleDateFormat("MM/dd/yyyy");
		int indexPos = eventDto.getEventDescr().indexOf(END);
		if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
			levelOfCareRtrvReq.setEventDescr((indexPos > 0 ? eventDto.getEventDescr().substring(0, indexPos) : eventDto.getEventDescr())
							+ END
							+ simpleDate.format(placementReq.getDtPlcmtStart()));
		} else {
			levelOfCareRtrvReq.setEventDescr((indexPos > 0 ? eventDto.getEventDescr().substring(0, indexPos) : eventDto.getEventDescr())
							+ END
							+ simpleDate.format(placementReq.getDtPlcmtStart()));
		}
		levelOfCareRtrvReq.setLastUpdate(new Date());
		levelOfCareRtrvReq.setDtPlocEnd(placementReq.getDtPlcmtStart());
		if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
			levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_BLOC);
		} else {
			levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_ALOC);
		}
		return levelOfCareRtrvReq;

	}

	/**
	 * Method Name: prepareEventServiceStatusObj 
	 * Method Description:to prepare event status for Qrtp placement
	 * @param placementReq
	 * @param placementType
	 * @return LevelOfCareRtrvReq
	 */
	private LevelOfCareRtrvReq prepareEventServiceStatusObjForQrtp(PlacementReq placementReq, String placementType,
																   EventDto eventDto) {
		LevelOfCareRtrvReq levelOfCareRtrvReq = new LevelOfCareRtrvReq();
		levelOfCareRtrvReq.setIdCase(placementReq.getIdCase());
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonLoc.class)
				.add(Restrictions.eq("idPlocEvent", eventDto.getIdEvent()));
		PersonLoc personLoc = (PersonLoc) criteria.uniqueResult();
		levelOfCareRtrvReq.setIdPlocEvent(personLoc.getIdPlocEvent());
		levelOfCareRtrvReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		levelOfCareRtrvReq.setIdPerson(personLoc.getPerson().getIdPerson());
		levelOfCareRtrvReq.setIdStage(placementReq.getIdStage());
		levelOfCareRtrvReq.setIdPersUpdt(placementReq.getCommonDto().getIdUser());
		levelOfCareRtrvReq.setCdTask(PLOC_TASKCODE);
		levelOfCareRtrvReq.setCdEventType(null);
		if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
			levelOfCareRtrvReq.setEventDescr(placementReq.getBlocEventDesc());
			levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_BLOC);
			if(placementReq.getBlocEventDesc().contains(END)) {
				levelOfCareRtrvReq.setDtPlocEnd(placementReq.getDtPlcmtEnd());
			}else {
				levelOfCareRtrvReq.setDtPlocEnd(ServiceConstants.MIN_CONSTANTS_DATE);
			}
		}
		else {
			levelOfCareRtrvReq.setEventDescr(placementReq.getAlocEventDesc());
			levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_ALOC);
		}
		levelOfCareRtrvReq.setLastUpdate(new Date());
		return levelOfCareRtrvReq;
	}
}
