/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Sep 27, 2017- 2:13:27 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.stageprogression.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.BaseAddRiskAssmtValueDto;
import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.common.dto.SafetyAssmtDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.arstageprog.dao.ArStageProgDao;
import us.tx.state.dfps.service.casepackage.dao.CaseDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dto.SituationDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.BaseAddRiskAssmtRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.RiskAssmtValueDto;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.personmergesplit.dto.CaseValueDto;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyFactorDto;
import us.tx.state.dfps.service.stageprogression.service.StageProgressionService;
import us.tx.state.dfps.service.workload.dao.StageProgDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is
 * Implementation class for Stage Progression Ejb. Sep 27, 2017- 2:13:27 PM ©
 * 2017 Texas Department of Family and Protective Services
 * 
 * This is Implementation class for Stage Progression Ejb. This Session Bean has
 * methods that can be used to Create new Case and Stage and to copy person and
 * event information to new stage. This is common Stage progression class which
 * can be used for Stage Progression of any Stage.
 */

@Service
@Transactional
public class StageProgressionServiceimpl implements StageProgressionService {

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	ArStageProgDao arStageProgDao;

	@Autowired
	CaseDao caseDao;

	@Autowired
	SituationDao situationDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventService eventService;

	@Autowired
	RiskAssessmentDao riskAssessmentDao;

	private static final Logger log = Logger.getLogger(StageProgressionServiceimpl.class);

	/**
	 * 
	 * Method Name: createNewStage Method Description: This method creates new
	 * Stage from StageValueBean. StageValueBean object passed should be
	 * populated with Case and Stage Information.
	 * 
	 * @param stageValueBeanDto
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long createNewStage(StageValueBeanDto stageValueBeanDto) {
		StageValueBeanDto fromStageValBean = stageDao.retrieveStageInfoList(stageValueBeanDto.getIdStage());

		validateStageProgression(fromStageValBean, stageValueBeanDto.getCdStage());

		if (!TypeConvUtil.isNullOrEmpty(fromStageValBean)) {
			if (ServiceConstants.CSTAGES_AR.equals(fromStageValBean.getCdStage())
					|| ServiceConstants.CSTAGES_INT.equals(fromStageValBean.getCdStage())) {
				fromStageValBean.setCdStageReasonClosed(null);
			}
			// artf163908 - On FPR Creation, when INV has CRSR stage type then copy the same to FPR else use REG from DTO
			if (ServiceConstants.CSTAGES_INV.equals(fromStageValBean.getCdStage())
					&& !ObjectUtils.isEmpty(fromStageValBean.getCdStageType())
					&& ServiceConstants.CASE_REL_SPEC_REQ.equals(fromStageValBean.getCdStageType().substring(0, 2))) {
				stageValueBeanDto.setCdStageType(fromStageValBean.getCdStageType());
			}
		}
		fromStageValBean.setCdStage(stageValueBeanDto.getCdStage());
		Long newStageId = createNewStages(fromStageValBean, stageValueBeanDto);

		return newStageId;
	}

	/**
	 * 
	 * Method Name: validateStageProgression Method Description: This method
	 * validated if another stage is already open for given id.
	 * 
	 * @param stageValueBeanDto
	 * @param cdToStage
	 * @return @
	 */
	private boolean validateStageProgression(StageValueBeanDto fromStageValBeanDto, String cdToStage) {

		// Check if the Current Stage is already closed.
		if (fromStageValBeanDto.getDtStageClose() != null) {
			// return error if the stage is closed
			throw new ServiceLayerException(messageSource.getMessage("stageprog.stage.closed", null, Locale.US));
		}
		// Check if another Stage is Open for the case Id with new Stage Code.
		boolean anotherStageOpenFlag = stageProgDao.isAnotherStageOpen(fromStageValBeanDto.getIdCase(), cdToStage);

		if (anotherStageOpenFlag) {// Another stage is open
			throw new ServiceLayerException(messageSource.getMessage("stageprog.anotherstage.open", null, Locale.US));
		}

		return false;
	}

