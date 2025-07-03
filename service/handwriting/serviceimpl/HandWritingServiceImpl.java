package us.tx.state.dfps.service.handwriting.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casepackage.dto.HWKeyEventDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.HandWrittenFieldViewedReq;
import us.tx.state.dfps.service.common.response.HandWritingRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.handwriting.dao.HandWritingDao;
import us.tx.state.dfps.service.handwriting.dto.HandWritingDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;
import us.tx.state.dfps.service.handwriting.service.HandWritingService;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * Service implementation for functions required for implementing handwriting
 * functionality
 *
 */
@Service
@Transactional
public class HandWritingServiceImpl implements HandWritingService {

	@Autowired
	HandWritingDao handWritingDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CaseUtils caseUtils;

	private final static String percentage = "%";

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes updateHandWrittenData(HandWritingDto handWritingDto) {

		HandWritingRes handWritingRes = new HandWritingRes();

		if (0 == handWritingDto.getIdEvent()) {

			Long idEvent = handWritingDao.getEventforContact(handWritingDto);

			if (null != idEvent) {
				handWritingDto.setIdEvent(idEvent.intValue());

				handWritingDto.setDataKey("ESTM_" + idEvent + "_" + handWritingDto.getTxtFieldName());

			}

		}

		handWritingRes.setUpdateResult(handWritingDao.updateHandWrittenData(handWritingDto));
		return handWritingRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes updateHandwritingKeyAndEventWithNew(HWKeyEventDto hwKeyEventDto) {

		HandWritingRes handWritingRes = new HandWritingRes();

		handWritingRes.setUpdateResult(handWritingDao.updateHandwritingKeyAndEventWithNew(hwKeyEventDto));

		return handWritingRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes updateHandwrittenFieldViewed(HandWrittenFieldViewedReq fieldViewedReq) {

		String idWrittenData = (!ObjectUtils.isEmpty(fieldViewedReq.getsFormKey())) ? fieldViewedReq.getsFormKey()
				: fieldViewedReq.getsFieldKey();
		idWrittenData = idWrittenData + percentage;
		HandWritingRes handWritingRes = new HandWritingRes();
		handWritingRes.setUpdateResult(handWritingDao.updateHandwrittenFieldViewed(idWrittenData));

		return handWritingRes;

	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes saveHandwrttenData(HandWritingDto handWritingDto) {

		HandWritingRes handWritingRes = new HandWritingRes();
		handWritingRes.setUpdateResult(handWritingDao.saveHandwrttenData(handWritingDto));
		return handWritingRes;

	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes updateHandwritingKeyWithNew(HWKeyEventDto hwKeyEventDto) {

		HandWritingRes handWritingRes = new HandWritingRes();

		handWritingRes.setUpdateResult(handWritingDao.updateHandwritingKeyWithNew(hwKeyEventDto));

		return handWritingRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes deleteHandwrittenData(HandWritingDto handWritingDto) {
		HandWritingRes handWritingRes = new HandWritingRes();

		handWritingRes.setUpdateResult(handWritingDao.deleteHandwrittenData(handWritingDto));

		return handWritingRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes deleteHandwrittenDataForEvent(HandWritingDto handWritingDto) {
		HandWritingRes handWritingRes = new HandWritingRes();

		handWritingRes.setUpdateResult(handWritingDao.deleteHandwrittenDataForEvent(handWritingDto));

		return handWritingRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public HandWritingRes deleteUsedHandwrittenData(String sKeyValue) {
		long noOfHandwrittenDatasDel = 0;
		HandWritingRes handWritingRes = new HandWritingRes();
		String[] handWritingList = sKeyValue.split(",");
		if (handWritingList.length > 0) {
			for (String string : handWritingList) {
				noOfHandwrittenDatasDel += handWritingDao.deleteUsedHandwrittenData(string);
			}
		}
		handWritingRes.setUpdateResult(noOfHandwrittenDatasDel);
		return handWritingRes;
	}

	/**
	 * Method Name: fetchHwFieldListForForm Method Description: This method
	 * fetches handwriting fields for a form. These fields are configured in a
	 * table HANDWRITING_FIELDS. For normal pages, we dont maintain the list of
	 * handwritable fields in the table. It is part of the page coding
	 * 
	 * This method fetches the handwritable fields and checks if handwritten
	 * data exist for that field, then loads information about handwritten data
	 * also
	 * 
	 * @param getsDocType
	 * @param eventId
	 * @return List<HandWritingValueDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<HandWritingValueDto> fetchHwFieldListForForm(String sDocType, Long eventId) {
		List<HandWritingValueDto> handwritingFieldList = new ArrayList<HandWritingValueDto>();
		List<HandWritingValueDto> handwrittenFieldList = new ArrayList<HandWritingValueDto>();
		if (TypeConvUtil.isNullOrEmpty(eventId)) {
			return handwritingFieldList;
		}
		HandWritingValueDto hwStageInfo = (HandWritingValueDto) handWritingDao.fetchEventStageInfo(eventId);
		handwritingFieldList = (List<HandWritingValueDto>) handWritingDao.fetchHandwritableFieldList(sDocType);
		handwrittenFieldList = (List<HandWritingValueDto>) handWritingDao.fetchHandwrittenDataForEvent(eventId,
				Boolean.TRUE);
		for (HandWritingValueDto handWritingValueDtos : handwritingFieldList) {
			handWritingValueDtos.setTxtKeyValue(sDocType + ServiceConstants.UNDERSCORE_SYMBOL + eventId.toString()
					+ ServiceConstants.UNDERSCORE_SYMBOL + handWritingValueDtos.getTxtFieldName());
			handWritingValueDtos.setIdEvent(eventId);
			for (HandWritingValueDto handWrittenValueDtos : handwrittenFieldList) {
				if (handWrittenValueDtos.getTxtKeyValue().equals(handWritingValueDtos.getTxtKeyValue())) {
					handWritingValueDtos.setIsConverted(handWrittenValueDtos.getIsConverted());
					handWritingValueDtos.setTxtTranslatedNotes(handWrittenValueDtos.getTxtTranslatedNotes());
				}
				if (handWrittenValueDtos.getTxtFieldName().equals(handWritingValueDtos.getTxtFieldName())
						&& (ServiceConstants.FAMILYPLANEVALFPR.equalsIgnoreCase(sDocType)
								|| ServiceConstants.FAMILYPLANFPR.equalsIgnoreCase(sDocType))) {
					if (ObjectUtils.isEmpty(handWrittenValueDtos.getIdFamilyPlanNarr())) {
						handWritingValueDtos.setTxtKeyValue(handWrittenValueDtos.getTxtKeyValue());
					}

				} else if (handWrittenValueDtos.getTxtFieldName().equals(handWritingValueDtos.getTxtFieldName())) {
					handWritingValueDtos.setTxtKeyValue(handWrittenValueDtos.getTxtKeyValue());
				}
			}
			if ((TypeConvUtil.isNullOrEmpty(handWritingValueDtos.getIdStage()))
					|| (handWritingValueDtos.getIdStage().intValue() == ServiceConstants.EVENTSTATUS_NEW_PRIORITY)) {
				if (!TypeConvUtil.isNullOrEmpty(hwStageInfo)) {
					handWritingValueDtos.setCdStage(hwStageInfo.getCdStage());
					handWritingValueDtos.setIdCase(hwStageInfo.getIdCase());
					handWritingValueDtos.setIdStage(hwStageInfo.getIdStage());
				}
			}
		}
		return handwritingFieldList;
	}


	/**
	 * Method Name: fetchHandwrittenDataForEvent Method Description: This method
	 * fetches handwritten data for an event
	 * 
	 * @param idEvent
	 * @param bfetchImage
	 * @return List<HandWritingValueDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<HandWritingValueDto> fetchHandwrittenDataForEvent(Long idEvent, Boolean bfetchImage) {
		List<HandWritingValueDto> handwrittenFieldList = new ArrayList<HandWritingValueDto>();
		handwrittenFieldList = (List<HandWritingValueDto>) handWritingDao.fetchHandwrittenDataForEvent(idEvent,
				bfetchImage);
		if (TypeConvUtil.isNullOrEmpty(handwrittenFieldList))
			handwrittenFieldList = new ArrayList<HandWritingValueDto>();
		return handwrittenFieldList;
	}

	/**
	 * Method Name: fetchHandwrittenDataForKey Method Description: This method
	 * fetches handwritten data for a key value
	 * 
	 * @param dataKey
	 * @param bfetchImage
	 * @return List<HandWritingValueDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<HandWritingValueDto> fetchHandwrittenDataForKey(String dataKey, Boolean bfetchImage) {
		List<HandWritingValueDto> handwrittenField = new ArrayList<HandWritingValueDto>();
		handwrittenField = (List<HandWritingValueDto>) handWritingDao.fetchHandwrittenDataForKey(dataKey, bfetchImage);
		return handwrittenField;
	}

	/**
	 * Method Name: fetchLinkedNotesForStages Method Description: This method is
	 * called at the time of stage Check-in when we want to check if handwritten
	 * notes linked to pages exist. So we fetch only notes which are linked to
	 * pages
	 * 
	 * @param stageList
	 * @return List<HandWritingValueDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public List<HandWritingValueDto> fetchLinkedNotesForStages(List<String> stageList) {
		List<HandWritingValueDto> handwrittenFieldList = new ArrayList<HandWritingValueDto>();
		handwrittenFieldList = (List<HandWritingValueDto>) handWritingDao.fetchLinkedNotesForStages(stageList);
		if (TypeConvUtil.isNullOrEmpty(handwrittenFieldList)) {
			handwrittenFieldList = new ArrayList<HandWritingValueDto>();
		}
		return handwrittenFieldList;
	}

	/**
	 * Method Name: fetchStageName Method Description: This method fetches the
	 * stage information
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public String fetchStageName(Long idStage) {
		return handWritingDao.fetchStageName(idStage);
	}

	/**
	 * Method Name: fetchStageInfo Method Description: This method fetches the
	 * stage information
	 * 
	 * @param idStage
	 * @return StageDBDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public StageDBDto fetchStageInfo(Long idStage) {
		return handWritingDao.fetchStageInfo(idStage);
	}

	/**
	 * Method Name: fetchCaseIdForStageId Method Description: This method
	 * fetches the Case Id for a Stage Id
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public Long fetchCaseIdForStageId(Long idStage) {
		return handWritingDao.fetchCaseIdForStageId(idStage);
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public void updateFamilyPlanNarrToSignatures(Long idEvent, Long idFamilyPlanNarr) {
		handWritingDao.updateFamilyPlanNarrToSignatures(idEvent, idFamilyPlanNarr);
	}
	
}