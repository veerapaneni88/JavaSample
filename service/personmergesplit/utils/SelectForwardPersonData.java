package us.tx.state.dfps.service.personmergesplit.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.exception.ImpactException;
import us.tx.state.dfps.common.exception.WebException;
import us.tx.state.dfps.common.web.bean.PersonBean;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.PersonEmailValueDto;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.person.service.PersonPhoneService;
import us.tx.state.dfps.service.person.service.PersonRaceEthnicityService;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonDataDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Util class
 * for Select Forward Person Data> Jun 14, 2018- 5:31:09 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
@Transactional
public class SelectForwardPersonData {

	@Autowired
	SelectForwardPersonOtherData selectFwdCommonData;

	@Autowired
	SelectForwardPersonDataManager selectFwdPersonData;

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	PersonPhoneService personPhoneService;

	@Autowired
	PersonRaceEthnicityService personRaceEthnicityService;

	@Autowired
	LookupDao lookupDao;

	static final Logger Log = Logger.getLogger(SelectForwardPersonData.class);

	private static final String PRESENT = "Present";
	private static final String NOT_PRESENT = "Not Present";
	private static final int MAX_RACES_IN_ROW = 4;
	private static final String RACE_MIDDLE_COMMA = "  ,  ";
	private static final String RACE_END_COMMA = "  ,";
	private static final String PHONE_EXT = " Ext. ";

	public SelectForwardPersonData() {
		super();
	}

