/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:12:07 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.digitalpicture.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.PictureDetail;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DigitalPictureReq;
import us.tx.state.dfps.service.common.response.DigitalPictureRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.digitalpicture.dao.DigitalPictureDao;
import us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.workload.dto.ExternalDocumentDetailDto;
import us.tx.state.dfps.web.casemanagement.bean.PictureDetailBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 2, 2017- 5:12:07 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public class DigitalPictureServiceImpl implements DigitalPictureService {

	private static final Logger log = Logger.getLogger(DigitalPictureServiceImpl.class);

	@Autowired
	DigitalPictureDao digitalPictureDao;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public DigitalPictureRes addDigitalPicture(DigitalPictureReq digitalPictureReq){
		DigitalPictureRes digitalPictureRes = new DigitalPictureRes();

		PictureDetailBean bean = digitalPictureReq.getPictureDetailBean();
		java.util.Date today = new java.util.Date(System.currentTimeMillis());
		bean.setDtLastUpdate(today);

		// SimpleDateFormat format = new
		// SimpleDateFormat(ServiceConstants.UTIL_DATE_FORMAT);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		String time = bean.getTmStrPicTaken();

		if (StringUtils.isBlank(bean.getTmStrAmPmPicTaken()))
			bean.setTmStrAmPmPicTaken(ServiceConstants.AM);
		if (StringUtils.isBlank(time))
			bean.setTmStrPicTaken(ServiceConstants.UTIL_TIME);
		else
			bean.setTmStrPicTaken(time + ":01");
		Date dtPictureTaken = null;
		PictureDetail pictureDetail= null;
		try {
			dtPictureTaken = format
					.parse(bean.getDtStrPicTaken() + " " + bean.getTmStrPicTaken() + " " + bean.getTmStrAmPmPicTaken());
		
		bean.setDtPictureTaken(dtPictureTaken);
		digitalPictureReq.setPictureDetailBean(bean);

		pictureDetail = digitalPictureDao.addDigitalPicture(digitalPictureReq);
		} catch (ParseException e) {
			throw new ServiceLayerException(e.getMessage());
		}
		log.info("pictureDetail: " + pictureDetail);
		digitalPictureRes.setTransactionId(digitalPictureReq.getTransactionId());

		digitalPictureRes.setPictDetailId(pictureDetail.getIdPictureDetail());
		return digitalPictureRes;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public DigitalPictureRes getExtDocDetail(int idExtDocDetail) {
		DigitalPictureRes digitalPictureRes = new DigitalPictureRes();
		ExternalDocumentDetailDto externalDocumentDetailDto = null;

		externalDocumentDetailDto = digitalPictureDao.getExtDocDetail(idExtDocDetail);
		digitalPictureRes.setExternalDocumentDetailDto(externalDocumentDetailDto);

		return digitalPictureRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService#
	 * updateDigitalPicture(us.tx.state.dfps.service.common.request.
	 * DigitalPictureReq)
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public DigitalPictureRes updateDigitalPicture(DigitalPictureReq digitalPictureReq){

		DigitalPictureRes digitalPictureRes = new DigitalPictureRes();
		PictureDetailBean bean = digitalPictureReq.getPictureDetailBean();

		java.util.Date today = new java.util.Date(System.currentTimeMillis());
		bean.setDtLastUpdate(today);

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		String time = bean.getTmStrPicTaken();

		if (StringUtils.isBlank(bean.getTmStrAmPmPicTaken()))
			bean.setTmStrAmPmPicTaken(ServiceConstants.AM);
		if (StringUtils.isBlank(time))
			bean.setTmStrPicTaken(ServiceConstants.UTIL_TIME);
		else
			bean.setTmStrPicTaken(time + ":01");
		if (ObjectUtils.isEmpty(bean.getDtStrPicTaken()))
			bean.setDtStrPicTaken(DateUtils.dateString(today));

		Date dtPictureTaken = null;
		try {
			dtPictureTaken = format
					.parse(bean.getDtStrPicTaken() + " " + bean.getTmStrPicTaken() + " " + bean.getTmStrAmPmPicTaken());
		} catch (ParseException e) {
			throw new ServiceLayerException(e.getMessage());
		}
		bean.setDtPictureTaken(dtPictureTaken);

		digitalPictureReq.setPictureDetailBean(bean);

		PictureDetail pictureDetail = digitalPictureDao.updateDigitalPicture(digitalPictureReq);
		log.info("pictureDetail: " + pictureDetail);

		digitalPictureRes.setTransactionId(digitalPictureReq.getTransactionId());
		digitalPictureRes.setPictDetailId(pictureDetail.getIdPictureDetail());
		return digitalPictureRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService#
	 * checkAndUpdateExternalDocumentation(us.tx.state.dfps.service.common.
	 * request.DigitalPictureReq)
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public DigitalPictureRes checkAndUpdateExternalDocumentation(DigitalPictureReq digitalPictureReq) {
		DigitalPictureRes digitalPictureRes = new DigitalPictureRes();

		boolean checkAndUpdateExternalDocumentationStatus = digitalPictureDao
				.checkAndUpdateExternalDocumentation(digitalPictureReq);

		digitalPictureRes.setTransactionId(digitalPictureReq.getTransactionId());
		digitalPictureRes.setCheckAndUpdateExternalDocumentationStatus(checkAndUpdateExternalDocumentationStatus);

		return digitalPictureRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService#
	 * getDigitalPictureDetails(digitalPictureReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public DigitalPictureRes getDigitalPictureDetails(DigitalPictureReq digitalPictureReq) {
		DigitalPictureRes digitalPictureRes = new DigitalPictureRes();
		PictureDetailBean digitalPictureDetails = digitalPictureDao.getDigitalPictureDetails(
				digitalPictureReq.getPictureDetailBean().getIdPictureDetail(), digitalPictureReq.getUserId());

		if (!ObjectUtils.isEmpty(digitalPictureDetails)) {

			Date dtPictureTaken = digitalPictureDetails.getDtPictureTaken();

			SimpleDateFormat dtFormatter = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
			digitalPictureDetails.setDtStrPicTaken(dtFormatter.format(dtPictureTaken));
		}
		digitalPictureRes.setPictureDetailBean(digitalPictureDetails);
		return digitalPictureRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService#
	 * getDigitalPictureList(int)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public DigitalPictureRes getDigitalPictureList(int extDocmnttnID) {
		DigitalPictureRes digitalPictureRes = new DigitalPictureRes();
		List<PictureDetailBean> digitalPictureList = digitalPictureDao.getDigitalPictureList(extDocmnttnID);

		digitalPictureList.stream().forEach(bean -> {

			Date dtPictureTaken = bean.getDtPictureTaken();

			SimpleDateFormat dtFormatter = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
			SimpleDateFormat tmFormatter = new SimpleDateFormat(ServiceConstants.TIME_MIN_FORMAT);
			SimpleDateFormat tmSecFormatter = new SimpleDateFormat(ServiceConstants.TIME_SEC_FORMAT);
			SimpleDateFormat apFormatter = new SimpleDateFormat(ServiceConstants.TIME_AM);

			tmSecFormatter.format(dtPictureTaken);
			bean.setDtStrPicTaken(dtFormatter.format(dtPictureTaken));
			// determine if the time was entered
			/* if (!"12:00:00".equals(tmSecStr)) { */
			bean.setTmStrPicTaken(tmFormatter.format(dtPictureTaken));
			bean.setTmStrAmPmPicTaken(apFormatter.format(dtPictureTaken));
			/* } */

		});

		digitalPictureRes.setPictureDetailBeanList(digitalPictureList);
		return digitalPictureRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.digitalpicture.service.DigitalPictureService#
	 * getPictureDetailById(int)
	 */
	@Transactional
	public PictureDetail getPictureDetailById(int idPictureDetail) {
		return digitalPictureDao.getPictureDetailById(idPictureDetail);
	}

}
