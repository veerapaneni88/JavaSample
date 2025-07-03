package us.tx.state.dfps.service.workload.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.casepackage.dto.CaseListDto;
import us.tx.state.dfps.service.casepackage.dto.PrincipalListDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.workload.dao.PrincipalCaseHistoryDao;

@Repository
public class PrincipalCaseHistoryDaoImpl implements PrincipalCaseHistoryDao {

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(PrincipalCaseHistoryDaoImpl.class);

	@Value("${PrincipalCaseHistoryDaoImpl.utcSQLQuery}")
	private String utcSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.finalSQLQuery}")
	private String FinalSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.prnSQLQuery}")
	private String prnSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.cpsSQLQuery}")
	private String cpsSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.apsSQLQuery}")
	private String apsSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.afcSQLQuery}")
	private String afcSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.licSQLQuery}")
	private String licSQL;

	@Value("${PrincipalCaseHistoryDaoImpl.ftlProcedureCall}")
	private String callProcedureFTL;

	public PrincipalCaseHistoryDaoImpl() {

	}

	/**
	 * Method Description: This method will get the unique Case IDs. Also get
	 * the relvant Case List information for each Case Id from the following
	 * table. CAPS_CASE, STAGE, STAGE_PERSON_LINK, CASE_LINK, CASE_,MERGE,
	 * CPS_INVST_DETAIL, PERSON table. caseList() will populate Case List
	 * Section on the PrincipalCaseHistory page.
	 *
	 * @param caseID
	 * @return List<CaseListDto>
	 */
	@Override
	public List<CaseListDto> caseList(long caseID) {
		List<CaseListDto> caseListDtos = new ArrayList<CaseListDto>();
		Query caseListQuery = sessionFactory.getCurrentSession().createSQLQuery(FinalSQL)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("program", StandardBasicTypes.STRING)
				.addScalar("caseSensitive", StandardBasicTypes.STRING)
				.addScalar("dtCaseClosed", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCaseOpened", StandardBasicTypes.TIMESTAMP)
				.addScalar("caseName", StandardBasicTypes.STRING).addScalar("stage", StandardBasicTypes.STRING)
				.addScalar("deleteCase", StandardBasicTypes.LONG).addScalar("caseLink", StandardBasicTypes.STRING)
				.addScalar("dtLinked", StandardBasicTypes.TIMESTAMP).addScalar("linkedCase", StandardBasicTypes.LONG)
				.addScalar("personUpdate", StandardBasicTypes.LONG).addScalar("personName", StandardBasicTypes.STRING)
				.addScalar("idCaseMerge", StandardBasicTypes.LONG).setParameter("idCase1", caseID)
				.setParameter("idCase2", caseID).setParameter("idCase3", caseID)
				.setResultTransformer(Transformers.aliasToBean(CaseListDto.class));
		caseListDtos = caseListQuery.list();
		if (!ObjectUtils.isEmpty(caseListDtos)) {
			caseListDtos.stream().forEach(caseListDto -> {
				// Second Query utcSQL - executes CPS_INVST_DETAIL table.
				// The UTC column will display a flag for a case if that case
				// contains
				// a stage that has an overall disposition of UTC or MOV. Also
				// it
				// will execute for all the unique Case IDs.
				Query utcQuery = sessionFactory.getCurrentSession().createSQLQuery(utcSQL).setParameter("idCase",
						caseListDto.getIdCase());
				BigDecimal utcCaseId = (BigDecimal) utcQuery.uniqueResult();
				if (!ObjectUtils.isEmpty(utcCaseId))
					caseListDto.setIdUtcCase(utcCaseId.longValue());
				// Setting the booleans and the status based on the query
				// results
				if (!ObjectUtils.isEmpty(caseListDto.getCaseLink())
						&& caseListDto.getCaseLink().equals(ServiceConstants.YES))
					caseListDto.setIndCaseLink(true);
				if (!ObjectUtils.isEmpty(caseListDto.getCaseSensitive())
						&& caseListDto.getCaseSensitive().equals(ServiceConstants.YES))
					caseListDto.setIndCaseSensitive(true);
				if (!ObjectUtils.isEmpty(caseListDto.getChildFatality())
						&& caseListDto.getChildFatality().equals(ServiceConstants.YES))
					caseListDto.setIndChildFatality(true);
				if (!ObjectUtils.isEmpty(caseListDto.getIdCaseMerge()) && caseListDto.getIdCaseMerge() > 0)
					caseListDto.setIndMrg(true);
				if (!ObjectUtils.isEmpty(caseListDto.getDeleteCase()) && caseListDto.getDeleteCase() > 0)
					caseListDto.setIndPrgElg(true);
				if (!ObjectUtils.isEmpty(caseListDto.getIdUtcCase()) && caseListDto.getIdUtcCase() > 0)
					caseListDto.setIndUTC(true);
				if (!ObjectUtils.isEmpty(caseListDto.getDtCaseClosed())) {
					caseListDto.setStatus(ServiceConstants.STATUS_CLOSED);
				} else {
					caseListDto.setStatus(ServiceConstants.STATUS_OPEN);
				}

			});
			Integer location = 0;
			for (CaseListDto caseListDto : caseListDtos) {
				// Case List, If yes then set the fatality attribute of the
				// CaseListDto
				if (isCaseHaveChildFatality(caseListDto.getIdCase())) {
					caseListDto.setChildFatality(ServiceConstants.Y);
				}
				caseListDto.setSelectedCase(location.toString());
				location++;
			}
		}
		return caseListDtos;
	}

	/**
	 * Method Description: This method will be called when the radio button for
	 * a case is selected on the PrincipalCaseHistory page. Also the Principal
	 * List section will include the Stage Id, Stage Type and Overall
	 * Disposition for the INV stage and all of the principals in the stage. For
	 * each principal, the Name, Person ID, Age, DOB, Gender, Role, and Rel/Int
	 * will be displayed. The Principal List section will be sorted by Stage ID
	 * descending, and then by ID Person ascending order.
	 *
	 * @param caseID
	 *            the case ID
	 * @param globalCaseID
	 *            the global case ID
	 * @return List<PrincipalListDto> @ the service exception
	 */
	@Override
	public List<PrincipalListDto> selectPrincipalList(long idCase, long idGlobalCase) {
		List<PrincipalListDto> principalListDtos = new ArrayList<PrincipalListDto>();
		Query principalCaseListQuery = sessionFactory.getCurrentSession().createSQLQuery(prnSQL)
				.addScalar(ServiceConstants.ID_PERSON_CONST, StandardBasicTypes.LONG)
				.addScalar("relation", StandardBasicTypes.STRING).addScalar("role", StandardBasicTypes.STRING)
				.addScalar(ServiceConstants.idStage, StandardBasicTypes.LONG)
				.addScalar(ServiceConstants.NMSTAGE, StandardBasicTypes.STRING)
				.addScalar("personName", StandardBasicTypes.STRING).addScalar("dtOfBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("gender", StandardBasicTypes.STRING).addScalar("allegType", StandardBasicTypes.STRING)
				.addScalar("allegDisposition", StandardBasicTypes.STRING)
				.addScalar("allegSeverity", StandardBasicTypes.STRING)
				.addScalar("dtOfDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("reasonForDeath", StandardBasicTypes.STRING)
				.addScalar(ServiceConstants.CDSTAGEPROGRAM, StandardBasicTypes.STRING)
				.setParameter(ServiceConstants.IDCASE, idCase)
				.setResultTransformer(Transformers.aliasToBean(PrincipalListDto.class));
		log.info("principalCaseListQuery: " + principalCaseListQuery);
		principalListDtos = principalCaseListQuery.list();
		if (!ObjectUtils.isEmpty(principalListDtos)) {
			principalListDtos.stream().forEach(principalListDto -> {
				String sqlQuery = null;
				// overall disposition Indicator.
				if (!ObjectUtils.isEmpty(principalListDto.getNmStage())
						&& principalListDto.getNmStage().equals(ServiceConstants.CSTAGES_INV)
						&& !ObjectUtils.isEmpty(principalListDto.getCdStageProgram())) {
					if ((ServiceConstants.CSRPGTYP_CPS).equals(principalListDto.getCdStageProgram())) {
						sqlQuery = cpsSQL;
					}
					if ((ServiceConstants.APS_PROGRAM).equals(principalListDto.getCdStageProgram())) {
						sqlQuery = apsSQL;
					}
					if ((ServiceConstants.CPGRMS_AFC).equals(principalListDto.getCdStageProgram())) {
						sqlQuery = afcSQL;
					}
					if ((ServiceConstants.CCL).equals(principalListDto.getCdStageProgram())
							|| (ServiceConstants.CAPS_PROG_RCL).equals(principalListDto.getCdStageProgram())) {
						sqlQuery = licSQL;
					}
					Query overallDispQuery = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
							.setParameter(ServiceConstants.idStage, principalListDto.getIdStage());
					log.info("overallDispQuery: " + overallDispQuery);
					List<String> ovrDispositions = overallDispQuery.list();
					if (!ObjectUtils.isEmpty(ovrDispositions)) {
						// In the loop get the Over all disposition Indicator
						// and set it to
						// PrincipalListDto.
						ovrDispositions.stream().forEach(ovrDisposition -> {
							principalListDto.setOvrDisposition(ovrDisposition);
						});
					}
				}
				if(!ObjectUtils.isEmpty(principalListDto.getDtOfBirth())){
					principalListDto.setAge(DateUtils.getAge(principalListDto.getDtOfBirth()));
				}
			});
		}
		return principalListDtos;
	}

	/**
	 * Method Description: This method will insert Parent Child relationship
	 * Checked Linked case information into CASE_LINK Table. EJB Name:
	 * PrincipalCaseHistoryBean
	 *
	 * @param idUser
	 * @param idCase
	 * @param idLinkCase
	 * @param indicator
	 * @return void
	 */
	public void insertCaseInfo(Long idUser, Long idCase, Long idLinkCase, String indicator) {
		CaseLink caseLink = new CaseLink();
		Person person = null;
		Long personId = idUser;
		if (!ObjectUtils.isEmpty(personId)) {
			person = (Person) sessionFactory.getCurrentSession().get(Person.class, (personId));
			caseLink.setPerson(person);
		}
		CapsCase mainCapsCase = null;
		Long mainCaseId = idCase;
		if (!ObjectUtils.isEmpty(mainCaseId)) {
			mainCapsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, Long.valueOf(mainCaseId));
		}
		CapsCase linkCapsCase = null;
		Long linkCaseId = idLinkCase;
		if (!ObjectUtils.isEmpty(linkCaseId)) {
			linkCapsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, Long.valueOf(linkCaseId));
		}
		caseLink.setCapsCaseByIdCase(mainCapsCase);
		caseLink.setCapsCaseByIdLinkedCase(linkCapsCase);
		caseLink.setDtLastUpdate(new Date());
		caseLink.setDtCaseLinked(new Date());
		caseLink.setIndCaseLink(indicator);
		sessionFactory.getCurrentSession().save("CaseLink", caseLink);
	}

	/**
	 * Method Description: This method will update Parent to Child Information.
	 * This method updates the Unchecked Linked Case Information into the Case
	 * Link. Users with the Merger Case security attribute will be able to
	 * update linking even if the case is closed and not in their chain of
	 * command. All other users will only be able to use it if the case is
	 * open.EJB Name: PrincipalCaseHistoryBean
	 *
	 * @param idUser
	 * @param idCase
	 * @param idLinkCase
	 * @param indicator
	 * @return void
	 */
	@Override
	public void updateCaseInfo(Long idUser, Long idCase, Long idLinkCase, String indicator) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaseLink.class);
		Long mainCaseId = idCase;
		if (!ObjectUtils.isEmpty(mainCaseId)) {
			CapsCase mainCapsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					Long.valueOf(mainCaseId));
			criteria.add(Restrictions.eq("capsCaseByIdCase", mainCapsCase));
		}
		Long linkCaseId = idLinkCase;
		if (!ObjectUtils.isEmpty(linkCaseId)) {
			CapsCase linkCapsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					Long.valueOf(linkCaseId));
			criteria.add(Restrictions.eq("capsCaseByIdLinkedCase", linkCapsCase));
		}
		List<CaseLink> caseLinks = (List<CaseLink>) criteria.list();
		if (!ObjectUtils.isEmpty(caseLinks)) {
			Person person = null;
			Long personId = idUser;
			if (!ObjectUtils.isEmpty(personId)) {
				person = (Person) sessionFactory.getCurrentSession().get(Person.class, Long.valueOf(personId));
			}
			for (CaseLink caseLink : caseLinks) {
				caseLink.setPerson(person);
				caseLink.setDtLastUpdate(new Date());
				caseLink.setDtCaseLinked(new Date());
				caseLink.setIndCaseLink(indicator);
				sessionFactory.getCurrentSession().update("CaseLink", caseLink);
			}
		}
	}

	/**
	 * Checks if is case have child fatality.
	 *
	 * @param idCase
	 * @return true, if is case have child fatality @ the service exception
	 */
	private boolean isCaseHaveChildFatality(long idCase) {
		String fatalitylValue = "";
		int errorCode = 0;
		String errorMessage = null;
		sessionFactory.getCurrentSession().createSQLQuery(callProcedureFTL).setParameter("idCase", idCase)
				.setParameter("fatalitylValue", fatalitylValue).setParameter("cdError", errorCode)
				.setParameter("errorMessage", errorMessage);
		return ServiceConstants.Y.equalsIgnoreCase(fatalitylValue) ? true : false;
	}
}
