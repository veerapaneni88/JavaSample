package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.common.domain.UnitEmpLink;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dto.UnitDetailEmpDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkDto;
import us.tx.state.dfps.service.admin.dto.UnitSupervisorDto;
import us.tx.state.dfps.service.admin.service.UnitService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.ExternalUserUnitReq;
import us.tx.state.dfps.service.common.request.SaveUnitReq;
import us.tx.state.dfps.service.common.request.SearchUnitSupervisorReq;
import us.tx.state.dfps.service.common.request.UnitDetailReq;
import us.tx.state.dfps.service.common.request.UnitListReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.SaveUnitRes;
import us.tx.state.dfps.service.common.response.SearchUnitSupervisorRes;
import us.tx.state.dfps.service.common.response.UnitDetailRes;
import us.tx.state.dfps.service.common.response.UnitListRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.NotValidEntityException;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.workload.dao.CaseWorkloadDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;

import static us.tx.state.dfps.service.common.ServiceConstants.*;

@Service
@Transactional
public class UnitServiceImpl implements UnitService {

	@Autowired
	UnitDao unitDao;

	@Autowired
	StageWorkloadDao stageDao;

	@Autowired
	CaseWorkloadDao caseDao;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(UnitServiceImpl.class);
	private static final int magicErrorCode = 99;
	/**
	 *
	 * Method Description: This Service receives input to get unit detail through
	 * query. Tuxedo Service Name:CCMN23S
	 *
	 * @param unitDetailReq
	 * @return searchUnitDetailRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public UnitDetailRes getUnitDetail(UnitDetailReq unitDetailReq) {
		UnitDetailRes searchUnitDetailRes = new UnitDetailRes();
		if (!TypeConvUtil.isNullOrEmpty(unitDetailReq.getUlIdUnit())) {
			UnitDto searchUnitDetailDto = unitDao.searchUnitDtls(unitDetailReq.getUlIdUnit());
			Boolean bSysIndGeneric = this.unitAccess(unitDetailReq.getUlIdUnit(), null, null, null,
					unitDetailReq.getUlIdPerson());
			if (bSysIndGeneric == true) {
				searchUnitDetailDto.setSysCdWinMode(ServiceConstants.WINDOW_MODE_MODIFY);
			} else {
				searchUnitDetailDto.setSysCdWinMode(ServiceConstants.WINDOW_MODE_INQUIRE);
			}
			List<UnitDetailEmpDto> empUnitDetailDto = unitDao.searchEmpUnitDtls(unitDetailReq.getUlIdUnit());
			ErrorDto errorCode = new ErrorDto();
			if (TypeConvUtil.isNullOrEmpty(searchUnitDetailDto)) {
				errorCode.setErrorCode(ServiceConstants.MSG_NO_ROWS_RETURNED);
				searchUnitDetailRes.setErrorDto(errorCode);
			}
			searchUnitDetailRes.setUnitDetailDto(searchUnitDetailDto);
			searchUnitDetailRes.setUnitDetailEmpDto(empUnitDetailDto);
		}
		searchUnitDetailRes.setTransactionId(unitDetailReq.getTransactionId());
		log.info("TransactionId :" + unitDetailReq.getTransactionId());
		return searchUnitDetailRes;
	}

	/**
	 * '
	 *
	 * Method Description: This service receives input to get list of unit from dao
	 * query Tuxedo Service Name:CCMN24S
	 *
	 * @param searchUnitListReq
	 * @return UnitListServiceOutput @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public UnitListRes getUnitList(UnitListReq searchUnitListReq) {
		List<UnitDto> UnitListServiceOutput = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(searchUnitListReq.getNbrUnit())) {
			if (!TypeConvUtil.isNullOrEmpty(searchUnitListReq.getUnitProgram())
					&& !TypeConvUtil.isNullOrEmpty(searchUnitListReq.getUnitRegion())) {
				UnitListServiceOutput = unitDao.searchNbrUnitList(searchUnitListReq);
			}
		} else if (!TypeConvUtil.isNullOrEmpty(searchUnitListReq.getUnitProgram())
				&& !TypeConvUtil.isNullOrEmpty(searchUnitListReq.getUnitRegion())) {
			UnitListServiceOutput = unitDao.searchUnitList(searchUnitListReq);
		}
		UnitListRes unitListRes = new UnitListRes();
		ErrorDto errorCode = new ErrorDto();
		if (UnitListServiceOutput.size() <= 0) {
			if (!TypeConvUtil.isNullOrEmpty(searchUnitListReq.getNbrUnit())) {
				errorCode.setErrorCode(ServiceConstants.MSG_CMN_INVALID_UNIT);
				unitListRes.setErrorDto(errorCode);
			} else {
				errorCode.setErrorCode(ServiceConstants.MSG_CMN_UNIT_LIST_INV);
				unitListRes.setErrorDto(errorCode);
			}
		}
		unitListRes.setUnitDtoList(UnitListServiceOutput);
		unitListRes.setTransactionId(searchUnitListReq.getTransactionId());
		log.info("TransactionId :" + searchUnitListReq.getTransactionId());
		return unitListRes;
	}

	/**
	 *
	 * Method Description:saveUnit
	 *
	 * @param saveUnitReq
	 * @return
	 */
	// CCMN22S
	@Override
//	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveUnitRes saveUnit(SaveUnitReq saveUnitReq) {

