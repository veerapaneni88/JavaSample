package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NotifToParentEngReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.NotifToParentEngPrefillData;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.NotifToParentEngDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.NotifToParentDto;
import us.tx.state.dfps.service.workload.dto.PrimaryWorkerDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;
import us.tx.state.dfps.service.workload.service.NotifToParentEngService;

/**
 * service-business- IMPACT <This service will Retrieve names and locations> Mar
 * 5, 2018- 11:55:09 AM © 2017 Texas Department of Family and Protective
 * Services
 */

@Service
@Transactional
public class NotifToParentEngServiceImpl implements NotifToParentEngService {

	@Autowired
	NotifToParentEngDao notifToParentEngDao;

	@Autowired
	PopulateLetterDao populateLetterDao;

	@Autowired
	CommonApplicationDao commonApplicationDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	AdminReviewDao adminReviewDao;

	@Autowired
	CaseMaintenanceSelectStageDao caseMaintenanceSelectStageDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	NotifToParentEngPrefillData notifToParentEngPrefillData;

	private static final Logger log = Logger.getLogger(NotifToParentEngServiceImpl.class);

	public NotifToParentEngServiceImpl() {

	}

	/**
	 * Service Name: ccfc45s Method Description: This service will Retrieve
	 * names and locations of Board Members of TDPRS and the Executive Directory
	 * Name from table CODES_TABLES
	 *
	 * @param notifToParentEngReq
	 * @return notifToParentEngRes @ the service exception
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getParentReporterNotified(NotifToParentEngReq notifToParentEngReq) {
		// select the name and title of the document
		Long idStageRelated = null;
		NotifToParentDto notifToParentDto = new NotifToParentDto();
		Date currDate = DateUtils.getCurrentDate();
		Date dtStageCloseDate = null;

		// This dao clsc03d retrieves Name and title info based upon Code Type.
		List<CodesTablesDto> codesTableDtoList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
				ServiceConstants.NAME);
		notifToParentDto.setCodesTablesDtolist(codesTableDtoList);

		// This dao csec35d retrieves the first, middle and last name for an
		// ID_PERSON from the from service
		NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(notifToParentEngReq.getIdPerson());
		notifToParentDto.setNameDetailDto(nameDetailDto);

		// This dao csec34d used to retrieve an address of a specified type from
		// the address person link table
		PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(notifToParentEngReq.getIdPerson(),
				ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
		if (!TypeConvUtil.isNullOrEmpty(personAddressDto)) {
			personAddressDto.setDtPersAddrLinkEnd(currDate);
		}
		notifToParentDto.setPersonAddressDto(personAddressDto);

		// Call to the CSES63D dam to get the admin_review record

		AdminReviewDto adminReview = notifToParentEngDao.getAdminReview(notifToParentEngReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(adminReview)) {
			idStageRelated = adminReview.getIdStageRelated();

			if (!TypeConvUtil.isNullOrEmpty(adminReview.getIdPerson())) {
				// callCSEC35D
				notifToParentEngReq.setIdPerson(adminReview.getIdPerson());
				NameDetailDto nameDetail = commonApplicationDao.getNameDetails(notifToParentEngReq.getIdPerson());
				notifToParentDto.setNameDetailDto(nameDetail);
			}
			/*
			 ** Call to the pCINT21D dam to retrieve the close date for the most
			 * recent stage.
			 */
			StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
			StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();

			stageRtrvInDto.setUlIdStage(idStageRelated);
			caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
			notifToParentDto.setStageRtrvOutDto(stageRtrvOutDto);
			dtStageCloseDate = notifToParentDto.getStageRtrvOutDto().getDtStageClose();

		}
		notifToParentDto.setAdminReviewdto(adminReview);

		// call to the clsc65d to retrieve a list of prior allegations and
		// victims
		// need dtDtStageClose from CINT21D, pass YES into notifToParentEngReq

		List<StageReviewDto> stageReViewDto = notifToParentEngDao.getStageReviewed(notifToParentEngReq.getIdStage(),
				dtStageCloseDate, ServiceConstants.YES);
		notifToParentDto.setStageReviewDtolist(stageReViewDto);
		/*
		 ** Call to the pCLSC65D dam to retrieve a list of current aggregations
		 * and victims pass No into notifToParentEngReq
		 */

		notifToParentDto.setStageNotReviewDto(notifToParentEngDao.getStageReviewed(notifToParentEngReq.getIdStage(),
				dtStageCloseDate, ServiceConstants.NO));
		/*
		 ** Call to the pCSEC53D dam to retrieve the primary worker for the
		 ** admin_review stage
		 */

		PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notifToParentEngReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
			/*
			 ** Call to the pCSEC01D dam to retrieve the first, middle, and last
			 * name of the primary worker
			 */
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao
					.searchPersonPhoneName(primaryWorkerDto.getIdPerson());

			if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)
					&& !(ServiceConstants.PERSON_PHONE_TYPE_BUSINESS.equals(employeePersPhNameDto.getCdPhoneType())
							|| ServiceConstants.BUSINESS_CELL.equals(employeePersPhNameDto.getCdPhoneType()))) {

				employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
				employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getNbrMailCodePhoneExt());
			}
			employeePersPhNameDto.setDtEmpTermination(currDate);
			notifToParentDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}
		notifToParentDto.setPrimaryWorkerDto(primaryWorkerDto);

		log.info("TransactionId :" + notifToParentEngReq.getTransactionId());
		return notifToParentEngPrefillData.returnPrefillData(notifToParentDto);
	}

}
