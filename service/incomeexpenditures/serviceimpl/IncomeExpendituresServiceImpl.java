package us.tx.state.dfps.service.incomeexpenditures.serviceimpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.IncomeAndResources;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.IncomeExpenditureReq;
import us.tx.state.dfps.service.common.request.SaveIncRsrcReq;
import us.tx.state.dfps.service.common.response.IncomeExpenditureRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.ApplicationReasonsNotEligibleUtil;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.common.utils.FceUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.FceDepCareDeductDto;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.fce.dao.DepCareDeductDao;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.fce.service.AgeCitizenshipService;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.fce.serviceimpl.FceApplicationErrorListUtil;
import us.tx.state.dfps.service.fostercarereview.dao.FceIncomeDao;
import us.tx.state.dfps.service.incomeexpenditures.dao.IncomeExpendituresDao;
import us.tx.state.dfps.service.incomeexpenditures.service.IncomeExpendituresService;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;
import us.tx.state.dfps.service.person.dto.PersonIncomeResourceDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:This class is doing service Implementation for
 * IncomeExpendituresService Nov 21 2017- 5:50:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public class IncomeExpendituresServiceImpl implements IncomeExpendituresService {

	@Autowired
	IncomeExpendituresDao incomeExpendituresDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	AgeCitizenshipService ageCitizenshipService;

	@Autowired
	FceDao fceApplicationDao;

	@Autowired
	FceService fceService;

	@Autowired
	DepCareDeductDao depCareDeductionDao;
	@Autowired
	FceEligibilityDao fceEligibilityDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	EventService eventService;

	@Autowired
	private FceApplicationErrorListUtil fceApplicationErrorListUtil;

	@Autowired
	EventUtil eventutil;

	@Autowired
	PersonDao personDao;

	@Autowired
	ApplicationReasonsNotEligibleUtil applicationReasonsNotEligibleUtil;

	@Autowired
	TodoDao todoDao;

	@Autowired
	FceUtil fceUtil;

	@Autowired
	FceIncomeDao fceIncomeDao;

	/**
	 * Method Name: readIncomeExpDtl Method Description:Fetches income
	 * expenditures details
	 * 
	 * @param StageId
	 * @param PersonId
	 * @return IncomeExpenditureRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public IncomeExpenditureRes readIncomeExpDtl(IncomeExpenditureReq incomeExpenditureReq) {
		IncomeExpenditureRes incomeExpenditureRes = new IncomeExpenditureRes();
		IncomeExpenditureDto incomeExpenditureDto = new IncomeExpenditureDto();
		FceContextDto fceContextDto = fceService.initializeFceApplication(incomeExpenditureReq.getIdStage(),
				incomeExpenditureReq.getIdEvent(), incomeExpenditureReq.getIdPerson());
		FceApplicationDto fceApplicationDto = fceContextDto.getFceApplicationDto();
		FceEligibilityDto fceEligibilityDto = fceContextDto.getFceEligibilityDto();

		// Set fce application and eligibility details
		incomeExpenditureDto.setFceApplicationDto(fceApplicationDto);
		incomeExpenditureDto.setFceEligibilityDto(fceEligibilityDto);

		// Set Event status
		incomeExpenditureDto.setCdEventStatus(fceContextDto.getCdEventStatus());

		// Set employee fields
		PersonDto persondto = incomeExpendituresDao.findPrimaryWorkerForStage(incomeExpenditureReq.getIdStage(),
				incomeExpenditureDto);
		incomeExpenditureDto.setNbrEmployeePersonPhone(persondto.getPersonPhone());
		incomeExpenditureDto.setNmEmployeePersonFull(persondto.getNmPersonFull());

		incomeExpenditureRes.setIncomeExpenditureDto(incomeExpenditureDto);
		return incomeExpenditureRes;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void setIncomeAndResources(IncomeExpenditureDto incomeExpenditureDto) {
		long idFceEligibility = incomeExpenditureDto.getFceEligibilityDto().getIdFceEligibility();

		List<FceIncomeDto> allIncomes = fceIncomeDao.getFceIncomeDtosByIdElig(idFceEligibility);
		// Set incomeForChild
		incomeExpenditureDto
				.setIncomeForChild(allIncomes.stream()
						.filter(o -> ServiceConstants.STRING_IND_Y.equals(o.getIndChild())
								&& ServiceConstants.STRING_IND_Y.equals(o.getIndIncomeSource()))
						.collect(Collectors.toList()));

		// Set incomeForFamily
		incomeExpenditureDto
				.setIncomeForFamily(allIncomes.stream()
						.filter(o -> ServiceConstants.STRING_IND_Y.equals(o.getIndFamily())
								&& ServiceConstants.STRING_IND_Y.equals(o.getIndIncomeSource()))
						.collect(Collectors.toList()));

		// Set resourcesForChild
		incomeExpenditureDto.setResourcesForChild(allIncomes.stream()
				.filter(o -> ServiceConstants.STRING_IND_Y.equals(o.getIndChild())
						&& ServiceConstants.STRING_IND_Y.equals(o.getIndResourceSource()))
				.collect(Collectors.toList()));

		// Set resourcesForFamily
		incomeExpenditureDto.setResourcesForFamily(allIncomes.stream()
				.filter(o -> ServiceConstants.STRING_IND_Y.equals(o.getIndFamily())
						&& ServiceConstants.STRING_IND_Y.equals(o.getIndResourceSource()))
				.collect(Collectors.toList()));

	}

	/**
	 * Method Name: saveFceApplication Method Description: Save fce application
	 * details
	 * 
	 * @param fceApplicationDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public IncomeExpenditureRes saveFceApplication(IncomeExpenditureReq incomeExpenditureReq) {

		IncomeExpenditureRes incomeExpenditureRes = new IncomeExpenditureRes();
		fceService.verifyCanSave(incomeExpenditureReq.getIdStage(), incomeExpenditureReq.getIdPerson());

		FceApplicationDto fceApplicationDto = incomeExpenditureReq.getIncomeExpenditureDto().getFceApplicationDto();

		FceEligibilityDto fceEligibilityDto = incomeExpenditureReq.getIncomeExpenditureDto().getFceEligibilityDto();
		FceApplication fceApplication = fceApplicationDao.getFceApplication(fceApplicationDto.getIdFceApplication());
		FceEligibilityDto fceEligibilityDtoLatest = fceApplicationDao
				.getFceEligibility(fceEligibilityDto.getIdFceEligibility());
		fceUtil.dateLastUpdateCheck(fceEligibilityDtoLatest, fceApplication,
				incomeExpenditureReq.getIncomeExpenditureDto());

		fceApplicationDao.updateFcEligibilityAndApp(fceApplicationDto, fceEligibilityDto);

		// Save income and resources for child and family

		// Save income for child
		List<FceIncomeDto> incomesForChild = incomeExpenditureReq.getIncomeExpenditureDto().getIncomeForChild();
		setForIncomeSave(incomesForChild);
		// Save income for Family
		List<FceIncomeDto> incomesForFamily = incomeExpenditureReq.getIncomeExpenditureDto().getIncomeForFamily();
		setForIncomeSave(incomesForFamily);
		// Save resource for child
		List<FceIncomeDto> resourcesForChild = incomeExpenditureReq.getIncomeExpenditureDto().getResourcesForChild();
		setForIncomeSave(resourcesForChild);
		// Save resource for Family
		List<FceIncomeDto> resourcesForFamily = incomeExpenditureReq.getIncomeExpenditureDto().getResourcesForFamily();
		setForIncomeSave(resourcesForFamily);

		// Change Event Status
		Long fceApplicationIdEvent = incomeExpenditureReq.getIncomeExpenditureDto().getFceApplicationDto().getIdEvent();
		EventDto eventDto = eventDao.getEventByid(fceApplicationIdEvent);
		String eventType = eventDto.getCdEventStatus();
		if (ServiceConstants.EVENT_STATUS_NEW.equals(eventType)
				|| ServiceConstants.EVENT_STATUS_COMPLETE.equals(eventType)) {
			if (ServiceConstants.EVENT_STATUS_NEW.equals(eventType)) {
				eventutil.changeEventStatus(fceApplicationIdEvent, ServiceConstants.EVENT_STATUS_NEW, ServiceConstants.PROCESS_EVENT);
			} else {
				eventutil.changeEventStatus(fceApplicationIdEvent, ServiceConstants.EVENT_STATUS_COMPLETE, ServiceConstants.PENDING_EVENT);
			}
		}

		return incomeExpenditureRes;

	}

	/**
	 * Method Name: setForSave Method Description:
	 * 
	 * @param incomeDtos
	 */
	private void setForIncomeSave(List<FceIncomeDto> incomeDtos) {
		if (ObjectUtils.isEmpty(incomeDtos)) {
			// confirmation needed
			throw new ServiceLayerException("Incomes for child not found");
		}

		incomeDtos.forEach(incomedto -> {
			if (incomedto.getIdFceIncome() == 0) {
				throw new ServiceLayerException("IdFceIncome is not valid");
			} else {
				incomeExpendituresDao.saveFceIncomeResource(incomedto);
			}
		});
	}

	/**
	 * Method Name: saveFceEligibility Method Description: Save fce eligibility
	 * details
	 * 
	 * @param fceEligibilityDto
	 */
	@Override
	@Transactional(readOnly = false)
	public IncomeExpenditureRes submitApplication(IncomeExpenditureDto incomeExpenditureDto) {

		IncomeExpenditureRes incomeExpenditureRes = new IncomeExpenditureRes();
		long idFceApplication = incomeExpenditureDto.getFceApplicationDto().getIdFceApplication();
		FceEligibilityDto fceEligibilityDto = syncApplication(incomeExpenditureDto, idFceApplication);
		// Get Fce Application Dto
		FceApplication fceApplication = fceApplicationDao.getFceApplication(idFceApplication);
		long idEvent = incomeExpenditureDto.getFceApplicationDto().getIdEvent();

		fceUtil.dateLastUpdateCheck(fceEligibilityDto, fceApplication, incomeExpenditureDto);

		List<ErrorDto> errorDtos = fceApplicationErrorListUtil.checkApplicationErrors(incomeExpenditureDto, false);
		if (CollectionUtils.isNotEmpty(errorDtos)) {
			FceEligibilityDto fceEligibilityDtoLatest = fceApplicationDao
					.getFceEligibility(fceEligibilityDto.getIdFceEligibility());
			incomeExpenditureDto.getFceApplicationDto().setDtLastUpdate(fceApplication.getDtLastUpdate());
			incomeExpenditureDto.setFceEligibilityDto(fceEligibilityDtoLatest);
			incomeExpenditureRes.setIncomeExpenditureDto(incomeExpenditureDto);
			incomeExpenditureRes.setErrorDtos(errorDtos);
			return incomeExpenditureRes;
		}

		String cdLivingCondition = fceApplication.getCdLivingMonthRemoval();
		BeanUtils.copyProperties(fceApplication, incomeExpenditureDto.getFceApplicationDto());

		applicationReasonsNotEligibleUtil.calculateSystemDerivedParentalDeprivation(cdLivingCondition,
				incomeExpenditureDto.getFceApplicationDto(), fceEligibilityDto);

		eventutil.completeTodosForEventId(idEvent);

		return incomeExpenditureRes;

	}

	/**
	 * Method Name: syncApplication Method Description:
	 * 
	 * @param incomeExpenditureDto
	 * @param idFceApplication
	 * @return
	 */
	private FceEligibilityDto syncApplication(IncomeExpenditureDto incomeExpenditureDto, long idFceApplication) {
		long idEvent = incomeExpenditureDto.getFceApplicationDto().getIdEvent();
		long idFceEligibility = incomeExpenditureDto.getFceEligibilityDto().getIdFceEligibility();
		long idFcePerson = incomeExpenditureDto.getFceEligibilityDto().getIdFcePerson();
		long idPerson = incomeExpenditureDto.getFceEligibilityDto().getIdPerson();

		// Verfiy non zero values

		FceUtil.verifyNonZero("idEvent", idEvent);
		FceUtil.verifyNonZero("idFceApplication", idFceApplication);
		FceUtil.verifyNonZero("idFceEligibility", idFceEligibility);
		FceUtil.verifyNonZero("idFcePerson", idFcePerson);
		FceUtil.verifyNonZero("idPerson", idPerson);

		FceContextDto fceContextDto = new FceContextDto();

		fceContextDto.setIdEvent(idEvent);
		fceContextDto.setIdFceApplication(idFceApplication);
		fceContextDto.setIdFceEligibility(idFceEligibility);
		fceContextDto.setIdFcePerson(idFcePerson);
		fceContextDto.setIdPerson(idPerson);

		// Get Fce Eligibility Dto

		FceEligibilityDto fceEligibilityDto = fceApplicationDao.getFceEligibility(idFceEligibility);

		// Sync Fce Application data Uncomment
		fceService.syncFceApplicationStatus(fceEligibilityDto);
		return fceEligibilityDto;
	}

	/**
	 * Method Name: calcualteFceData Method Description: This method is used to
	 * calcualte fce data
	 * 
	 * @param fceEligibilityDto
	 */
	@Override
	@Transactional(readOnly = false)
	public IncomeExpenditureRes calcualteFceData(IncomeExpenditureReq incomeExpenditureReq) {

		IncomeExpenditureRes incomeExpenditureRes = new IncomeExpenditureRes();

		FceEligibilityDto fceEligibilityDto = incomeExpenditureReq.getIncomeExpenditureDto().getFceEligibilityDto();
		FceApplicationDto fceApplicationDto = incomeExpenditureReq.getIncomeExpenditureDto().getFceApplicationDto();
		// Get Fce Application Dto
		fceApplicationDao.getFceApplication(fceEligibilityDto.getIdFceApplication());

		incomeExpendituresDao.syncFceApplicationStatus(fceEligibilityDto);

		// fceUtil.dateLastUpdateCheck(fceEligibilityDto,fceApplication,incomeExpenditureReq.getIncomeExpenditureDto());

		List<ErrorDto> errorDtos = fceApplicationErrorListUtil
				.checkApplicationErrors(incomeExpenditureReq.getIncomeExpenditureDto(), true);

		if (CollectionUtils.isNotEmpty(errorDtos)) {
			IncomeExpenditureDto incomeExpenditureDto = incomeExpenditureReq.getIncomeExpenditureDto();
			FceApplication fceApplication = fceApplicationDao
					.getFceApplication(fceApplicationDto.getIdFceApplication());
			FceEligibilityDto fceEligibilityDtoLatest = fceApplicationDao
					.getFceEligibility(fceEligibilityDto.getIdFceEligibility());
			incomeExpenditureDto.getFceApplicationDto().setDtLastUpdate(fceApplication.getDtLastUpdate());
			incomeExpenditureDto.setFceEligibilityDto(fceEligibilityDtoLatest);
			incomeExpenditureRes.setIncomeExpenditureDto(incomeExpenditureDto);
			incomeExpenditureRes.setErrorDtos(errorDtos);
			return incomeExpenditureRes;
		}

		// Need to test and uncomment
		applicationReasonsNotEligibleUtil.calculateFceAppDetail(incomeExpenditureReq.getIncomeExpenditureDto());
		Long idEvent = fceApplicationDto.getIdEvent();
		EventDto eventDto = eventDao.getEventByid(idEvent);
		String eventType = eventDto.getCdEventStatus();
		if (ServiceConstants.EVENTSTATUS_PENDING.equals(eventType)) {
			eventutil.changeEventStatus(fceApplicationDto.getIdEvent(), ServiceConstants.EVENTSTATUS_PENDING,
					ServiceConstants.COMPLETE_EVENT);
		}
		return incomeExpenditureRes;
	}

	/**
	 * 
	 * Method Name: readDepCareDeduction Method Description: this method used to
	 * read the DepcareDeduction
	 * 
	 * @param incomeExpenditureReq
	 * @return IncomeExpenditureRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public IncomeExpenditureRes readDepCareDeduction(IncomeExpenditureReq incomeExpenditureReq) {

		IncomeExpenditureRes res = new IncomeExpenditureRes();
		List<FceDepCareDeductDto> depCareDeductions = depCareDeductionDao.findFceDepCareDeduct(
				incomeExpenditureReq.getIncomeExpenditureDto().getIdFceEligibility(), Boolean.FALSE);
		IncomeExpenditureDto incomeExpenditureDto = incomeExpenditureReq.getIncomeExpenditureDto();
		incomeExpenditureDto.setDepCareDeduction(depCareDeductions);
		res.setIncomeExpenditureDto(incomeExpenditureDto);

		return res;

	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public IncomeExpenditureRes checkDepCareDeductionErrors(IncomeExpenditureDto incomeExpenditureDto) {

		IncomeExpenditureRes incomeExpenditureRes = new IncomeExpenditureRes();
		long idFceApplication = incomeExpenditureDto.getFceApplicationDto().getIdFceApplication();
		FceEligibilityDto fceEligibilityDto = syncApplication(incomeExpenditureDto, idFceApplication);
		// Get Fce Application Dto
		FceApplication fceApplication = fceApplicationDao.getFceApplication(idFceApplication);
		fceUtil.dateLastUpdateCheck(fceEligibilityDto, fceApplication, incomeExpenditureDto);

		ErrorDto errorDto = fceApplicationErrorListUtil.checkDepCareDeductionErrors(incomeExpenditureDto);

		incomeExpenditureRes.setIncomeExpenditureDto(incomeExpenditureDto);
		if (!ObjectUtils.isEmpty(errorDto)) {
			incomeExpenditureRes.setDepCareError(errorDto);

		}

		return incomeExpenditureRes;

	}

	/**
	 * Method Name: transformsToIncomeAndresourseDto Method Description: This
	 * method maps the data from person table to Income and Resource
	 * 
	 * @param incomeAndResource
	 * @return incomeResourceDto
	 */
	private PersonIncomeResourceDto transformToIncomeAndResourceDto(final IncomeAndResources incomeAndResource) {
		PersonIncomeResourceDto incomeResourceDto = new PersonIncomeResourceDto();

		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getAmtIncRsrc())) {
			incomeResourceDto.setAmtIncRsrc(incomeAndResource.getAmtIncRsrc().doubleValue());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getCdIncRsrcIncome())) {
			incomeResourceDto.setCdIncRsrcIncome(incomeAndResource.getCdIncRsrcIncome());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getCdIncRsrcType())) {
			incomeResourceDto.setCdIncRsrcType(incomeAndResource.getCdIncRsrcType());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getDtIncRsrcFrom())) {
			incomeResourceDto.setDtIncRsrcFrom(incomeAndResource.getDtIncRsrcFrom());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getDtLastUpdate())) {
			incomeResourceDto.setDtLastUpdate(incomeAndResource.getDtLastUpdate());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getIdIncRsrc())) {
			incomeResourceDto.setIdIncRsrc(incomeAndResource.getIdIncRsrc());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getTxtIncRsrcDesc())) {
			incomeResourceDto.setIncRsrcDesc(incomeAndResource.getTxtIncRsrcDesc());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getPersonByIdIncRsrcWorker().getIdPerson())) {
			incomeResourceDto.setPersonByIdPerson(incomeAndResource.getPersonByIdIncRsrcWorker().getIdPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getSdsIncRsrcSource())) {
			incomeResourceDto.setSdsIncRsrcSource(incomeAndResource.getSdsIncRsrcSource());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getSdsIncRsrcVerfMethod())) {
			incomeResourceDto.setSdsIncRsrcVerfMethod(incomeAndResource.getSdsIncRsrcVerfMethod());
		}
		//modified the code to set the Staff name - Warranty defect 11729 
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getPersonByIdIncRsrcWorker().getNmPersonFull())) {
			incomeResourceDto.setNmPersonFull(incomeAndResource.getPersonByIdIncRsrcWorker().getNmPersonFull());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getIndIncRsrcNotAccess())) {
			incomeResourceDto.setIndIncRsrcNotAccess(incomeAndResource.getIndIncRsrcNotAccess().toString());
		}
		if (!TypeConvUtil.isNullOrEmpty(incomeAndResource.getDtIncRsrcTo()) && !new SimpleDateFormat("yyyy-MM-dd")
				.format(incomeAndResource.getDtIncRsrcTo()).equals("4712-12-31")) {

			incomeResourceDto.setDtIncRsrcTo(incomeAndResource.getDtIncRsrcTo());
		}
		incomeResourceDto.setJsonIncomeResDetail(objectToEcodedJson(incomeResourceDto));
		return incomeResourceDto;
	}

	/**
	 * Method Name: objectToEcodedJson Method Description:This method is used to
	 * convert object To EcodedJson
	 * 
	 * @param object
	 * @return
	 */
	public static String objectToEcodedJson(Object object) {
		ObjectMapper mapperObj = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = mapperObj.writeValueAsString(object);
			jsonStr = URLEncoder.encode(jsonStr, "UTF-8");
		} catch (IOException e) {
			throw new ServiceLayerException("objectToEcodedJson for Person Income Resource Failed");
		}
		return jsonStr;
	}

	/**
	 * Method saveIncomeResource Method Description: this method save the
	 * IncomeAndResources data in the table
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveIncomeResource(SaveIncRsrcReq saveIncRsrcReq) {
		IncomeAndResources incomeAndResources = getIncomeAndResourceFromRequest(saveIncRsrcReq);
		incomeExpendituresDao.saveIncomeAndResource(incomeAndResources);
	}

	/**
	 * Method Name:deleteIncomeResource Method Description: this method deletes
	 * the IncomeAndResources corresponding to the IdIncResc return null
	 * 
	 * @param saveIncRsrcReq
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void deleteIncomeResource(SaveIncRsrcReq saveIncRsrcReq) {
		incomeExpendituresDao.deleteIncomeAndResourceById(saveIncRsrcReq.getIncomeAndResourceDto().getIdIncRsrc());
	}

	/**
	 * Method Name: getIncomeAndResourceFromRequest Method Description: This
	 * private method will fetches the income and resources of the person and
	 * maps to IncomeAndResources from IncomeAndResourceDto
	 * 
	 * @param saveIncRsrcReq
	 * @return
	 */
	private IncomeAndResources getIncomeAndResourceFromRequest(SaveIncRsrcReq saveIncRsrcReq) {

		IncomeAndResources incomeAndResources = new IncomeAndResources();
		IncomeAndResourceDto dto = saveIncRsrcReq.getIncomeAndResourceDto();

		if (!TypeConvUtil.isNullOrEmpty(BigDecimal.valueOf(dto.getAmount()))) {
			incomeAndResources.setAmtIncRsrc(BigDecimal.valueOf(dto.getAmount().doubleValue()));
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getIncomeOrResource())) {
			incomeAndResources.setCdIncRsrcIncome(dto.getIncomeOrResource());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getType())) {
			incomeAndResources.setCdIncRsrcType(dto.getType());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getEffectiveFrom())) {
			incomeAndResources.setDtIncRsrcFrom(dto.getEffectiveFrom());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getEffectiveTo())) {
			incomeAndResources.setDtIncRsrcTo(dto.getEffectiveTo());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getScrDtLastUpdate())) {
			incomeAndResources.setDtLastUpdate(dto.getScrDtLastUpdate());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getIdIncRsrc())) {
			incomeAndResources.setIdIncRsrc(dto.getIdIncRsrc());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getNotAccessible())) {
			incomeAndResources.setIndIncRsrcNotAccess(dto.getNotAccessible().charAt(0));
		} else {
			incomeAndResources.setIndIncRsrcNotAccess('N');
		}
		//Modified the code to insert the worker personId - Warranty defect 11729 
		Person personWrkr = personDao.getPerson(!ObjectUtils.isEmpty(dto.getIdIncRsrcWorker())
				? dto.getIdIncRsrcWorker() : ServiceConstants.Zero_Value);
		incomeAndResources.setPersonByIdIncRsrcWorker(personWrkr);
		Person person = personDao.getPerson(saveIncRsrcReq.getIdRecPerson());
		incomeAndResources.setPersonByIdPerson(person);
		if (!TypeConvUtil.isNullOrEmpty(dto.getSource())) {
			incomeAndResources.setSdsIncRsrcSource(dto.getSource());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getVerfMethod())) {
			incomeAndResources.setSdsIncRsrcVerfMethod(dto.getVerfMethod());
		}

		if (!TypeConvUtil.isNullOrEmpty(dto.getComments())) {
			incomeAndResources.setTxtIncRsrcDesc(dto.getComments());
		}
		if (!TypeConvUtil.isNullOrEmpty(new Date())) {
			incomeAndResources.setDtLastUpdate(new Date());
		}
		//PD 90488 - Added Date Created, ID Created person and ID Updated person
		if (!TypeConvUtil.isNullOrEmpty(dto.getDtCreated())) {
			incomeAndResources.setDtCreated(dto.getDtCreated());
		} else {
			incomeAndResources.setDtCreated(new Date());
		}
		if (!TypeConvUtil.isNullOrEmpty(dto.getIdCreatedPerson())) {
			incomeAndResources.setIdCreatedPerson(dto.getIdCreatedPerson());
		}
		if (!TypeConvUtil.isNullOrEmpty(dto.getIdLastUpdatePerson())) {
			incomeAndResources.setIdLastUpdatePerson(dto.getIdLastUpdatePerson());
		}
		return incomeAndResources;
	}

	/**
	 * Method Name : getIncomeResource Method Description : This method fetches
	 * the data of person corresponding to the idPerson
	 * 
	 * @parma idPerson
	 * @return PersonIncomeResourceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PersonIncomeResourceDto> getIncomeResource(Long idPerson) {
		List<IncomeAndResources> list = incomeExpendituresDao.getPersonIncomeForIdPerson(idPerson);
		Collections.sort(list, new IncomeAndResourceComparator());

		List<PersonIncomeResourceDto> listDto = list.stream()
				.map(incomeAndResources -> transformToIncomeAndResourceDto(incomeAndResources))
				.collect(Collectors.toList());
		return listDto;
	}
}
