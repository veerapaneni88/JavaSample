package us.tx.state.dfps.service.prt.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDataFetchDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Data
 * Access Object handles PRT Action Plan and FollowUp Oct 31, 2017- 5:57:27 PM ©
 * 2017 Texas Department of Family and Protective Services.
 */
@Repository
public class PRTActionPlanDataFetchDaoImpl implements PRTActionPlanDataFetchDao {

	/** The select PC for stages with PMC legal status sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.selectPCForStagesWithPMCLegalStatusSql}")
	private String selectPCForStagesWithPMCLegalStatusSql;

	/** The fetch open action plansql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchOpenActionPlansql}")
	private String fetchOpenActionPlansql;

	/** The fetch and populate PR unit using id unit sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchAndPopulatePRUnitUsingIdUnitSql}")
	private String fetchAndPopulatePRUnitUsingIdUnitSql;

	/** The fetch connections for stage sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchConnectionsForStageSql}")
	private String fetchConnectionsForStageSql;

	/** The fetch action planfor person sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchActionPlanforPersonSql}")
	private String fetchActionPlanforPersonSql;

	/** The select and populate latest placement sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.selectAndPopulateLatestPlacementSql}")
	private String selectAndPopulateLatestPlacementSql;

	/** The fetch and populate latest child plans sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchAndPopulateLatestChildPlansSql}")
	private String fetchAndPopulateLatestChildPlansSql;

	/** The fetch and populate PR unit sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchAndPopulatePRUnitSql}")
	private String fetchAndPopulatePRUnitSql;

	/** The select and populate plcmt using id plcmt sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.selectAndPopulatePlcmtUsingIdPlcmtSql}")
	private String selectAndPopulatePlcmtUsingIdPlcmtSql;

	/** The fetch latest legal status sql. */
	@Value("${PRTActionPlanDataFetchDaoImpl.fetchLatestLegalStatusSql}")
	private String fetchLatestLegalStatusSql;

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/**
	 * Method Name: selectPCForStagesWithPMCLegalStatus Method Description:This
	 * method fetches Primary Children for Stages with PMC Legal Status.
	 *
	 * @param stageIdList
	 *            the stage id list
	 * @return List<PRTPersonLinkValueDto> @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPersonLinkDto> selectPCForStagesWithPMCLegalStatus(List<Long> stageIdList) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectPCForStagesWithPMCLegalStatusSql).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("dtBirth", StandardBasicTypes.DATE)
				.addScalar("idChildSUBStage", StandardBasicTypes.LONG).setParameterList("idStage", stageIdList)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));
		List<PRTPersonLinkDto> prtPersonLinkValueDtoList = query.list();
		if (TypeConvUtil.isNullOrEmpty(prtPersonLinkValueDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("prtPersonLinkValueDtoList.not.found", null, Locale.US));
		}

		return prtPersonLinkValueDtoList;
	}

	/**
	 * Method Name: fetchOpenActionPlan Method Description:This method gets Open
	 * Action Plan Id for the given Person.
	 *
	 * @param idPerson
	 *            the id person
	 * @return Long @ the data not found exception
	 */
	@Override
	public Long fetchOpenActionPlan(long idPerson) {
		Long idActionPlan = null;
		;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchOpenActionPlansql)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).setParameter("pERSLinkIdPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));
		PRTPersonLinkDto prtPersonLinkValueDto = (PRTPersonLinkDto) query.uniqueResult();
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto)) {
			idActionPlan = prtPersonLinkValueDto.getIdPrtActionPlan();
		}

		return !ObjectUtils.isEmpty(idActionPlan) ? idActionPlan : ServiceConstants.ZERO_VAL;
	}

	/**
	 * This method fetches Unit Number / Region from Unit Table.
	 *
	 * @param prtActionPlanDto
	 *            the prt action plan dto
	 * @return the long @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long fetchAndPopulatePRUnitUsingIdUnit(PRTActionPlanDto prtActionPlanDto) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchAndPopulatePRUnitUsingIdUnitSql).addScalar("unitWorker", StandardBasicTypes.STRING)
				.addScalar("cdRegionWorker", StandardBasicTypes.STRING)
				.setParameter("idUnitWorker", prtActionPlanDto.getIdUnitWorker())
				.setResultTransformer(Transformers.aliasToBean(PRTActionPlanDto.class));

		List<PRTActionPlanDto> actionPlanDtos = query.list();
		if (TypeConvUtil.isNullOrEmpty(actionPlanDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("PRTActionPlanDto.not.found", null, Locale.US));
		}

		for (PRTActionPlanDto prtplanDto : actionPlanDtos) {

			String nbrUnit = prtplanDto.getUnitWorker();
			String cdRegion = prtplanDto.getCdRegionWorker();

			prtActionPlanDto.setUnitWorker(nbrUnit);
			prtActionPlanDto.setCdRegionWorker(cdRegion);
		}
		return (long) actionPlanDtos.size();
		// return (PRTActionPlanDto) actionPlanDtos;

	}

	/**
	 * Method Name: fetchConnectionsForStage Method Description:This method
	 * fetches Connections for the given Stage.
	 *
	 * @param idChildSUBStage
	 *            the id child SUB stage
	 * @return List<PRTConnectionDto> @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTConnectionDto> fetchConnectionsForStage(long idChildSUBStage) {
		List<PRTConnectionDto> prtConnections = new ArrayList<PRTConnectionDto>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchConnectionsForStageSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("relIntDecode", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING);
		if (ObjectUtils.isEmpty(idChildSUBStage)) {
			query.setParameter("idStage", 0);
		} else {
			query.setParameter("idStage", idChildSUBStage);
		}

		query.setResultTransformer(Transformers.aliasToBean(PRTConnectionDto.class));
		List<PRTConnectionDto> prtConnectionValueDto = query.list();
		if (!TypeConvUtil.isNullOrEmpty(prtConnectionValueDto)) {
			for (PRTConnectionDto prtConnectionDto : prtConnectionValueDto) {

				PRTConnectionDto connectionValueDto = new PRTConnectionDto();
				connectionValueDto.setIdPerson(prtConnectionDto.getIdPerson());
				connectionValueDto.setNmPersonFull(prtConnectionDto.getNmPersonFull());
				connectionValueDto.setConnRellong(prtConnectionDto.getCdStagePersRelInt());
				connectionValueDto.setConnRellongDecode(prtConnectionDto.getRelIntDecode());

				prtConnections.add(connectionValueDto);
			}
		}
		return prtConnections;
	}

	/**
	 * Method Name: fetchAndPopulateLatestChildPlans Method Description:This
	 * method fetches Latest Child Plan with Primary and Concurrent Goals for
	 * all the Children.
	 *
	 * @param stageIdList
	 *            the stage id list
	 * @param children
	 *            the children
	 * @return Long @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPersonLinkDto> fetchAndPopulateLatestChildPlans(List<Long> stageIdList,
			List<PRTPersonLinkDto> children) {

		children.forEach(child -> {
			if (child.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
				child.setIndNoConGoal(ServiceConstants.EMPTY_STRING);
				child.setPrtPermGoalValueDtoList(new ArrayList<>());
			}
		});

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchAndPopulateLatestChildPlansSql).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("childPlanType", StandardBasicTypes.STRING)
				.addScalar("idChildEventId", StandardBasicTypes.LONG)
				.addScalar("cdRcmndPrimaryGoal", StandardBasicTypes.STRING)
				.addScalar("indNoConGoal", StandardBasicTypes.STRING)
				.addScalar("cdRcmndConcurrentGoal", StandardBasicTypes.STRING)
				.setParameterList("idEventStage", stageIdList)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));
		List<PRTPersonLinkDto> prtPersonLinkValueDtoList = query.list();
		if (!CollectionUtils.isEmpty(prtPersonLinkValueDtoList)) {
			for (PRTPersonLinkDto prtPersonLinkDto : prtPersonLinkValueDtoList) {
				for (PRTPersonLinkDto personLinkValueDto : children) {

					if (personLinkValueDto.getIdPerson().equals(prtPersonLinkDto.getIdPerson()) && personLinkValueDto
							.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {

						personLinkValueDto.setChildPlanType(prtPersonLinkDto.getChildPlanType());
						personLinkValueDto.setIndNoConGoal(prtPersonLinkDto.getIndNoConGoal());
						personLinkValueDto.getPrtPermGoalValueDtoList().clear();

						PRTPermGoalDto prtPermGoalValueDto = new PRTPermGoalDto(CodesConstant.CPRMGLTY_10,
								prtPersonLinkDto.getCdRcmndPrimaryGoal(), prtPersonLinkDto.getIdPerson());
						prtPermGoalValueDto.setIdPrtPersonLink(personLinkValueDto.getIdPrtPersonLink());
						personLinkValueDto.getPrtPermGoalValueDtoList().add(prtPermGoalValueDto);

						if (StringUtil.isValid(prtPersonLinkDto.getCdRcmndConcurrentGoal())) {
							StringTokenizer tokens = new StringTokenizer(prtPersonLinkDto.getCdRcmndConcurrentGoal(),
									",");
							while (tokens.hasMoreTokens()) {
								String concurGoal = tokens.nextToken();
								prtPermGoalValueDto = new PRTPermGoalDto(ServiceConstants.CPRMGLTY_20, concurGoal,
										prtPersonLinkDto.getIdPerson());
								prtPermGoalValueDto.setIdPrtPersonLink(personLinkValueDto.getIdPrtPersonLink());
								personLinkValueDto.getPrtPermGoalValueDtoList().add(prtPermGoalValueDto);
							}
						}

					}

				}
			}
		}
		return children;

	}

	/**
	 * Method Name: selectAndPopulateLatestPlacement Method Description:This
	 * method fetches Latest Placement for all the Children.
	 *
	 * @param linkValueDtos
	 *            the link value dtos
	 * @return the long @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long selectAndPopulateLatestPlacement(List<PRTPersonLinkDto> children) {

		List<Long> idList = new ArrayList<Long>();
		if (!CollectionUtils.isEmpty(children)) {
			idList = children.stream().map(PRTPersonLinkDto::getIdPerson).collect(Collectors.toList());
		}

		String sql = selectAndPopulateLatestPlacementSql;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPlcmtChild", StandardBasicTypes.LONG).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING).addScalar("nmPlacement", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtEffectivePerm", StandardBasicTypes.DATE).setParameterList("personIdList", idList)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));

		List<PRTPersonLinkDto> actionPlanDtosList = query.list();
		if (!CollectionUtils.isEmpty(actionPlanDtosList)) {
			actionPlanDtosList.forEach(placement -> {
				if (!CollectionUtils.isEmpty(children)) {
					PRTPersonLinkDto prtPersonLinkDto = children.stream()
							.filter(child -> placement.getIdPlcmtChild().equals(child.getIdPerson())
									&& child.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT)
							.findFirst().orElse(null);
					if (!ObjectUtils.isEmpty(prtPersonLinkDto)) {
						prtPersonLinkDto.setIdPlcmtEvent(placement.getIdPlcmtEvent());
						prtPersonLinkDto.setCdPlcmtType(placement.getCdPlcmtType());
						prtPersonLinkDto.setNmPlacement(placement.getNmPlacement());
						prtPersonLinkDto.setDtPlcmtEffectivePerm(placement.getDtPlcmtEffectivePerm());
					}
				}
			});
		}
		return (long) actionPlanDtosList.size();

	}

	/**
	 * This method creates in clause with the given List.
	 *
	 * @param idList
	 *            the id list
	 * @return the string
	 * @returns In Clause.
	 */
	private String createInClause(List<Long> idList) {
		StringBuffer inClauseStr = new StringBuffer();

		inClauseStr.append("(");

		for (int index = 0; index < idList.size(); index++) {
			inClauseStr.append(idList.get(index));
			if (index != idList.size() - 1) {
				inClauseStr.append(", ");
			}
		}
		inClauseStr.append(")");

		return inClauseStr.toString();
	}

	/**
	 * Method Name: fetchActionPlanforPerson Method Description:This method gets
	 * Action Plan for the Child.
	 *
	 * @param idPerson
	 *            the id person
	 * @param eventStatus
	 *            the event status
	 * @return Long @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long fetchActionPlanforPerson(long idPerson, String eventStatus) {

		Long idActionPlan = ServiceConstants.ZERO_VAL;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchActionPlanforPersonSql)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).setString("cdEventStatus", eventStatus)
				.setLong("pERSLinkIdPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));
		List<PRTPersonLinkDto> prtPersonLinkValueDtoList = (List<PRTPersonLinkDto>) query.list();
		if (CollectionUtils.isEmpty(prtPersonLinkValueDtoList)) {
			return 0l;
		}
		for (PRTPersonLinkDto prtPersonLinkDtoList : prtPersonLinkValueDtoList) {
			idActionPlan = prtPersonLinkDtoList.getIdPrtActionPlan();
		}
		return idActionPlan;
	}

	/**
	 * Method Name: fetchAndPopulatePRUnit Method Description:This method
	 * fetches Unit Number of the Primary Worker of the Stage.
	 *
	 * @param pRtActionPlanDto
	 *            the rt action plan dto
	 * @param idStage
	 *            the id stage
	 * @return Long @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long fetchAndPopulatePRUnit(PRTActionPlanDto pRtActionPlanDto, Long idStage) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchAndPopulatePRUnitSql)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("unitWorker", StandardBasicTypes.STRING).addScalar("idUnitWorker", StandardBasicTypes.LONG)
				.addScalar("cdRegionWorker", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PRTActionPlanDto.class));

		List<PRTActionPlanDto> actionPlanDtosList = query.list();
		for (PRTActionPlanDto prtplanDto : actionPlanDtosList) {

			String nbrUnit = prtplanDto.getUnitWorker();
			Long idUnit = prtplanDto.getIdUnitWorker();
			String cdRegion = prtplanDto.getCdRegionWorker();

			pRtActionPlanDto.setUnitWorker(nbrUnit);
			pRtActionPlanDto.setIdUnitWorker(idUnit);
			pRtActionPlanDto.setCdRegionWorker(cdRegion);
		}
		return (long) actionPlanDtosList.size();
	}

	/**
	 * This method fetches Placement for all the Children.
	 *
	 * @param children
	 *            the children
	 * @return the long @ the data not found exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long selectAndPopulatePlcmtUsingIdPlcmt(List<PRTPersonLinkDto> children) {

		List<Long> idList = new ArrayList<Long>();
		for (PRTPersonLinkDto child : children) {
			idList.add(child.getIdPlcmtEvent());
		}
		String sql = selectAndPopulatePlcmtUsingIdPlcmtSql.replaceAll("<ID_PLCMT_LIST>", createInClause(idList));
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPlcmtChild", StandardBasicTypes.LONG).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING).addScalar("nmPlacement", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtEffectivePerm", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));

		List<PRTPersonLinkDto> actionPlanDtosList = query.list();

		if (TypeConvUtil.isNullOrEmpty(actionPlanDtosList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("prtPersonLinkValueDtoList.not.found", null, Locale.US));
		}
		for (PRTPersonLinkDto prtplanDto : actionPlanDtosList) {
			Long idPlcmtEvent = prtplanDto.getIdPlcmtEvent();
			String cdPlcmtType = prtplanDto.getCdPlcmtType();
			Date dtPlcmtEffectivePerm = prtplanDto.getDtPlcmtEffectivePerm();
			String nmPlacement = prtplanDto.getNmPlacement();

			for (PRTPersonLinkDto personLinkValueDto : children) {

				if (personLinkValueDto.getIdPlcmtEvent().equals(idPlcmtEvent)) {

					personLinkValueDto.setCdPlcmtType(cdPlcmtType);
					personLinkValueDto.setNmPlacement(nmPlacement);
					personLinkValueDto.setDtPlcmtEffectivePerm(dtPlcmtEffectivePerm);

					break;
				}
			}
		}
		return (long) actionPlanDtosList.size();
	}

	/**
	 * Method Name: fetchLatestLegalStatus Method Description: This method gets
	 * latest Legal Status for the Child.
	 *
	 * @param idPerson
	 *            the id person
	 * @return String latestLegalStatus @ the data not found exception
	 */
	@Override
	public LegalStatusDto fetchLatestLegalStatus(Long idPerson) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchLatestLegalStatusSql)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDto.class));
		List<LegalStatusDto> legalStatusDtoList = query.list();
		LegalStatusDto legalStatusDto = legalStatusDtoList.get(0);

		return legalStatusDto;

	}
}
