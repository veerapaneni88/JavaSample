package us.tx.state.dfps.service.pal.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.PalFollowUpReq;
import us.tx.state.dfps.service.common.response.PalFollowUpRes;
import us.tx.state.dfps.service.pal.dao.PalFollowUpDao;
import us.tx.state.dfps.service.pal.service.PalFollowUpService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Implementation for PalFollowUpService. Oct 9, 2017- 11:26:25 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PalFollowUpServiceImpl implements PalFollowUpService {

	@Autowired
	private PalFollowUpDao palFollowUpDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PalFollowUpServiceLog");

	/**
	 * Method Name: retrievePal Method Description: Retrieves the Pal record
	 * with to new fields CD_NO_ILS_REASON and DT_TRAINING_CMPLTD using
	 * ID_PAL_STAGE from database.
	 * 
	 * @param followUpBean
	 * @return PalFollowUpRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PalFollowUpRes retrievePal(PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method retrievePal in PalFollowUpService");
		PalFollowUpRes palFollowUpRes = new PalFollowUpRes();
		palFollowUpRes.setPalFollowUpDto(palFollowUpDao.selectPal(palFollowUpReq.getPalFollowUpDto()));

		LOG.debug("Exiting method retrievePal in PalFollowUpService");
		return palFollowUpRes;
	}

	/**
	 * Method Name: retrievePalFollowUp Method Description: Retrieves the Pal
	 * Follow Up record set details from the database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PalFollowUpRes retrievePalFollowUp(PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method retrievePalFollowUp in PalFollowUpService");

		PalFollowUpRes palFollowUpRes = new PalFollowUpRes();
		palFollowUpRes.setPalFollowUpDto(palFollowUpDao.selectPalFollowUp(palFollowUpReq.getPalFollowUpDto()));

		LOG.debug("Exiting method retrievePalFollowUp in PalFollowUpService");
		return palFollowUpRes;
	}

	/**
	 * Method Name: insertPalFollowUp Method Description: Inserts the Pal Follow
	 * Up records into the database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PalFollowUpRes insertPalFollowUp(PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method insertPalFollowUp in PalFollowUpService");

		PalFollowUpRes palFollowUpRes = new PalFollowUpRes();
		palFollowUpRes.setPalFollowUpDto(palFollowUpDao.insertPalFollowUp(palFollowUpReq.getPalFollowUpDto()));

		LOG.debug("Exiting method insertPalFollowUp in PalFollowUpService");
		return palFollowUpRes;
	}

	/**
	 * Method Name: updatePal Method Description: Update the Pal table.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PalFollowUpRes updatePal(PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method updatePal in PalFollowUpService");

		// Insert a follow up record
		PalFollowUpRes palFollowUpRes = new PalFollowUpRes();
		palFollowUpRes.setPalFollowUpDto(palFollowUpDao.updatePal(palFollowUpReq.getPalFollowUpDto()));

		LOG.debug("Exiting method updatePal in PalFollowUpService");
		return palFollowUpRes;
	}

}
