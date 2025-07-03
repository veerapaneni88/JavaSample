package us.tx.state.dfps.service.person.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.PersonEmail;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.CaseExtendedPersonReq;
import us.tx.state.dfps.service.common.request.GroupUpdateReq;
import us.tx.state.dfps.service.common.request.PersonListReq;
import us.tx.state.dfps.service.common.request.UpdateSearchPersonIndReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEmailDao;
import us.tx.state.dfps.service.person.dao.PersonListDao;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dto.*;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.visitationplan.dao.NoCnctVstPlnDtlDao;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.NotifToParentEngDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Service
@Transactional
public class PersonListServiceImpl implements PersonListService {

	@Autowired
	PersonDao personDao;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	PersonListDao personListDao;

	@Autowired
	PersonPhoneDao personPhoneDao;

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	PersonEmailDao personEmailDao;

	@Autowired
	NameDao nameDao;

	@Autowired
	NoCnctVstPlnDtlDao noCnctVstPlnDtlDao;

	@Autowired
	MobileUtil mobileUtil;

	@Autowired
	StageDao stageDao;

	@Autowired
	AdminReviewDao adminReviewDao;

	@Autowired
	NotifToParentEngDao notifToParentEngDao;

	private static final Logger log = Logger.getLogger(PersonListServiceImpl.class);

