package us.tx.state.dfps.service.subcare.serviceimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.CD_STAGE_KIN;
import static us.tx.state.dfps.service.common.ServiceConstants.MSG_CMN_DUP_PRIMARY_CARE;
import static us.tx.state.dfps.service.common.ServiceConstants.MSG_PERS_PKC_OTHER_STAGE;
import static us.tx.state.dfps.service.common.ServiceConstants.STRING_IND_Y;
import static us.tx.state.dfps.service.common.ServiceConstants.TRUE_VAL;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.PersonDtl;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.common.response.CvsFaHomeRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.placement.dao.CvsFaHmDao;
import us.tx.state.dfps.service.placement.dto.CvsFaHomeValueDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.subcare.service.CvsFaHomeService;

/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Description: Service Layer to
 * call methods related to Subcare. Apr 20, 2017 - 5:56:40 PM
 */

@Service
@Transactional
public class CvsFaHomeServiceImpl implements CvsFaHomeService {

	@Autowired
	PersonDao personDtlDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CvsFaHmDao cvsFaHmDao;

	/**
	 * 
	 * Method Description: Method to retrieve Person details to populate the CVS
	 * Home window. This method is also called in the save and update
	 * functionalities. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CvsFaHomeRes getCvsFaHomeDetails(CvsFaHomeReq cvsFaHomeReq) {

		PersonDtlDto personDtlDto = null;
		CvsFaHomeRes cvsFaHomeRes = new CvsFaHomeRes();
		StgPersonLinkDto stgPersonLinkDto = new StgPersonLinkDto();

		if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIdPerson())) {
			Long ulIdPerson = cvsFaHomeReq.getPersonDtlDto().getIdPerson();
			personDtlDto = personDtlDao.searchPersonDtlById(ulIdPerson);
			cvsFaHomeRes.setPersonDtlDto(personDtlDto);

			if (null != cvsFaHomeReq.getStagePersonLinkDto().getIdStage()) {
				stgPersonLinkDto = stagePersonLinkDao.getStagePersonLinkDetails(
						cvsFaHomeReq.getPersonDtlDto().getIdPerson(),
						cvsFaHomeReq.getStagePersonLinkDto().getIdStage());
				cvsFaHomeRes.setStgPersonLinkDto(stgPersonLinkDto);

				String isKin = stagePersonLinkDao.getIsKin(cvsFaHomeReq);
				String isExistKin = stagePersonLinkDao.getIsExistPrimaryKin(cvsFaHomeReq);
				Long resourceId = capsResourceDao.getResourceId(cvsFaHomeReq);

				cvsFaHomeRes.setIsKin(isKin);
				cvsFaHomeRes.setIsExistKin(isExistKin);

				if (!TypeConvUtil.isNullOrEmpty(resourceId)) {
					cvsFaHomeRes.setIdResource(resourceId);
				}

			}
		}

		return cvsFaHomeRes;

	}

	/**
	 * 
	 * Method Description: Method to save person details in the CVS Home window.
	 * This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CvsFaHomeRes saveCvsFaHome(CvsFaHomeReq cvsFaHomeReq) {

		PersonDtl personDtl = new PersonDtl();
		PersonDtlDto personDtlDto = new PersonDtlDto();
		CvsFaHomeRes cvsFaHomeRes = new CvsFaHomeRes();

		if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto())) {

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIdPerson()))
				personDtl.setIdPerson(cvsFaHomeReq.getPersonDtlDto().getIdPerson());
			personDtl.setDtLastUpdate(new Date());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getAmtPersonAnnualIncome()))
				personDtl.setAmtPersonAnnualIncome(cvsFaHomeReq.getPersonDtlDto().getAmtPersonAnnualIncome());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCity()))
				personDtl.setCdPersonBirthCity(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCity());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCountry()))
				personDtl.setCdPersonBirthCountry(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCountry());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCounty()))
				personDtl.setCdPersonBirthCounty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCounty());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthState()))
				personDtl.setCdPersonBirthState(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthState());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonCitizenship()))
				personDtl.setCdPersonCitizenship(cvsFaHomeReq.getPersonDtlDto().getCdPersonCitizenship());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonEyeColor()))
				personDtl.setCdPersonEyeColor(cvsFaHomeReq.getPersonDtlDto().getCdPersonEyeColor());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonFaHomeRole()))
				personDtl.setCdPersonFaHomeRole(cvsFaHomeReq.getPersonDtlDto().getCdPersonFaHomeRole());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonHairColor()))
				personDtl.setCdPersonHairColor(cvsFaHomeReq.getPersonDtlDto().getCdPersonHairColor());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonHighestEduc()))
				personDtl.setCdPersonHighestEduc(cvsFaHomeReq.getPersonDtlDto().getCdPersonHighestEduc());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIndPersonNoUsBrn()))
				personDtl.setIndPersonNoUsBrn(cvsFaHomeReq.getPersonDtlDto().getIndPersonNoUsBrn());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getNmPersonLastEmployer()))
				personDtl.setNmPersonLastEmployer(cvsFaHomeReq.getPersonDtlDto().getNmPersonLastEmployer());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getNmPersonMaidenName()))
				personDtl.setNmPersonMaidenName(cvsFaHomeReq.getPersonDtlDto().getNmPersonMaidenName());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightFeet()))
				personDtl.setQtyPersonHeightFeet(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightFeet());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightInches()))
				personDtl.setQtyPersonHeightInches(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightInches());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getQtyPersonWeight()))
				personDtl.setQtyPersonWeight(cvsFaHomeReq.getPersonDtlDto().getQtyPersonWeight());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd()))
				personDtl.setCdRemovalMothrMarrd(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdEverAdopted()))
				personDtl.setCdEverAdopted(cvsFaHomeReq.getPersonDtlDto().getCdEverAdopted());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtMostRecentAdoption()))
				personDtl.setDtMostRecentAdoption(cvsFaHomeReq.getPersonDtlDto().getDtMostRecentAdoption());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdAgencyAdoption()))
				personDtl.setCdAgencyAdoption(cvsFaHomeReq.getPersonDtlDto().getCdAgencyAdoption());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdEverAdoptInternatl()))
				personDtl.setCdEverAdoptInternatl(cvsFaHomeReq.getPersonDtlDto().getCdEverAdoptInternatl());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIndAdoptDateUnknown()))
				personDtl.setIndAdoptDateUnknown(cvsFaHomeReq.getPersonDtlDto().getIndAdoptDateUnknown());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdDocType()))
				personDtl.setCdDocType(cvsFaHomeReq.getPersonDtlDto().getCdDocType());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtUsEntry()))
				personDtl.setDtUsEntry(cvsFaHomeReq.getPersonDtlDto().getDtUsEntry());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier()))
				personDtl.setTxtAlienDocIdentifier(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtAlienStatusExpiration()))
				personDtl.setDtAlienStatusExpiration(cvsFaHomeReq.getPersonDtlDto().getDtAlienStatusExpiration());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtUsEntry()))
				personDtl.setDtUsEntry(cvsFaHomeReq.getPersonDtlDto().getDtUsEntry());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd()))
				personDtl.setCdRemovalMothrMarrd(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdDocType()))
				personDtl.setCdDocType(cvsFaHomeReq.getPersonDtlDto().getCdDocType());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtAlienStatusExpiration()))
				personDtl.setDtAlienStatusExpiration(cvsFaHomeReq.getPersonDtlDto().getDtAlienStatusExpiration());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier()))
				personDtl.setTxtAlienDocIdentifier(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier());

			personDtlDto = personDtlDao.saveCvsFaHome(personDtl, cvsFaHomeReq);

		}

		cvsFaHomeRes.setPersonDtlDto(personDtlDto);

		return cvsFaHomeRes;
	}

	/**
	 * 
	 * Method Description: Method to update person details in the CVS Home
	 * window. This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)

	public CvsFaHomeRes updateCvsFaHome(CvsFaHomeReq cvsFaHomeReq) {

		PersonDtl personDtl = new PersonDtl();
		PersonDtlDto personDtlDto = new PersonDtlDto();
		StgPersonLinkDto stagePersonLinkDto = new StgPersonLinkDto();
		CvsFaHomeRes cvsFaHomeRes = new CvsFaHomeRes();
		StagePersonLink stagePersonLink = new StagePersonLink();
		Date date = new Date();

		Long resourceId = null;
		if (null != cvsFaHomeReq.getStagePersonLinkDto().getIdStage()) {
			resourceId = capsResourceDao.getResourceId(cvsFaHomeReq);
		}

		if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto())) {

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIdPerson()))
				personDtl.setIdPerson(cvsFaHomeReq.getPersonDtlDto().getIdPerson());
			personDtl.setDtLastUpdate(new Date());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getAmtPersonAnnualIncome()))
				personDtl.setAmtPersonAnnualIncome(cvsFaHomeReq.getPersonDtlDto().getAmtPersonAnnualIncome());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCity()))
				personDtl.setCdPersonBirthCity(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCity());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCountry()))
				personDtl.setCdPersonBirthCountry(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCountry());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCounty()))
				personDtl.setCdPersonBirthCounty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthCounty());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthState()))
				personDtl.setCdPersonBirthState(cvsFaHomeReq.getPersonDtlDto().getCdPersonBirthState());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonCitizenship()))
				personDtl.setCdPersonCitizenship(cvsFaHomeReq.getPersonDtlDto().getCdPersonCitizenship());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonEyeColor()))
				personDtl.setCdPersonEyeColor(cvsFaHomeReq.getPersonDtlDto().getCdPersonEyeColor());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonFaHomeRole()))
				personDtl.setCdPersonFaHomeRole(cvsFaHomeReq.getPersonDtlDto().getCdPersonFaHomeRole());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonHairColor()))
				personDtl.setCdPersonHairColor(cvsFaHomeReq.getPersonDtlDto().getCdPersonHairColor());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdPersonHighestEduc()))
				personDtl.setCdPersonHighestEduc(cvsFaHomeReq.getPersonDtlDto().getCdPersonHighestEduc());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIndPersonNoUsBrn()))
				personDtl.setIndPersonNoUsBrn(cvsFaHomeReq.getPersonDtlDto().getIndPersonNoUsBrn());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getNmPersonLastEmployer()))
				personDtl.setNmPersonLastEmployer(cvsFaHomeReq.getPersonDtlDto().getNmPersonLastEmployer());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getNmPersonMaidenName()))
				personDtl.setNmPersonMaidenName(cvsFaHomeReq.getPersonDtlDto().getNmPersonMaidenName());
			if (!ObjectUtils.isEmpty(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightFeet())
					&& ServiceConstants.Zero_INT < cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightFeet())
				personDtl.setQtyPersonHeightFeet(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightFeet());
			if (!ObjectUtils.isEmpty(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightInches())
					&& ServiceConstants.Zero_INT < cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightInches())
				personDtl.setQtyPersonHeightInches(cvsFaHomeReq.getPersonDtlDto().getQtyPersonHeightInches());
			if (!ObjectUtils.isEmpty(cvsFaHomeReq.getPersonDtlDto().getQtyPersonWeight())
					&& ServiceConstants.Zero_INT < cvsFaHomeReq.getPersonDtlDto().getQtyPersonWeight())
				personDtl.setQtyPersonWeight(cvsFaHomeReq.getPersonDtlDto().getQtyPersonWeight());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd()))
				personDtl.setCdRemovalMothrMarrd(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdEverAdopted()))
				personDtl.setCdEverAdopted(cvsFaHomeReq.getPersonDtlDto().getCdEverAdopted());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtMostRecentAdoption()))
				personDtl.setDtMostRecentAdoption(cvsFaHomeReq.getPersonDtlDto().getDtMostRecentAdoption());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdAgencyAdoption()))
				personDtl.setCdAgencyAdoption(cvsFaHomeReq.getPersonDtlDto().getCdAgencyAdoption());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdEverAdoptInternatl()))
				personDtl.setCdEverAdoptInternatl(cvsFaHomeReq.getPersonDtlDto().getCdEverAdoptInternatl());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIndAdoptDateUnknown()))
				personDtl.setIndAdoptDateUnknown(cvsFaHomeReq.getPersonDtlDto().getIndAdoptDateUnknown());
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdDocType()))
				personDtl.setCdDocType(cvsFaHomeReq.getPersonDtlDto().getCdDocType());

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier()))
				personDtl.setTxtAlienDocIdentifier(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier());

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtUsEntry())) {

				date = cvsFaHomeReq.getPersonDtlDto().getDtUsEntry();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				// cal.add(Calendar.DATE, 1);
				date = cal.getTime();
				personDtl.setDtUsEntry(date);
			}

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd()))
				personDtl.setCdRemovalMothrMarrd(cvsFaHomeReq.getPersonDtlDto().getRemovalMothrMarrd());

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getCdDocType()))
				personDtl.setCdDocType(cvsFaHomeReq.getPersonDtlDto().getCdDocType());

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getDtAlienStatusExpiration())) {

				date = cvsFaHomeReq.getPersonDtlDto().getDtAlienStatusExpiration();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				// cal.add(Calendar.DATE, 1);
				date = cal.getTime();
				personDtl.setDtAlienStatusExpiration(date);
			}

			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier()))
				personDtl.setTxtAlienDocIdentifier(cvsFaHomeReq.getPersonDtlDto().getAlienDocIdentifier());

			CapsResource capsResource = null;
			if (null != resourceId) {
				cvsFaHomeReq.getCapsResourceDto().setIdResource(resourceId);
				capsResource = capsResourceDao.getCapsResourceById(resourceId);
			}

			if (CD_STAGE_KIN.equals(cvsFaHomeReq.getCdStage())) {
				if (STRING_IND_Y.equals(cvsFaHomeReq.getStagePersonLinkDto().getIndKinPrCaregiver())) {

					boolean isKinship = TRUE_VAL.equals(stagePersonLinkDao.getIsKin(cvsFaHomeReq));
					boolean isExistKinship = TRUE_VAL.equals(stagePersonLinkDao.getIsExistPrimaryKin(cvsFaHomeReq));

					if (isKinship && isExistKinship) {
						throw new InvalidRequestException(messageSource.getMessage(MSG_CMN_DUP_PRIMARY_CARE,
								null, Locale.US), Long.valueOf(55334));
					} else if (isKinship && !isExistKinship) {
						throw new InvalidRequestException(messageSource.getMessage(MSG_PERS_PKC_OTHER_STAGE,
								null, Locale.US), Long.valueOf(55333));
					} else if (isExistKinship && !isKinship) {
						throw new InvalidRequestException(messageSource.getMessage(MSG_CMN_DUP_PRIMARY_CARE,
								null, Locale.US), Long.valueOf(55334));
					} else {
						// Update resource id in placement table
						updatePlacementResourceId(cvsFaHomeReq);
						// Updates resource name in caps resource table
						if (!ObjectUtils.isEmpty(capsResource)) {
							capsResource.setNmResource(cvsFaHomeReq.getCapsResourceDto().getPersonName());
						}
					}
				}
				//Updates Primary Kinship caregiver indicator in Stage Person Link table
				updatePrimaryKinshipIndicator(cvsFaHomeReq, stagePersonLink);

			}
			
			personDtlDto = personDtlDao.updateCvsFaHome(personDtl, cvsFaHomeReq);

			// Update StagePersonLink
			// artf241880 - AND Fetching the resource ID to pull the record from the CAPS_RESOURCE table and update NM_RSHS_LAST_UPDATE columns
			// with the ID of the supervisor logged in

			if (!ObjectUtils.isEmpty(capsResource)) {
					capsResource.setDtLastUpdate(new Date());
					capsResource.setNmRsrcLastUpdate(cvsFaHomeReq.getFullName());
					if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getCapsResourceDto().getIdResource())
								&& !TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getCapsResourceDto().getPersonName())) {
						capsResourceDao.updateNmResource(cvsFaHomeReq, capsResource);
					}
			}

		}

		if (null != cvsFaHomeReq.getStagePersonLinkDto().getIdStage()) {

			StgPersonLinkDto stgPersonLinkDto = new StgPersonLinkDto();
			stgPersonLinkDto = stagePersonLinkDao.getStagePersonLinkDetails(
					cvsFaHomeReq.getPersonDtlDto().getIdPerson(), cvsFaHomeReq.getStagePersonLinkDto().getIdStage());
			cvsFaHomeRes.setStgPersonLinkDto(stgPersonLinkDto);

			String isKin = stgPersonLinkDto.getIndKinPrCaregiver();
			String isExistKin = stagePersonLinkDao.getIsExistPrimaryKin(cvsFaHomeReq);

			cvsFaHomeRes.setIsKin(isKin);
			cvsFaHomeRes.setIsExistKin(isExistKin);

			if (!TypeConvUtil.isNullOrEmpty(resourceId)) {
				cvsFaHomeRes.setIdResource(resourceId);
			}
			stagePersonLinkDto.setCdStagePersRole(stgPersonLinkDto.getCdStagePersRole());
			cvsFaHomeRes.setPersonDtlDto(personDtlDto);
			cvsFaHomeRes.setStgPersonLinkDto(stagePersonLinkDto);

		}
		return cvsFaHomeRes;
	}

	private void updatePrimaryKinshipIndicator(CvsFaHomeReq cvsFaHomeReq,StagePersonLink stagePersonLink){
		if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getStagePersonLinkDto())) {
			if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIdPerson())
					&& !TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getStagePersonLinkDto().getIdStage())) {
				StgPersonLinkDto existingStgPersonLinkDto = stagePersonLinkDao.getStagePersonLinkDetails(
						cvsFaHomeReq.getPersonDtlDto().getIdPerson(), cvsFaHomeReq.getStagePersonLinkDto().getIdStage());
				if (!ObjectUtils.isEmpty(existingStgPersonLinkDto)) {
					BeanUtils.copyProperties(existingStgPersonLinkDto, stagePersonLink);
					stagePersonLink.setDtLastUpdate(new Date());
					stagePersonLink.setIndKinPrCaregiver(cvsFaHomeReq.getStagePersonLinkDto().getIndKinPrCaregiver());
					stagePersonLink.setCdStagePersRole(cvsFaHomeReq.getStagePersonLinkDto().getCdStagePersRole());
					stagePersonLinkDao.updateStagePersonLinkDetails(stagePersonLink);
				}
			}
		}
	}

	private void updatePlacementResourceId(CvsFaHomeReq cvsFaHomeReq){
		if (!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getPersonDtlDto().getIdPerson()) &&
				!TypeConvUtil.isNullOrEmpty(cvsFaHomeReq.getCapsResourceDto().getIdResource())) {
			CvsFaHomeValueDto cvsFaHomeValueDto = new CvsFaHomeValueDto();
			cvsFaHomeValueDto.setIdResource(cvsFaHomeReq.getCapsResourceDto().getIdResource());
			cvsFaHomeValueDto.setIdPerson(cvsFaHomeReq.getPersonDtlDto().getIdPerson());
			cvsFaHmDao.updateResourceId(cvsFaHomeValueDto);
		}
	}
}