	/**
	 * 
	 * Method Name: createNewStage Method Description: This method creates new
	 * Stage from StageValueBean. StageValueBean object passed should be
	 * populated with Case and Stage Information.
	 * 
	 * @param stageValueBeanDto
	 * @return
	 */
	private Long createNewStages(StageValueBeanDto toStageValBean, StageValueBeanDto stageCreationValBean) {
		Long newStageId = 0l;

		// **** Stage Progression Steps **** //
		// 1. Insert into Stage.
		// If Stage Type(REG) passed use that instead of Stage Type from
		// Previous Stage.
		if (!TypeConvUtil.isNullOrEmpty(stageCreationValBean.getCdStageType())) {
			toStageValBean.setCdStageType(stageCreationValBean.getCdStageType());
		}
		toStageValBean.setDtStageCreated(new java.util.Date());
		toStageValBean.setDtStageStart(new java.util.Date());
		newStageId = stageDao.insertIntoStage(toStageValBean);

		// 2. Create new Event for new Stage.
		arStageProgDao.insertIntoEvent(toStageValBean, newStageId, stageCreationValBean.getIdCreatedPerson());

		// 3. Copy Primary Worker Information to new Stage.
		// Commenting below because all person roles will be linked with new
		// stage id from linkNonStaffPersonToNewStage function so not required.
		
		stageProgDao.linkPersonToNewStage(Long.valueOf(toStageValBean.getIdStage()), Long.valueOf(newStageId),
				ServiceConstants.CROLEALL_PR);
		 
		// 4. Copy Non Staff Information to New stage.
		stageProgDao.linkNonStaffPersonToNewStage(toStageValBean.getIdStage(), newStageId);

		// 5. Link New stage to Old Stage (entry into Stage_Link table).
		arStageProgDao.insertIntoStageLink(toStageValBean, newStageId);

		return newStageId;
	}

