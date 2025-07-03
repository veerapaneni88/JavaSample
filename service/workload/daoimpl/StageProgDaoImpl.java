package us.tx.state.dfps.service.workload.daoimpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.SafetyArea;
import us.tx.state.dfps.common.domain.SafetyAssessment;
import us.tx.state.dfps.common.domain.SafetyFactors;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StageLink;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.StageProg;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.SafetyAssmtDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyFactorDto;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

@Repository
public class StageProgDaoImpl implements StageProgDao {

	@Value("${StageProgDaoImpl.queryPageData}")
	private String queryPageDatasql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	public StageProgDaoImpl() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StageProgDto> getStgProgroession(String cdStageProgStage, String cdStageProgProgram,
			String cdStageProgRsnClose) {
		List<StageProgDto> stageProgDtoList = new ArrayList<>();

		stageProgDtoList = sessionFactory.getCurrentSession().createCriteria(StageProg.class)
				.setProjection(Projections.projectionList().add(Projections.property("idStageProg"), "idStageProg")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("cdStageProgStage"), "cdStageProgStage")
						.add(Projections.property("cdStageProgRsnClose"), "cdStageProgRsnClose")
						.add(Projections.property("cdStageProgProgram"), "cdStageProgProgram")
						.add(Projections.property("indStageProgClose"), "indStageProgClose")
						.add(Projections.property("cdStageProgOpen"), "cdStageProgOpen")
						.add(Projections.property("cdStageProgEventType"), "cdStageProgEventType")
						.add(Projections.property("cdStageProgStageType"), "cdStageProgStageType")
						.add(Projections.property("cdStageProgStatus"), "cdStageProgStatus")
						.add(Projections.property("cdStageProgTask"), "cdStageProgTask")
						.add(Projections.property("cdStageProgTodoInfo"), "cdStageProgTodoInfo")
						.add(Projections.property("txtStageProgEvntDesc"), "stageProgEvntDesc")
						.add(Projections.property("txtStageProgTodoDesc"), "stageProgTodoDesc")
						.add(Projections.property("nbrStageProgDaysDue"), "stageProgDaysDue"))
				.add(Restrictions.eq("cdStageProgStage", cdStageProgStage))
				.add(Restrictions.eq("cdStageProgProgram", cdStageProgProgram))
				.add(Restrictions.eq("cdStageProgRsnClose", cdStageProgRsnClose))
				.setResultTransformer(Transformers.aliasToBean(StageProgDto.class)).list();

