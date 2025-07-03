package us.tx.state.dfps.service.placement.daoimpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CaExCommonApplication;
import us.tx.state.dfps.common.domain.CaExEducationService;
import us.tx.state.dfps.common.domain.CaExHospitalization;
import us.tx.state.dfps.common.domain.CaExMedication;
import us.tx.state.dfps.common.domain.CaExPlacementLog;
import us.tx.state.dfps.common.domain.CaExReturnFromRunaway;
import us.tx.state.dfps.common.domain.CaExServicesProvided;
import us.tx.state.dfps.common.domain.CaExSexualBehavior;
import us.tx.state.dfps.common.domain.CaExSiblings;
import us.tx.state.dfps.common.domain.CaExSpecialProgramming;
import us.tx.state.dfps.common.domain.CaExSubstanceUse;
import us.tx.state.dfps.common.domain.CaExTraffickingHistory;
import us.tx.state.dfps.common.domain.CaExTraumaHistory;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonAppShortFormReq;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.CommonApplicationShortFormDao;
import us.tx.state.dfps.service.placement.dto.CommonApplicationShortFormDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.ShortFormCsaEpisodeIncdntsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormMedicationDto;
import us.tx.state.dfps.service.placement.dto.ShortFormPsychiatricDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSiblingsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormTherapyDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationShortDaoImpl implemented all operation defined
 * in CommonApplicationShortDao Interface to fetch the records from table which
 * are mapped
 * 
 * © 2017 Texas Department ofFamily and Protective Services
 */

@Repository
@SuppressWarnings("unchecked")
public class CommonApplicationShortFormDaoImpl implements CommonApplicationShortFormDao {

	
	@Value("${commonAplicationDaoImpl.getFceEligibilityDtls}")
	String getFceEligibilityDtlsSql;
	
	@Value("${commonAplicationDaoImpl.getServiceLevelInfo}")
	String getServiceLevelInfoSql;
	
	@Value("${CommonApplicationShortFormDaoImpl.getCSAEpisodeIncidentsDtl}")
	String getCSAEpisodeIncidentsSql;
	
	@Value("${CommonApplicationShortFormDaoImpl.getSFTraumaHistory}")
	String getSFTraumaHistorySql;
	
	@Value("${CommonApplicationShortFormDaoImpl.getSFTraffickingHistory}")
	String getSFTraffickingHistorySql;
	
	@Value("${CommonApplicationShortFormDaoImpl.getSFEpisodeIncidents}")
	String getSFEpisodeIncidentsSql;
	
	@Value("${CommonApplicationShortFormDaoImpl.getRemovalDate}")
	String getRemovalDate;
	
	@Value("${CommonApplicationShortFormDaoImpl.getSiblingsList}")
	String getSFSiblingsSql;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private FceDao fceDao;
	
	@Autowired
	CommonApplicationDao commonDao;
	
	private static final Logger logger = Logger.getLogger(CommonApplicationShortFormDaoImpl.class);
	
