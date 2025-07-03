package us.tx.state.dfps.service.caretakerinfo.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.caretaker.dto.CaretkrInfoCaretakerDto;
import us.tx.state.dfps.service.caretaker.dto.CaretkrInfoResourceDto;
import us.tx.state.dfps.service.caretakerinfo.dao.CareTakerInfoDao;
import us.tx.state.dfps.service.caretakerinfo.service.CareTakerInfoService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CaretakerInformationReq;
import us.tx.state.dfps.service.common.response.CaretakerInformationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implement
 * the service to read, save and delete info for Caretaker Information page Feb
 * 8, 2018- 7:50:38 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class CareTakerInfoServiceImpl implements CareTakerInfoService {
	@Autowired
	MessageSource messageSource;

	@Autowired
	CareTakerInfoDao careTakerInfoDao;

	private static final Logger log = Logger.getLogger(CareTakerInfoServiceImpl.class);

	/**
	 * Method name: getCaretakerInfo Method description : Get the info about
	 * Caretaker Information page.
	 * 
	 * @param careTakerInfoReq
	 * @return CaretakerInformationRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaretakerInformationRes getCaretakerInfo(CaretakerInformationReq careTakerInfoReq) {
		log.debug("Entering method getCaretakerInfo in CareTakerInfoServiceImpl");

		CaretakerInformationRes careTakerInfoRes = new CaretakerInformationRes();

		// CRES55D -- get informtion for the Individual Caretakers
		List<CaretkrInfoCaretakerDto> caretkrInfoCaretakerDto = careTakerInfoDao
				.getCareTakerInfo(careTakerInfoReq.getIdResource());
		if (!ObjectUtils.isEmpty(caretkrInfoCaretakerDto)) {
			careTakerInfoRes.setCaretkrInfoCaretakerDtoList(caretkrInfoCaretakerDto);

		}
		// CRES57D -- get information for the CaretakerInformation.
		CaretkrInfoResourceDto caretkrInfoRsrcDto = careTakerInfoDao
				.getCareTakerInfoFromResource(careTakerInfoReq.getIdResource());
		if (!ObjectUtils.isEmpty(caretkrInfoRsrcDto)) {
			careTakerInfoRes.setCaretkrInfoResourceDto(caretkrInfoRsrcDto);
		}

		return careTakerInfoRes;
	}

	/**
	 * Method name: saveCareTakerInfo Method-Description : Save a row in the
	 * Caretakers table about CareTakerInformation Page.
	 * 
	 * @param careTakerInfoReq
	 * @ @returns CaretakerInformationRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaretakerInformationRes saveCareTakerInfo(CaretakerInformationReq careTakerInfoReq) {
		CaretakerInformationRes careTakerInfoRes = new CaretakerInformationRes();
		return careTakerInfoRes;
	}

	/**
	 * Method name: deleteCareTaker Method-Description : Delete a row in the
	 * Caretakers table on CareTakerInformation Page.
	 * 
	 * @param careTakerInfoReq.idCareTaker
	 *            required.
	 * @ @returns CaretakerInformationRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaretakerInformationRes deleteCareTaker(CaretakerInformationReq careTakerInfoReq) {
		CaretakerInformationRes careTakerInfoRes = new CaretakerInformationRes();
		careTakerInfoReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DEL);
		if (!TypeConvUtil.isNullOrEmpty(careTakerInfoReq)
				&& !TypeConvUtil.isNullOrEmpty(careTakerInfoReq.getIdCaretaker())) {
			CaretkrInfoCaretakerDto caretkrInfoCaretakerDto = careTakerInfoDao
					.deleteCareTaker(careTakerInfoReq.getIdCaretaker());
			if (!TypeConvUtil.isNullOrEmpty(caretkrInfoCaretakerDto)
					&& !TypeConvUtil.isNullOrEmpty(caretkrInfoCaretakerDto.getDeleteSuccess())) {
				careTakerInfoRes.setDeleteUpdate(caretkrInfoCaretakerDto.getDeleteSuccess());
			}
		}
		return careTakerInfoRes;
	}
}