	/**
	 * Method Name: linkSecondaryWorkerToStage Method Description:This method
	 * links secondary worker to the stage by creating entry into Stage Person
	 * Link table.
	 * 
	 * @param idStage
	 * @param idSecWorker
	 * @return Long @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long linkSecondaryWorkerToStage(Long idStage, Long idSecWorker) {
		boolean alreadyAssigned = false;
		List<String> personRoles = new ArrayList<>();
		// First Check if the worker is already assigned to the stage.
		personRoles.add(ServiceConstants.CROLEALL_PR);
		personRoles.add(ServiceConstants.CROLEALL_SE);
		List<StagePersonValueDto> stagePersonList = new ArrayList<>();
		stagePersonList = stageProgDao.selectStagePersonLink(idStage.intValue(), personRoles);
		for (StagePersonValueDto stagePersonValueDto : stagePersonList) {
			//Modified the compare condition for warranty defect 11887
			if (stagePersonValueDto.getIdPerson().equals(idSecWorker)) {
				alreadyAssigned = true;
				break;
			}
		}
		if (alreadyAssigned == false) {
			stageProgDao.linkSecondaryWorkerToNewStage(idStage, idSecWorker);
		}
		return (long) stagePersonList.size();
	}

	/**
	 * Method Name: createNewCaseAndStage Method Description:This method creates
	 * new Case and Stage during Stage Progression.
	 * 
	 * @param stageValueBeanDto
	 * @return StageValueBeanDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public StageValueBeanDto createNewCaseAndStage(StageValueBeanDto stageValueBeanDto) {
		StageValueBeanDto newStageValBeanDto = new StageValueBeanDto();
		String cdToStageCode = stageValueBeanDto.getCdToStageCode();
		// Get Stage Information using idStage.
		StageValueBeanDto fromStageValBeanDto = stageDao.retrieveStageInfoList(stageValueBeanDto.getIdFromStage());

		// Stage Progression Validations..
		validateStageProgression(fromStageValBeanDto, cdToStageCode);

		// **** Stage Progression Steps **** //
		// 1. Insert into Case.
		// Case Country = from Stage, Case Region = from Stage, Date Opened =
		// Sysdate.
		CaseValueDto caseValDto = new CaseValueDto();
		if (isValid(stageValueBeanDto.getNmNewCase())) {
			caseValDto.setNmCase(stageValueBeanDto.getNmNewCase());
		} else {
			caseValDto.setNmCase(fromStageValBeanDto.getNmStage());
		}

		// Make sure that Case Name is less than 25 Characters.
		caseValDto.setNmCase(truncate(caseValDto.getNmCase(), 25));
		caseValDto.setCdCaseCounty(fromStageValBeanDto.getCdStageCnty());
		caseValDto.setCdCaseRegion(fromStageValBeanDto.getCdStageRegion());
		caseValDto.setCdCaseProgram(fromStageValBeanDto.getCdStageProgram());
		Long newCaseId = caseDao.insertIntoCapsCase(caseValDto);

		// 2. Insert into Situation.
		SituationDto situationDto = new SituationDto();
		situationDto.setIdCase(newCaseId);
		Long newSituationId = situationDao.insertIntoSituation(situationDto);
		fromStageValBeanDto.setIdCase(newCaseId);
		fromStageValBeanDto.setIdSituation(newSituationId);
		fromStageValBeanDto.setCdStage(cdToStageCode);

		// 3. Create New Stage.
		// Populate StageValueBean Object.
		Long newStageId = createNewStages(fromStageValBeanDto, stageValueBeanDto);

		newStageValBeanDto.setIdCase(newCaseId);
		newStageValBeanDto.setIdSituation(newSituationId);
		newStageValBeanDto.setIdStage(newStageId);
		return newStageValBeanDto;
	}

	private boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

	public String truncate(String string, int maxLength) {
		if (string == null) {
			return null;
		}
		if (string.length() <= maxLength) {
			return string;
		}
		return string.substring(0, maxLength);
	}

	// This function inserts Records into Safety Assessment Tables.
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)

	public SafetyAssessmentRes addSafetyAssmt(SafetyAssessmentReq addSafetyAssmtReq) {
		SafetyAssessmentRes addSafetyAssmtRes = new SafetyAssessmentRes();
		EventValueDto eventValueDto = new EventValueDto();

		eventValueDto.setIdStage(addSafetyAssmtReq.getIdStage());
		eventValueDto.setCdEventType(ServiceConstants.CEVNTTYP_ASM);
		eventValueDto.setIdCase(addSafetyAssmtReq.getIdCase());
		eventValueDto.setIdUser(addSafetyAssmtReq.getIdUser().longValue());
		eventValueDto.setIdPerson(addSafetyAssmtReq.getIdPerson());
		eventValueDto.setEventDescr(addSafetyAssmtReq.getEventDesc());
		eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_APRV);
		eventValueDto.setCdEventTask("2297");
		// the following value is obtained by calling postevent method
		Long idSftAssmtEvent = ServiceConstants.LongZero;
		// Postevent method call

		SafetyAssmtDto safetyAssmtDto = new SafetyAssmtDto();
		safetyAssmtDto.setEventId(idSftAssmtEvent);
		safetyAssmtDto.setStageId(addSafetyAssmtReq.getIdStage());
		safetyAssmtDto.setCaseId(addSafetyAssmtReq.getIdCase());

		// 2. Save Safety Assessment Details.
		SafetyAssmtDto returnSafetyAssmtDto = stageProgDao.queryPageData(safetyAssmtDto);
		returnSafetyAssmtDto.setUlPersonId(addSafetyAssmtReq.getIdPerson());
		returnSafetyAssmtDto.setTxtDecisionRationale(addSafetyAssmtReq.getTxtDecisionRationale());

		this.addSafetyAssmt(returnSafetyAssmtDto);
		// population is incomplete
		addSafetyAssmtRes.setIdCase(addSafetyAssmtReq.getIdCase());
		return addSafetyAssmtRes;

	}

	// This function inserts Records into Safety Assessment Tables.
	@SuppressWarnings("rawtypes")
	private void addSafetyAssmt(SafetyAssmtDto returnSftAssmtBean) {

		String previousAreaCode = null;
		String previousCategoryCode = null;

		returnSftAssmtBean.setSubStageOpen(stageProgDao.getSubStageOpen(returnSftAssmtBean));
		returnSftAssmtBean.setUlTodoId(stageProgDao.getTodoId(returnSftAssmtBean.getEventId()));

		// Inserts into Safety Assessment, Safety Area, and Safety Factors Table
		// 1.1 Insert into SAFETY_ASSESSMENT.
		stageProgDao.addSafetyAssmtDetails(returnSftAssmtBean);

		Iterator iter = returnSftAssmtBean.getFactors().iterator();
		while (iter.hasNext()) {
			SafetyFactorDto safetyFactorDto = (SafetyFactorDto) iter.next();
			safetyFactorDto.setIdPerson(returnSftAssmtBean.getUlPersonId());

			if (previousAreaCode == null || !previousAreaCode.equals(safetyFactorDto.getCdSafetyArea())) {
				// 2.2 Insert into Safety Area
				stageProgDao.addAreaDetails(safetyFactorDto, returnSftAssmtBean);
				previousAreaCode = safetyFactorDto.getCdSafetyArea();
			}

			if (previousCategoryCode == null || !previousCategoryCode.equals(safetyFactorDto.getCdSafetyFactor())) {
				// 2.3 Insert into Safety Factor
				stageProgDao.addFactorDetails(safetyFactorDto, returnSftAssmtBean);
				previousCategoryCode = safetyFactorDto.getCdSafetyFactor();
			}

		}

	}

	/**
	 * Method Name: addRiskAssmt Method Description: SIR 1022996 AR - Post AR to
	 * INV stage progression changes. Fetch Risk Assessment seed data This
	 * function inserts Records into Risk Assessment Tables.
	 * 
	 * @param baseAddRiskAssmtValueDto
	 * @return BaseAddRiskAssmtRes
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public BaseAddRiskAssmtRes addRiskAssmt(BaseAddRiskAssmtValueDto baseAddRiskAssmtValueDto) {
		BaseAddRiskAssmtRes baseAddRiskAssmtRes = new BaseAddRiskAssmtRes();
		EventValueDto eventValueDto = new EventValueDto();
		eventValueDto.setIdStage(Long.valueOf(baseAddRiskAssmtValueDto.getIdStage()));
		eventValueDto.setCdEventType(ServiceConstants.CEVNTTYP_ASM);
		eventValueDto.setIdCase(Long.valueOf(baseAddRiskAssmtValueDto.getIdCase()));
		eventValueDto.setIdPerson(Long.valueOf(baseAddRiskAssmtValueDto.getIdPerson()));
		eventValueDto.setCdEventTask("2295");
		eventValueDto.setEventDescr("Risk Assessment");
		eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
		PostEventReq postEventReq = new PostEventReq();

		postEventReq.setSzCdTask(eventValueDto.getCdEventTask());
		postEventReq.setSzCdEventType(eventValueDto.getCdEventType());
		postEventReq.setSzTxtEventDescr(eventValueDto.getEventDescr());

		Date date = new Date();
		postEventReq.setDtDtEventOccurred(date);

		postEventReq.setSzCdEventStatus(eventValueDto.getCdEventStatus());

		PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
		postEventPersonDto.setIdPerson(eventValueDto.getIdPerson());

		List<PostEventPersonDto> postEventPersonDtos = new ArrayList<PostEventPersonDto>();
		postEventPersonDtos.add(postEventPersonDto);

		postEventReq.setPostEventPersonList(postEventPersonDtos);

		postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventReq.setUlIdCase((long) eventValueDto.getIdCase());
		postEventReq.setUlIdStage((long) eventValueDto.getIdStage());
		postEventReq.setUlIdPerson((long) baseAddRiskAssmtValueDto.getIdUser());
		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		long idRiskEvent = postEventRes.getUlIdEvent();
		RiskAssmtValueDto riskAssmtValueBean = riskAssessmentDao.populateRiskData(baseAddRiskAssmtValueDto.getIdCase(),
				baseAddRiskAssmtValueDto.getIdStage());
		riskAssmtValueBean.setIdCase((long) baseAddRiskAssmtValueDto.getIdCase());
		riskAssmtValueBean.setIdStage((long) baseAddRiskAssmtValueDto.getIdStage());
		riskAssmtValueBean.setIdEvent((long) idRiskEvent);
		riskAssessmentDao.addRiskAssmtDetails(riskAssmtValueBean);
		String previousAreaCode = null;
		String previousCategoryCode = null;
		long newRiskAreaId = 0;
		long newRiskCategoryId = 0;

		// Iterator<RiskAssmtValueDto> iter =
		// riskAssmtValueBean.getFactors().iterator();
		// while (iter.hasNext()) {
		// RiskAssmtValueDto formFactorBean = (RiskAssmtValueDto) iter.next();
		while (riskAssmtValueBean != null) {

			riskAssmtValueBean.setIdCase((long) baseAddRiskAssmtValueDto.getIdCase());
			riskAssmtValueBean.setIdStage((long) baseAddRiskAssmtValueDto.getIdStage());
			if (previousAreaCode == null || !previousAreaCode.equals(riskAssmtValueBean.getAreaCode())) {
				newRiskAreaId = riskAssessmentDao.addAreaDetails(idRiskEvent, riskAssmtValueBean);
				previousAreaCode = riskAssmtValueBean.getAreaCode();
			}
			if (previousCategoryCode == null || !previousCategoryCode.equals(riskAssmtValueBean.getCategoryCode())) {
				newRiskCategoryId = riskAssessmentDao.addCategoryDetails(idRiskEvent, newRiskAreaId,
						riskAssmtValueBean);
				previousCategoryCode = riskAssmtValueBean.getCategoryCode();
			}
			baseAddRiskAssmtRes.setTotalRecCount(riskAssessmentDao.addFactorDetails(idRiskEvent, newRiskAreaId,
					newRiskCategoryId, riskAssmtValueBean));
		}
		return baseAddRiskAssmtRes;
	}
}
