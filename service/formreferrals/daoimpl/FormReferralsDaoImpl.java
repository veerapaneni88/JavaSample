package us.tx.state.dfps.service.formreferrals.daoimpl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Characteristics;
import us.tx.state.dfps.common.domain.CourtesyFormReferrals;
import us.tx.state.dfps.common.domain.CourtesyReferralIntrvw;
import us.tx.state.dfps.common.domain.DlgntSrchChildDtl;
import us.tx.state.dfps.common.domain.DlgntSrchDtl;
import us.tx.state.dfps.common.domain.DlgntSrchHdr;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FbssReferrals;
import us.tx.state.dfps.common.domain.FormsReferrals;
import us.tx.state.dfps.common.domain.QuickFind;
import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AudDiligentSearchReq;
import us.tx.state.dfps.service.common.request.FormReferralsReq;
import us.tx.state.dfps.service.common.response.AudDiligentSearchRes;
import us.tx.state.dfps.service.common.response.FormReferralsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.formreferrals.dao.FormReferralsDao;
import us.tx.state.dfps.service.formreferrals.dto.CaseWorkerDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.CourtesyFormReferlDto;
import us.tx.state.dfps.service.formreferrals.dto.CourtesyInterviewDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchChildDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchHdrDto;
import us.tx.state.dfps.service.formreferrals.dto.FbssReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.FormReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.OnLoadDlgntHdrDto;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindDto;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindPersonDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 28, 2017- 7:01:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FormReferralsDaoImpl implements FormReferralsDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	PostEventService postEventService;

	@Value("${FormReferralsDaoImpl.getFormList}")
	private String getFormsReferralSql;

	@Value("${FormReferralsDaoImpl.getIdCourtesyRefrl}")
	private String getIdCourtesyRefrlSql;

	@Value("${FormReferralsDaoImpl.getquickfindperson}")
	private String getquickfindpersonsql;

	@Value("${FormReferralsDaoImpl.getDiligentSearchHeader}")
	private String getDiligentSearchHeader;

	@Value("${FormReferralsDaoImpl.getDiligentSearchChildDetails}")
	private String getDiligentSearchChildDetails;

	@Value("${FormReferralsDaoImpl.getDiligentSearchDetail}")
	private String getDiligentSearchDetail;

	@Value("${FormReferralsDaoImpl.getPersonDtlByStageId}")
	private String getPersonDtlByStageId;

	@Value("${FormReferralsDaoImpl.getCaseWorkerDtl}")
	private String getCaseWorkerDtl;

	@Value("${FormReferralsDaoImpl.getSupervisorId}")
	private String getSupervisorId;

	@Value("${FormReferralsDaoImpl.getDlgntHdrByStageId}")
	private String getDlgntHdrByStageId;

	@Value("${FormDaoImpl.getCaseWorkerCounty}")
	private String getCaseWorkerCounty;

	@Value("${FormReferralsDaoImpl.delDiligentDtl}")
	private String delDiligentDtl;

	@Value("${FormReferralsDaoImpl.delDiligentChild}")
	private String delDiligentChild;

	@Value("${FormReferralsDaoImpl.delDiligentChildren}")
	private String delDiligentChildren;

	@Value("${FormReferralsDaoImpl.gethouseHoldDetails}")
	private String gethouseHoldDetails;

	@Value("${FormReferralsDaoImpl.validateSaveAndSubmit}")
	private String validateSaveAndSubmit;

	@Value("${FormReferralsDaoImpl.getFormReferralIdByApproval}")
	private String getFormsReferralIdByApproval;

	@Value("${FormReferralsDaoImpl.getFBSSReferral}")
	private String getFBSSReferralSql;

	@Value("${FormReferralsDaoImpl.searchFormReferralByIdEvent}")
	private String searchFormReferralByIdEvent;

	@Value("${FormReferralsDaoImpl.gethouseHoldDetailsByCpsSA}")
	private String gethouseHoldDetailsByCpsSA;

	@Value("${FormReferralsDaoImpl.getDiligentSearchHeaderId}")
	private String getDiligentSearchHeaderId;

	private static final Logger log = Logger.getLogger(FormReferralsDaoImpl.class);

	public FormReferralsDaoImpl() {

	}

	/**
	 * Method Name: formReferralsList Method Description: Method to retrieve all
	 * the form referrals from the forms_referral table.
	 * 
	 * @param formReq
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	public FormReferralsRes formReferralsList(FormReferralsReq formReq) {
		List<FormReferralsDto> formReferralsDto = new ArrayList<FormReferralsDto>();
		formReferralsDto = (List<FormReferralsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFormsReferralSql).setParameter("idStage", formReq.getFormReferralDto().getIdStage()))
						.addScalar("idFormsReferrals", StandardBasicTypes.LONG)
						.addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("dtEntered", StandardBasicTypes.TIMESTAMP)
						.addScalar("eventStatus", StandardBasicTypes.STRING)
						.addScalar("formType", StandardBasicTypes.STRING).addScalar("stage", StandardBasicTypes.STRING)
						.addScalar("nmStage", StandardBasicTypes.STRING)
						.addScalar("nmEnteredBy", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("cdTaskCode", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FormReferralsDto.class)).list();
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsRes.setFormReferralsList(formReferralsDto);
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;
	}

	/**
	 * Method Name: formReferralsDetail Method Description: Method to retrieve
	 * onerecord from FORMS_REFERRAL table.
	 * 
	 * @param formReq
	 * @
	 */
	@SuppressWarnings("unchecked")
	public FormReferralsRes formReferralsDetail(FormReferralsReq formReq) {
		Criteria formReferralList = sessionFactory.getCurrentSession().createCriteria(Characteristics.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idFormsReferrals"), "idFormsReferrals")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idCase"), "idCase").add(Projections.property("idStage"), "idStage")
						.add(Projections.property("idEvent"), "idEvent")
						.add(Projections.property("eventStatus"), "eventStatus")
						.add(Projections.property("cdCharCategory"), "cdCharCategory")
						.add(Projections.property("formType"), "formType").add(Projections.property("stage"), "stage")
						.add(Projections.property("nmStage"), "nmStage")
						.add(Projections.property("nmEnteredBy"), "nmEnteredBy")
						.add(Projections.property("dtEntered"), "dtEntered"))
				.add(Restrictions.eq("idCase", formReq.getIdCase()))
				.setResultTransformer(Transformers.aliasToBean(FormReferralsDto.class));
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsRes.setFormReferralsList(formReferralList.list());
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;
	}

	/**
	 * Method Name: formReferralsSave Method Description: Method to save a
	 * record in FORMS_REFFERALS table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public Long formReferralsSave(FormsReferrals formsReferrals) {
		sessionFactory.getCurrentSession().saveOrUpdate(formsReferrals);
		return formsReferrals.getIdFormsReferrals();
	}

	/**
	 * Method Name: formReferralsDelete Method Description: Method to delete a
	 * record in Forms_referral table.
	 * 
	 * @param formReq
	 * @
	 */
	public void formReferralsDelete(FormReferralsReq formReq) {
		FormsReferrals formsReferrals = new FormsReferrals();
		formsReferrals.setIdFormsReferrals(formReq.getIdFormReferral());
		formsReferrals = (FormsReferrals) sessionFactory.getCurrentSession().load(FormsReferrals.class,
				Long.valueOf(formReq.getIdFormReferral()));
		sessionFactory.getCurrentSession().delete(formsReferrals);
	}

	/**
	 * Method Name: getCourtesyReferralDetail Method Description: Method to get
	 * Courtesy referral Detail
	 * 
	 * @param formReq
	 * @
	 */
	public CourtesyFormReferlDto getCourtesyReferralDetail(Long idFormRefrl) {
		CourtesyFormReferlDto courtesyFormReferlDto = new CourtesyFormReferlDto();
		Long idCourtesyReferl = (Long) ((SQLQuery) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getIdCourtesyRefrlSql).setParameter("idFormRefrl", idFormRefrl))
						.addScalar("idCourtesyFormReferrals", StandardBasicTypes.LONG)).uniqueResult();
		CourtesyFormReferrals courtesyFormReferrals = (CourtesyFormReferrals) sessionFactory.getCurrentSession()
				.load(CourtesyFormReferrals.class, idCourtesyReferl);
		if (null != courtesyFormReferrals) {
			BeanUtils.copyProperties(courtesyFormReferrals, courtesyFormReferlDto);
			courtesyFormReferlDto.setDtLastUpdate(courtesyFormReferrals.getDtLastUpdate());
		}
		return courtesyFormReferlDto;
	}

	/**
	 * Method Name: getCourtesyReferralInterview Method Description: Method to
	 * retrieve list of interview information.
	 * 
	 * @param idFormRefrl
	 * @
	 */
	@SuppressWarnings("unchecked")
	public List<CourtesyInterviewDto> getCourtesyReferralInterview(Long idFormRefrl) {
		List<CourtesyInterviewDto> courtesyInterviewDto = null;
		Long idCourtesyReferl = (Long) ((SQLQuery) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getIdCourtesyRefrlSql).setParameter("idFormRefrl", idFormRefrl))
						.addScalar("idCourtesyFormReferrals", StandardBasicTypes.LONG)).uniqueResult();
		Criteria interviewList = sessionFactory.getCurrentSession().createCriteria(CourtesyReferralIntrvw.class)
				.createAlias("idCourtesyFormReferrals", "a")
				.setProjection(Projections.projectionList()
						.add(Projections.property("idCourtesyReferralIntrvw"), "idCourtesyReferralIntrvw")
						.add(Projections.property("phoneAltrnt"), "phoneAltrnt")
						.add(Projections.property("cdStagePersRelInt"), "cdStagePersRelInt")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("addrStLn2"), "addrStLn2")
						.add(Projections.property("dtCreated"), "dtCreated")
						.add(Projections.property("addrStLn1"), "addrStLn1")
						.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("addrCity"), "addrCity")
						.add(Projections.property("addrZip"), "addrZip")
						.add(Projections.property("infoNeeded"), "infoNeeded")
						.add(Projections.property("idPerson"), "idPerson").add(Projections.property("phone"), "phone"))
				.add(Restrictions.eq("a.idCourtesyFormReferrals", idCourtesyReferl))
				.setResultTransformer(Transformers.aliasToBean(CourtesyInterviewDto.class));
		courtesyInterviewDto = interviewList.list();
		return courtesyInterviewDto;
	}

	/**
	 * Method Name: saveCourtesyReferralDetail Method Description:Method to Save
	 * a record in Courtesy referral table.
	 * 
	 * @param formReq
	 * @
	 */
	public CourtesyFormReferrals saveCourtesyReferralDetail(CourtesyFormReferrals courtesyFormReferrals) {
		sessionFactory.getCurrentSession().saveOrUpdate(courtesyFormReferrals);
		return courtesyFormReferrals;
	}

	/**
	 * Method Name: getQuickFind Method Description:Method to retrieve record
	 * from quick_find table.
	 * 
	 * @param formReq
	 * @
	 */
	public FormsReferrals getFormReferrals(FormReferralsReq formReq) {
		long fromReferralsID = formReq.getIdFormReferral();
		FormsReferrals formsReferrals = null;
		if (!StringUtils.isEmpty(fromReferralsID)) {
			formsReferrals = (FormsReferrals) sessionFactory.getCurrentSession().load(FormsReferrals.class,
					fromReferralsID);
		}
		return formsReferrals;
	}

	/**
	 * Method Name: getFormReferralsByEvent Method Description:Method to retrieve record
	 * from forms_referrals table.
	 *
	 * @param formReq
	 * @
	 */
	public FormsReferrals getFormReferralsByEvent(FormReferralsReq formReq) {
		FormsReferrals formsReferral = null;

		Query queryFbssReferral = sessionFactory.getCurrentSession().createQuery(searchFormReferralByIdEvent);
		queryFbssReferral.setParameter("idEvent", formReq.getIdEvent());
		List<FormsReferrals> formsReferralLst = queryFbssReferral.list();

		if(null != formsReferralLst && formsReferralLst.size() > 0){
			return formsReferralLst.get(0);
		}

		return formsReferral;
	}

	/**
	 * Method Name: quickFindSave Method Description:Method to save record in
	 * quick_find table.
	 * 
	 * @param formReq
	 * @
	 */
	public FormsReferrals saveQuickFind(FormReferralsReq formReq) {
		QuickFindDto quickFindDto = formReq.getQuickFindList().get(0);
		FormReferralsDto formReferralDto = formReq.getFormReferralsList().get(0);
		QuickFind quickFindEntity = null;
		FormsReferrals formReferralsEntity = null;
		if (!StringUtils.isEmpty(quickFindDto.getIdQuickFind())) {
			quickFindEntity = (QuickFind) sessionFactory.getCurrentSession().get(QuickFind.class,
					quickFindDto.getIdQuickFind());
		}
		if (quickFindEntity != null && quickFindEntity.getIdQuickFind() != 0) {
			quickFindEntity.setAddrStLn1(quickFindDto.getCurrentAddr());
			quickFindEntity.setAddrStLn2(quickFindDto.getCurrentApt());
			quickFindEntity.setAddrCity(quickFindDto.getCurrentCity());
			quickFindEntity.setCdCnty(quickFindDto.getCurrentCounty());
			quickFindEntity.setNbrPhone(quickFindDto.getCurrentPhn());
			quickFindEntity.setCdState(quickFindDto.getCurrentState());
			quickFindEntity.setAddrZip(quickFindDto.getCurrentZip());
			quickFindEntity.setCdSbjctDob(quickFindDto.getDob());
			quickFindEntity.setIdCreatedPerson(quickFindDto.getIdCreatedPerson());
			quickFindEntity.setIdLastUpdatePerson(quickFindDto.getIdLastUpdatePerson());
			quickFindEntity.setIdSbjctPerson(quickFindDto.getIdPerson());
			quickFindEntity.setIndAps(quickFindDto.getIndAps());
			quickFindEntity.setIndCcl(quickFindDto.getIndCcl());
			quickFindEntity.setIndCps(quickFindDto.getIndCps());
			quickFindEntity.setIndAcctng(quickFindDto.getIndAccounting());
			quickFindEntity.setIndEmr(quickFindDto.getIndEmr());
			quickFindEntity.setIndLegal(quickFindDto.getIndLegal());
			quickFindEntity.setIndNytd(quickFindDto.getIndCpsNytd());
			quickFindEntity.setIndLegal(quickFindDto.getIndLegal());
			quickFindEntity.setIndQuickEnd(quickFindDto.getIndQuickEnd());
			quickFindEntity.setTxtLookingFor(quickFindDto.getLookingFor());
			quickFindEntity.setTxtOther(quickFindDto.getOther());
			quickFindEntity.setCdRqstrCnty(quickFindDto.getRequestorCounty());
			quickFindEntity.setNmRqstr(quickFindDto.getRequestorName());
			quickFindEntity.setCdRqstrRegion(quickFindDto.getRequestorRegion());
			quickFindEntity.setCdSbjctSsn(quickFindDto.getSsn());
			quickFindEntity.setTxtSbjctAlias(quickFindDto.getSubjectAliase());
			quickFindEntity.setNmSbjct(quickFindDto.getSubjectName());
			quickFindEntity.setTxtSbjctRel(quickFindDto.getSubjectRelationship());
			quickFindEntity.setIdCase(quickFindDto.getIdCase());
			quickFindEntity.setIndOther(quickFindDto.getIndOther());
			sessionFactory.getCurrentSession().saveOrUpdate(quickFindEntity);
			formReferralsEntity = quickFindEntity.getIdFormsReferrals();
			if (!StringUtils.isEmpty(formReferralDto.getIdEvent())
					&& formReferralDto.getEventStatus().equals(ServiceConstants.EVENT_STATUS_COMPLETE)) {
				Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class,
						formReferralDto.getIdEvent());
				if (eventEntity != null)
					eventEntity.setCdEventStatus(formReferralDto.getEventStatus());
			}
		} else {
			Long eventId = createAndReturnEventid(formReq, ServiceConstants.QUICKFIND_DESC,"");
			formReferralsEntity = new FormsReferrals();
			formReferralsEntity.setCdFormType(formReferralDto.getFormType());
			formReferralsEntity.setIdEvent(eventId);
			formReferralsEntity.setIdStage(formReferralDto.getIdStage());
			formReferralsEntity.setIdCreatedPerson(formReferralDto.getIdCreatedPerson());
			formReferralsEntity.setIdLastUpdatePerson(formReferralDto.getIdLastUpdatePerson());
			quickFindEntity = new QuickFind();
			quickFindEntity.setIndQuickEnd(quickFindDto.getIndQuickEnd());
			quickFindEntity.setTxtLookingFor(quickFindDto.getLookingFor());
			quickFindEntity.setTxtOther(quickFindDto.getOther());
			quickFindEntity.setAddrStLn1(quickFindDto.getCurrentAddr());
			quickFindEntity.setAddrStLn2(quickFindDto.getCurrentApt());
			quickFindEntity.setAddrCity(quickFindDto.getCurrentCity());
			quickFindEntity.setCdCnty(quickFindDto.getCurrentCounty());
			quickFindEntity.setNbrPhone(quickFindDto.getCurrentPhn());
			quickFindEntity.setCdState(quickFindDto.getCurrentState());
			quickFindEntity.setAddrZip(quickFindDto.getCurrentZip());
			quickFindEntity.setCdSbjctDob(quickFindDto.getDob());
			quickFindEntity.setIdSbjctPerson(quickFindDto.getIdPerson());
			quickFindEntity.setIndAps(quickFindDto.getIndAps());
			quickFindEntity.setIndCcl(quickFindDto.getIndCcl());
			quickFindEntity.setIndCps(quickFindDto.getIndCps());
			quickFindEntity.setIndAcctng(quickFindDto.getIndAccounting());
			quickFindEntity.setIdCreatedPerson(quickFindDto.getIdCreatedPerson());
			quickFindEntity.setIdLastUpdatePerson(quickFindDto.getIdLastUpdatePerson());
			quickFindEntity.setIndEmr(quickFindDto.getIndEmr());
			quickFindEntity.setIndLegal(quickFindDto.getIndLegal());
			quickFindEntity.setIndNytd(quickFindDto.getIndCpsNytd());
			quickFindEntity.setIndLegal(quickFindDto.getIndLegal());
			quickFindEntity.setIndQuickEnd(quickFindDto.getIndQuickEnd());
			quickFindEntity.setTxtLookingFor(quickFindDto.getLookingFor());
			quickFindEntity.setTxtOther(quickFindDto.getOther());
			quickFindEntity.setCdRqstrCnty(quickFindDto.getRequestorCounty());
			quickFindEntity.setNmRqstr(quickFindDto.getRequestorName());
			quickFindEntity.setCdRqstrRegion(quickFindDto.getRequestorRegion());
			quickFindEntity.setCdSbjctSsn(quickFindDto.getSsn());
			quickFindEntity.setTxtSbjctAlias(quickFindDto.getSubjectAliase());
			quickFindEntity.setNmSbjct(quickFindDto.getSubjectName());
			quickFindEntity.setTxtSbjctRel(quickFindDto.getSubjectRelationship());
			quickFindEntity.setIdCase(quickFindDto.getIdCase());
			quickFindEntity.setIndOther(quickFindDto.getIndOther());
			formReferralsEntity.getQuickFindCollection().add(quickFindEntity);
			quickFindEntity.setIdFormsReferrals(formReferralsEntity);
			sessionFactory.getCurrentSession().saveOrUpdate(formReferralsEntity);
			if (!StringUtils.isEmpty(formReferralsEntity.getIdFormsReferrals())) {
				formReferralsEntity = (FormsReferrals) sessionFactory.getCurrentSession().get(FormsReferrals.class,
						formReferralsEntity.getIdFormsReferrals());
			}
		}
		return formReferralsEntity;
	}

	/**
	 * Method Name: fbssSave Method Description:Method to save record in
	 * fbss_referrals table.
	 * 
	 * @param formReq
	 * @
	 */
	public FormsReferrals saveFBSS(FormReferralsReq formReq) {
		FbssReferralsDto fbssReferralsDto = formReq.getFbssReferralsList().get(0);
		FormReferralsDto formReferralsDto = formReq.getFormReferralsList().get(0);
		FbssReferrals fbssReferralsEntity = null;
		FormsReferrals formReferralsEntity = null;
		if (!StringUtils.isEmpty(fbssReferralsDto.getIdFbssReferrals())) {
			fbssReferralsEntity = (FbssReferrals) sessionFactory.getCurrentSession().load(FbssReferrals.class,
					fbssReferralsDto.getIdFbssReferrals());
		}
		if (fbssReferralsEntity != null && fbssReferralsEntity.getIdFbssReferrals() != 0) {
			return processSave(null,fbssReferralsEntity,fbssReferralsDto,formReferralsDto,false);
		} else {
			Long eventId = createAndReturnEventid(formReq, ServiceConstants.FBSS_DESC,fbssReferralsDto.getCdTask());
			formReferralsEntity = new FormsReferrals();
			formReferralsEntity.setCdFormType(formReferralsDto.getFormType());
			formReferralsEntity.setIdEvent(eventId);
			formReferralsEntity.setIdStage(formReferralsDto.getIdStage());
			formReferralsEntity.setIdCreatedPerson(formReferralsDto.getIdCreatedPerson());
			formReferralsEntity.setIdLastUpdatePerson(formReferralsDto.getIdLastUpdatePerson());
			fbssReferralsEntity = new FbssReferrals();
			return processSave(formReferralsEntity,fbssReferralsEntity,fbssReferralsDto,formReferralsDto,true);
		}

	}
	private FormsReferrals processSave(FormsReferrals formReferralsEntity,FbssReferrals fbssReferralsEntity,FbssReferralsDto fbssReferralsDto,FormReferralsDto formReferralsDto,boolean initialSave){
		fbssReferralsEntity.setNmCswrkr(fbssReferralsDto.getCaseWorker());
		fbssReferralsEntity.setNbrCswrkrPhone(fbssReferralsDto.getCaseWorkerPhn());
		fbssReferralsEntity.setTxtCntctFmly(fbssReferralsDto.getContactFamily());
		fbssReferralsEntity.setAddrStLn2(fbssReferralsDto.getAddrSt2());
		fbssReferralsEntity.setAddrCity(fbssReferralsDto.getAddrCity());
		fbssReferralsEntity.setAddrStLn1(fbssReferralsDto.getAddrSt1());
		fbssReferralsEntity.setAddrZip(fbssReferralsDto.getAddrZip());
		fbssReferralsEntity.setTxtPrfrdLang(fbssReferralsDto.getReferredLang());
		fbssReferralsEntity.setCdRfrngCnty(fbssReferralsDto.getReferringCnty());
		fbssReferralsEntity.setCdRfrngUnit(fbssReferralsDto.getReferringUnit());
		fbssReferralsEntity.setNmSuprvsr(fbssReferralsDto.getSupervisor());
		fbssReferralsEntity.setNbrSupvsrPhone(fbssReferralsDto.getSupervisorPhn());
		fbssReferralsEntity.setDtCourtHearng(fbssReferralsDto.getDtCourtHearing());
		fbssReferralsEntity.setDtRefrdToFbss(fbssReferralsDto.getDtReferred());
		fbssReferralsEntity.setIdFbssReferrals(fbssReferralsDto.getIdFbssReferrals());
		fbssReferralsEntity.setIndFmlyBarriers(fbssReferralsDto.getBarriersFamily());
		fbssReferralsEntity.setIndChildPcsp(fbssReferralsDto.getChildPcsp());
		fbssReferralsEntity.setIndCourtOrdr(fbssReferralsDto.getCourtOrder());
		fbssReferralsEntity.setIndFmlyAccptsFbss(fbssReferralsDto.getFamilyAccept());
		fbssReferralsEntity.setIndAbsntPrnt(fbssReferralsDto.getNonResident());
		fbssReferralsEntity.setIndPndngLeActns(fbssReferralsDto.getPendingLeAction());
		fbssReferralsEntity.setIndSpeclNeeds(fbssReferralsDto.getSpecialChildern());
		fbssReferralsEntity.setIndWrkrSfty(fbssReferralsDto.getWorkerSafety());
		fbssReferralsEntity.setTxtFmlyBarriers(fbssReferralsDto.getBarriersFamilyStr());
		fbssReferralsEntity.setTxtAdtnlCmnts(fbssReferralsDto.getTxtComments());
		fbssReferralsEntity.setTxtDngrs(fbssReferralsDto.getDangerIndicator());
		fbssReferralsEntity.setTxtFmlyAccptsFbss(fbssReferralsDto.getFamilyAcceptStr());
		fbssReferralsEntity.setTxtAbsntPrnt(fbssReferralsDto.getNonResidentStr());
		fbssReferralsEntity.setTxtPrntPrtctvActns(fbssReferralsDto.getParentProtective());
		fbssReferralsEntity.setTxtPrntDmnshdCapcty(fbssReferralsDto.getParentProtectiveTarget());
		fbssReferralsEntity.setTxtPndngLeActns(fbssReferralsDto.getPendingLeActionStr());
		fbssReferralsEntity.setTxtSpeclNeeds(fbssReferralsDto.getSpecialChildernStr());
		fbssReferralsEntity.setTxtWrkrSfty(fbssReferralsDto.getWorkerSafetyStr());
		fbssReferralsEntity.setIdLastUpdatePerson(fbssReferralsDto.getIdLastUpdatePerson());
		fbssReferralsEntity.setIdPerson(fbssReferralsDto.getIdPerson());
		//PPM#46797 artf150671 : Changes to save the new variables that are added as per the concurrent stage project
		fbssReferralsEntity.setIdCpsSA(fbssReferralsDto.getIdCpsSA());
		fbssReferralsEntity.setCourtReportUpld(fbssReferralsDto.getCourtReportUpld());
		fbssReferralsEntity.setFtmHeld(fbssReferralsDto.getFtmHeld());
		fbssReferralsEntity.setAutoAssign(fbssReferralsDto.getAutoAssign());
		//end
		if(initialSave){
			fbssReferralsEntity.setIdCreatedPerson(fbssReferralsDto.getIdCreatedPerson());
			formReferralsEntity.getFbssReferralsCollection().add(fbssReferralsEntity);
			fbssReferralsEntity.setIdFormsReferrals(formReferralsEntity);
			sessionFactory.getCurrentSession().saveOrUpdate(formReferralsEntity);
			if (!StringUtils.isEmpty(formReferralsEntity.getIdFormsReferrals())) {
				formReferralsEntity = (FormsReferrals) sessionFactory.getCurrentSession().get(FormsReferrals.class,
						formReferralsEntity.getIdFormsReferrals());
			}
		}else {
			fbssReferralsEntity.setIdFbssReferrals(fbssReferralsDto.getIdFbssReferrals());
			sessionFactory.getCurrentSession().saveOrUpdate(fbssReferralsEntity);
			formReferralsEntity = fbssReferralsEntity.getIdFormsReferrals();
			if (!StringUtils.isEmpty(formReferralsDto.getIdEvent())
					&& formReferralsDto.getEventStatus().equals(ServiceConstants.FORM_COMP)) {
				Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class,
						formReferralsDto.getIdEvent());
				if (eventEntity != null)
					eventEntity.setCdEventStatus(formReferralsDto.getEventStatus());
			}
		}
		return formReferralsEntity;
	}
	/**
	 * Method Name: quickFindDelete Method Description:Method to delete record
	 * from quick_find,fbss_referrals table.
	 * 
	 * @param formReq
	 * @
	 */
	@Override
	public String deleteQuickFind(FormReferralsReq formReq) {
		String message = null;
		FormReferralsDto formRefeDto = formReq.getFormReferralsList().get(0);
		if (!StringUtils.isEmpty(formRefeDto.getIdEvent())) {
			Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class, formRefeDto.getIdEvent());
			if (!StringUtils.isEmpty(eventEntity.getCdEventStatus())
					&& !StringUtils.isEmpty(formRefeDto.getIdFormsReferrals())) {
				FormsReferrals referalEntity = (FormsReferrals) sessionFactory.getCurrentSession()
						.load(FormsReferrals.class, formRefeDto.getIdFormsReferrals());
				if (referalEntity != null) {
					if (eventEntity.getCdEventStatus().equals(ServiceConstants.EVENT_STATUS_PROCESS) || ServiceConstants.TASK_CODE_CPSINV_FBSS_REF.equals(eventEntity.getCdTask()) ) {
						sessionFactory.getCurrentSession().delete(referalEntity);
						sessionFactory.getCurrentSession().delete(eventEntity);
						message = ServiceConstants.FORM_SUCCESS;
					} else {
						message = ServiceConstants.FORM_NOT_PROC;
					}
				} else {
					message = ServiceConstants.FORM_ID_ABSENT;
				}
			}
		} else {
			message = ServiceConstants.FORM_NO_ID;
		}
		return message;
	}

	/**
	 * Method Name: createAndReturnEventid Method Description:Method to generate
	 * event id in event table.
	 * 
	 * @param formReq
	 * @return @
	 */
	private Long createAndReturnEventid(FormReferralsReq formReq, String desc,String taskCode) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		Date date = new Date(System.currentTimeMillis());
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		Employee employee = (Employee) sessionFactory.getCurrentSession().get(Employee.class, formReq.getUserID());
		postEventIPDto.setEventDescr(desc.concat(employee.getNmEmployeeLast() + ", " + employee.getNmEmployeeFirst()));
		postEventIPDto.setCdTask(taskCode);
		postEventIPDto.setIdPerson(formReq.getUserID());
		postEventIPDto.setIdStage(formReq.getFormReferralsList().get(0).getIdStage());
		postEventIPDto.setDtEventOccurred(date);
		postEventIPDto.setUserId(formReq.getUserLogonID().toString());
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		if (!(TypeConvUtil.isNullOrEmpty(formReq.getFormReferralsList().get(0).getIdEvent()))) {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			postEventIPDto.setIdEvent(formReq.getFormReferralsList().get(0).getIdEvent());
		} else {
			postEventIPDto.setDtEventOccurred(date);
		}
		postEventIPDto.setTsLastUpdate(date);
		postEventIPDto.setCdEventStatus(formReq.getFormReferralsList().get(0).getEventStatus());
		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_FRM);
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	/**
	 * Method Name: createAndReturnEventid Method Description:Method to generate
	 * event id in event table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public Long postEvent(FormReferralsReq formReq, String description) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		postEventIPDto.setCdTask(ServiceConstants.EMPTY_STRING);
		postEventIPDto.setIdPerson(formReq.getUserID());
		postEventIPDto.setIdStage(formReq.getIdStage());
		postEventIPDto.setDtEventOccurred(new Date());
		Employee employee = (Employee) sessionFactory.getCurrentSession().get(Employee.class, formReq.getUserID());
		postEventIPDto
				.setEventDescr(description.concat(employee.getNmEmployeeLast() + ", " + employee.getNmEmployeeFirst()));
		postEventIPDto.setUserId(formReq.getUserLogonID());
		//artf255856 - If event id exist means caseworker updating the form referral page else adding new form referral data
		//it will prevent to add new event every time caseworker modify and save the form referral screen
		if ((0L != formReq.getIdEvent())) {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			postEventIPDto.setIdEvent(formReq.getIdEvent());
		} else {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		}
		postEventIPDto.setTsLastUpdate(new Date());
		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_FRM);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_PROC);
		if (formReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.FORM_REQ_C)) {
			postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_FRM);
			postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_COMP);
		}
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	/**
	 * Method Name: saveCourtesyReferralIntrvwDetail Method Description:Method
	 * to Save a record in Courtesy referral interview table.
	 * 
	 * @param formReq
	 * @
	 */
	public CourtesyReferralIntrvw saveCourtesyReferralIntrvwDetail(CourtesyReferralIntrvw courtesyReferralIntrvw) {
		sessionFactory.getCurrentSession()
				.saveOrUpdate(sessionFactory.getCurrentSession().merge(courtesyReferralIntrvw));
		return courtesyReferralIntrvw;
	}

	/**
	 * Method Name: CourtesyReferralDelete Method Description: Method to delete
	 * a record in CourtesyReferral table.
	 * 
	 * @param formReq
	 * @
	 */
	public void courtesyReferralDelete(Long idCourtesyFormReferrals) {
		CourtesyFormReferrals courtesyFormReferrals = new CourtesyFormReferrals();
		courtesyFormReferrals.setIdCourtesyFormReferrals(idCourtesyFormReferrals);
		courtesyFormReferrals = (CourtesyFormReferrals) sessionFactory.getCurrentSession()
				.load(CourtesyFormReferrals.class, idCourtesyFormReferrals);
		sessionFactory.getCurrentSession().delete(courtesyFormReferrals);
		return;
	}

	/**
	 * Method Name: getQuickFindPerson Method Description:This method is used to
	 * fetch the quch_find page values onload.
	 * 
	 * @param formReq
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	public FormReferralsRes getQuickFindPerson(FormReferralsReq formReq) {
		QuickFindPersonDto quickFindPersonDto = formReq.getQuickFindPersonDto().get(0);
		List<QuickFindPersonDto> quickFindPersonDtoList = new ArrayList<QuickFindPersonDto>();
		quickFindPersonDtoList = (List<QuickFindPersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getquickfindpersonsql).setParameter("id_stage", quickFindPersonDto.getIdStage()))
						.addScalar("requestorName", StandardBasicTypes.STRING)
						.addScalar("requestorRegion", StandardBasicTypes.STRING)
						.addScalar("requestorCounty", StandardBasicTypes.STRING)
						.addScalar("idCase", StandardBasicTypes.LONG).addScalar("age", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("subjectName", StandardBasicTypes.STRING).addScalar("ssn", StandardBasicTypes.STRING)
						.addScalar("currentAddr", StandardBasicTypes.STRING)
						.addScalar("currentApt", StandardBasicTypes.STRING)
						.addScalar("dtLastFTF", StandardBasicTypes.TIMESTAMP)
						.addScalar("currentCity", StandardBasicTypes.STRING)
						.addScalar("lang", StandardBasicTypes.STRING)
						.addScalar("currentState", StandardBasicTypes.STRING)
						.addScalar("type", StandardBasicTypes.STRING).addScalar("currentZip", StandardBasicTypes.STRING)
						.addScalar("role", StandardBasicTypes.STRING)
						.addScalar("currentCounty", StandardBasicTypes.STRING)
						.addScalar("dob", StandardBasicTypes.TIMESTAMP)
						.addScalar("currentPhn", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(QuickFindPersonDto.class)).list();
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsRes.setQuickFindPersonList(quickFindPersonDtoList);
		// log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;
	}

	/**
	 * 
	 * Method Name: getDiligentSearchHeader Method Description:This method
	 * retrieves data from DLGNT_SRCH_HDR, PERSON and STAGE_PERSON_LINK tables.
	 * 
	 * @param idFormsReferrals
	 * @return DlgntSrchHdrDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public DlgntSrchHdrDto getDiligentSearchHeader(Long idFormsReferrals) {
		DlgntSrchHdrDto dlgntSrchHdrDto = new DlgntSrchHdrDto();
		dlgntSrchHdrDto = (DlgntSrchHdrDto) sessionFactory.getCurrentSession().createSQLQuery(getDiligentSearchHeader)
				.addScalar("idDlgntSrchHdr", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idFormsReferrals", StandardBasicTypes.LONG)
				.addScalar("idRqstrPerson", StandardBasicTypes.LONG).addScalar("rostrPhone", StandardBasicTypes.STRING)
				.addScalar("mailCodeRqstr", StandardBasicTypes.STRING)
				.addScalar("emailRqstr", StandardBasicTypes.STRING).addScalar("unitRole", StandardBasicTypes.STRING)
				.addScalar("idWrkrPerson", StandardBasicTypes.LONG).addScalar("phoneWrkr", StandardBasicTypes.STRING)
				.addScalar("mailCodeWrkr", StandardBasicTypes.STRING).addScalar("emailWrkr", StandardBasicTypes.STRING)
				.addScalar("idSuprvsrPerson", StandardBasicTypes.LONG)
				.addScalar("phoneSuprvsr", StandardBasicTypes.STRING)
				.addScalar("mailCodeSuprvsr", StandardBasicTypes.STRING)
				.addScalar("emailSuprvsr", StandardBasicTypes.STRING).addScalar("legalCnty", StandardBasicTypes.STRING)
				.addScalar("regn", StandardBasicTypes.STRING).addScalar("causeNbr", StandardBasicTypes.STRING)
				.addScalar("indLoctPrnt", StandardBasicTypes.STRING)
				.addScalar("indPlcmntAdtnlParties", StandardBasicTypes.STRING)
				.addScalar("indLoctRel", StandardBasicTypes.STRING).addScalar("indOther", StandardBasicTypes.STRING)
				.addScalar("otherRost", StandardBasicTypes.STRING)
				.addScalar("indFindrsReport", StandardBasicTypes.STRING)
				.addScalar("indCourtCntnueJrsdctn", StandardBasicTypes.STRING)
				.addScalar("indPtrntyRgstry", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("nmRqstrFull", StandardBasicTypes.STRING).addScalar("nmWrkrFull", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("nmSuprvsrFull", StandardBasicTypes.STRING)
				.setParameter("idFormRef", idFormsReferrals)
				.setResultTransformer(Transformers.aliasToBean(DlgntSrchHdrDto.class)).uniqueResult();
		return dlgntSrchHdrDto;
	}

	/**
	 * 
	 * Method Name: getDiligentSearchChildDetails Method Description: This
	 * method retrieves data from DLGNT_SRCH_CHILD_DTL table.
	 * 
	 * @param idDlgntSrchDtl
	 * @return List<DlgntSrchChildDtlDto> @
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<DlgntSrchChildDtlDto> getDiligentSearchChildDetails(Long idDlgntSrchDtl) {
		List<DlgntSrchChildDtlDto> dlgntSrchChildDtlDtoList = new ArrayList<>();
		dlgntSrchChildDtlDtoList = (List<DlgntSrchChildDtlDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getDiligentSearchChildDetails).addScalar("idDlgntSrchChildDtl", StandardBasicTypes.LONG)
				.addScalar("idDlgntSrchDtl", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameter("idSrchDtl", idDlgntSrchDtl)
				.setResultTransformer(Transformers.aliasToBean(DlgntSrchChildDtlDto.class)).list();
		return dlgntSrchChildDtlDtoList;
	}

	/**
	 * 
	 * Method Name: getDiligentSearchDetail Method Description: This method
	 * retrieves data from DLGNT_SRCH_DTL table.
	 * 
	 * @param idDlgntSrchHdr
	 * @return List<DlgntSrchDtlDto> @
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<DlgntSrchDtlDto> getDiligentSearchDetail(Long idDlgntSrchHdr) {
		List<DlgntSrchDtlDto> dlgntSrchDtlDtoList = new ArrayList<>();
		dlgntSrchDtlDtoList = (List<DlgntSrchDtlDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getDiligentSearchDetail).addScalar("idDlgntSrchDtl", StandardBasicTypes.LONG)
				.addScalar("idDlgntSrchHdr", StandardBasicTypes.LONG)
				.addScalar("childSrchDetails", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("personSex", StandardBasicTypes.STRING).addScalar("dtPersonBirth", StandardBasicTypes.STRING)
				.addScalar("ssn", StandardBasicTypes.STRING).addScalar("idPersonP", StandardBasicTypes.LONG)
				.addScalar("birthState", StandardBasicTypes.STRING).addScalar("birthCounty", StandardBasicTypes.STRING)
				.addScalar("birthCity", StandardBasicTypes.STRING)
				.addScalar("indRqstLoctInfo", StandardBasicTypes.STRING)
				.addScalar("addrPerson", StandardBasicTypes.STRING)
				.addScalar("altrntAddrPerson", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnic", StandardBasicTypes.STRING)
				.addScalar("reltnshpToChild", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).setParameter("idDlgntHdr", idDlgntSrchHdr)
				.setResultTransformer(Transformers.aliasToBean(DlgntSrchDtlDto.class)).list();
		return dlgntSrchDtlDtoList;
	}

	/**
	 * 
	 * Method Name: saveAndUpdateDiligentSearch Method Description: This method
	 * will perform SAVE & UPDATE operations on FORMS_REFERRALS, DLGNT_SRCH_HDR,
	 * DLGNTSRCH_DTL and DLGNT_SRCH_CHILD_DTL
	 * 
	 * @param audDiligentSearchReq
	 * @return AudDiligentSearchRes
	 */
	public AudDiligentSearchRes saveAndUpdateDiligentSearch(AudDiligentSearchReq audDiligentSearchReq) {
		AudDiligentSearchRes audDiligentSearchRes = new AudDiligentSearchRes();
		FormReferralsDto formReferralsDto = audDiligentSearchReq.getFormReferralDto();
		DlgntSrchHdrDto dlgntSrchHdrDto = formReferralsDto.getDlgntSrchHdrDto();
		List<DlgntSrchDtlDto> dlgntSrchDtlDtoList = dlgntSrchHdrDto.getDlgntSrchDtlDtoList();
		FormsReferrals formReferralsEntity = new FormsReferrals();
		DlgntSrchDtl dlgntSrchDtlEntity = new DlgntSrchDtl();
		DlgntSrchChildDtl dlgntSrchChildDtlEntity = new DlgntSrchChildDtl();
		Set<DlgntSrchChildDtl> dlgntSrchChildDtlCollection = new HashSet<>();
		Set<DlgntSrchDtl> dlgntSrchDtlCollection = new HashSet<>();
		Date sysDate = new Date();
		if (!StringUtils.isEmpty(formReferralsDto.getIdFormsReferrals())) {
			formReferralsEntity = (FormsReferrals) sessionFactory.getCurrentSession().get(FormsReferrals.class,
					formReferralsDto.getIdFormsReferrals());
			if (!TypeConvUtil.isNullOrEmpty(formReferralsEntity.getDtCreated())) {
				formReferralsEntity.setDtCreated(formReferralsEntity.getDtCreated());
			}
			if (!TypeConvUtil.isNullOrEmpty(formReferralsDto.getIdCreatedPerson())) {
				formReferralsEntity.setIdCreatedPerson(formReferralsDto.getIdCreatedPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(formReferralsDto.getIdStage())) {
				formReferralsEntity.setDtLastUpdate(sysDate);
			}
			if (!TypeConvUtil.isNullOrEmpty(formReferralsDto.getIdLastUpdatePerson())) {
				formReferralsEntity.setIdLastUpdatePerson(formReferralsDto.getIdLastUpdatePerson());
			}
			if (!StringUtils.isEmpty(audDiligentSearchReq.getFormReferralDto().getIdEvent()) && audDiligentSearchReq
					.getFormReferralDto().getEventStatus().equals(ServiceConstants.EVENT_STATUS_COMPLETE)) {
				Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class,
						audDiligentSearchReq.getFormReferralDto().getIdEvent());
				if (eventEntity != null)
					eventEntity.setCdEventStatus(audDiligentSearchReq.getFormReferralDto().getEventStatus());
			}
		} else {
			formReferralsEntity = this.getTransiantFormReferralsEntity(audDiligentSearchReq);
		}
		DlgntSrchHdr dlgntSrchHdrEntity = null;
		if (!StringUtils.isEmpty(dlgntSrchHdrDto.getIdDlgntSrchHdr())) {
			dlgntSrchHdrEntity = (DlgntSrchHdr) sessionFactory.getCurrentSession().get(DlgntSrchHdr.class,
					dlgntSrchHdrDto.getIdDlgntSrchHdr());
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdStage())) {
				dlgntSrchHdrEntity.setIdStage(dlgntSrchHdrDto.getIdStage());
			}
			if (!TypeConvUtil.isNullOrEmpty(formReferralsEntity)) {
				dlgntSrchHdrEntity.setIdFormsReferrals(formReferralsEntity);
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdRqstrPerson())) {
				dlgntSrchHdrEntity.setIdRqstrPerson(dlgntSrchHdrDto.getIdRqstrPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getRostrPhone())) {
				dlgntSrchHdrEntity.setNbrRqstrPhone(dlgntSrchHdrDto.getRostrPhone());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getMailCodeRqstr())) {
				dlgntSrchHdrEntity.setMailCodeRqstr(dlgntSrchHdrDto.getMailCodeRqstr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getEmailRqstr())) {
				dlgntSrchHdrEntity.setTxtEmailRqstr(dlgntSrchHdrDto.getEmailRqstr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getUnitRole())) {
				dlgntSrchHdrEntity.setTxtUnitRole(dlgntSrchHdrDto.getUnitRole());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdWrkrPerson())) {
				dlgntSrchHdrEntity.setIdWrkrPerson(dlgntSrchHdrDto.getIdWrkrPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getPhoneWrkr())) {
				dlgntSrchHdrEntity.setNbrPhoneWrkr(dlgntSrchHdrDto.getPhoneWrkr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getMailCodeWrkr())) {
				dlgntSrchHdrEntity.setMailCodeWrkr(dlgntSrchHdrDto.getMailCodeWrkr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getEmailWrkr())) {
				dlgntSrchHdrEntity.setTxtEmailWrkr(dlgntSrchHdrDto.getEmailWrkr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdSuprvsrPerson())) {
				dlgntSrchHdrEntity.setIdSuprvsrPerson(dlgntSrchHdrDto.getIdSuprvsrPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getPhoneSuprvsr())) {
				dlgntSrchHdrEntity.setNbrPhoneSuprvsr(dlgntSrchHdrDto.getPhoneSuprvsr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getMailCodeSuprvsr())) {
				dlgntSrchHdrEntity.setMailCodeSuprvsr(dlgntSrchHdrDto.getMailCodeSuprvsr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getEmailSuprvsr())) {
				dlgntSrchHdrEntity.setTxtEmailSuprvsr(dlgntSrchHdrDto.getEmailSuprvsr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getLegalCnty())) {
				dlgntSrchHdrEntity.setTxtLegalCnty(dlgntSrchHdrDto.getLegalCnty());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getRegn())) {
				dlgntSrchHdrEntity.setTxtRegn(dlgntSrchHdrDto.getRegn());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getCauseNbr())) {
				dlgntSrchHdrEntity.setTxtCauseNbr(dlgntSrchHdrDto.getCauseNbr());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndLoctPrnt())) {
				dlgntSrchHdrEntity.setIndLoctPrnt(dlgntSrchHdrDto.getIndLoctPrnt());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndPlcmntAdtnlParties())) {
				dlgntSrchHdrEntity.setIndPlcmntAdtnlParties(dlgntSrchHdrDto.getIndPlcmntAdtnlParties());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndLoctRel())) {
				dlgntSrchHdrEntity.setIndLoctRel(dlgntSrchHdrDto.getIndLoctRel());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndOther())) {
				dlgntSrchHdrEntity.setIndOther(dlgntSrchHdrDto.getIndOther());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getOtherRost())) {
				dlgntSrchHdrEntity.setTxtOtherRqst(dlgntSrchHdrDto.getOtherRost());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndFindrsReport())) {
				dlgntSrchHdrEntity.setIndFindrsReport(dlgntSrchHdrDto.getIndFindrsReport());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndCourtCntnueJrsdctn())) {
				dlgntSrchHdrEntity.setIndCourtCntnueJrsdctn(dlgntSrchHdrDto.getIndCourtCntnueJrsdctn());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndPtrntyRgstry())) {
				dlgntSrchHdrEntity.setIndPtrntyRgstry(dlgntSrchHdrDto.getIndPtrntyRgstry());
			}
			dlgntSrchHdrEntity.setDtCreated(sysDate);
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdCreatedPerson())) {
				dlgntSrchHdrEntity.setIdCreatedPerson(dlgntSrchHdrDto.getIdCreatedPerson());
			}
			dlgntSrchHdrEntity.setDtLastUpdate(sysDate);
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdLastUpdatePerson())) {
				dlgntSrchHdrEntity.setIdLastUpdatePerson(dlgntSrchHdrDto.getIdLastUpdatePerson());
			}
		} else {
			dlgntSrchHdrEntity = this.getTransiantdlgntSrchHdrEntity(dlgntSrchHdrDto);
			if (!TypeConvUtil.isNullOrEmpty(formReferralsEntity)) {
				dlgntSrchHdrEntity.setIdFormsReferrals(formReferralsEntity);
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDtoList)) {
			for (DlgntSrchDtlDto dlgntSrchDtlDto : dlgntSrchDtlDtoList) {
				dlgntSrchDtlEntity = new DlgntSrchDtl();
				if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdPerson())) {
					if (!StringUtils.isEmpty(dlgntSrchDtlDto.getIdDlgntSrchDtl())) {
						dlgntSrchDtlEntity = (DlgntSrchDtl) sessionFactory.getCurrentSession().get(DlgntSrchDtl.class,
								dlgntSrchDtlDto.getIdDlgntSrchDtl());
						dlgntSrchDtlEntity.setIdDlgntSrchHdr(dlgntSrchHdrEntity);
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getChildSrchDetails())) {
							dlgntSrchDtlEntity.setTxtChildSrchDetails(dlgntSrchDtlDto.getChildSrchDetails());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdPerson())) {
							dlgntSrchDtlEntity.setIdPerson(dlgntSrchDtlDto.getIdPerson());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getPersonSex())) {
							dlgntSrchDtlEntity.setTxtPersonSex(dlgntSrchDtlDto.getPersonSex());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getDtPersonBirth())) {
							dlgntSrchDtlEntity.setDtPersonBirth(dlgntSrchDtlDto.getDtPersonBirth());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getSsn())) {
							dlgntSrchDtlEntity.setNbrSsn(dlgntSrchDtlDto.getSsn());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdPersonP())) {
							dlgntSrchDtlEntity.setNbrPersonPid(dlgntSrchDtlDto.getIdPersonP());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getBirthState())) {
							dlgntSrchDtlEntity.setTxtBirthState(dlgntSrchDtlDto.getBirthState());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getBirthCounty())) {
							dlgntSrchDtlEntity.setTxtBirthCounty(dlgntSrchDtlDto.getBirthCounty());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getBirthCity())) {
							dlgntSrchDtlEntity.setTxtBirthCity(dlgntSrchDtlDto.getBirthCity());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIndRqstLoctInfo())) {
							dlgntSrchDtlEntity.setIndRqstLoctInfo(dlgntSrchDtlDto.getIndRqstLoctInfo());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getAddrPerson())) {
							dlgntSrchDtlEntity.setTxtAddrPerson(dlgntSrchDtlDto.getAddrPerson());
						}
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getAltrntAddrPerson())) {
							dlgntSrchDtlEntity.setTxtAltrntAddrPerson(dlgntSrchDtlDto.getAltrntAddrPerson());
						}
						dlgntSrchDtlEntity.setTxtReltnshpToChild(dlgntSrchDtlDto.getReltnshpToChild());
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getCdPersonEthnic())) {
							dlgntSrchDtlEntity.setCdPersonEthnic(dlgntSrchDtlDto.getCdPersonEthnic());
						}
						dlgntSrchDtlEntity.setDtCreated(dlgntSrchDtlEntity.getDtCreated());
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdCreatedPerson())) {
							dlgntSrchDtlEntity.setIdCreatedPerson(dlgntSrchDtlDto.getIdCreatedPerson());
						}
						dlgntSrchDtlEntity.setDtLastUpdate(sysDate);
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdLastUpdatePerson())) {
							dlgntSrchDtlEntity.setIdLastUpdatePerson(dlgntSrchDtlDto.getIdLastUpdatePerson());
						}
					} else {
						dlgntSrchDtlEntity = this.getTransiantDlgntSrchDtlEntity(dlgntSrchDtlDto);
						if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrEntity)) {
							dlgntSrchDtlEntity.setIdDlgntSrchHdr(dlgntSrchHdrEntity);
						}
					}
					if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getDlgntSrchChildDtlDtoList())) {
						for (DlgntSrchChildDtlDto dlgntSrchChildDtlDto : dlgntSrchDtlDto
								.getDlgntSrchChildDtlDtoList()) {
							if (dlgntSrchChildDtlDto.getIdPerson() == 0
									&& !TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdDlgntSrchDtl())) {
								deleteDlgntChildren(dlgntSrchChildDtlDto.getIdDlgntSrchChildDtl());
							}

							if (dlgntSrchChildDtlDto.getIdPerson() == 0) {
								continue;
							}
							dlgntSrchChildDtlEntity = new DlgntSrchChildDtl();
							if (!StringUtils.isEmpty(dlgntSrchChildDtlDto.getIdDlgntSrchChildDtl())) {
								dlgntSrchChildDtlEntity = (DlgntSrchChildDtl) sessionFactory.getCurrentSession()
										.get(DlgntSrchChildDtl.class, dlgntSrchChildDtlDto.getIdDlgntSrchChildDtl());
								dlgntSrchChildDtlEntity.setIdDlgntSrchDtl(dlgntSrchDtlEntity);
								if (!TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdPerson())) {
									dlgntSrchChildDtlEntity.setIdPerson(dlgntSrchChildDtlDto.getIdPerson());
								}
								dlgntSrchChildDtlEntity.setDtCreated(dlgntSrchChildDtlEntity.getDtCreated());
								if (!TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdCreatedPerson())) {
									dlgntSrchChildDtlEntity
											.setIdCreatedPerson(dlgntSrchChildDtlDto.getIdCreatedPerson());
								}
								dlgntSrchChildDtlEntity.setDtLastUpdate(sysDate);
								if (!TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdLastUpdatePerson())) {
									dlgntSrchChildDtlEntity
											.setIdLastUpdatePerson(dlgntSrchChildDtlDto.getIdLastUpdatePerson());
								}
							} else {

								dlgntSrchChildDtlEntity = this
										.getTransiantDlgntSrchChildDtlEntity(dlgntSrchChildDtlDto);
								if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlEntity)) {
									dlgntSrchChildDtlEntity.setIdDlgntSrchDtl(dlgntSrchDtlEntity);
								}
							}
							dlgntSrchChildDtlCollection.add(dlgntSrchChildDtlEntity);
						}
					}
					dlgntSrchDtlEntity.setDlgntSrchChildDtlCollection(dlgntSrchChildDtlCollection);
					dlgntSrchDtlCollection.add(dlgntSrchDtlEntity);
				} else {

					for (DlgntSrchDtlDto dlgntSearchDtl : dlgntSrchDtlDtoList) {
						if (TypeConvUtil.isNullOrEmpty(dlgntSearchDtl.getIdPerson())
								&& !TypeConvUtil.isNullOrEmpty(dlgntSearchDtl.getIdDlgntSrchDtl())) {
							deleleDlgntPersons(dlgntSearchDtl.getIdDlgntSrchDtl(), dlgntSearchDtl.getReltnshpToChild());
						}
					}
				}
			}
		}
		dlgntSrchHdrEntity.setDlgntSrchDtlCollection(dlgntSrchDtlCollection);
		Set<DlgntSrchHdr> dlgntSrchHdrCollection = new HashSet<>();
		dlgntSrchHdrCollection.add(dlgntSrchHdrEntity);
		formReferralsEntity.setDlgntSrchHdrCollection(dlgntSrchHdrCollection);
		if (audDiligentSearchReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
			sessionFactory.getCurrentSession().saveOrUpdate(formReferralsEntity);
			audDiligentSearchRes.setIdFormsReferral(formReferralsEntity.getIdFormsReferrals());
			audDiligentSearchRes.setIdEvent(formReferralsEntity.getIdEvent());
			audDiligentSearchRes.setMessage(ServiceConstants.UPDATE_MSG);
		} else {
			Long idForms = (Long) sessionFactory.getCurrentSession().save(formReferralsEntity);
			audDiligentSearchRes.setIdFormsReferral(idForms);
			audDiligentSearchRes.setIdEvent(formReferralsEntity.getIdEvent());
			audDiligentSearchRes.setMessage(ServiceConstants.SAVE_SUCCESS);
		}
		log.info("TransactionId :" + audDiligentSearchReq.getTransactionId());
		return audDiligentSearchRes;
	}

	/**
	 * 
	 * Method Name: getPersonDtlByStageId Method Description: This method will
	 * retrieves data from PERSON,PERSON_DTL and STAGE_PERSON_LINK tables.
	 * 
	 * @param idStage
	 * @return List<DlgntSrchDtlDto> @
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<DlgntSrchDtlDto> getPersonDtlByStageId(Long idStage) {
		List<DlgntSrchDtlDto> dlgntSrchDtlDtoList = new ArrayList<>();
		dlgntSrchDtlDtoList = (List<DlgntSrchDtlDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonDtlByStageId).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtPersonBirth", StandardBasicTypes.STRING)
				.addScalar("birthCounty", StandardBasicTypes.STRING)
				.addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("birthState", StandardBasicTypes.STRING).addScalar("birthCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnic", StandardBasicTypes.STRING)
				.addScalar("personSex", StandardBasicTypes.STRING).addScalar("addrPerson", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("ssn", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).setParameter("idStg", idStage)
				.setResultTransformer(Transformers.aliasToBean(DlgntSrchDtlDto.class)).list();
		return dlgntSrchDtlDtoList;
	}

	/**
	 * Method Name: deletedlgtSearch Method Description:Method to delete record
	 * from the
	 * DLGNT_SRCH_HDR,DLGNT_SRCH_DTL,DLGNT_SRCH_CHILD_DTL,FORMS_REFERRALS table.
	 * 
	 * @param formReq
	 * @return @
	 */
	public String deletedlgtSearch(FormReferralsReq formReq) {
		String message = null;
		FormsReferrals referalEntity = (FormsReferrals) sessionFactory.getCurrentSession().load(FormsReferrals.class,
				formReq.getIdFormReferral());
		if (referalEntity != null && !StringUtils.isEmpty(referalEntity.getIdEvent())) {
			Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class, referalEntity.getIdEvent());
			if (eventEntity != null && !StringUtils.isEmpty(eventEntity.getCdEventStatus())) {
				if (eventEntity.getCdEventStatus().equals(ServiceConstants.EVENT_STATUS_PROCESS)) {
					sessionFactory.getCurrentSession().delete(referalEntity);
					//artf220413 : Set event id to the request form in web and add a code to delete the event on service using event id.
					sessionFactory.getCurrentSession().delete(eventEntity);
					message = ServiceConstants.FORM_SUCCESS;
				} else {
					message = ServiceConstants.FORM_NOT_PROC;
				}
			} else {
				message = ServiceConstants.FORM_NO_ID;
			}
		} else {
			message = ServiceConstants.FORM_ID_ABSENT;
		}
		return message;
	}

	public String deleleDlgntPersons(Long idDlgntDetail, String txtReltnshpToChild) {
		String message = null;

		Session session = sessionFactory.getCurrentSession();
		if (("Mother").equalsIgnoreCase(txtReltnshpToChild) || ("Child").equalsIgnoreCase(txtReltnshpToChild)) {
			Query q = session.createQuery(delDiligentDtl).setParameter("idDlgntDetail", idDlgntDetail);
			q.executeUpdate();
			message = "SUCCESS";
		} else {
			Query q1 = session.createSQLQuery(delDiligentChild).setParameter("idDlgntDetail", idDlgntDetail);
			q1.executeUpdate();
			Query q2 = session.createQuery(delDiligentDtl).setParameter("idDlgntDetail", idDlgntDetail);

			q2.executeUpdate();
			message = "SUCCESS";
		}
		return message;
	}

	public String deleteDlgntChildren(Long IdDlgntSrchChildDtl) {
		String message = null;
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(delDiligentChildren).setParameter("idDlgntSrchChildDtl", IdDlgntSrchChildDtl);
		q.executeUpdate();
		message = "SUCCESS";
		return message;
	}

	/**
	 * 
	 * Method Name: getCaseWorkerDtl Method Description: This method will
	 * retrieve idCase and Region by passing idStage.
	 * 
	 * @param idStage
	 * @return DlgntSrchHdrDto @
	 */
	public DlgntSrchHdrDto getCaseWorkerDtl(Long idStage) {
		DlgntSrchHdrDto dlgntSrchHdrDto = new DlgntSrchHdrDto();
		dlgntSrchHdrDto = (DlgntSrchHdrDto) sessionFactory.getCurrentSession().createSQLQuery(getCaseWorkerDtl)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("regn", StandardBasicTypes.STRING)
				.addScalar("idWrkrPerson", StandardBasicTypes.LONG).addScalar("nmWrkrFull", StandardBasicTypes.STRING)
				.setParameter("idStg", idStage).setResultTransformer(Transformers.aliasToBean(DlgntSrchHdrDto.class))
				.uniqueResult();
		return dlgntSrchHdrDto;
	}

	/**
	 * 
	 * Method Name: getSupervisorId Method Description: This method will
	 * retrieve ID Person for the Supervisor.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	public Long getSupervisorId(Long idPerson) {
		Long idSupr = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSupervisorId).setParameter("idPerson",
				idPerson);
		BigDecimal bigDecimal = ((BigDecimal) query.uniqueResult());
		if (!TypeConvUtil.isNullOrEmpty(bigDecimal)) {
			idSupr = Long.valueOf(bigDecimal.longValue());
		}
		return idSupr;
	}

	/**
	 * 
	 * Method Name: getDlgntHdrByStageId Method Description: This method
	 * retrieves case worker's information by passing idPerson.
	 * 
	 * @param idPerson
	 * @return OnLoadDlgntHdrDto @
	 */
	public OnLoadDlgntHdrDto getDlgntHdrByStageId(Long idPerson) {
		OnLoadDlgntHdrDto onLoadDlgntHdrDto = new OnLoadDlgntHdrDto();
		onLoadDlgntHdrDto = (OnLoadDlgntHdrDto) sessionFactory.getCurrentSession().createSQLQuery(getDlgntHdrByStageId)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("phone", StandardBasicTypes.STRING)
				.addScalar("cdMail", StandardBasicTypes.STRING).addScalar("email", StandardBasicTypes.STRING)
				.addScalar("unitRole", StandardBasicTypes.STRING).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idPrsn", idPerson)
				.setResultTransformer(Transformers.aliasToBean(OnLoadDlgntHdrDto.class)).uniqueResult();
		return onLoadDlgntHdrDto;
	}

	/**
	 * 
	 * Method Name: createEvent Method Description: This method will create
	 * event for Diligent Search Insert.
	 * 
	 * @param audDiligentSearchReq
	 * @param desc
	 * @return Long @
	 */
	private Long createEvent(AudDiligentSearchReq audDiligentSearchReq, String desc) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		Date date = new Date(System.currentTimeMillis());
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		Employee employee = (Employee) sessionFactory.getCurrentSession().get(Employee.class,
				audDiligentSearchReq.getIdUser());
		postEventIPDto.setEventDescr(desc.concat(employee.getNmEmployeeLast() + ", " + employee.getNmEmployeeFirst()));
		postEventIPDto.setCdTask(ServiceConstants.EMPTY_STRING);
		postEventIPDto.setIdPerson(audDiligentSearchReq.getIdUser());
		postEventIPDto.setIdStage(audDiligentSearchReq.getFormReferralDto().getIdStage());
		postEventIPDto.setDtEventOccurred(date);
		postEventIPDto.setUserId(audDiligentSearchReq.getLogonUserId());
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		if (!(TypeConvUtil.isNullOrEmpty(audDiligentSearchReq.getFormReferralDto().getIdEvent()))) {
			archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			postEventIPDto.setIdEvent(audDiligentSearchReq.getFormReferralDto().getIdEvent());
		} else {
			postEventIPDto.setDtEventOccurred(date);
		}
		postEventIPDto.setTsLastUpdate(date);
		postEventIPDto.setCdEventStatus(audDiligentSearchReq.getFormReferralDto().getEventStatus());
		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_FRM);
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	private DlgntSrchHdr getTransiantdlgntSrchHdrEntity(DlgntSrchHdrDto dlgntSrchHdrDto) {
		DlgntSrchHdr dlgntSrchHdrEntity = new DlgntSrchHdr();
		Date sysDate = new Date();
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdStage())) {
			dlgntSrchHdrEntity.setIdStage(dlgntSrchHdrDto.getIdStage());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdRqstrPerson())) {
			dlgntSrchHdrEntity.setIdRqstrPerson(dlgntSrchHdrDto.getIdRqstrPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getRostrPhone())) {
			dlgntSrchHdrEntity.setNbrRqstrPhone(dlgntSrchHdrDto.getRostrPhone());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getMailCodeRqstr())) {
			dlgntSrchHdrEntity.setMailCodeRqstr(dlgntSrchHdrDto.getMailCodeRqstr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getEmailRqstr())) {
			dlgntSrchHdrEntity.setTxtEmailRqstr(dlgntSrchHdrDto.getEmailRqstr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getUnitRole())) {
			dlgntSrchHdrEntity.setTxtUnitRole(dlgntSrchHdrDto.getUnitRole());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdWrkrPerson())) {
			dlgntSrchHdrEntity.setIdWrkrPerson(dlgntSrchHdrDto.getIdWrkrPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getPhoneWrkr())) {
			dlgntSrchHdrEntity.setNbrPhoneWrkr(dlgntSrchHdrDto.getPhoneWrkr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getMailCodeWrkr())) {
			dlgntSrchHdrEntity.setMailCodeWrkr(dlgntSrchHdrDto.getMailCodeWrkr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getEmailWrkr())) {
			dlgntSrchHdrEntity.setTxtEmailWrkr(dlgntSrchHdrDto.getEmailWrkr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdSuprvsrPerson())) {
			dlgntSrchHdrEntity.setIdSuprvsrPerson(dlgntSrchHdrDto.getIdSuprvsrPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getPhoneSuprvsr())) {
			dlgntSrchHdrEntity.setNbrPhoneSuprvsr(dlgntSrchHdrDto.getPhoneSuprvsr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getMailCodeSuprvsr())) {
			dlgntSrchHdrEntity.setMailCodeSuprvsr(dlgntSrchHdrDto.getMailCodeSuprvsr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getEmailSuprvsr())) {
			dlgntSrchHdrEntity.setTxtEmailSuprvsr(dlgntSrchHdrDto.getEmailSuprvsr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getLegalCnty())) {
			dlgntSrchHdrEntity.setTxtLegalCnty(dlgntSrchHdrDto.getLegalCnty());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getRegn())) {
			dlgntSrchHdrEntity.setTxtRegn(dlgntSrchHdrDto.getRegn());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getCauseNbr())) {
			dlgntSrchHdrEntity.setTxtCauseNbr(dlgntSrchHdrDto.getCauseNbr());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndLoctPrnt())) {
			dlgntSrchHdrEntity.setIndLoctPrnt(dlgntSrchHdrDto.getIndLoctPrnt());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndPlcmntAdtnlParties())) {
			dlgntSrchHdrEntity.setIndPlcmntAdtnlParties(dlgntSrchHdrDto.getIndPlcmntAdtnlParties());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndLoctRel())) {
			dlgntSrchHdrEntity.setIndLoctRel(dlgntSrchHdrDto.getIndLoctRel());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndOther())) {
			dlgntSrchHdrEntity.setIndOther(dlgntSrchHdrDto.getIndOther());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getOtherRost())) {
			dlgntSrchHdrEntity.setTxtOtherRqst(dlgntSrchHdrDto.getOtherRost());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndFindrsReport())) {
			dlgntSrchHdrEntity.setIndFindrsReport(dlgntSrchHdrDto.getIndFindrsReport());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndCourtCntnueJrsdctn())) {
			dlgntSrchHdrEntity.setIndCourtCntnueJrsdctn(dlgntSrchHdrDto.getIndCourtCntnueJrsdctn());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIndPtrntyRgstry())) {
			dlgntSrchHdrEntity.setIndPtrntyRgstry(dlgntSrchHdrDto.getIndPtrntyRgstry());
		}
		dlgntSrchHdrEntity.setDtCreated(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdCreatedPerson())) {
			dlgntSrchHdrEntity.setIdCreatedPerson(dlgntSrchHdrDto.getIdCreatedPerson());
		}
		dlgntSrchHdrEntity.setDtLastUpdate(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdLastUpdatePerson())) {
			dlgntSrchHdrEntity.setIdLastUpdatePerson(dlgntSrchHdrDto.getIdLastUpdatePerson());
		}
		return dlgntSrchHdrEntity;
	}

	private FormsReferrals getTransiantFormReferralsEntity(AudDiligentSearchReq audDiligentSearchReq) {
		FormsReferrals formReferralsEntity = new FormsReferrals();
		Date sysDate = new Date();
		FormReferralsDto formReferralsDto = audDiligentSearchReq.getFormReferralDto();
		Long eventId = createEvent(audDiligentSearchReq, ServiceConstants.DLGNT_DESC);
		if (!TypeConvUtil.isNullOrEmpty(formReferralsDto.getIdStage())) {
			formReferralsEntity.setIdStage(formReferralsDto.getIdStage());
		}
		if (!TypeConvUtil.isNullOrEmpty(eventId)) {
			formReferralsEntity.setIdEvent(eventId);
		}
		formReferralsEntity.setCdFormType(ServiceConstants.FORM_TYPE_DLGN);
		formReferralsEntity.setDtCreated(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(formReferralsDto.getIdCreatedPerson())) {
			formReferralsEntity.setIdCreatedPerson(formReferralsDto.getIdCreatedPerson());
		}
		formReferralsEntity.setDtLastUpdate(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(formReferralsDto.getIdLastUpdatePerson())) {
			formReferralsEntity.setIdLastUpdatePerson(formReferralsDto.getIdLastUpdatePerson());
		}
		return formReferralsEntity;
	}

	private DlgntSrchDtl getTransiantDlgntSrchDtlEntity(DlgntSrchDtlDto dlgntSrchDtlDto) {
		DlgntSrchDtl dlgntSrchDtlEntity = new DlgntSrchDtl();
		Date sysDate = new Date();
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getChildSrchDetails())) {
			dlgntSrchDtlEntity.setTxtChildSrchDetails(dlgntSrchDtlDto.getChildSrchDetails());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdPerson())) {
			dlgntSrchDtlEntity.setIdPerson(dlgntSrchDtlDto.getIdPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getPersonSex())) {
			dlgntSrchDtlEntity.setTxtPersonSex(dlgntSrchDtlDto.getPersonSex());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getDtPersonBirth())) {
			dlgntSrchDtlEntity.setDtPersonBirth(dlgntSrchDtlDto.getDtPersonBirth());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getSsn())) {
			dlgntSrchDtlEntity.setNbrSsn(dlgntSrchDtlDto.getSsn());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdPersonP())) {
			dlgntSrchDtlEntity.setNbrPersonPid(dlgntSrchDtlDto.getIdPersonP());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getBirthState())) {
			dlgntSrchDtlEntity.setTxtBirthState(dlgntSrchDtlDto.getBirthState());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getBirthCounty())) {
			dlgntSrchDtlEntity.setTxtBirthCounty(dlgntSrchDtlDto.getBirthCounty());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getBirthCity())) {
			dlgntSrchDtlEntity.setTxtBirthCity(dlgntSrchDtlDto.getBirthCity());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIndRqstLoctInfo())) {
			dlgntSrchDtlEntity.setIndRqstLoctInfo(dlgntSrchDtlDto.getIndRqstLoctInfo());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getAddrPerson())) {
			dlgntSrchDtlEntity.setTxtAddrPerson(dlgntSrchDtlDto.getAddrPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getAltrntAddrPerson())) {
			dlgntSrchDtlEntity.setTxtAltrntAddrPerson(dlgntSrchDtlDto.getAltrntAddrPerson());
		}
		dlgntSrchDtlEntity.setTxtReltnshpToChild(dlgntSrchDtlDto.getReltnshpToChild());
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getCdPersonEthnic())) {
			dlgntSrchDtlEntity.setCdPersonEthnic(dlgntSrchDtlDto.getCdPersonEthnic());
		}
		dlgntSrchDtlEntity.setDtCreated(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdCreatedPerson())) {
			dlgntSrchDtlEntity.setIdCreatedPerson(dlgntSrchDtlDto.getIdCreatedPerson());
		}
		dlgntSrchDtlEntity.setDtLastUpdate(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDto.getIdLastUpdatePerson())) {
			dlgntSrchDtlEntity.setIdLastUpdatePerson(dlgntSrchDtlDto.getIdLastUpdatePerson());
		}
		return dlgntSrchDtlEntity;
	}

	private DlgntSrchChildDtl getTransiantDlgntSrchChildDtlEntity(DlgntSrchChildDtlDto dlgntSrchChildDtlDto) {
		DlgntSrchChildDtl dlgntSrchChildDtlEntity = new DlgntSrchChildDtl();
		Date sysDate = new Date();
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdPerson())) {
			dlgntSrchChildDtlEntity.setIdPerson(dlgntSrchChildDtlDto.getIdPerson());
		}
		dlgntSrchChildDtlEntity.setDtCreated(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdCreatedPerson())) {
			dlgntSrchChildDtlEntity.setIdCreatedPerson(dlgntSrchChildDtlDto.getIdCreatedPerson());
		}
		dlgntSrchChildDtlEntity.setDtLastUpdate(sysDate);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchChildDtlDto.getIdLastUpdatePerson())) {
			dlgntSrchChildDtlEntity.setIdLastUpdatePerson(dlgntSrchChildDtlDto.getIdLastUpdatePerson());
		}
		return dlgntSrchChildDtlEntity;
	}

	/**
	 * 
	 * Method Name: getCaseWorkerCounty Method Description: This method
	 * retrieves case worker's information by passing idPerson.
	 * 
	 * @param idPerson
	 * @return caseWorkerDto @
	 */
	public CaseWorkerDtlDto getCaseWorkerCounty(Long idPerson) {
		CaseWorkerDtlDto caseWorkerDtlDto = new CaseWorkerDtlDto();
		caseWorkerDtlDto = (CaseWorkerDtlDto) sessionFactory.getCurrentSession().createSQLQuery(getCaseWorkerCounty)
				.addScalar("cdCounty", StandardBasicTypes.STRING).addScalar("region", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(CaseWorkerDtlDto.class)).uniqueResult();
		return caseWorkerDtlDto;
	}

	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetails
	 * Method Description: This method retrieves household SDM safety assessment, and address details.
	 *
	 * @param householdList
	 * @return List
	 */
	@Override
	public List<QuickFindPersonDto> getHouseHoldDetails(List<Long> householdList,Long idCase) {
		List<QuickFindPersonDto> houseHoldDetailsList=sessionFactory.getCurrentSession().createSQLQuery(gethouseHoldDetails)
				.addScalar("section", StandardBasicTypes.STRING)
				.addScalar("idCpsSa", StandardBasicTypes.LONG)
				.addScalar("selectedTxt", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("dtAssessed", StandardBasicTypes.DATE)
				.addScalar("eventStatus", StandardBasicTypes.STRING)
				.addScalar("currentAddr", StandardBasicTypes.STRING)
				.addScalar("currentApt", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("currentCity", StandardBasicTypes.STRING)
				.addScalar("currentZip", StandardBasicTypes.STRING)
				.addScalar("lang", StandardBasicTypes.STRING).addScalar("fullname", StandardBasicTypes.STRING)
				.addScalar("followupquestiontext", StandardBasicTypes.STRING).addScalar("otherdesc", StandardBasicTypes.STRING)
				.addScalar("questionNbr", StandardBasicTypes.INTEGER).setParameter("idCase",idCase )
				.setParameterList("householdList",householdList )
				.setResultTransformer(Transformers.aliasToBean(QuickFindPersonDto.class)).list();
		return houseHoldDetailsList;
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: getHouseHoldDetailsBySA
	 * Method Description: This method retrieves household id for the cpssa.
	 *
	 * @param idcpssa
	 * @return Long
	 */
	@Override
	public List<QuickFindPersonDto> getHouseHoldDetailsBySA(Long idcpssa) {
		List<QuickFindPersonDto> houseHoldDetailsList=sessionFactory.getCurrentSession().createSQLQuery(gethouseHoldDetailsByCpsSA)
				.addScalar("section", StandardBasicTypes.STRING)
				.addScalar("idCpsSa", StandardBasicTypes.LONG)
				.addScalar("selectedTxt", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("dtAssessed", StandardBasicTypes.DATE)
				.addScalar("eventStatus", StandardBasicTypes.STRING)
				.addScalar("currentAddr", StandardBasicTypes.STRING)
				.addScalar("currentApt", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("currentCity", StandardBasicTypes.STRING)
				.addScalar("currentZip", StandardBasicTypes.STRING)
				.addScalar("lang", StandardBasicTypes.STRING).addScalar("fullname", StandardBasicTypes.STRING)
				.addScalar("followupquestiontext", StandardBasicTypes.STRING).addScalar("otherdesc", StandardBasicTypes.STRING)
				.addScalar("questionNbr", StandardBasicTypes.INTEGER).setParameter("idCpsSA",idcpssa )
				.setResultTransformer(Transformers.aliasToBean(QuickFindPersonDto.class)).list();
		return houseHoldDetailsList;
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: valdiateSaveAndSubmit
	 * Method Description: This method valdiates on SaveAndSubmit.
	 *
	 * @param idStage
	 * @param indApprovalFlow
	 * @param idcpssa
	 * @param idPersonHouseHold
	 * @return Integer
	 */
	@Override
	public String validateSaveAndSubmit(Long idStage, String indApprovalFlow, Long idcpssa, Long idPersonHouseHold) {
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		String validationMessageNbr="";
		String errorMessage = null;
		try ( Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			  CallableStatement callStatement =connection.prepareCall(validateSaveAndSubmit)) {
			callStatement.setLong(1, idStage);
			callStatement.setString(2, indApprovalFlow);
			callStatement.setLong(3, idcpssa);
			callStatement.setLong(4, idPersonHouseHold);
			callStatement.registerOutParameter(5, Types.VARCHAR);
			callStatement.registerOutParameter(6, Types.VARCHAR);
			callStatement.executeUpdate();
			validationMessageNbr = callStatement.getString(5);
			errorMessage = callStatement.getString(6);

			if ("-1".equalsIgnoreCase(validationMessageNbr)) {
				log.error(errorMessage);
			}
		} catch (SQLException sqlExp) {
			DataLayerException dataLayerException = new DataLayerException(sqlExp.getMessage());
			dataLayerException.initCause(sqlExp);
			throw dataLayerException;
		}
		return  validationMessageNbr;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: getFormsReferralIdByApproval
	 * Method Description: This method retrieves the idFormReferral based on the idApproval
	 *
	 * @param idApproval
	 * @return
	 */
	@Override
	public Long getFormsReferralIdByApproval(Long idApproval) {

		Long idFormsReferral = null;
		Query query =  sessionFactory.getCurrentSession().createSQLQuery(getFormsReferralIdByApproval)
				.setParameter("idApproval", idApproval);

		BigDecimal queryResult = (BigDecimal) query.uniqueResult();

		if (!ObjectUtils.isEmpty(queryResult)) {
			idFormsReferral = queryResult.longValue();
		}

		return idFormsReferral;
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: getFBSSReferralsForFPR Method Description: Method to retrieve
	 * FBSS referral details .
	 *
	 * @param idStage
	 * @return @FormReferralsDto
	 */
	@Override
	public FormReferralsDto getFBSSReferralsForFPR(Long idStage) {
		FormReferralsDto formReferralsDto = new FormReferralsDto();
		formReferralsDto = (FormReferralsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFBSSReferralSql).setParameter("idStage", idStage))
				.addScalar("idFormsReferrals", StandardBasicTypes.LONG)
				.addScalar("eventStatus", StandardBasicTypes.STRING)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(FormReferralsDto.class)).uniqueResult();
		return formReferralsDto;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public DlgntSrchHdrDto getDiligentSearchId(Long idFormsReferrals){
		DlgntSrchHdrDto dlgntSrchHdrDto = new DlgntSrchHdrDto();
		Query query =  sessionFactory.getCurrentSession().createSQLQuery(getDiligentSearchHeaderId)
				.setParameter("idFormRef", idFormsReferrals);
		BigDecimal queryResult = (BigDecimal) query.uniqueResult();

		if (!ObjectUtils.isEmpty(queryResult)) {
			dlgntSrchHdrDto.setIdDlgntSrchHdr(queryResult.longValue());
		}
		return dlgntSrchHdrDto;
	}
}
