package us.tx.state.dfps.service.kinhomeinfo.serviceimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_CAS;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_STG;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringHelper;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetentionDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kinhomeinfo.dao.KinHomeInfoDao;
import us.tx.state.dfps.service.kinhomeinfo.service.KinHomeInfoService;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

@Service
@Transactional
public class KinHomeInfoServiceImpl implements KinHomeInfoService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private KinHomeInfoDao kinHomeInfoDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	SvcAuthDetailDao svcAuthDetailDao;

	@Autowired
	RecordsRetentionDao recordsRetentionDao;

	@Autowired
	CaseFileManagementDao caseFileManagementDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-KinHomeInfoServiceLog");

	/**
	 * Method Name: getResServiceInfo Method Description:Fetches the Resource
	 * information
	 * 
	 * @param kinHomeInfoDto
	 * @return KinHomeInfoDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public KinHomeInfoDto getResServiceInfo(KinHomeInfoDto kinHomeInfoDto) {
		return kinHomeInfoDao.getResServiceInfo(kinHomeInfoDto);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int updateKinHomeStatus(KinHomeInfoDto kinHomeInfoDto) {
		return kinHomeInfoDao.updateKinHomeStatus(kinHomeInfoDto);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public KinHomeInfoDto getKinHomeInfo(Long stageId) {
		return kinHomeInfoDao.getKinHomeInfo(stageId);
	}

	@Override
	public int updateKinHomeInfrmtnStatus(KinHomeInfoDto kinHomeInfoDto) {
		return kinHomeInfoDao.updateKinHomeInfrmtnStatus(kinHomeInfoDto);
	}

	@Override
	public void closeKinHome(KinHomeInfoDto kinHomeInfoDto) {
		Date today = Calendar.getInstance().getTime();
		List<StagePersonLink> personList = null;
		StageProgDto stageProgDto = new StageProgDto(kinHomeInfoDto.getIdHomeStage());
		StageDto stageDto = stageDao.getStageById(kinHomeInfoDto.getIdHomeStage());
		EventValueDto eventValueDto = new EventValueDto();
		StgPersonLinkDto stgPersonLinkDto = new StgPersonLinkDto();

		List<StageProgDto> stageProgDtos = stageProgDao.getStgProgroession(stageProgDto.getCdStageProgStage(), stageProgDto.getCdStageProgStage(),
				stageProgDto.getCdStageProgRsnClose());

		if (!CollectionUtils.isEmpty(stageProgDtos)) {
			stageProgDto = stageProgDtos.get(0);
		}

		if (ObjectUtils.isEmpty(stageDto.getDtStageClose())) {
			svcAuthDetailDao.termServiceAuthDetails(kinHomeInfoDto.getIdHomeResource());

			stageDto.setCdStageReasonClosed(kinHomeInfoDto.getResourceClosureReason());
			kinHomeInfoDao.closeStage(stageDto, today);

			eventValueDto.setEventStatusCode(ServiceConstants.EVENT_STATUS_COMP);
			eventValueDto.setCdEventType(CEVNTTYP_STG);
			eventValueDto.setEventDescr("Kin Home Closed");
			eventValueDto.setCdEventTask(StringHelper.EMPTY_STRING);
			eventValueDto.setIdCase(kinHomeInfoDto.getIdHomeCase());
			eventValueDto.setIdStage(kinHomeInfoDto.getIdHomeStage());
			eventValueDto.setIdPerson(null);

			eventDao.createEvent(eventValueDto, kinHomeInfoDto);

			stgPersonLinkDto.setIdStage(kinHomeInfoDto.getIdHomeStage());
			stgPersonLinkDto.setCdStagePersRole(ServiceConstants.PRIMARY_WORKER);

			stgPersonLinkDto = kinHomeInfoDao.getStagePersonInfo(stgPersonLinkDto);

			EmployeeDto employeeDto = kinHomeInfoDao.getSelectEmployee(stgPersonLinkDto.getIdPerson());
			Long officeId = employeeDto.getIdOffice();
			Long idUnit = employeeDto.getIdEmpUnit();

			kinHomeInfoDao.updateStagePersonLink(stgPersonLinkDto, ServiceConstants.HISTORICAL_PRIMARY);

			kinHomeInfoDao.deleteStagePersonRecords(stgPersonLinkDto.getIdStage());

			personList = new ArrayList<StagePersonLink>();
			personList.addAll(stagePersonLinkDao.getStagePersonLinkNonHpRole(stgPersonLinkDto.getIdStage()));
			if (!CollectionUtils.isEmpty(personList)) {

				StgPersonLinkDto finalStgPersonLinkDto = stgPersonLinkDto;
				personList.stream().forEach(personBean -> {

					if (!(ServiceConstants.STAFF).equals(personBean.getCdStagePersType())) {
						boolean isExists = kinHomeInfoDao.checkPersonInOpenStage(finalStgPersonLinkDto.getIdStage(),
								finalStgPersonLinkDto.getIdPerson());

						if (!isExists) {
							String status = CodesConstant.INACTIVE;
							Long personId = personBean.getIdPerson();
							kinHomeInfoDao.updatePersonStatus(personId, status);
						}
					}
				});
			}

			Date dtCaseClosed = kinHomeInfoDao.getCaseClosedDate(kinHomeInfoDto.getIdHomeCase());
			boolean isOtherOpenStage = kinHomeInfoDao.checkOtherStageOpen(kinHomeInfoDto.getIdHomeStage(),
					kinHomeInfoDto.getIdHomeCase());

			if (ObjectUtils.isEmpty(dtCaseClosed) && !isOtherOpenStage) {
				kinHomeInfoDao.updateCase(today, kinHomeInfoDto.getIdHomeCase());
				kinHomeInfoDao.updateSituation(today, kinHomeInfoDto.getIdHomeCase());

				eventValueDto.setEventStatusCode(ServiceConstants.EVENT_STATUS_COMP);
				eventValueDto.setCdEventType(CEVNTTYP_CAS);
				eventValueDto.setEventDescr("Case Closed");

				eventDao.createEvent(eventValueDto, kinHomeInfoDto);

				RecordsRetentionDto record = new RecordsRetentionDto();
				record.setIdRecRtnCase(kinHomeInfoDto.getIdHomeCase());
				RecordsRetentionDto recordReturn = recordsRetentionDao.getRecordsRetentionDestructionDate(record.getIdRecRtnCase());

				RecordsRetentionDto recordsRetentionDto = new RecordsRetentionDto();
				recordsRetentionDto.setIdRecRtnCase(kinHomeInfoDto.getIdHomeCase());

				RecordsRetentionDto retRecordsRetentionDto = kinHomeInfoDao.getRecordsRetention(recordsRetentionDto.getIdRecRtnCase());

				if (!ObjectUtils.isEmpty(retRecordsRetentionDto)) {
					retRecordsRetentionDto.setDtRecRtnDestroyActual(recordReturn.getDtRecRtnDestroyActual());
					retRecordsRetentionDto.setDtRecRtnDestroyEligible(recordReturn.getDtRecRtnDestroyActual());

					retRecordsRetentionDto.setCdRecRetentionType(recordReturn.getCdRecRetentionType());
					kinHomeInfoDao.updateRecordsRetention(retRecordsRetentionDto);
				} else {
					recordsRetentionDto.setIdRecRtnCase(kinHomeInfoDto.getIdHomeCase());
					recordsRetentionDto.setDtRecRtnDestroyActual(recordReturn.getDtRecRtnDestroyActual());
					recordsRetentionDto.setDtRecRtnDestroyEligible(recordReturn.getDtRecRtnDestroyActual());
					recordsRetentionDto.setCdRecRetentionType(recordReturn.getCdRecRetentionType());
					recordsRetentionDto.setRecRtnDestoryDtReason(null);
					recordsRetentionDao.insertRecordsRetention(recordsRetentionDto);
				}

				CaseFileManagement csfilemgmt = new CaseFileManagement();
				csfilemgmt.setIdCaseFileCase(kinHomeInfoDto.getIdHomeCase());

				CaseFileManagement retCasefile = caseFileManagementDao.findCaseFileManagementById(csfilemgmt.getIdCaseFileCase());

				if (retCasefile != null ) {
					retCasefile.setCdCaseFileOfficeType(CodesConstant.CASE_FILE_MGMT_TYPE);
					retCasefile.setIdCaseFileCase(retCasefile.getIdCaseFileCase());

					if (!TypeConvUtil.isNullOrEmpty(officeId)) {
						Office office = (Office) sessionFactory.getCurrentSession().get(Office.class, officeId);
						if (TypeConvUtil.isNullOrEmpty(office)) {
							throw new DataNotFoundException(
									messageSource.getMessage("record.not.found.office", null, Locale.US));
						}
						retCasefile.setOffice(office);
					}

					if (!TypeConvUtil.isNullOrEmpty(idUnit)) {
						Unit unit = (Unit) sessionFactory.getCurrentSession().get(Unit.class, idUnit);
						if (TypeConvUtil.isNullOrEmpty(unit)) {
							throw new DataNotFoundException(
									messageSource.getMessage("record.not.found.unit", null, Locale.US));
						}
						retCasefile.setUnit(unit);
					}

					retCasefile.setDtLastUpdate(new Date());
					caseFileManagementDao.updateCaseFileManagement(retCasefile);
				} else {
					csfilemgmt.setAddrCaseFileCity(null);
					csfilemgmt.setAddrCaseFileStLn1(null);
					csfilemgmt.setAddrCaseFileStLn2(null);
					csfilemgmt.setCdCaseFileOfficeType(CodesConstant.CASE_FILE_MGMT_TYPE);
					csfilemgmt.setDtCaseFileArchCompl(null);
					csfilemgmt.setDtCaseFileArchElig(null);

					if (!TypeConvUtil.isNullOrEmpty(officeId)) {
						Office office = (Office) sessionFactory.getCurrentSession().get(Office.class, officeId);
						if (TypeConvUtil.isNullOrEmpty(office)) {
							throw new DataNotFoundException(
									messageSource.getMessage("record.not.found.office", null, Locale.US));
						}
						csfilemgmt.setOffice(office);
					}

					if (!TypeConvUtil.isNullOrEmpty(idUnit)) {
						Unit unit = (Unit) sessionFactory.getCurrentSession().get(Unit.class, idUnit);
						if (TypeConvUtil.isNullOrEmpty(unit)) {
							throw new DataNotFoundException(
									messageSource.getMessage("record.not.found.unit", null, Locale.US));
						}
						csfilemgmt.setUnit(unit);
					}
					CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(kinHomeInfoDto.getIdHomeCase());
					csfilemgmt.setCapsCase(capsCase);
					csfilemgmt.setIdCaseFileCase(kinHomeInfoDto.getIdHomeCase());
					csfilemgmt.setTxtAddSkpTrn(null);
					csfilemgmt.setDtLastUpdate(new Date());
					caseFileManagementDao.insertCaseFileManagement(csfilemgmt);
				}

				todoDao.deleteTodo(kinHomeInfoDto.getIdHomeCase());
			}
		}
	}

	public int rejectKinHome(KinHomeInfoDto kinHomeInfoDto) {
		return kinHomeInfoDao.savePaymentInfo(kinHomeInfoDto);
	}

	@Override
	public void deleteResourcePhone(ResourcePhone resourcePhone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteResourceAddress(ResourceAddress resourceAddress) throws DataNotFoundException {
		// TODO Auto-generated method stub

	}

}