	/**
	 * Method Name: getAPS Description:Builds the APS Component with attributes
	 * ( display text , enabled/disabled, default) for ValueBean used in
	 * SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 */
	protected void setAPS(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		List personCharList;
		// Initialize the field to disabled and non defaulted
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// check if APS characteristics present
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CAP);
		} else {
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CAP, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		}
		if (CollectionUtils.isNotEmpty(personCharList))
			closedPersonAttribute = PRESENT;
		else
			closedPersonAttribute = NOT_PRESENT;
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CAP);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CAP, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			forwardPersonAttribute = PRESENT;
		else
			forwardPersonAttribute = NOT_PRESENT;
		/* Initialize forward & closed person attributes */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			selectForwardPersonValueBean.getAPS().getForwardPerson().setDisplayValue(forwardPersonAttribute);
			// set default attribute if characteristics present
			if (StringUtil.isValid(forwardPersonAttribute) && forwardPersonAttribute.equals(PRESENT)) {
				selectFwdPerDto.setForwardPersonDefault(Boolean.TRUE);
			}
		} else
			selectForwardPersonValueBean.getAPS().setForwardPerson(null);
		selectForwardPersonValueBean.getAPS().getClosedPerson().setDisplayValue(closedPersonAttribute);
		if (StringUtil.isValid(closedPersonAttribute) && closedPersonAttribute.equals(PRESENT)) {
			selectFwdPerDto.setClosedPersonDefault(Boolean.TRUE);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getAPS().getForwardPerson(),
				selectForwardPersonValueBean.getAPS().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: getChildInv Description: Builds the Child Inv Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * 
	 * @return
	 */
	protected void setChildInv(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		List personCharList;
		// Initialize the field to disabled and non defaulted
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// Check if Child Inv characteristics present
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCH);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCH, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			closedPersonAttribute = PRESENT;
		else
			closedPersonAttribute = NOT_PRESENT;
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCH);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCH, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			forwardPersonAttribute = PRESENT;
		else
			forwardPersonAttribute = NOT_PRESENT;
		/* Initialize forward & closed person attributes */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			selectForwardPersonValueBean.getChildInv().getForwardPerson().setDisplayValue(forwardPersonAttribute);
			// set default attribute if characteristics present
			if (StringUtil.isValid(forwardPersonAttribute) && forwardPersonAttribute.equals(PRESENT)) {
				selectFwdPerDto.setForwardPersonDefault(Boolean.TRUE);
			}
		} else
			selectForwardPersonValueBean.getChildInv().setForwardPerson(null);
		selectForwardPersonValueBean.getChildInv().getClosedPerson().setDisplayValue(closedPersonAttribute);
		if (StringUtil.isValid(closedPersonAttribute) && closedPersonAttribute.equals(PRESENT)) {
			selectFwdPerDto.setClosedPersonDefault(Boolean.TRUE);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getChildInv().getForwardPerson(),
				selectForwardPersonValueBean.getChildInv().getClosedPerson(), selectFwdPerDto);
	}

	/**
	 * Method Name: getParentCaretaker Description:Builds the Parent Care Taker
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 */
	protected void setParentCaretaker(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		List personCharList;
		// Initialize the field to disabled and non defaulted
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// Check if Parent Care taker characteristic present
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCT);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCT, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			closedPersonAttribute = PRESENT;
		else
			closedPersonAttribute = NOT_PRESENT;
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCT);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CCT, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			forwardPersonAttribute = PRESENT;
		else
			forwardPersonAttribute = NOT_PRESENT;
		/* Initialize forward & closed person attributes */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			selectForwardPersonValueBean.getParentCaretaker().getForwardPerson()
					.setDisplayValue(forwardPersonAttribute);
			// set default attribute if characteristics present
			if (StringUtil.isValid(forwardPersonAttribute) && forwardPersonAttribute.equals(PRESENT)) {
				selectFwdPerDto.setForwardPersonDefault(Boolean.TRUE);
			}
		} else
			selectForwardPersonValueBean.getParentCaretaker().setForwardPerson(null);
		selectForwardPersonValueBean.getParentCaretaker().getClosedPerson().setDisplayValue(closedPersonAttribute);
		if (StringUtil.isValid(closedPersonAttribute) && closedPersonAttribute.equals(PRESENT)) {
			selectFwdPerDto.setClosedPersonDefault(Boolean.TRUE);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getParentCaretaker().getForwardPerson(),
				selectForwardPersonValueBean.getParentCaretaker().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: getChildPlcmnt Description:Builds the Child Placement
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * 
	 * @return
	 * @throws ImpactException
	 */
	protected void setChildPlcmnt(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		List personCharList;
		// Initialize the field to disabled and non defaulted
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// retrieve child placement records and Check if characteristics present
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CPL);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CPL, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			closedPersonAttribute = PRESENT;
		else
			closedPersonAttribute = NOT_PRESENT;
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0)
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CPL);
		else
			personCharList = personDtlService.getPersonCharList(selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					CodesConstant.CCHRTCAT_CPL, selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B);
		if (CollectionUtils.isNotEmpty(personCharList))
			forwardPersonAttribute = PRESENT;
		else
			forwardPersonAttribute = NOT_PRESENT;
		/* Initialize forward and closed Person attributes */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			selectForwardPersonValueBean.getChildPlcmnt().getForwardPerson().setDisplayValue(forwardPersonAttribute);
			// set default attribute if characteristics present
			if (StringUtil.isValid(forwardPersonAttribute) && forwardPersonAttribute.equals(PRESENT)) {
				selectFwdPerDto.setForwardPersonDefault(Boolean.TRUE);
			}
		} else
			selectForwardPersonValueBean.getChildPlcmnt().setForwardPerson(null);
		selectForwardPersonValueBean.getChildPlcmnt().getClosedPerson().setDisplayValue(closedPersonAttribute);
		if (StringUtil.isValid(closedPersonAttribute) && closedPersonAttribute.equals(PRESENT)) {
			selectFwdPerDto.setClosedPersonDefault(Boolean.TRUE);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getChildPlcmnt().getForwardPerson(),
				selectForwardPersonValueBean.getChildPlcmnt().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: getRelationships Description: Builds the Relationship
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * 
	 * @return
	 */
	protected void setRelationships(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		List<FTPersonRelationDto> personRelatioshipList;
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// retrieve relationships for closed person
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			personRelatioshipList = personDtlService
					.selectAllRelationsOfPerson((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
		} else {
			personRelatioshipList = personDtlService.selectAllRelationsOfPerson(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
		}
		// find relationship count ecluding ones where CD_ORIGIN = 'EX'
		if (CollectionUtils.isNotEmpty(personRelatioshipList) && personRelatioshipList.stream()
				.anyMatch(persRelBean -> !CodesConstant.CRELSGOR_EX.equals(persRelBean.getCdOrigin()))) {
			closedPersonAttribute = PRESENT;
		} else
			closedPersonAttribute = NOT_PRESENT;
		// retrieve relationships for forward person
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			personRelatioshipList = personDtlService
					.selectAllRelationsOfPerson((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
		} else {
			personRelatioshipList = personDtlService.selectAllRelationsOfPerson(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
		}
		// find relationship count ecluding ones where CD_ORIGIN = 'EX'
		if (CollectionUtils.isNotEmpty(personRelatioshipList) && personRelatioshipList.stream()
				.anyMatch(persRelBean -> !CodesConstant.CRELSGOR_EX.equals(persRelBean.getCdOrigin()))) {
			forwardPersonAttribute = PRESENT;
		} else
			forwardPersonAttribute = NOT_PRESENT;
		// Initialize forward attributes
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			selectForwardPersonValueBean.getRelationships().getForwardPerson().setDisplayValue(forwardPersonAttribute);
			// set default attribute if relationships present
			if (StringUtil.isValid(forwardPersonAttribute) && PRESENT.equals(forwardPersonAttribute)) {
				selectFwdPerDto.setForwardPersonDefault(Boolean.TRUE);
			}
		} else
			selectForwardPersonValueBean.getRelationships().setForwardPerson(null);
		/* Initialize closed attributes */
		selectForwardPersonValueBean.getRelationships().getClosedPerson().setDisplayValue(closedPersonAttribute);
		// set default attribute if relationships present
		if (StringUtil.isValid(closedPersonAttribute) && closedPersonAttribute.equals(PRESENT)) {
			selectFwdPerDto.setClosedPersonDefault(Boolean.TRUE);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getRelationships().getForwardPerson(),
				selectForwardPersonValueBean.getRelationships().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: getEmail Description: Builds the Email Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 * @throws ImpactException
	 */
	protected void setEmail(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		// retrieve person Current Email Address for closed and forward person
		PersonEmailValueDto persClosedEmailValueBean = null;
		PersonEmailValueDto persForwardEmailValueBean = null;
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedEmailValueBean = personDtlService
					.getPersonPrimaryEmail((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			persForwardEmailValueBean = personDtlService
					.getPersonPrimaryEmail((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
		} else {
			persClosedEmailValueBean = personDtlService.getPersonPrimaryEmail(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
			persForwardEmailValueBean = personDtlService.getPersonPrimaryEmail(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
		}
		// init forward person display values and get string that will be used
		// in
		// commonDefaultDisableChk
		forwardPersonAttribute = setUpEmailDisplayValues(persForwardEmailValueBean);
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpEmailDisplayValues(persClosedEmailValueBean);
		selectForwardPersonValueBean.getEmail().setForwardPerson(forwardPersonAttribute);
		selectForwardPersonValueBean.getEmail().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward
		// and
		// closed person
		if (null != closedPersonAttribute && !StringUtils.isEmpty(closedPersonAttribute.getDisplayValues())) {
			String displayValue = "";
			displayValue = closedPersonAttribute.getDisplayValues().stream().map(Object::toString)
					.collect(Collectors.joining(ServiceConstants.CONST_SPACE));
			selectFwdPerDto.setClosedPersonAttribute(displayValue);
		}
		if (null != forwardPersonAttribute && !StringUtils.isEmpty(forwardPersonAttribute.getDisplayValues())) {
			String displayValue = "";
			displayValue = forwardPersonAttribute.getDisplayValues().stream().map(Object::toString)
					.collect(Collectors.joining(ServiceConstants.CONST_SPACE));
			selectFwdPerDto.setForwardPersonAttribute(displayValue);
		}
		//select email in Person merge
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getEmail().getForwardPerson(),
				selectForwardPersonValueBean.getEmail().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setUpEmailDisplayValues Description: Set's up email
	 * attributes using input PersonEmailValueBean, initializes primary key used
	 * for dirty read ,returns a string that is used to set default and disabled
	 * attribute,
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonEmailValueBean
	 * @return String
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpEmailDisplayValues(
			PersonEmailValueDto personEmailValueBean) {
		SelectForwardPersonValueBean.AttributeValueBean emailAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayValues = new ArrayList();
		if (!ObjectUtils.isEmpty(personEmailValueBean) && !ObjectUtils.isEmpty(personEmailValueBean.getCdEmailType())) {
			// set up email type
			displayValues.add(lookupDao.decode(CodesConstant.CEMLPRTY,
					StringUtil.getNonNullString(personEmailValueBean.getCdEmailType())));
			// set up email
			displayValues.add(StringUtil.getNonNullString(personEmailValueBean.getTxtEmail()));
			// setup email start date
			displayValues.add(FormattingUtils.formatDate(personEmailValueBean.getDtStart()));
			// set up Primary Key value for email record used for dirty read
			selectFwdCommonData.initPrKeyData(emailAttribute, personEmailValueBean.getIdPersonEmail(),
					personEmailValueBean.getLastUpdate());
		} else // if no email record ste up 3 empty rows
		{
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
		}
		emailAttribute.setDisplayValues(displayValues);
		return emailAttribute;
	}

	/**
	 * Method Name : getPhone Description : Builds the Phone attribute ( display
	 * text , enabled/disabled, default) for ValueBean used in
	 * SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param
	 * @return
	 */
	protected void setPhone(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		// Get primary phone record for closed and forward person
		PersonPhoneRetDto persClosedPhoneDB = null;
		PersonPhoneRetDto persForwardPhoneDB = null;
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedPhoneDB = personPhoneService
					.getPersonPrimaryActivePhone((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			persForwardPhoneDB = personPhoneService
					.getPersonPrimaryActivePhone((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
		} else {
			persClosedPhoneDB = personPhoneService.getPersonPrimaryActivePhone(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
			persForwardPhoneDB = personPhoneService.getPersonPrimaryActivePhone(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
		}
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/*
		 * Initialize forward person phone attributes and initialize string to
		 * be used in commonDefaultDisableChk
		 */
		forwardPersonAttribute = setUpPhoneDisplayValues(persForwardPhoneDB);
		// init forward person and closed person string var's, these
		// strings will be used in commonDefaultDisableChk
		closedPersonAttribute = setUpPhoneDisplayValues(persClosedPhoneDB);
		selectForwardPersonValueBean.getPhone().setForwardPerson(forwardPersonAttribute);
		selectForwardPersonValueBean.getPhone().setClosedPerson(closedPersonAttribute);
		/*
		 * Initialize closed person phone attributes and initialize string to be
		 * used in commonDefaultDisableChk
		 */
		if (null != closedPersonAttribute && !StringUtils.isEmpty(closedPersonAttribute.getDisplayValues())) {
			String displayValue = "";
			displayValue = closedPersonAttribute.getDisplayValues().stream().map(Object::toString)
					.collect(Collectors.joining(ServiceConstants.CONST_SPACE));
			selectFwdPerDto.setClosedPersonAttribute(displayValue);
		}
		if (null != forwardPersonAttribute && !StringUtils.isEmpty(forwardPersonAttribute.getDisplayValues())) {
			String displayValue = "";
			displayValue = forwardPersonAttribute.getDisplayValues().stream().map(Object::toString)
					.collect(Collectors.joining(ServiceConstants.CONST_SPACE));
			selectFwdPerDto.setForwardPersonAttribute(displayValue);
		}
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getPhone().getForwardPerson(),
				selectForwardPersonValueBean.getPhone().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setUpPhoneDisplayValues Description: Set's up the phone
	 * attributes using the input phone DB Value bean , initializes primary key
	 * used for dirty and * returns a string that is used to set default and
	 * disabled attribute.
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PhoneDB
	 * @return String
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpPhoneDisplayValues(PersonPhoneRetDto personPhoneDB) {
		SelectForwardPersonValueBean.AttributeValueBean phoneAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayValues = new ArrayList();
		if (!ObjectUtils.isEmpty(personPhoneDB) && !ObjectUtils.isEmpty(personPhoneDB.getCdPersonPhoneType())) {
			// set phone type
			displayValues.add(lookupDao.decode(CodesConstant.CPHNTYP,
					StringUtil.getNonNullString(personPhoneDB.getCdPersonPhoneType())));
			// set phone number
			String phoneNumber = FormattingUtils.formatPhone(personPhoneDB.getPersonPhone());
			if (StringUtil.isValid(personPhoneDB.getPersonPhoneExtension())) {
				phoneNumber += PHONE_EXT + personPhoneDB.getPersonPhoneExtension();
			}
			displayValues.add(phoneNumber);
			// set start date
			displayValues.add(FormattingUtils.formatDate(personPhoneDB.getDtPersonPhoneStart()));
			// set primary key data used for dirty read
			selectFwdCommonData.initPrKeyData(phoneAttribute, personPhoneDB.getIdPersonPhone().intValue(),
					personPhoneDB.getDtLastUpdate());
		} else // if no record init 3 empty strings
		{
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
		}
		phoneAttribute.setDisplayValues(displayValues);
		return phoneAttribute;
	}

	/**
	 * Method Name: getRace Description: Builds the Race Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param
	 * @return
	 */
	protected void setRace(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		ArrayList<PersonRaceDto> persClosedRace;
		ArrayList<PersonRaceDto> persForwardRace;
		// Get race records for closed and forward person
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedRace = personDtlService
					.fetchPersonRaceList(selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			persForwardRace = personDtlService
					.fetchPersonRaceList(selectFwdPerDto.getPersForwardValueBean().getIdPerson());
		} else {
			persClosedRace = (ArrayList<PersonRaceDto>) personRaceEthnicityService.getPersonRaceList(
					(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			persForwardRace = (ArrayList<PersonRaceDto>) personRaceEthnicityService.getPersonRaceList(
					(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
		}
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/*
		 * Initialize forward Race attributes and get value to compare in
		 * commonDefaultDisableChk
		 */
		forwardPersonAttribute = setUpRaceDisplayValues(selectForwardPersonValueBean, persForwardRace, Boolean.TRUE);
		/*
		 * Initialize closed Race attributes and get value to compare in
		 * commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpRaceDisplayValues(selectForwardPersonValueBean, persClosedRace, Boolean.FALSE);
		// call function to set disabled and default strings for forward
		// and closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// if forward person has Adoption Assistance eligibility disable closed
		if (selectFwdPerDto.getFwdPrsnHasAAElig()) {
			selectFwdPerDto.setClosedPersonDisabled(Boolean.TRUE);
			selectFwdPerDto.setClosedPersonDefault(Boolean.FALSE);
		}
		// closed to default
		if ((!selectFwdPerDto.getForwardPersonDisabled() && forwardPersonAttribute.equals(CodesConstant.CRACE_UD))
				&& (!selectFwdPerDto.getClosedPersonDisabled()
						&& !closedPersonAttribute.equals(CodesConstant.CRACE_UD))) {
			selectFwdPerDto.setForwardPersonDefault(Boolean.FALSE);
			selectFwdPerDto.setClosedPersonDefault(Boolean.TRUE);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getRace().getForwardPerson(),
				selectForwardPersonValueBean.getRace().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setUpRaceDisplayValues Description: Builds the display
	 * values , primary key list and comaprison strings for forward or closed
	 * race attribute
	 * 
	 * @param SelectForwardPersonValueBean
	 * @param ArrayList
	 * @return String
	 */
	protected String setUpRaceDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			ArrayList<PersonRaceDto> personRace, Boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean personRaceAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayRace = new ArrayList();
		ArrayList raceCodes = new ArrayList();
		Object[] displayRaceText;
		String displayTextValue = null;
		String raceCodeValues = null;
		// list for dirty read
		if (!ObjectUtils.isEmpty(personRace)) {
			for (PersonRaceDto personRaceValueBean : personRace) {
				// form display value and add to temp array
				displayRace.add(lookupDao.decode(CodesConstant.CRACE, personRaceValueBean.getCdPersonRace()));
				// add the primary key and date last update to the primary key
				// data list
				// of input attribute
				selectFwdCommonData.initPersonPrKeyDataList(personRaceAttribute,
						personRaceValueBean.getIdPersonRace().intValue(), personRaceValueBean.getDtRaceUpdate());
				// add race codes to temp array to be sorted
				raceCodes.add(personRaceValueBean.getCdPersonRace());
			}
			// sort temp array's by alpabetical order
			Collections.sort(displayRace);
			Collections.sort(raceCodes);
			displayRaceText = displayRace.toArray();
			displayRace = new ArrayList();
			int noRacesInRow = MAX_RACES_IN_ROW;
			int raceCnt = 0;
			// row
			for (int count = 0; count < displayRaceText.length; count++) {
				if (raceCnt == 0)
					displayTextValue = (String) displayRaceText[count];
				else
					displayTextValue += RACE_MIDDLE_COMMA + (String) displayRaceText[count];
				raceCnt += 1;
				if (raceCnt == noRacesInRow) {
					if (!((count + 1) == (displayRaceText.length)))
						displayTextValue += RACE_END_COMMA;
					displayRace.add(displayTextValue);
					raceCnt = 0;
				}
			}
			if (raceCnt > 0) {
				displayRace.add(displayTextValue);
			}
		} else // if none present add an empty string for display
		{
			displayTextValue = StringUtil.EMPTY_STRING;
			displayRace.add(displayTextValue);
		}
		// add the values to be displayed in attributes display values arraylist
		personRaceAttribute.setDisplayValues(displayRace);
		if (isFwdPerson) {
			selectForwardPersonValueBean.getRace().setForwardPerson(personRaceAttribute);
		} else {
			selectForwardPersonValueBean.getRace().setClosedPerson(personRaceAttribute);
		}
		// person for enabling and setting default
		for (int count = 0; count < raceCodes.size(); count++) {
			if (count == 0)
				raceCodeValues = (String) raceCodes.get(count);
			else
				raceCodeValues += (String) raceCodes.get(count);
		}
		return raceCodeValues;
	}

	/**
	 * Method Name: getReligion Description: Builds the Person Religion
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setReligion(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();

		/*
		 * Initialize forward Religion attribute and get string used in
		 * commonDefaultDisableChk
		 */
		forwardPersonAttribute = setUpReligionDisplayValue(selectFwdPerDto.getPersForwardValueBean());
		/*
		 * Initialize closed Religion attribute and get string used in
		 * commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpReligionDisplayValue(selectFwdPerDto.getPersClosedValueBean());
		selectForwardPersonValueBean.getReligion().setForwardPerson(forwardPersonAttribute);
		selectForwardPersonValueBean.getReligion().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person parameter passed is set to false to indicate that
		// employee/former
		// employee check is not needed
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		selectFwdCommonData.commonDefaultDisableChk(false, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getReligion().getForwardPerson(),
				selectForwardPersonValueBean.getReligion().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setUpReligionDisplayValue Description: Set's up religion
	 * attribute
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk comparison
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpReligionDisplayValue(PersonBean personValueBean) {
		SelectForwardPersonValueBean.AttributeValueBean religionAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		// if valid value bean
		if (!ObjectUtils.isEmpty(personValueBean) && !ObjectUtils.isEmpty(personValueBean.getCdReligion())) {
			// This string is used in commonDefaultDisableChk
			attributeValue = lookupDao.decode(CodesConstant.CRELIGNS,
					StringUtil.getNonNullString(personValueBean.getCdReligion()));
			// Initialize display text for first name
			religionAttribute.setDisplayValue(attributeValue);
		}
		return religionAttribute;
	}

	/**
	 * Method Name: getDOD Description:Builds the Person DOD Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setDOD(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		int forwardPersonDODRank = 0;
		int closedPersonDODRank = 0;

		/*
		 * Init DOD and Reason of Death for closed and forward person and get a
		 * rank
		 */
		forwardPersonDODRank = setDODDisplayValues(selectForwardPersonValueBean,
				selectFwdPerDto.getPersForwardValueBean(), Boolean.TRUE);
		closedPersonDODRank = setDODDisplayValues(selectForwardPersonValueBean,
				selectFwdPerDto.getPersClosedValueBean(), Boolean.FALSE);
		if ((closedPersonDODRank > 0) && (forwardPersonDODRank == 0) && selectFwdPerDto.getFwdPrsnHasElig()) {
			String message = lookupDao.getMessage(ServiceConstants.MSG_FWD_PRSON_ELIG_DOD);
			message = FormattingUtils.addMessageParameter(message, selectFwdPerDto.getMsgPrsnEligDODChk());
			selectFwdPersonData.setUpMessages(message, ServiceConstants.MSG_FWD_PRSON_ELIG_DOD,
					selectForwardPersonValueBean);
		}
		// strings will be used in commonDefaultDisableChk
		if (// init forward person
		forwardPersonDODRank > closedPersonDODRank)
			// attribute only
			forwardPersonAttribute = Integer.toString(forwardPersonDODRank);
		else if (// init closed person
		forwardPersonDODRank < closedPersonDODRank)
			// attribute only
			closedPersonAttribute = Integer.toString(closedPersonDODRank);
		else if (forwardPersonDODRank != 0 && closedPersonDODRank != 0) {
			// if both ranks are the same manipulate rank such that forward
			// person is
			// defaulted but both are enabled
			forwardPersonAttribute = Integer.toString(forwardPersonDODRank + 1);
			closedPersonAttribute = Integer.toString(closedPersonDODRank);
		}
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(false, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getDOD().getForwardPerson(),
				selectForwardPersonValueBean.getDOD().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setDODDisplayValues Description: Set's up DOD
	 * attributes(including reason of death (CPS and nonCPS) ) using input
	 * personValueBean, initializes primary key used for dirty read ,returns a
	 * rank (integer) that will be used to determine if which person will be set
	 * as default selection.
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return int
	 */
	protected int setDODDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			PersonBean personValueBean, Boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean dodAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayValues = new ArrayList();
		// String Array of Reason Death that has highest priority
		String[] firstPriority = new String[] { CodesConstant.CRSNFDTH_ABN, CodesConstant.CRSNFDTH_ABO,
				CodesConstant.CRSNFDTH_ABP, CodesConstant.CRSNFDTH_OAN, CodesConstant.CRSNFDTH_OSV };
		// SIR 1017973
		// String Array of Reason Death (CPS and Non CPS) that has second
		// priority
		String[] secondPriority = new String[] { CodesConstant.CRSNFDTH_NAB, CodesConstant.CRSNFDTH_OEA,
				CodesConstant.CRSNFDTH_OIN, CodesConstant.CRSNDTH2_NAB, CodesConstant.CRSNDTH2_NIN,
				CodesConstant.CRSNDTH2_NTC, CodesConstant.CRSNDTH2_NTD };
		// Flag to show two RFD fields
		boolean isDodAfterFlag = false;
		// Add Reason for Death (CPS) only if DOD > 9/1/2014
		// Date cfDateFlag = DateHelper.getJavaDate( 2014, 9, 01 );
		// vyasa added this
		Date cfDateFlag = null;
		DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
		try {
			cfDateFlag = df.parse(lookupDao.decode(CodesConstant.CRELDATE, CodesConstant.CRELDATE_AUG_2014_IMPACT));
		} catch (ParseException e) {
			WebException webException = new WebException();
			webException.initCause(e);
			throw webException;
		}
		if (!ObjectUtils.isEmpty(personValueBean.getDtOfDeath()) && personValueBean.getDtOfDeath().after(cfDateFlag))
			isDodAfterFlag = true;
		// intialize rank to the lowest (assumption Death of Death and Reason of
		// death not present)
		int rankDOD = 0;
		if (!ObjectUtils.isEmpty(personValueBean) && !ObjectUtils.isEmpty(personValueBean.getCdReasonForDeath())) {
			// set up DOD Display
			displayValues.add(FormattingUtils.formatDate(personValueBean.getDtOfDeath()));
			// set up Reason of Death for Display
			displayValues.add(lookupDao.decode(CodesConstant.CRSNFDTH,
					StringUtil.getNonNullString(personValueBean.getCdReasonForDeath())));
			// SIR 1017973
			if (isDodAfterFlag) {
				// set up Reason of Death (CPS) for Display
				displayValues.add(lookupDao.decode(CodesConstant.CRSNDTH2,
						StringUtil.getNonNullString(personValueBean.getCdDeathReasonCps())));
			}
			// if Date of Death present
			if (!ObjectUtils.isEmpty(personValueBean.getDtOfDeath())) {
				// if Reason of Death(Non CPS) present
				if (!ObjectUtils.isEmpty(personValueBean.getCdReasonForDeath())
						&& ObjectUtils.isEmpty(personValueBean.getCdDeathReasonCps())) {
					// sort arrays for binary serach
					Arrays.sort(firstPriority);
					Arrays.sort(secondPriority);
					// if reason of death falls in the highest priority give
					// rank of 3
					if (Arrays.binarySearch(firstPriority, personValueBean.getCdReasonForDeath()) >= 0)
						rankDOD = 3;
					else // if reason of death falls in the second priority give
							// rank of 2
					if (Arrays.binarySearch(secondPriority, personValueBean.getCdReasonForDeath()) >= 0)
						rankDOD = 2;
				} else if (!ObjectUtils.isEmpty(personValueBean.getCdDeathReasonCps())
						&& ObjectUtils.isEmpty(personValueBean.getCdReasonForDeath())) {
					// SIR 1017973
					// if Reason of Death (CPS) present
					// sort arrays for binary search
					Arrays.sort(firstPriority);
					Arrays.sort(secondPriority);
					// if reason of death falls in the highest priority give
					// rank of 3
					if (Arrays.binarySearch(firstPriority, personValueBean.getCdDeathReasonCps()) >= 0)
						rankDOD = 3;
					else // if reason of death (CPS) falls in the second
							// priority give rank of 2
					if (Arrays.binarySearch(secondPriority, personValueBean.getCdDeathReasonCps()) >= 0)
						rankDOD = 2;
				} else if (!ObjectUtils.isEmpty(personValueBean.getCdDeathReasonCps())
						&& !ObjectUtils.isEmpty(personValueBean.getCdReasonForDeath())) {
					// sort arrays for binary search
					Arrays.sort(firstPriority);
					Arrays.sort(secondPriority);
					// if reason of death (CPS & Non CPS) falls in the highest
					// priority give rank of
					// 3
					if (Arrays.binarySearch(firstPriority, personValueBean.getCdReasonForDeath()) >= 0
							&& Arrays.binarySearch(firstPriority, personValueBean.getCdDeathReasonCps()) >= 0)
						rankDOD = 4;
					else // if reason of death (CPS & Non CPS) falls in the
							// second priority give rank of
							// 2
					if (Arrays.binarySearch(secondPriority, personValueBean.getCdReasonForDeath()) >= 0
							&& Arrays.binarySearch(secondPriority, personValueBean.getCdDeathReasonCps()) >= 0)
						rankDOD = 2;
					else
						// WHAT TO DO WHEN ONE IS FIRST AND ONE IS SECOND
						// if both ranks are the same manipulate rank such that
						// forward person is
						// defaulted but both are enabled
						rankDOD = 3;
				} else
					// if only date of death present give rank of 1
					rankDOD = 1;
			}
		} else // setup blank rows if no DOD
		{
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			// SIR 1017973
			if (isDodAfterFlag)
				displayValues.add(StringUtil.EMPTY_STRING);
		}
		dodAttribute.setDisplayValues(displayValues);
		if (isFwdPerson) {
			selectForwardPersonValueBean.getDOD().setForwardPerson(dodAttribute);
		} else {
			selectForwardPersonValueBean.getDOD().setClosedPerson(dodAttribute);
		}
		return rankDOD;
	}
}