		SaveUnitRes saveUnitRes;

		saveUnitReq.getUnitDto().setIdLastUpdatePerson(saveUnitReq.getRequestor());
		if ((saveUnitReq != null) && !TypeConvUtil.isNullOrEmpty(saveUnitReq.getReqFuncCd())) {
			if (saveUnitReq.getReqFuncCd().equals(REQ_FUNC_CD_UPDATE)) {
				saveUnitRes = updateUnit(saveUnitReq);
			}
			if (saveUnitReq.getReqFuncCd().equals(REQ_FUNC_CD_ADD)) {
				saveUnitRes = saveNewUnit(saveUnitReq);
			} else {
				saveUnitRes = commandError("Unknown Command");
			}
		} else {
			saveUnitRes = commandError("Invalid Request");
		}
		return saveUnitRes;
	}

	private SaveUnitRes commandError(String error) {

		SaveUnitRes saveUnitRes = new SaveUnitRes();
		ErrorDto errorDto = new ErrorDto();

		errorDto.setErrorMsg("Bad Command: "+error);
		errorDto.setErrorCode(-1);
		saveUnitRes.setActionResult(error);
		saveUnitRes.setErrorDto(errorDto);
		return saveUnitRes;
	}

	// The original code to update units
	private SaveUnitRes updateUnit(SaveUnitReq saveUnitReq) {
		UnitDto unitDto = new UnitDto();
		SaveUnitRes saveUnitRes = new SaveUnitRes();
		if (saveUnitReq != null) {
			if (!TypeConvUtil.isNullOrEmpty(saveUnitReq.getReqFuncCd())) {
				if (saveUnitReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
					//ALM:13551 added for Unit approver listed as LEAD of Unit.
					this.saveUnitEmpLink(saveUnitReq.getUnitEmpLinkDtoList(), saveUnitReq.getUlIdUnit(),saveUnitReq.getUnitDto());

					if (saveUnitReq.getUnitDto() != null) {
						unitDao.updateUnit(buildUnit(saveUnitReq.getUnitDto()));
					}
					saveUnitRes.setActionResult("Edit Successfully");
				}
			}
			if (saveUnitReq.getUnitDto() != null) {
				unitDto = saveUnitReq.getUnitDto();
				if (!TypeConvUtil.isNullOrEmpty(saveUnitReq.getUlIdUnit())) {
					unitDto.setIdUnit(saveUnitReq.getUlIdUnit());
				}
				if (!TypeConvUtil.isNullOrEmpty(unitDto.getAction())) {
					if (!unitDto.getAction().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
						this.editUnitDto(unitDto, unitDto.getAction());
						if (unitDto.getAction().equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
							// CCMNF3D
							if (saveUnitReq.getUlIdUnit() != null) {
								unitDao.updateUnitByUnitParentId(saveUnitReq.getUlIdUnit());
								// CAUDC2D
								stageDao.updateStagesByUnitId(saveUnitReq.getUlIdUnit());
								caseDao.updateCasesByUnitId(saveUnitReq.getUlIdUnit());
							}
						}
						saveUnitRes.setActionResult("Edit Successfully");
					}
				}
			}
		}
		if (null != saveUnitReq) {
			saveUnitRes.setTransactionId(saveUnitReq.getTransactionId());
			log.info("TransactionId :" + saveUnitRes.getTransactionId());
		}
		return saveUnitRes;
	}

	private Unit buildUnit(UnitDto dto) {
		Unit unit = new Unit();
		unit.setIdUnit(dto.getIdUnit());
		unit.setCdUnitProgram(dto.getCdUnitProgram());
		unit.setCdUnitRegion(dto.getCdUnitRegion());
		unit.setNbrUnit(dto.getNbrUnit());
		unit.setIdUnitParent(dto.getIdUnitParent());
		unit.setIdPerson(dto.getIdPerson());
		unit.setCdUnitSpecialization(dto.getCdUnitSpecialization());
		unit.setIndExternal(dto.getIndExternal());
		unit.setCdSsccCatchment(dto.getCdSsccCatchment());
		unit.setDtLastUpdate(new Date());
		unit.setIdCreatedPerson(dto.getIdCreatedPerson());
		unit.setDtCreated(dto.getDtCreated());
		unit.setDtExternalCreated(dto.getDtExternalCreated());
		unit.setIdLastUpdatePerson(dto.getIdLastUpdatePerson());
		return unit;
	}
	// code to save new unit
	private SaveUnitRes saveNewUnit(SaveUnitReq saveUnitReq) {

		UnitDto unitDto = new UnitDto();

		SaveUnitRes saveUnitRes = new SaveUnitRes();
		Session session = null;
		Transaction tx = null;
		String errMsg = "Error Creating Unit.";
			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();
				final long createdId=unitDao.saveAndFlushUnit(createUnit(saveUnitReq));
				saveUnitReq.setUlIdUnit(createdId);
				errMsg="Error adding unit members.";
				saveUnitReq.getUnitDto().setIdUnit(createdId);
				Date currentDate=new Date();
				saveUnitReq.getUnitEmpLinkDtoList().stream()
					.map(dto -> {
						dto.setIdUnit(createdId);
						dto.setDtCreated(currentDate);
						dto.setIdLastUpdatePerson(saveUnitReq.getRequestor());
						dto.setIdCreatedPerson(saveUnitReq.getRequestor());
						dto.setDtLastUpdate(currentDate);
						return createUnitEmpLink(dto);})
					.forEach(	dto->unitDao.saveUnitEmpLink(dto));
				saveUnitRes.setActionResult("Unit Added");
				tx.commit();
			} catch (Exception e) {
				ErrorDto errDto = new ErrorDto();
				errDto.setErrorMsg(e.getLocalizedMessage());
				errDto.setErrorCode(magicErrorCode);
				saveUnitRes.setErrorDto(errDto);
				saveUnitRes.setActionResult(errMsg);
				tx.rollback();
			}
		saveUnitRes.setTransactionId(saveUnitReq.getTransactionId());
		log.info("TransactionId :" + saveUnitRes.getTransactionId());
		return saveUnitRes;
	}
	private  UnitEmpLink createUnitEmpLink(UnitEmpLinkDto oldDto) {

		UnitEmpLink newDBRec = new UnitEmpLink();

		newDBRec.setDtCreated(oldDto.getDtCreated());
		newDBRec.setIdCreatedPerson(oldDto.getIdCreatedPerson());
		newDBRec.setIdUnit(oldDto.getIdUnit());
		newDBRec.setIdUnitEmpLink(oldDto.getIdUnitEmpLink());
		newDBRec.setCdUnitMemberInOut(oldDto.getUnitMemberInOut());
		newDBRec.setCdUnitMemberRole(oldDto.getUnitMemberRole());
		newDBRec.setIdPerson(oldDto.getIdPerson());
		newDBRec.setIdLastUpdatePerson(oldDto.getIdLastUpdatePerson());
		newDBRec.setDtLastUpdate(oldDto.getDtLastUpdate());
		return newDBRec;
	}
	/**
	 *
	 * Method Description:getUnitEmpLinkDto
	 *
	 * @param uel
	 * @return
	 */
	// CCMN05S
	@Override
	public UnitEmpLinkDto getUnitEmpLinkDto(UnitEmpLink uel) {
		UnitEmpLinkDto ueld = new UnitEmpLinkDto();
		if (uel != null) {
			if (uel.getIdUnitEmpLink() != null) {
				ueld.setIdUnitEmpLink(uel.getIdUnitEmpLink());
			}
			if (uel.getIdPerson() != null) {
				ueld.setIdPerson(uel.getIdPerson());
			}
			if (uel.getIdUnit() != null) {
				ueld.setIdUnit(uel.getIdUnit());
			}
			if (uel.getCdUnitMemberRole() != null) {
				ueld.setUnitMemberRole(uel.getCdUnitMemberRole());
			}
			if (uel.getCdUnitMemberInOut() != null) {
				ueld.setUnitMemberInOut(uel.getCdUnitMemberInOut());
			}
			if (uel.getDtLastUpdate() != null) {
				ueld.setDtLastUpdate(uel.getDtLastUpdate());
			}
		}
		return ueld;
	}

	/**
	 *
	 * Method Description:getUnitDto
	 *
	 * @param unit
	 * @return
	 */
	// CCMN22S
	@Override
	public UnitDto getUnitDto(Unit unit) {
		UnitDto output = new UnitDto();
		if (unit != null) {
			if (unit.getIdUnit() != null) {
				output.setIdUnit(unit.getIdUnit());
			}
			if (unit.getDtLastUpdate() != null) {
				output.setDtLastUpdate(unit.getDtLastUpdate());
			}
			if (unit.getNbrUnit() != null) {
				output.setNbrUnit(unit.getNbrUnit());
			}
			if (unit.getCdUnitRegion() != null) {
				output.setCdUnitRegion(unit.getCdUnitRegion());
			}
			if (unit.getCdUnitProgram() != null) {
				output.setCdUnitProgram(unit.getCdUnitProgram());
			}
			if (unit.getIdPerson() != null) {
				output.setIdPerson(unit.getIdPerson());
			}
			if (unit.getIdUnitParent() != null) {
				output.setIdUnitParent(unit.getIdUnitParent());
			}
		}
		return output;
	}

	/**
	 *
	 * Method Description:searchUnitSupervisor
	 *
	 * @param searchUnitSupervisorReq
	 * @return
	 */
	// CCMN08S
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public SearchUnitSupervisorRes searchUnitSupervisor(SearchUnitSupervisorReq searchUnitSupervisorReq) {
		SearchUnitSupervisorRes searchUnitSupervisorRes = new SearchUnitSupervisorRes();
		List<UnitSupervisorDto> unitSuperDtos = null;
		// CCMN34D
		if (searchUnitSupervisorReq != null) {
			if (searchUnitSupervisorReq.getUnitProgram() != null && searchUnitSupervisorReq.getUnitRegion() != null
					&& searchUnitSupervisorReq.getNbrUnit() != null
					&& searchUnitSupervisorReq.getIndExternal() != null) {
				unitSuperDtos = unitDao.searchUnitSupervisor(searchUnitSupervisorReq.getUnitProgram(),
						searchUnitSupervisorReq.getUnitRegion(), searchUnitSupervisorReq.getNbrUnit(),
						searchUnitSupervisorReq.getIndExternal());
			}
		}
		if (unitSuperDtos != null) {
			for (UnitSupervisorDto unit : unitSuperDtos) {
				if (unit.getUnit() != null && unit.getIdUnit() != null) {
					if (!unit.getUnit().equals(ServiceConstants.EMPTY_STRING)) {
						if (!(unitDao.searchIndExternal(unit.getIdUnit(), unit.getUnit()))
								.equals(ServiceConstants.UNIT_IND_EXTERNAL_Y)) {
							unit.setIndExternal(ServiceConstants.UNIT_IND_EXTERNAL_N);
						}
					}
				}
				if (searchUnitSupervisorReq.getIndExternal() != null && unit.getIndExternal() != null) {
					if (!searchUnitSupervisorReq.getIndExternal().equals(ServiceConstants.EMPTY_STRING)
							&& !unit.getIndExternal().equals(ServiceConstants.EMPTY_STRING)) {
						if (!unit.getIndExternal().equals(searchUnitSupervisorReq.getIndExternal())
								&& unit.getIndExternal().equals(ServiceConstants.UNIT_IND_EXTERNAL_N)) {
						} else if (!unit.getIndExternal().equals(searchUnitSupervisorReq.getIndExternal())
								&& unit.getIndExternal().equals(ServiceConstants.UNIT_IND_EXTERNAL_Y)) {
						} else {
							searchUnitSupervisorRes.setUnitSupervisorList(unitSuperDtos);
						}
					}
				}
			}
		}
		searchUnitSupervisorRes.setTransactionId(searchUnitSupervisorReq.getTransactionId());
		log.info("TransactionId :" + searchUnitSupervisorReq.getTransactionId());
		return searchUnitSupervisorRes;
	}

	/**
	 *
	 * Method Description:saveUnitEmpLink
	 *
	 * @param unitEmpLinkDtoList
	 * @param unitId
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveUnitEmpLink(List<UnitEmpLinkDto> unitEmpLinkDtoList, Long unitId, UnitDto unitDto) {
		List<Long> units = null;
		boolean unitApproverCheck = false;
		boolean successCheck = false;
		if (unitEmpLinkDtoList != null) {
			for (UnitEmpLinkDto ueld : unitEmpLinkDtoList) {
				if (ueld != null) {
					ueld.setIdUnit(unitId);
					if (!TypeConvUtil.isNullOrEmpty(ueld.getAction())) {
						if (!ueld.getAction().equals(ServiceConstants.REQ_FUNC_CD_NO_ACTION)) {
							if (ueld.getUnitMemberInOut() != null) {
								if (!ueld.getUnitMemberInOut().equals(ServiceConstants.EMPTY_STRING)) {
									if (ueld.getUnitMemberInOut().equals(ServiceConstants.UNIT_MEMBER_IN_ASSIGNED)) {
										if (ueld.getIdPerson() != null) {
											// CCMNG5D
											if (unitId != null) {
												units = unitDao.searchUnitByUnitApprover(ueld.getIdPerson());
												if (units != null) {
													if (units.size() > 0) {
														unitApproverCheck = true;
													} else {
														successCheck = true;
													}
												} else {
													successCheck = true;
												}
											}
										}
									}
								}
							}
							if (unitApproverCheck) {
								// CCMN36D
								if (units != null) {
									if (!units.isEmpty()) {
										if (unitDao.searchUnitEmpLinkAttributesByUnitId(units.get(0)) != null) {
											if (unitDao.searchUnitEmpLinkAttributesByUnitId(units.get(0)).size() == 1) {
												successCheck = true;
											} else if (unitDao.searchUnitEmpLinkAttributesByUnitId(units.get(0))
													.size() == 2) {
												successCheck = false;
												unitApproverCheck = true;
											}
										} else {
											successCheck = false;
											unitApproverCheck = true;
										}
									} else {
										successCheck = true;
									}
								} else {
									successCheck = true;
								}
							}
							if (successCheck) {
								if (ueld.getUnitMemberInOut() != null && ueld.getUnitScrMemberInOut() != null) {
									if (!ueld.getUnitMemberInOut().equals(ServiceConstants.EMPTY_STRING)
											&& !ueld.getUnitScrMemberInOut().equals(ServiceConstants.EMPTY_STRING)) {
										if (ueld.getUnitMemberInOut().equals(ServiceConstants.UNIT_MEMBER_IN_ASSIGNED)
												&& ueld.getUnitScrMemberInOut()
												.equals(ServiceConstants.UNIT_MEMBER_OUT_ASSIGNED)) {
											// CCMNF4D
											unitDao.updateUnitApprover(ueld.getIdPerson(),
													ServiceConstants.UNIT_MEMBER_IN_ASSIGNED);
											// CCMNE0D
											unitDao.deleteUnitEmpLinkByPersonIdAndInOut(ueld.getIdPerson(),
													ServiceConstants.UNIT_MEMBER_IN_ASSIGNED);
										}
									}
								}
							}
							// ALM:13551 added for Unit approver listed as LEAD of Unit.
							// artf263882 - moved the code to this line.
							if(ServiceConstants.LEAD_TYPE.equals(ueld.getUnitMemberRole())) {
								unitDto.setIdPerson(ueld.getIdPerson());
							}
							// CCMN49D
							this.editUnitEmpLinkDto(ueld, ueld.getAction());
						}
					}
				}
			}
		}
	}

	/**
	 *
	 * Method Description:editUnitEmpLinkDto
	 *
	 * @param unitEmpLinkDto
	 * @param action
	 * @return UnitEmpLinkDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public UnitEmpLinkDto editUnitEmpLinkDto(UnitEmpLinkDto unitEmpLinkDto, String action) {
		UnitEmpLinkDto unitDto = new UnitEmpLinkDto();
		UnitEmpLink uel = new UnitEmpLink();
		Date date = new Date();
		if (action.equals(REQ_FUNC_CD_UPDATE)) {
			if (unitEmpLinkDto.getIdUnitEmpLink() != null) {
				uel = unitDao.searchUnitEmpLinkById(unitEmpLinkDto.getIdUnitEmpLink());
			}
		}
		if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			if (unitEmpLinkDto.getIdUnitEmpLink() != null) {
				uel = unitDao.searchUnitEmpLinkById(unitEmpLinkDto.getIdUnitEmpLink());
			}
		}
		uel.setDtLastUpdate(date);
		if (unitEmpLinkDto.getIdUnitEmpLink() != null) {
			uel.setIdUnitEmpLink(unitEmpLinkDto.getIdUnitEmpLink());
		}
		if (unitEmpLinkDto.getIdPerson() != null) {
			uel.setIdPerson(unitEmpLinkDto.getIdPerson());
		}
		if (unitEmpLinkDto.getIdUnit() != null) {
			uel.setIdUnit(unitEmpLinkDto.getIdUnit());
		}
		if (unitEmpLinkDto.getUnitMemberRole() != null) {
			if (!unitEmpLinkDto.getUnitMemberRole().equals(ServiceConstants.EMPTY_STRING)) {
				uel.setCdUnitMemberRole(unitEmpLinkDto.getUnitMemberRole());
			}
		}
		if (unitEmpLinkDto.getUnitMemberInOut() != null) {
			if (!unitEmpLinkDto.getUnitMemberInOut().equals(ServiceConstants.EMPTY_STRING)) {
				uel.setCdUnitMemberInOut(unitEmpLinkDto.getUnitMemberInOut());
			}
		}
		if (action.equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
			unitDao.saveUnitEmpLink(uel);
		} else if (action.equals(REQ_FUNC_CD_UPDATE)) {
			unitDao.updateUnitEmpLink(uel);
		} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			unitDao.deleteUnitEmpLink(uel);
		}
		unitDto = this.getUnitEmpLinkDto(uel);
		return unitDto;
	}

	/**
	 *
	 * Method Description:editUnitDto
	 *
	 * @param unitDto
	 * @param action
	 * @return UnitDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public UnitDto editUnitDto(UnitDto unitDto, String action) {
		UnitDto dto = new UnitDto();
		Unit unit = new Unit();
		Date date = new Date();
		if (action.equals(REQ_FUNC_CD_UPDATE)) {
			if (unitDto.getIdUnit() != null) {
				unit = unitDao.searchUnitEntityById(unitDto.getIdUnit());
			}
		}
		if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			if (unitDto.getIdUnit() != null) {
				unit = unitDao.searchUnitEntityById(unitDto.getIdUnit());
			}
		}
		unit.setDtLastUpdate(date);
		if (unitDto.getCdUnitProgram() != null) {
			if (!unitDto.getCdUnitProgram().equals(ServiceConstants.EMPTY_STRING)) {
				unit.setCdUnitProgram(unitDto.getCdUnitProgram());
			}
		}
		if (unitDto.getCdUnitRegion() != null) {
			if (!unitDto.getCdUnitRegion().equals(ServiceConstants.EMPTY_STRING)) {
				unit.setCdUnitRegion(unitDto.getCdUnitRegion());
			}
		}
		if (unitDto.getNbrUnit() != null) {
			if (!unitDto.getNbrUnit().equals(ServiceConstants.EMPTY_STRING)) {
				unit.setNbrUnit(unitDto.getNbrUnit());
			}
		}
		if (unitDto.getCdUnitSpecialization() != null) {
			if (!unitDto.getCdUnitSpecialization().equals(ServiceConstants.EMPTY_STRING)) {
				unit.setCdUnitSpecialization(unitDto.getCdUnitSpecialization());
			}
		}
		if (unitDto.getIdUnitParent() != null) {
			if (!unitDto.getIdUnitParent().equals(ServiceConstants.ZERO_VAL)) {
				unit.setIdUnitParent(unitDto.getIdUnitParent());
			}
		}
		if (unitDto.getIdPerson() != null) {
			unit.setIdPerson(unitDto.getIdPerson());
		}
		if (unitDto.getCdSsccCatchment() != null) {
			if (!unitDto.getCdSsccCatchment().equals(ServiceConstants.EMPTY_STRING)) {
				unit.setCdSsccCatchment(unitDto.getCdSsccCatchment());
			}
		}
		if (unitDto.getIdUnit() != null) {
			unit.setIdUnit(unitDto.getIdUnit());
		}
		if (action.equals(REQ_FUNC_CD_UPDATE)) {
			unitDao.updateUnit(unit);
		} else if (action.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			unitDao.deleteUnit(unit);
		} else if (action.equals(REQ_FUNC_CD_ADD)) {
			unitDao.saveUnit(unit);
		}
		dto = this.getUnitDto(unit);
		return dto;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public boolean unitAccess(Long ulIdUnit, String cdUnitProgram, String cdUnitRegion, String nbrUnit,
							  List<Long> ulIdPersons) {
		boolean unitAccess = false;
		UnitDto unit = null;
		List<UnitEmpLinkDto> uels = null;
		List<Long> uelss = null;
		if (!TypeConvUtil.isNullOrEmpty(ulIdUnit)) {
			unit = unitDao.searchUnitById(ulIdUnit);
		} else {
			if (cdUnitProgram != null && cdUnitRegion != null && nbrUnit != null) {
				unit = unitDao.searchUnitByAttributes(cdUnitProgram, cdUnitRegion, nbrUnit);
			}
		}
		if (unit != null) {
			if (unit.getIdPerson() != ServiceConstants.ZERO_VAL) {
				boolean bIsApprover = false;
				if (!TypeConvUtil.isNullOrEmpty(ulIdPersons)) {
					if (ulIdPersons.contains(unit.getIdPerson())) {
						bIsApprover = true;
					}
				}
				unitAccess = bIsApprover;
				if (!bIsApprover) {
					if (unit.getIdUnit() != null && !ObjectUtils.isEmpty(unit.getIdPerson())) {
						// CCMND5D
						uels = unitDao.searchUnitEmpLinkByUnitPersonId(unit.getIdUnit(), unit.getIdPerson());
					}
					if (uels != null) {
						for (UnitEmpLinkDto uel : uels) {

							if (uel.getIdUnit() != null && ulIdPersons != null && uel.getUnitMemberRole() != null) {
								// Added this loop to fix the defect HP ALM ID 11401(Designee permissions do not carry over to Unit Maintenance)
								for (Long idPerson : ulIdPersons) {
									// CCMN32D
									uelss = unitDao.searchUnitEmpLinkByUnitPersonIdAndRole(unit.getIdUnit(),
											idPerson, uel.getUnitMemberRole(),
											ServiceConstants.UNIT_MEMBER_ROLE_CLERK);
									if (uelss != null && uelss.size() > 0) {
										unitAccess = true;
										break;
									}

								}
								if (unitAccess) {
									break;
								}
							}

						}
					}
				}
			} else {
				unitAccess = true;
			}
		} else {
			unitAccess = true;
		}
		return unitAccess;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveNewExternalUnitDetail(ExternalUserUnitReq externalUserUnitReq) {

				/* Original code

		if ((hasInternalLeadExists(externalUserUnitReq)) && isRoleCountInLimit(externalUserUnitReq)) {
			unitDao.saveExtUserUnit(externalUserUnitReq);
		} else if (!hasInternalLeadExists(externalUserUnitReq)) {
			throw new NotValidEntityException("Each unit must have exactly one lead.", 25002L);
		} else if (!isRoleCountInLimit(externalUserUnitReq)) {
			// parent/lead/maintaine        r role exceeds 40.
			throw new NotValidEntityException(
					"The total number of Lead, Parent, and Maintainer roles in the Unit exceeds 200", 55313L);
		}
        */
		if (!isRoleCountInLimit(externalUserUnitReq)) {
			// parent/lead/maintaine        r role exceeds 40.

			throw new NotValidEntityException(
					"The total number of Lead, Parent, and Maintainer roles in the Unit exceeds 200", 55313L);
		}
		if (hasInternalLeadExists(externalUserUnitReq)) {
			unitDao.saveExtUserUnit(externalUserUnitReq);
		} else {
			throw new NotValidEntityException("Each unit must have exactly one lead.", 25002L);
		}
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> fetchCatchmentsForRegion(String cdRegion) {
		return unitDao.fetchCatchmentsForRegion(cdRegion);
	}

	private boolean hasInternalLeadExists(ExternalUserUnitReq extUsrUnitDB) {
		Optional<UnitEmpLinkDto> optional = extUsrUnitDB.getUnitEmpLinkDtoList().stream()
				.filter(extUsrUnitMemDB -> StringUtils.isBlank(extUsrUnitMemDB.getIndExternalType())
						&& LEAD_TYPE.equalsIgnoreCase(extUsrUnitMemDB.getUnitMemberRole()))
				.findAny();
		return optional.isPresent();
	}

	/**
	 * Checks the total number of parent/lead/maintainer roles are not more than 40.
	 * SIR 25072
	 *
	 * @param extUsrUnitDB
	 * @return
	 */
	private boolean isRoleCountInLimit(ExternalUserUnitReq extUsrUnitDB) {
		final List<String> roles = Arrays.asList(new String[] { LEAD_TYPE, MAINTAINER_TYPE, PARENT_TYPE });
		long overLimit = extUsrUnitDB.getUnitEmpLinkDtoList().stream()
				.filter(unit -> roles.contains(unit.getUnitMemberRole())).count();
		//artf123435 : changed logic to check if countRoles is greater than 200
		return overLimit > 200 ? false : true;
	}

	/**
	 *
	 * Method Name: isExteralUnit Method Description:
	 *
	 * @param program
	 * @param region
	 * @param unitNbr
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes isExteralUnit(String program, String region, String unitNbr) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsExternalUnit(unitDao.isExternalUnit(program, region, unitNbr));
		return commonHelperRes;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Unit createUnit(SaveUnitReq saveUnitReq) {
		Unit unit = new Unit();
		unit.setIdUnit(saveUnitReq.getUnitDto().getIdUnit());
		unit.setCdUnitRegion(saveUnitReq.getUnitDto().getCdUnitRegion());
		unit.setCdUnitProgram(saveUnitReq.getUnitDto().getCdUnitProgram());
		unit.setNbrUnit(saveUnitReq.getUnitDto().getNbrUnit());
		unit.setCdUnitSpecialization(saveUnitReq.getUnitDto().getCdUnitSpecialization());
		unit.setIndExternal(saveUnitReq.getUnitDto().getIndExternal());
		unit.setCdSsccCatchment(saveUnitReq.getUnitDto().getCdSsccCatchment());
		unit.setIdUnit(saveUnitReq.getUnitDto().getIdUnit());
		unit.setIdUnitParent(saveUnitReq.getUnitDto().getIdUnitParent());
		unit.setDtLastUpdate(new Date());
		unit.setDtExternalCreated(unit.getDtLastUpdate());
		unit.setIdPerson(saveUnitReq.getUnitDto().getIdPerson());
		unit.setDtCreated(saveUnitReq.getUnitDto().getDtCreated());
		unit.setIdLastUpdatePerson(saveUnitReq.getUnitDto().getIdLastUpdatePerson());
		unit.setIdCreatedPerson(saveUnitReq.getUnitDto().getIdCreatedPerson());
		unit.setDtCreated(new Date());
		return unit;
	}

}
