package us.tx.state.dfps.service.populateform.daoImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.pcsphistoryform.dto.CareDetailDto;
import us.tx.state.dfps.pcsphistoryform.dto.CareNarrativeInfoDto;
import us.tx.state.dfps.pcsphistoryform.dto.PcspCaseInfoDto;
import us.tx.state.dfps.service.casepackage.dao.PcspAssessmentDao;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssmtValueDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPPersonDto;
import us.tx.state.dfps.service.casepackage.dto.PcspAsmntDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.populateform.dao.PcspHistoryFormDao;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:<Implementation class for PcspHistoryFormDao> Mar 29, 2018-
 * 12:17:00 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PcspHistoryFormDaoImpl implements PcspHistoryFormDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PcspAssessmentDao pcspAssessmentDao;

	@Value("${PcspHistoryFormDaoImpl.getCareNarrativeInfo}")
	private transient String getCareNarrativeInfosql;

	@Value("${PcspHistoryFormDaoImpl.getCareDetailInfo}")
	private transient String getCareDetailInfosql;

	@Value("${PcspHistoryFormDaoImpl.getPcspCaseInfo}")
	private transient String getPcspCaseInfosql;

	@Value("${CaseUtils.getStageDetails}")
	private transient String getStageDetails;

	@Value("${PcspHistoryFormDaoImpl.getPcspasmntDetails}")
	private transient String getpcspasmntdetailssql;

	@Value("${PcspHistoryFormDaoImpl.getAgeAtCompletion}")
	private transient String getageatcompletionsql;

	@Value("${PcspHistoryFormDaoImpl.getIdStageFromEvent}")
	private transient String getIdStageFromEventsql;

	public PcspHistoryFormDaoImpl() {

	}

	/**
	 * DAM Name: CINVD7D Method Description:get ID_EVENT from CARE FACTOR table
	 * to populate CARE Narrative
	 * 
	 * @param idCase
	 * @return List<CareNarrativeInfoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CareNarrativeInfoDto> getCareNarrativeInfo(Long idCase) {

		List<CareNarrativeInfoDto> careNarr = new ArrayList<CareNarrativeInfoDto>();
		careNarr = (List<CareNarrativeInfoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCareNarrativeInfosql).setParameter("idCase", idCase))
						.addScalar("careCdCareCategory", StandardBasicTypes.STRING)
						.addScalar("careCdCareFactor", StandardBasicTypes.STRING)
						.addScalar("careDtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idEventCare", StandardBasicTypes.LONG)
						.addScalar("careCdApsOutcomeAction", StandardBasicTypes.STRING)
						.addScalar("careCdApsOutcomeActnCateg", StandardBasicTypes.STRING)
						.addScalar("careDtApsOutcomeAction", StandardBasicTypes.DATE)
						.addScalar("careCdApsOutcomeResult", StandardBasicTypes.STRING)
						.addScalar("careDtApsOutcomeRecord", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(CareNarrativeInfoDto.class)).list();
		return careNarr;

	}

	/**
	 * DAM Name: CINVD6D Method Description:Retrieves CARE details Category,
	 * Sub-Category & Date for Problem, Action and OutCome.
	 * 
	 * @param idCase
	 * @return List<CareDetailDto>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CareDetailDto> getCareDetailInfo(Long idCase) {
		List<CareDetailDto> careDetail = new ArrayList<CareDetailDto>();
		careDetail = (List<CareDetailDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCareDetailInfosql).setParameter("idCase", idCase))
						.addScalar("cdApsCltFactorCateg", StandardBasicTypes.STRING)
						.addScalar("cdApsClientFactor", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("txtApsCltFactorCmnts", StandardBasicTypes.STRING)
						.addScalar("cdApsOutcomeActnCateg", StandardBasicTypes.STRING)
						.addScalar("cdApsOutcomeAction", StandardBasicTypes.STRING)
						.addScalar("dtApsOutcomeAction", StandardBasicTypes.DATE)
						.addScalar("txtApsOutcomeAction", StandardBasicTypes.STRING)
						.addScalar("cdApsOutcomeResult", StandardBasicTypes.STRING)
						.addScalar("dtApsOutcomeRecord", StandardBasicTypes.DATE)
						.addScalar("txtApsOutcomeResult", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(CareDetailDto.class)).list();
		return careDetail;
	}

	/**
	 * Method Description:Retrieves the Case Name,Case Number,Worker Name
	 * 
	 * @param idEvent
	 * @return EventDto
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EventDto getIdStageFromEvent(Long idEvent) {
		EventDto pcspCase = new EventDto();
		pcspCase = (EventDto) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIdStageFromEventsql)
				.setParameter("idEvent", idEvent)).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("cdEventStatus", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
						.addScalar("cdEventTask", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(EventDto.class)).uniqueResult();
		return pcspCase;
	}

	/**
	 *
	 * DAM Name: CINVD5D Method Description:Retrieves the Case Name,Case
	 * Number,Worker Name
	 * 
	 * @param idCase
	 * @return List<PcspCaseInfoDto>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PcspCaseInfoDto> getPcspCaseInfo(Long idCase) {
		List<PcspCaseInfoDto> pcspCase = new ArrayList<PcspCaseInfoDto>();
		pcspCase = (List<PcspCaseInfoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPcspCaseInfosql).setParameter("idCase", idCase))
						.addScalar("nmCase", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("nmNameFirst", StandardBasicTypes.STRING)
						.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
						.addScalar("nmNameLast", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PcspCaseInfoDto.class)).uniqueResult();
		return pcspCase;
	}

	/**
	 * DAM Name: CINVD5D Method Description:Retrieves the Case Name,Case
	 * Number,Worker Name
	 *
	 * @param idCase
	 * @return PcspCaseInfoDto
	 *
	 */
	@Override
	public List<PcspCaseInfoDto> getPcspCase(Long idCase) {
		return ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPcspCaseInfosql).setParameter("idCase", idCase))
				.addScalar("nmCase", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("nmNameLast", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PcspCaseInfoDto.class)).list();
	}

	/**
	 * DAM Name: getStageDetails Method Description:Retrieves stage details
	 * 
	 * @param idStage
	 * @return List<stageDto>
	 * 
	 */
	@Override
	public List<StageDto> getStageDetails(Long idStage) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDetails)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class));

		sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdStageType", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idUnit", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idSituation", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtStageClose", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageClassification", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indStageClose", StandardBasicTypes.STRING);
		sqlQuery.addScalar("nmStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtStageStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("nmCase", StandardBasicTypes.STRING);

		sqlQuery.setParameter("idStage", idStage);

		List<StageDto> liStage = sqlQuery.list();

		return liStage;
	}

	/**
	 * DAM Name: getPcspasmntDetails Method Description: This method retrieves
	 * person details
	 * 
	 * @param idStage
	 * @return List<stageDto>
	 * 
	 */
	@Override
	public PCSPAssessmentDto getPcspasmntDetails(PCSPAssessmentDto pcspAssessmentDto) {

		PcspAsmntDto pcspAsmntDto = new PcspAsmntDto();
		PCSPAssmtValueDto pcspAsmntValueDto = new PCSPAssmtValueDto();
		if (!ObjectUtils.isEmpty(pcspAssessmentDto.getIdPrsncrgvr())) {
			if (pcspAssessmentDao.getaddressExists(pcspAssessmentDto.getIdPrsncrgvr())
					&& pcspAssessmentDao.getphoneExists(pcspAssessmentDto.getIdPrsncrgvr())) {

				SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getpcspasmntdetailssql)
						.setResultTransformer(Transformers.aliasToBean(PcspAsmntDto.class));

				sqlQuery.addScalar("idPerson", StandardBasicTypes.LONG);
				sqlQuery.addScalar("nbrPersonPhone", StandardBasicTypes.STRING);
				sqlQuery.addScalar("addrStLn1", StandardBasicTypes.STRING);
				sqlQuery.addScalar("addrStLn2", StandardBasicTypes.STRING);
				sqlQuery.addScalar("addrCity", StandardBasicTypes.STRING);
				sqlQuery.addScalar("addrState", StandardBasicTypes.STRING);
				sqlQuery.addScalar("addrZip", StandardBasicTypes.STRING);

				sqlQuery.setParameter("idPcspAsmnt", pcspAssessmentDto.getIdPcspAsmnt());

				pcspAsmntDto = (PcspAsmntDto) sqlQuery.uniqueResult();

				// check if pcspAsmntDto is null, then append strings to one
				// Address string
				if (!ObjectUtils.isEmpty(pcspAsmntDto)) {
					StringBuilder sb = new StringBuilder();
					if (!ObjectUtils.isEmpty(pcspAsmntDto.getAddrStLn1())) {
						sb.append(pcspAsmntDto.getAddrStLn1());
					}
					if (!ObjectUtils.isEmpty(pcspAsmntDto.getAddrStLn2())) {
						sb.append(", ");
						sb.append(pcspAsmntDto.getAddrStLn2());
					}
					if (!ObjectUtils.isEmpty(pcspAsmntDto.getAddrCity())) {
						sb.append(", ");
						sb.append(pcspAsmntDto.getAddrCity());
					}
					if (!ObjectUtils.isEmpty(pcspAsmntDto.getAddrState())) {
						sb.append(", ");
						sb.append(pcspAsmntDto.getAddrState());
					}
					if (!ObjectUtils.isEmpty(pcspAsmntDto.getAddrZip())) {
						sb.append("  ");
						sb.append(pcspAsmntDto.getAddrZip());
					}
					pcspAsmntValueDto.setAddress(sb.toString());
					pcspAsmntValueDto.setPhone(pcspAsmntDto.getNbrPersonPhone());
					pcspAsmntValueDto.setIdPerson(pcspAsmntDto.getIdPerson());
					if (!ObjectUtils.isEmpty(pcspAssessmentDto.getPgAssessedList())) {
						/*
						 * only one care giver, then break;
						 */
						for (PCSPPersonDto dto : pcspAssessmentDto.getPgAssessedList()) {
							if (!ObjectUtils.isEmpty(pcspAsmntValueDto.getIdPerson())
									&& pcspAsmntValueDto.getIdPerson().equals(dto.getIdPerson())) {
								dto.setPhone(pcspAsmntValueDto.getPhone());
								dto.setAddress(pcspAsmntValueDto.getAddress());
								break;
							}
						}
					}
				}
			}

			/*
			 * Using the pcsp assessment data bean, returns the bean after
			 * retrieving data contemporary to the assessment completion date
			 * for age of PRNs.
			 */
			StringBuilder prn = new StringBuilder();
			if (!ObjectUtils.isEmpty(pcspAssessmentDto.getPgAssessedList())) {
				for (PCSPPersonDto dto : pcspAssessmentDto.getPgAssessedList()) {
					if (!ObjectUtils.isEmpty(dto.getIdPerson())) {
						prn.append(dto.getIdPerson());
						break;
					}
				}
			}
			/*
			 * if
			 * (!ObjectUtils.isEmpty(pcspAssessmentDto.getChildrenAssessedList()
			 * )) { for (PCSPPersonDto dto:
			 * pcspAssessmentDto.getChildrenAssessedList()) { if
			 * (pcspAssessmentDto.getChildrenAssessedList().contains(Integer.
			 * valueOf(dto.getIdPerson().intValue()))) { if (0 !=
			 * prn.toString().length()) { prn.append( "," ); } prn.append(
			 * dto.getIdPerson()); } } } if
			 * (!ObjectUtils.isEmpty(pcspAssessmentDto.getOhmAssessedList())) {
			 * for (PCSPPersonDto dto: pcspAssessmentDto.getOhmAssessedList()) {
			 * if (pcspAssessmentDto.getChildrenAssessedList().contains(new
			 * Integer(dto.getIdPerson().intValue()))) { if ( 0 !=
			 * prn.toString().length()) { prn.append( "," ); }
			 * prn.append(dto.getIdPerson()); } } }
			 */
			if (!ObjectUtils.isEmpty(prn.toString())) {
				PcspAsmntDto asmntDto = getAgeAtCompletion(pcspAssessmentDto, prn.toString());
				if (!ObjectUtils.isEmpty(asmntDto)) {
					PCSPAssmtValueDto valueBean = new PCSPAssmtValueDto();
					valueBean.setIdPerson(asmntDto.getIdPerson());
					valueBean.setDtPersonBirth(asmntDto.getDtPersonBirth());
					if (!ObjectUtils.isEmpty(pcspAssessmentDto.getPgAssessedList())) {
						for (PCSPPersonDto dto : pcspAssessmentDto.getPgAssessedList()) {
							if (!ObjectUtils.isEmpty(valueBean.getIdPerson())
									&& valueBean.getIdPerson().equals(dto.getIdPerson())) {
								dto.setDtPersonBirth(valueBean.getDtPersonBirth());
								break;
							}
						}
					}
					if (!ObjectUtils.isEmpty(pcspAssessmentDto.getChildrenAssessedList())) {
						for (PCSPPersonDto dto : pcspAssessmentDto.getChildrenAssessedList()) {
							if (!ObjectUtils.isEmpty(valueBean.getIdPerson())
									&& valueBean.getIdPerson().equals(dto.getIdPerson())) {
								dto.setDtPersonBirth(valueBean.getDtPersonBirth());
								break;
							}
						}
					}
					if (!ObjectUtils.isEmpty(pcspAssessmentDto.getOhmAssessedList())) {
						for (PCSPPersonDto dto : pcspAssessmentDto.getOhmAssessedList()) {
							if (!ObjectUtils.isEmpty(valueBean.getIdPerson())
									&& valueBean.getIdPerson().equals(dto.getIdPerson())) {
								dto.setDtPersonBirth(valueBean.getDtPersonBirth());
								break;
							}
						}
					}
				}
			}
		}
		return pcspAssessmentDto;
	}

	private PcspAsmntDto getAgeAtCompletion(PCSPAssessmentDto pcspAssessmentDto, String prn) {

		PcspAsmntDto pcspAsmntDto = new PcspAsmntDto();

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getageatcompletionsql.replace("PRNLIST", prn))
				.setResultTransformer(Transformers.aliasToBean(PcspAsmntDto.class));

		sqlQuery.addScalar("idHistPerson", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idPersonHistory", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtPersonBirth", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtPersonEffect", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtCMPLT", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtPersonEnd", StandardBasicTypes.DATE);

		sqlQuery.setParameter("idPcspAsmnt", pcspAssessmentDto.getIdPcspAsmnt());

		return pcspAsmntDto;
	}
}