	/**
	 * Method Description: This method is used to call Person List Dao methods
	 * with input as idCase or stage_id and Stage Program. The risk assessment
	 * is has task code = 2290 and status of event should be comp or pend. The
	 * ind merge is set based on count checked in person merge table. Tuxedo
	 * Service Name: CINV01S
	 *
	 * @param retrievePersonListReq
	 * @return personListOut
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PersonListRes getPersonList(PersonListReq retrievePersonListReq) {
		List<PersonListDto> personList = new ArrayList<>();
		List<PersonListAlleDto> personListAlle = new ArrayList<>();
		List<PersonListEventDto> personListEvent = new ArrayList<>();
		List<PersonListEventDto> personListEventBlob = new ArrayList<>();
		PersonListRes personListOut = new PersonListRes();
		Boolean TDMHMR = Boolean.FALSE;
		Boolean MHMR = Boolean.FALSE;
		boolean isAFCProg = false;
		personList = (!TypeConvUtil.isNullOrEmpty(retrievePersonListReq.getIdCase()))
				? personDao.getPersonListByIdCase(retrievePersonListReq.getIdCase(), ServiceConstants.STAFF_TYPE)
				: personDao.getPersonListByIdStage(retrievePersonListReq.getIdStage(), ServiceConstants.STAFF_TYPE);
		if (!TypeConvUtil.isNullOrEmpty(retrievePersonListReq.getIdStage())) {
			if (((ServiceConstants.CAPS_PROG_AFC).equalsIgnoreCase(retrievePersonListReq.getStageProgram())
					&& (ServiceConstants.ALLEGED_PERPS_ONLY)
					.equalsIgnoreCase(retrievePersonListReq.getSysCdWinMode()))) {
				isAFCProg = true;
				personListAlle = personDao.getPersonListAllegation(retrievePersonListReq.getIdStage());
				if (personListAlle.size() > 0) {
					List<Long> idPersonAlle = personListAlle.stream().map(PersonListAlleDto::getIdAllePerpetrator)
							.collect(Collectors.toList());
					List<PersonListDto> persList = personList.stream().filter(p -> {
						if (idPersonAlle.contains(p.getIdPerson())) {
							setIndActiveStatusMerge(p);
							return true;
						} else {
							return false;
						}
					}).collect(Collectors.toList());
					personListOut.setRetrievePersonList(persList);
				}
			}
			personListEvent = personDao.getPersonListEvent(retrievePersonListReq.getIdStage(),
					ServiceConstants.ASSESS_EVENT_TYPE);
			for (PersonListEventDto eventIdRiskAss : personListEvent) {
				if (((ServiceConstants.RISK_ASSMNT_TASK).equalsIgnoreCase(eventIdRiskAss.getCdTask()))
						&& ((ServiceConstants.EVENT_STATUS_COMPLETE).equalsIgnoreCase(eventIdRiskAss.getEventStatus()))
						|| ((ServiceConstants.EVENT_STATUS_PENDING)
						.equalsIgnoreCase(eventIdRiskAss.getEventStatus()))) {
					personListOut.setIdEvent(eventIdRiskAss.getIdEvent());
					personListOut.setEventStatus(eventIdRiskAss.getEventStatus());
				}
			}
			List<Long> tempeventlist = new ArrayList<>();
			List<Boolean> tempInd = new ArrayList<>();
			personListEventBlob = personDao.getPersonListEvent(retrievePersonListReq.getIdStage(),
					ServiceConstants.FLR_EVENT_TYPE);
			for (PersonListEventDto personIdEvent : personListEventBlob) {
				if ((ServiceConstants.FLR_EVENT_DESC_INIT_TDMHMR).equalsIgnoreCase(personIdEvent.getEventDescr())
						&& (!TDMHMR)) {
					tempeventlist.add(personIdEvent.getIdEvent());
					TDMHMR = tempInd.add(ServiceConstants.IND_BLOB);
				} else if ((ServiceConstants.FLR_EVENT_DESC_INIT_MHMR).equalsIgnoreCase(personIdEvent.getEventDescr())
						&& (!MHMR)) {
					tempeventlist.add(personIdEvent.getIdEvent());
					MHMR = tempInd.add(ServiceConstants.IND_BLOB);
				}
				personListOut.setbIndBlobExistsInDatabase(tempInd);
				personListOut.setEventList(tempeventlist);
			}
		}
		if (!isAFCProg) {
			for (PersonListDto personListDto : personList) {
				if (!TypeConvUtil.isNullOrEmpty(personListDto.getIdPerson()))
					setIndActiveStatusMerge(personListDto);
				personListOut.setRetrievePersonList(personList);
			}
		}

		List<PersonListDto> personOut = personListOut.getRetrievePersonList();
		List<PersonListDto> prn = new ArrayList<>();

		//Added null condition check for the list - Warranty defect 11092
		List<PersonListDto> reporter = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
				.filter(p -> (ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter()))
				.collect(Collectors.toList()):null;
		List<PersonListDto> victim = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
				.filter(p -> (ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getStagePersRole()))
				.collect(Collectors.toList()): null;
		//double check on stage for INV :TODO
		if(retrievePersonListReq.getCdStage()!=null&&retrievePersonListReq.getCdStage().equalsIgnoreCase("INV")&&retrievePersonListReq.isARI()){

		 prn = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
					.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getStagePersType())))
					.collect(Collectors.toList()):null;
		}else{
			 prn = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
					.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getStagePersType())
							&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
							&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getStagePersRole())
							&& null != p.getDtPersonBirth()))
					.collect(Collectors.toList()):null;
		}

		List<PersonListDto> prnNoBirthDate = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
				.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getStagePersRole())
						&& null == p.getDtPersonBirth()))
				.collect(Collectors.toList()):null;
		List<PersonListDto> col = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
				.filter(p -> ((ServiceConstants.COLLATERAL).equalsIgnoreCase(p.getStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getStagePersRole())))
				.collect(Collectors.toList()):null;
		List<PersonListDto> colNoName = !ObjectUtils.isEmpty(personOut) ? personOut.stream()
				.filter(p -> ((ServiceConstants.PRINCIPAL).equalsIgnoreCase(p.getStagePersType())
						&& !(ServiceConstants.Y).equalsIgnoreCase(p.getIndStagePersReporter())
						&& !(ServiceConstants.PRIMARY_CHILD).equalsIgnoreCase(p.getStagePersRole())
						&& null == p.getPersonFull()))
				.collect(Collectors.toList()):null;
		List<PersonListDto> sortedPrn = !ObjectUtils.isEmpty(prn) ? prn.stream()
				.filter(person->person.getDtPersonBirth()!=null)
				.sorted((a, b) -> (b.getDtPersonBirth()).compareTo((a.getDtPersonBirth())))
				.collect(Collectors.toList()):null;
		Comparator<PersonListDto> PersonListDtoComparator = Comparator.comparing(PersonListDto::getPersonFull,
				(s1, s2) -> {
					if (null == s1)
						return (null == s2) ? 0 : -1;
					if (null == s2)
						return 1;
					return s2.compareTo(s1);
				});
		List<PersonListDto> sortedCol = !ObjectUtils.isEmpty(personOut) ? col.stream().sorted(PersonListDtoComparator).collect(Collectors.toList()):null;
		List<PersonListDto> res = new ArrayList<>();
		List<PersonListDto> mpsPersonList = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(retrievePersonListReq.getIdStage())) {
			mpsPersonList = personDao.getPersonListByIdCaseForMPS(retrievePersonListReq.getIdStage());
		}
		//Added null condition check for the list - Warranty defect 11092
		if(!ObjectUtils.isEmpty(reporter))
			res.addAll(reporter);
		if(!ObjectUtils.isEmpty(victim))
			res.addAll(victim);
		if(!ObjectUtils.isEmpty(sortedPrn))
			res.addAll(sortedPrn);
		if(!ObjectUtils.isEmpty(prnNoBirthDate))
			res.addAll(prnNoBirthDate);
		if(!ObjectUtils.isEmpty(sortedCol))
			res.addAll(sortedCol);
		if(!ObjectUtils.isEmpty(colNoName))
			res.addAll(colNoName);
		if(!ObjectUtils.isEmpty(mpsPersonList)) {
			res.addAll(mpsPersonList);
		}
		if(retrievePersonListReq.isARI()){

            if (sortedPrn != null && prn != null && sortedPrn.size() < prn.size()) {

                sortedPrn = prn;
            }

            personListOut.setRetrievePersonList(sortedPrn);

			personListOut.setDtSystemDate(new Date());
			return personListOut;

		}

		List<Long> unique = new ArrayList<>();
		List<PersonListDto> filtered = new ArrayList<>();
		for (PersonListDto personListDto : res) {
			if (!unique.contains(personListDto.getIdPerson())) {
				filtered.add(personListDto);
				unique.add(personListDto.getIdPerson());
			}
		}

		personListOut.setRetrievePersonList(filtered);
		personListOut.setDtSystemDate(new Date());
		log.info("TransactionId :" + retrievePersonListReq.getTransactionId());

		return personListOut;
	}

	/**
	 * This service will update only the person search indicator on the stage
	 * person link table. Service Name - CINV50S
	 *
	 * @param updateSearchPersonIndReq
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public UpdateSearchPersonIndRes personSearchIndUpdate(UpdateSearchPersonIndReq updateSearchPersonIndReq) {
		UpdateSearchPersonIndRes updateSearchPersonIndRes = new UpdateSearchPersonIndRes();
		String retVal = ServiceConstants.FND_FAIL;
		if (!TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq)) {
			if (!TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getReqFuncCd())
					&& !TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getCdTask())
					&& !TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getIdStage())) {
				InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
				inCheckStageEventStatusDto.setCdTask(updateSearchPersonIndReq.getCdTask());
				inCheckStageEventStatusDto.setIdStage(updateSearchPersonIndReq.getIdStage());
				inCheckStageEventStatusDto.setCdReqFunction(updateSearchPersonIndReq.getReqFuncCd());
				Boolean status = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
				if (!status) {
					retVal = ServiceConstants.FND_FAIL;
				} else {
					retVal = ServiceConstants.FND_SUCCESS;
				}
			}
			if (retVal.equals(ServiceConstants.FND_SUCCESS)) {
				if (!TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getIdStagePerson())
						&& !TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getIdStage())
						&& !TypeConvUtil.isNullOrEmpty(updateSearchPersonIndReq.getCdStagePersSearchInd())) {
					personDao.updatePersonSearchIndicator(updateSearchPersonIndReq.getCdStagePersSearchInd(),
							updateSearchPersonIndReq.getIdStagePerson(), updateSearchPersonIndReq.getIdStage());
					updateSearchPersonIndRes.setActionResult("Update Successfully");
				}
			}
		}
		log.info("TransactionId :" + updateSearchPersonIndReq.getTransactionId());
		return updateSearchPersonIndRes;
	}

	private void setIndActiveStatusMerge(PersonListDto personListDto) {
		Date currentDate = new Date();
		if (!TypeConvUtil.isNullOrEmpty(personListDto.getDtPersonBirth())) {
			personListDto.setPersonAge((TypeConvUtil.isNullOrEmpty(personListDto.getDtPersonDeath()))
					? DateUtils.getPersonListAge(personListDto.getDtPersonBirth(), currentDate)
					: DateUtils.getPersonListAge(personListDto.getDtPersonBirth(), personListDto.getDtPersonDeath()));
		} else if (TypeConvUtil.isNullOrEmpty(personListDto.getPersonAge())
				&& TypeConvUtil.isNullOrEmpty(personListDto.getDtPersonBirth())) {
			personListDto.setPersonAge(ServiceConstants.NULL);
		}
		if (!TypeConvUtil.isNullOrEmpty(personListDto.getIdPerson())) {
			personListDto.setIndMerge((personDao.getPersonMergeCount(personListDto.getIdPerson()) > 0)
					? (ServiceConstants.STRING_IND_Y) : (ServiceConstants.STRING_IND_N));
			ArrayList<PersonCategoryDto> personCategoryList = personDao
					.getPersonCategoryList(personListDto.getIdPerson());
			List<String> personCategory = personCategoryList.stream().map(PersonCategoryDto::getCdPersonCategory)
					.collect(Collectors.toList());
			String str = ServiceConstants.EMPLOYEE_CATEGORY;
			for (String category : personCategory) {
				boolean bFound = str.contains(category);
				if (bFound) {
					personListDto.setIndActiveStatus((ServiceConstants.STRING_IND_Y));
					break;
				} else {
					personListDto.setIndActiveStatus((ServiceConstants.STRING_IND_N));
				}
			}
			if (personListDto.getPersonChar() != null) {
				personListDto.setPersonChar(
						((ServiceConstants.PERSON_CHAR_ONE).equalsIgnoreCase(personListDto.getPersonChar())
								|| (ServiceConstants.PERSON_CHAR_TWO).equalsIgnoreCase(personListDto.getPersonChar()))
								? (ServiceConstants.STRING_IND_Y) : (ServiceConstants.STRING_IND_N));
			} else {
				personListDto.setPersonChar((ServiceConstants.STRING_IND_N));
			}
			if (StringUtils.isBlank(personListDto.getIndPersonDobApprox())) {
				personListDto.setIndPersonDobApprox(ServiceConstants.STRING_IND_N);
			}
		}
	}

	/**
	 * This Method will retrieve the list of citizenship status for the person
	 * within the stage. Ejb Service name : PersonList
	 *
	 * @param personListReq
	 * @return PersonListRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PersonListRes fetchPersonCitizenshipDtls(PersonListReq personListReq) {
		log.info("TransactionId :" + personListReq.getTransactionId());
		List<PersonListValueDto> personListValueDto = personListDao
				.getPersonCitizenshipDtls(personListReq.getIdStage());
		PersonListRes personListRes = new PersonListRes();
		personListRes.setPersonListDto(personListValueDto);
		return personListRes;
	}

	/**
	 * This Method will retrieve the case person list for the person
	 * information. Ejb Service name : PersonList
	 *
	 * @param personListReq
	 * @return PersonListRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public PersonListRes fetchCasePersonList(PersonListReq personListReq) {
		log.info("TransactionId :" + personListReq.getTransactionId());
		List<PersonListValueDto> personListValueDto = personListDao.getCasePersonList(personListReq.getIdCase());
		PersonListRes personListRes = new PersonListRes();
		personListRes.setPersonListDto(personListValueDto);
		return personListRes;
	}

	/**
	 * Method Name:getExtendedPersonList Method Description:This method returns
	 * list of extended case persons list related to a single person
	 *
	 * @param caseExtendedPersonReq
	 * @return CaseExtendedPersonRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public CaseExtendedPersonRes fetchExtendedPersonList(CaseExtendedPersonReq caseExtendedPersonReq) {
		List<CaseExtendedPersonDto> caseExtendedPersonDtoList = personListDao
				.getExtendedPersonList(caseExtendedPersonReq.getIdPerson());
		CaseExtendedPersonRes caseExtendedPersonRes = new CaseExtendedPersonRes();
		caseExtendedPersonRes.setExtendedPersonDto(caseExtendedPersonDtoList);
		return caseExtendedPersonRes;
	}

	/**
	 *
	 * Method Name: getPersonAddress Method Description:This method get person
	 * address details
	 *
	 * @param idPerson
	 * @return PersonDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonDto fetchPersonAddress(Long idPerson) {
		return personListDao.getIdPersonAddress(idPerson);
	}

	/**
	 * Method Description: This service will retrieve information related to
	 * person from person list based on stage or case Service name : Retrieve
	 * GroupUpdate
	 *
	 * @param retrieveGroupUpdateListReq
	 * @return groupUpdateListRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public GroupUpdateRes getGroupUpdateList(PersonListReq retrieveGroupUpdateListReq) {
		GroupUpdateRes groupUpdateListRes = new GroupUpdateRes();
		List<GroupUpdateDto> groupUpdateDtoList = (!TypeConvUtil.isNullOrEmpty(retrieveGroupUpdateListReq.getIdCase()))
				? personDao.getGroupUpdateListByIdCase(retrieveGroupUpdateListReq)
				: personDao.getGroupUpdateListByIdStage(retrieveGroupUpdateListReq);
		for (GroupUpdateDto groupUpdateRace : groupUpdateDtoList) {
			if (!mobileUtil.isMPSEnvironment()){
				List<PersonRaceDto> race = personDao.getRace(groupUpdateRace.getIdPerson());
				groupUpdateRace.setPersonRace(race);
			}
			groupUpdateRace.setIndActiveEvent(personDao.getIndActiveEvent(groupUpdateRace.getIdPerson()));
			groupUpdateRace.setPersonEventList(personDao.getAllPersonEvent(groupUpdateRace.getIdPerson()));
			if (!mobileUtil.isMPSEnvironment() && !org.springframework.util.StringUtils.isEmpty(groupUpdateRace.getIdCase())) {
				groupUpdateRace.setIndHomeRemovePers(
						personDao.getIndHomeRemovePers(groupUpdateRace.getIdPerson(), groupUpdateRace.getIdCase()));
			}
			if (!org.springframework.util.StringUtils.isEmpty(retrieveGroupUpdateListReq.getIdStage())) {
				groupUpdateRace.setIndAllegation(personDao.getIndAllegation(groupUpdateRace.getIdPerson(),
						retrieveGroupUpdateListReq.getIdStage()));
			}
		}
		groupUpdateListRes.setGroupUpdateDtoList(groupUpdateDtoList);
		log.info("TransactionId :" + retrieveGroupUpdateListReq.getTransactionId());
		return groupUpdateListRes;
	}

	/**
	 * Method Description: This service will save information related to person
	 * from person list based on stage or case Service name : Save GroupUpdate
	 *
	 * @param saveGroupUpdateReq
	 * @return response @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public GroupUpdateRes saveGroupUpdate(GroupUpdateReq saveGroupUpdateReq) {
		GroupUpdateRes response = new GroupUpdateRes();
		for (GroupUpdateDto groupUpdateEvent : saveGroupUpdateReq.getGroupUpdateDtoList()) {
			groupUpdateEvent.setIndActiveEvent(personDao.getIndActiveEvent(groupUpdateEvent.getIdPerson()));
			if (!org.springframework.util.StringUtils.isEmpty(groupUpdateEvent.getIdStage())) {
				groupUpdateEvent.setIndAllegation(
						personDao.getIndAllegation(groupUpdateEvent.getIdPerson(), groupUpdateEvent.getIdStage()));
			}
		}
		personDao.saveGroupUpdate(saveGroupUpdateReq);
		response.setMessage(ServiceConstants.SUCCESS);
		log.info("TransactionId :" + saveGroupUpdateReq.getTransactionId());
		return response;
	}

	/**
	 * Method Name: getPrimaryCaseworkerForStage Method Description: Method
	 * returns the Primary or Hitorical primary(if Closed Stage) Worker for the
	 * current stage.
	 *
	 * @param idStage
	 * @return idPerson
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getPrimaryCaseworkerForStage(Long idStage) {
		Long idPerson = stagePersonLinkDao.getPersonIdByRole(idStage, ServiceConstants.PRIMARY_WORKER);
		if (ObjectUtils.isEmpty(idPerson)) {
			idPerson = stagePersonLinkDao.getPersonIdByRole(idStage, ServiceConstants.HISTORICAL_PRIMARY);
		}
		CommonHelperRes response = new CommonHelperRes();
		response.setUlIdPerson(idPerson);
		ActBSandRSPhDto phoneDto = personPhoneDao.getActiveBSandRSPhone(idPerson);
		response.setPhoneNumber(phoneDto.getBSPhone());
		EmpNameDto empNameDto = nameDao.getNameByPersonId(idPerson);
		response.setNmPerson(
				empNameDto.getNmNameFirst() + ServiceConstants.CONSTANT_SPACE + empNameDto.getNmNameLast());
		return response;
	}

	/**
	 * Method Description: This Service checks if all persons have an open
	 * placement with same adult or resource.
	 *
	 * @param List
	 *            checkPersons
	 * @return String NO_PLACEMENTS - if there are no placements for any of the
	 *         person SINGLE_CAREGIVER - if the placements for all the persons
	 *         are with the same caregiver MULTIPLE_CAREGIVER -if the placements
	 *         for all the persons are not with the same caregiver
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String isPersPlcmtWithSameCareGiver(List<Long> personIds) {
		return personDao.isPersPlcmtWithSameCareGiver(personIds);

	}

	/**
	 * This method returns Person Ids for the given Day Care Request.
	 *
	 * @param idDayCareReqEvent
	 * @return List of Person Ids
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> getDayCarePersonIdList(Long idEvent) {
		return personDao.getDayCarePersonIdList(idEvent);
	}

	/**
	 * Method Name: isPersAtSameAddr :this service checks if all persons reside
	 * at the same location address Method Description:
	 *
	 * @param personIds
	 * @return Boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPersAtSameAddr(List<Long> personIds) {
		return personDao.isPersAtSameAddr(personIds);
	}

	/**
	 * Method Description: This method is used to call Person List Dao methods
	 * with input as idCase or stage_id and Stage Program. The risk assessment
	 * is has task code = 2290 and status of event should be comp or pend. The
	 * ind merge is set based on count checked in person merge table. Tuxedo
	 * Service Name: Modified CINV01S to include indicators for email, business
	 * fax and address required\ for child plan detail page
	 *
	 * @param retrievePersonListReq
	 * @return personListOut
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PersonListRes getPersonLists(PersonListReq retrievePersonListReq) {
		PersonListRes resp = getPersonList(retrievePersonListReq);
		for (int i = 0; i < resp.getRetrievePersonList().size(); i++) {
			PersonListDto personListDto = resp.getRetrievePersonList().get(i);
			// Getting the list of Active Business and Residence Fax
			ActBSandRSPhDto actBSandRSPhDto = personPhoneDao.getActiveBFPhone(personListDto.getIdPerson());
			if (!ObjectUtils.isEmpty(actBSandRSPhDto)) {
				resp.getRetrievePersonList().get(i).setIndPersonHasFax(ServiceConstants.YES);
			} else {
				resp.getRetrievePersonList().get(i).setIndPersonHasFax(ServiceConstants.NO);
			}
			AddressDtlReq addressDtlReq = new AddressDtlReq();
			addressDtlReq.setUlIdPerson(resp.getRetrievePersonList().get(i).getIdPerson());
			// Getting the List of All the Address for the particular Person
			// which are active i.e not end dated.
			List<AddressDto> addressList = personAddressDao.getAddressList(addressDtlReq);
			if (!ObjectUtils.isEmpty(addressList)) {
				// Filtering out the Valid Address if exists from the list of
				// Active Addresses.
				AddressDto addressDto = addressList.stream()
						.filter(address -> ServiceConstants.NO.equalsIgnoreCase(address.getIndPersAddrLinkInvalid()))
						.findAny().orElse(null);
				if (!ObjectUtils.isEmpty(addressDto)) {
					resp.getRetrievePersonList().get(i).setIndPersonHasAddrs(ServiceConstants.YES);
				} else {
					resp.getRetrievePersonList().get(i).setIndPersonHasAddrs(ServiceConstants.NO);
				}
			} else {
				resp.getRetrievePersonList().get(i).setIndPersonHasAddrs(ServiceConstants.NO);
			}
			// Getting the List of Email Address for a particular Person.
			List<PersonEmail> emailList = personEmailDao
					.getPersonEmailList(resp.getRetrievePersonList().get(i).getIdPerson());
			if (!ObjectUtils.isEmpty(emailList)) {
				// Filtering out the Valid and Active Email Address if exists.
				PersonEmail personEmail = emailList.stream()
						.filter(email -> ServiceConstants.NO.equalsIgnoreCase(String.valueOf(email.getIndInvalid()))
								&& ObjectUtils.isEmpty(email.getDtEnd()))
						.findAny().orElse(null);
				if (!ObjectUtils.isEmpty(personEmail)) {
					resp.getRetrievePersonList().get(i).setIndPersonHasEmail(ServiceConstants.YES);
				} else {
					resp.getRetrievePersonList().get(i).setIndPersonHasEmail(ServiceConstants.NO);
				}
			} else {
				resp.getRetrievePersonList().get(i).setIndPersonHasEmail(ServiceConstants.NO);
			}
		}

		return resp;

	}

	/**
	 * Method Name :getPersonListByStage Method Description: to get person list
	 * by stage id
	 *
	 * @param idStage
	 * @param idCase
	 *
	 * @return PersonListRes
	 */
	@Override
	public PersonListRes getPersonListByStage(PersonListReq personListReq) {
		PersonListRes personListRes = new PersonListRes();
		List<PersonListDto> personList = personDao.getPersonListByStage(personListReq.getIdStage());
		// added to Retrieve the Filtered List for domicile and deprivate screen
		// for model pop of select Person
		// personlist with the relation of parent birth or Adoptive parent
		/*
		 * List<PersonListDto> parentBirthRelation = personList.stream()
		 * .filter(o ->
		 * (ServiceConstants.CRPTRINT_PB.equals(o.getStagePersRelInt()) ||
		 * ServiceConstants.CRPTRINT_AP.equals(o.getStagePersRelInt())))
		 * .collect(Collectors.toList());
		 */
		personListRes.setRetrievePersonList(personList);
		// personListRes.setFilteredPersonList(parentBirthRelation);
		if (!ObjectUtils.isEmpty(personListReq.getIdCase())) {
			List<SubChildCauseNumberDto> subChildList = personDao.getSubChildCauseNumber(personListReq.getIdCase());
			if (!ObjectUtils.isEmpty(subChildList)) {
				Map<Long, SubChildCauseNumberDto> map = (HashMap<Long, SubChildCauseNumberDto>) subChildList.stream()
						.collect(Collectors.toMap(SubChildCauseNumberDto::getIdPerson,
								subChildCauseNumberDto -> subChildCauseNumberDto));
				for (PersonListDto person : personList) {
					if (!ObjectUtils.isEmpty(map) && !ObjectUtils.isEmpty(map.get(person.getIdPerson()))) {
						person.setCauseNbr(map.get(person.getIdPerson()).getTxtCauseNbr());
					}
				}
			}

			List<SubChildDto> subChildDtoList = personDao.getSubChildForCase(personListReq.getIdCase());
			if (!ObjectUtils.isEmpty(subChildList)) {
				Map<Long, SubChildDto> map = (HashMap<Long, SubChildDto>) subChildDtoList.stream()
						.collect(Collectors.toMap(SubChildDto::getIdPerson, subChildDto -> subChildDto));
				for (PersonListDto person : personList) {
					if (!ObjectUtils.isEmpty(map) && !ObjectUtils.isEmpty(map.get(person.getIdPerson()))) {
						person.setIndChildRmvd(ServiceConstants.Y);
					} else {
						person.setIndChildRmvd(ServiceConstants.N);
					}
				}
			}

		}
		return personListRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StagePersonValueDto selectStagePersonLink(Long idPerson, Long idStage) {
		StagePersonValueDto stagePerson = stageDao.selectStagePersonLink(idPerson, idStage);
		return stagePerson;

	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  IDListRes getAdminReviewOpenStagesByPerson(Long idPerson, Long idStage) {
		IDListRes ariOpenStageByPerson = new IDListRes();
		ariOpenStageByPerson.setIdList(adminReviewDao.getAdminReviewOpenStagesByPerson(idPerson, idStage));
		return ariOpenStageByPerson;

	}
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  IDListRes getAdminReviewOpenExists(Long idStage) {
		IDListRes ariOpenStageExists = new IDListRes();
		ariOpenStageExists.setIdList(adminReviewDao.getAdminReviewOpenExists(idStage));
		return ariOpenStageExists;

	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public  AdminReviewDto getAdminReviewDetails(Long idStage) {
		AdminReviewDto adminReviewDto = new AdminReviewDto();
		try{
			adminReviewDto = notifToParentEngDao.getAdminReview(idStage);
		}catch(Exception e){

		}
		return adminReviewDto;
	}
}