		return stageProgDtoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.workload.dao.StageProgDao#updateStagePersonLink(
	 * us.tx.state.dfps.service.common.request.CommonHelperReq)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateStagePersonLink(CommonHelperReq commonHelperReq) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", commonHelperReq.getIdStage()));
		criteria.add(Restrictions.eq("idPerson", commonHelperReq.getIdPerson()));

		List<StagePersonLink> stagePersonLinks = criteria.list();
		if (stagePersonLinks != null) {
			StagePersonLink stagePersonLink = stagePersonLinks.get(0);
			stagePersonLink.setCdStagePersRole(ServiceConstants.CROLES_PC);
			sessionFactory.getCurrentSession().saveOrUpdate(stagePersonLink);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.workload.dao.StageProgDao#linkPersonToNewStage(
	 * us.tx.state.dfps.service.common.request.CommonHelperReq)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void linkPersonToNewStage(CommonHelperReq commonHelperReq) {
		List personRoles = new ArrayList();
		Long idNewStage = 0L;
		personRoles.add(ServiceConstants.CROLEALL_SE);
		List<StagePersonValueDto> stagePersonList = selectStagePersonLink(commonHelperReq.getIdStage().intValue(),
				personRoles);
		if (commonHelperReq.getStageIDs() != null) {
			idNewStage = commonHelperReq.getStageIDs().get(0);
		}
		// Link all the records to new stage.
		for (int index = 0; index < stagePersonList.size(); index++) {
			StagePersonValueDto stagePersonLinkBean = (StagePersonValueDto) stagePersonList.get(index);
			// Set new Stage Id.
			stagePersonLinkBean.setIdStage(idNewStage);
			// Set Stage Person Link Date to Current Date.
			stagePersonLinkBean.setDtStagePersLink(new java.util.Date());
			// Insert into Stage Person Link
			insertIntoStagePersonLink(stagePersonLinkBean);
		}

	}

	/**
	 * This method retrieves a Record from STAGE_PERSON_LINK using StageId and
	 * Person Role.
	 * 
	 * @param idStage
	 * @param personRoles
	 * @return List<StagePersonValueDto> - Person information for the Stage.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonValueDto> selectStagePersonLink(int idFromStage, List<String> personRoles) {
		List<StagePersonLink> stagePersonLink = new ArrayList<StagePersonLink>();
		List<StagePersonValueDto> stagePersonValue = new ArrayList<StagePersonValueDto>();
		// fetch the columns
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

		if (personRoles != null && personRoles.size() > 0) {
			String[] personRoleStrObjects = new String[personRoles.size()];

			for (int index = 0; index < personRoles.size(); index++) {
				personRoleStrObjects[index] = new String(personRoles.get(index));
			}
			// where condition mapped.
			criteria.add(Restrictions.in("cdStagePersRole", personRoleStrObjects));
		}
		criteria.add(Restrictions.eq("idStage", (long) idFromStage));

		stagePersonLink = criteria.list();
		stagePersonValue = populateStagePersonValueDto(stagePersonLink);
		return stagePersonValue;
	}

	/**
	 * This Method populates stagepersonvaluedto from stagepersonlinkdto
	 * 
	 * @param stagePersonLink
	 * @return @
	 */
	private List<StagePersonValueDto> populateStagePersonValueDto(List<StagePersonLink> stagePersonLink) {
		List<StagePersonValueDto> stagePersonList = new ArrayList<StagePersonValueDto>();

		if (stagePersonLink != null && stagePersonLink.size() > 0)
			for (StagePersonLink stagePersonLinkDto : stagePersonLink) {

				StagePersonValueDto stagePersonLinkBean = new StagePersonValueDto();

				stagePersonLinkBean.setIdStagePersonLink(stagePersonLinkDto.getIdStagePersonLink());
				stagePersonLinkBean.setDtLastUpdate(new Timestamp(stagePersonLinkDto.getDtLastUpdate().getTime()));
				stagePersonLinkBean.setIdStage(stagePersonLinkDto.getIdStage());
				stagePersonLinkBean.setIdPerson(stagePersonLinkDto.getIdPerson());
				stagePersonLinkBean.setIdCase(stagePersonLinkDto.getIdCase());
				stagePersonLinkBean.setCdStagePersRole(stagePersonLinkDto.getCdStagePersRole());
				stagePersonLinkBean.setIndStagePersInLaw(stagePersonLinkDto.getIndStagePersInLaw());
				stagePersonLinkBean.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
				stagePersonLinkBean.setCdStagePersSearchInd(stagePersonLinkDto.getCdStagePersSearchInd());
				stagePersonLinkBean.setStagePersNotes(stagePersonLinkDto.getTxtStagePersNotes());
				stagePersonLinkBean.setDtStagePersLink(stagePersonLinkDto.getDtStagePersLink());
				stagePersonLinkBean.setCdStagePersRelLong(stagePersonLinkDto.getCdStagePersRelInt());
				stagePersonLinkBean.setIndStagePersReporter(stagePersonLinkDto.getIndStagePersReporter());
				stagePersonLinkBean.setIndStagePersPrSecAsgn(stagePersonLinkDto.getIndStagePersPrSecAsgn());
				stagePersonLinkBean.setIndStagePersEmpNew(stagePersonLinkDto.getIndStagePersEmpNew());
				stagePersonLinkBean.setCdStagePersLstSort(stagePersonLinkDto.getCdStagePersLstSort());
				// Production Defect#12702 - Set NMStage to 1 only the previous value is true
				stagePersonLinkBean.setIndNmStage((!ObjectUtils.isEmpty(stagePersonLinkDto.getIndNmStage()) && stagePersonLinkDto.getIndNmStage()
						&& !stagePersonLinkDto.getCdStagePersType().equals(ServiceConstants.STAFF_TYPE))
						? ServiceConstants.ONE_LONG : ServiceConstants.ZERO );
				stagePersonLinkBean.setIndKinPrCaregiver(stagePersonLinkDto.getIndKinPrCaregiver());
				stagePersonLinkBean.setIndCaringAdult(stagePersonLinkDto.getIndCaringAdult());
				stagePersonList.add(stagePersonLinkBean);

			}

		return stagePersonList;

	}

	/**
	 * Method Name: insertIntoStagePersonLink Method Description:This function
	 * inserts new Record into Stage_Person_Link table. It is used to copy
	 * Person Details from Old Stage to New Stage.
	 * 
	 * @param stagePersonLinkBean
	 * @return Long @
	 */
	public Long insertIntoStagePersonLink(StagePersonValueDto stagePersonLinkBean) {
		StagePersonLink stagePersonLink = new StagePersonLink();
		stagePersonLink.setIdStage(stagePersonLinkBean.getIdStage());
		stagePersonLink.setIdPerson(stagePersonLinkBean.getIdPerson());
		stagePersonLink.setCdStagePersRole(stagePersonLinkBean.getCdStagePersRole());
		stagePersonLink.setIndStagePersInLaw(stagePersonLinkBean.getIndStagePersInLaw());
		stagePersonLink.setCdStagePersType(stagePersonLinkBean.getCdStagePersType());
		stagePersonLink.setCdStagePersSearchInd(stagePersonLinkBean.getCdStagePersSearchInd());
		stagePersonLink.setTxtStagePersNotes(stagePersonLinkBean.getStagePersNotes());
		stagePersonLink.setDtStagePersLink(stagePersonLinkBean.getDtStagePersLink());
		stagePersonLink.setCdStagePersRelInt(stagePersonLinkBean.getCdStagePersRelLong());
		stagePersonLink.setIndStagePersReporter(stagePersonLinkBean.getIndStagePersReporter());
		stagePersonLink.setIndStagePersEmpNew(stagePersonLinkBean.getIndStagePersEmpNew());
		stagePersonLink.setIndNmStage(Long.valueOf(ServiceConstants.ONE).equals(stagePersonLinkBean.getIndNmStage()));
		stagePersonLink.setDtLastUpdate(new Date());

		Long idStagePersonLink = (Long) sessionFactory.getCurrentSession().save(stagePersonLink);
		
		return idStagePersonLink;

	}

	/**
	 * Method Name: linkPersonToNewStage Method Description:This method links
	 * Person (including Staff) to the new Stage by creating entry into Stage
	 * Person Link table. It first retrieves Person information of given Roles
	 * from Stage Person Link table of the Old Stage and then inserts them into
	 * new Stage.
	 * 
	 * @param idStage
	 * @param idNewStage
	 * @param personRole
	 * @ @
	 * 
	 */
	@Override
	public void linkPersonToNewStage(Long idStage, Long idNewStage, String personRole) {

		List<String> personRoles = new ArrayList<>();
		personRoles.add(personRole);

		List<StagePersonValueDto> stagePersonList = selectStagePersonLink(idStage.intValue(), personRoles);

		if (TypeConvUtil.isNullOrEmpty(stagePersonList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		for (int index = 0; index < stagePersonList.size(); index++) {
			StagePersonValueDto stagePersonValueDto = (StagePersonValueDto) stagePersonList.get(index);
			// Set new Stage Id.
			stagePersonValueDto.setIdStage(idNewStage);
			// Set Stage Person Link Date to Current Date.
			Date today = new Date();
			stagePersonValueDto.setDtStagePersLink(today);
			// Insert into Stage Person Link
			insertIntoStagePersonLink(stagePersonValueDto);
		}

	}

	/**
	 * Method Name: linkNonStaffPersonToNewStage Method Description:This method
	 * links Non-Staff Persons to the new Stage by creating entry into Stage
	 * Person Link table. It first retrieves Person information of given Roles
	 * from Stage Person Link table of the Old Stage and then inserts them into
	 * new Stage.
	 * 
	 * @param idStage
	 * @param idNewStage
	 * @return Long @
	 */
	public Long linkNonStaffPersonToNewStage(Long idStage, Long idNewStage) {
		List<StagePersonValueDto> stagePersonList = selectStagePersonLinkNonStaff(idStage);
		
		// Fix for defect 12674 - CPS - AR case not progressing to FBSS -Error 8888 on
		// approval
		stagePersonList = stagePersonList.parallelStream().filter(s -> !CodesConstant.CROLEALL_HP.equals(s.getCdStagePersRole())
				&& !CodesConstant.CPRSNALL_STF.equals(s.getCdStagePersType())).collect(Collectors.toList());
		
		
		// Link all the records to new stage.
		for (int index = 0; index < stagePersonList.size(); index++) {
			StagePersonValueDto personValueDto = (StagePersonValueDto) stagePersonList.get(index);
			
			// Set Stage Person Link Date to Current Date.
			personValueDto.setDtStagePersLink(new java.util.Date());
			personValueDto.setIdStage(idNewStage);
			personValueDto.setIndStagePersEmpNew(ServiceConstants.ONE);
			insertIntoStagePersonLink(personValueDto);
		}
		return idNewStage;

	}

	/**
	 * Method Name: selectStagePersonLinkNonStaff Method Description:This method
	 * retrieves a Record from STAGE_PERSON_LINK using StageId and Person Role.
	 * 
	 * @param idStage
	 * @return List<StagePersonValueDto>
	 */
	@SuppressWarnings("unchecked")
	private List<StagePersonValueDto> selectStagePersonLinkNonStaff(Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		List<StagePersonLink> list = criteria.list();
		List<StagePersonValueDto> personValueDtos = new ArrayList<StagePersonValueDto>();
		if (TypeConvUtil.isNullOrEmpty(list)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (StagePersonLink stagePersonLink : list) {
			StagePersonValueDto personValueDto = new StagePersonValueDto();
			personValueDto.setIdStagePersonLink(stagePersonLink.getIdStagePersonLink());
			personValueDto.setDtLastUpdate(new Timestamp(stagePersonLink.getDtLastUpdate().getTime()));
			personValueDto.setIdCase(stagePersonLink.getIdStage());
			personValueDto.setIdPerson(stagePersonLink.getIdPerson());
			personValueDto.setIdStage(stagePersonLink.getIdStage());
			personValueDto.setCdStagePersRole(stagePersonLink.getCdStagePersRole());
			personValueDto.setIndStagePersInLaw(stagePersonLink.getIndStagePersInLaw());
			personValueDto.setCdStagePersType(stagePersonLink.getCdStagePersType());
			personValueDto.setCdStagePersSearchInd(stagePersonLink.getCdStagePersSearchInd());
			personValueDto.setStagePersNotes(stagePersonLink.getTxtStagePersNotes());
			personValueDto.setDtStagePersLink(stagePersonLink.getDtStagePersLink());
			personValueDto.setCdStagePersRelLong(stagePersonLink.getCdStagePersRelInt());
			personValueDto.setIndStagePersReporter(stagePersonLink.getIndStagePersReporter());
			personValueDto.setIndStagePersPrSecAsgn(stagePersonLink.getIndStagePersPrSecAsgn());
			personValueDto.setIndStagePersEmpNew(stagePersonLink.getIndStagePersEmpNew());
			personValueDto.setCdStagePersLstSort(stagePersonLink.getCdStagePersLstSort());
			// Production Defect#12702 - Set NMStage to 1 only the previous value is true
			if (!ObjectUtils.isEmpty(stagePersonLink.getIndNmStage()) && stagePersonLink.getIndNmStage()
					&& !stagePersonLink.getCdStagePersType().equals(ServiceConstants.STAFF_TYPE))
				personValueDto.setIndNmStage(ServiceConstants.ONE_VAL);
			else
				personValueDto.setIndNmStage(ServiceConstants.ZERO_VAL);
			personValueDto.setIndKinPrCaregiver(stagePersonLink.getIndKinPrCaregiver());
			personValueDto.setIndCaringAdult(stagePersonLink.getIndCaringAdult());
			personValueDto.setIndNytdContact(
					stagePersonLink.getIndNytdContact() == null ? "" : stagePersonLink.getIndNytdContact().toString());
			personValueDto.setIndNytdContactPrimary(stagePersonLink.getIndNytdContactPrimary() == null ? ""
					: stagePersonLink.getIndNytdContactPrimary().toString());
			personValueDtos.add(personValueDto);
		}
		return personValueDtos;
	}

	/**
	 * 
	 * Method Name: isAnotherStageOpen Method Description: to find the given
	 * stage is open
	 * 
	 * @param stage
	 * @return Boolean @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean isAnotherStageOpen(Long idCase, String cdStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("cdStage", cdStage));
		criteria.add(Restrictions.eq("capsCase.idCase", idCase));
		//Defect 11004 - Add conditions to dtStageClose to pull the correct list
		Disjunction or = Restrictions.disjunction();
		or.add(Restrictions.eq("dtStageClose",DateUtils.stringToDate(ServiceConstants.MAX_JAVA_DATE_STRING)));
		or.add(Restrictions.isNull("dtStageClose"));
		criteria.add(or);
		List<Stage> list = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(list) && (!list.isEmpty())) {
			return ServiceConstants.TRUEVAL;
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Name: linkSecondaryWorkerToNewStage Method Description: * This
	 * method assigns Secondary Worker to a Stage. It first retrieves Primary
	 * Worker information from Stage Person Link table, substitutes Secondary
	 * Worker Id, Role in the record and inserts a new Record into Stage Person
	 * Link table.
	 * 
	 * @param idStage
	 * @param idSecWorker
	 * @return Long @
	 */
	@Override
	public Long linkSecondaryWorkerToNewStage(Long idStage, Long idSecWorker) {
		List<String> personRoles = new ArrayList<>();
		Long updatedResult;
		personRoles.add(ServiceConstants.STAGE_PERS_ROLE_PR);
		List<StagePersonValueDto> stagePersonList = selectStagePersonLink(idStage.intValue(), personRoles);
		StagePersonValueDto stagePersonLinkBean = stagePersonList.get(ServiceConstants.Zero);
		stagePersonLinkBean.setIdPerson(idSecWorker);
		stagePersonLinkBean.setCdStagePersRole(ServiceConstants.CROLEALL_SE);
		stagePersonLinkBean.setDtStagePersLink(new Date());

		updatedResult = insertIntoStagePersonLink(stagePersonLinkBean);

		return updatedResult;
	}

	/**
	 * This method batch updates StagePersonLink table using
	 * StagePersonValueBean list.
	 * 
	 * @param spLinkBeans
	 *            ArrayList of StagePersonValueBeans
	 * @return Integer new AllegationID
	 */
	public Integer updateStagePersonLinks(List<StagePersonValueDto> spLinkBeans) {
		if ((spLinkBeans == null) || ((spLinkBeans != null) && (spLinkBeans.size() == 0)))
			return null;

		int returnData = 0;

		//Fix for Production Defect 13049 - cannot progress AR-INV
		for (StagePersonValueDto stagePersonValueBean : spLinkBeans) {
			returnData += 1;
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
			criteria.add(Restrictions.eq("idStagePersonLink", stagePersonValueBean.getIdStagePersonLink()));
			StagePersonLink stage = (StagePersonLink) criteria.uniqueResult();
			stage.setCdStagePersType(stagePersonValueBean.getCdStagePersType());
			stage.setCdStagePersRole(stagePersonValueBean.getCdStagePersRole());
			stage.setCdStagePersRelInt(stagePersonValueBean.getCdStagePersRelLong());
			sessionFactory.getCurrentSession().update(stage);
		}

		return returnData;

	}

	/**
	 * Method Name: insertIntoStagePersonLinks Method Description:This method
	 * batch inserts StagePersonLink table using StagePersonValueBean list.
	 * 
	 * @param StagePersonValueDtoList
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public int insertIntoStagePersonLinks(List<StagePersonValueDto> stagePersonValueDtoList)
			throws DataNotFoundException {
		if ((stagePersonValueDtoList == null)
				|| ((stagePersonValueDtoList != null) && (stagePersonValueDtoList.size() == 0))) {
			return 0;
		}
		StagePersonLink stagePersonLink = new StagePersonLink();
		for (StagePersonValueDto stagePersonValueDtos : stagePersonValueDtoList) {
			stagePersonLink.setIdPerson(stagePersonValueDtos.getIdPerson());
			stagePersonLink.setCdStagePersRole(stagePersonValueDtos.getCdStagePersRole());
			stagePersonLink.setIndStagePersInLaw(stagePersonValueDtos.getIndStagePersInLaw());
			stagePersonLink.setCdStagePersType(stagePersonValueDtos.getCdStagePersType());
			stagePersonLink.setCdStagePersSearchInd(stagePersonValueDtos.getCdStagePersSearchInd());
			stagePersonLink.setTxtStagePersNotes(stagePersonValueDtos.getStagePersNotes());
			stagePersonLink.setDtStagePersLink(stagePersonValueDtos.getDtStagePersLink());
			stagePersonLink.setCdStagePersRelInt(stagePersonValueDtos.getCdStagePersRelLong());
			stagePersonLink.setIndStagePersReporter(stagePersonValueDtos.getIndStagePersReporter());
			stagePersonLink.setIndStagePersEmpNew(ServiceConstants.EMP_NEW);
			stagePersonLink.setIdStagePersonLink(new Long(ServiceConstants.ID_STAGE_VALUE).longValue());
			sessionFactory.getCurrentSession().saveOrUpdate(stagePersonLink);
		}
		return stagePersonValueDtoList.size();
	}

	/**
	 * Method Name: queryPageData Method Description:Retrieve the data needed to
	 * build the Safety Assessment page.
	 * 
	 * @param safetyAssmtDto
	 * @return SafetyAssmtDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SafetyAssmtDto queryPageData(SafetyAssmtDto safetyAssmtDto) {
		List<SafetyFactorDto> safetyFactorDtos = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryPageDatasql)).addScalar("cdSafetyArea", StandardBasicTypes.STRING)
						.addScalar("txtArea", StandardBasicTypes.STRING)
						.addScalar("cdSafetyCategory", StandardBasicTypes.STRING)
						.addScalar("txtCategory", StandardBasicTypes.STRING)
						.addScalar("cdSafetyFactor", StandardBasicTypes.STRING)
						.addScalar("txtFactors", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SafetyFactorDto.class)).list();

		SafetyAssmtDto returnBean = new SafetyAssmtDto();
		returnBean.setFactors(safetyFactorDtos);
		returnBean.setCaseId(safetyAssmtDto.getCaseId());
		returnBean.setStageId(safetyAssmtDto.getStageId());
		returnBean.setEventId(safetyAssmtDto.getEventId());
		return returnBean;
	}

	/**
	 * Method Name: getSubStageOpen Method Description:Returns true if SubStage
	 * is Open else returns false
	 * 
	 * @param safetyAssmtDto
	 * @return Boolean
	 * @throws DataNotFoundException
	 */

	@SuppressWarnings("unchecked")
	@Override
	public Boolean getSubStageOpen(SafetyAssmtDto safetyAssmtDto) throws DataNotFoundException {

		boolean result;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("cdStage", ServiceConstants.STAGE_CODE_SUB));
		criteria.add(Restrictions.isNull("dtStageClose"));
		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(safetyAssmtDto.getCaseId());
		criteria.add(Restrictions.eq("capsCase", capsCase));

		ProjectionList reqcolumns = Projections.projectionList();
		reqcolumns.add(Projections.property("cdStage"));
		criteria.setProjection(reqcolumns);
		List<String> stageList = criteria.list();

		if (stageList.get(0) != null) {

			result = true;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * Method Name: getTodoId Method Description:Returns back an integer
	 * containing Todo Id
	 * 
	 * @param eventId
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getTodoId(Long eventId) throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		Event event = new Event();
		event.setIdEvent((long) eventId);
		criteria.add(Restrictions.eq("event", event));
		ProjectionList reqcolumns = Projections.projectionList();
		reqcolumns.add(Projections.property("idTodo"));
		criteria.setProjection(reqcolumns);
		List<Long> todoList = criteria.list();
		return todoList.get(0);

	}

	/**
	 * MethodName : addSafetyAssmtDetails MethodDescription:this method Saves
	 * the new Safety Assessment
	 * 
	 * @param SafetyAssmtDto
	 * @return Long
	 */
	@Override
	public long addSafetyAssmtDetails(SafetyAssmtDto safetyAssmtDto) {

		SafetyAssessment safetyAssessment = new SafetyAssessment();

		Event event = new Event();
		event.setIdEvent((long) safetyAssmtDto.getEventId());
		safetyAssessment.setEvent(event);

		Stage stage = new Stage();
		stage.setIdStage(safetyAssmtDto.getStageId());
		safetyAssessment.setStage(stage);

		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(safetyAssmtDto.getCaseId());
		safetyAssessment.setCapsCase(capsCase);

		safetyAssessment.setCdSafetyDecision(safetyAssmtDto.getCdSafetyDecision());
		safetyAssessment.setIndAbuseNeglHistCompl(safetyAssmtDto.getIndAbuseNeglHistCompl());
		safetyAssessment.setTxtAbuseNeglHistNotCompl(safetyAssmtDto.getTxtAbuseNeglHistNotCompl());
		safetyAssessment.setIndInsufficientSafety(safetyAssmtDto.getIndInsufficientSafety());
		safetyAssessment.setTxtDecisionRationale(safetyAssmtDto.getTxtDecisionRationale());
		safetyAssessment.setIndChildPresentDanger(safetyAssmtDto.getIndChildPresentDanger());
		safetyAssessment.setNbrVersion((byte) ServiceConstants.VERSION_NUMBER);
		safetyAssessment.setDtLastUpdate(safetyAssmtDto.getDateLastUpdate());

		long result = (long) sessionFactory.getCurrentSession().save(safetyAssessment);
		return result;

	}

	// this method Saves the new Safety Assessment Area details to the database
	@Override
	public long addAreaDetails(SafetyFactorDto safetyFactorDB, SafetyAssmtDto safetyAssmtDto) {

		SafetyArea safetyArea = new SafetyArea();

		Event event = new Event();
		event.setIdEvent((long) safetyAssmtDto.getEventId());
		safetyArea.setEvent(event);

		Stage stage = new Stage();
		stage.setIdStage(safetyAssmtDto.getStageId());
		safetyArea.setStage(stage);

		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(safetyAssmtDto.getCaseId());
		safetyArea.setCapsCase(capsCase);

		safetyArea.setCdSafetyArea(safetyFactorDB.getCdSafetyArea());
		safetyArea.setTxtDiscussYFactors(safetyFactorDB.getDiscussyFactors());
		safetyArea.setDtLastUpdate(safetyAssmtDto.getDateLastUpdate());

		long result = (long) sessionFactory.getCurrentSession().save(safetyArea);
		return result;

	}

	/**
	 * MethoName: addFactorDetails MethodDescription:this method Saves the new
	 * Safety Assessment Factor details to the database
	 * 
	 * @param safetyFactorDB
	 * @param safetyAssmtDto
	 * @return long
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public long addFactorDetails(SafetyFactorDto safetyFactorDB, SafetyAssmtDto safetyAssmtDto) {

		HashMap areaIdMap = getSafetyAreaIds(safetyAssmtDto);
		areaIdMap.get(safetyFactorDB.getCdSafetyArea());

		SafetyFactors safetyFactors = new SafetyFactors();

		safetyFactors.setIdSafetyFactorArea((long) safetyFactorDB.getIdSafetyFactorArea());

		Event event = new Event();
		event.setIdEvent((long) safetyAssmtDto.getEventId());
		safetyFactors.setEvent(event);

		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(safetyAssmtDto.getCaseId());
		safetyFactors.setCapsCase(capsCase);

		Stage stage = new Stage();
		stage.setIdStage(safetyAssmtDto.getStageId());
		safetyFactors.setStage(stage);

		Person person = new Person();
		person.setIdPerson((long) safetyAssmtDto.getUlPersonId());
		safetyFactors.setPerson(person);

		safetyFactors.setCdSafetyFactor(safetyFactorDB.getCdSafetyArea());
		safetyFactors.setCdSafetyFactorCategory(safetyFactorDB.getCdSafetyCategory());
		safetyFactors.setCdSafetyFactorResponse(safetyFactorDB.getCdSafetyFactorResponse());
		safetyFactors.setDtLastUpdate(safetyAssmtDto.getDateLastUpdate());

		long result = (long) sessionFactory.getCurrentSession().save(safetyFactors);
		return result;

	}

	// this method Fetches back SafetyAreaIds for a given input.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap getSafetyAreaIds(SafetyAssmtDto safetyAssmtDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SafetyArea.class);

		Event event = new Event();
		event.setIdEvent((long) safetyAssmtDto.getEventId());
		criteria.add(Restrictions.eq("event.idEvent", event.getIdEvent()));

		Stage stage = new Stage();
		stage.setIdStage(safetyAssmtDto.getStageId());
		criteria.add(Restrictions.eq("stage.idStage", event.getStage()));

		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(safetyAssmtDto.getCaseId());
		criteria.add(Restrictions.eq("capsCase.idCase", capsCase.getIdCase()));

		ProjectionList reqcolumns = Projections.projectionList();
		reqcolumns.add(Projections.property("cdSafetyArea"));
		reqcolumns.add(Projections.property("idSafetyArea"));
		criteria.setProjection(reqcolumns);
		List<Long> safetyAreaList = criteria.list();
		return (HashMap) safetyAreaList;

	}

	/**
	 * 
	 * Method Name: updateINVSafetyAssignmentWithARSafetyAssignment Method
	 * Description:Update the safety Assessment indicator suggesting existence
	 * of a approved AR Safety assessment
	 * 
	 * @param idARSafetyAssessment
	 * @param idCase
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public Long updateINVSafetyAssignmentWithARSafetyAssignment(Long idARSafetyAssessment, Long idCase, Long idStage)
			throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SafetyAssessment.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.eq("capsCase.idCase", idCase));
		List<SafetyAssessment> safetyAssessmentList = criteria.list();
		for (SafetyAssessment safetyAssessment : safetyAssessmentList) {
			SafetyAssessment assessment = new SafetyAssessment();
			assessment.getArSafetyAssmt().setIdArSafetyAssmt(idARSafetyAssessment);
			sessionFactory.getCurrentSession().saveOrUpdate(assessment);
		}

		return (long) criteria.list().size();
	}

	/**
	 * Method Name: updateStageLink Method Description:This method batch updates
	 * StageLink table using StageValueBean. currently only updates case id ,
	 * can be used to update other fields
	 * 
	 * @param stageValueBeanDto
	 * @param newStageId
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateStageLink(StageValueBeanDto stageValueBeanDto, Long newStageId) throws DataNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StageLink.class);
		criteria.add(Restrictions.eq("idStage", newStageId));
		List<StageLink> stageLinks = criteria.list();
		for (StageLink stageLink : stageLinks) {
			stageLink.setIdCase(stageValueBeanDto.getIdCase());
			sessionFactory.getCurrentSession().saveOrUpdate(stageLink);
		}
	}
}
