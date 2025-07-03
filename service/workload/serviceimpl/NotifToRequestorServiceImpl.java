package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.Date;
import java.util.Optional;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NotifToRequestorReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.fce.dto.EligibilityDeterminationFceDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.NotifToRequestorPrefillData;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.NotifToParentEngDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.NotifToRequestorDto;
import us.tx.state.dfps.service.workload.dto.PrimaryWorkerDto;
import us.tx.state.dfps.service.workload.service.NotifToRequestorService;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Converted
 * service CCFC46S Mar 7, 2018- 11:13:47 AM © 2017 Texas Department of Family
 * and Protective Services
 */
@Service
@Transactional
public class NotifToRequestorServiceImpl implements NotifToRequestorService {

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
	NotifToRequestorPrefillData prefillData;

	/**
	 * Method Name: getRequestor Method Description: Service method to make DAO
	 * calls and gather data
	 * 
	 * @param notifToRequestorReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonFormRes getRequestor(NotifToRequestorReq notifToRequestorReq) {
		NotifToRequestorDto prefillDto = new NotifToRequestorDto();
		long idCaseworker = 0l;
		long idPerson = 0l;
		long idStageRelated = 0l;
		Date dtStageCloseDate = null;
		Date currDate = DateUtils.getCurrentDate();

		// CSES63D
		AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(notifToRequestorReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
			idStageRelated = adminReviewDto.getIdStageRelated();
			idPerson = adminReviewDto.getIdPerson();
			prefillDto.setAdminReviewDto(adminReviewDto);

			//CSEC34D
			PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
					ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
			if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(personAddressDto)) {
				prefillDto.setPersonAddressDto(personAddressDto);
			}

			// CSEC35D
			prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));


			// CSEC53D
			PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notifToRequestorReq.getIdStage());
			if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
				prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
				idCaseworker = primaryWorkerDto.getIdPerson();
			}

			// CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
			if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
				if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
						|| (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {
					employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
					employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());
				}
				employeePersPhNameDto.setDtEmpTermination(currDate);
				prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
			}

			// CINT21D
			StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
			StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
			stageRtrvInDto.setUlIdStage(idStageRelated);
			caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
			if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
				prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
				dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
			}

			// CLSC65D call 1
			prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(notifToRequestorReq.getIdStage(),
					dtStageCloseDate, ServiceConstants.Y));

			if (!CodesConstant.CARVWRES_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
				// CLSC65D call 2
				prefillDto.setStageReviewNoDto(notifToParentEngDao
						.getStageReviewed(notifToRequestorReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
			}

		}


		CommonFormRes commonFormRes = new CommonFormRes();
		NotifToRequestorPrefillData notifToRequestorPrefillData = new NotifToRequestorPrefillData();
		PreFillDataServiceDto preFillDataServiceDto = notifToRequestorPrefillData.returnPrefillData(prefillDto);
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
		return commonFormRes;

	}
}
