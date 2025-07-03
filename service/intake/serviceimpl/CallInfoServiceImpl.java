package us.tx.state.dfps.service.intake.serviceimpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.IncomingFacility;
import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.request.FacDetailUpdtReq;
import us.tx.state.dfps.service.common.request.PersListReq;
import us.tx.state.dfps.service.common.response.FacilRtrvRes;
import us.tx.state.dfps.service.common.response.PersListRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.intake.dao.IncomingFacilityDao;
import us.tx.state.dfps.service.intake.dto.PersListRtrvDto;
import us.tx.state.dfps.service.intake.dto.PersonListKey;
import us.tx.state.dfps.service.intake.service.CallInfoService;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.PersonAddressDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneOutDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Mar 27, 2017 - 9:13:51 AM
 */
@Service
@Transactional
public class CallInfoServiceImpl implements CallInfoService {

	@Autowired
	PersonDao personDao;

	@Autowired
	PersonAddressDao personAddressDao;

	@Autowired
	NameDao nameDao;

	@Autowired
	PersonPhoneDao personPhoneDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private IncomingFacilityDao incomingFacilityDao;

	private static final Logger log = Logger.getLogger(CallInfoServiceImpl.class);

	public CallInfoServiceImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.service.CallInfoService#getPersonList(org.tx.us.
	 * dfps.impact.model.Input.PersListReq)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersListRes getPersonList(PersListReq persListInRec) {
		PersListRes persListRes = new PersListRes();
		HashMap<PersonListKey, PersListRtrvDto> personListMap = new HashMap<>();
		if (!persListInRec.getCdIncmgStatus().equals(ServiceConstants.STATUS_CLOSED))
			persListInRec.setTsIncmgCallDisp(DateUtils.addDays(ServiceConstants.GENERIC_END_DATE, -1));
		if (persListInRec.getCdIncmgStatus().equals(ServiceConstants.STATUS_OPEN))
			personListMap = personDao.getPersonList(persListInRec);
		else
			personListMap = personDao.getPersonListHistory(persListInRec);
		Iterator it = personListMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<PersonListKey, PersListRtrvDto> personListEntry = (Entry) it.next();
			PersonListKey personListKey = (PersonListKey) personListEntry.getKey();
			PersListRtrvDto persListRtrvStruct = (PersListRtrvDto) personListEntry.getValue();
			PersonAddrLinkDto pesnAddrDto = personAddressDao.getPersonAddrForPersonList(persListInRec,
					personListKey.getUlIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(pesnAddrDto) && pesnAddrDto.getIdAddrPersonLink() > 0) {
				persListRtrvStruct.setIdAddrPersonLink(pesnAddrDto.getIdAddrPersonLink());
				persListRtrvStruct.setIdPerson(pesnAddrDto.getIdPerson());
				persListRtrvStruct.setCdPersAddrLinkType(pesnAddrDto.getCdPersAddrLinkType());
				persListRtrvStruct.setAddrPersAddrStLn1(pesnAddrDto.getAddrPersAddrStLn1());
				persListRtrvStruct.setAddrPersAddrStLn2(pesnAddrDto.getAddrPersAddrStLn2());
				persListRtrvStruct.setAddrCity(pesnAddrDto.getAddrPersonAddrCity());
				persListRtrvStruct.setCdAddrState(pesnAddrDto.getCdPersonAddrState());
				persListRtrvStruct.setCdAddrCounty(pesnAddrDto.getCdPersonAddrCounty());
				persListRtrvStruct.setAddrZip(pesnAddrDto.getAddrPersonAddrZip());
				persListRtrvStruct.setIndPersAddrLinkInvalid(pesnAddrDto.getIndPersAddrLinkInvalid());
				persListRtrvStruct.setDtPersAddrLinkStart(pesnAddrDto.getDtPersAddrLinkStart());
				persListRtrvStruct.setDtPersAddrLinkEnd(pesnAddrDto.getDtPersAddrLinkEnd());
				persListRtrvStruct.setPersAddrCmnts(pesnAddrDto.getPersAddrCmnts());
			}
			Name name = nameDao.getNameForPersonList(persListInRec, personListKey.getUlIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(name) && name.getIdName() > 0) {
				persListRtrvStruct.setScrIndAlias("Y");
				persListRtrvStruct.setNmNameFirst(name.getNmNameFirst());
				persListRtrvStruct.setNmNameLast(name.getNmNameLast());
				persListRtrvStruct.setNmNameMiddle(name.getNmNameMiddle());
				persListRtrvStruct.setDtNameStart(name.getDtNameStartDate());
				persListRtrvStruct.setDtNameEnd(name.getDtNameEndDate());
				persListRtrvStruct.setIndNameInvalid(name.getIndNameInvalid());
				persListRtrvStruct.setCdNameSuffix(name.getCdNameSuffix());
			}
			PersonPhoneOutDto personPhone = personPhoneDao.getpersonPhoneforPersonList(persListInRec,
					personListKey.getUlIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(personPhone) && personPhone.getIdPersonPhone() > 0) {
				persListRtrvStruct.setIdPhone(personPhone.getIdPersonPhone());
				persListRtrvStruct.setCdPhoneType(personPhone.getCdPersonPhoneType());
				persListRtrvStruct.setNbrPhone(personPhone.getPersonPhone());
				persListRtrvStruct.setNbrPhoneExtension(personPhone.getPersonPhoneExtension());
				persListRtrvStruct.setDtPersonPhoneStart(personPhone.getDtPersonPhoneStart());
				persListRtrvStruct.setDtPersonPhoneEnd(personPhone.getDtPersonPhoneEnd());
				persListRtrvStruct.setIndPersonPhoneInvalid(personPhone.getIndPersonPhoneInvalid());
				persListRtrvStruct.setPhoneComments(personPhone.getPersonPhoneComments());
			}
		}
		List<PersListRtrvDto> personList = personListMap.entrySet().stream().map(x -> x.getValue())
				.collect(Collectors.toList());
		persListRes.setPersonList(personList);
		persListRes.setTransactionId(persListInRec.getTransactionId());
		log.info("TransactionId :" + persListInRec.getTransactionId());
		return persListRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.intake.service.CallInfoService#getFacilDetail(org.
	 * tx.us.dfps.impact.request.StageidIn)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FacilRtrvRes getFacilDetail(CommonStageIdReq stageidIn) {
		FacilRtrvRes facilRtrvRes = new FacilRtrvRes();
		facilRtrvRes.setTransactionId(stageidIn.getTransactionId());
		return stageDao.getFacilityDetail(stageidIn.getUlIdStage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.intake.service.CallInfoService#updtFacilityDetail(
	 * org.tx.us.dfps.impact.request.FacDetailUpdtReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String updtFacilityDetail(FacDetailUpdtReq facDetailUpdtReq) {
		String status = null;
		IncomingFacility inc = dtoToEntityMapper(facDetailUpdtReq);
		status = incomingFacilityDao.updtFacilityDetail(inc, facDetailUpdtReq.getReqFuncCd());
		return status;
	}

	/**
	 * Method Description:
	 * 
	 * @param facDetailUpdtReq
	 * @return
	 */
	private IncomingFacility dtoToEntityMapper(FacDetailUpdtReq facDetailUpdtReq) {
		IncomingFacility inc = new IncomingFacility();
		inc.setIdStage(facDetailUpdtReq.getUlIdStage());
		Date dtLastUpdate = new Date();
		inc.setDtLastUpdate(dtLastUpdate);
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getUlIdResource())) {
			CapsResource capsResource = new CapsResource();
			capsResource.setIdResource(facDetailUpdtReq.getUlIdResource());
			inc.setCapsResource(capsResource);
		}
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getbIndIncmgFacilAbSupvd()))
			inc.setIndIncmgFacilAbSupvd(facDetailUpdtReq.getbIndIncmgFacilAbSupvd());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getbIndIncmgFacilSearch()))
			inc.setIndIncmgFacilSearch(facDetailUpdtReq.getbIndIncmgFacilSearch());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getbIndIncmgOnGrnds()))
			inc.setIndIncmgFacilOffGrnds(facDetailUpdtReq.getbIndIncmgOnGrnds());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzAddrIncmgFacilCity()))
			inc.setAddrIncmgFacilCity(facDetailUpdtReq.getSzAddrIncmgFacilCity());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzAddrIncmgFacilStLn1()))
			inc.setAddrIncmgFacilStLn1(facDetailUpdtReq.getSzAddrIncmgFacilStLn1());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzAddrIncmgFacilStLn2()))
			inc.setAddrIncmgFacilStLn2(facDetailUpdtReq.getSzAddrIncmgFacilStLn2());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzAddrIncmgFacilZip()))
			inc.setAddrIncmgFacilZip(facDetailUpdtReq.getSzAddrIncmgFacilZip());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzCdIncFacilOperBy()))
			inc.setCdIncmgFacilOperBy(facDetailUpdtReq.getSzCdIncFacilOperBy());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzCdIncmgFacilCnty()))
			inc.setCdIncmgFacilCnty(facDetailUpdtReq.getSzCdIncmgFacilCnty());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzCdIncmgFacilState()))
			inc.setCdIncmgFacilState(facDetailUpdtReq.getSzCdIncmgFacilState());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzCdIncmgFacilType()))
			inc.setCdIncmgFacilType(facDetailUpdtReq.getSzCdIncmgFacilType());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzNbrIncmgFacilPhone()))
			inc.setNbrIncmgFacilPhone(facDetailUpdtReq.getSzNbrIncmgFacilPhone());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzNbrIncmgFacilPhoneExt()))
			inc.setNbrIncmgFacilPhoneExt(facDetailUpdtReq.getSzNbrIncmgFacilPhoneExt());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzNmIncmgFacilAffiliated()))
			inc.setNmIncmgFacilAffiliated(facDetailUpdtReq.getSzNmIncmgFacilAffiliated());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzNmIncmgFacilSuprtdant()))
			inc.setNmIncmgFacilSuprtdant(facDetailUpdtReq.getSzNmIncmgFacilSuprtdant());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzNmUnitWard()))
			inc.setNmIncmgFacilUnitWard(facDetailUpdtReq.getSzNmUnitWard());
		if (!TypeConvUtil.isNullOrEmpty(facDetailUpdtReq.getSzTxtFacilCmnts()))
			inc.setTxtIncomingFacilCmnts(facDetailUpdtReq.getSzTxtFacilCmnts());
		return inc;
	}
}
