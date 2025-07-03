package us.tx.state.dfps.service.person.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.EducListDtlSaveiDto;
import us.tx.state.dfps.service.common.request.EducationHistoryReq;
import us.tx.state.dfps.service.common.response.EducListDtlSaveoDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dao.EducationHistoryDao;
import us.tx.state.dfps.service.person.dao.EducationalHistoryDao;
import us.tx.state.dfps.service.person.dto.EducHistorydiDto;
import us.tx.state.dfps.service.person.dto.EducHistorydoDto;
import us.tx.state.dfps.service.person.dto.EducNeedediDto;
import us.tx.state.dfps.service.person.dto.EducNeededoDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.service.EducListDtlSaveService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION class name:
 * EducListDtlSaveServiceImpl description : ccfc18s tux service conversion save
 * update and delete Educational_history and educational_needed tables March,
 * 2018- © 2017 Texas Department of Family and Protective Services
 *
 * 
 */
@Service
@Transactional
public class EducListDtlSaveServiceImpl implements EducListDtlSaveService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EducationalHistoryDao educationalhistoryDao;

	@Autowired
	EducationHistoryDao educationHistoryDao;

	@Autowired
	AlertService alertService;

	public static final String IN_STATE = "I";

	private static final Logger log = Logger.getLogger(EducListDtlSaveServiceImpl.class);

	/**
	 * Method name: saveEducationalDetail Method description : use to modify
	 * educational Resources ( save update and delete) on Educational_history
	 * and educational_needed tables
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EducListDtlSaveoDto saveEducationalDetail(EducListDtlSaveiDto educListDtlSaveiDto) {
		log.debug("Entering method saveEducationalDetail in EducListDtlSaveServiceImpl");
		EducListDtlSaveoDto educListDtlSaveoDto = new EducListDtlSaveoDto();
		EducHistorydiDto educHistorydiDto = new EducHistorydiDto();
		EducHistorydoDto educHistorydoDto = new EducHistorydoDto();

		// get Last saved educational history detail by educational history Id
		EducationHistoryReq educationHistoryReq = new EducationHistoryReq();
		educationHistoryReq.setIdPerson(educListDtlSaveiDto.getEducationHistoryDto().getIdPerson());
		List<EducationHistoryDto> eduHistList = educationHistoryDao.getPersonEducationHistoryList(educationHistoryReq);

		// when Not enroll date is present, then check the last saved Edu Hist
		// to set the enrolled date, enrolled grade, withdrawn date,
		// grade and comments; this is done to persist the previous state of the
		// disabled fields
		/*
		 * if ( !TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getDtEdHistNotEnrollDate())) { if
		 * (TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getDtEdHistEnrollDate())) {
		 * educHistorydiDto.setDtEdHistEnrollDate(prevEduHist.
		 * getDtEdHistEnrollDate()); } else { educHistorydiDto
		 * .setDtEdHistEnrollDate(educListDtlSaveiDto.getEducationHistoryDto().
		 * getDtEdHistEnrollDate()); } if
		 * (TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getCdEdHistEnrollGrade())) {
		 * educHistorydiDto.setCdEdHistEnrollGrade(prevEduHist.
		 * getCdEdHistEnrollGrade()); } else { educHistorydiDto
		 * .setCdEdHistEnrollGrade(educListDtlSaveiDto.getEducationHistoryDto().
		 * getCdEdHistEnrollGrade()); } if
		 * (TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getDtEdHistWithdrawn())) {
		 * educHistorydiDto.setDtEdHistWithdrawnDate(prevEduHist.
		 * getDtEdHistWithdrawn()); } else { educHistorydiDto
		 * .setDtEdHistWithdrawnDate(educListDtlSaveiDto.getEducationHistoryDto(
		 * ).getDtEdHistWithdrawn()); } if
		 * (TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getCdEdHistWithdrawnGrade())) {
		 * educHistorydiDto.setCdEdHistWithdrawnGrade(prevEduHist.
		 * getCdEdHistWithdrawnGrade()); } else {
		 * educHistorydiDto.setCdEdHistWithdrawnGrade(
		 * educListDtlSaveiDto.getEducationHistoryDto().
		 * getCdEdHistWithdrawnGrade()); } if
		 * (TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getTxtWithdrawnComments())) {
		 * educHistorydiDto.setTxtWithdrawnComments(prevEduHist.
		 * getTxtWithdrawnComments()); } else {
		 * educHistorydiDto.setTxtWithdrawnComments(
		 * educListDtlSaveiDto.getEducationHistoryDto().getTxtWithdrawnComments(
		 * )); } if (TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.
		 * getEducationHistoryDto().getNmEdHistSchool())) {
		 * if(TypeConvUtil.isNullOrEmpty(prevEduHist.getNmEdHistSchool())){
		 * educHistorydiDto.setNmEdHistSchool(ServiceConstants.BLANK_NAME);
		 * }else{
		 * educHistorydiDto.setNmEdHistSchool(prevEduHist.getNmEdHistSchool());
		 * } } else { educHistorydiDto
		 * .setNmEdHistSchool(educListDtlSaveiDto.getEducationHistoryDto().
		 * getNmEdHistSchool()); } } else {
		 */
		if (null != educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistEnrollDate()) {
			educHistorydiDto
					.setDtEdHistEnrollDate(educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistEnrollDate());
		}
		educHistorydiDto.setCdEdHistEnrollGrade(educListDtlSaveiDto.getEducationHistoryDto().getCdEdHistEnrollGrade());
		if (null != educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistWithdrawn()) {

			educHistorydiDto
					.setDtEdHistWithdrawnDate(educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistWithdrawn());
		}
		educHistorydiDto
				.setCdEdHistWithdrawnGrade(educListDtlSaveiDto.getEducationHistoryDto().getCdEdHistWithdrawnGrade());

		if (educListDtlSaveiDto.getEducationHistoryDto().getTxtWithdrawnComments() != null) {
			educHistorydiDto
					.setTxtWithdrawnComments(educListDtlSaveiDto.getEducationHistoryDto().getTxtWithdrawnComments());
		}
		educHistorydiDto.setNmEdHistSchool(educListDtlSaveiDto.getEducationHistoryDto().getNmEdHistSchool());
		// }
		educHistorydiDto.setReqFuncCd(educListDtlSaveiDto.getEducationHistoryDto().getCdScrDataAction());
		educHistorydiDto.setIndEdHistTeaSchool(educListDtlSaveiDto.getEducationHistoryDto().getIndEdHistTeaSchool());

		// check if the school is out of state school and populate detail
		// if
		// (!IN_STATE.equalsIgnoreCase(educListDtlSaveiDto.getEducationHistoryDto().getIndEdHistTeaSchool()))
		// {
		educHistorydiDto.setAddrEdHistCity(educListDtlSaveiDto.getEducationHistoryDto().getAddrEdHistCity());
		educHistorydiDto.setAddrEdHistCnty(educListDtlSaveiDto.getEducationHistoryDto().getAddrEdHistCnty());
		educHistorydiDto.setAddrEdHistState(educListDtlSaveiDto.getEducationHistoryDto().getAddrEdHistState());
		educHistorydiDto.setAddrEdHistStreetLn1(educListDtlSaveiDto.getEducationHistoryDto().getAddrEdHistStreetLn1());
		educHistorydiDto.setAddrEdHistStreetLn2(educListDtlSaveiDto.getEducationHistoryDto().getAddrEdHistStreetLn2());
		educHistorydiDto.setAddrEdHistZip(educListDtlSaveiDto.getEducationHistoryDto().getAddrEdHistZip());
		educHistorydiDto.setEdHistPhone(educListDtlSaveiDto.getEducationHistoryDto().getEdHistPhone());
		educHistorydiDto.setEdHistPhoneExt(educListDtlSaveiDto.getEducationHistoryDto().getEdHistPhoneExt());
		// }

		// populate the educational detail information
		educHistorydiDto.setEdHistAddrCmnt(educListDtlSaveiDto.getEducationHistoryDto().getEdHistAddrCmnt());

		// educHistorydiDto.setNmEdHistSchool(educListDtlSaveiDto.getEducationHistoryDto().getNmEdHistSchool());
		educHistorydiDto.setNmEdHistSchDist(educListDtlSaveiDto.getEducationHistoryDto().getNmEdHistSchDist());
		educHistorydiDto.getIdEdHist(educListDtlSaveiDto.getEducationHistoryDto().getIdEdHist());

		if (null != educListDtlSaveiDto.getEducationHistoryDto().getIdResource())
			educHistorydiDto.getIdResource(educListDtlSaveiDto.getEducationHistoryDto().getIdResource());

		educHistorydiDto.getIdPerson(educListDtlSaveiDto.getIdPerson());
		educHistorydiDto.setSpecialAccmdtns(educListDtlSaveiDto.getEducationHistoryDto().getSpecialAccmdtns());

		// part of ADS Changes
		/*
		 * educHistorydiDto
		 * .setTxtNotEnrollComments(educListDtlSaveiDto.getEducationHistoryDto()
		 * .getTxtNotEnrollComments());
		 */ educHistorydiDto.setReqFuncCd(educListDtlSaveiDto.getReqFuncCd());
		educHistorydiDto.setTsLastUpdate(educListDtlSaveiDto.getEducationHistoryDto().getDtLastUpdate());

		/*
		 * if (null !=
		 * educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistNotEnrollDate
		 * ()) { educHistorydiDto
		 * .setDtEdHistNotEnrollDate(educListDtlSaveiDto.getEducationHistoryDto(
		 * ).getDtEdHistNotEnrollDate());
		 * 
		 * }
		 */

		if (null != educListDtlSaveiDto.getEducationHistoryDto().getDtLastArdiep())
			educHistorydiDto.setDtLastArdiep(educListDtlSaveiDto.getEducationHistoryDto().getDtLastArdiep());

		// if external user create new educational detail recrod and click on
		// save
		if (!TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.getEducationHistoryDto().getIdExternalUser())
				&& educListDtlSaveiDto.getEducationHistoryDto().getIdExternalUser() != 0L) {
			educHistorydiDto.setIdExtUser(educListDtlSaveiDto.getEducationHistoryDto().getIdExternalUser());
		}
         //added for artf227808
		educHistorydiDto.setCdEdHistCompletedGrade(educListDtlSaveiDto.getEducationHistoryDto().getCdEdHistCompletedGrade());
		educHistorydiDto.setDtEdHistCompleted(educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistCompleted());
		// added for artf227808
		// check the operation if the operation is delete
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(educListDtlSaveiDto.getReqFuncCd())) {
			saveEducationNeed_aud(educListDtlSaveiDto, ServiceConstants.REQ_FUNC_CD_DELETE, educListDtlSaveoDto);
		}
		// call dao method to execute the operation on the history table
		ServiceResHeaderDto serviceResHeaderDto = educationalhistoryDao.saveEducationalDetail(educHistorydiDto,
				educHistorydoDto);
		if (null != serviceResHeaderDto && null != serviceResHeaderDto.getErrorDto()) {
			educListDtlSaveoDto.setErrorDto(serviceResHeaderDto.getErrorDto());
		} else {
			// check if external user and Add/update education details then
			// create alerts for DFPS user
			if (!TypeConvUtil.isNullOrEmpty(educListDtlSaveiDto.getEducationHistoryDto().getIdExternalUser())
					&& educListDtlSaveiDto.getEducationHistoryDto().getIdExternalUser() != 0L) {

				alertService.createAlert(educListDtlSaveiDto.getIdStage(), educListDtlSaveiDto.getIdPersonAssigned(),
						educListDtlSaveiDto.getIdPerson(), educListDtlSaveiDto.getIdCase(),
						ServiceConstants.EDUCATION_UPDATE, new Date());
			}
		}
		if (educHistorydoDto != null) {

			if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(educListDtlSaveiDto.getReqFuncCd())) {
				saveEducationNeed_aud(educListDtlSaveiDto, ServiceConstants.REQ_FUNC_CD_DELETE, educListDtlSaveoDto);
				saveEducationNeed_aud(educListDtlSaveiDto, ServiceConstants.REQ_FUNC_CD_ADD, educListDtlSaveoDto);
			}

			if (0l == educListDtlSaveiDto.getEducationHistoryDto().getIdEdHist()) {
				educListDtlSaveiDto.getEducationHistoryDto().setIdEdHist(educHistorydoDto.getUlIdEdhist());
			}
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(educListDtlSaveiDto.getReqFuncCd())) {
				saveEducationNeed_aud(educListDtlSaveiDto, ServiceConstants.REQ_FUNC_CD_ADD, educListDtlSaveoDto);
			}
		}
		//artf257790 : stage id is not available when we come to this page from person search.
		if(educListDtlSaveiDto.getIdStage() != null) {
			//For APS program comments field is not displaying in Education details screen, we don't need to check for error code
			String cdStageProgram = educationalhistoryDao.getStageProgramByPersonId(educListDtlSaveiDto.getIdPerson(), educListDtlSaveiDto.getIdStage());
			if (!ServiceConstants.STAGE_PROGRAM_APS.equals(cdStageProgram) && educListDtlSaveiDto.getEducationHistoryDto().getTxtWithdrawnComments() == null
					&& educListDtlSaveiDto.getEducationHistoryDto().getDtEdHistWithdrawn() != null) {
				educListDtlSaveoDto.getErrorDto().setErrorCode(56994);
			}
		}

		log.debug("Exiting method callEducListDtlSaveService in EducListDtlSaveServiceImpl");
		return educListDtlSaveoDto;
	}

	/**
	 * Method name: saveEducationNeed_aud Description: service method for save
	 * new rec
	 * 
	 * @param educListDtlSaveiDto
	 * @param iIndex
	 * @param addOrDelete
	 * @param educListDtlSaveoDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveEducationNeed_aud(EducListDtlSaveiDto educListDtlSaveiDto, String addOrDelete,
			EducListDtlSaveoDto educListDtlSaveoDto) {
		log.debug("Entering method modifyEducationNeed in EducListDtlSaveServiceImpl");
		EducNeedediDto educNeedediDto = new EducNeedediDto();
		EducNeededoDto educNeededoDto = new EducNeededoDto();
		int iAddDelete = 0;
		String[] tempEdNeed = null;
		educNeedediDto.setReqFuncCd(addOrDelete);
		educNeedediDto.setIdEdHist(educListDtlSaveiDto.getEducationHistoryDto().getIdEdHist());
		educNeedediDto.setIdPerson(educListDtlSaveiDto.getIdPerson());
		educNeedediDto.setTsLastUpdate(educListDtlSaveiDto.getEducationHistoryDto().getDtLastUpdate());

		if (null != educListDtlSaveiDto.getEducationHistoryDto().getCdEducationalNeed())
			iAddDelete = educListDtlSaveiDto.getEducationHistoryDto().getCdEducationalNeed().length();
		if (null != educListDtlSaveiDto.getEducationHistoryDto().getCdEducationalNeed())
			tempEdNeed = educListDtlSaveiDto.getEducationHistoryDto().getCdEducationalNeed().split("(?<=\\G...)");
		// for delete
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(educNeedediDto.getReqFuncCd())) {
			educationalhistoryDao.saveEducationNeed(educNeedediDto, educNeededoDto);
		}
		// end of delete

		for (int i = 0; i < (tempEdNeed != null ? tempEdNeed.length : 0) && educListDtlSaveoDto != null; i++) {
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(educNeedediDto.getReqFuncCd()) && 0 == iAddDelete)
				break;
			educNeedediDto.setCdEducationalNeed(tempEdNeed[i]);
			educationalhistoryDao.saveEducationNeed(educNeedediDto, educNeededoDto);

			if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(educNeedediDto.getReqFuncCd()))
				break;
		}

		log.debug("Exiting method modifyEducationNeed in EducListDtlSaveServiceImpl");
	}

}
