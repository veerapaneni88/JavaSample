package us.tx.state.dfps.service.person.serviceimpl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.AddressPersonLink;
import us.tx.state.dfps.common.domain.PersonAddress;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.service.admin.dao.PersonStageLinkCatgIdDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AddressDtlReq;
import us.tx.state.dfps.service.common.request.AddressMassUpdateReq;
import us.tx.state.dfps.service.common.request.EditPersonAddressReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.common.response.AddressMassUpdateRes;
import us.tx.state.dfps.service.common.response.EditPersonAddressRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.EditPersonAddressDto;
import us.tx.state.dfps.service.person.dto.MedicaidUpdateDto;
import us.tx.state.dfps.service.person.service.MedicaidUpdateService;
import us.tx.state.dfps.service.person.service.PersonAddressService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN44S Class
 * Description:Operations for PersonAddress Apr 14, 2017 - 11:16:39 AM
 */
@Service
@Transactional
public class PersonAddressServiceImpl implements PersonAddressService {

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	MedicaidUpdateService medicaidUpdateService;

	@Autowired
	MobileUtil mobileUtil;

	@Autowired
	PersonStageLinkCatgIdDao personStageLinkDao;

	/**
	 * This does full row adds and updates of the PERSON_ADDRESS table
	 * savePersonAddress Service Name - CCMN44S, DAM Name - CCMNA8D
	 * 
	 * @param editpersonAddressDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long editPersonAddress(EditPersonAddressDto editpersonAddressDto, String action) {
		Long idPersonAddress = null;
		PersonAddress personAddress = new PersonAddress();
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(action)) {
			if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				if (!TypeConvUtil.isNullOrEmpty(editpersonAddressDto.getIdAddress())) {
					personAddress = personAddressDao.getPersonAddressById(editpersonAddressDto.getIdAddress());
					idPersonAddress = personAddress.getIdPersonAddr();
				}
			}
			personAddress.setDtLastUpdate(date);
			if (editpersonAddressDto.getAddrPersAddrStLn1() != null) {
				personAddress.setAddrPersAddrStLn1(editpersonAddressDto.getAddrPersAddrStLn1());
			}
			if (editpersonAddressDto.getAddrPersAddrStLn2() != null) {
				personAddress.setAddrPersAddrStLn2(editpersonAddressDto.getAddrPersAddrStLn2());
			}
			if (editpersonAddressDto.getAddrCity() != null) {
				personAddress.setAddrPersonAddrCity(editpersonAddressDto.getAddrCity());
			}
			if (editpersonAddressDto.getAddrZip() != null) {
				personAddress.setAddrPersonAddrZip(editpersonAddressDto.getAddrZip());
			}
			if (editpersonAddressDto.getAddrPersAddrAttn() != null) {
				personAddress.setAddrPersonAddrAttn(editpersonAddressDto.getAddrPersAddrAttn());
			}
			if (editpersonAddressDto.getCdAddrCounty() != null) {
				personAddress.setCdPersonAddrCounty(editpersonAddressDto.getCdAddrCounty());
			}
			if (editpersonAddressDto.getCdAddrState() != null) {
				personAddress.setCdPersonAddrState(editpersonAddressDto.getCdAddrState());
			}
			if (editpersonAddressDto.getNmCnty() != null) {
				personAddress.setNmCnty(editpersonAddressDto.getNmCnty());
			}
			if (editpersonAddressDto.getNmCntry() != null) {
				personAddress.setNmCntry(editpersonAddressDto.getNmCntry());
			}
			if (editpersonAddressDto.getNbrGcdLat() != null) {
				personAddress.setNbrGcdLat(new BigDecimal(editpersonAddressDto.getNbrGcdLat(), MathContext.DECIMAL64));
			}
			if (editpersonAddressDto.getNbrGcdLong() != null) {
				personAddress
						.setNbrGcdLong(new BigDecimal(editpersonAddressDto.getNbrGcdLong(), MathContext.DECIMAL64));
			}
			if (editpersonAddressDto.getCdAddrRtrn() != null) {
				personAddress.setCdAddrRtrn(editpersonAddressDto.getCdAddrRtrn());
			}
			if (editpersonAddressDto.getCdGcdRtrn() != null) {
				personAddress.setCdGcdRtrn(editpersonAddressDto.getCdGcdRtrn());
			}
			if (editpersonAddressDto.getIndValdtd() != null) {
				personAddress.setIndValdtd(editpersonAddressDto.getIndValdtd());
			}
			if (editpersonAddressDto.getDtValdtd() != null) {
				personAddress.setDtValdtd(editpersonAddressDto.getDtValdtd());
			}
			if (editpersonAddressDto.getMailbltyScore() != null) {
				personAddress.setTxtMailbltyScore(editpersonAddressDto.getMailbltyScore());
			}
			if (editpersonAddressDto.getIdAddress() != null) {
				personAddress.setIdPersonAddr(editpersonAddressDto.getIdAddress());
			}
			if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
				personAddressDao.updatePersonAddress(personAddress);
			} else if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
				personAddressDao.savePersonAddress(personAddress);
				idPersonAddress = personAddress.getIdPersonAddr();
			}
		}
		return idPersonAddress;
	}

	/**
	 * This does full row adds and updates of the ADDRESS_PERSON_LINK table.
	 * Although UPDATE will not modify column DT_PERS_ADDR_LINK_START.
	 * saveAddressPersonLink ServiceName - CCMN44S, DAM Name - CCMNA9D
	 * 
	 * @param editpersonAddressDto
	 * @param action
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String editAddressPersonLink(EditPersonAddressDto editpersonAddressDto, String action, Long ulIdPerson) {
		AddressPersonLink addressPersonLink = new AddressPersonLink();
		Date date = new Date();
		Date endDate = ServiceConstants.GENERIC_END_DATE;
		String addressPersonLinkType = ServiceConstants.EMPTY_STRING;
		try {
			addressPersonLink.setDtLastUpdate(date);
			if (!TypeConvUtil.isNullOrEmpty(action)) {
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					if (!TypeConvUtil.isNullOrEmpty(editpersonAddressDto.getIdAddrPersonLink())) {
						addressPersonLink = personAddressDao
								.getAddressPersonLinkById(editpersonAddressDto.getIdAddrPersonLink());
						if (editpersonAddressDto.getDtPersAddrLinkEnd() != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(editpersonAddressDto.getDtPersAddrLinkEnd());
							// cal.add(Calendar.DATE, -1);
							endDate = cal.getTime();
							addressPersonLink.setDtPersAddrLinkEnd(endDate);
						}
					}
				}
				if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					if (editpersonAddressDto.getIdAddrPersonLink() != null) {
						addressPersonLink.setIdAddrPersonLink(editpersonAddressDto.getIdAddrPersonLink());
					}
					if (ulIdPerson != null) {
						addressPersonLink.setIdPerson(ulIdPerson);
					}
					if (editpersonAddressDto.getIdAddress() != null) {
						addressPersonLink.setIdPersonAddr(editpersonAddressDto.getIdAddress());
					}
					if (editpersonAddressDto.getDtPersAddrLinkEnd() != null) {
						addressPersonLink.setDtPersAddrLinkEnd(editpersonAddressDto.getDtPersAddrLinkEnd());
						addressPersonLink.setDtPersAddrLinkStart(date);
					} else {
						addressPersonLink.setDtPersAddrLinkEnd(endDate);
						if (editpersonAddressDto.getDtPersAddrLinkStart() != null) {
							endDate = editpersonAddressDto.getDtPersAddrLinkStart();
							Calendar cal = Calendar.getInstance();
							cal.setTime(endDate);
							// cal.add(Calendar.DATE, 1);
							endDate = cal.getTime();
							addressPersonLink.setDtPersAddrLinkStart(endDate);
						}
					}
				}
				if (editpersonAddressDto.getCdPersAddrLinkType() != null) {
					addressPersonLink.setCdPersAddrLinkType(editpersonAddressDto.getCdPersAddrLinkType());
				}
				if (editpersonAddressDto.getIndPersAddrLinkInvalid() != null) {
					addressPersonLink.setIndPersAddrLinkInvalid(editpersonAddressDto.getIndPersAddrLinkInvalid());
				}
				if (editpersonAddressDto.getIndPersAddrLinkPrimary() != null) {
					addressPersonLink.setIndPersAddrLinkPrimary(editpersonAddressDto.getIndPersAddrLinkPrimary());
				}
				if (editpersonAddressDto.getPersAddrCmnts() != null) {
					addressPersonLink.setTxtPersAddrCmnts(editpersonAddressDto.getPersAddrCmnts());
				}
				if (action.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					personAddressDao.updateAddressPersonLink(addressPersonLink);
				} else if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
					personAddressDao.saveAddressPersonLink(addressPersonLink);
				}
				addressPersonLinkType = addressPersonLink.getCdPersAddrLinkType();
			}
		} catch (HibernateException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return addressPersonLinkType;
	}

	/**
	 * This is the AUD service for the Address List/Detail window
	 * 
	 * @param editPersonAddressReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EditPersonAddressRes editPersonAddressDetail(EditPersonAddressReq editPersonAddressReq) {
		EditPersonAddressRes editPersonAddressRes = new EditPersonAddressRes();
		Boolean status;
		String retVal = ServiceConstants.FND_SUCCESS;
		Long idPersonAddress = ServiceConstants.ZERO_VAL;
		String addressPersonLinkType = ServiceConstants.EMPTY_STRING;
		List<EditPersonAddressDto> editPersonAddressDtoList = new ArrayList<>();
		MedicaidUpdateDto medicaidUpdateDto = new MedicaidUpdateDto();
		try {
			if (editPersonAddressReq != null) {
				if (editPersonAddressReq.getUlIdStage() != null) {
					if (!editPersonAddressReq.getUlIdStage().equals(ServiceConstants.ZERO_VAL)) {
						// CCMN06U
						editPersonAddressReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
						InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
						inCheckStageEventStatusDto.setCdTask(editPersonAddressReq.getSzCdTask());
						inCheckStageEventStatusDto.setIdStage(editPersonAddressReq.getUlIdStage());
						inCheckStageEventStatusDto.setCdReqFunction(editPersonAddressReq.getReqFuncCd());
						status = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
						if (status) {
							retVal = ServiceConstants.FND_SUCCESS;
						} else {
							retVal = ServiceConstants.FND_FAIL;
						}
					}
				}
				if (retVal.equals(ServiceConstants.FND_SUCCESS)) {
					if (editPersonAddressReq.getEditPersonAddressDtoList() != null) {
						editPersonAddressDtoList = editPersonAddressReq.getEditPersonAddressDtoList();
						for (EditPersonAddressDto editPersonAddressDto : editPersonAddressDtoList) {
							if (!TypeConvUtil.isNullOrEmpty(editPersonAddressDto.getCdScrDataAction())) {
								if (editPersonAddressDto.getCdScrDataAction().equals(ServiceConstants.REQ_FUNC_CD_ADD)
										|| editPersonAddressDto.getCdScrDataAction()
												.equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
									// CCMNA8D
									idPersonAddress = this.editPersonAddress(editPersonAddressDto,
											editPersonAddressDto.getCdScrDataAction());
									if (!TypeConvUtil.isNullOrEmpty(idPersonAddress)) {
										editPersonAddressDto.setIdAddress(idPersonAddress);
									}
									if (!TypeConvUtil.isNullOrEmpty(editPersonAddressReq.getUlIdPerson())) {
										// CCMNA9D
										addressPersonLinkType = this.editAddressPersonLink(editPersonAddressDto,
												editPersonAddressDto.getCdScrDataAction(),
												editPersonAddressReq.getUlIdPerson());
									}
									if (!TypeConvUtil.isNullOrEmpty(addressPersonLinkType) && !TypeConvUtil
											.isNullOrEmpty(editPersonAddressDto.getSysIndAddrMedUpdate())) {
										if (addressPersonLinkType.equals(ServiceConstants.MEDICAID_ADDRESS_CODE)
												&& editPersonAddressDto.getSysIndAddrMedUpdate()
														.equals(ServiceConstants.STRING_IND_Y)
												&& !TypeConvUtil.isNullOrEmpty(editPersonAddressReq.getUlIdStage())) {
											// CAUD99D
											medicaidUpdateDto.setIdMedUpdStage(editPersonAddressReq.getUlIdStage());
											if (!TypeConvUtil.isNullOrEmpty(editPersonAddressReq.getUlIdPerson())) {
												medicaidUpdateDto
														.setIdMedUpdPerson(editPersonAddressReq.getUlIdPerson());
											}
											if (!TypeConvUtil.isNullOrEmpty(idPersonAddress)) {
												medicaidUpdateDto.setIdMedUpdRecord(idPersonAddress);
											}
											medicaidUpdateDto.setCdMedUpdType(ServiceConstants.MED_UPDATE_TYPE);
											medicaidUpdateDto
													.setCdMedUpdTransType(ServiceConstants.MED_UPDATE_TRANS_TYPE);
											medicaidUpdateService.editMedicaidUpdate(medicaidUpdateDto,
													ServiceConstants.REQ_FUNC_CD_ADD);
										}
									}
								}
							}
						}
					}
					editPersonAddressRes.setActionResult("Edit Successfully");
				}
			}
		} catch (HibernateException e) {
			editPersonAddressRes.setActionResult("Edit failed");
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return editPersonAddressRes;
	}

	/**
	 * 
	 * Method Description: This method is to get the List of address of a a
	 * Person from Person Address Table. Tuxedo Servive Name:CCMN42S Tuxedo DAM
	 * Name: CCMN96D
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public AddressDtlRes getAddressList(AddressDtlReq addressDtlReq) {
		AddressDtlRes addressDtlRes = new AddressDtlRes();
		addressDtlRes.setAddressList(personAddressDao.getAddressList(addressDtlReq));
		return addressDtlRes;
	}

	/**
	 * MethodName: getAddressListPullback Method Description: This method is to
	 * get the List of address of a all the persons in a stage.
	 * 
	 * @param AddressDtlReq
	 * @return AddressDtlRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public AddressDtlRes getAddressListPullback(AddressDtlReq addressDtlReq) {
		AddressDtlRes addressDtlRes = new AddressDtlRes();
		Set<AddressDto> addressDtoSet = (personAddressDao.getAddressListPullback(addressDtlReq)).stream()
				.collect(Collectors.toSet());
		addressDtlRes.setAddressList(addressDtoSet.stream().collect(Collectors.toList()));
		return addressDtlRes;
	}

	/**
	 * Method Description: This method will implement perform mass update of
	 * address for person list. Service Name: Address Mass Update
	 * 
	 * @param addressMassUpdateReq
	 * @return AddressMassUpdateRes @
	 */
	@SuppressWarnings("unused")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AddressMassUpdateRes massAddrUpdate(AddressMassUpdateReq addressMassUpdateReq) {

		List<EditPersonAddressDto> editPersonAddressDtoList = addressMassUpdateReq.getEditPersonAddressDtoList();
		Long idPersonAddress = ServiceConstants.ZERO_VAL;
		for (EditPersonAddressDto editPersonAddressDto : editPersonAddressDtoList) {
			idPersonAddress = this.editPersonAddress(editPersonAddressDto, editPersonAddressDto.getCdScrDataAction());
			if (!TypeConvUtil.isNullOrEmpty(idPersonAddress)) {
				editPersonAddressDto.setIdAddress(idPersonAddress);
			}
			if (!TypeConvUtil.isNullOrEmpty(editPersonAddressDto.getIdPerson())) {
				this.editAddressPersonLink(editPersonAddressDto, editPersonAddressDto.getCdScrDataAction(),
						editPersonAddressDto.getIdPerson());
			}
		}
		AddressMassUpdateRes addressMassUpdateRes = new AddressMassUpdateRes();

		addressMassUpdateRes.setIsUpdate(Boolean.TRUE);
		return addressMassUpdateRes;
	}

	/**
	 * MethodName: getPersonAddressDtls Method Description: This method will get
	 * the current residential address from person_Address table
	 * 
	 * @param IdPerson
	 * @return AddressDtlRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public AddressDtlRes getPersonAddressDtls(AddressDtlReq addressDtlReq) {
		AddressDtlRes addressDtlRes = personAddressDao.getPersonAddressDtls(addressDtlReq.getIdPerson());
		return addressDtlRes;
	}

	/**
	 * Method Description: Returns all the active addresses for people attached
	 * to a stage. Primary Address from the Address_Person_Link table.
	 * 
	 * @param addressDtlReq
	 * @return AddressDtlRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AddressDtlRes getActiveAddressForStage(AddressDtlReq addressDtlReq) {
		AddressDtlRes addressDtlRes = new AddressDtlRes();
		List<AddressDto> addressList = personAddressDao.getActiveAddressForStage(addressDtlReq.getUlIdStage(),
				addressDtlReq.getStageCode());
		addressDtlRes.setAddressList(addressList);
		return addressDtlRes;
	}
}
