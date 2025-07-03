package us.tx.state.dfps.service.arsafetyassmt.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.arsafetyassmt.dao.ARSafetyAssmtDao;
import us.tx.state.dfps.service.arsafetyassmt.service.ARSafetyAssmtService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ARClosureSafetyAssmtReq;
import us.tx.state.dfps.service.common.response.ARClosureSafetyAssmtRes;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ARSafetyAssmtServiceImpl Sep 20, 2017- 9:47:04 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class ARSafetyAssmtServiceImpl implements ARSafetyAssmtService {
	@Autowired
	ARSafetyAssmtDao arSafetyAssmtDao;

	@Autowired
	EventService eventService;

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getARSafetyAssmt Method Description:fetches the ARSafety
	 * Assessment data from the database.
	 * 
	 * @param ARClosureSafetyAssmtReq
	 * @return ARClosureSafetyAssmtRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ARClosureSafetyAssmtRes getARSafetyAssmt(ARClosureSafetyAssmtReq arSafetyAssmtReq) {

		ARSafetyAssmtValueDto arSafetyAssmtValueDto = new ARSafetyAssmtValueDto();
		ARClosureSafetyAssmtRes arClosureSafetyAssmtRes = new ARClosureSafetyAssmtRes();

		// Fetch the Safety Assessment Dto
		arSafetyAssmtValueDto = arSafetyAssmtDao.getARSafetyAssmt(arSafetyAssmtReq.getIdStage(),
				arSafetyAssmtReq.getIndAssmtType(), arSafetyAssmtReq.getIdUser());

		if (!ObjectUtils.isEmpty(arSafetyAssmtValueDto)) {
			// Retrieve the Event Details and Set to Safety Assessment Dto
			EventDto eventDto = eventService.getEvent(arSafetyAssmtValueDto.getIdEvent().longValue());

			if (!ObjectUtils.isEmpty(eventDto)) {
				EventValueDto eventValueDto = new EventValueDto();
				eventValueDto.setIdEvent(eventDto.getIdEvent());
				eventValueDto.setIdApproval(eventDto.getIdApproval());
				eventValueDto.setIdCase(eventDto.getIdCase());
				eventValueDto.setIdStage(eventDto.getIdStage());
				eventValueDto.setDtLastUpdate(eventDto.getDtLastUpdate());
				eventValueDto.setCdEventStatus(eventDto.getCdEventStatus());
				eventValueDto.setCdEventTask(eventDto.getCdTask());
				eventValueDto.setCdEventType(eventDto.getCdEventType());
				eventValueDto.setEventDescr(eventDto.getEventDescr());
				eventValueDto.setDtEventOccurred(eventDto.getDtEventOccurred());
				eventValueDto.setCdStage(eventDto.getCdStage());
				eventValueDto.setDtEventModified(eventDto.getDtEventModified());
				eventValueDto.setDtEventCreated(eventDto.getDtEventCreated());
				arSafetyAssmtValueDto.setSafetyAssmtEvent(eventValueDto);

			}
		} else if (ObjectUtils.isEmpty(arSafetyAssmtValueDto)) {
			arSafetyAssmtValueDto = new ARSafetyAssmtValueDto();
			arSafetyAssmtValueDto.setIdStage(arSafetyAssmtReq.getIdStage());
			arSafetyAssmtValueDto.setIndAssmtType(arSafetyAssmtReq.getIndAssmtType());
			arSafetyAssmtValueDto.setIdUser(arSafetyAssmtReq.getIdUser());

		}

		// Fetch the Areas
		List<ARSafetyAssmtAreaValueDto> arSafetyAssmtAreaValueDtoList = new ArrayList<ARSafetyAssmtAreaValueDto>();
		arSafetyAssmtAreaValueDtoList = arSafetyAssmtDao.getARSafetyAssmtAreas(arSafetyAssmtReq.getIndAssmtType(),
				arSafetyAssmtReq.getIdStage());
		arSafetyAssmtValueDto.setaRSafetyAssmtAreas(arSafetyAssmtAreaValueDtoList);

		List<ARSafetyAssmtFactorValueDto> arSafetyAssmtFactorValueDtoList = new ArrayList<ARSafetyAssmtFactorValueDto>();

		if (!ObjectUtils.isEmpty(arSafetyAssmtAreaValueDtoList)) {
			for (ARSafetyAssmtAreaValueDto arSafetyAssmtAreaValueDto : arSafetyAssmtAreaValueDtoList) {
				// Fetch the Factors
				arSafetyAssmtFactorValueDtoList = arSafetyAssmtDao.getARSafetyAssmtFactors(
						arSafetyAssmtAreaValueDto.getIdArea(), arSafetyAssmtValueDto.getIdArSafetyAssmt());

				arSafetyAssmtAreaValueDto.setaRSafetyAssmtFactors(arSafetyAssmtFactorValueDtoList);

				if (!ObjectUtils.isEmpty(arSafetyAssmtFactorValueDtoList)) {
					for (ARSafetyAssmtFactorValueDto arSafetyAssmtFactorValueDto : arSafetyAssmtFactorValueDtoList) {

						String rbrows = "";
						if (!ObjectUtils.isEmpty(arSafetyAssmtFactorValueDto.getResponse()))
							rbrows = String.valueOf(arSafetyAssmtFactorValueDto.getIdArea())
									.concat(String.valueOf(arSafetyAssmtFactorValueDto.getIdFactor())
											.concat(arSafetyAssmtFactorValueDto.getResponse()));
						else
							rbrows = String.valueOf(arSafetyAssmtFactorValueDto.getIdArea())
									.concat(String.valueOf(arSafetyAssmtFactorValueDto.getIdFactor()));

						arSafetyAssmtFactorValueDto.setRbrows(rbrows);

						/*
						 * arSafetyAssmtFactorValueDto.
						 * setaRSafetyAssmtFactorVals(arSafetyAssmtDao
						 * .getARSafetyAssmtFactorVals(
						 * arSafetyAssmtFactorValueDto.getIdFactor()));
						 */

						if (arSafetyAssmtFactorValueDto.getIndFactorType()
								.equals(ServiceConstants.TODO_ACTIONS_REMINDER)) {
							arSafetyAssmtFactorValueDto.setaRSafetyAssmtFactorVals(arSafetyAssmtDao
									.getARSafetyAssmtFactorVals(arSafetyAssmtFactorValueDto.getIdFactor()));

						}

					}
				}

			}
			arSafetyAssmtValueDto.setaRSafetyAssmtFactors(arSafetyAssmtFactorValueDtoList);
		}

		arClosureSafetyAssmtRes.setArSafetyAssmtValueDto(arSafetyAssmtValueDto);

		return arClosureSafetyAssmtRes;
	}

	/**
	 * Method Name: getARSafetyAssmtFactorValueBean Method Description:
	 * Retrieves ARSafetyAssmtFactorValueDto from database
	 * 
	 * @param idFactor
	 * @param idSafetyAssmt
	 * @return ARSafetyAssmtFactorValueDto
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ARClosureSafetyAssmtRes getARSafetyAssmtFactor(Integer idFactor, Integer idSafetyAssmt) {
		ARClosureSafetyAssmtRes arClosureSafetyAssmtRes = new ARClosureSafetyAssmtRes();
		ARSafetyAssmtValueDto arSafetyAssmtValueDto = new ARSafetyAssmtValueDto();
		List<ARSafetyAssmtFactorValueDto> aRSafetyAssmtFactorsList = new ArrayList<ARSafetyAssmtFactorValueDto>();

		aRSafetyAssmtFactorsList.add(arSafetyAssmtDao.getARSafetyAssmtFactor(idFactor, idSafetyAssmt));
		arSafetyAssmtValueDto.setaRSafetyAssmtFactors(aRSafetyAssmtFactorsList);
		arClosureSafetyAssmtRes.setArSafetyAssmtValueDto(arSafetyAssmtValueDto);

		return arClosureSafetyAssmtRes;
	}

}
