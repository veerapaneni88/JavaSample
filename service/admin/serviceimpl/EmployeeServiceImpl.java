package us.tx.state.dfps.service.admin.serviceimpl;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import gov.texas.dfps.api.notification.client.NotificationApi;
import gov.texas.dfps.api.notification.client.model.EventAction;
import gov.texas.dfps.api.notification.client.model.EventObject;
import gov.texas.dfps.api.notification.client.model.Subscription;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dao.EmpSecClassLinkDao;
import us.tx.state.dfps.service.admin.dao.EmpTempAssignDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmpJobHisDto;
import us.tx.state.dfps.service.admin.dto.EmpNameDto;
import us.tx.state.dfps.service.admin.dto.EmpOfficeDto;
import us.tx.state.dfps.service.admin.dto.EmpPersonDto;
import us.tx.state.dfps.service.admin.dto.EmpPersonIdDto;
import us.tx.state.dfps.service.admin.dto.EmpSkillDto;
import us.tx.state.dfps.service.admin.dto.EmpTempAssignDto;
import us.tx.state.dfps.service.admin.dto.EmpUnitDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.admin.dto.EmployeeProfileDto;
import us.tx.state.dfps.service.admin.dto.EmployeeSearchByIdDto;
import us.tx.state.dfps.service.admin.dto.EmployeeSearchDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityStringDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceStringDto;
import us.tx.state.dfps.service.admin.dto.SSCCCatchmentDto;
import us.tx.state.dfps.service.admin.dto.SubscriptionDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkDto;
import us.tx.state.dfps.service.admin.dto.UserType;
import us.tx.state.dfps.service.admin.service.EmployeeService;
import us.tx.state.dfps.service.admin.service.PersonIdService;
import us.tx.state.dfps.service.admin.service.UnitService;
import us.tx.state.dfps.service.casepackage.dao.OfficeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.EditEmployeeReq;
import us.tx.state.dfps.service.common.request.ExtUserChildPlanStatusEnum;
import us.tx.state.dfps.service.common.request.ExtUserOrgResourceLinkDelReq;
import us.tx.state.dfps.service.common.request.SearchEmployeeByIdReq;
import us.tx.state.dfps.service.common.request.SearchEmployeeReq;
import us.tx.state.dfps.service.common.request.SubscriptionReq;
import us.tx.state.dfps.service.common.response.EditEmployeeRes;
import us.tx.state.dfps.service.common.response.EmployeeProfileRes;
import us.tx.state.dfps.service.common.response.SearchEmployeeByIdRes;
import us.tx.state.dfps.service.common.response.SearchEmployeeRes;
import us.tx.state.dfps.service.common.response.SubscriptionRes;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataMismatchException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.externalorg.service.ExtUserOrgMappingService;
import us.tx.state.dfps.service.externaluser.dto.ExternalUserDto;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PersonCategoryDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dao.PersonIdDao;
import us.tx.state.dfps.service.person.dao.PersonListDao;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dao.PersonRaceDao;
import us.tx.state.dfps.service.person.dto.ActBSandRSPhDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.SupervisorDOBDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.person.service.NameService;
import us.tx.state.dfps.service.person.service.PersonRaceEthnicityService;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.sscc.dao.SSCCEmployeeDetailDao;
import us.tx.state.dfps.service.workload.dao.CaseWorkloadDao;
import us.tx.state.dfps.service.workload.dao.OnCallDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.security.user.UserRolesEnum;
import us.tx.state.dfps.web.workload.bean.AssignBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Has service
 * methods related to Employee Dec 21, 2017- 4:15:21 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    public static final String ID_PERSON = "ID_PERSON";
    @Autowired
    CommonService commonService;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    PersonPhoneDao personPhoneDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PersonRaceDao personRaceDao;

    @Autowired
    NameDao nameDao;

    @Autowired
    OfficeDao officeDao;

    @Autowired
    PersonRaceEthnicityService personRaceEthnicityService;

    @Autowired
    PersonIdDao personIdDao;

    @Autowired
    UnitService unitService;

    @Autowired
    NameService nameService;

    @Autowired
    OnCallDao onCallDao;

    @Autowired
    TodoDao toDoDao;

    @Autowired
    StageWorkloadDao stageDao;

    @Autowired
    PersonCategoryDao personCategoryDao;

    @Autowired
    EmpTempAssignDao empTempAssignDao;

    @Autowired
    EmpSecClassLinkDao empSecClassLinkDao;

    @Autowired
    CaseWorkloadDao caseDao;

    @Autowired
    PersonEthnicityDao personEthnicityDao;

    @Autowired
    UnitDao unitDao;

    @Autowired
    PersonIdService personIdService;

    @Autowired
    PersonListDao personListDao;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ExtUserOrgMappingService extUserOrgMappingService;

    @Autowired
    private PopulateFormDao populateFormDao;

    @Autowired
    SSCCEmployeeDetailDao ssccEmployeeDetailDao;

    @Autowired
    CodesDao codesDao;

    private static final String SEC_PROFILE_RCCP = "RCCP_CM";

    private static final Logger log = Logger.getLogger(EmployeeServiceImpl.class);
    private static final Map<String, String> mapToSubscriptionTypes = new HashMap<>();
    private static final Map<String, String> mapToSubscriptionCodes = new HashMap<>();

    static {
        mapToSubscriptionTypes.put("AC", "CA_EX_COMMON_APPLICATION");
        mapToSubscriptionTypes.put("RE", "SSCC_REFERRAL");
    }

    static {
        mapToSubscriptionCodes.put("CA_EX_COMMON_APPLICATION", "AC");
        mapToSubscriptionCodes.put("SSCC_REFERRAL", "RE");
    }

    @Autowired
    NotificationApi notificationApi;

    private static String getMapToSubscriptionTypes(String key) {
        return mapToSubscriptionTypes.containsKey(key) ? mapToSubscriptionTypes.get(key) : null;
    }

    private static String getMapToSubscriptionCodes(String key) {
        return mapToSubscriptionCodes.containsKey(key) ? mapToSubscriptionCodes.get(key) : null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.tx.us.dfps.impact.admin.service.EmployeeService#searchEmployees(org.
     * tx.us.dfps.impact.request.SearchEmployeeReq)
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public SearchEmployeeRes searchEmployees(SearchEmployeeReq searchEmployeeReq) {
        HashMap<Long, EmployeeSearchDto> employeeSearchMap = null;
        Long resultCount = null;
        if (searchEmployeeReq.getSearchstageIds() != null && searchEmployeeReq.getSearchstageIds().size() > 0) {
			if (TypeConvUtil.isNullOrEmpty(searchEmployeeReq.getTotalRecCount())) {
				resultCount = employeeDao.EmpbyStageListRecCount(searchEmployeeReq);
			} else {resultCount = searchEmployeeReq.getTotalRecCount();}
            searchEmployeeReq.setTotalRecCount(resultCount);
			if (resultCount > 0) {employeeSearchMap = employeeDao.searchEmpbystageList(searchEmployeeReq);}
        } else {
			if (TypeConvUtil.isNullOrEmpty(searchEmployeeReq.getTotalRecCount())) {
				resultCount = employeeDao.EmpbyInputRecCount(searchEmployeeReq);
			} else {resultCount = searchEmployeeReq.getTotalRecCount();}
            searchEmployeeReq.setTotalRecCount(resultCount);
			if (resultCount > 0) {employeeSearchMap = employeeDao.searchEmpbyInput(searchEmployeeReq);}
        }
        // employeeSearchMap
        if (employeeSearchMap != null) {
            for (Entry<Long, EmployeeSearchDto> entry : employeeSearchMap.entrySet()) {
                EmployeeSearchDto rowEmployeeSearch = null;
                ActBSandRSPhDto actBSandRSPhDto = personPhoneDao.getActiveBSandRSPhone(entry.getKey());
                rowEmployeeSearch = entry.getValue();
                if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto)) {
					if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto.getRSPhone())) {
						rowEmployeeSearch.setSysNbrPersPhnHome(actBSandRSPhDto.getRSPhone());
					}
					if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto.getBSPhone())) {
						rowEmployeeSearch.setSysNbrPersPhoneWork(actBSandRSPhDto.getBSPhone());
					}
					if (!TypeConvUtil.isNullOrEmpty(actBSandRSPhDto.getBSPhoneExtension())) {
						rowEmployeeSearch.setNbrPhoneExtension(actBSandRSPhDto.getBSPhoneExtension());
					}
                }
                // search for dob
                if (!ObjectUtils.isEmpty(searchEmployeeReq.getSearchForInternalUser()) &&
                        !searchEmployeeReq.getSearchForInternalUser().booleanValue()) {
                    PersonDto personDto = personDao.getPersonById(entry.getKey());
                    if (!ObjectUtils.isEmpty(personDto) && !ObjectUtils.isEmpty(personDto.getDtPersonBirth())) {
                        rowEmployeeSearch.setDtPersonBirth(personDto.getDtPersonBirth());
                    }
                } else {
                    SupervisorDOBDto supervisorDOBDto = personDao.getSupervisorAndDobByPersonId(entry.getKey());
                    // set supervisor and DOB
                    if (!TypeConvUtil.isNullOrEmpty(supervisorDOBDto)) {
						if (!TypeConvUtil.isNullOrEmpty(supervisorDOBDto.getDtBirth())) {
							rowEmployeeSearch.setDtPersonBirth(supervisorDOBDto.getDtBirth());
						}
						if (!TypeConvUtil.isNullOrEmpty(supervisorDOBDto.getNmSupervisorFull())) {
							rowEmployeeSearch.setNmSupervisorFull(supervisorDOBDto.getNmSupervisorFull());
						}
						if (!TypeConvUtil.isNullOrEmpty(supervisorDOBDto.getIdsupervisor())) {
							rowEmployeeSearch.setIdPersonSupervisor(supervisorDOBDto.getIdsupervisor());
						}
                    }
                }
                if (!TypeConvUtil.isNullOrEmpty(rowEmployeeSearch.getIdPerson())) {
                    boolean isEnabled = employeeDao.getSecAttrEnabled(rowEmployeeSearch.getIdPerson());
                    rowEmployeeSearch.setPlusCF1050BSecAttrEnabled(isEnabled);
                }
                // external user search for address and email
                if (!ObjectUtils.isEmpty(searchEmployeeReq.getSearchForInternalUser()) &&
                        searchEmployeeReq.getSearchForInternalUser().booleanValue() == false) {
                    PersonDto personDto = personListDao.getIdPersonAddress(entry.getKey());
					if (!ObjectUtils.isEmpty(personDto)) {rowEmployeeSearch.setCity(personDto.getAddrPersonCity());}
                    List<PersonEmailDto> personEmailList = populateFormDao.returnEmailById(entry.getKey());
                    if (!ObjectUtils.isEmpty(personEmailList)) {
                        rowEmployeeSearch.setEmail(personEmailList.get(0).getTxtEmail());
                    }

                }

                if (!ObjectUtils.isEmpty(searchEmployeeReq.getSearchForInternalUser()) &&
                        searchEmployeeReq.getSearchForInternalUser().booleanValue() == false) {
                    rowEmployeeSearch.setImpactAccess(employeeDao.impactAccessForExternalUser(entry.getKey()));
                }
                entry.setValue(rowEmployeeSearch);
            }
        }
        SearchEmployeeRes searchEmployeeRes = new SearchEmployeeRes();
        searchEmployeeRes.setTotalRecCount(resultCount);
        searchEmployeeRes.setDtWCDDtSystemDate(new Date());
        searchEmployeeRes.setEmployeeSearchMap(employeeSearchMap);
        searchEmployeeRes.setTransactionId(searchEmployeeReq.getTransactionId());
        log.info("TransactionId :" + searchEmployeeReq.getTransactionId());
        return searchEmployeeRes;
    }

    /**
     * Method Description:searchEmployeeById
     *
     * @param searchEmployeeByIdReq
     * @return
     */
    // CCMN04S
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public SearchEmployeeByIdRes searchEmployeeById(SearchEmployeeByIdReq searchEmployeeByIdReq) {
        EmployeeDetailDto employeeDetailDto = null;
        EmployeeSearchByIdDto employeeSearchByIdDto = new EmployeeSearchByIdDto();
        SearchEmployeeByIdRes searchEmployeeByIdRes = new SearchEmployeeByIdRes();
        if (!ObjectUtils.isEmpty(searchEmployeeByIdReq) &&
                !ObjectUtils.isEmpty(searchEmployeeByIdReq.getIdEmployee())) {

            // populate employee information for external user other wise for
            // legacy staff
            if (searchEmployeeByIdReq.isExternalEmployee()) {
                employeeDetailDto = employeeDao.getExternalUserById(searchEmployeeByIdReq.getIdEmployee());

            } else {
                employeeDetailDto = employeeDao.getEmployeeById(searchEmployeeByIdReq.getIdEmployee());
            }
            if (TypeConvUtil.isNullOrEmpty(employeeDetailDto)) {
                throw new DataNotFoundException(
                        messageSource.getMessage("employee.not.found" + searchEmployeeByIdReq.getIdEmployee(), null,
                                Locale.US));
            } else {
                employeeDetailDto.setExternalEmployee(searchEmployeeByIdReq.isExternalEmployee());
                employeeSearchByIdDto = this.getEmployeeSearchByIdDto(employeeDetailDto);

                // set the boolean value for impact access if true has impact
                // access
                if (searchEmployeeByIdReq.isExternalEmployee()) {
                    boolean impactAccess =
                            employeeDao.impactAccessForExternalUser(searchEmployeeByIdReq.getIdEmployee());
                    employeeSearchByIdDto.setImpactAccess(impactAccess);
                }
                searchEmployeeByIdRes.setEmployeeSearchByIdDto(employeeSearchByIdDto);
            }
        }
        if (!ObjectUtils.isEmpty(searchEmployeeByIdReq) &&
                !ObjectUtils.isEmpty(searchEmployeeByIdReq.getTransactionId())) {
            searchEmployeeByIdRes.setTransactionId(searchEmployeeByIdReq.getTransactionId());
            log.info("TransactionId :" + searchEmployeeByIdReq.getTransactionId());
        }
        return searchEmployeeByIdRes;
    }

    /**
     * Method Description:getEmployeeSearchByIdDto
     *
     * @param employee
     * @return
     */
    // CCMN04S, CCMN03S
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmployeeSearchByIdDto getEmployeeSearchByIdDto(EmployeeDetailDto employeeDetailDto) {
        EmployeeSearchByIdDto employeeSearchByIdDto = new EmployeeSearchByIdDto();
        List<PersonRaceDto> personRaceDtoList = new ArrayList<>();
        List<PersonEthnicityDto> personEthnicityDtoList = new ArrayList<>();
        List<EmpJobHisDto> empJobHisDtoList = new ArrayList<>();
        List<EmpSkillDto> empSkillList = new ArrayList<>();
        List<Integer> ulRowQty = new ArrayList<>();
        List<EmpUnitDto> empUnitDtoList = new ArrayList<>();
        if (employeeDetailDto != null) {
            if (!TypeConvUtil.isNullOrEmpty(employeeDetailDto.getIdPerson())) {
                EmpPersonIdDto empPersonIdDto = personIdDao.getPersonIdByPersonId(employeeDetailDto.getIdPerson(),
                        ServiceConstants.PERSON_ID_TYPE_SSN, ServiceConstants.PERSON_ID_NOT_INVALID,
                        ServiceConstants.GENERIC_END_DATE);
                // External user check for ssn if ssn exist hide the values for
                // other Identifiers if not display the recent identifier based
                // on the date creation
                if (employeeDetailDto.isExternalEmployee()) {
                    if (ObjectUtils.isEmpty(empPersonIdDto)) {
                        List<PersonIdDto> persIdentifierList =
                                personIdDao.getPersonIdentifiersList(employeeDetailDto.getIdPerson());
                        if (!ObjectUtils.isEmpty(persIdentifierList)) {
                            persIdentifierList.sort((PersonIdDto o1, PersonIdDto o2) -> o1.getDtPersonIdStart()
                                    .compareTo(o2.getDtPersonIdStart()));
                            if (ObjectUtils.isEmpty(empPersonIdDto)) {
                                empPersonIdDto = new EmpPersonIdDto();
                            }
                            BeanUtils.copyProperties(persIdentifierList.get(0), empPersonIdDto);
                            empPersonIdDto.setActiveNonSSNIdentifier(true);
                            employeeSearchByIdDto.setSelectOtherIdTypeVal(empPersonIdDto.getCdPersonIdType());
                            employeeSearchByIdDto.setOtherId(empPersonIdDto.getPersonIdNumber());
                            empPersonIdDto.setPersonIdNumber(ServiceConstants.EMPTY_STRING);
                        }
                    } else {
                        empPersonIdDto.setActiveNonSSNIdentifier(false);
                        employeeSearchByIdDto.setSelectOtherIdTypeVal(ServiceConstants.EMPTY_STRING);
                        employeeSearchByIdDto.setOtherId(ServiceConstants.EMPTY_STRING);
                    }
                    // Email address for the external user
                    List<PersonEmailDto> personEmailList =
                            populateFormDao.returnEmailById(employeeDetailDto.getIdPerson());
                    if (!ObjectUtils.isEmpty(personEmailList)) {
                        employeeDetailDto.setEmployeeEmailAddress(personEmailList.get(0).getTxtEmail());
                    }
                }
                PersonDto personDto = personDao.getPersonById(employeeDetailDto.getIdPerson());
                personRaceDtoList = personRaceDao.getPersonRaceByPersonId(employeeDetailDto.getIdPerson());
                personEthnicityDtoList =
                        personEthnicityDao.getPersonEthnicityByPersonId(employeeDetailDto.getIdPerson());
                EmpNameDto empNameDto = nameDao.getNameByPersonId(employeeDetailDto.getIdPerson());
                empJobHisDtoList = employeeDao.getEmpJobHistoryByPersonId(employeeDetailDto.getIdPerson());
                empSkillList = employeeDao.getEmployeeSkillByPersonId(employeeDetailDto.getIdPerson());
                if (employeeDetailDto.getIndActiveStatus().equals(ServiceConstants.IND_EMP_ACTIVE_STATUS_Y)) {
                    // CCMN39D
                    empUnitDtoList = unitDao.searchUnitAttributesByPersonId(employeeDetailDto.getIdPerson(),
                            ServiceConstants.UNIT_MEMBER_IN_ASSIGNED);
                    if (!TypeConvUtil.isNullOrEmpty(employeeDetailDto.getIdOffice())) {
                        EmpOfficeDto empOfficeDto = officeDao.getOfficeById(employeeDetailDto.getIdOffice());
                        if (empOfficeDto != null) {
                            if (empOfficeDto.getCdOfficeMail() != null) {
                                employeeSearchByIdDto.setCdAddrMail(empOfficeDto.getCdOfficeMail());
                            }
                            if (empOfficeDto.getIdOffice() != null) {
                                employeeSearchByIdDto.setIdOffice(empOfficeDto.getIdOffice());
                            }
                            if (empOfficeDto.getNmOfficeName() != null) {
                                employeeSearchByIdDto.setNmOfficeName(empOfficeDto.getNmOfficeName());
                            }
                        }
                    }
                    // CCMN04U
                    if (empUnitDtoList != null) {
                        UnitDto unit = null;
                        List<UnitEmpLinkDto> uels = null;
                        List<Long> uelss = null;
                        for (EmpUnitDto empUnitDto : empUnitDtoList) {
                            if (!TypeConvUtil.isNullOrEmpty(empUnitDto.getIdUnit())) {
                                unit = unitDao.searchUnitById(empUnitDto.getIdUnit());
                            } else {
                                if (empUnitDto.getCdUnitProgram() != null && empUnitDto.getCdUnitRegion() != null &&
                                        empUnitDto.getUnit() != null) {
                                    unit = unitDao.searchUnitByAttributes(empUnitDto.getCdUnitProgram(),
                                            empUnitDto.getCdUnitRegion(), empUnitDto.getUnit());
                                }
                            }
                            if (unit != null) {
                                if (null != unit.getIdPerson()) {
                                    if ((unit.getIdPerson()).equals(employeeDetailDto.getIdPerson())) {
                                        empUnitDto.setSysCdWinMode(ServiceConstants.WINDOW_MODE_MODIFY);
                                    } else {
                                        if (!ObjectUtils.isEmpty(unit) && null != unit.getIdUnit() &&
                                                !ObjectUtils.isEmpty(unit.getIdPerson())) {
                                            // CCMND5D
                                            uels = unitDao.searchUnitEmpLinkByUnitPersonId(unit.getIdUnit(),
                                                    unit.getIdPerson());
                                        }
                                        if (uels != null) {
                                            for (UnitEmpLinkDto uel : uels) {
                                                if (null != uel.getIdUnit() && null != uel.getIdPerson() &&
                                                        null != uel.getUnitMemberRole()) {
                                                    // CCMN32D
                                                    uelss = unitDao.searchUnitEmpLinkByUnitPersonIdAndRole(
                                                            uel.getIdUnit(), uel.getIdPerson(), uel.getUnitMemberRole(),
                                                            ServiceConstants.UNIT_MEMBER_ROLE_CLERK);
                                                }
                                                if (uelss != null) {
                                                    empUnitDto.setSysCdWinMode(ServiceConstants.WINDOW_MODE_MODIFY);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                empUnitDto.setSysCdWinMode(ServiceConstants.WINDOW_MODE_MODIFY);
                            }
                        }
                        if (empUnitDtoList != null) {
                            if (empUnitDtoList.size() > 0) {
                                employeeSearchByIdDto.setEmpUnitDto(empUnitDtoList.get(0));
                            }
                        }
                    }
                }
                employeeSearchByIdDto.setEmployeeDetailDto(employeeDetailDto);
                if (empPersonIdDto != null) {
                    employeeSearchByIdDto.setEmpPersonIdDto(empPersonIdDto);
                }
                if (personDto != null) {
                    employeeSearchByIdDto.setEmpPersonDto(this.getEmployeePersonDto(personDto));
                }
                if (empNameDto != null) {
                    employeeSearchByIdDto.setEmpNameDto(empNameDto);
                }
                if (personRaceDtoList != null) {
                    ulRowQty.add(personRaceDtoList.size());
                    employeeSearchByIdDto.setPersonRaceDtoList(personRaceDtoList);
                }
                if (personEthnicityDtoList != null) {
                    ulRowQty.add(personEthnicityDtoList.size());
                    employeeSearchByIdDto.setPersonEthnicityDtoList(personEthnicityDtoList);
                }
                if (empJobHisDtoList != null) {
                    ulRowQty.add(empJobHisDtoList.size());
                    employeeSearchByIdDto.setEmpJobHisDtoList(empJobHisDtoList);
                }
                if (empSkillList != null) {
                    ulRowQty.add(empSkillList.size());
                    employeeSearchByIdDto.setEmpSkillDtoList(empSkillList);
                }
                employeeSearchByIdDto.setRowQty(ulRowQty);
            }
        }
        return employeeSearchByIdDto;
    }

    /**
     * Method Description:editEmployee
     *
     * @param editEmployeeReq
     * @return editEmployeeRes
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes editEmployee(EditEmployeeReq editEmployeeReq) {

        /// Defectfix # artf253111 Check for existing office , and create new office if not present
        // artf230759 : Check for emOfficeDto not null to avoid Null pointer
        if(editEmployeeReq.getEmpOfficeDto()  != null) {
            Office office = officeDao.getOfficeName(editEmployeeReq.getEmpOfficeDto().getCdOfficeMail(),
                    editEmployeeReq.getEmpUnitDto().getCdUnitRegion(), editEmployeeReq.getEmpUnitDto().getCdUnitProgram());
            if (office == null) {
                String mailCode = editEmployeeReq.getEmpOfficeDto().getCdOfficeMail();
                MailCode mailCodeObj = new MailCode();
                if (mailCode != null && mailCode.matches("^[a-zA-Z0-9-]{4}")) {
                    mailCodeObj = officeDao.getMailCode(mailCode);
                }

                office = officeDao.createOffice(editEmployeeReq.getEmpOfficeDto().getCdOfficeMail(),
                        editEmployeeReq.getEmpUnitDto().getCdUnitRegion(), editEmployeeReq.getEmpUnitDto().getCdUnitProgram(),
                        mailCodeObj.getAddrMailCodeCity());
            }

            editEmployeeReq.getEmployeeDetailDto().setIdOffice(office.getIdOffice());
            editEmployeeReq.getEmpOfficeDto().setIdOffice(office.getIdOffice());
        }
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getUlIdPerson())) {
                        editEmployeeRes = this.deleteEmployee(editEmployeeReq);
                    }
                } else if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                    editEmployeeRes = this.addEmployee(editEmployeeReq);
                } else if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getUlIdPerson())) {
                        editEmployeeRes = this.updateEmployee(editEmployeeReq);
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty(editEmployeeReq) && !ObjectUtils.isEmpty(editEmployeeReq.getTransactionId())) {
            editEmployeeRes.setTransactionId(editEmployeeReq.getTransactionId());
            log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        }
        return editEmployeeRes;
    }

    /**
     * Method Description:getEmployeeDetailDto
     *
     * @param employee
     * @return
     */
    @Override
    public EmployeeDetailDto getEmployeeDetailDto(Employee employee) {
        EmployeeDetailDto employeeDetailDto = new EmployeeDetailDto();
        if (employee != null) {
            if (employee.getIdPerson() != null) {
                employeeDetailDto.setIdPerson(employee.getIdPerson());
            }
            if (employee.getOffice() != null && employee.getOffice().getIdOffice() != null) {
                employeeDetailDto.setIdOffice(employee.getOffice().getIdOffice());
            }
            if (employee.getIdEmpJobHistory() != null) {
                employeeDetailDto.setIdEmpJobHistory(employee.getIdEmpJobHistory());
            }
            if (employee.getIdEmployeeLogon() != null) {
                employeeDetailDto.setIdEmployeeLogon(employee.getIdEmployeeLogon());
            }
            if (employee.getIndEmpActiveStatus() != null) {
                employeeDetailDto.setIndActiveStatus(employee.getIndEmpActiveStatus());
            }
            if (employee.getIndEmpConfirmedHrmis() != null) {
                employeeDetailDto.setIndEmpConfirmedHrmis(employee.getIndEmpConfirmedHrmis());
            }
            if (employee.getIndEmpPendingHrmis() != null) {
                employeeDetailDto.setIndEmpPendingHrmis(employee.getIndEmpPendingHrmis());
            }
            if (Short.valueOf(employee.getNbrEmpActivePct()) != null) {
                employeeDetailDto.setNbrEmpActivePct(employee.getNbrEmpActivePct());
            }
            if (employee.getDtLastUpdate() != null) {
                employeeDetailDto.setTsLastUpdate(employee.getDtLastUpdate());
            }
            if (employee.getCdExternalType() != null) {
                employeeDetailDto.setCdExternalUserType(employee.getCdExternalType());
            }
            if (employee.getTxtEmailAddress() != null) {
                employeeDetailDto.setEmailAddress(employee.getTxtEmailAddress());
            }
            if (employee.getTxtEmployeeEmailAddress() != null) {
                employeeDetailDto.setEmailAddress(employee.getTxtEmployeeEmailAddress());
            }
            if (employee.getDtHrTerm() != null) {
                employeeDetailDto.setDtHrEmpTerm(employee.getDtHrTerm());
            }
            if (employee.getDtHrHire() != null) {
                employeeDetailDto.setDtHrHire(employee.getDtHrHire());
            }
            if (employee.getIndExtAccess() != null) {
                employeeDetailDto.setIndExtAccess(employee.getIndExtAccess());
            }
            if (employee.getTxtBirthCity() != null) {
                employeeDetailDto.setBirthCity(employee.getTxtBirthCity());
            }
            if (employee.getCdBirthState() != null) {
                employeeDetailDto.setCdBirthState(employee.getCdBirthState());
            }
            if (employee.getIdReqsitn() != null) {
                employeeDetailDto.setIdReqsitn(employee.getIdReqsitn());
            }
            if (employee.getNbrHhscPurchsOrder() != null) {
                employeeDetailDto.setHhscPurchsOrder(employee.getNbrHhscPurchsOrder());
            }
            if (employee.getIdDept() != null) {
                employeeDetailDto.setIdDept(employee.getIdDept());
            }
            if (employee.getTxtRoleJobDuty() != null) {
                employeeDetailDto.setRoleJobDuty(employee.getTxtRoleJobDuty());
            }
            if (employee.getCdEmpProgram() != null) {
                employeeDetailDto.setCdEmpProgram(employee.getCdEmpProgram());
            }
            if (employee.getCdEmployeeClass() != null) {
                employeeDetailDto.setCdEmployeeClass(employee.getCdEmployeeClass());
            }
            if (employee.getDtEmpHire() != null) {
                employeeDetailDto.setDtEmpHire(employee.getDtEmpHire());
            }
            if (employee.getDtEmpLastAssigned() != null) {
                employeeDetailDto.setDtEmpLastAssigned(employee.getDtEmpLastAssigned());
            }
            if (employee.getDtEmpTermination() != null) {
                employeeDetailDto.setDtEmpTermination(employee.getDtEmpTermination());
            }
            if (employee.getIdEmployeeLogon() != null) {
                employeeDetailDto.setIdEmployeeLogon(employee.getIdEmployeeLogon());
            }
        }
        return employeeDetailDto;
    }

    /**
     * Method : getExternalEmployeeDetailDto Method Description : To get the
     * external Employee Details
     *
     * @param ExtrnlUserloyee
     * @return EmployeeDetailDto
     */
    @Override
    public EmployeeDetailDto getExternalEmployeeDetailDto(ExtrnlUser ExtrnlUserloyee) {
        EmployeeDetailDto employeeDetailDto = new EmployeeDetailDto();
        if (ExtrnlUserloyee != null) {
            if (ExtrnlUserloyee.getIdPerson() != null) {
                employeeDetailDto.setIdPerson(ExtrnlUserloyee.getIdPerson());
            }
            if (ExtrnlUserloyee.getDtLastUpdate() != null) {
                employeeDetailDto.setTsLastUpdate(ExtrnlUserloyee.getDtLastUpdate());
            }
            /*
             * if (ExtrnlUserloyee.getIdExtrnlUserLogon() != null) {
             * employeeDetailDto.setIdEmployeeLogon(ExtrnlUserloyee.
             * getIdExtrnlUserLogon()); }
             */
            if (ExtrnlUserloyee.getDtExtrnlUserStart() != null) {
                employeeDetailDto.setDtHrHire(ExtrnlUserloyee.getDtExtrnlUserStart());
            }
            if (ExtrnlUserloyee.getDtExtrnlUserEnd() != null) {
                employeeDetailDto.setDtEmpTermination(ExtrnlUserloyee.getDtExtrnlUserEnd());
            }

        }
        return employeeDetailDto;
    }

    /**
     * Method Description:getEmployeePersonDto
     *
     * @param person
     * @return
     */
    @Override
    public EmpPersonDto getEmployeePersonDto(PersonDto personDto) {
        EmpPersonDto empPersonDto = new EmpPersonDto();
        if (personDto != null) {
            if (!ObjectUtils.isEmpty(personDto) && null != (personDto.getIdPerson())) {
                empPersonDto.setIdPerson(personDto.getIdPerson());
            }
            if (personDto.getCdPersonSex() != null) {
                empPersonDto.setPersonSex(personDto.getCdPersonSex());
            }
            if (personDto.getCdPersonEthnicGroup() != null) {
                empPersonDto.setCdPersonEthnicGroup(personDto.getCdPersonEthnicGroup());
            }
            if (personDto.getDtPersonBirth() != null) {
                empPersonDto.setDtPersonBirth(personDto.getDtPersonBirth());
            }
            if (personDto.getNmPersonFull() != null) {
                empPersonDto.setNmPersonFull(personDto.getNmPersonFull());
            }
            if (personDto.getDtLastUpdate() != null) {
                empPersonDto.setTsLastUpdate(personDto.getDtLastUpdate());
            }
        }
        return empPersonDto;
    }

    /**
     * Method Description:getEmpJobHisDto
     *
     * @param ejh
     * @return
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpJobHisDto getEmpJobHisDto(EmpJobHistory ejh) {
        EmpJobHisDto empJobHisDto = new EmpJobHisDto();
        if (ejh != null) {
            if (ejh.getCdJobBjn() != null) {
                empJobHisDto.setBjnJob(ejh.getCdJobBjn());
            }
            if (ejh.getCdJobClass() != null) {
                empJobHisDto.setCdJobClass(ejh.getCdJobClass());
            }
            if (ejh.getCdJobFunction() != null) {
                empJobHisDto.setCdJobFunction(ejh.getCdJobFunction());
            }
            if (ejh.getDtJobEnd() != null) {
                empJobHisDto.setDtJobEnd(ejh.getDtJobEnd());
            }
            if (ejh.getDtJobStart() != null) {
                empJobHisDto.setDtJobStart(ejh.getDtJobStart());
            }
            if (ejh.getIdEmpJobHistory() != null) {
                empJobHisDto.setIdEmpJobHistory(ejh.getIdEmpJobHistory());
            }
            if (!ObjectUtils.isEmpty(ejh) && ejh.getPerson() != null && ejh.getPerson().getIdPerson() != null) {
                empJobHisDto.setIdPerson(ejh.getPerson().getIdPerson());
            }
            if (ejh.getIdJobPersSupv() != null) {
                empJobHisDto.setIdJobPersSupv(ejh.getIdJobPersSupv());
                empJobHisDto.setNmPersonFull(personDao.getPersonById(ejh.getIdJobPersSupv()).getNmPersonFull());
            }
            if (ejh.getIndJobAssignable() != null) {
                empJobHisDto.setIndJobAssignable(ejh.getIndJobAssignable());
            }
            if (ejh.getDtLastUpdate() != null) {
                empJobHisDto.setTsLastUpdate(ejh.getDtLastUpdate());
            }
        }
        return empJobHisDto;
    }

    /**
     * Method Description:getEmpSkillDto
     *
     * @param employeeSkill
     * @return
     */
    @Override
    public EmpSkillDto getEmpSkillDto(EmployeeSkill employeeSkill) {
        EmpSkillDto empSkillDto = new EmpSkillDto();
        if (employeeSkill != null && null != (employeeSkill.getIdPerson())) {
            empSkillDto.setIdPerson(employeeSkill.getIdPerson());
            if (employeeSkill.getCdEmpSkill() != null) {
                empSkillDto.setCdEmpSkill(employeeSkill.getCdEmpSkill());
            }
            if (employeeSkill.getIdEmployeeSkill() != null) {
                empSkillDto.setIdEmpSkill(employeeSkill.getIdEmployeeSkill());
            }
        }
        return empSkillDto;
    }

    /**
     * Method Description:deleteEmployee
     *
     * @param editEmployeeReq
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes deleteEmployee(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        List<Long> units = new ArrayList<Long>();
        List<Long> onCalls = new ArrayList<Long>();
        List<Long> todos = new ArrayList<Long>();
        List<Long> prStagePersonLinks = new ArrayList<Long>();
        List<Long> seStagePersonLinks = new ArrayList<Long>();
        List<String> personCategorys = new ArrayList<String>();
        List<String> szCdPersonRaceList = new ArrayList<String>();
        List<String> szCdPersonEthnicityList = new ArrayList<String>();
        PersonRaceStringDto personRaceStringDto = new PersonRaceStringDto();
        PersonEthnicityStringDto personEthnicityStringDto = new PersonEthnicityStringDto();
        List<PersonRaceDto> personRaces = new ArrayList<PersonRaceDto>();
        List<PersonEthnicityDto> personEthnicitys = new ArrayList<PersonEthnicityDto>();
        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    if (editEmployeeReq.getUlIdPerson() != null) {
                        // CCMNG6D
                        units = unitDao.searchUnitByPersonId(editEmployeeReq.getUlIdPerson());
                        if (units.size() == 0) {
                            // CCMNE6D
                            onCalls = onCallDao.getCurrentOnCallByPersonId(editEmployeeReq.getUlIdPerson());
                            if (onCalls.size() == 0) {
                                // CCMNE7D
                                todos = toDoDao.getTodoBypersonId(editEmployeeReq.getUlIdPerson());
                                if (todos.size() == 0) {
                                    // CCMNE8D
                                    prStagePersonLinks = stageDao.searchStageIdsFromLinkByPersonIdAndStageRole(
                                            editEmployeeReq.getUlIdPerson(), ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
                                    if (prStagePersonLinks.size() == 0) {
                                        // CCMNE8D
                                        seStagePersonLinks = stageDao.searchStageIdsFromLinkByPersonIdAndStageRole(
                                                editEmployeeReq.getUlIdPerson(), ServiceConstants.SEC_ROLE_STAGE_OPEN);
                                        if (seStagePersonLinks.size() == 0) {
                                            // CCMNE9D
                                            personCategorys = personCategoryDao.getOtherPersonCategoryByPersonId(
                                                    editEmployeeReq.getUlIdPerson(),
                                                    ServiceConstants.PERSON_CATEGORY_EMPLOYEE);
                                            // CCMNF5D
                                            if (personCategorys.size() > 0) {
                                                employeeDao.complexDeleteEmployee(editEmployeeReq.getUlIdPerson());
                                            }
                                            // CCMNF7D
                                            else if (personCategorys.size() == 0) {
                                                employeeDao.deleteEmployeeJobHistoryByPersonId(
                                                        editEmployeeReq.getUlIdPerson());
                                                employeeDao.deleteEmployeeHistoryByPersonId(
                                                        editEmployeeReq.getUlIdPerson());
                                                employeeDao.complexDeletePerson(editEmployeeReq.getUlIdPerson());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        editEmployeeRes.setUlIdPerson(editEmployeeReq.getUlIdPerson());
                        personRaces = personRaceDao.getPersonRaceByPersonId(editEmployeeReq.getUlIdPerson());
                        personEthnicitys =
                                personEthnicityDao.getPersonEthnicityByPersonId(editEmployeeReq.getUlIdPerson());
                        if (personRaces.size() > 0) {
                            for (PersonRaceDto personRace : personRaces) {
                                szCdPersonRaceList.add(personRace.getCdPersonRace());
                            }
                            personRaceStringDto.setCdPersonRace(szCdPersonRaceList);
                        }
                        if (personEthnicitys.size() > 0) {
                            for (PersonEthnicityDto personEthnicity : personEthnicitys) {
                                szCdPersonEthnicityList.add(personEthnicity.getCdPersonEthnicity());
                            }
                            personEthnicityStringDto.setCdPersonEthnicity(szCdPersonEthnicityList);
                        }
                        editEmployeeRes.setPersonRaceStringDto(personRaceStringDto);
                        editEmployeeRes.setPersonEthnicityStringDto(personEthnicityStringDto);
                        editEmployeeRes.setActionResult("Delete Successfully!");
                    }
                }
            }
        }

        if (!ObjectUtils.isEmpty(editEmployeeReq) && !ObjectUtils.isEmpty(editEmployeeReq.getTransactionId())) {
            editEmployeeRes.setTransactionId(editEmployeeReq.getTransactionId());
            log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        }
        return editEmployeeRes;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes validateEmployee(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        EmpPersonIdDto personIdDto = null;
        List<Long> idPersons = new ArrayList<Long>();

        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD) ||
                        editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    if (editEmployeeReq.getEmpOfficeDto() != null && editEmployeeReq.getEmpUnitDto() != null) {
                        String mailCode = editEmployeeReq.getEmpOfficeDto().getCdOfficeMail();
                        if (mailCode != null && mailCode.matches("^[a-zA-Z0-9-]{4}")) {
                            // Defect fix # artf253111
                            // For valid mail code , setting the mail city
                            // If MailCode is not present then return error.
                            MailCode mailCodeObj = officeDao.getMailCode(mailCode);

                            if (ObjectUtils.isEmpty(mailCodeObj)) {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.setErrorCode(ServiceConstants.MSG_CMN_INVALID_MAIL_CD);
                                editEmployeeRes.setErrorDto(errorDto);
                                return editEmployeeRes;
                            }
                        } else {
                             ErrorDto errorDto = new ErrorDto();
                             errorDto.setErrorCode(ServiceConstants.MSG_CMN_INVALID_MAIL_CD);
                             editEmployeeRes.setErrorDto(errorDto);
                             return editEmployeeRes;
                         }
                    }
                    if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                        if (editEmployeeReq.getEmpPersonIdDto() != null) {
                            personIdDto = editEmployeeReq.getEmpPersonIdDto();
                            if (personIdDto.getCdPersonIdType() != null && personIdDto.getPersonIdNumber() != null) {
                                if (!personIdDto.getCdPersonIdType().equals(ServiceConstants.EMPTY_STRING) &&
                                        !personIdDto.getPersonIdNumber().equals(ServiceConstants.EMPTY_STRING)) {
                                    // CCMND6D
                                    idPersons = personIdDao.getPersonIdTableByNBRPersonIdNumber(
                                            personIdDto.getPersonIdNumber(), personIdDto.getCdPersonIdType());
                                    if (idPersons.size() > 0) {
                                        ErrorDto errorDto = new ErrorDto();
                                        errorDto.setErrorCode(ServiceConstants.MSG_CMN_SSN_NOT_UNIQUE);
                                        editEmployeeRes.setErrorDto(errorDto);
                                        return editEmployeeRes;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        return editEmployeeRes;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes validateUnitChange(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        EmpUnitDto unitDto = null;
        Long unitEmpLinkCount = ServiceConstants.ZERO_VAL;
        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD) ||
                        editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    if (editEmployeeReq.getEmpUnitDto() != null) {
                        unitDto = editEmployeeReq.getEmpUnitDto();
                        if ((!ObjectUtils.isEmpty(unitDto)) && null != (unitDto.getIdUnit())) {
                            // CCMNI1D
                            unitEmpLinkCount = unitDao.getUnitEmpLinkCountByUnitId(unitDto.getIdUnit());
                            if (unitEmpLinkCount >= ServiceConstants.MAX_STAFF_MEMEBER) {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.setErrorCode(ServiceConstants.MSG_CMN_MORE_THAN_100_MEMBERS);
                                editEmployeeRes.setErrorDto(errorDto);
                                return editEmployeeRes;
                            }
                        }
                    }
                }
            }
        }
        log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        return editEmployeeRes;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes validateRoleChange(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        EmpUnitDto unitDto = null;
        Long unitEmpLinkCombinationCount = ServiceConstants.ZERO_VAL;
        List<String> roles = new ArrayList<String>();
        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD) ||
                        editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    if (editEmployeeReq.getEmpUnitDto() != null) {
                        unitDto = editEmployeeReq.getEmpUnitDto();
                        if (unitDto.getUnitMemberRole() != null) {
                            if (!unitDto.getUnitMemberRole().equals(ServiceConstants.EMPTY_STRING)) {
                                if (unitDto.getUnitMemberRole().equals(ServiceConstants.UNIT_MEMBER_ROLE_20) ||
                                        unitDto.getUnitMemberRole().equals(ServiceConstants.UNIT_MEMBER_ROLE_30) ||
                                        unitDto.getUnitMemberRole().equals(ServiceConstants.UNIT_MEMBER_ROLE_40)) {
                                    // CSESB9D
                                    roles.add(ServiceConstants.UNIT_MEMBER_ROLE_20);
                                    roles.add(ServiceConstants.UNIT_MEMBER_ROLE_30);
                                    roles.add(ServiceConstants.UNIT_MEMBER_ROLE_40);
                                    unitEmpLinkCombinationCount =
                                            unitDao.getUnitEmpLinkCountByUnitIdAndRole(unitDto.getIdUnit(), roles);
                                    if (unitEmpLinkCombinationCount >= ServiceConstants.MAX_ROW_TYPE) {
                                        ErrorDto errorDto = new ErrorDto();
                                        errorDto.setErrorCode(ServiceConstants.MSG_PRNT_LEAD_MAINT);
                                        editEmployeeRes.setErrorDto(errorDto);
                                        return editEmployeeRes;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        return editEmployeeRes;
    }

    /**
     * Method Description:addEmployee
     *
     * @param editEmployeeReq
     * @return editEmployeeRes
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes addEmployee(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        EmpPersonIdDto personIdDto = null;
        EmpUnitDto unitDto = null;
        List<Long> idPersons = new ArrayList<Long>();
        Long unitEmpLinkCount = ServiceConstants.ZERO_VAL;
        Long unitEmpLinkCombinationCount = ServiceConstants.ZERO_VAL;
        List<String> roles = new ArrayList<String>();
        boolean uniqueSSNCheck = true;
        boolean unitMoreThan100Check = true;
        boolean unitCombination40Check = true;
        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                    if (editEmployeeReq.getEmpPersonIdDto() != null) {
                        personIdDto = editEmployeeReq.getEmpPersonIdDto();
                        if (personIdDto.getCdPersonIdType() != null && personIdDto.getPersonIdNumber() != null) {
                            if (!personIdDto.getCdPersonIdType().equals(ServiceConstants.EMPTY_STRING) &&
                                    !personIdDto.getPersonIdNumber().equals(ServiceConstants.EMPTY_STRING)) {
                                // CCMND6D
                                idPersons =
                                        personIdDao.getPersonIdTableByNBRPersonIdNumber(personIdDto.getPersonIdNumber(),
                                                personIdDto.getCdPersonIdType());
                                if (idPersons.size() > 0) {
                                    uniqueSSNCheck = false;
                                }
                            }
                        }
                    }
                    if (uniqueSSNCheck) {
                        if (editEmployeeReq.getEmpUnitDto() != null) {
                            unitDto = editEmployeeReq.getEmpUnitDto();
                            if ((!ObjectUtils.isEmpty(unitDto)) && null != (unitDto.getIdUnit())) {
                                // CCMNI1D
                                unitEmpLinkCount = unitDao.getUnitEmpLinkCountByUnitId(unitDto.getIdUnit());
                                if (unitEmpLinkCount >= ServiceConstants.MAX_STAFF_MEMEBER) {
                                    unitMoreThan100Check = false;
                                }
                            }
                        }
                    }
                    if (unitMoreThan100Check) {
                        if (editEmployeeReq.getEmpUnitDto() != null) {
                            unitDto = editEmployeeReq.getEmpUnitDto();
                            if (unitDto.getUnitMemberRole() != null) {
                                if (!unitDto.getUnitMemberRole().equals(ServiceConstants.EMPTY_STRING)) {
                                    if (unitDto.getUnitMemberRole().equals(ServiceConstants.UNIT_MEMBER_ROLE_20) ||
                                            unitDto.getUnitMemberRole().equals(ServiceConstants.UNIT_MEMBER_ROLE_30) ||
                                            unitDto.getUnitMemberRole().equals(ServiceConstants.UNIT_MEMBER_ROLE_40)) {
                                        // CSESB9D
                                        roles.add(ServiceConstants.UNIT_MEMBER_ROLE_20);
                                        roles.add(ServiceConstants.UNIT_MEMBER_ROLE_30);
                                        roles.add(ServiceConstants.UNIT_MEMBER_ROLE_40);
                                        unitEmpLinkCombinationCount =
                                                unitDao.getUnitEmpLinkCountByUnitIdAndRole(unitDto.getIdUnit(), roles);
                                        if (unitEmpLinkCombinationCount >= ServiceConstants.MAX_ROW_TYPE) {
                                            unitCombination40Check = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (unitCombination40Check) {
                        editEmployeeRes = this.saveEmployeeProcedure(editEmployeeReq);
                        editEmployeeRes.setActionResult("Add Successfully!");
                    }
                }
            }
        }
        if ((!ObjectUtils.isEmpty(editEmployeeReq))) {
            log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        }
        return editEmployeeRes;
    }

    /**
     * Method Description:SaveUpdateEmpPersonDto
     *
     * @param empPersonDto
     * @param action
     * @return EmpPersonDto
     */
    // CCMN71D
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpPersonDto saveUpdateEmpPersonDto(EmpPersonDto empPersonDto, String action) {
        EmpPersonDto personDto = new EmpPersonDto();
        Person person = new Person();
        Date date = new Date();
        if (empPersonDto != null) {
            if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(action) && empPersonDto.getIdPerson() != null) {
                person = personDao.getPersonByPersonId(empPersonDto.getIdPerson());
                if (person != null) {
                    person.setDtLastUpdate(date);
                }
            }
            if (empPersonDto.getPersonSex() != null) {
                if (!empPersonDto.getPersonSex().equals(ServiceConstants.EMPTY_STRING)) {
                    person.setCdPersonSex(empPersonDto.getPersonSex());
                }
            }
            if (empPersonDto.getNmPersonFull() != null) {
                if (!empPersonDto.getNmPersonFull().equals(ServiceConstants.EMPTY_STRING)) {
                    person.setNmPersonFull(empPersonDto.getNmPersonFull());
                }
            }
            if (empPersonDto.getCdPersonEthnicGroup() != null) {
                if (!empPersonDto.getCdPersonEthnicGroup().equals(ServiceConstants.EMPTY_STRING)) {
                    person.setCdPersonEthnicGroup(empPersonDto.getCdPersonEthnicGroup());
                }
            }
            if (empPersonDto.getDtPersonBirth() != null) {
                person.setDtPersonBirth(empPersonDto.getDtPersonBirth());
            }
            if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                personDao.savePerson(person);
            } else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                personDao.updatePerson(person);
            }
            if (person != null) {
                if (person.getIdPerson() != null) {
                    personDto = this.getEmployeePersonDto(personDao.getPersonById(person.getIdPerson()));
                }
            }
        }
        return personDto;
    }

    /**
     * Method Description:SaveUpdateEmpJobHisDto
     *
     * @param empJobHisDto
     * @param action
     * @return EmpJobHisDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpJobHisDto saveUpdateEmpJobHisDto(EmpJobHisDto empJobHisDto, String action) {
        EmpJobHisDto jobHisDto = new EmpJobHisDto();
        EmpJobHistory empJobHistory = new EmpJobHistory();
        Person person = new Person();
        Date date = new Date();
        if (empJobHisDto != null) {

            if (!action.equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
                if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE) ||
                        action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    if (empJobHisDto.getIdEmpJobHistory() != null) {
                        empJobHistory = employeeDao.getEmpJobHistoryById(empJobHisDto.getIdEmpJobHistory());
                    }
                    // artf230759 : Moving constant left to check null pointer
                    if(!ObjectUtils.isEmpty(empJobHisDto.getIndJobAssignable())){
                        empJobHistory.setIndJobAssignable(empJobHisDto.getIndJobAssignable());
                    }
                }
                empJobHistory.setDtLastUpdate(date);
                if ((!ObjectUtils.isEmpty(empJobHisDto)) && null != (empJobHisDto.getIdPerson())) {
                    person = personDao.getPersonByPersonId(empJobHisDto.getIdPerson());
                    empJobHistory.setPerson(person);
                }
                if (empJobHisDto.getBjnJob() != null) {
                    if (!empJobHisDto.getBjnJob().equals(ServiceConstants.EMPTY_STRING)) {
                        empJobHistory.setCdJobBjn(empJobHisDto.getBjnJob());
                    }
                }
                if (empJobHisDto.getCdJobClass() != null) {
                    if (!empJobHisDto.getCdJobClass().equals(ServiceConstants.EMPTY_STRING)) {
                        empJobHistory.setCdJobClass(empJobHisDto.getCdJobClass());
                    }
                }
                if (empJobHisDto.getCdJobFunction() != null) {
                    empJobHistory.setCdJobFunction(empJobHisDto.getCdJobFunction());
                }
                if (empJobHisDto.getDtJobEnd() != null) {
                    empJobHistory.setDtJobEnd(empJobHisDto.getDtJobEnd());
                }
                if (empJobHisDto.getDtJobStart() != null) {
                    empJobHistory.setDtJobStart(empJobHisDto.getDtJobStart());
                }
                if (empJobHisDto.getIdJobPersSupv() != null) {
                    empJobHistory.setIdJobPersSupv(empJobHisDto.getIdJobPersSupv());
                }
                if (empJobHisDto.getIndJobAssignable() != null) {
                    empJobHistory.setIndJobAssignable(empJobHisDto.getIndJobAssignable());
                }
                if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                    employeeDao.saveEmpJobHistory(empJobHistory);
                } else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    employeeDao.updateEmpJobHistory(empJobHistory);
                } else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    employeeDao.deleteEmpJobHistory(empJobHistory);
                }
                jobHisDto = this.getEmpJobHisDto(empJobHistory);
            }
        }
        return jobHisDto;
    }

    /**
     * Method Description:SaveEmployeeProcedure
     *
     * @param editEmployeeReq
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes saveEmployeeProcedure(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        List<String> szCdPersonRaceList = new ArrayList<String>();
        List<String> szCdPersonEthnicityList = new ArrayList<String>();
        PersonRaceStringDto personRaceStringDto = new PersonRaceStringDto();
        PersonEthnicityStringDto personEthnicityStringDto = new PersonEthnicityStringDto();
        List<PersonRaceDto> personRaces = new ArrayList<PersonRaceDto>();
        List<PersonEthnicityDto> personEthnicitys = new ArrayList<PersonEthnicityDto>();
        EmpPersonDto personDto = null;
        EmpPersonDto newPersonDto = new EmpPersonDto();
        List<EmpJobHisDto> empJobHisDtoList = null;
        List<EmpJobHisDto> newEmpJobHisDtoList = new ArrayList<EmpJobHisDto>();
        EmployeeDetailDto detailDto = null;
        EmpPersonIdDto empPersonIdDto = null;
        EmpUnitDto empUnitDto = null;
        List<EmpSkillDto> empSkillDtoList = null;
        // List<EmpSkillDto> newSkillDtoList = new ArrayList<EmpSkillDto>();
        List<PersonRaceDto> personRaceDtoList = null;
        List<PersonEthnicityDto> personEthnicityDtoList = null;
        EmpNameDto empNameDto = null;
        // CCMN71D
        if (editEmployeeReq.getEmpPersonDto() != null) {
            personDto = editEmployeeReq.getEmpPersonDto();
            newPersonDto = this.saveUpdateEmpPersonDto(personDto, ServiceConstants.REQ_FUNC_CD_ADD);
            editEmployeeReq.setUlIdPerson(newPersonDto.getIdPerson());
            if (editEmployeeReq.getEmpJobHisDtoList() != null) {
                empJobHisDtoList = editEmployeeReq.getEmpJobHisDtoList();
                for (EmpJobHisDto ejh : empJobHisDtoList) {
                    // CCMN78D
                    ejh.setIdPerson(editEmployeeReq.getUlIdPerson());
                    EmpJobHisDto newEmpJobHisDto = this.saveUpdateEmpJobHisDto(ejh, ejh.getEmpJobHisAction());
                    if (!ejh.getEmpJobHisAction().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
                        if (editEmployeeReq.getEmployeeDetailDto() != null) {
                            if (editEmployeeReq.getEmployeeDetailDto().getIdEmpJobHistory() == null) {
                                if (ejh.getEmpJobHisAction().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                                    editEmployeeReq.getEmployeeDetailDto()
                                            .setIdEmpJobHistory(newEmpJobHisDto.getIdEmpJobHistory());
                                }
                            } else {
                                if (editEmployeeReq.getEmployeeDetailDto().getIdEmpJobHistory()
                                        .equals(ServiceConstants.ZERO_VAL)) {
                                    if (ejh.getEmpJobHisAction().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                                        editEmployeeReq.getEmployeeDetailDto()
                                                .setIdEmpJobHistory(newEmpJobHisDto.getIdEmpJobHistory());
                                    }
                                }
                            }
                        }
                    }
                    newEmpJobHisDtoList.add(newEmpJobHisDto);
                }
            }
            // CCMN70D
            if (editEmployeeReq.getEmployeeDetailDto() != null) {
                detailDto = editEmployeeReq.getEmployeeDetailDto();
                if (newEmpJobHisDtoList.size() > 0) {
                    detailDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                    //ALM ID : 11036 : pass employee name fields in EmpNameDto
                    this.saveUpdateEmployeeDetailDto(detailDto, editEmployeeReq.getEmpNameDto(),
                            ServiceConstants.REQ_FUNC_CD_ADD);
                }
            }
            // CCMN73D
            if (editEmployeeReq.getEmpPersonIdDto() != null) {
                empPersonIdDto = editEmployeeReq.getEmpPersonIdDto();
                empPersonIdDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                this.saveEmpPersonIdDto(empPersonIdDto, ServiceConstants.REQ_FUNC_CD_ADD);
            }
            // CCMN49D
            if (editEmployeeReq.getEmpUnitDto() != null) {
                empUnitDto = editEmployeeReq.getEmpUnitDto();
                empUnitDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                if (empUnitDto.getIdUnitEmpLink() != null) {
                    if (empUnitDto.getIdUnitEmpLink().equals(ServiceConstants.ZERO_VAL)) {
                        this.editEmpUnitDto(empUnitDto, ServiceConstants.REQ_FUNC_CD_ADD);
                    } else {
                        this.editEmpUnitDto(empUnitDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
                    }
                } else {
                    this.editEmpUnitDto(empUnitDto, ServiceConstants.REQ_FUNC_CD_ADD);
                }
            }
            // CCMN98D
            if (editEmployeeReq.getEmpSkillDtoList() != null) {
                empSkillDtoList = editEmployeeReq.getEmpSkillDtoList();
                for (EmpSkillDto esd : empSkillDtoList) {
                    esd.setIdPerson(editEmployeeReq.getUlIdPerson());
                    // newSkillDtoList.add(this.saveDeleteEmpSkillDto(esd,
                    // esd.getEmpSkillAction()));
                }
            }
            // CAUDD5D, CAUDD4D
            if (editEmployeeReq.getPersonRaceDtoList() != null) {
                personRaceDtoList = editEmployeeReq.getPersonRaceDtoList();
                for (PersonRaceDto pr : personRaceDtoList) {
                    pr.setIdPerson(editEmployeeReq.getUlIdPerson());
                    this.editPersonRaceEthnicityDto(pr, null, pr.getPersonRaceAction());
                }
            }
            if (editEmployeeReq.getPersonEthnicityDtoList() != null) {
                personEthnicityDtoList = editEmployeeReq.getPersonEthnicityDtoList();
                for (PersonEthnicityDto pe : personEthnicityDtoList) {
                    pe.setIdPerson(editEmployeeReq.getUlIdPerson());
                    this.editPersonRaceEthnicityDto(null, pe, pe.getPersonEthnicityAction());
                }
            }
            // CCMNA0D
            if (editEmployeeReq.getEmpNameDto() != null) {
                empNameDto = editEmployeeReq.getEmpNameDto();
                empNameDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                if (!TypeConvUtil.isNullOrEmpty(empNameDto.getEmpNameAction())) {
                    if (!empNameDto.getEmpNameAction().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
                        this.saveUpdateEmpNameDto(empNameDto, empNameDto.getEmpNameAction());
                    }
                }
            }
            // CCMNC2D
            if (editEmployeeReq.getEmployeeDetailDto() != null) {
                this.editPersonCategory(editEmployeeReq.getEmployeeDetailDto(), ServiceConstants.REQ_FUNC_CD_ADD);
            }
            editEmployeeRes.setUlIdPerson(editEmployeeReq.getUlIdPerson());
            personRaces = personRaceDao.getPersonRaceByPersonId(editEmployeeReq.getUlIdPerson());
            personEthnicitys = personEthnicityDao.getPersonEthnicityByPersonId(editEmployeeReq.getUlIdPerson());
            if (personRaces.size() > 0) {
                for (PersonRaceDto personRace : personRaces) {
                    szCdPersonRaceList.add(personRace.getCdPersonRace());
                }
                personRaceStringDto.setCdPersonRace(szCdPersonRaceList);
            }
            if (personEthnicitys.size() > 0) {
                for (PersonEthnicityDto personEthnicity : personEthnicitys) {
                    szCdPersonEthnicityList.add(personEthnicity.getCdPersonEthnicity());
                }
                personEthnicityStringDto.setCdPersonEthnicity(szCdPersonEthnicityList);
            }
            editEmployeeRes.setPersonRaceStringDto(personRaceStringDto);
            editEmployeeRes.setPersonEthnicityStringDto(personEthnicityStringDto);
        }
        log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        return editEmployeeRes;
    }

    /**
     * Method Description:SaveUpdateEmployeeDetailDto
     *
     * @param employeeDetailDto
     * @param action
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmployeeDetailDto saveUpdateEmployeeDetailDto(EmployeeDetailDto employeeDetailDto, EmpNameDto empNameDto,
                                                         String action) {
        EmployeeDetailDto detailDto = new EmployeeDetailDto();
        Employee employee = new Employee();
        Date date = new Date();
        if (employeeDetailDto != null) {
            if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE) && null != (employeeDetailDto.getIdPerson())) {
                employee = employeeDao.getEmployeeEntityById(employeeDetailDto.getIdPerson());
                if (!TypeConvUtil.isNullOrEmpty(employeeDetailDto.getTsLastUpdate())) {
                    SimpleDateFormat formatter = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_yyyyMMddHHss);
                    String dateStr = formatter.format(employee.getDtLastUpdate());
                    Date lastUpdate = null;
                    try {
                        lastUpdate = formatter.parse(dateStr);
                    } catch (ParseException e) {
                        DataMismatchException dataMismatchException = new DataMismatchException(
                                "Employee updated failed - Dt Last Updated is Failed ParseException" + e.getMessage());
                        ;
                        dataMismatchException.initCause(e);
                        throw dataMismatchException;
                    }
                    if (!lastUpdate.equals(employeeDetailDto.getTsLastUpdate())) {
                        throw new DataMismatchException(
                                "Employee updated failed - " + "Dt Last updated is not equal to the value in table");
                    }
                } else {
                    throw new DataMismatchException("Employee updated failed -" + "Dt Last Updated is null");
                }
                //ALM ID : 11036 : Insert trigger on Name table happens before the employee update
                // so the values are getting overwritten, copied the trigger code to update the employee table with
					// names
                if (empNameDto != null) {
                    if (StringUtils.isNotEmpty(empNameDto.getNmNameFirst())) {
                        employee.setNmEmployeeFirst(empNameDto.getNmNameFirst());
                    }
                    if (StringUtils.isNotEmpty(empNameDto.getNmNameLast())) {
                        employee.setNmEmployeeLast(empNameDto.getNmNameLast());
                    }
                    if (StringUtils.isNotEmpty(empNameDto.getNmNameMiddle())) {
                        employee.setNmEmployeeMiddle(empNameDto.getNmNameMiddle());
                    }
                    if (StringUtils.isNotEmpty(empNameDto.getCdNameSuffix())) {
                        employee.setCdEmployeeSuffix(empNameDto.getCdNameSuffix());
                    }
                }
            }
            employee.setDtLastUpdate(date);
            if (employeeDetailDto.getIdPerson() != null) {
                employee.setIdPerson(employeeDetailDto.getIdPerson());
            }
            if (employeeDetailDto.getIdEmpJobHistory() != null) {
                employee.setIdEmpJobHistory(employeeDetailDto.getIdEmpJobHistory());
            }
            employee.setCdEmpProgram(employeeDetailDto.getCdEmpProgram());
            employee.setCdEmployeeClass(employeeDetailDto.getCdEmployeeClass());
            employee.setDtEmpHire(employeeDetailDto.getDtEmpHire());
            employee.setDtEmpLastAssigned(employeeDetailDto.getDtEmpLastAssigned());
            employee.setDtEmpTermination(employeeDetailDto.getDtEmpTermination());
            if (employeeDetailDto.getIdOffice() != null) {
                employee.setOffice(officeDao.getOfficeEntityById(employeeDetailDto.getIdOffice()));
            }
            employee.setIdEmployeeLogon(employeeDetailDto.getIdEmployeeLogon());
            employee.setIndEmpActiveStatus(employeeDetailDto.getIndActiveStatus());
            employee.setIndEmpConfirmedHrmis(employeeDetailDto.getIndEmpConfirmedHrmis());
            employee.setIndEmpPendingHrmis(employeeDetailDto.getIndEmpPendingHrmis());
            if (Short.valueOf(employeeDetailDto.getNbrEmpActivePct()) != null) {
                employee.setNbrEmpActivePct(employeeDetailDto.getNbrEmpActivePct());
            }
            employee.setTxtEmailAddress(employeeDetailDto.getEmailAddress());
            employee.setTxtEmployeeEmailAddress(employeeDetailDto.getEmployeeEmailAddress());
            employee.setCdExternalType(employeeDetailDto.getCdExternalUserType());
            employee.setIndExtAccess(employeeDetailDto.getIndExtAccess());
            employee.setTxtBirthCity(employeeDetailDto.getBirthCity());
            employee.setCdBirthState(employeeDetailDto.getCdBirthState());
            employee.setIdReqsitn(employeeDetailDto.getIdReqsitn());
            employee.setNbrHhscPurchsOrder(employeeDetailDto.getHhscPurchsOrder());
            employee.setIdDept(employeeDetailDto.getIdDept());
            employee.setTxtRoleJobDuty(employeeDetailDto.getRoleJobDuty());
            if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                employeeDao.saveEmployee(employee);
            } else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                employeeDao.updateEmployee(employee);
            }
            if(!ObjectUtils.isEmpty(employeeDetailDto.getDtEmpTermination())){
                ssccEmployeeDetailDao.updateSSCCEmployeeEndDate(employeeDetailDto.getIdPerson(),employeeDetailDto.getDtEmpTermination(),employeeDetailDto.getLastUpdatedPersonId());
            }
            detailDto = this.getEmployeeDetailDto(employee);
        }
        return detailDto;
    }

    /**
     * Method Name :saveUpdateExtEmployeeDetailDto Method Description:to save
     * external employee
     *
     * @param empNameDto
     * @param EmployeeDetailDto
     * @param action
     * @return EmployeeDetailDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmployeeDetailDto saveUpdateExtEmployeeDetailDto(EmpNameDto empNameDto, EmployeeDetailDto employeeDetailDto,
                                                            String action) {
        EmployeeDetailDto detailDto = new EmployeeDetailDto();
        ExtrnlUser ExtrnlUserloyee = new ExtrnlUser();
        Date date = new Date();
        if (employeeDetailDto != null) {
            if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE) && null != (employeeDetailDto.getIdPerson())) {
                ExtrnlUserloyee = employeeDao.getExternalUserEntityById(employeeDetailDto.getIdPerson());
                if (!ObjectUtils.isEmpty(ExtrnlUserloyee)) {
                    if (!TypeConvUtil.isNullOrEmpty(employeeDetailDto.getTsLastUpdate())) {
                        SimpleDateFormat formatter = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_yyyyMMddHHss);
                        String dateStr = formatter.format(ExtrnlUserloyee.getDtLastUpdate());
                        Date lastUpdate = null;
                        try {
                            lastUpdate = formatter.parse(dateStr);
                        } catch (ParseException e) {
                            DataMismatchException dataMismatchException = new DataMismatchException(
                                    "Employee updated failed - Dt Last Updated is Failed ParseException" +
                                            e.getMessage());
                            ;
                            dataMismatchException.initCause(e);
                            throw dataMismatchException;
                        }
                        if (!lastUpdate.equals(employeeDetailDto.getTsLastUpdate())) {
                            throw new DataMismatchException("Employee updated failed - " +
                                    "Dt Last updated is not equal to the value in table");
                        }
                    } else {
                        throw new DataMismatchException("Employee updated failed -" + "Dt Last Updated is null");
                    }
                }

            }
            if (!ObjectUtils.isEmpty(ExtrnlUserloyee)) {
                ExtrnlUserloyee.setDtLastUpdate(date);
                if (employeeDetailDto.getIdPerson() != null) {
                    ExtrnlUserloyee.setIdPerson(employeeDetailDto.getIdPerson());
                }
                ExtrnlUserloyee.setDtExtrnlUserStart(employeeDetailDto.getDtEmpHire());
                ExtrnlUserloyee.setNmExtrnlUserFirst(empNameDto.getNmNameFirst());
				if (!TypeConvUtil.isNullOrEmpty(empNameDto.getNmNameMiddle())) {
					ExtrnlUserloyee.setNmExtrnlUserMiddle(empNameDto.getNmNameMiddle());
				}
                ExtrnlUserloyee.setNmExtrnlUserLast(empNameDto.getNmNameLast());
                if (!TypeConvUtil.isNullOrEmpty(employeeDetailDto.getDtEmpHire())) {
                    ExtrnlUserloyee.setDtExtrnlUserStart(employeeDetailDto.getDtEmpHire());
                }
                if (!TypeConvUtil.isNullOrEmpty(employeeDetailDto.getDtEmpTermination())) {
                    ExtrnlUserloyee.setDtExtrnlUserEnd(employeeDetailDto.getDtEmpTermination());
                } else {
                    ExtrnlUserloyee.setDtExtrnlUserEnd(null);
                }
                employeeDao.updateExternalEmployee(ExtrnlUserloyee);
                detailDto = this.getExternalEmployeeDetailDto(ExtrnlUserloyee);
            }
        }
        return detailDto;
    }

    /**
     * Method Description:SaveEmpPersonIdDto
     *
     * @param empPersonIdDto
     * @param action
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpPersonIdDto saveEmpPersonIdDto(EmpPersonIdDto empPersonIdDto, String action) {
        EmpPersonIdDto personIdDto = new EmpPersonIdDto();
        PersonId personId = new PersonId();
        Date date = new Date();
        if (empPersonIdDto != null) {
            personId.setDtLastUpdate(date);
            if (empPersonIdDto.getCdPersonIdType() != null) {
                if (!empPersonIdDto.getCdPersonIdType().equals(ServiceConstants.EMPTY_STRING)) {
                    personId.setCdPersonIdType(empPersonIdDto.getCdPersonIdType());
                }
            }
            if (empPersonIdDto.getIdPerson() != null) {
                personId.setPerson(personDao.getPersonByPersonId(empPersonIdDto.getIdPerson()));
            }
            if (empPersonIdDto.getPersonIdNumber() != null) {
                if (!empPersonIdDto.getPersonIdNumber().equals(ServiceConstants.EMPTY_STRING)) {
                    personId.setNbrPersonIdNumber(empPersonIdDto.getPersonIdNumber());
                }
            }
            if (empPersonIdDto.getIdDescPerson() != null) {
                if (!empPersonIdDto.getIdDescPerson().equals(ServiceConstants.EMPTY_STRING)) {
                    personId.setDescPersonId(empPersonIdDto.getIdDescPerson());
                }
            }
            if (empPersonIdDto.getIndPersonIDInvalid() != null) {
                if (!empPersonIdDto.getIndPersonIDInvalid().equals(ServiceConstants.EMPTY_STRING)) {
                    personId.setIndPersonIdInvalid(empPersonIdDto.getIndPersonIDInvalid());
                }
            }
            personId.setDtPersonIdEnd(null);
            personId.setDtPersonIdStart(date);
            if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                personIdDao.savePersonId(personId);
            }
            personIdDto = personIdService.getEmpPersonIdDto(personId);
        }
        return personIdDto;
    }

    /**
     * Method Description:editEmpUnitDto
     *
     * @param empUnitDto
     * @param action
     * @return EmpUnitDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpUnitDto editEmpUnitDto(EmpUnitDto empUnitDto, String action) {
        EmpUnitDto unitDto = new EmpUnitDto();
        UnitEmpLink uel = new UnitEmpLink();
        Date date = new Date();
        if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE) && null != empUnitDto.getIdUnitEmpLink() &&
                empUnitDto.getIdUnitEmpLink().compareTo(ServiceConstants.ZERO_VAL) != 0) {
            uel = unitDao.searchUnitEmpLinkById(empUnitDto.getIdUnitEmpLink());
        }
        if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE) && empUnitDto.getIdUnitEmpLink() != null &&
                empUnitDto.getIdUnitEmpLink().compareTo(ServiceConstants.ZERO_VAL) != 0) {
            uel = unitDao.searchUnitEmpLinkById(empUnitDto.getIdUnitEmpLink());
        }
        uel.setDtLastUpdate(date);
        if ((!ObjectUtils.isEmpty(empUnitDto)) && null != (empUnitDto.getIdPerson())) {
            uel.setIdPerson(empUnitDto.getIdPerson());
        }
        if ((!ObjectUtils.isEmpty(empUnitDto)) && null != (empUnitDto.getIdUnit())) {
            uel.setIdUnit(empUnitDto.getIdUnit());
        }
        if (empUnitDto.getUnitMemberRole() != null &&
                !empUnitDto.getUnitMemberRole().equals(ServiceConstants.EMPTY_STRING)) {
            uel.setCdUnitMemberRole(empUnitDto.getUnitMemberRole());
        }
        if (empUnitDto.getUnitMemberInOut() != null) {
            if (!empUnitDto.getUnitMemberInOut().equals(ServiceConstants.EMPTY_STRING)) {
                uel.setCdUnitMemberInOut(empUnitDto.getUnitMemberInOut());
            }
        }
        if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
            unitDao.saveUnitEmpLink(uel);
        } else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
            unitDao.updateUnitEmpLink(uel);
        } else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
            unitDao.deleteUnitEmpLink(uel);
        }
        unitDto = this.getEmpUnitDto(uel);
        return unitDto;
    }

    /**
     * Method Description:getEmpUnitDto
     *
     * @param uel
     * @return
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpUnitDto getEmpUnitDto(UnitEmpLink uel) {
        EmpUnitDto unitDto = new EmpUnitDto();
        if (uel != null) {
            if ((!ObjectUtils.isEmpty(uel)) && null != (uel.getIdUnitEmpLink())) {
                unitDto.setIdUnitEmpLink(uel.getIdUnitEmpLink());
            }
            if ((!ObjectUtils.isEmpty(uel)) && null != (uel.getIdPerson())) {
                unitDto.setIdPerson(uel.getIdPerson());
            }
            if ((!ObjectUtils.isEmpty(uel)) && null != (uel.getIdUnit())) {
                unitDto.setIdUnit(uel.getIdUnit());
            }
            if (uel.getCdUnitMemberRole() != null) {
                unitDto.setUnitMemberRole(uel.getCdUnitMemberRole());
            }
            if (uel.getCdUnitMemberInOut() != null) {
                unitDto.setUnitMemberInOut(uel.getCdUnitMemberInOut());
            }
        }
        return unitDto;
    }

    /**
     * Method Description:SaveDeleteEmpSkillDto
     *
     * @param empSkillDto
     * @param action
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpSkillDto saveDeleteEmpSkillDto(EmpSkillDto empSkillDto, String action) {
        EmpSkillDto skillDto = new EmpSkillDto();
        EmployeeSkill es = new EmployeeSkill();
        Date date = new Date();
        if (!action.equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
            if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                if (empSkillDto.getIdPerson() != null && empSkillDto.getCdEmpSkill() != null) {
                    if (!empSkillDto.getCdEmpSkill().equals(ServiceConstants.EMPTY_STRING)) {
                        es = employeeDao.getEmployeeSkillByIdSkill(empSkillDto.getIdPerson(),
                                empSkillDto.getCdEmpSkill());
                    }
                }
            }
            es.setDtLastUpdate(date);
            if (empSkillDto.getCdEmpSkill() != null) {
                if (!empSkillDto.getCdEmpSkill().equals(ServiceConstants.EMPTY_STRING)) {
                    es.setCdEmpSkill(empSkillDto.getCdEmpSkill());
                }
            }
            if ((!ObjectUtils.isEmpty(empSkillDto)) && null != (empSkillDto.getIdPerson())) {
                es.setIdPerson(empSkillDto.getIdPerson());
            }
            if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                employeeDao.saveEmployeeSkill(es);
            } else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                employeeDao.deleteEmployeeSkill(es);
            }
            skillDto = this.getEmpSkillDto(es);
        }
        return skillDto;
    }

    /**
     * Method Description:editPersonRaceEthnicityDto
     *
     * @param personRaceDto
     * @param personEthnicityDto
     * @param action
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void editPersonRaceEthnicityDto(PersonRaceDto personRaceDto, PersonEthnicityDto personEthnicityDto,
                                           String action) {
        PersonRace pr = new PersonRace();
        PersonEthnicity pe = new PersonEthnicity();
        Date date = new Date();
        Person person = new Person();
        if (!action.equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
            if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                if ((!ObjectUtils.isEmpty(personEthnicityDto)) &&
                        !ObjectUtils.isEmpty(personEthnicityDto.getIdPersonEthnicity())) {
                    pe = personEthnicityDao.getPersonEthnicityByPersonEthnicityId(
                            personEthnicityDto.getIdPersonEthnicity());
                }
                if ((!ObjectUtils.isEmpty(personRaceDto)) && !ObjectUtils.isEmpty(personRaceDto.getIdPersonRace())) {
                    pr = personRaceDao.getPersonRaceByPersonRaceId(personRaceDto.getIdPersonRace());
                }
            }
            if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                if (personEthnicityDto != null) {
                    if (personEthnicityDto.getIdPerson() != null && personEthnicityDto.getCdPersonEthnicity() != null) {
                        if (!personEthnicityDto.getCdPersonEthnicity().equals(ServiceConstants.EMPTY_STRING)) {
                            pe = personEthnicityDao.getPersonEthnicityByPersonIdAndEthnicity(
                                    personEthnicityDto.getIdPerson(), personEthnicityDto.getCdPersonEthnicity());
                        }
                    }
                }
                if (personRaceDto != null) {
                    if (personRaceDto.getIdPersonRace() != null && personRaceDto.getCdPersonRace() != null) {
                        if (!personRaceDto.getCdPersonRace().equals(ServiceConstants.EMPTY_STRING)) {
                            pr = personRaceDao.getPersonRaceByPersonIdAndRace(personRaceDto.getIdPersonRace(),
                                    personRaceDto.getCdPersonRace());
                        }
                    }
                }
            }
            pr.setDtLastUpdate(date);
            pe.setDtLastUpdate(date);
            if (personEthnicityDto != null) {
                if (null != (personEthnicityDto.getIdPerson())) {
                    person = personDao.getPersonByPersonId(personEthnicityDto.getIdPerson());
                    pe.setPerson(person);
                }
                if (personEthnicityDto.getCdPersonEthnicity() != null) {
                    if (!personEthnicityDto.getCdPersonEthnicity().equals(ServiceConstants.EMPTY_STRING)) {
                        pe.setCdEthnicity(personEthnicityDto.getCdPersonEthnicity());
                    }
                }
            }
            if (personRaceDto != null) {
                if (personRaceDto.getCdPersonRace() != null) {
                    if (!personRaceDto.getCdPersonRace().equals(ServiceConstants.EMPTY_STRING)) {
                        pr.setCdRace(personRaceDto.getCdPersonRace());
                    }
                }
                if ((!ObjectUtils.isEmpty(personRaceDto)) && null != (personRaceDto.getIdPerson())) {
                    person = personDao.getPersonByPersonId(personRaceDto.getIdPerson());
                    pr.setPerson(person);
                }
            }
            if (personEthnicityDto != null) {
                if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                    personEthnicityDao.savePersonEthnicity(pe);
                } else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    personEthnicityDao.deletePersonEthnicity(pe);
                } else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    personEthnicityDao.updatePersonEthnicity(pe);
                }
            }
            if (personRaceDto != null) {
                if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                    personRaceDao.savePersonRace(pr);
                } else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    personRaceDao.deletePersonRace(pr);
                } else if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    personRaceDao.updatePersonRace(pr);
                }
            }
        }
    }

    /**
     * Method Description:SaveUpdateEmpNameDto
     *
     * @param empNameDto
     * @param action
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmpNameDto saveUpdateEmpNameDto(EmpNameDto empNameDto, String action) {
        EmpNameDto nameDto = new EmpNameDto();
        Name name = new Name();
        Date date = new Date();
        Person person = new Person();
        if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
            if (empNameDto.getIdName() != null) {
                name = nameDao.getNameById(empNameDto.getIdName());
                name.setDtNameEndDate(date);
                name.setDtLastUpdate(date);
                nameDao.updateName(name);
                empNameDto.setIdName(null);
                name = new Name();
                name.setDtNameStartDate(date);
                name.setDtNameEndDate(null);
                name.setDtLastUpdate(date);
                if (empNameDto.getIdPerson() != null) {
                    person = personDao.getPersonByPersonId(empNameDto.getIdPerson());
                    name.setPerson(person);
                }
                if (empNameDto.getCdNameSuffix() != null) {
                    if (!empNameDto.getCdNameSuffix().equals(ServiceConstants.EMPTY_STRING)) {
                        name.setCdNameSuffix(empNameDto.getCdNameSuffix());
                    }
                }
                if (empNameDto.getIndNameInvalid() != null) {
                    if (!empNameDto.getIndNameInvalid().equals(ServiceConstants.EMPTY_STRING)) {
                        name.setIndNameInvalid(empNameDto.getIndNameInvalid());
                    }
                }
                if (empNameDto.getIndNamePrimary() != null) {
                    if (!empNameDto.getIndNamePrimary().equals(ServiceConstants.EMPTY_STRING)) {
                        name.setIndNamePrimary(empNameDto.getIndNamePrimary());
                    }
                }
                if (empNameDto.getNmNameFirst() != null) {
                    if (!empNameDto.getNmNameFirst().equals(ServiceConstants.EMPTY_STRING)) {
                        name.setNmNameFirst(empNameDto.getNmNameFirst());
                    }
                }
                if (empNameDto.getNmNameLast() != null) {
                    if (!empNameDto.getNmNameLast().equals(ServiceConstants.EMPTY_STRING)) {
                        name.setNmNameLast(empNameDto.getNmNameLast());
                    }
                }
                if (empNameDto.getNmNameMiddle() != null) {
                    if (!empNameDto.getNmNameMiddle().equals(ServiceConstants.EMPTY_STRING)) {
                        name.setNmNameMiddle(empNameDto.getNmNameMiddle());
                    }
                }
                nameDao.saveName(name);
            }
        } else if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
            name.setDtNameStartDate(date);
            name.setDtNameEndDate(null);
            if (empNameDto.getIdPerson() != null) {
                person = personDao.getPersonByPersonId(empNameDto.getIdPerson());
                name.setPerson(person);
            }
            if (empNameDto.getCdNameSuffix() != null) {
                if (!empNameDto.getCdNameSuffix().equals(ServiceConstants.EMPTY_STRING)) {
                    name.setCdNameSuffix(empNameDto.getCdNameSuffix());
                }
            }
            if (empNameDto.getIndNameInvalid() != null) {
                if (!empNameDto.getIndNameInvalid().equals(ServiceConstants.EMPTY_STRING)) {
                    name.setIndNameInvalid(empNameDto.getIndNameInvalid());
                }
            }
            if (empNameDto.getIndNamePrimary() != null) {
                if (!empNameDto.getIndNamePrimary().equals(ServiceConstants.EMPTY_STRING)) {
                    name.setIndNamePrimary(empNameDto.getIndNamePrimary());
                }
            }
            if (empNameDto.getNmNameFirst() != null) {
                if (!empNameDto.getNmNameFirst().equals(ServiceConstants.EMPTY_STRING)) {
                    name.setNmNameFirst(empNameDto.getNmNameFirst());
                }
            }
            if (empNameDto.getNmNameLast() != null) {
                if (!empNameDto.getNmNameLast().equals(ServiceConstants.EMPTY_STRING)) {
                    name.setNmNameLast(empNameDto.getNmNameLast());
                }
            }
            if (empNameDto.getNmNameMiddle() != null) {
                if (!empNameDto.getNmNameMiddle().equals(ServiceConstants.EMPTY_STRING)) {
                    name.setNmNameMiddle(empNameDto.getNmNameMiddle());
                }
            }
        }
        name.setDtLastUpdate(date);
        if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
            nameDao.saveName(name);
        }
        nameDto = nameService.getEmpNameDto(name);
        return nameDto;
    }

    /**
     * Method Description:updateEmployee
     *
     * @param editEmployeeReq
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes updateEmployee(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        EmployeeDetailDto employeeDetailDto = new EmployeeDetailDto();
        EmpUnitDto empUnitDto = new EmpUnitDto();
        List<Long> units = new ArrayList<Long>();
        List<Long> onCalls = new ArrayList<Long>();
        List<Long> todos = new ArrayList<Long>();
        List<Long> prStagePersonLinks = new ArrayList<Long>();
        List<Long> seStagePersonLinks = new ArrayList<Long>();
        boolean dateTermCheck = false;
        boolean unitEmpCheck = false;
        if (editEmployeeReq != null) {
            if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getReqFuncCd())) {
                if (editEmployeeReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getUlIdPerson())) {
                        if (editEmployeeReq.getEmployeeDetailDto() != null) {
                            employeeDetailDto = editEmployeeReq.getEmployeeDetailDto();
                            if (employeeDetailDto.getDtEmpTermination() != null &&
                                    !employeeDetailDto.isExternalEmployee()) {
                                // CCMNG6D
                                units = unitDao.searchUnitByPersonId(editEmployeeReq.getUlIdPerson());
                                if (units.size() == 0) {
                                    // CCMNE6D
                                    onCalls = onCallDao.getCurrentOnCallByPersonId(editEmployeeReq.getUlIdPerson());
                                    if (onCalls.size() == 0) {
                                        // CCMNE7D
                                        todos = toDoDao.getTodoBypersonId(editEmployeeReq.getUlIdPerson());
                                        if (todos.size() == 0) {
                                            // CCMNE8D
                                            prStagePersonLinks = stageDao.searchStageIdsFromLinkByPersonIdAndStageRole(
                                                    editEmployeeReq.getUlIdPerson(),
                                                    ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
                                            if (prStagePersonLinks.size() == 0) {
                                                // CCMNE8D
                                                seStagePersonLinks =
                                                        stageDao.searchStageIdsFromLinkByPersonIdAndStageRole(
                                                                editEmployeeReq.getUlIdPerson(),
                                                                ServiceConstants.SEC_ROLE_STAGE_OPEN);
                                                if (seStagePersonLinks.size() == 0) {
                                                    dateTermCheck = true;
                                                } else {
                                                    ErrorDto errorDto = new ErrorDto();
                                                    errorDto.setErrorCode(ServiceConstants.MSG_CMN_STAGES_OUTSTANDING);
                                                    editEmployeeRes.setErrorDto(errorDto);
                                                    return editEmployeeRes;
                                                }
                                            }
                                        } else {
                                            ErrorDto errorDto = new ErrorDto();
                                            errorDto.setErrorCode(ServiceConstants.MSG_CMN_TODO_OUTSTANDING);
                                            editEmployeeRes.setErrorDto(errorDto);
                                            return editEmployeeRes;
                                        }
                                    } else {
                                        ErrorDto errorDto = new ErrorDto();
                                        errorDto.setErrorCode(ServiceConstants.MSG_CMN_ONCALL_OUTSTANDING);
                                        editEmployeeRes.setErrorDto(errorDto);
                                        return editEmployeeRes;
                                    }
                                } else {
                                    ErrorDto errorDto = new ErrorDto();
                                    errorDto.setErrorCode(ServiceConstants.MSG_CMN_STF_APPRVR_MOD);
                                    editEmployeeRes.setErrorDto(errorDto);
                                    return editEmployeeRes;
                                }
                            } else if (employeeDetailDto.getDtEmpTermination() != null &&
                                    employeeDetailDto.isExternalEmployee()) {
                                /*
                                 * for person isexternal user and has closure
                                 * date then 1) unlink person from organization
                                 * 2) unlink person from roles 3) unlink person
                                 * from resources 4) remove the security profile
                                 * RCCP_CM 5) update active status field in
                                 * extrnl employee table to N 6) update employee
                                 * end date in extrnl employee tablw with the
                                 * user enterred value
                                 */
                                ExtUserOrgResourceLinkDelReq extUserOrgResourceLinkDelReq =
                                        new ExtUserOrgResourceLinkDelReq();
                                extUserOrgResourceLinkDelReq.setIdPerson(editEmployeeReq.getUlIdPerson());
                                extUserOrgMappingService.deleteExtUserOrgResourceLink(extUserOrgResourceLinkDelReq);
                                dateTermCheck = true;
                            } else {
                                dateTermCheck = true;
                            }
                        } else {
                            dateTermCheck = true;
                        }
                        if (editEmployeeReq.getEmpUnitDto() != null) {
                            empUnitDto = editEmployeeReq.getEmpUnitDto();
                            if (empUnitDto.getIdUnitEmpLink() != null) {
                                if (empUnitDto.getIdUnitEmpLink().equals(ServiceConstants.ZERO_VAL)) {
                                    // CCMNG5D
                                    units = unitDao.searchUnitByUnitApprover(editEmployeeReq.getUlIdPerson());
                                    if (units.size() == 0) {
                                        unitEmpCheck = true;
                                    } else {
                                        ErrorDto errorDto = new ErrorDto();
                                        errorDto.setErrorCode(ServiceConstants.MSG_CMN_STF_APPRVR_MOD);
                                        editEmployeeRes.setErrorDto(errorDto);
                                        return editEmployeeRes;
                                    }
                                } else {
                                    unitEmpCheck = true;
                                }
                            } else {
                                unitEmpCheck = true;
                            }
                        } else {
                            unitEmpCheck = true;
                        }
                        if (unitEmpCheck && dateTermCheck) {
                            editEmployeeRes = this.updateEmployeeProcedure(editEmployeeReq);
                            editEmployeeRes.setActionResult("Update Successfully!");
                        }
                    }
                }
            }
        }
		if ((!ObjectUtils.isEmpty(editEmployeeReq))) {log.info("TransactionId :" + editEmployeeReq.getTransactionId());}
        return editEmployeeRes;
    }

    /**
     * Method Description:UpdateEmployeeProcedure
     *
     * @param editEmployeeReq
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EditEmployeeRes updateEmployeeProcedure(EditEmployeeReq editEmployeeReq) {
        EditEmployeeRes editEmployeeRes = new EditEmployeeRes();
        List<String> szCdPersonRaceList = new ArrayList<String>();
        List<String> szCdPersonEthnicityList = new ArrayList<String>();
        PersonRaceStringDto personRaceStringDto = new PersonRaceStringDto();
        PersonEthnicityStringDto personEthnicityStringDto = new PersonEthnicityStringDto();
        List<PersonRaceDto> personRaces = new ArrayList<PersonRaceDto>();
        List<PersonEthnicityDto> personEthnicitys = new ArrayList<PersonEthnicityDto>();
        EmpPersonDto personDto = null;
        List<EmpJobHisDto> empJobHisDtoList = null;
        // List<EmpJobHisDto> newEmpJobHisDtoList = new
        // ArrayList<EmpJobHisDto>();
        List<EmpSkillDto> empSkillDtoList = null;
        // List<EmpSkillDto> newSkillDtoList = new ArrayList<EmpSkillDto>();
        EmployeeDetailDto detailDto = null;
        EmpUnitDto empUnitDto = null;
        List<PersonRaceDto> personRaceDtoList = null;
        List<PersonEthnicityDto> personEthnicityDtoList = null;
        EmpNameDto empNameDto = null;
        List<UnitEmpLinkDto> uels = new ArrayList<UnitEmpLinkDto>();
        if (editEmployeeReq != null && null != editEmployeeReq.getUlIdPerson()) {
            // CCMN71D
            if (editEmployeeReq.getEmpPersonDto() != null) {
                personDto = editEmployeeReq.getEmpPersonDto();
                personDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                this.saveUpdateEmpPersonDto(personDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
            }
            if (editEmployeeReq.getEmpJobHisDtoList() != null) {
                empJobHisDtoList = editEmployeeReq.getEmpJobHisDtoList();
                for (EmpJobHisDto ejh : empJobHisDtoList) {
                    // CCMN78D
                    ejh.setIdPerson(editEmployeeReq.getUlIdPerson());
                    EmpJobHisDto newEmpJobHisDto = this.saveUpdateEmpJobHisDto(ejh, ejh.getEmpJobHisAction());
                    if (!TypeConvUtil.isNullOrEmpty(ejh.getEmpJobHisAction())) {
                        if (!ejh.getEmpJobHisAction().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
                            if (editEmployeeReq.getEmployeeDetailDto() != null) {
                                if (editEmployeeReq.getEmployeeDetailDto().getIdEmpJobHistory() == null) {
                                    if (ejh.getEmpJobHisAction().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                                        editEmployeeReq.getEmployeeDetailDto()
                                                .setIdEmpJobHistory(newEmpJobHisDto.getIdEmpJobHistory());
                                    }
                                } else {
                                    if (editEmployeeReq.getEmployeeDetailDto().getIdEmpJobHistory()
                                            .equals(ServiceConstants.ZERO_VAL)) {
                                        if (ejh.getEmpJobHisAction().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                                            editEmployeeReq.getEmployeeDetailDto()
                                                    .setIdEmpJobHistory(newEmpJobHisDto.getIdEmpJobHistory());
                                            editEmployeeReq.getEmployeeDetailDto().setCdEmployeeClass(newEmpJobHisDto.getCdJobClass());
                                            List<CodeAttributes> codesList = codesDao.getCodesTable("CEMPJBCL", newEmpJobHisDto.getCdJobClass());
                                            if(!ObjectUtils.isEmpty(codesList)) {
                                                String employeeClass =   codesList.get(0).getDecode();
                                                editEmployeeReq.getEmployeeDetailDto().setEmployeeClass(employeeClass);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // newEmpJobHisDtoList.add(newEmpJobHisDto);
                }
            }
            // CCMN70D
            if (editEmployeeReq.getEmployeeDetailDto() != null) {
                detailDto = editEmployeeReq.getEmployeeDetailDto();
                detailDto.setIdPerson(editEmployeeReq.getUlIdPerson());

                if (detailDto.isExternalEmployee()) {
                    this.saveUpdateExtEmployeeDetailDto(editEmployeeReq.getEmpNameDto(), detailDto,
                            ServiceConstants.REQ_FUNC_CD_UPDATE);
                } else {
                    //ALM ID : 11036 : pass employee name fields in EmpNameDto
                    this.saveUpdateEmployeeDetailDto(detailDto, editEmployeeReq.getEmpNameDto(),
                            ServiceConstants.REQ_FUNC_CD_UPDATE);
                }

            }
            if (editEmployeeReq.getEmpUnitDto() != null) {
                empUnitDto = editEmployeeReq.getEmpUnitDto();
                if (empUnitDto.getIdUnitEmpLink() != null) {
                    if (empUnitDto.getIdUnitEmpLink().equals(ServiceConstants.ZERO_VAL)) {
                        // CCMNE0D
                        unitDao.deleteUnitEmpLinkByPersonIdAndInOut(editEmployeeReq.getUlIdPerson(),
                                ServiceConstants.UNIT_MEMBER_IN_ASSIGNED);
                        // CCMND5D
                        if (empUnitDto.getIdUnit() != null && !ObjectUtils.isEmpty(empUnitDto.getIdPerson())) {
                            uels = unitDao.searchUnitEmpLinkByUnitPersonId(editEmployeeReq.getUlIdPerson(),
                                    empUnitDto.getIdUnit());
                            if (uels.size() > 0) {
                                for (UnitEmpLinkDto uell : uels) {
                                    EmpUnitDto newUnitDto = new EmpUnitDto();
                                    newUnitDto.setIdUnitEmpLink(uell.getIdUnitEmpLink());
                                    this.editEmpUnitDto(newUnitDto, ServiceConstants.REQ_FUNC_CD_DELETE);
                                }
                            }
                        }
                    }
                }
                if (editEmployeeReq.getEmployeeDetailDto() != null) {
                    if (editEmployeeReq.getEmployeeDetailDto().getDtEmpTermination() == null ||
                            String.valueOf(editEmployeeReq.getEmployeeDetailDto().getDtEmpTermination())
                                    .equals(ServiceConstants.EMPTY_STRING)) {
                        // CCMN49D
                        empUnitDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                        if (empUnitDto.getIdUnitEmpLink() != null) {
                            if (empUnitDto.getIdUnitEmpLink().equals(ServiceConstants.ZERO_VAL)) {
                                this.editEmpUnitDto(empUnitDto, ServiceConstants.REQ_FUNC_CD_ADD);
                            } else {
                                this.editEmpUnitDto(empUnitDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
                            }
                        } else {
                            this.editEmpUnitDto(empUnitDto, ServiceConstants.REQ_FUNC_CD_ADD);
                        }
                    }
                }
            }
            // CCMN98D
            if (editEmployeeReq.getEmpSkillDtoList() != null) {
                empSkillDtoList = editEmployeeReq.getEmpSkillDtoList();
                for (EmpSkillDto esd : empSkillDtoList) {
                    esd.setIdPerson(editEmployeeReq.getUlIdPerson());
                    // newSkillDtoList.add(this.saveDeleteEmpSkillDto(esd,
                    // esd.getEmpSkillAction()));
                }
            }
            // CAUDD5D, CAUDD4D
            if (editEmployeeReq.getPersonRaceDtoList() != null) {
                personRaceDtoList = editEmployeeReq.getPersonRaceDtoList();
                for (PersonRaceDto pr : personRaceDtoList) {
                    pr.setIdPerson(editEmployeeReq.getUlIdPerson());
                    this.editPersonRaceEthnicityDto(pr, null, pr.getPersonRaceAction());
                }
            }
            if (editEmployeeReq.getPersonEthnicityDtoList() != null) {
                personEthnicityDtoList = editEmployeeReq.getPersonEthnicityDtoList();
                for (PersonEthnicityDto pe : personEthnicityDtoList) {
                    pe.setIdPerson(editEmployeeReq.getUlIdPerson());
                    this.editPersonRaceEthnicityDto(null, pe, pe.getPersonEthnicityAction());
                }
            }
            // CCMNA0D
            if (editEmployeeReq.getEmpNameDto() != null) {
                empNameDto = editEmployeeReq.getEmpNameDto();
                empNameDto.setIdPerson(editEmployeeReq.getUlIdPerson());
                if (!TypeConvUtil.isNullOrEmpty(empNameDto.getCdScrDataAction())) {
                    if (!empNameDto.getCdScrDataAction().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
                        this.saveUpdateEmpNameDto(empNameDto, empNameDto.getCdScrDataAction());
                    }
                }
                if (editEmployeeReq.getEmpPersonDto() != null) {
                    if (!TypeConvUtil.isNullOrEmpty(editEmployeeReq.getEmpPersonDto().getNmPersonFull()) &&
                            !TypeConvUtil.isNullOrEmpty(editEmployeeReq.getNmPersonFull())) {
                        // CCMNH4D
                        this.updateCaseName(editEmployeeReq.getEmpPersonDto().getNmPersonFull(),
                                editEmployeeReq.getUlIdPerson(), editEmployeeReq.getNmPersonFull());
                        // CCMNH5D
                        this.updateStageName(editEmployeeReq.getEmpPersonDto().getNmPersonFull(),
                                editEmployeeReq.getUlIdPerson(), editEmployeeReq.getNmPersonFull());
                        if (editEmployeeReq.getNmPersonFull().length() <=
                                ServiceConstants.NM_PERSON_FULL_LEN - ServiceConstants.CASE_NM_ET_AL_LEN - 1) {
                            String name = editEmployeeReq.getNmPersonFull();
                            editEmployeeReq.setNmPersonFull(name + ServiceConstants.CASE_NM_ET_AL);
                        } else {
                            String name = editEmployeeReq.getNmPersonFull().substring(0,
                                    ServiceConstants.NM_PERSON_FULL_LEN - ServiceConstants.CASE_NM_ET_AL_LEN - 2);
                            editEmployeeReq.setNmPersonFull(name + ServiceConstants.CASE_NM_ET_AL);
                        }
                        if (editEmployeeReq.getEmpPersonDto().getNmPersonFull().length() <=
                                ServiceConstants.NM_PERSON_FULL_LEN - ServiceConstants.CASE_NM_ET_AL_LEN - 1) {
                            String name = editEmployeeReq.getEmpPersonDto().getNmPersonFull();
                            editEmployeeReq.getEmpPersonDto().setNmPersonFull(name + ServiceConstants.CASE_NM_ET_AL);
                        } else {
                            String name = editEmployeeReq.getEmpPersonDto().getNmPersonFull().substring(0,
                                    ServiceConstants.NM_PERSON_FULL_LEN - ServiceConstants.CASE_NM_ET_AL_LEN - 2);
                            editEmployeeReq.getEmpPersonDto().setNmPersonFull(name + ServiceConstants.CASE_NM_ET_AL);
                        }
                        // CCMNH4D
                        this.updateCaseName(editEmployeeReq.getEmpPersonDto().getNmPersonFull(),
                                editEmployeeReq.getUlIdPerson(), editEmployeeReq.getNmPersonFull());
                        // CCMNH5D
                        this.updateStageName(editEmployeeReq.getEmpPersonDto().getNmPersonFull(),
                                editEmployeeReq.getUlIdPerson(), editEmployeeReq.getNmPersonFull());
                    }
                }
            }
            // CCMNC2D
            if (editEmployeeReq.getEmployeeDetailDto() != null) {
                this.editPersonCategory(editEmployeeReq.getEmployeeDetailDto(), ServiceConstants.REQ_FUNC_CD_UPDATE);
            }
            if (editEmployeeReq.getEmployeeDetailDto() != null) {
                if (editEmployeeReq.getEmployeeDetailDto().getDtEmpTermination() != null) {
                    if (!editEmployeeReq.getEmployeeDetailDto().getDtEmpTermination()
                            .equals(ServiceConstants.NULL_VALDAT)) {
                        // CCMNE0D
                        if (!editEmployeeReq.getEmployeeDetailDto().isExternalEmployee()) {
                            unitDao.deleteUnitEmpLinkByPersonIdAndInOut(editEmployeeReq.getUlIdPerson(),
                                    ServiceConstants.UNIT_MEMBER_OUT_ASSIGNED);
                            // CCMNH2D
                            empTempAssignDao.deleteEmpTempAssignByPersonId(editEmployeeReq.getUlIdPerson());
                        }
                        // CAUDE2D
                        if (editEmployeeReq.getEmployeeDetailDto().isExternalEmployee()) {
                            empSecClassLinkDao.deleteExternalUserSecClassLinkByPersonId(
                                    editEmployeeReq.getUlIdPerson());
                        } else {
                            empSecClassLinkDao.deleteEmpSecClassLinkByPersonId(editEmployeeReq.getUlIdPerson());
                        }
                    }
                }
            }
            editEmployeeRes.setUlIdPerson(editEmployeeReq.getUlIdPerson());
            personRaces = personRaceDao.getPersonRaceByPersonId(editEmployeeReq.getUlIdPerson());
            personEthnicitys = personEthnicityDao.getPersonEthnicityByPersonId(editEmployeeReq.getUlIdPerson());
            if (personRaces.size() > 0) {
                for (PersonRaceDto personRace : personRaces) {
                    szCdPersonRaceList.add(personRace.getCdPersonRace());
                }
                personRaceStringDto.setCdPersonRace(szCdPersonRaceList);
            }
            if (personEthnicitys.size() > 0) {
                for (PersonEthnicityDto personEthnicity : personEthnicitys) {
                    szCdPersonEthnicityList.add(personEthnicity.getCdPersonEthnicity());
                }
                personEthnicityStringDto.setCdPersonEthnicity(szCdPersonEthnicityList);
            }
            editEmployeeRes.setPersonRaceStringDto(personRaceStringDto);
            editEmployeeRes.setPersonEthnicityStringDto(personEthnicityStringDto);
        }
        log.info("TransactionId :" + editEmployeeReq.getTransactionId());
        return editEmployeeRes;
    }

    /**
     * Method Description:updateCaseName
     *
     * @param empNameDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void updateCaseName(String caseName, Long ulIdPerson, String nmPersonFull) {
        List<CapsCase> capsCases = null;
        Date date = new Date();
        if (ulIdPerson != null) {
            capsCases = caseDao.searchCaseByNameAndPersonId(nmPersonFull, ulIdPerson);
            if (capsCases != null) {
                for (CapsCase capsCase : capsCases) {
                    capsCase.setNmCase(caseName);
                    capsCase.setDtLastUpdate(date);
                    caseDao.updateCapsCase(capsCase);
                }
            }
        }
    }

    /**
     * Method Description:updateStageName
     *
     * @param empNameDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void updateStageName(String caseName, Long ulIdPerson, String nmPersonFull) {
        List<Stage> stages = null;
        Date date = new Date();
        if (ulIdPerson != null) {
            stages = stageDao.searchStageByNameAndPersonId(nmPersonFull, ulIdPerson);
            if (stages != null) {
                for (Stage stage : stages) {
                    stage.setNmStage(caseName);
                    stage.setDtLastUpdate(date);
                    stageDao.updateStage(stage);
                }
            }
        }
    }

    /**
     * Method Description:editPersonCategory
     *
     * @param employee
     * @param action
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void editPersonCategory(EmployeeDetailDto empDetailDto, String action) {
        List<PersonCategory> personCategorys = new ArrayList<PersonCategory>();
        Date date = new Date();
        PersonCategory personCategory = new PersonCategory();
        if (empDetailDto != null) {
            if ((!ObjectUtils.isEmpty(empDetailDto)) && null != (empDetailDto.getIdPerson())) {
                if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
                    personCategorys =
                            personCategoryDao.getPersonCategoryByPersonIdAndCategory(empDetailDto.getIdPerson(),
                                    ServiceConstants.PERSON_CATEGORY_EMPLOYEE,
                                    ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE);
                    if (personCategorys.size() > 0) {
                        for (PersonCategory pc : personCategorys) {
                            pc.setCdPersonCategory(ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE);
                            personCategoryDao.updatePersonCategory(pc);
                        }
                    }
                }
                if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
                    if (empDetailDto.getDtEmpTermination() == null ||
                            String.valueOf(empDetailDto.getDtEmpTermination()).equals(ServiceConstants.EMPTY_STRING)) {
                        personCategorys =
                                personCategoryDao.getPersonCategoryByPersonIdAndCategory(empDetailDto.getIdPerson(),
                                        ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE,
                                        ServiceConstants.PERSON_CATEGORY_EMPLOYEE);
                        if (personCategorys.size() > 0) {
                            for (PersonCategory pc : personCategorys) {
                                pc.setCdPersonCategory(ServiceConstants.PERSON_CATEGORY_EMPLOYEE);
                                personCategoryDao.updatePersonCategory(pc);
                            }
                        }
                    } else if (empDetailDto.getDtEmpTermination() != null &&
                            !String.valueOf(empDetailDto.getDtEmpTermination()).equals(ServiceConstants.EMPTY_STRING)) {
                        personCategorys =
                                personCategoryDao.getPersonCategoryByPersonIdAndCategory(empDetailDto.getIdPerson(),
                                        ServiceConstants.PERSON_CATEGORY_EMPLOYEE,
                                        ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE);
                        if (personCategorys != null) {
                            for (PersonCategory pc : personCategorys) {
                                pc.setCdPersonCategory(ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE);
                                personCategoryDao.updatePersonCategory(pc);
                            }
                        }
                    }
                }
                if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
                    if (empDetailDto.getDtEmpTermination() == null ||
                            String.valueOf(empDetailDto.getDtEmpTermination()).equals(ServiceConstants.EMPTY_STRING)) {
                        personCategorys =
                                personCategoryDao.getPersonCategoryByPersonIdAndCategory(empDetailDto.getIdPerson(),
                                        ServiceConstants.PERSON_CATEGORY_EMPLOYEE);
                        if (personCategorys.size() == 0) {
                            personCategory.setIdPerson(empDetailDto.getIdPerson());
                            personCategory.setCdPersonCategory(ServiceConstants.PERSON_CATEGORY_EMPLOYEE);
                            personCategory.setDtLastUpdate(date);
                            personCategoryDao.savePersonCategory(personCategory);
                        }
                    } else if (empDetailDto.getDtEmpTermination() != null &&
                            !String.valueOf(empDetailDto.getDtEmpTermination()).equals(ServiceConstants.EMPTY_STRING)) {
                        personCategorys =
                                personCategoryDao.getPersonCategoryByPersonIdAndCategory(empDetailDto.getIdPerson(),
                                        ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE);
                        if (personCategorys.size() == 0) {
                            personCategory.setIdPerson(empDetailDto.getIdPerson());
                            personCategory.setCdPersonCategory(ServiceConstants.PERSON_CATEGORY_FORMER_EMPLOYEE);
                            personCategory.setDtLastUpdate(date);
                            personCategoryDao.savePersonCategory(personCategory);
                        }
                    }
                }
            }
        }
    }

    /**
     * Method Name: getEmployeeProfileByLogon Method Description:This method
     * gets the Employee Security Profile using Employee's Logon Id.
     *
     * @param employeeLogon
     * @return employeeProfileDto
     */
    // CARC01D,CLSCB4D,CARC06D
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmployeeProfileRes getEmployeeProfileByLogon(String employeeLogon, Long idUser) {
        String securityProfileNew = null;

        Long idPerson = idUser;
        EmployeeDto employee = null;
        EmployeeProfileDto employeeProfileDTO = new EmployeeProfileDto();
        ExternalUserDto externalUserDto = null;
        EmployeeProfileRes employeeProfileRes = new EmployeeProfileRes();

        if (!StringUtils.isEmpty(employeeLogon)) {
            employee = employeeDao.getEmployeeByLogonId(employeeLogon);
            externalUserDto = employeeDao.getExtUserByLogonId(employeeLogon);
            if (ObjectUtils.isEmpty(employee) && !ObjectUtils.isEmpty(externalUserDto)) {
                idPerson = externalUserDto.getIdPerson();
            } else if (!ObjectUtils.isEmpty(employee)) {
                idPerson = employee.getIdPerson();
            }

        } else if (!TypeConvUtil.isNullOrEmpty(idUser)) {
            employee = employeeDao.getEmployeeByIdUser(idPerson);
            externalUserDto = searchExternalUser(idPerson);
        }

        if (ObjectUtils.isEmpty(employee)) {
            if (!ObjectUtils.isEmpty(externalUserDto) && externalUserDto.isExtUserActiveStatus()) {
                employeeProfileDTO.setUserType(UserType.RCCP_EXTERNAL_USER);
                employee = new EmployeeDto();
                employee.setIdPerson(externalUserDto.getIdPerson());
                PersonDto personDto = personDao.getPersonById(externalUserDto.getIdPerson());
                employee.setPersonFullName(personDto.getNmPersonFull());
                employee.setIndExtrnlEmpConsent(externalUserDto.getIndExtrnlUserConsent());
                boolean impactAccess = employeeDao.impactAccessForExternalUser(externalUserDto.getIdPerson());
                employee.setImpactAccessForExtUser(impactAccess);

            }
        } else if (!ObjectUtils.isEmpty(externalUserDto) && !ObjectUtils.isEmpty(employee)) {
            // Fetch the Security Profile for the user.
            List<EmpSecClassLink> empSecClassLinkList =
                    empSecClassLinkDao.getEmployeeSecurityProfile(employee.getIdPerson());
            // Check if the User has RCCP Profiles.
            boolean isRCCPUser = false;
            if (!ObjectUtils.isEmpty(empSecClassLinkList)) {
                isRCCPUser = empSecClassLinkList.stream().anyMatch(
                        e -> SEC_PROFILE_RCCP.equalsIgnoreCase(e.getSecurityClass().getCdSecurityClassName()));
                if (isRCCPUser) {
                    employeeProfileDTO.setUserType(UserType.RCCP_EXTERNAL_USER);
                } else {
                    employeeProfileDTO.setUserType(UserType.INTERNAL_STAFF);
                }
            } else {
                employeeProfileDTO.setUserType(UserType.INTERNAL_STAFF);
            }

            employee.setIndExtrnlEmpConsent(externalUserDto.getIndExtrnlUserConsent());
            boolean impactAccess = employeeDao.impactAccessForExternalUser(externalUserDto.getIdPerson());
            employee.setImpactAccessForExtUser(impactAccess);
            // employeeDao.setExtrnlUserLoginUpdateLastDateTime(externalUserDto.getIdPerson());
        } else if (ObjectUtils.isEmpty(externalUserDto) && !ObjectUtils.isEmpty(employee)) {
            employeeProfileDTO.setUserType(UserType.INTERNAL_STAFF);
        }

        if (ObjectUtils.isEmpty(employee)) {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrorCode(ServiceConstants.DATA_NOT_FOUND_EXCEPTION);
            errorDto.setErrorMsg(messageSource.getMessage("employee.not.found.employeelogonid", null, Locale.US));
            employeeProfileRes.setErrorDto(errorDto);
            return employeeProfileRes;

        }
        List<EmpSecClassLink> empSecClassLinkList = empSecClassLinkDao.getEmployeeSecurityProfile(idPerson);
        if (ObjectUtils.isEmpty(empSecClassLinkList)) {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrorCode(ServiceConstants.DATA_NOT_FOUND_EXCEPTION);
            errorDto.setErrorMsg(messageSource.getMessage("employee.not.found.employeelogonid", null, Locale.US));
            employeeProfileRes.setErrorDto(errorDto);
            return employeeProfileRes;
        }

        List<String> empSecClassList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(empSecClassLinkList)) {
            empSecClassLinkList.stream().forEach(
                    empSecClass -> empSecClassList.add(empSecClass.getSecurityClass().getCdSecurityClassName()));
        }
        List<EmpTempAssign> empTempAssgnList = empTempAssignDao.getActiveEmpTempAssignByPersonId(idPerson);
        String securityProfile = mergeSecurityProfiles(empSecClassLinkList);
        log.info("Logged-In User's merged Profile " + securityProfile);
        if (!TypeConvUtil.isNullOrEmpty(empTempAssgnList)) {
            securityProfileNew = securityProfile;
            for (EmpTempAssign empTempAssign : empTempAssgnList) {
                // 2 times loops.
                log.info("Assigned-To Id " + empTempAssign.getIdPersonEmp());
                List<EmpSecClassLink> empTempAssignSecLink =
                        empSecClassLinkDao.getEmployeeSecurityProfile(empTempAssign.getIdPersonEmp());
                //PPM 68168, Not allowing TLETS roles to designee.
                empTempAssignSecLink.removeIf(security -> ServiceConstants.EXCLUDE_FOR_DESIGNEE_LIST.contains(
                        security.getSecurityClass().getCdSecurityClassName()));
                securityProfileNew = mergeSecurityProfilesEmpTemp(empTempAssignSecLink, securityProfileNew);
            }
        }
        if (!TypeConvUtil.isNullOrEmpty(securityProfileNew)) {
            securityProfile = securityProfileNew;
        }
        log.info("User's final profile: (in getEmployeeProfileByLogon) " + securityProfile);

        Type listType = new TypeToken<List<EmpTempAssignDto>>() {
        }.getType();
        @SuppressWarnings("unchecked") List<EmpTempAssignDto> empTempAssignDtoList =
                (List<EmpTempAssignDto>) modelMapper.map(empTempAssgnList, listType);
        employeeProfileDTO.setEmpSecClassLinkList(empSecClassList);
        employeeProfileDTO.setEmployeeDetails(employee);
        employeeProfileDTO.setEmpTempAssgnList(empTempAssignDtoList);
        employeeProfileDTO.setSecurityProfile(securityProfile);

        employeeProfileRes.setEmployeeProfileDto(employeeProfileDTO);
        return employeeProfileRes;

    }

    /**
     * Method Name: updateEmployeeLogon Method Description: This method updates
     * the ID_EMPLOYEE_LOGON column
     *
     * @param employeeLogonId
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void updateEmployeeLogon(String employeeLogonId, Long personId) {
        employeeDao.updateEmployeeLogon(employeeLogonId, personId);
    }

	/**
	 *
	 * Method Description:Utilitiy method to merge multiple security profile
	 * into one profile.
	 *
	 * @param empSecClassLinkList
	 * @return securityProfile
	 */
	private String mergeSecurityProfiles(List<EmpSecClassLink> empSecClassLinkList) {
		   String maxString =
				 empSecClassLinkList.stream().map(o -> o.getSecurityClass().getTxtSecurityClassProfil())
					   .max(Comparator.comparingInt(String::length)).get();
		   StringBuilder securityProfileBuffer = new StringBuilder(maxString.length());
		   IntStream.range(0, maxString.length()).forEach(i -> securityProfileBuffer.append('0'));
		   if (empSecClassLinkList != null && empSecClassLinkList.size() > 0) {
			  @SuppressWarnings("rawtypes")
			  Iterator iterator = empSecClassLinkList.iterator();
			  while (iterator.hasNext()) {
				 EmpSecClassLink empSecClassLink = (EmpSecClassLink) iterator.next();
				 String securityClassProfile = empSecClassLink.getSecurityClass().getTxtSecurityClassProfil();
				 log.info("Logged in User's profile: " + securityClassProfile);
				 for (int i = 0; i < securityClassProfile.length(); i++) {
					if (securityClassProfile.charAt(i) == '1') {
					   securityProfileBuffer.setCharAt(i, '1');
					}
				 }
			  }
		   }
		   return securityProfileBuffer.toString();
	}
    /**
     * Method Description:The method is used to Merge the loggedin User's
     * temporary security profile with the regular security profile existing for
     * the logged in user.
     *
     * @param empSecClassLinkList
     * @return securityProfile
     */
    private String mergeSecurityProfilesEmpTemp(List<EmpSecClassLink> empSecClassLinkList,
                                                String securityProfileLoggedIn) {
        StringBuilder loggedInUserSecurityProfileBuffer = new StringBuilder(securityProfileLoggedIn);
         // PPM 79191: artf234288 - Logged-in user security profile string is lesser than the designator profile.
        if (!CollectionUtils.isEmpty(empSecClassLinkList)) {
            String maxString =
                    empSecClassLinkList.stream().map(o -> o.getSecurityClass().getTxtSecurityClassProfil())
                            .max(Comparator.comparingInt(String::length)).get() ;
            if (maxString.length() > loggedInUserSecurityProfileBuffer.length()) {
                IntStream.range(loggedInUserSecurityProfileBuffer.length(), maxString.length()).forEach(i -> loggedInUserSecurityProfileBuffer.append('0'));
            }
            @SuppressWarnings("rawtypes") Iterator iterator = empSecClassLinkList.iterator();
            while (iterator.hasNext()) {
                EmpSecClassLink empSecClassLink = (EmpSecClassLink) iterator.next();
                String securityClassProfile = empSecClassLink.getSecurityClass().getTxtSecurityClassProfil();
                log.info("Designee User's profile: " + securityClassProfile);
                for (int i = 0; i < securityClassProfile.length(); i++) {
                    if (securityClassProfile.charAt(i) == '1') {
                        if (i == 32 && securityProfileLoggedIn.charAt(i) != '1') {
                            loggedInUserSecurityProfileBuffer.setCharAt(i, '0');
                        } else if (i == 99 && securityProfileLoggedIn.charAt(i) != '1') {
                            loggedInUserSecurityProfileBuffer.setCharAt(i, '0');
                        } else if (i != UserRolesEnum.MAINT_ALL_UNITS.ordinal()) {
                            loggedInUserSecurityProfileBuffer.setCharAt(i, '1');
                        }
                    }
                }
            }
        }
        log.info("Designee User's intermediate profile: (in mergeSecurityProfilesEmpTemp) " +
                loggedInUserSecurityProfileBuffer.toString());
        return loggedInUserSecurityProfileBuffer.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * us.tx.state.dfps.service.admin.service.EmployeeService#getEmployeeByClass
     * (java.lang.String, java.lang.String)
     */
    // UIDS 2.3.3.5 - Remove a child from home - To-Do Detail
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public List<EmployeePersonDto> getEmployeeByClass(String employeeClass, String securityClass) {
        return employeeDao.getEmployeeByClass(employeeClass, securityClass);
    }

    /**
     * Method Name: isExternalStaff Method Description: Check is the person is
     * external staff
     *
     * @param idPerson
     * @return
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isExternalStaff(Long idPerson) {
        log.info("Inside isExternalStaff in EmployeeService");
        return employeeDao.isExternalStaff(idPerson);
    }

    /**
     * Method Name: getLocalPlacementSupervisor Method Description:Get LPS
     * Supervisor for the region
     *
     * @param cdRegion
     * @return
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmployeePersonDto getLocalPlacementSupervisor(String cdRegion) {
        Employee employee = employeeDao.getLocalPlacementSupervisor("0" + cdRegion);
        //EmployeePersonDto employeePersonDto = new EmployeePersonDto();
        EmployeePersonDto employeePersonDto = null;
        if (!ObjectUtils.isEmpty(employee)) {
            employeePersonDto = new EmployeePersonDto();
            employeePersonDto.setIdPerson(employee.getIdPerson());
            employeePersonDto.setNmEmployeeFirst(employee.getNmEmployeeFirst());
            employeePersonDto.setNmEmployeeLast(employee.getNmEmployeeLast());
            employeePersonDto.setNmEmployeeMiddle(employee.getNmEmployeeMiddle());
        }
        return employeePersonDto;
    }

    /*
     * (non-Javadoc)
     *
     * @see us.tx.state.dfps.service.admin.service.EmployeeService#
     * getLocalPlacementSupervisorList(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public AssignBean getLocalPlacementSupervisorList(String cdRegion, AssignBean assignBean) {
        return employeeDao.getLocalPlacementSupervisorList("0" + cdRegion, assignBean);
    }

    /**
     * Method Name: searchExternalUser Method Description:get the external user
     * details based on idExtUser
     *
     * @param idExtUser
     * @return ExternalUserDto
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public ExternalUserDto searchExternalUser(Long idExtUser) {
        ExternalUserDto externalUserDto = null;
        ExtrnlUser extrnlUser = employeeDao.getExternalUserEntityById(idExtUser);
        if (!ObjectUtils.isEmpty(extrnlUser)) {
            externalUserDto = new ExternalUserDto();
            BeanUtils.copyProperties(extrnlUser, externalUserDto);
            // Fetch the Security Profile for the user.
            List<EmpSecClassLink> empSecClassLinkList = empSecClassLinkDao.getEmployeeSecurityProfile(idExtUser);
            // Check if the User has RCCP Profiles.
            boolean isRCCPUser = false;
            if (!ObjectUtils.isEmpty(empSecClassLinkList)) {
                isRCCPUser = empSecClassLinkList.stream()
                        .anyMatch(e -> SEC_PROFILE_RCCP.equals(e.getSecurityClass().getCdSecurityClassName()));
            }

            if (ObjectUtils.isEmpty(externalUserDto.getDtExtrnlUserEnd()) && isRCCPUser) {
                externalUserDto.setExtUserActiveStatus(true);
            }
        }
        return externalUserDto;
    }

    /**
     * Method Name: externalUserAccessToChildAPR Method Description: check for
     * external user access to child active placement resources
     *
     * @param idPerson
     * @param rccpLoggedInUserId
     * @return boolean
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Boolean externalUserAccessToChildAPR(Long idPerson, Long rccpLoggedInUserId) {

        return employeeDao.externalUserAccessToChildActivePlacement(rccpLoggedInUserId, idPerson);

    }

    /**
     * Method Name: externalUserAccessToChildAPR Method Description: check for
     * external user access to child active placement resources
     *
     * @param idPerson
     * @param rccpLoggedInUserId
     * @return Boolean
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public ExtUserChildPlanStatusEnum validateExtUserAccessToChildPlan(Long idchild, Long rccpLoggedInUserId) {

        ExternalUserDto externalUserDto = searchExternalUser(rccpLoggedInUserId);
        if (ObjectUtils.isEmpty(externalUserDto) || !externalUserDto.isExtUserActiveStatus()) {
            return ExtUserChildPlanStatusEnum.EXT_USER_IN_ACTIVE;
        }
        boolean userAccess = externalUserAccessToChildAPR(idchild, rccpLoggedInUserId);
        if (!userAccess) {
            return ExtUserChildPlanStatusEnum.EXT_USER_ACTIVE_NO_ACCESS_CHILD_PLAN;
        } else {
            return ExtUserChildPlanStatusEnum.EXT_USER_ACTIVE_ACCESS_CHILD_PLAN;
        }

    }

    /**
     * Method Name: searchExternalUserByLogonID Method Description:get external
     * user by logon id
     *
     * @param idLogon
     * @return ExternalUserDto
     */
    public ExternalUserDto searchExternalUserByLogonID(String idLogon) {
        ExternalUserDto externalUserDto = employeeDao.getExtUserByLogonId(idLogon);
        if (!ObjectUtils.isEmpty(externalUserDto)) {
            if (ObjectUtils.isEmpty(externalUserDto.getDtExtrnlUserEnd())) {
                externalUserDto.setExtUserActiveStatus(true);
            }
        }
        return externalUserDto;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmployeeProfileRes getEmployeeSecLinkByLogonId(String logonId) {
        EmployeeProfileRes employeeProfileRes = new EmployeeProfileRes();
        EmployeeProfileDto employeeProfileDto = new EmployeeProfileDto();
        employeeProfileDto.setEmpSecClassLinkList(empSecClassLinkDao.getEmployeeSecurityProfileByLogonId(logonId));
        employeeProfileRes.setEmployeeProfileDto(employeeProfileDto);
        return employeeProfileRes;
    }

    /**
     * Method Name: externalUserBackGroundCheck Method Description:external user
     * back ground check for logon id
     *
     * @param idLogon
     * @return booelan
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean externalUserBackGroundCheck(long idLogon) {
        return employeeDao.externalUserBackGroundCheck(idLogon);
    }

    @Override
    public SubscriptionRes createSubscriptions(SubscriptionReq subscriptionReq) {
        SubscriptionRes subscriptionRes = new SubscriptionRes();
        List<SSCCCatchmentDto> ssccCatchmentDtos = employeeDao.fetchSSCCCatchment(subscriptionReq.getPersonId());

        ssccCatchmentDtos.forEach(ssccCatchmentDto -> {
            if (!StringUtils.isEmpty(
                    employeeDao.fetchVendorUrl(ssccCatchmentDto.getRegion(), ssccCatchmentDto.getSsccCatchment()))) {
                subscriptionReq.getSubscriptionTypes().forEach(subscriptionType -> {
                    if (StringUtils.isNotEmpty(subscriptionType)) {
                        Subscription subscription = new Subscription();
                        subscription.setActiveInd(Boolean.TRUE);
                        subscription.setSubscriberId(ssccCatchmentDto.getPersonId());
                        subscription.setUrl(employeeDao.fetchVendorUrl(ssccCatchmentDto.getRegion(),
                                ssccCatchmentDto.getSsccCatchment()));
                        subscription.setObject(EventObject.fromValue(getMapToSubscriptionTypes(subscriptionType)));
                        subscription.setEntity(ID_PERSON);
                        subscription.setAction(EventAction.CREATED);
                        subscription.setLastUpdateDate(new Date());
                        subscription.setCreatedDate(new Date());
                        subscription.setCreatedPersonId(Long.valueOf(subscriptionReq.getUserId()));
                        subscription.setLastUpdatePersonId(Long.valueOf(subscriptionReq.getUserId()));
                        notificationApi.createSubscription(subscription);
                    }
                });
            }
        });
        subscriptionRes.setStatus(HttpStatus.OK);
        return subscriptionRes;
    }

    @Override
    public SubscriptionRes deleteSubscriptions(SubscriptionReq subscriptionReq) {
        SubscriptionRes subscriptionRes = new SubscriptionRes();
        subscriptionReq.getSubscriptionIds().forEach(s -> {
            Long subscriptionId = Long.valueOf(s);
            notificationApi.deleteSubscription(subscriptionId);
        });
        subscriptionRes.setStatus(HttpStatus.OK);
        return subscriptionRes;
    }

    @Override
    public SubscriptionRes updateSubscriptions(SubscriptionReq subscriptionReq) {
        SubscriptionRes subscriptionRes = new SubscriptionRes();
        List<SSCCCatchmentDto> ssccCatchmentDtos = employeeDao.fetchSSCCCatchment(subscriptionReq.getPersonId());

        ssccCatchmentDtos.forEach(ssccCatchmentDto -> {
            if (!StringUtils.isEmpty(
                    employeeDao.fetchVendorUrl(ssccCatchmentDto.getRegion(), ssccCatchmentDto.getSsccCatchment()))) {
                subscriptionReq.getUpdateActiveToActiveIndMap().forEach((id, subscriptionType) -> {
                    Long subscriptionId = Long.valueOf(id);
                    Subscription subscription = new Subscription();
                    subscription.setActiveInd(Boolean.TRUE);
                    subscription.setUrl(employeeDao.fetchVendorUrl(ssccCatchmentDto.getRegion(),
                            ssccCatchmentDto.getSsccCatchment()));
                    subscription.setObject(EventObject.fromValue(getMapToSubscriptionTypes(subscriptionType)));
                    subscription.setAction(EventAction.UPDATED);
                    subscription.setLastUpdatePersonId(Long.valueOf(subscriptionReq.getUserId()));
                   notificationApi.updateSubscription(subscriptionId, subscription);
                });
            }
        });
        return subscriptionRes;
    }

    public SubscriptionRes getSubscriptions(SubscriptionReq subscriptionReq) {
        SubscriptionRes subscriptionRes = new SubscriptionRes();
        List<Subscription> subscriptions =
                notificationApi.getSubscriptions(subscriptionReq.getPersonId(), Boolean.FALSE, null, null);
        Map<Long, SubscriptionDto> newSubscriptionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(subscriptions)) {
            Map<Long, Subscription> subscriptionMap = subscriptions.stream()
                    .collect(Collectors.toMap(Subscription::getSubscriptionId, Function.identity()));
            subscriptionMap.forEach((k, v) -> {
                newSubscriptionMap.put(k, convertToDto(v));
            });
        }
        subscriptionRes.setSubscriptionsMap(newSubscriptionMap);
        return subscriptionRes;
    }


    private SubscriptionDto convertToDto(Subscription subscription) {

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setSubscriberId(subscription.getSubscriberId());
        subscriptionDto.setSubscriptionId(subscription.getSubscriptionId());
        subscriptionDto.setIndActive(subscription.getActiveInd());
        subscriptionDto.setSubscriptionType(getMapToSubscriptionCodes(subscription.getObject().getValue()));
        return subscriptionDto;
    }





}
