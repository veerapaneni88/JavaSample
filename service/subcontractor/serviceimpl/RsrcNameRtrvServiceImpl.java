package us.tx.state.dfps.service.subcontractor.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.RsrcNameReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcontractor.service.RsrcNameRtrvService;

@Service
@Transactional
public class RsrcNameRtrvServiceImpl implements RsrcNameRtrvService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CapsResourceDao capsResourceDao;

	/**
	 *
	 * Method Name: getRsrcName
	 *
	 * Method Description: Implementation of RsrcNameRtrvService which selects a
	 * record from the CAPS_RESOURCE table via RsrcNameRtrvService, Cres04dDao,
	 * and Cres04dDaoImpl using ID_RESOURCE (table ID field) as input.
	 *
	 * @param pInputMsg
	 * @return CommonStringRes @
	 *
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonStringRes getRsrcName(RsrcNameReq pInputMsg) {
		CommonStringRes commonStringRes = new CommonStringRes();
		// callCRES04D
		ResourceDto resourceDto = capsResourceDao.getResourceById(pInputMsg.getIdRsrcLinkChild());
		if (!ObjectUtils.isEmpty(resourceDto)) {
			commonStringRes.setCommonRes(resourceDto.getNmResource());
		} else {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_CON_RESOURCE_INVALID);
			commonStringRes.setErrorDto(errorDto);
		}
		return commonStringRes;
	}
}
