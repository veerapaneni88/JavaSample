package us.tx.state.dfps.service.arservicereferral.serviceimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.arservicereferral.dao.ARServiceReferralDao;
import us.tx.state.dfps.service.arservicereferral.service.ARServiceReferralService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ARServRefDetailReq;
import us.tx.state.dfps.service.common.request.ARServRefListReq;
import us.tx.state.dfps.service.common.request.ARServiceReferralDelReq;
import us.tx.state.dfps.service.common.response.ARServRefDetailRes;
import us.tx.state.dfps.service.common.response.ARServRefListRes;
import us.tx.state.dfps.service.common.response.ARServiceReferralUpdtRes;
import us.tx.state.dfps.service.servicereferral.dto.ARServRefListDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ARServiceReferralServiceImpl for ARServiceReferral Sep 6, 2017- 8:19:34 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ARServiceReferralServiceImpl implements ARServiceReferralService {

	@Autowired
	ARServiceReferralDao arServiceReferralDao;

	/**
	 * Method Name: deleteARServiceReferral Method Description: Method to delete
	 * arService referral based on service referral id(s).
	 * 
	 * @param idServiceReferrals
	 * @param idStage
	 * @return arServiceReferralDelRes
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ARServRefListRes deleteARServiceReferral(long idServiceReferrals, long idStage) {

		arServiceReferralDao.deleteARServiceReferral(idServiceReferrals);

		long updateRes = 0;
		boolean servRef = servRefExists(idStage);
		if (!servRef) {
			updateRes = deleteEvent(idStage, ServiceConstants.CEVNTTYP_CHK);
		}
		ARServRefListRes arServRefListRes = new ARServRefListRes();
		arServRefListRes.setTotalRecCount(updateRes);
		return arServRefListRes;
	}

	/**
	 * Method Name: servRefExists Method Description:
	 * 
	 * @param idStage
	 * @return boolean
	 */
	private boolean servRefExists(long idStage) {

		return arServiceReferralDao.servRefExists(idStage);
	}

	/**
	 * Method Name: deleteEvent Method Description:
	 * 
	 * @param idStage
	 * @param cevnttypChk
	 * @return long
	 */
	private long deleteEvent(long idStage, String cevnttypChk) {

		return arServiceReferralDao.deleteEvent(idStage, cevnttypChk);

	}

	/**
	 * Method Name: updateMultipleServRefs Method Description: Updates multiple
	 * service referrals, with new comments and final outcome.
	 * 
	 * @param idServiceReferrals
	 * @param txtComments
	 * @param cdFinalOutcome
	 * @return arServiceReferralUpdtRes
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ARServiceReferralUpdtRes updateMultipleServRefs(String[] idServiceReferrals, String txtComments,
			String cdFinalOutcome) {
		ARServRefListReq arServiceReferralDto = new ARServRefListReq();
		List<ARServRefListDto> arServRefList = new ArrayList<>();
		for (String idServRef : idServiceReferrals) {
			ARServRefListDto arRefListDto = new ARServRefListDto();
			arRefListDto.setIdArServRefChklist(Long.valueOf(idServRef));
			arRefListDto.setComments(txtComments);
			arRefListDto.setCdFinalOutcome(cdFinalOutcome);
			arServRefList.addAll(arServRefList);
		}
		arServiceReferralDto.setArServRefList(arServRefList);
		arServiceReferralDao.updateMultipleServRefs(arServiceReferralDto);
		ARServiceReferralUpdtRes arServiceReferralUpdtRes = new ARServiceReferralUpdtRes();
		return arServiceReferralUpdtRes;

	}
		 

	/**
	 * Method Name: saveARServRefDetails Method Description: Method to save
	 * arService referral values.
	 * 
	 * @param arServiceReferralDto
	 * @return arServiceReferralSaveRes
	 * 
	 * @throws ParseException
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<ARServRefListDto> saveARServRefDetails(ARServRefListReq arServRefListReq) {

		ARServRefListDto arServRefListInd = null;
		Long idStage = arServRefListReq.getStageId();
		List<ARServRefListDto> arDtoList = arServRefListReq.getArServRefList();
		if (CollectionUtils.isNotEmpty(arDtoList)) {
			ARServRefListDto arDto = arDtoList.get(0);
			if (ObjectUtils.isEmpty(idStage) || 0L == (long) idStage) {
				idStage = arDto.getIdStage();
			}
		}
		if (ObjectUtils.isEmpty(idStage))
			idStage = 0L;

		List<ARServRefListDto> arServRefChklist = arServiceReferralDao.getARServRefDetails(idStage);
		if (CollectionUtils.isNotEmpty(arServRefChklist)) {
			arServRefListInd = arServRefChklist.stream()
					.filter(dto -> ServiceConstants.Y.equals(dto.getIndNoServicReferrals())).findAny().orElse(null);
		}
		if (!ObjectUtils.isEmpty(arServRefListInd)
				&& !ServiceConstants.Y.equals(arServRefListReq.getIndNoServicReferrals())) {
			arServiceReferralDao.deleteARServiceReferral(arServRefListInd.getIdArServRefChklist());
		}

		for (ARServRefListDto arServRefListDto : arDtoList) {
			// idArServRefChkList = 0 Save functionality is triggered
			if (arServRefListDto.getIdArServRefChklist() == 0) {
				arServiceReferralDao.saveARServRefDetails(arServRefListReq);
			}

			// idArServRefChkList !=0 and "ReferralType" is Empty and
			// "SubReferralType" is Empty Delete functionality is triggered
			else if (arServRefListDto.getIdArServRefChklist() != 0
					&& ServiceConstants.EMPTY_STRING.equals(arServRefListDto.getCdSrType())
					&& ServiceConstants.EMPTY_STRING.equals(arServRefListDto.getCdSrSubtype())) {

				ARServiceReferralDelReq arServiceReferralDelReq = new ARServiceReferralDelReq();
				arServiceReferralDelReq.setIdStage(arServRefListDto.getIdStage());
				arServiceReferralDelReq.setIdServiceReferrals(arServRefListDto.getIdArServRefChklist());
				deleteARServiceReferral(arServiceReferralDelReq.getIdServiceReferrals(),
						arServiceReferralDelReq.getIdStage());

			}
			// idArServRefChkList !=0 then Update functionality is triggered
			else if (arServRefListDto.getIdArServRefChklist() != 0) {

				arServiceReferralDao.updateMultipleServRefs(arServRefListReq);
			}
		}

		ARServRefListDto arServRefListDto = null;
		if (ServiceConstants.Y.equals(arServRefListReq.getIndNoServicReferrals())) {
			// remove all records if not empty for this idStage
			arServRefChklist = arServiceReferralDao.getARServRefDetails(idStage);
			if (CollectionUtils.isNotEmpty(arServRefChklist)) {
				arServRefChklist.stream().forEach(ar -> {
					arServiceReferralDao.deleteARServiceReferral(ar.getIdArServRefChklist());
				});
			}

			arServRefListDto = arServRefListReq.getArServRefListDto();
			Long idCreatedPerson = ObjectUtils.isEmpty(arServRefListInd) ? arServRefListDto.getIdLastUpdatePerson()
					: arServRefListInd.getIdCreatedPerson();
			arServRefListDto.setIdCreatedPerson(idCreatedPerson);
			List<ARServRefListDto> arServRefList = new ArrayList<>();
			arServRefList.add(arServRefListDto);
			arServRefListReq.setArServRefList(arServRefList);
			arServiceReferralDao.saveARServRefDetails(arServRefListReq);
		}

		List<ARServRefListDto> arList = arServiceReferralDao.getARServRefDetails(idStage);
		return arList;
	}

	/**
	 * Method Name: getARServRefDetails Method Description: This method fetches
	 * Service Referral Details
	 * 
	 * @param arServiceReferralDto
	 * @return List<ArServRefChklist>
	 * @throws ParseException
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ARServRefListRes getARServRefDetails(long idStage) {
		ARServRefListRes arServiceReferralRes = new ARServRefListRes();
		arServiceReferralRes.setARList( arServiceReferralDao.getARServRefDetails(idStage));
		arServiceReferralRes.setDateFamilyPlanComplete( arServiceReferralDao.getPlanCompletionDate(idStage,"IFP"));
		arServiceReferralRes.setDateSafetyPlanComplete( arServiceReferralDao.getPlanCompletionDate(idStage,"ISP"));
		return arServiceReferralRes;
	}

	/**
	 * Method Name: getARServiceReferralsDetails Method Description: This method
	 * fetches Service Referral Details
	 * 
	 * @param arServRefDetailReq
	 * @return ARServRefDetailRes
	 * @throws ParseException
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ARServRefDetailRes getARServiceReferralsDetails(ARServRefDetailReq arServRefDetailReq) {
		ARServRefDetailRes arServRefDetailRes = arServiceReferralDao
				.getARServiceReferralsDetails(arServRefDetailReq.getIdServRefChklist());
		return arServRefDetailRes;

	}

}