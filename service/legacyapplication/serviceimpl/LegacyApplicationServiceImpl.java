package us.tx.state.dfps.service.legacyapplication.serviceimpl;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.EligibilityUtil;
import us.tx.state.dfps.service.common.utils.FceUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.LegacyApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceContextDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.legacyapplication.service.LegacyApplicationService;

@Service
@Transactional
public class LegacyApplicationServiceImpl implements LegacyApplicationService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FceService fceService;

	@Autowired
	EligibilityUtil eligibilityUtil;

	@Autowired
	EventDao eventDao;

	@Autowired
	FceUtil fceUtil;

	private static final Logger log = Logger.getLogger(LegacyApplicationServiceImpl.class);

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public LegacyApplicationDto read(Long idStage, Long idEvent, Long idLastUpdatePerson) {

		LegacyApplicationDto legacyAppDto = new LegacyApplicationDto();
		FceContextDto fceContext = new FceContextDto();
		log.info("inside LegacyApplicationServiceImpl and starting initializeFceReview");
		fceContext = fceService.initializeFceReview(idStage, idEvent, idLastUpdatePerson);
		long idFceApplication = 0;

		if (!TypeConvUtil.isNullOrEmpty(fceContext)) {
			if (!TypeConvUtil.isNullOrEmpty(fceContext.getIdFceApplication())) {
				idFceApplication = fceContext.getIdFceApplication();
			}
			if (!TypeConvUtil.isNullOrEmpty(fceContext.getIdEvent())) {
				legacyAppDto.setIdEvent(fceContext.getIdEvent());
			}

			if (!TypeConvUtil.isNullOrEmpty(fceContext.getIdEvent())) {
				legacyAppDto.setCdEventStatus(eventDao.getEventStatus(fceContext.getIdEvent()));
			}

		}

		FceEligibilityDto applicationFceEligibility = eligibilityUtil
				.findEligibilityByIdFceApplication(idFceApplication);

		BeanUtils.copyProperties(applicationFceEligibility, legacyAppDto);
		return legacyAppDto;

	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long save(LegacyApplicationDto legacyApplicationDto) {
		Long updateResult = 0L;
		log.debug("Entering method save in LegacyApplicationService");
		{
			updateResult++;
			fceUtil.verifyCanSave(legacyApplicationDto, null);

			FceEligibilityDto fceEligibilityDB = new FceEligibilityDto();

			BeanUtils.copyProperties(legacyApplicationDto, fceEligibilityDB);

			try {
				eligibilityUtil.saveEligibility(fceEligibilityDB);
			} catch (IllegalAccessException e) {
				throw new ServiceLayerException(e.getMessage());
			} catch (InvocationTargetException e) {
				throw new ServiceLayerException(e.getMessage());
			}

		}
		log.debug("Exiting method save in LegacyApplicationService");
		return updateResult;
	}

}
