package us.tx.state.dfps.service.subcontractor.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.subcontractor.dto.SubcontrListSaveiDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: Validates SbcntrListSave service mandatory input fields
 *
 * Aug 21, 2017- 2:06:17 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
public class SbcntrListSaveValidator {
	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: validateInputs
	 * 
	 * Method Description: Validates input mandatory fields for SbcntrListSave
	 * service
	 * 
	 * @param liSbcntrListSaveiDto
	 * @throws InvalidRequestException
	 */
	public void validateInputs(List<SubcontrListSaveiDto> liSbcntrListSaveiDto) {

		if (TypeConvUtil.isNullOrEmpty(liSbcntrListSaveiDto)) {
			throw new InvalidRequestException(
					messageSource.getMessage("sbcntrListSave.input.mandatory", null, Locale.US));
		}

		for (SubcontrListSaveiDto sbcntrListSaveiDto : liSbcntrListSaveiDto) {
			boolean isDataActionNotExists = TypeConvUtil.isNullOrEmpty(sbcntrListSaveiDto.getCdScrDataAction());

			if (isDataActionNotExists) {
				throw new InvalidRequestException(
						messageSource.getMessage("sbcntrListSave.cdScrDataAction.mandatory", null, Locale.US));
			}

			boolean isDataActionAdd = sbcntrListSaveiDto.getCdScrDataAction().equals(ServiceConstants.ADD);

			boolean isDataActionUpDel = sbcntrListSaveiDto.getCdScrDataAction().equals(ServiceConstants.UPDATE)
					|| sbcntrListSaveiDto.getCdScrDataAction().equals(ServiceConstants.DELETE);

			if (isDataActionAdd) {
				validateForAdd(sbcntrListSaveiDto);
			} else if (isDataActionUpDel) {
				validateForModify(sbcntrListSaveiDto);
			} else {
				throw new InvalidRequestException(
						messageSource.getMessage("sbcntrListSave.cdScrDataAction.mandatory", null, Locale.US));
			}
		}
	}

	/**
	 * Method Name: validateForModify Method Description:
	 * 
	 * @param sbcntrListSaveiDto
	 */
	private void validateForModify(SubcontrListSaveiDto sbcntrListSaveiDto) {
		if (TypeConvUtil.isNullOrEmpty(sbcntrListSaveiDto.getIdRsrcLink())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sbcntrListSave.idRsrcLink.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(sbcntrListSaveiDto.getDtLastUpdate())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sbcntrListSave.tsLastUpdate.mandatory", null, Locale.US));
		}
	}

	/**
	 * Method Name: validateForAdd Method Description:
	 * 
	 * @param sbcntrListSaveiDto
	 */
	private void validateForAdd(SubcontrListSaveiDto sbcntrListSaveiDto) {
		if (TypeConvUtil.isNullOrEmpty(sbcntrListSaveiDto.getCdRsrcLinkType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sbcntrListSave.cdRsrcLinkType.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(sbcntrListSaveiDto.getIdRsrcLinkChild())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sbcntrListSave.idRsrcLinkChild.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(sbcntrListSaveiDto.getIdRsrcLinkParent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sbcntrListSave.idRsrcLinkParent.mandatory", null, Locale.US));
		}
	}

	/**
	 * Method Name: validateInputsService
	 * 
	 * Method Description: Validates input mandatory fields for SbcntrListSave
	 * service
	 * 
	 * @param liSbcntrListSaveiDto
	 * 
	 */
	public void validateInputsService(List<SubcontrListSaveiDto> liSbcntrListSaveiDto) {
		try {
			validateInputs(liSbcntrListSaveiDto);
		} catch (InvalidRequestException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
	}

}