	private static final String SSCC_REFERRAL_SQL = "from SsccReferral where idStage= :idStage and idPerson= :idPerson and dtDischargeActual is null order by dtRecorded desc";
	
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public CaExCommonApplication retrieveCommonById(Long idCaExCommonApplication) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExCommonApplication.class)
				.add(Restrictions.eq("idCaExCommonApplication", idCaExCommonApplication));
		CaExCommonApplication commonApp = (CaExCommonApplication) criteria.uniqueResult();
		return commonApp;
	}

	@Override
	public CaExCommonApplication retrieveCommonByEventId(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExCommonApplication.class)
				.add(Restrictions.eq("idEvent", idEvent));
		CaExCommonApplication commonApp = (CaExCommonApplication) criteria.uniqueResult();
		return commonApp;
	}
	
	@Override
	public List<CaExPlacementLog> retrievePlacementLogByExId(Long idCaExCommonApplication) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExPlacementLog.class);
		criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaExCommonApplication));
		List<CaExPlacementLog> placementLogList = (List<CaExPlacementLog>) criteria.list();
		return placementLogList;
	}
	
	@Override
	public List<CaExHospitalization> retrieveHospitalizationByExId(Long idCaExCommonApplication) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExHospitalization.class);
		criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaExCommonApplication));
		List<CaExHospitalization> hospitalizationList = (List<CaExHospitalization>) criteria.list();
		return hospitalizationList;
	}
	
	
	
	@Override
	public FceEligibilityDto getFceEligibility(Long idPerson) {
		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		fceEligibilityDto = (FceEligibilityDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFceEligibilityDtlsSql).setParameter("idPerson", idPerson))
						.addScalar("idFceEligibility", StandardBasicTypes.LONG)
						.addScalar("idFceApplication", StandardBasicTypes.LONG)
						.addScalar("idFceReview", StandardBasicTypes.LONG)
						.addScalar("idFcePerson", StandardBasicTypes.LONG)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idEligibilityEvent", StandardBasicTypes.LONG)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("cdEligibilityActual", StandardBasicTypes.STRING)
						.addScalar("cdEligibilitySelected", StandardBasicTypes.STRING)
						.addScalar("cdMedicaidEligibilityType", StandardBasicTypes.STRING)
						.addScalar("cdPersonCitizenship", StandardBasicTypes.STRING)
						.addScalar("cdBlocChild", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FceEligibilityDto.class)).uniqueResult();

		return fceEligibilityDto;
	}
	
	
	@Override
	public List<SexualVictimIncidentDto> getSFSexualVictimization(Long idCaEx) {
		List<SexualVictimIncidentDto> sfSexualVictimList = new ArrayList<SexualVictimIncidentDto>();
		sfSexualVictimList = (List<SexualVictimIncidentDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSFTraumaHistorySql).setParameter("idCaEx", idCaEx))
						.addScalar("idIncident", StandardBasicTypes.LONG)
						.addScalar("dtIncident", StandardBasicTypes.DATE)
						.addScalar("indApproxDate", StandardBasicTypes.STRING)
						.addScalar("victimComments", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SexualVictimIncidentDto.class)).list();
		return sfSexualVictimList;
	}
	
	@Override
	public List<TraffickingDto> getSFTraffickingHistory(Long idCaEx) {
		List<TraffickingDto> sfTraffickingList = new ArrayList<TraffickingDto>();
		sfTraffickingList = (List<TraffickingDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSFTraffickingHistorySql).setParameter("idCaEx", idCaEx))
						.addScalar("idTrfckngDtl", StandardBasicTypes.LONG)
						.addScalar("dtOfIncdnt", StandardBasicTypes.DATE)
						.addScalar("indApproxDate", StandardBasicTypes.STRING)
						.addScalar("txtVictimizationComments", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(TraffickingDto.class)).list();
		return sfTraffickingList;
	}
	
	@Override
	public List<ShortFormCsaEpisodeIncdntsDto> getSFEpisodeIncidents(Long idCaEx) {
		List<ShortFormCsaEpisodeIncdntsDto> sfSexualizedBehvList = new ArrayList<ShortFormCsaEpisodeIncdntsDto>();
		sfSexualizedBehvList = (List<ShortFormCsaEpisodeIncdntsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSFEpisodeIncidentsSql).setParameter("idCaEx", idCaEx))
						.addScalar("dtEpisodeSt", StandardBasicTypes.DATE)
						.addScalar("dtEpisodeEnd", StandardBasicTypes.DATE)
						.addScalar("dtIncdnt", StandardBasicTypes.DATE)
						.addScalar("indAppxDt", StandardBasicTypes.STRING)
						.addScalar("txtIncdntDesc", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ShortFormCsaEpisodeIncdntsDto.class)).list();
		return sfSexualizedBehvList;
	}
	
	@Override
	public List<ShortFormCsaEpisodeIncdntsDto> getCSAEpisodeIncdntDtls(Long idPerson) {
		List<ShortFormCsaEpisodeIncdntsDto> csaEpisodeIncdntsDtoList = new ArrayList<ShortFormCsaEpisodeIncdntsDto>();
		csaEpisodeIncdntsDtoList = (List<ShortFormCsaEpisodeIncdntsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCSAEpisodeIncidentsSql).setParameter("idPerson", idPerson))
						.addScalar("dtEpisodeSt", StandardBasicTypes.DATE)
						.addScalar("dtIncdnt", StandardBasicTypes.DATE)
						.addScalar("indAppxDt", StandardBasicTypes.STRING)
						.addScalar("txtIncdntDesc", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ShortFormCsaEpisodeIncdntsDto.class)).list();
		return csaEpisodeIncdntsDtoList;
	}
	
	@Override
	public CnsrvtrshpRemovalDto getRemovalDate(Long idPerson) {
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto = new CnsrvtrshpRemovalDto();
		cnsrvtrshpRemovalDto = (CnsrvtrshpRemovalDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRemovalDate).setParameter("idPerson", idPerson))
						.addScalar("dtRemoval", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class)).uniqueResult();
		return cnsrvtrshpRemovalDto;
	}
	
	
	@Override
	public FceApplicationDto getRemovalAddr(Long personId){
		
		FceApplicationDto fceApplicationDto = null;
		FceEligibilityDto fceEligibilityDto = commonDao
				.getFceEligibility(personId);
		if (!ObjectUtils.isEmpty(fceEligibilityDto)) {
			fceApplicationDto = getFceApplicationDto(
					fceDao.getFceApplication(fceEligibilityDto.getIdFceApplication()));
		}
		
		return fceApplicationDto;
	}
	
	private FceApplicationDto getFceApplicationDto(FceApplication fceApplication) {
		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		if (!ObjectUtils.isEmpty(fceApplication)) {
			fceApplicationDto.setIdFceApplication(fceApplication.getIdFceApplication());
			fceApplicationDto.setIdEvent(fceApplication.getEvent().getIdEvent());
			fceApplicationDto.setIdPerson(fceApplication.getPersonByIdPerson().getIdPerson());
			fceApplicationDto.setIdFceEligibility(fceApplication.getFceEligibility().getIdFceEligibility());
			fceApplicationDto.setAddrRemovalStLn1(fceApplication.getAddrRemovalStLn1());
			fceApplicationDto.setAddrRemovalStLn2(fceApplication.getAddrRemovalStLn2());
			fceApplicationDto.setAddrRemovalCity(fceApplication.getAddrRemovalCity());
			fceApplicationDto.setAddrRemovalAddrZip(fceApplication.getAddrRemovalAddrZip());
			//code changes made for 61082
			fceApplicationDto.setCdRemovalAddrState(fceApplication.getCdRemovalAddrState());
			fceApplicationDto.setCdRemovalAddrCounty(fceApplication.getCdRemovalAddrCounty());
		}
		return fceApplicationDto;
	}
	
	@Override
	public PersonLocDto getServiceLevelInfo(Long idPerson) {
		PersonLocDto serviceLvlpersonLocDto = new PersonLocDto();
		serviceLvlpersonLocDto = (PersonLocDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getServiceLevelInfoSql).setParameter("idPerson", idPerson))
						.addScalar("idPlocEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("dtPlocEnd", StandardBasicTypes.DATE)
						.addScalar("cdPlocChild", StandardBasicTypes.STRING)
						.addScalar("cdPlocType", StandardBasicTypes.STRING)
						.addScalar("dtPlocStart", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(PersonLocDto.class)).uniqueResult();
		return serviceLvlpersonLocDto;
	}
	

	@Override
	public CommonAppShortFormRes saveShortFormData(CommonAppShortFormReq req) {
		
		
		Event event = new Event();
		Stage stage = new Stage();
		Person person = new Person();
		
		CommonAppShortFormRes response = new CommonAppShortFormRes();
		
		CaExCommonApplication shortForm = new CaExCommonApplication();
		
		if(null != req.getCaExCommonApplicationId() && req.getCaExCommonApplicationId() >0){
			retrieveEventDtls(req, event, stage, person, shortForm);
		}else{
			createEventDtls(req, event, stage, person, shortForm);
		}
		Long idCaExCommonAppShortForm = saveShortForm(req, shortForm);
		
		if(null != shortForm.getIdCaExCommonApplication() && shortForm.getIdCaExCommonApplication() > 0){
			
			deleteTraumaHistory(shortForm);
			deleteSplProgramming(shortForm);
			deleteTraffickingHistory(shortForm);
			deleteRunaway(shortForm);
			deleteSexualizedBehv(shortForm);
			deleteEducation(shortForm);
			deleteMedication(shortForm);
			deleteSiblings(shortForm);
			deleteServicesProvided(shortForm);
			deleteSubstance(shortForm);
			deleteHospitalization(shortForm);
			
			saveTraumaHistory(req, shortForm);
			saveSpecialProgramming(req, shortForm);
			saveTraffickingHistory(req, shortForm);
			saveReturnRunaway(req, shortForm);
			saveSexualizedBehv(req, shortForm);
			saveEducationService(req, shortForm);
			saveMedication(req, shortForm);
			saveSiblings(req, shortForm);
			saveServicesProvided(req, shortForm);
			saveSubstanceUse(req, shortForm);
			saveHospitalization(req, shortForm);
			
			if(ServiceConstants.EVENTSTATUS_COMPLETE.equals(req.getEventStatus()) || ServiceConstants.EVENTSTATUS_APPROVE.equals(req.getEventStatus())){
				savePlacement(req, shortForm);
			}
			
		}
		CommonApplicationShortFormDto dto = new CommonApplicationShortFormDto();
				dto.setIdCaExCommonApplication(idCaExCommonAppShortForm);
				response.setCommonApplicationShortFormDto(dto);
		
		return response;
		
	}

	private void saveSexualizedBehv(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getEpisodeIncidentsDtls() && req.getEpisodeIncidentsDtls().size() > 0){
			for(ShortFormCsaEpisodeIncdntsDto incidentDto : req.getEpisodeIncidentsDtls()){
				try{
					if(("" != incidentDto.getTxtIncdntDesc() && null != incidentDto.getTxtIncdntDesc()) || null != incidentDto.getIndAppxDt() || null != incidentDto.getDtEpisodeSt() || null != incidentDto.getDtEpisodeEnd() || null != incidentDto.getDtIncdnt()){
						CaExSexualBehavior episodeIncHis = new CaExSexualBehavior();
						
						episodeIncHis.setCaExCommonApplication(shortForm);
						episodeIncHis.setIdCreatedPerson(req.getCreateUserId());
						episodeIncHis.setIdLastUpdatePerson(req.getCreateUserId());
						episodeIncHis.setDtCreated(new Date());
						episodeIncHis.setDtLastUpdate(new Date());
						episodeIncHis.setDtIncidentStart(incidentDto.getDtEpisodeSt());
						episodeIncHis.setDtIncidentEnd(incidentDto.getDtEpisodeEnd());
						episodeIncHis.setDtIncident(incidentDto.getDtIncdnt());
						if(null != incidentDto.getIndAppxDt() && ServiceConstants.YES.equals(incidentDto.getIndAppxDt())){
							episodeIncHis.setIndApproxDate(incidentDto.getIndAppxDt());
						}else{
							episodeIncHis.setIndApproxDate(ServiceConstants.NO);
						}
						episodeIncHis.setTxtIncident(incidentDto.getTxtIncdntDesc());
						
						sessionFactory.getCurrentSession().save(episodeIncHis);	
					}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
		
	}

	private void saveTraffickingHistory(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getTraffickingList() && req.getTraffickingList().size() > 0){
			for(TraffickingDto traffickingDto : req.getTraffickingList()){
				try{
					if(("" != traffickingDto.getTxtVictimizationComments() && null != traffickingDto.getTxtVictimizationComments()) || null != traffickingDto.getIndApproxDate() || null != traffickingDto.getDtOfIncdnt()){
						CaExTraffickingHistory traffickingHis = new CaExTraffickingHistory();
						
						traffickingHis.setCaExCommonApplication(shortForm);
						traffickingHis.setIdCreatedPerson(req.getCreateUserId());
						traffickingHis.setIdLastUpdatePerson(req.getCreateUserId());
						traffickingHis.setDtCreated(new Date());
						traffickingHis.setDtLastUpdate(new Date());
						traffickingHis.setDtIncident(traffickingDto.getDtOfIncdnt());
						if(null != traffickingDto.getIndApproxDate() && ServiceConstants.YES.equals(traffickingDto.getIndApproxDate())){
							traffickingHis.setIndApproxDate(traffickingDto.getIndApproxDate());
						}else{
							traffickingHis.setIndApproxDate(ServiceConstants.NO);
						}
						traffickingHis.setTxtIncident(traffickingDto.getTxtVictimizationComments());
						
						sessionFactory.getCurrentSession().save(traffickingHis);
					}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
		
	}

	private void saveTraumaHistory(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getIncidents() && req.getIncidents().size() > 0){
			for(SexualVictimIncidentDto sexualVictimDto : req.getIncidents()){
				try{
					if(("" != sexualVictimDto.getVictimComments() && null != sexualVictimDto.getVictimComments() )|| null != sexualVictimDto.getIndApproxDate() || null != sexualVictimDto.getDtIncident()){
						CaExTraumaHistory traumaHis = new CaExTraumaHistory();
						
						traumaHis.setCaExCommonApplication(shortForm);
						traumaHis.setIdCreatedPerson(req.getCreateUserId());
						traumaHis.setIdLastUpdatePerson(req.getCreateUserId());
						traumaHis.setDtCreated(new Date());
						traumaHis.setDtLastUpdate(new Date());
						traumaHis.setDtIncident(sexualVictimDto.getDtIncident());
						if(null != sexualVictimDto.getIndApproxDate() && ServiceConstants.YES.equals(sexualVictimDto.getIndApproxDate())){
							traumaHis.setIndApproxDate(sexualVictimDto.getIndApproxDate());
						}else{
							traumaHis.setIndApproxDate(ServiceConstants.NO);
						}
						traumaHis.setTxtIncident(sexualVictimDto.getVictimComments());
						
						sessionFactory.getCurrentSession().save(traumaHis);	
					}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
		
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void savePlacement(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		List<PlacementDtlDto> plcmntLogDtlLst = commonDao
				.getPlacementLogDtl(req.getPersonId());
		
		if(null != plcmntLogDtlLst && plcmntLogDtlLst.size() > 0){
			for(PlacementDtlDto dto : plcmntLogDtlLst){
				try{
					CaExPlacementLog plcmntLog = new CaExPlacementLog();
					
					plcmntLog.setCaExCommonApplication(shortForm);
					plcmntLog.setNmPlacement(dto.getNmPlcmtFacil());
					plcmntLog.setCdPlacementType(dto.getCdPlcmtType());
					plcmntLog.setCdPlcmtLivArr(dto.getCdPlcmtLivArr());
					plcmntLog.setDtStart(dto.getDtPlcmtStart());
					plcmntLog.setDtEnd(dto.getDtPlcmtEnd());
					plcmntLog.setIdCreatedPerson(req.getCreateUserId());
					plcmntLog.setIdLastUpdatePerson(req.getCreateUserId());
					plcmntLog.setDtCreated(new Date());
					plcmntLog.setDtLastUpdate(new Date());
					
					
					sessionFactory.getCurrentSession().save(plcmntLog);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveHospitalization(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getPsychiatricList() && req.getPsychiatricList().size() > 0){
			for(ShortFormPsychiatricDto psychDto : req.getPsychiatricList()){
				try{
					if(null != psychDto.getDtHospitalized() || "" != psychDto.getLenHospitalized() || "" != psychDto.getBehvAdmitted()){
						CaExHospitalization psych = new CaExHospitalization();
						
						psych.setCaExCommonApplication(shortForm);
						psych.setIdCreatedPerson(req.getCreateUserId());
						psych.setIdLastUpdatePerson(req.getCreateUserId());
						psych.setDtCreated(new Date());
						psych.setDtLastUpdate(new Date());
						/*try {
							psych.setDtHospitalization(df1.parse(psychDto.getDtHospitalized()));
						} catch (ParseException e) {
							logger.warn(e.getMessage());
						}*/
						psych.setDtHospitalization(psychDto.getDtHospitalized());
						psych.setTxtLengthOfHospitalization(psychDto.getLenHospitalized());
						psych.setTxtAdmittedForBehavior(psychDto.getBehvAdmitted());
						
						sessionFactory.getCurrentSession().save(psych);	
					}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveSubstanceUse(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getSubstanceUse()){
			
			if(null != req.getSubstanceUse().getIndAlcohol() && "" != req.getSubstanceUse().getIndAlcohol()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_ALC);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndAlcohol());
					if(null != req.getSubstanceUse().getAlcoholFrstUse() && "" != req.getSubstanceUse().getAlcoholFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getAlcoholFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getAlcoholFreq());
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getAlcoholLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndMarijuana() && "" != req.getSubstanceUse().getIndMarijuana()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_MRJ);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndMarijuana());
					if(null != req.getSubstanceUse().getMarijuanaFrstUse() && "" != req.getSubstanceUse().getMarijuanaFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getMarijuanaFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getMarijuanaFreq());
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getMarijuanaLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndInhalant() && "" != req.getSubstanceUse().getIndInhalant()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_INH);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndInhalant());
					if(null != req.getSubstanceUse().getInhalantFrstUse() && "" != req.getSubstanceUse().getInhalantFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getInhalantFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getInhalantFreq());
					if(null != req.getSubstanceUse().getInhalantLastUse() && !"".equals(req.getSubstanceUse().getInhalantLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getInhalantLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndCocaine() && "" != req.getSubstanceUse().getIndCocaine()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_COC);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndCocaine());
					if(null != req.getSubstanceUse().getCocaineFrstUse() && "" != req.getSubstanceUse().getCocaineFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getCocaineFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getCocaineFreq());
					if(null != req.getSubstanceUse().getCocaineLastUse() && !"".equals(req.getSubstanceUse().getCocaineLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getCocaineLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndHeroin() && "" != req.getSubstanceUse().getIndHeroin()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_HRN);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndHeroin());
					if(null != req.getSubstanceUse().getHeroinFrstUse() && "" != req.getSubstanceUse().getHeroinFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getHeroinFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getHeroinFreq());
					if(null != req.getSubstanceUse().getHeroinLastUse() && !"".equals(req.getSubstanceUse().getHeroinLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getHeroinLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndMeth() && "" != req.getSubstanceUse().getIndMeth()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_MTH);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndMeth());
					if(null != req.getSubstanceUse().getMethFrstUse() && "" != req.getSubstanceUse().getMethFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getMethFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getMethFreq());
					if(null != req.getSubstanceUse().getMethLastUse() && !"".equals(req.getSubstanceUse().getMethLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getMethLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndEcstasy() && "" != req.getSubstanceUse().getIndEcstasy()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_ECS);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndEcstasy());
					if(null != req.getSubstanceUse().getEcstasyFrstUse() && "" != req.getSubstanceUse().getEcstasyFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getEcstasyFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getEcstasyFreq());
					if(null != req.getSubstanceUse().getEcstasyLastUse() && !"".equals(req.getSubstanceUse().getEcstasyLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getEcstasyLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndOpioids() && "" != req.getSubstanceUse().getIndOpioids()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_OPI);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndOpioids());
					if(null != req.getSubstanceUse().getOpioidsFrstUse() && "" != req.getSubstanceUse().getOpioidsFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getOpioidsFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getOpioidsFreq());
					if(null != req.getSubstanceUse().getOpioidsLastUse() && !"".equals(req.getSubstanceUse().getOpioidsLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getOpioidsLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
			if(null != req.getSubstanceUse().getIndDrugs() && "" != req.getSubstanceUse().getIndDrugs()){
				try{
					CaExSubstanceUse substanceUse = new CaExSubstanceUse();
				
					createSubstanceUseBean(req, shortForm, substanceUse);
					substanceUse.setCdSubstanceType(ServiceConstants.SF_SUBSTANCE_DRG);
					substanceUse.setIndSubstanceUse(req.getSubstanceUse().getIndDrugs());
					if(null != req.getSubstanceUse().getDrugsFrstUse() && "" != req.getSubstanceUse().getDrugsFrstUse()){
						substanceUse.setNbrAgeFirstUse(Long.valueOf(req.getSubstanceUse().getDrugsFrstUse()));
					}
					substanceUse.setTxtFrequencyOfUse(req.getSubstanceUse().getDrugsFreq());
					if(null != req.getSubstanceUse().getDrugsLastUse() && !"".equals(req.getSubstanceUse().getDrugsLastUse())){
					try {
						substanceUse.setDtUseLast(df.parse(req.getSubstanceUse().getDrugsLastUse()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}
					
					sessionFactory.getCurrentSession().save(substanceUse);	
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
				
			}
			
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 * @param substanceUse
	 */
	private void createSubstanceUseBean(CommonAppShortFormReq req, CaExCommonApplication shortForm,
			CaExSubstanceUse substanceUse) {
		substanceUse.setCaExCommonApplication(shortForm);
		substanceUse.setIdCreatedPerson(req.getCreateUserId());
		substanceUse.setIdLastUpdatePerson(req.getCreateUserId());
		substanceUse.setDtCreated(new Date());
		substanceUse.setDtLastUpdate(new Date());
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveServicesProvided(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getTherapyList() && req.getTherapyList().size() > 0){
			for(ShortFormTherapyDto therapyDto : req.getTherapyList()){
				try{
					if(("" != therapyDto.getAgencyName() && null != therapyDto.getAgencyName())  || null != therapyDto.getTherapy() || null != therapyDto.getDtStart() || "" != therapyDto.getFrequency() || ("" != therapyDto.getOtherTxt() && null != therapyDto.getOtherTxt())){
						CaExServicesProvided srvProvided = new CaExServicesProvided();
						
						srvProvided.setCaExCommonApplication(shortForm);
						srvProvided.setIdCreatedPerson(req.getCreateUserId());
						srvProvided.setIdLastUpdatePerson(req.getCreateUserId());
						srvProvided.setDtCreated(new Date());
						srvProvided.setDtLastUpdate(new Date());
						srvProvided.setNmAgencyProvidedService(therapyDto.getAgencyName());
						srvProvided.setCdTherapyType(therapyDto.getTherapy());
						srvProvided.setDtStartService(therapyDto.getDtStart());
						//srvProvided.set
						/*if(null != therapyDto.getDtStart()){
							try {
								srvProvided.setDtStartService(therapyDto.getDtStart());
							} catch (ParseException e) {
								logger.warn(e.getMessage());
							}
							}*/
						srvProvided.setTxtFrequencyOfTherapy(therapyDto.getFrequency());
						srvProvided.setTxtOtherTherapy(therapyDto.getOtherTxt());
						
						sessionFactory.getCurrentSession().save(srvProvided);	
					}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveSiblings(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getSiblingsList() && req.getSiblingsList().size() > 0){
			for(ShortFormSiblingsDto siblingDto : req.getSiblingsList()){
				try{
					if("" != siblingDto.getSiblingsName() || null != siblingDto.getDfpsCare() || null != siblingDto.getDob() || "" != siblingDto.getAddress()){
						CaExSiblings sibling = new CaExSiblings();
						
						sibling.setCaExCommonApplication(shortForm);
						sibling.setIdCreatedPerson(req.getCreateUserId());
						sibling.setIdLastUpdatePerson(req.getCreateUserId());
						sibling.setDtCreated(new Date());
						sibling.setDtLastUpdate(new Date());
						sibling.setNmSibling(siblingDto.getSiblingsName());
						sibling.setIndInDfpsCare(siblingDto.getDfpsCare());
						sibling.setDtBirthSibling(siblingDto.getDob());
						/*if(null != siblingDto.getDob()){
							try {
								//sibling.setDtBirthSibling(df.parse(siblingDto.getDob()));
								
							} catch (ParseException e) {
								logger.warn(e.getMessage());
							}
							}*/
						sibling.setTxtAddress(siblingDto.getAddress());
						sibling.setIdPerson(siblingDto.getPersonId());
						
						sessionFactory.getCurrentSession().save(sibling);
					}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveMedication(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		
		if(null != req.getMedList() && req.getMedList().size() > 0){
			for(ShortFormMedicationDto medDto : req.getMedList()){
				try{
					if("" != medDto.getMedName() || "" != medDto.getDosage() || "" != medDto.getFrequency() || null != medDto.getDtPrescribed() || "" != medDto.getCondition()){
					CaExMedication med = new CaExMedication();
					
					med.setCaExCommonApplication(shortForm);
					med.setIdCreatedPerson(req.getCreateUserId());
					med.setIdLastUpdatePerson(req.getCreateUserId());
					med.setDtCreated(new Date());
					med.setDtLastUpdate(new Date());
					med.setNmMedication(medDto.getMedName());
					med.setTxtDosage(medDto.getDosage());
					med.setTxtFrequency(medDto.getFrequency());
					med.setDtPrescribed(medDto.getDtPrescribed());
					
					/*if(null != medDto.getDtPrescribed()){
					try {
						//med.setDtPrescribed(df1.parse(medDto.getDtPrescribed()));
					} catch (ParseException e) {
						logger.warn(e.getMessage());
					}
					}*/
					med.setTxtTreatingCondition(medDto.getCondition());
					
					sessionFactory.getCurrentSession().save(med);
				}
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
		}
		
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveEducationService(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getEduReq()){
			if(null != req.getEduReq().getRegularClass() && !req.getEduReq().getRegularClass().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_REGCLS);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getBilingualESL() && !req.getEduReq().getBilingualESL().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_BILSSL);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getGiftedTalented() && !req.getEduReq().getGiftedTalented().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_GIFTED);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getSelfContained() && !req.getEduReq().getSelfContained().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_SLFCNT);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getSplTransport() && !req.getEduReq().getSplTransport().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_SPLTRAN);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getCreditRecovery() && !req.getEduReq().getCreditRecovery().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_CRDRECV);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getVocational() && !req.getEduReq().getVocational().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_VOCTNL);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getCounselingSrv() && !req.getEduReq().getCounselingSrv().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_CNSLSVC);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getAdvPlacement() && !req.getEduReq().getAdvPlacement().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_ADVPLC);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getSplEducation() && !req.getEduReq().getSplEducation().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_SPLEDU);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getDaepJjaep() && !req.getEduReq().getDaepJjaep().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_DAEP);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getOtherSpec() && !req.getEduReq().getOtherSpec().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_OTH);
					educationSrv.setTxtOtherEducationService(req.getEduReq().getOtherTxt());
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getSelfPaced() && !req.getEduReq().getSelfPaced().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_SLFPCD);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getEduReq().getModification504() && !req.getEduReq().getModification504().isEmpty()){
				try{
					CaExEducationService educationSrv = new CaExEducationService();
					
					createEducationSrvBean(req, shortForm, educationSrv);
					educationSrv.setCdEducationServiceType(ServiceConstants.SF_EDU_504MOD);
					
					sessionFactory.getCurrentSession().save(educationSrv);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 * @param educationSrv
	 */
	private void createEducationSrvBean(CommonAppShortFormReq req, CaExCommonApplication shortForm,
			CaExEducationService educationSrv) {
		educationSrv.setCaExCommonApplication(shortForm);
		educationSrv.setIdCreatedPerson(req.getCreateUserId());
		educationSrv.setIdLastUpdatePerson(req.getCreateUserId());
		educationSrv.setDtCreated(new Date());
		educationSrv.setDtLastUpdate(new Date());
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveReturnRunaway(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getRunawayReq()){
			
			if(null != req.getRunawayReq().getVoluntary()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_VOLTRY);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getTycStaff()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_TYCSTF);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getFacilityStaff()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_FACSTF);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getJpcStaff()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_JPCSTF);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getCpsStaff()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_CPSSTF);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getIcjStaff()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_ICJSTF);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getLocalLawEnforcement()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_LLENF);
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
			
			if(null != req.getRunawayReq().getOtherRunaway()){
				try{
					CaExReturnFromRunaway returnRunaway = new CaExReturnFromRunaway();
					
					createReturnRunawayBean(req, shortForm, returnRunaway);
					returnRunaway.setCdReturnFromRunaway(ServiceConstants.SF_RUNAWAY_OTH);
					if(null != req.getRunawayReq().getTxtRunaway() && "" != req.getRunawayReq().getTxtRunaway()){
						returnRunaway.setTxtOtherReturnRunaway(req.getRunawayReq().getTxtRunaway());
					}
					
					sessionFactory.getCurrentSession().save(returnRunaway);
				}catch(Exception e){
					logger.warn(e.getMessage());
				}
			}
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 * @param returnRunaway
	 */
	private void createReturnRunawayBean(CommonAppShortFormReq req, CaExCommonApplication shortForm,
			CaExReturnFromRunaway returnRunaway) {
		returnRunaway.setCaExCommonApplication(shortForm);
		returnRunaway.setIdCreatedPerson(req.getCreateUserId());
		returnRunaway.setIdLastUpdatePerson(req.getCreateUserId());
		returnRunaway.setDtCreated(new Date());
		returnRunaway.setDtLastUpdate(new Date());
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private void saveSpecialProgramming(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		if(null != req.getPregnant() && "" != req.getPregnant() && !req.getPregnant().isEmpty()){
			try{
				CaExSpecialProgramming caEx = new CaExSpecialProgramming();
				
				createSplProgBean(req, shortForm, caEx);
				caEx.setCdSpecializedProgramming(ServiceConstants.SF_SPL_PROG_PREG);
				
				sessionFactory.getCurrentSession().save(caEx);
			}catch(Exception e){
				logger.warn(e.getMessage());
			}
		}
		if(null != req.getParenting() && "" != req.getParenting() && !req.getParenting().isEmpty()){
			try{
				CaExSpecialProgramming caEx = new CaExSpecialProgramming();
				
				createSplProgBean(req, shortForm, caEx);
				caEx.setCdSpecializedProgramming(ServiceConstants.SF_SPL_PROG_PARE);
				
				sessionFactory.getCurrentSession().save(caEx);
			}catch(Exception e){
				logger.warn(e.getMessage());
			}
		}
		if(null != req.getNone() && "" != req.getNone() && !req.getNone().isEmpty()){
			try{
				CaExSpecialProgramming caEx = new CaExSpecialProgramming();
				
				createSplProgBean(req, shortForm, caEx);
				caEx.setCdSpecializedProgramming(ServiceConstants.SF_SPL_PROG_NONE);
				
				sessionFactory.getCurrentSession().save(caEx);
			}catch(Exception e){
				logger.warn(e.getMessage());
			}
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 * @param caEx
	 */
	private void createSplProgBean(CommonAppShortFormReq req, CaExCommonApplication shortForm,
			CaExSpecialProgramming caEx) {
		caEx.setCaExCommonApplication(shortForm);
		caEx.setIdCreatedPerson(req.getCreateUserId());
		caEx.setIdLastUpdatePerson(req.getCreateUserId());
		caEx.setDtCreated(new Date());
		caEx.setDtLastUpdate(new Date());
	}

	/**
	 * @param shortForm
	 */
	private void deleteHospitalization(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExHospitalization.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExHospitalization> hospitalizationList = (List<CaExHospitalization>) criteria.list();
			
			if(null != hospitalizationList && hospitalizationList.size() > 0){
				for(CaExHospitalization hospitalization : hospitalizationList){
					sessionFactory.getCurrentSession().delete(hospitalization);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shortForm
	 */
	private void deleteSubstance(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSubstanceUse.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExSubstanceUse> substanceUseList = (List<CaExSubstanceUse>) criteria.list();
			
			if(null != substanceUseList && substanceUseList.size() > 0){
				for(CaExSubstanceUse substanceUse : substanceUseList){
					sessionFactory.getCurrentSession().delete(substanceUse);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shortForm
	 */
	private void deleteServicesProvided(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExServicesProvided.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExServicesProvided> therapyList = (List<CaExServicesProvided>) criteria.list();
			
			if(null != therapyList && therapyList.size() > 0){
				for(CaExServicesProvided agencyProd : therapyList){
					sessionFactory.getCurrentSession().delete(agencyProd);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shortForm
	 */
	private void deleteSiblings(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSiblings.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExSiblings> siblingsList = (List<CaExSiblings>) criteria.list();
			
			if(null != siblingsList && siblingsList.size() > 0){
				for(CaExSiblings sibling : siblingsList){
					sessionFactory.getCurrentSession().delete(sibling);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shortForm
	 */
	private void deleteMedication(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExMedication.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExMedication> medication = (List<CaExMedication>) criteria.list();
			
			if(null != medication && medication.size() > 0){
				for(CaExMedication med : medication){
					sessionFactory.getCurrentSession().delete(med);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shortForm
	 */
	private void deleteEducation(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExEducationService.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExEducationService> educSrv = (List<CaExEducationService>) criteria.list();
			
			if(null != educSrv && educSrv.size() > 0){
				for(CaExEducationService edService : educSrv){
					sessionFactory.getCurrentSession().delete(edService);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shortForm
	 */
	private void deleteRunaway(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExReturnFromRunaway.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExReturnFromRunaway> returnRunawayList = (List<CaExReturnFromRunaway>) criteria.list();
			
			if(null != returnRunawayList && returnRunawayList.size() > 0){
				for(CaExReturnFromRunaway runaway : returnRunawayList){
					sessionFactory.getCurrentSession().delete(runaway);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	
	/**
	 * @param shortForm
	 */
	private void deleteTraumaHistory(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExTraumaHistory.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExTraumaHistory> traumaHistList = (List<CaExTraumaHistory>) criteria.list();
			
			if(null != traumaHistList && traumaHistList.size() > 0){
				for(CaExTraumaHistory traumaHist : traumaHistList){
					sessionFactory.getCurrentSession().delete(traumaHist);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * @param shortForm
	 */
	private void deleteSplProgramming(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSpecialProgramming.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExSpecialProgramming> splProgramming = (List<CaExSpecialProgramming>) criteria.list();
			
			if(null != splProgramming && splProgramming.size() > 0){
				for(CaExSpecialProgramming exSplProg : splProgramming){
					sessionFactory.getCurrentSession().delete(exSplProg);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * @param shortForm
	 */
	private void deleteTraffickingHistory(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExTraffickingHistory.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExTraffickingHistory> trafickingHistList = (List<CaExTraffickingHistory>) criteria.list();
			
			if(null != trafickingHistList && trafickingHistList.size() > 0){
				for(CaExTraffickingHistory traffickingHist : trafickingHistList){
					sessionFactory.getCurrentSession().delete(traffickingHist);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * @param shortForm
	 */
	private void deleteSexualizedBehv(CaExCommonApplication shortForm) {
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSexualBehavior.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", shortForm.getIdCaExCommonApplication()));
			List<CaExSexualBehavior> sexualizedBehvList = (List<CaExSexualBehavior>) criteria.list();
			
			if(null != sexualizedBehvList && sexualizedBehvList.size() > 0){
				for(CaExSexualBehavior sexualizedBehv : sexualizedBehvList){
					sessionFactory.getCurrentSession().delete(sexualizedBehv);
				}
			}
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param req
	 * @param shortForm
	 */
	private Long saveShortForm(CommonAppShortFormReq req, CaExCommonApplication shortForm) {
		Long idCaExCommonAppShortForm = 0L;
		shortForm.setIdStage(req.getStageId());
		
		
		//Header
		shortForm.setNmCase(req.getCaseName());
		shortForm.setNmChildPerson(req.getChildName());
		if(null != req.getDob()){
			try {
				shortForm.setDtBirthChild(df.parse(req.getDob()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		shortForm.setDtCompletedApplication(new Date());
		
		//Section1
		shortForm.setNmDfpsCaseWorker(req.getCaseWorker());
		shortForm.setNbrEmpUnitEmpIn(req.getUnit());
		shortForm.setNmDfpsSupervisor(req.getSupervisor());
		
		//Section2
		shortForm.setIdChildPerson(req.getPersonId());
		shortForm.setCdPersonSex(req.getGender());
		shortForm.setTxtGenderIdentification(req.getGenderIdentification());
		shortForm.setCdLegalRegion(req.getRegion());
		shortForm.setTxtPersonCitizenship(req.getCitizenship());
		shortForm.setTxtEthnicity(req.getEthnicity());
		shortForm.setTxtRace(req.getRace());
		shortForm.setTxtPersonLanguage(req.getLanguage());
		shortForm.setCdLegalCounty(req.getCounty());
		shortForm.setTxtRemovalAddress(req.getRemovalAddr());
		//code added for 61082
		shortForm.setRemovalAddrStLn1(req.getRemovalAddrStLn1());
		shortForm.setRemovalAddrStLn2(req.getRemovalAddrStLn2());
		shortForm.setRemovalAddrCity(req.getRemovalAddrCity());
		shortForm.setAddrRemovalAddrZip(req.getAddrRemovalAddrZip());
		shortForm.setCdRemovalAddrCounty(req.getCdRemovalAddrCounty());
		shortForm.setCdRemovalAddrState(req.getCdRemovalAddrState());
		shortForm.setNbrGcdLat(req.getNbrGcdLat());
		shortForm.setNbrGcdLong(req.getNbrGcdLong());
		shortForm.setCdAddrRtrn(req.getCdAddrRtrn());
		shortForm.setCdGcdRtrn(req.getCdGcdRtrn());
		shortForm.setIndValdtd(req.getIndValdtd());
		shortForm.setTxtMailbltyScore(req.getTxtMailbltyScore());
		shortForm.setNmCntry(req.getNmCntry());
		shortForm.setNmCnty(req.getNmCnty());
		shortForm.setDtValdtd(req.getDtValdtd()==null && ServiceConstants.YES.equals(req.getIndValdtd())?new Date():req.getDtValdtd());
		shortForm.setIndRmvlAddrDisabled(req.getIndRmvlAddrDisabled());
		
		if(null != req.getRemovalDt()){
			try {
				shortForm.setDtRemoval(df.parse(req.getRemovalDt()));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		shortForm.setTxtAuthorizedLevelOfCare(req.getLevelOfCare());

		shortForm.setCdServicePackage(req.getCdServicePackage());

		//Section Trauma history
		shortForm.setIndHistOfSexualVictim(req.getSexualVictim());
		shortForm.setIndUnconfirmedVictimHistory(req.getIndUnconfirmedVictimHistory());
		shortForm.setTxtTraumaAbuseNeglect(req.getTxtTraumaAbuseNeglect());
		shortForm.setTxtTraumaOthAbuseNeglect(req.getTxtTraumaOthAbuseNeglect());
		shortForm.setTxtTraumaOthTraumaticExp(req.getTxtTraumaOthTraumaticExp());
		//Section3
		shortForm.setIndSuspVictimSexTrfckng(req.getSuspSexTraff());
		shortForm.setIndCnfrmVictimSexTrfckng(req.getConfSexTraff());
		shortForm.setIndSuspVictimLaborTrfckng(req.getSuspLaborTraff());
		shortForm.setIndCnfrmVictimLaborTrfckng(req.getConfLaborTraff());
		shortForm.setTxtTrafficking(req.getSupportServices());
		
		//Section4
		//Sub-section1
		
		if(null != req.getMedicalExam()){
			try {
				shortForm.setDtHealthStepsMedExam(df.parse(req.getMedicalExam()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		if(null != req.getDentalExam()){
			try {
				shortForm.setDtHealthStepsDentalExam(df.parse(req.getDentalExam()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		if(null != req.getTbExam()){
			try {
				shortForm.setDtLastTbTest(df.parse(req.getTbExam()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		shortForm.setTxtAllergies(req.getListAllergies());
		shortForm.setTxtMedicationReactions(req.getListMedications());
		shortForm.setIndUsingAnyMeds(req.getTakingMedications());
		shortForm.setIndImmunizationsCurrent(req.getImmunizationCurrent());
		shortForm.setIndSelfMedicalConsenter(req.getMedicalConsenter());
		
		//Sub-section2		
		shortForm.setTxtDiagPhyHlthConditions(req.getPhysicalHlth());
		shortForm.setTxtSpecialistAppointments(req.getMedicalSpecialist());
		shortForm.setTxtDevelopmentalFunctioning(req.getYouthDevelopHis());
		shortForm.setIndEciServicesReceived(req.getEci());
		shortForm.setTxtEciServicesReceived(req.getEciDiagnosis());
		shortForm.setIndOtPtStTherapy(req.getOtPtSt());
		shortForm.setIndEncopresisPast90days(req.getEncopresis());
		shortForm.setIndEnuresisPast90days(req.getEnuresis());
		shortForm.setTxtEncopresisEnuresis(req.getDescEncopresis());
		
		//Sub-section3
		shortForm.setIndPmn(req.getPmn());
		shortForm.setTxtPmnDiagnosis(req.getPmnTreatment());
		shortForm.setTxtPmnMedSpecialist(req.getSpecialistContact());
		shortForm.setTxtPmnPrimaryHospital(req.getHospAddr());
		shortForm.setTxtNursingHours(req.getNursingHrs());
		shortForm.setTxtHomeHealthAgency(req.getHealthAgencyAddr());
		shortForm.setIndDmeRequired(req.getDme());
		shortForm.setTxtDme(req.getDmeSupplies());
		shortForm.setIndAmbulance(req.getAmbulance());
		shortForm.setIndDnr(req.getDnr());
		shortForm.setIndDeafHearingImpaired(req.getDeaf());
		shortForm.setTxtCommunicationType(req.getYouthCommunicate());
		shortForm.setIndBlindVisuallyImpaired(req.getBlind());
		shortForm.setTxtBlindVisuallyImpaired(req.getBlindSupport());
		
		//Sub-section4		
		shortForm.setTxtEmotionalStrengths(req.getEmotionalStrengths());
		shortForm.setTxtTraumaTriggers(req.getYouthTrauma());
		shortForm.setTxtTherapistImpressions(req.getTherapistImpression());
		shortForm.setTxtMhBhServices(req.getMentalBehvHlth());
		shortForm.setIndCansAssessment(req.getCans());
		if(null != req.getCansAssessment()){
			try {
				shortForm.setDtCansAssessment(df.parse(req.getCansAssessment()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		shortForm.setTxtCansRecommendations(req.getCansRecom());
		if(null != req.getPsychologyEval()){
			try {
				shortForm.setDtPsychologicalEvaluation(df.parse(req.getPsychologyEval()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		if(null != req.getPsychiatricEval()){
			try {
				shortForm.setDtPsychiatricEvaluation(df.parse(req.getPsychiatricEval()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		shortForm.setTxtCurrentDiagnosis(req.getCurrentDiagnosis());
		shortForm.setIndMhCrisisPast6months(req.getMetalCrisis());
		shortForm.setTxtMhCrisis(req.getDescMentalHlthCrisis());
		shortForm.setIndPsychiatricHospital(req.getPsychiatricHospital());
		
		//Section5
		shortForm.setIndSubstanceUse(req.getSubstance());
		
		//Section6
		shortForm.setIndCurrentPregnant(req.getPregCurr());
		shortForm.setTxtBabyDue(req.getBabyDue());
		shortForm.setTxtPlanForBaby(req.getBabyPlan());
		shortForm.setIndCurrentParent(req.getParentCurr());
		shortForm.setTxtChildReside(req.getChildrenReside());
		shortForm.setTxtParentingRole(req.getYouthRole());
		
		//Section7
		//Sub-section1
		shortForm.setIndSelfHarmingBehavior(req.getSelfHarmingBehv());
		shortForm.setTxtSelfHarmingBehavior(req.getDescBehvManaged());
		shortForm.setIndSuicidalAttempts(req.getSuicidalAttempts());
		
		//Sub-section2
		shortForm.setIndHistRunaway(req.getRunawayHis());
		shortForm.setTxtHistRunaway(req.getRunawayBehv());
		if(null != req.getDtLastEpisode() && "" != req.getDtLastEpisode()){
			try {
				shortForm.setDtLastRunaway(df.parse(req.getDtLastEpisode()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		shortForm.setTxtRunawayDetails(req.getDescRunAway());
		
		//Sub-section3
		shortForm.setIndHistSettingFires(req.getSettingFire());
		shortForm.setTxtHistSettingFires(req.getDescSettingFire());
		shortForm.setIndHistCrueltyToAnimals(req.getCrueltyAnimal());
		shortForm.setTxtHistCrueltyToAnimals(req.getDescCrueltyAnimails());
		shortForm.setIndOtherBehaviorProblems(req.getSignificantProb());
		shortForm.setTxtOtherBehaviorProblems(req.getDescBehv());
		
		//Section 8
		shortForm.setIndSexualAggresBehavior(req.getAggressiveBehv());
		shortForm.setTxtSexualAggresBehavior(req.getDescAggressiveBehv());
		shortForm.setIndSexualBehaviorProblem(req.getIndSexualBehaviorProblem());
		shortForm.setTxtSexualBehaviorProblem(req.getTxtSexualBehaviorProblem());

		//Section9
		shortForm.setIndCurrentlyEnrolSchool(req.getEnrolledInSchool());
		shortForm.setNmSchool(req.getSchoolName());
		shortForm.setTxtAddressSchool(req.getAddr());
		shortForm.setNmCitySchool(req.getCity());
		shortForm.setNmStateSchool(req.getState());
		if(null != req.getDtWithdrawn() && "" != req.getDtWithdrawn()){
			try {
				shortForm.setDtWithdrawn(df.parse(req.getDtWithdrawn()));
			} catch (ParseException e) {
				logger.warn(e.getMessage());
			}
		}
		shortForm.setNmSchoolContact(req.getContactName());
		if(null != req.getContactPhn() && !req.getContactPhn().isEmpty()){
			shortForm.setNbrSchoolContactPhone(Long.valueOf(req.getContactPhn()));
		}
		if(null != req.getContactPhnExt() && !req.getContactPhnExt().isEmpty()){
			shortForm.setNbrSchoolContactPhoneExt(Long.valueOf(req.getContactPhnExt()));
		}
		shortForm.setTxtSchoolContactEmail(req.getContactEmail());
		shortForm.setTxtCurrentGrade(req.getCurrentGrade());
		if(null != req.getAsOfDate() && "" != req.getAsOfDate()){
		try {
			shortForm.setDtCurrentGrade(df.parse(req.getAsOfDate()));
		} catch (ParseException e) {
			logger.warn(e.getMessage());
		}
		}
		shortForm.setIndOnGradeLevel(req.getGradeLevel());
		shortForm.setNmLastSchoolAttended(req.getLastSchlName());
		shortForm.setTxtLastSchoolAddress(req.getLastSchlAddr());
		shortForm.setTxtLastSchoolContact(req.getLastSchlCnct());
		shortForm.setIndHistoryOfTruancy(req.getTruancyHis());
		
		//Section10
		shortForm.setTxtLifeSkillProgress(req.getLifeSkills());
		shortForm.setIndLifeSkillAssessment(req.getLifeSkillAssessment());
		shortForm.setIndPalSkillTraining(req.getPalLifeSkills());
		shortForm.setTxtRegionalPalStaff(req.getYouthPalStaff());
		shortForm.setIndCircleOfSupport(req.getCircleOfSupport());
		shortForm.setTxtExtendedFcOptions(req.getFosterCareOption());
		
		//Section11
		shortForm.setIndJjInvolvement(req.getJuvenileHis());
		shortForm.setTxtJjInvolvement(req.getListArrests());
		
		//Section12
		shortForm.setIndPlacedOutOfHome(req.getOutOfHome());
		shortForm.setTxtPlacedOutOfHome(req.getDescOutOfHome());
		shortForm.setIndAdoptedDomestically(req.getAdoptedDomestically());
		shortForm.setTxtDomesticConsummated(req.getDescDomestically());
		shortForm.setIndAdoptedInternationally(req.getAdoptedInternationally());
		shortForm.setTxtInternConsummated(req.getDescInternationally());
		shortForm.setIndOtherLegalCustody(req.getLegalCustody());
		shortForm.setTxtOtherLegalCustody(req.getDescLegalCustody());

		if(null != req.getCaExCommonApplicationId() && req.getCaExCommonApplicationId() > 0){
			idCaExCommonAppShortForm = req.getCaExCommonApplicationId();
			sessionFactory.getCurrentSession().update(shortForm);
		}else{
			idCaExCommonAppShortForm = (Long)sessionFactory.getCurrentSession().save(shortForm);
		}
		
		return idCaExCommonAppShortForm;
	}

	/**
	 * @param req
	 * @param event
	 * @param stage
	 * @param person
	 * @param shortForm
	 */
	private void createEventDtls(CommonAppShortFormReq req, Event event, Stage stage, Person person,
			CaExCommonApplication shortForm) {
		stage.setIdStage(req.getStageId());
		person.setIdPerson(req.getCreateUserId());//employee id
		//person.setIdPerson(req.getPersonId());
		event.setStage(stage);
		event.setPerson(person);
		event.setCdEventType(ServiceConstants.SHORT_FORM_EVENT_TYPE);
		event.setCdEventStatus(req.getEventStatus());
		event.setCdTask(ServiceConstants.SHORT_FORM_TASK);
		event.setTxtEventDescr(ServiceConstants.SHORT_FORM_EVENT_DESC);
		event.setIdCase(req.getCaseId());
		event.setDtEventCreated(new Date());
		event.setDtLastUpdate(new Date());
		event.setDtEventOccurred(new Date());
		
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		
		Person p = new Person();
		p.setIdPerson(req.getPersonId());
		
		EventPersonLink personLink = new EventPersonLink();
		personLink.setDtLastUpdate(new Date());
		personLink.setPerson(p);
		personLink.setIdCase(req.getCaseId());
		personLink.setEvent(event);
		
		sessionFactory.getCurrentSession().saveOrUpdate(personLink);
		
		List<SsccReferral> ssccReferral = null;

		Query querySSCCReferral = sessionFactory.getCurrentSession().createQuery(SSCC_REFERRAL_SQL);
		querySSCCReferral.setParameter("idStage", req.getStageId());
		querySSCCReferral.setParameter("idPerson", req.getPersonId());
		querySSCCReferral.setMaxResults(100);
		ssccReferral = querySSCCReferral.list();
		/*********Uncomment once sscc referral is added******************************************/
		shortForm.setIdSsccReferral(ssccReferral.get(0).getIdSSCCReferral());
		//shortForm.setIdSsccReferral(3505l);
		shortForm.setIdEvent(event.getIdEvent());
		shortForm.setIdCreatedPerson(req.getCreateUserId());
		shortForm.setIdLastUpdatePerson(req.getCreateUserId());
		shortForm.setDtCreated(new Date());
		shortForm.setDtLastUpdate(new Date());
	}

	/**
	 * @param req
	 * @param event
	 * @param stage
	 * @param person
	 * @param shortForm
	 */
	private void retrieveEventDtls(CommonAppShortFormReq req, Event event, Stage stage, Person person,
			CaExCommonApplication shortForm) {
		stage.setIdStage(req.getStageId());
		person.setIdPerson(req.getCreateUserId());//employee id
		event.setStage(stage);
		event.setPerson(person);
		event.setIdEvent(req.getEventId());
		event.setCdEventType(ServiceConstants.SHORT_FORM_EVENT_TYPE);
		event.setCdEventStatus(req.getEventStatus());
		event.setCdTask(ServiceConstants.SHORT_FORM_TASK);
		event.setTxtEventDescr(ServiceConstants.SHORT_FORM_EVENT_DESC);
		event.setIdCase(req.getCaseId());
		event.setDtEventCreated(new Date());
		event.setDtLastUpdate(new Date());
		event.setDtEventOccurred(new Date());
		try{
		
			sessionFactory.getCurrentSession().saveOrUpdate(event);
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		shortForm.setIdCaExCommonApplication(req.getCaExCommonApplicationId());
		shortForm.setIdSsccReferral(req.getSsccReferralId());
		shortForm.setIdEvent(req.getEventId());
		shortForm.setIdCreatedPerson(req.getCreateUserId());
		shortForm.setIdLastUpdatePerson(req.getCreateUserId());
		shortForm.setDtCreated(new Date());
		shortForm.setDtLastUpdate(new Date());
	}

	@Override
	public List<CaExSpecialProgramming> retrieveSplProgById(Long idCaEx) {
		List<CaExSpecialProgramming> splProgramming = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSpecialProgramming.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			splProgramming = (List<CaExSpecialProgramming>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return splProgramming;
	}

	@Override
	public CommonAppShortFormRes approveShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto,
			Long approverId) {
		Event event = new Event();
		CommonAppShortFormRes response = new CommonAppShortFormRes();
		CaExCommonApplication caExCommonApplication = new CaExCommonApplication();
		Date date = new Date();
		if (!ObjectUtils.isEmpty(commonApplicationShortFormDto.getIdEvent())) {
				/* Update the event table with the new Status */
				Criteria cr = sessionFactory.getCurrentSession().createCriteria(Event.class)
						.add(Restrictions.eqOrIsNull("idEvent", commonApplicationShortFormDto.getIdEvent()));
				event = (Event) cr.uniqueResult();
				event.setCdEventStatus(ServiceConstants.EVENTSTATUS_APPROVE);
				event.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(event));

			}

			/* Update Ca_Ex_Common_Application Table */
			if (!ObjectUtils.isEmpty(commonApplicationShortFormDto.getIdCaExCommonApplication())) {
				Criteria cr = sessionFactory.getCurrentSession().createCriteria(CaExCommonApplication.class)
						.add(Restrictions.eqOrIsNull("idCaExCommonApplication",
								commonApplicationShortFormDto.getIdCaExCommonApplication()));
				caExCommonApplication = (CaExCommonApplication) cr.uniqueResult();
				caExCommonApplication.setCdAcknowledgeType(ServiceConstants.APPROVAL);
				caExCommonApplication.setIdLastUpdatePerson(approverId);
				caExCommonApplication.setDtAcknowledged(date);
				caExCommonApplication.setDtLastUpdate(date);
				sessionFactory.getCurrentSession()
						.saveOrUpdate(sessionFactory.getCurrentSession().merge(caExCommonApplication));
			}
	
		return response;
	}

	@Override
	public CommonAppShortFormRes rejectShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto,
			Long approverId, String rejectReason) {
		Event event = new Event();
		CommonAppShortFormRes response = new CommonAppShortFormRes();
		CaExCommonApplication caExCommonApplication = new CaExCommonApplication();
		
			
		if(null != commonApplicationShortFormDto.getIdEvent() && commonApplicationShortFormDto.getIdEvent()>0 ){
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(Event.class).add(Restrictions.eqOrIsNull("idEvent", commonApplicationShortFormDto.getIdEvent()));
			event = (Event)cr.uniqueResult();
			event.setCdEventStatus(ServiceConstants.EVENTSTATUS_PROC);
			event.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(event));
		}
		
		if(null != commonApplicationShortFormDto.getIdCaExCommonApplication() && commonApplicationShortFormDto.getIdCaExCommonApplication() > 0){
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(CaExCommonApplication.class).add(Restrictions.eqOrIsNull("idCaExCommonApplication", commonApplicationShortFormDto.getIdCaExCommonApplication()));
			caExCommonApplication = (CaExCommonApplication)cr.uniqueResult();
			caExCommonApplication.setCdAcknowledgeType(ServiceConstants.APPROVAL_REJECT);
			caExCommonApplication.setTxtRejectReason(rejectReason);
			caExCommonApplication.setIdLastUpdatePerson(approverId);
			caExCommonApplication.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(caExCommonApplication));
		}
		
		return response;
	}

	@Override
	public List<CaExReturnFromRunaway> retrieveReturnRunaway(Long idCaEx) {
		List<CaExReturnFromRunaway> returnRunaway = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExReturnFromRunaway.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			returnRunaway = (List<CaExReturnFromRunaway>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return returnRunaway;
	}
	
	@Override
	public List<CaExEducationService> retrieveEducationService(Long idCaEx) {
		List<CaExEducationService> educSrv = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExEducationService.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			educSrv = (List<CaExEducationService>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return educSrv;
	}
	
	@Override
	public List<CaExSiblings> retrieveSiblingList(Long idCaEx) {
		List<CaExSiblings> siblingLst = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSiblings.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			siblingLst = (List<CaExSiblings>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return siblingLst;
	}
	
	@Override
	public List<CaExMedication> retrieveMedicationLst(Long idCaEx) {
		List<CaExMedication> medicationList = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExMedication.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			medicationList = (List<CaExMedication>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return medicationList;
	}
	
	@Override
	public List<CaExServicesProvided> retrieveTherapyLst(Long idCaEx) {
		List<CaExServicesProvided> therapyList = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExServicesProvided.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			therapyList = (List<CaExServicesProvided>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return therapyList;
	}
	
	@Override
	public List<CaExSubstanceUse> retrieveSubstanceAbuse(Long idCaEx) {
		List<CaExSubstanceUse> substanceList = null;
		try{
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CaExSubstanceUse.class);
			criteria.add(Restrictions.eq("caExCommonApplication.idCaExCommonApplication", idCaEx));
			substanceList = (List<CaExSubstanceUse>) criteria.list();
		}catch(Exception e){
			logger.warn(e.getMessage());
		}
		
		return substanceList;
	}

	@Override
	public List<ShortFormSiblingsDto> retrieveSiblingListByExId(Long idCaEx) {
		List<ShortFormSiblingsDto> sfSiblingsList = new ArrayList<ShortFormSiblingsDto>();
		sfSiblingsList = (List<ShortFormSiblingsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSFSiblingsSql).setParameter("idCaEx", idCaEx))
						.addScalar("siblingsName", StandardBasicTypes.STRING)
						.addScalar("dfpsCare", StandardBasicTypes.STRING)
						.addScalar("dob", StandardBasicTypes.DATE)
						.addScalar("address", StandardBasicTypes.STRING)
						.addScalar("personId", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(ShortFormSiblingsDto.class)).list();
		return sfSiblingsList;
	}

}
