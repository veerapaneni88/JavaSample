package us.tx.state.dfps.service.contacts.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.CommonIndEnum;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FetchContactGuideReq;
import us.tx.state.dfps.service.contact.dto.ContactCFDto;
import us.tx.state.dfps.service.contact.dto.ContactDetailDto;
import us.tx.state.dfps.service.contact.dto.ContactFetchDto;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;
import us.tx.state.dfps.service.contacts.dao.ContactGuideDao;
import us.tx.state.dfps.service.contacts.service.ContactGuideService;
import us.tx.state.dfps.service.handwriting.service.HandWritingService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implements
 * methods from ContactGuideBean. Sep 6, 2017- 9:39:52 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class ContactGuideServiceImpl implements ContactGuideService {

	@Autowired
	ContactGuideDao contactGuideDao;

	@Autowired
	HandWritingService handWritingService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: updatecontact Method Description: Method to
	 * add/update/delete Contact guide information.
	 * 
	 * @param contactDetailDreq
	 * @return ContactDetailDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactDetailDto updatecontact(ContactDetailDto contactDetailDreq) {
		if (contactDetailDreq.getCntPurpose().equals(ServiceConstants.CCNTPURP_GFTF)
				|| contactDetailDreq.getCntPurpose().equals(ServiceConstants.CCNTPURP_GCMR)) {
			cntPurposeTrueCheck(contactDetailDreq);
		} else { // If contactGuideList is not null and Contact Purpose is other
					// than GFTF it means that Guide Topic info should be
					// deleted.
			if (contactDetailDreq.getContactGuideList() != null && contactDetailDreq.getContactGuideList().size() > 0) {
				deleteGuidePlanInfo(contactDetailDreq);
			}
		}
		return contactDetailDreq;
	}

	/**
	 * Method Name: cntPurposeTrueCheck Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactDetailDreq
	 * 
	 */
	private void cntPurposeTrueCheck(ContactDetailDto contactDetailDreq) {
		if (contactDetailDreq.getCntType().equals(ServiceConstants.CCNTCTYP_GREG)
				|| contactDetailDreq.getCntType().equals(ServiceConstants.CCNTCTYP_BREG)) {
			if (contactDetailDreq.getContactGuideList() != null && contactDetailDreq.getContactGuideList().size() > 0) {
				List<ContactGuideDto> contactGuideDtos = contactDetailDreq.getContactGuideList();
				for (ContactGuideDto contactGuideDto : contactGuideDtos) {
					contactGuideDto = txtGuidePlanTypeCheck(contactDetailDreq, contactGuideDto);
				}
			}
		}
	}

	/**
	 * Method Name: txtGuidePlanTypeCheck Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactDetailDreq
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */
	private ContactGuideDto txtGuidePlanTypeCheck(ContactDetailDto contactDetailDreq, ContactGuideDto contactGuideDto) {
		contactGuideDto.setIdEvent(contactDetailDreq.getIdEvent());
		contactGuideDto.setIdCase(contactDetailDreq.getIdCase());

		// if principal is Parent or Child
		if (contactGuideDto.getIdEvent() > 0
				&& (null != contactGuideDto.getGuidePlan()
						&& !contactGuideDto.getGuidePlan().equals(ServiceConstants.EMPTY_STRING))
				&& !contactGuideDto.getGuideplanType().equals(ServiceConstants.CGTXTTYP_COL)
				&& !contactGuideDto.getGuideplanType().equals(ServiceConstants.CGTXTTYP_CGVR)) {
			contactGuideDto = txtGuidePlanTypeOther(contactGuideDto);
		} else if (contactGuideDto.getGuideplanType().equals(ServiceConstants.CGTXTTYP_COL)) {
			txtGuidePlanTypeCol(contactGuideDto);
		} else if (contactGuideDto.getGuideplanType().equals(ServiceConstants.CGTXTTYP_CGVR)) {
			txtGuidePlanTypeCgvr(contactGuideDto);
		}

		return contactGuideDto;
	}

	/**
	 * Method Name: txtGuidePlanTypeOther Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */

	private ContactGuideDto txtGuidePlanTypeOther(ContactGuideDto contactGuideDto) {
		if (contactGuideDto.getCdOperation().equals(ServiceConstants.ADD)) {
			contactGuideDto = cdOperationAddCheck(contactGuideDto);
		} else if (contactGuideDto.getCdOperation().equals(ServiceConstants.DELETE)) {
			cdOperationDeleteCheck(contactGuideDto);
		} else if (contactGuideDto.getCdOperation().equals(ServiceConstants.UPDATE)) {
			cdOperationUpdateCheck(contactGuideDto);
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: txtGuidePlanTypeCgvr Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 */
	private void txtGuidePlanTypeCgvr(ContactGuideDto contactGuideDto) {
		if (contactGuideDto.getCdOperation().equals(ServiceConstants.ADD)) {
			txtGuideCgvrAddCheck(contactGuideDto);
		} else if (contactGuideDto.getCdOperation().equals(ServiceConstants.UPDATE)) {
			if (null != contactGuideDto.getGuidePlan()) {
				/*
				 * When guideBean comes in for an Update without a Narrative
				 * then the record needs to be deleted. Hence, if narrative
				 * exists then Update else Delete. Moved the Handwriting related
				 * code to else block.
				 */
				contactGuideDao.updateContactGuidePlan(contactGuideDto);
			}
			contactGuideDao.deleteContactGuideTopic(contactGuideDto);
			if (!contactGuideDto.getSelectedGuideTopics().isEmpty()
					&& contactGuideDto.getSelectedGuideTopics() != null) {
				List<String> selectedGuideTopics = contactGuideDto.getSelectedGuideTopics();
				for (String guideTopic : selectedGuideTopics) {
					contactGuideDao.saveCaregvrGuideTopics(contactGuideDto, guideTopic); // save
																							// caregiver
																							// guide
																							// topics
				}
			}
		}
	}

	/**
	 * Method Name: txtGuideCgvrAddCheck Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 * 
	 */
	private void txtGuideCgvrAddCheck(ContactGuideDto contactGuideDto) {
		/*
		 * A Caregiver record always needs to exist. This is because the Primary
		 * Key of the Caregiver record is needed to fetch the related Guide
		 * topics. There could be a scenario where there is no Guide Narrative
		 * for Caregiver but it may contain some Guide Topics.
		 */
		contactGuideDao.saveNarrColCargvr(contactGuideDto); // save caregiver
															// narrative
		if (!contactGuideDto.getSelectedGuideTopics().isEmpty() && contactGuideDto.getSelectedGuideTopics() != null) {
			List<String> selectedGuideTopics = contactGuideDto.getSelectedGuideTopics();
			for (String guideTopic : selectedGuideTopics) {
				contactGuideDao.saveCaregvrGuideTopics(contactGuideDto, guideTopic); // save
																						// caregiver
																						// guide
																						// topics

			}
		}
	}

	/**
	 * Method Name: txtGuidePlanTypeCol Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 */

	private void txtGuidePlanTypeCol(ContactGuideDto contactGuideDto) {
		if (contactGuideDto.getCdOperation().equals(ServiceConstants.ADD) && null != contactGuideDto.getGuidePlan()
				&& !contactGuideDto.getGuidePlan().equals(ServiceConstants.EMPTY_STRING)) {
			contactGuideDao.saveNarrColCargvr(contactGuideDto);
		} else if (contactGuideDto.getCdOperation().equals(ServiceConstants.UPDATE)
				&& contactGuideDto.getIdContactGuideNarr() > 0) {
			contactGuideDao.updateContactGuidePlan(contactGuideDto);
		} else if (contactGuideDto.getCdOperation().equals(ServiceConstants.DELETE)) {
			contactGuideDao.deleteContactGuidePlan(contactGuideDto);
		}
	}

	/**
	 * Method Name: cdOperationUpdateCheck Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 */

	private void cdOperationUpdateCheck(ContactGuideDto contactGuideDto) {
		contactGuideDao.updateContactGuidePlan(contactGuideDto);
		contactGuideDao.deleteContactGuideTopic(contactGuideDto);
		if (!contactGuideDto.getSelectedGuideTopics().isEmpty() && contactGuideDto.getSelectedGuideTopics() != null) {
			List<String> selectedGuideTopics = contactGuideDto.getSelectedGuideTopics();
			for (String guideTopic : selectedGuideTopics) {
				contactGuideDao.saveGuideTopics(contactGuideDto, guideTopic);
			}
		}
	}

	/**
	 * Method Name: cdOperationDeleteCheck Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 */

	private void cdOperationDeleteCheck(ContactGuideDto contactGuideDto) {
		contactGuideDao.deleteContactGuideTopic(contactGuideDto);
		contactGuideDao.deleteContactGuidePlan(contactGuideDto);
	}

	/**
	 * Method Name: cdOperationAddCheck Method Description: Helps in
	 * updateContact
	 * 
	 * @param contactGuideDto
	 * @return ContactGuideDto
	 */

	private ContactGuideDto cdOperationAddCheck(ContactGuideDto contactGuideDto) {
		contactGuideDto = contactGuideDao.saveGuidePlanForPrincipal(contactGuideDto);
		if (!contactGuideDto.getSelectedGuideTopics().isEmpty() && contactGuideDto.getSelectedGuideTopics() != null) {
			List<String> selectedGuideTopics = contactGuideDto.getSelectedGuideTopics();
			for (String guideTopic : selectedGuideTopics) {
				contactGuideDao.saveGuideTopics(contactGuideDto, guideTopic);
			}
		}
		return contactGuideDto;
	}

	/**
	 * Method Name: deleteGuidePlanInfo Method Description: Method to delete the
	 * Contact guide information when Contact is deleted.
	 * 
	 * @param ContactDetailDto
	 * @return ContactDetailDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ContactDetailDto deleteGuidePlanInfo(ContactDetailDto contactDetailDreq) {

		if (!CollectionUtils.isEmpty(contactDetailDreq.getContactGuideList())) {
			contactDetailDreq.getContactGuideList().forEach(contactGuideDto -> {
				contactGuideDto.setIdEvent(contactDetailDreq.getIdEvent());
				contactGuideDto.setIdCase(contactDetailDreq.getIdCase());
				if (!ObjectUtils.isEmpty(contactGuideDto.getIdContactGuideNarr())
						&& contactGuideDto.getIdContactGuideNarr() > 0) {
					// delete the Contact Guide Topics
					contactGuideDao.deleteContactGuideTopic(contactGuideDto);
					// delete the Contact Guide Narrative
					contactGuideDao.deleteContactGuidePlan(contactGuideDto);
				}
			});

		}

		return contactDetailDreq;
	}

	/**
	 * Method Name: fetchGuideTopicDescr Method Description: Method retrieves
	 * Guide Topic Description
	 * 
	 * @return ContactDetailDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<ContactFetchDto> fetchGuideTopicDescr() {
		return contactGuideDao.fetchGuideTopicDescr();
	}

	/**
	 * Method Name: checkIfGuideNarrExists Method Description: Method to check
	 * if Contact Guide Narrative records exist for a given Contact.
	 * 
	 * @param idEvent
	 * @return boolean
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean checkIfGuideNarrExists(long idEvent) {
		boolean indNarrExists = false;
		indNarrExists = contactGuideDao.checkifGuideNarrExists(idEvent);
		return indNarrExists;
	}

	/**
	 * Method Name: fetchContactGuideList Method Description: Method to retrieve
	 * all the Contact guide information for saved records/ to create a List of
	 * Contact Guide value beans for a new contact.
	 * 
	 * @param FetchContactGuideReq
	 * @return List<ContactGuideDto>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public List<ContactGuideDto> fetchContactGuideList(FetchContactGuideReq fetchContactGuideReq) {
		List<ContactGuideDto> contactGuideDtos = new ArrayList<>();
		String caregiverTopics[] = { "160", "190", "200", "210", "220", "230", "180", "150" };
		List guideTopicsForCaregiver = new ArrayList(Arrays.asList(caregiverTopics));
		String childTopics[] = { "010", "020", "030", "040", "050", "060", "070", "080", "090" };
		List guideTopicsForChild = new ArrayList(Arrays.asList(childTopics));
		String parentTopics[] = { "100", "110", "120", "130", "140", "170" };
		List guideTopicForParent = new ArrayList(Arrays.asList(parentTopics));
		long idEvent = fetchContactGuideReq.getIdEvent();
		if (!CollectionUtils.isEmpty(fetchContactGuideReq.getContactCFDtoList())) {
			for (ContactCFDto contactCFDto : fetchContactGuideReq.getContactCFDtoList()) {
				ContactGuideDto contactGuideDto = new ContactGuideDto();
				contactGuideDto.setIndChild(CommonIndEnum.valueOf(ServiceConstants.NO));
				contactGuideDto.setIndParent(CommonIndEnum.valueOf(ServiceConstants.NO));
				contactGuideDto.setIndContactOccurred(CommonIndEnum.valueOf(ServiceConstants.NO));
				contactGuideDto.setPersonFullNm(contactCFDto.getSzNmPersonFull());
				if (!StringUtils.isEmpty(contactCFDto.getcSysIndContactOccurred()))
					contactGuideDto
							.setIndContactOccurred(CommonIndEnum.valueOf(contactCFDto.getcSysIndContactOccurred()));
				contactGuideDto.setCdOperation(ServiceConstants.ADD);
				contactGuideDto.setIdEvent(idEvent);
				contactGuideDto.setIdStage(fetchContactGuideReq.getIdStage());
				contactGuideDto.setIdPerson(Long.valueOf(contactCFDto.getUlIdPerson()));
				contactGuideDao.isPrincipalParent(contactGuideDto);

				checkStageOpen(contactGuideDto);

				if (!ObjectUtils.isEmpty(contactGuideDto.getIndChild())
						&& contactGuideDto.getIndChild().compareTo(CommonIndEnum.valueOf(ServiceConstants.YES)) == 0) {
					contactGuideDto.setGuideTopicsForPrinc(guideTopicsForChild);
				} else if (!ObjectUtils.isEmpty(contactGuideDto.getIndChild())
						&& contactGuideDto.getIndParent().compareTo(CommonIndEnum.valueOf(ServiceConstants.YES)) == 0) {
					contactGuideDto.setGuideTopicsForPrinc(guideTopicForParent);
				}
				if (idEvent > 0) {
					contactGuideDao.fetchContactGuidePlan(contactGuideDto);

					// If Contact is first saved with Puporse not GFTF then on
					// Contact update it is GFTF
					if (!ObjectUtils.isEmpty(contactGuideDto.getIdContactGuideNarr())
							&& contactGuideDto.getIdContactGuideNarr() > 0) {
						contactGuideDao.fetchGuideTopicsForPerson(contactGuideDto);
						contactGuideDto.setSavedGuideTopics(contactGuideDto.getSelectedGuideTopics());
						contactGuideDto.setCdOperation(ServiceConstants.UPDATE);
					} else {
						contactGuideDto.setCdOperation(ServiceConstants.ADD);
					}
				}
				if (ServiceConstants.MOBILE_IMPACT) {
					String sHandwritingKeyPrefix = generateTimeKey();
					contactGuideDto.setHandwritingKeyName(sHandwritingKeyPrefix);
				}
				contactGuideDtos.add(contactGuideDto);
			}
			// Adding a value bean record for collateral
			ContactGuideDto contactGuideDtoCollateral = new ContactGuideDto();
			contactGuideDtoCollateral.setGuideplanType(ServiceConstants.CGTXTTYP_COL);
			contactGuideDtoCollateral.setCdGuideRole(ServiceConstants.NULL_STRING);
			contactGuideDtoCollateral.setCdOperation(ServiceConstants.ADD);
			contactGuideDtoCollateral.setIdEvent(idEvent);
			contactGuideDtoCollateral.setIdStage(fetchContactGuideReq.getIdStage());

			if (ServiceConstants.MOBILE_IMPACT) {
				String sHandwritingKeyPrefix = generateTimeKey();
				contactGuideDtoCollateral.setHandwritingKeyName(sHandwritingKeyPrefix);
			}
			// Adding a value Bean record for Caregiver
			ContactGuideDto contactGuideDtoCaregiver = new ContactGuideDto();
			contactGuideDtoCaregiver.setCdGuideRole(ServiceConstants.CGPROLE_030);
			contactGuideDtoCaregiver.setCdOperation(ServiceConstants.ADD);
			contactGuideDtoCaregiver.setIdEvent(idEvent);
			contactGuideDtoCaregiver.setIdStage(fetchContactGuideReq.getIdStage());
			contactGuideDtoCaregiver.setPersonFullNm(ServiceConstants.CAREGVR);
			contactGuideDtoCaregiver.setGuideplanType(ServiceConstants.CGTXTTYP_CGVR);
			contactGuideDtoCaregiver.setGuideTopicsForPrinc(guideTopicsForCaregiver);
			if (0 < idEvent) {
				// if id event is greater then zero, means it is an update
				idEventCheckFetch(idEvent, contactGuideDtoCaregiver, contactGuideDtoCollateral);
			}
			if (ServiceConstants.MOBILE_IMPACT) {
				String sHandwritingKeyPrefix = generateTimeKey();
				contactGuideDtoCaregiver.setHandwritingKeyName(sHandwritingKeyPrefix);
			}
			contactGuideDtos.add(contactGuideDtoCaregiver);
			contactGuideDtos.add(contactGuideDtoCollateral);
		}
		return contactGuideDtos;
	}

	/**
	 * Method Name: idEventCheckFetch Method Description: Helps in
	 * fetchContactGuideList method
	 * 
	 * @param idEvent
	 * @param contactGuideDto
	 * @param careGvrBean
	 */
	private void idEventCheckFetch(long idEvent, ContactGuideDto contactGuideDtoCaregiver,
			ContactGuideDto contactGuideDtoCollateral) {
		contactGuideDtoCollateral = contactGuideDao.fetchGuidePlanNarr(contactGuideDtoCollateral);
		contactGuideDtoCaregiver = contactGuideDao.fetchGuidePlanNarr(contactGuideDtoCaregiver);
		if (!ObjectUtils.isEmpty(contactGuideDtoCaregiver.getIdContactGuideNarr())
				&& contactGuideDtoCaregiver.getIdContactGuideNarr() > 0) {
			contactGuideDao.fetchGuideTopicsForPerson(contactGuideDtoCaregiver);
			contactGuideDtoCollateral.setCdOperation(ServiceConstants.UPDATE);
			contactGuideDtoCaregiver.setCdOperation(ServiceConstants.UPDATE);
		} else {
			contactGuideDtoCollateral.setCdOperation(ServiceConstants.ADD);
			contactGuideDtoCaregiver.setCdOperation(ServiceConstants.ADD);
		}
	}

	/**
	 * Method Name: checkStageOpen Method Description: Checking if the stage is
	 * open or not
	 * 
	 * @param contactGuideDto
	 */
	private void checkStageOpen(ContactGuideDto contactGuideDto) {
		if (!contactGuideDao.isStageOpen(contactGuideDto)) {
			contactGuideDao.princChildPC(contactGuideDto);
			if (!ObjectUtils.isEmpty(contactGuideDto.getIndChild())
					&& !ServiceConstants.YES.equals(contactGuideDto.getIndChild().toString())) {
				contactGuideDao.princChildInfoWhenStageClose(contactGuideDto);
			}
		} else {
			contactGuideDao.princHasSUBStage(contactGuideDto);
			if (!ObjectUtils.isEmpty(contactGuideDto.getIndChild())
					&& !ServiceConstants.YES.equals(contactGuideDto.getIndChild().toString())) {
				contactGuideDao.isPrincChild(contactGuideDto);
			}
		}
	}

	/**
	 * Method Name: generateTimeKey Method Description: Generates TimeKey
	 * 
	 * @return String
	 */
	private String generateTimeKey() {
		Calendar cal = Calendar.getInstance();
		return String.valueOf(cal.getTimeInMillis());
	}

}
