package us.tx.state.dfps.service.personmergesplit.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.exception.ImpactException;
import us.tx.state.dfps.common.web.bean.PersonBean;
import us.tx.state.dfps.service.admin.dto.NameDto;
import us.tx.state.dfps.service.admin.service.PersonIdService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.workload.dao.AddressDao;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonDataDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Utility
 * Class for Select Forward Person May 31, 2018- 12:29:24 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
@Transactional
public class SelectForwardPersonCommonDetail {

	@Autowired
	SelectForwardPersonOtherData selectFwdCommonData;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	PersonIdService personIdService;

	@Autowired
	PersonDao personDao;

	@Autowired
	AddressDao addressDao;

	private static final String DOB_AGE = " Age: ";
	private static final String DOB_APRX = " Aprx: ";
	private static final String ADDRESS_COMMA = ", ";
	private static final String ADDRESS_SPACE = " ";
	static final Logger Log = Logger.getLogger(SelectForwardPersonCommonDetail.class);

	public SelectForwardPersonCommonDetail() {
		super();
	}

	/**
	 * Method Name: getIdPerson Description: Builds the ID Person Component for
	 * ValueBean used in SelectForwardPersonData Page. This field displays the
	 * Person id's on the page and stores the dirty page information for the
	 * person record
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param idPersonForward
	 * @param idPersonClosed
	 * @return
	 */
	protected void setIdPerson(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		SelectForwardPersonValueBean.AttributeValueBean idFwdPersonAttribute = null;
		SelectForwardPersonValueBean.AttributeValueBean idClosedPersonAttribute = null;
		/* Initialize forward IdPerson attributes */
		idFwdPersonAttribute = setUpIdPersonDisplayValue(selectForwardPersonValueBean.getIdPerson().getForwardPerson(),
				selectFwdPerDto.getPersForwardValueBean());
		/* Initialize closed IdPerson attributes */
		idClosedPersonAttribute = setUpIdPersonDisplayValue(
				selectForwardPersonValueBean.getIdPerson().getClosedPerson(), selectFwdPerDto.getPersClosedValueBean());
		selectForwardPersonValueBean.getIdPerson().setForwardPerson(idFwdPersonAttribute);
		selectForwardPersonValueBean.getIdPerson().setClosedPerson(idClosedPersonAttribute);
	}

	/**
	 * Method Name:setUpIdPersonDisplayValue Description:Set's up idPerson
	 * attribute , and primary key data for person record used for dirty read
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return
	 */
	public SelectForwardPersonValueBean.AttributeValueBean setUpIdPersonDisplayValue(
			SelectForwardPersonValueBean.AttributeValueBean idPersonAttribute, PersonBean personValueBean) {
		// if valid value bean
		if (!ObjectUtils.isEmpty(personValueBean)) {
			// set idPerson value
			idPersonAttribute.setDisplayValue(Integer.valueOf(personValueBean.getIdPerson()).toString());
			// set primary key and date last update of the person record for
			// primaryKeyData field
			selectFwdCommonData.initPrKeyData(idPersonAttribute, personValueBean.getIdPerson(),
					personValueBean.getDtPersonTableLastUpdate());
		}
		return idPersonAttribute;
	}

	/**
	 * Method Name:getPersonName Description:Builds the Person Name Components
	 * (first,middle,last, suffix) with attributes ( display text ,
	 * enabled/disabled, default) for ValueBean used in SelectForwardPersonData
	 * Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @throws ImpactException
	 */
	protected void setPersonName(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setPersonName method in SelectForwardPersonCommonDetail class");
		NameDto persClosedNameDto;
		NameDto persFwdNameDto;
		// retrieve person name for closed and forward person
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedNameDto = personDtlService
					.fetchActivePrimaryName((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			persFwdNameDto = personDtlService
					.fetchActivePrimaryName((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
		} else {
			persClosedNameDto = personDtlService.fetchActivePrimaryName(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
			persFwdNameDto = personDtlService.fetchActivePrimaryName(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
		}
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/*
		 * Initialize forward person attributes and get string that is used in
		 * commonDefaultDisableChk Pass Parameter true if is forward person and
		 * false if it is closed person as an indicator
		 */
		if (!ObjectUtils.isEmpty(persFwdNameDto)) {
			forwardPersonAttribute = setUpFirstNameDisplayValue(selectForwardPersonValueBean, persFwdNameDto, true);
			forwardPersonAttribute = setUpMiddleNameDisplayValue(selectForwardPersonValueBean, persFwdNameDto,
					forwardPersonAttribute, true);
			forwardPersonAttribute = setUpLastNameDisplayValue(selectForwardPersonValueBean, persFwdNameDto,
					forwardPersonAttribute, true);
			forwardPersonAttribute = setUpSuffixDisplayValue(selectForwardPersonValueBean, persFwdNameDto,
					forwardPersonAttribute, true);
		}

		/*
		 * Initialize closed person attributes and get string that is used in
		 * commonDefaultDisableChk
		 */
		if (!ObjectUtils.isEmpty(persClosedNameDto)) {
			closedPersonAttribute = setUpFirstNameDisplayValue(selectForwardPersonValueBean, persClosedNameDto, false);
			closedPersonAttribute = setUpMiddleNameDisplayValue(selectForwardPersonValueBean, persClosedNameDto,
					closedPersonAttribute, false);
			closedPersonAttribute = setUpLastNameDisplayValue(selectForwardPersonValueBean, persClosedNameDto,
					closedPersonAttribute, false);
			closedPersonAttribute = setUpSuffixDisplayValue(selectForwardPersonValueBean, persClosedNameDto,
					closedPersonAttribute, false);
		}

		// call function to set disabled and default strings for forward
		// and closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getFirstName().getForwardPerson(),
				selectForwardPersonValueBean.getFirstName().getClosedPerson(), selectFwdPerDto);
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getMiddleName().getForwardPerson(),
				selectForwardPersonValueBean.getMiddleName().getClosedPerson(), selectFwdPerDto);
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getLastName().getForwardPerson(),
				selectForwardPersonValueBean.getLastName().getClosedPerson(), selectFwdPerDto);
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getSuffix().getForwardPerson(),
				selectForwardPersonValueBean.getSuffix().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setPersonName method in SelectForwardPersonCommonDetail class");

	}

	/**
	 * Method Name: setUpFirstNameDisplayValue Description: Set's up firstName
	 * attribute , and primary key data for name record used for dirty read
	 * 
	 * @param isFwdPerson
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk name comparison
	 */
	protected String setUpFirstNameDisplayValue(SelectForwardPersonValueBean selectForwardPersonValueBean,
			NameDto persFwdNameDto, boolean isFwdPerson) {
		String attributeValue = null;
		SelectForwardPersonValueBean.AttributeValueBean firstNameAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		// if valid value bean
		if (null != selectForwardPersonValueBean) {
			// This string is used in commonDefaultDisableChk
			attributeValue = StringUtil.getNonNullString(persFwdNameDto.getNmNameFirst());
			// Initialize display text for first name
			firstNameAttribute.setDisplayValue(attributeValue);
			// set primary key and date last update of the name record for
			// primaryKeyData field
			selectFwdCommonData.initPrKeyData(firstNameAttribute, persFwdNameDto.getIdName().intValue(),
					persFwdNameDto.getTsLastUpdate());
			if (isFwdPerson && !ObjectUtils.isEmpty(firstNameAttribute)) {
				selectForwardPersonValueBean.getFirstName().setForwardPerson(firstNameAttribute);
			} else if (!isFwdPerson && !ObjectUtils.isEmpty(firstNameAttribute)) {
				selectForwardPersonValueBean.getFirstName().setClosedPerson(firstNameAttribute);
			}
		}
		return attributeValue;
	}

	/**
	 * Method Name: setUpMiddleNameDisplayValue Description: Set's up middleName
	 * attribute
	 * 
	 * @param isFwdPerson
	 * @param selectForwardPersonValueBean
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk name comparison
	 */
	protected String setUpMiddleNameDisplayValue(SelectForwardPersonValueBean selectForwardPersonValueBean,
			NameDto personNameDto, String attributeValue, boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean middleNameAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		// if valid value bean
		if (null != personNameDto) {
			// This string is used in commonDefaultDisableChk
			attributeValue += StringUtil.getNonNullString(personNameDto.getNmNameMiddle());
			// Initialize display text for middle name
			middleNameAttribute.setDisplayValue(StringUtil.getNonNullString(personNameDto.getNmNameMiddle()));
		}
		if (isFwdPerson) {
			selectForwardPersonValueBean.getMiddleName().setForwardPerson(middleNameAttribute);
		} else {
			selectForwardPersonValueBean.getMiddleName().setClosedPerson(middleNameAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method Name: setUpLastNameDisplayValue Description: Set's up lastName
	 * attribute
	 * 
	 * @param isFwdPerson
	 * @param selectForwardPersonValueBean
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk name comparison
	 */
	protected String setUpLastNameDisplayValue(SelectForwardPersonValueBean selectForwardPersonValueBean,
			NameDto personNameDto, String attributeValue, boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean lastNameAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		// if valid value bean
		if (null != personNameDto) {
			// This string is used in commonDefaultDisableChk
			attributeValue += StringUtil.getNonNullString(personNameDto.getNmNameLast());
			// Initialize display text for Last name
			lastNameAttribute.setDisplayValue(StringUtil.getNonNullString(personNameDto.getNmNameLast()));
		}
		if (isFwdPerson) {
			selectForwardPersonValueBean.getLastName().setForwardPerson(lastNameAttribute);
		} else {
			selectForwardPersonValueBean.getLastName().setClosedPerson(lastNameAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method Name: setUpSuffixDisplayValue Description:Set's up suffix
	 * attribute
	 * 
	 * @param isFwdPerson
	 * @param selectForwardPersonValueBean
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk name comparison
	 */
	protected String setUpSuffixDisplayValue(SelectForwardPersonValueBean selectForwardPersonValueBean,
			NameDto personNameDto, String attributeValue, boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean suffixNameAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		// if valid value bean
		if (null != personNameDto && !StringUtils.isEmpty(personNameDto.getCdNameSuffix())) {
			// This string is used in commonDefaultDisableChk
			attributeValue += lookupDao.decode(CodesConstant.CSUFFIX,
					StringUtil.getNonNullString(personNameDto.getCdNameSuffix()));
			// Initialize display text for suffix
			suffixNameAttribute.setDisplayValue(lookupDao.decode(CodesConstant.CSUFFIX,
					StringUtil.getNonNullString(personNameDto.getCdNameSuffix())));
		}
		if (isFwdPerson) {
			selectForwardPersonValueBean.getSuffix().setForwardPerson(suffixNameAttribute);
		} else {
			selectForwardPersonValueBean.getSuffix().setClosedPerson(suffixNameAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method Name:getGender Description: Builds the Person Gender Component
	 * with attributes ( display text , enabled/disabled, default) for ValueBean
	 * used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setGender(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setGender method in SelectForwardPersonCommonDetail class");
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/*
		 * Initialize forward person Gender attribute and get string used in
		 * commonDefaultDisableChk set indicator for forward person as true
		 */
		forwardPersonAttribute = setUpGenderDisplayValue(selectForwardPersonValueBean,
				selectFwdPerDto.getPersForwardValueBean(), Boolean.TRUE);
		/*
		 * Initialize closed person Gender attribute and get string used in
		 * commonDefaultDisableChk set indicator for forward person as false
		 */
		closedPersonAttribute = setUpGenderDisplayValue(selectForwardPersonValueBean,
				selectFwdPerDto.getPersClosedValueBean(), Boolean.FALSE);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// then closed person is the default
		if ((!selectFwdPerDto.getForwardPersonDisabled()
				&& forwardPersonAttribute.equals(lookupDao.decode(CodesConstant.CSEX, CodesConstant.CSEX_U)))
				&& (!selectFwdPerDto.getClosedPersonDisabled()
						&& !closedPersonAttribute.equals(lookupDao.decode(CodesConstant.CSEX, CodesConstant.CSEX_U)))) {
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getGender().getForwardPerson(),
				selectForwardPersonValueBean.getGender().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setGender method in SelectForwardPersonCommonDetail class");
	}

	/**
	 * Method Name: setUpGenderDisplayValue Description:Set's up gender
	 * attribute
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk comparison
	 */
	protected String setUpGenderDisplayValue(SelectForwardPersonValueBean selectForwardPersonValueBean,
			PersonBean personValueBean, Boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean genderAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		// if valid value bean
		if (null != personValueBean && !ObjectUtils.isEmpty(personValueBean.getSex())) {
			// This string is used in commonDefaultDisableChk
			attributeValue = lookupDao.decode(CodesConstant.CSEX,
					StringUtil.getNonNullString(personValueBean.getSex()));
			// Initialize display text for gender
			genderAttribute.setDisplayValue(attributeValue);
		}
		if (isFwdPerson) {
			selectForwardPersonValueBean.getGender().setForwardPerson(genderAttribute);
		} else {
			selectForwardPersonValueBean.getGender().setClosedPerson(genderAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method Name: getMartialStatus Description:Builds the Person MartialStatus
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setMartialStatus(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/* Initialize forward person attributes */
		// if forward person is not an employee or former employee then only
		// setup
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) // attributes
		{
			// Initialize display text for MartialStatus and string used in
			// commonDefaultDisableChk, Indicator is set as true for forward
			// person
			forwardPersonAttribute = setUpMartialStatusDisplayValue(selectForwardPersonValueBean,
					selectFwdPerDto.getPersForwardValueBean(), Boolean.TRUE);
		} else
			selectForwardPersonValueBean.getMartialStatus().setForwardPerson(null);
		/* Initialize closed person attributes */
		// Initialize display text for MartialStatus and string used in
		// commonDefaultDisableChk,Indicator is set as true for closed person
		closedPersonAttribute = setUpMartialStatusDisplayValue(selectForwardPersonValueBean,
				selectFwdPerDto.getPersClosedValueBean(), Boolean.FALSE);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// default
		if (null != forwardPersonAttribute && null != closedPersonAttribute
				&& !StringUtils.isEmpty(forwardPersonAttribute) && !StringUtils.isEmpty(closedPersonAttribute)
				&& (!selectFwdPerDto.getForwardPersonDisabled() && forwardPersonAttribute
						.equals(lookupDao.decode(CodesConstant.CMARSTAT, CodesConstant.CMARSTAT_UK)))
				&& (!selectFwdPerDto.getClosedPersonDisabled() && !closedPersonAttribute
						.equals(lookupDao.decode(CodesConstant.CMARSTAT, CodesConstant.CMARSTAT_UK)))) {
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getMartialStatus().getForwardPerson(),
				selectForwardPersonValueBean.getMartialStatus().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setUpMartialStatusDisplayValue Description: Set's up
	 * martialStatus attribute
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk comparison
	 */
	protected String setUpMartialStatusDisplayValue(SelectForwardPersonValueBean selectForwardPersonValueBean,
			PersonBean personValueBean, Boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean martialStatusAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		// if valid value bean
		if (null != personValueBean && !ObjectUtils.isEmpty(personValueBean.getCdMaritalStatus())) {
			// This string is used in commonDefaultDisableChk
			attributeValue = lookupDao.decode(CodesConstant.CMARSTAT,
					StringUtil.getNonNullString(personValueBean.getCdMaritalStatus()));
			// Initialize display text for martialStatus
			martialStatusAttribute.setDisplayValue(attributeValue);
		}
		if (isFwdPerson) {
			selectForwardPersonValueBean.getMartialStatus().setForwardPerson(martialStatusAttribute);
		} else {
			selectForwardPersonValueBean.getMartialStatus().setClosedPerson(martialStatusAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method Name: getDOB Description:Builds the Person DOB Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return SelectForwardPersonValueBean
	 * @param selectForwardPersonValueBean
	 * @param persClosedValueBean
	 * @param persForwardValueBean
	 * @return
	 */
	protected void setDOB(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/*
		 * Initialize forward person attributes and get string that will be used
		 * in commonDefaultDisableChk
		 */
		forwardPersonAttribute = setUpDOBDisplayValues(selectForwardPersonValueBean,
				selectFwdPerDto.getPersForwardValueBean(), selectFwdPerDto, Boolean.TRUE);
		/*
		 * Initialize closed person attributes and get string that will be used
		 * in commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpDOBDisplayValues(selectForwardPersonValueBean,
				selectFwdPerDto.getPersClosedValueBean(), selectFwdPerDto, Boolean.FALSE);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		boolean forwardAprx = (null == selectFwdPerDto.getPersForwardValueBean().getIsApproxDateOfBirth() ? false
				: selectFwdPerDto.getPersForwardValueBean().getIsApproxDateOfBirth().booleanValue());
		boolean closedAprx = (null == selectFwdPerDto.getPersClosedValueBean().getIsApproxDateOfBirth() ? false
				: selectFwdPerDto.getPersClosedValueBean().getIsApproxDateOfBirth().booleanValue());
		// closed person
		if (!selectFwdPerDto.getClosedPersonDisabled()) {
			if (selectFwdPerDto.getFwdPrsnHasElig()) {
				selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
				selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
			}
		}
		// then closed person is the default
		if ((!selectFwdPerDto.getForwardPersonDisabled() && forwardAprx)
				&& (!selectFwdPerDto.getClosedPersonDisabled() && !closedAprx)) {
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getDOB().getForwardPerson(),
				selectForwardPersonValueBean.getDOB().getClosedPerson(), selectFwdPerDto);
	}

	/**
	 * Method Name: setUpDOBDisplayValues Description:Set's up DOB attributes ,
	 * returns a string that is used to set default and disabled attribute
	 * 
	 * @param selectFwdPerDto
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String
	 */
	protected String setUpDOBDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			PersonBean personValueBean, SelectForwardPersonDataDto selectFwdPerDto, Boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean dobAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		String displayValue;
		// if valid value bean
		if (null != personValueBean && !ObjectUtils.isEmpty(personValueBean.getDtDateOfBirth())) {
			// get DOB
			attributeValue = FormattingUtils.formatDate(personValueBean.getDtDateOfBirth());
			displayValue = attributeValue;
			// if DOB exists
			if (StringUtil.isValid(attributeValue)) {
				displayValue += DOB_AGE;
				// if DOD does not exists calc Age based on sysdate
				if (null == personValueBean.getDtOfDeath()) {
					// for post merge page use date of merge to compute the age.
					// Else use System
					// Date
					if (selectFwdPerDto.getIdPersonMerge() > 0 && selectFwdPerDto.getDtPersonMerge() != null)
						displayValue += Integer.toString(DateUtils.getAge(personValueBean.getDtDateOfBirth(),
								selectFwdPerDto.getDtPersonMerge()));
					else
						displayValue += Integer
								.toString(DateUtils.getAge(personValueBean.getDtDateOfBirth(), new Date()));
				} else
					// calc age based on DOD
					displayValue += Integer.toString(
							DateUtils.getAge(personValueBean.getDtDateOfBirth(), personValueBean.getDtOfDeath()));
				// Append approximate date of Birth
				displayValue += DOB_APRX + StringUtil.toYorN(((null == personValueBean.getIsApproxDateOfBirth()) ? false
						: personValueBean.getIsApproxDateOfBirth().booleanValue()));
				attributeValue += StringUtil.toYorN(((null == personValueBean.getIsApproxDateOfBirth()) ? false
						: personValueBean.getIsApproxDateOfBirth().booleanValue()));
			}
			dobAttribute.setDisplayValue(displayValue);
		}
		if (isFwdPerson) {
			selectForwardPersonValueBean.getDOB().setForwardPerson(dobAttribute);
		} else {
			selectForwardPersonValueBean.getDOB().setClosedPerson(dobAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method Name: getSSN Description: Builds the Person SSN Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return SelectForwardPersonValueBean
	 * @param selectForwardPersonValueBean
	 * @param persClosedValueBean
	 * @param persForwardValueBean
	 * @return
	 */
	protected void setSSN(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		ArrayList<PersonIdentifiersDto> persClosedIdentifiers;
		ArrayList<PersonIdentifiersDto> persForwardIdentifiers;
		// retrieve person Identifiers for closed and forward person
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedIdentifiers = (ArrayList<PersonIdentifiersDto>) personIdService
					.fetchIdentifiersList((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(), true);
			persForwardIdentifiers = (ArrayList<PersonIdentifiersDto>) personIdService
					.fetchIdentifiersList((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(), true);
		} else {
			persClosedIdentifiers = (ArrayList<PersonIdentifiersDto>) personIdService.fetchIdentifiersList(
					(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(), true,
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			persForwardIdentifiers = (ArrayList<PersonIdentifiersDto>) personIdService.fetchIdentifiersList(
					(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(), true,
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
		}

		selectFwdPerDto.setPersForwardIdentifiers(persForwardIdentifiers);
		selectFwdPerDto.setPersClosedIdentifiers(persClosedIdentifiers);
		// get closed and forward SSN value bean from retrieved Identifier
		// listed
		PersonIdentifiersDto persSSNClosedValueBean = selectFwdCommonData
				.getPersonIdentifier(CodesConstant.CNUMTYPE_SSN, persClosedIdentifiers);
		PersonIdentifiersDto persSSNForwardValueBean = selectFwdCommonData
				.getPersonIdentifier(CodesConstant.CNUMTYPE_SSN, persForwardIdentifiers);
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/*
		 * Initialize forward attributes if person not employee or former
		 * employee
		 */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// set up SSN displays values and get string to be used in
			// comparison in
			// commonDefaultDisableChk
			forwardPersonAttribute = setUpssnDisplayValues(selectForwardPersonValueBean, persSSNForwardValueBean, true);
		}
		/* Initialize closed attributes */
		// set up SSN displays values and get string to be used in comparison in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpssnDisplayValues(selectForwardPersonValueBean, persSSNClosedValueBean, false);
		// call function to set disabled and default strings for forward
		// and closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// if person fwd PC in OPEN Stage(SUB,ADO,PAD,PAL,PCA) disable closed
		// person
		if (!selectFwdPerDto.getClosedPersonDisabled() && selectFwdPerDto.getFwdPrsnHasPCInOpnSTG()) {
			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
		}
		// or Validated by Interface indicator
		if (!selectFwdPerDto.getClosedPersonDisabled() && !selectFwdPerDto.getForwardPersonDisabled()) {
			// get Method of Verification , Validated by Interface indicator for
			// closed and forward person

			String forwardValbyIntrfc = persSSNForwardValueBean.getIndValidatedByInterface();
			String closedValbyIntrfc = persSSNClosedValueBean.getIndValidatedByInterface();
			String forwardMethodOfVerf = persSSNForwardValueBean.getSsnVerificationMethod();
			String closedMethodOfVerf = persSSNClosedValueBean.getSsnVerificationMethod();
			if (StringUtil.isValid(forwardValbyIntrfc) || StringUtil.isValid(closedValbyIntrfc)) {
				// or NULL, then disable forward
				if ((StringUtil.isValid(forwardValbyIntrfc) && forwardValbyIntrfc.equals(ServiceConstants.NO))
						&& (StringUtil.isValid(closedValbyIntrfc) && closedValbyIntrfc.equals(ServiceConstants.Y))
						|| (StringUtil.isValid(closedValbyIntrfc) && !StringUtil.isValid(forwardValbyIntrfc))) {
					selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.TRUEVAL);
					selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
					selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
					selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
				}
				// or NULL, then disable closed
				if ((StringUtil.isValid(forwardValbyIntrfc) && forwardValbyIntrfc.equals(ServiceConstants.Y))
						&& (StringUtil.isValid(closedValbyIntrfc) && closedValbyIntrfc.equals(ServiceConstants.N))
						|| (StringUtil.isValid(forwardValbyIntrfc) && !StringUtil.isValid(closedValbyIntrfc))) {

					selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.FALSEVAL);
					selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
					selectFwdPerDto.setForwardPersonDefault(ServiceConstants.TRUEVAL);
					selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
				}
			}
			// present for either
			if (!selectFwdPerDto.getClosedPersonDisabled() && !selectFwdPerDto.getForwardPersonDisabled()) {
				selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
				selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
				// forward does not
				if ((((StringUtil.isValid(forwardMethodOfVerf)
						&& forwardMethodOfVerf.equals(CodesConstant.SSNVERIF_NVD))
						|| !StringUtil.isValid(forwardMethodOfVerf))
						&& (StringUtil.isValid(closedMethodOfVerf)
								&& !closedMethodOfVerf.equals(CodesConstant.SSNVERIF_NVD)))) {
					selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.TRUEVAL);
					selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
					selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
					selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
				}
			}
			// if the SSN are not identical then enable both
			if (!(FormattingUtils.formatSSN(persSSNForwardValueBean.getPersonIdNumber())
					.equals(FormattingUtils.formatSSN(persSSNClosedValueBean.getPersonIdNumber())))) {
				selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.FALSEVAL);
				selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
			}
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getSSN().getForwardPerson(),
				selectForwardPersonValueBean.getSSN().getClosedPerson(), selectFwdPerDto);
	}

	/**
	 * Method Name: setUpssnDisplayValues Description:Set's up SSN attributes
	 * 
	 * @param isFwdPerson
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonIdentifiersDto
	 * @return String
	 */
	protected String setUpssnDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			PersonIdentifiersDto personSSNValueBean, boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean ssnAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList<String> ssnDisplayValues = new ArrayList<>();
		String attributeValue = null;
		// if valid SSN value Bean
		if (null != personSSNValueBean) {
			// get SSN add to display array , and append to string used for
			// comparison in
			// commonDefaultDisableChk
			attributeValue = personSSNValueBean.getPersonIdNumber();
			attributeValue = FormattingUtils.formatSSN(attributeValue);
			ssnDisplayValues.add(attributeValue);
			// get Indicator for Validated by Interface add to display array ,
			// and append to string used for comparison in
			// commonDefaultDisableChk
			String ssnVerfValid = StringUtil.getNonNullString(personSSNValueBean.getIndValidatedByInterface());
			if (StringUtil.isValid(ssnVerfValid)) {
				ssnVerfValid = (ssnVerfValid.equals(ServiceConstants.YES) ? ServiceConstants.YES : ServiceConstants.NO);
				attributeValue += ssnVerfValid;
				ssnDisplayValues.add(ssnVerfValid);
			} else
				ssnDisplayValues.add(StringUtil.EMPTY_STRING);
			// get Method of Verification add to display array ,
			// and append to string used for comparison in
			// commonDefaultDisableChk
			ssnVerfValid = lookupDao.decode(CodesConstant.SSNVERIF,
					StringUtil.getNonNullString(personSSNValueBean.getSsnVerificationMethod()));
			if (StringUtil.isValid(ssnVerfValid)) {
				attributeValue += ssnVerfValid;
				ssnDisplayValues.add(ssnVerfValid);
			} else
				ssnDisplayValues.add(StringUtil.EMPTY_STRING);
			// init Primary Key Data for SSN record used for Dirty Read
			selectFwdCommonData.initPrKeyData(ssnAttribute, personSSNValueBean.getIdPersonId().intValue(),
					personSSNValueBean.getDtLastUpdated());
		} else // if there no SSN set up blank rows
		{
			ssnDisplayValues.add(StringUtil.EMPTY_STRING);
			ssnDisplayValues.add(StringUtil.EMPTY_STRING);
			ssnDisplayValues.add(StringUtil.EMPTY_STRING);
		}
		// add display values to attribute
		ssnAttribute.setDisplayValues(ssnDisplayValues);
		if (isFwdPerson) {
			selectForwardPersonValueBean.getSSN().setForwardPerson(ssnAttribute);
		} else {
			selectForwardPersonValueBean.getSSN().setClosedPerson(ssnAttribute);
		}
		return attributeValue;
	}

	/**
	 * Method name: getAddress Description:Builds the Address Type Component
	 * with attributes ( display text , enabled/disabled, default) for ValueBean
	 * used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param selectForwardPersonValueBean
	 * @param persClosedValueBean
	 * @param persForwardValueBean
	 * @return
	 */
	protected void setAddress(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {

		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		AddressValueDto persClosedAddressValueBean = null;
		AddressValueDto persForwardAddressValueBean = null;
		// retrieve person Current Address for closed and forward person
		if (selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedAddressValueBean = mappingAddressDto(personDao
					.fetchCurrentPrimaryAddress((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson()));
			persForwardAddressValueBean = mappingAddressDto(personDao
					.fetchCurrentPrimaryAddress((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson()));
		} else {
			persClosedAddressValueBean = addressDao.fetchCurrentPrimaryAddress(
					(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			persForwardAddressValueBean = addressDao.fetchCurrentPrimaryAddress(
					(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
		}
		// and get string that will be used in commonDefaultDisableChk
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			forwardPersonAttribute = setUpAddressDisplayValues(persForwardAddressValueBean, persClosedAddressValueBean);
		}
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpAddressDisplayValues(persClosedAddressValueBean, persForwardAddressValueBean);
		selectForwardPersonValueBean.getAddress().setForwardPerson(forwardPersonAttribute);
		selectForwardPersonValueBean.getAddress().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed
		// person
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
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getAddress().getForwardPerson(),
				selectForwardPersonValueBean.getAddress().getClosedPerson(), selectFwdPerDto);

	}

	/**
	 * Method Name: setUpAddressDisplayValues Description: Set's up address
	 * attributes using input PersonAddressValueBean, initializes primary key
	 * used for dirty read ,returns a string that is used to set default and
	 * disabled attribute,
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonEmailValueBean
	 * @return String
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpAddressDisplayValues(
			AddressValueDto personAddressValueBean, AddressValueDto chkAddressValueBean) {
		ArrayList<String> displayValues = new ArrayList<>();
		SelectForwardPersonValueBean.AttributeValueBean addressAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		if (null != personAddressValueBean && !ObjectUtils.isEmpty(personAddressValueBean.getAddressType())) {
			// set up address type
			displayValues.add(lookupDao.decode(CodesConstant.CADDRTYP,
					StringUtil.getNonNullString(personAddressValueBean.getAddressType())));
			// setup address Line 1
			displayValues.add(StringUtil.getNonNullString(personAddressValueBean.getStreetLn1()));
			// address Line 2
			if (StringUtil.isValid(personAddressValueBean.getStreetLn2())) {
				displayValues.add(StringUtil.getNonNullString(personAddressValueBean.getStreetLn2()));
			} else if ((null != chkAddressValueBean) && (StringUtil.isValid(chkAddressValueBean.getStreetLn2()))) {
				displayValues.add(StringUtil.EMPTY_STRING);
			}
			// set city state zip line
			displayValues.add(StringUtil.getNonNullString(personAddressValueBean.getCity()) + ADDRESS_COMMA
					+ StringUtil.getNonNullString(personAddressValueBean.getState()) + ADDRESS_SPACE
					+ StringUtil.getNonNullString(personAddressValueBean.getZip()));
			// set address start date
			displayValues.add(FormattingUtils.formatDate(personAddressValueBean.getStartDate()));
			// set up Primary Key value for address record used for dirty read
			selectFwdCommonData.initPrKeyData(addressAttribute, personAddressValueBean.getIdAddrPersonLink().intValue(),
					personAddressValueBean.getDtLastUpdate());
		} else // setup blank lines if no address
		{
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			if ((null != chkAddressValueBean) && (StringUtil.isValid(chkAddressValueBean.getStreetLn2()))) {
				displayValues.add(StringUtil.EMPTY_STRING);
			}
		}
		addressAttribute.setDisplayValues(displayValues);
		return addressAttribute;
	}

	/**
	 * 
	 * Method Name: mappingAddressDto Method Description: MAPPING addressDto to
	 * AddressValueDto
	 * 
	 * @param addressDto
	 * @return
	 */
	public AddressValueDto mappingAddressDto(AddressDto addressDto) {
		if (ObjectUtils.isEmpty(addressDto))
			return null;
		AddressValueDto addressValueDto = new AddressValueDto();
		addressValueDto.setPersonId(addressDto.getIdPerson().intValue());
		addressValueDto.setStreetLn1(addressDto.getAddrPersAddrStLn1());
		addressValueDto.setStreetLn2(addressDto.getAddrPersAddrStLn2());
		addressValueDto.setAttention(addressDto.getAddrPersAddrAttn());
		addressValueDto.setCity(addressDto.getAddrCity());
		addressValueDto.setState(addressDto.getCdAddrState());
		addressValueDto.setCounty(addressDto.getCdAddrCounty());
		addressValueDto.setZip(addressDto.getAddrZip());
		addressValueDto.setPrimary(addressDto.getIndPersAddrLinkPrimary());
		addressValueDto.setInvalid(addressDto.getIndPersAddrLinkInvalid());
		addressValueDto.setAddressType(addressDto.getCdPersAddrLinkType());
		addressValueDto.setEndDate(addressDto.getDtPersAddrLinkEnd());
		addressValueDto.setStartDate(addressDto.getDtPersAddrLinkStart());
		addressValueDto.setIdPersonAddr(addressDto.getIdAddress().intValue());
		if (ObjectUtils.isEmpty(addressDto.getIdAddrPersonLink()))
			addressValueDto.setIdAddrPersonLink(0);
		else
			addressValueDto.setIdAddrPersonLink(addressDto.getIdAddrPersonLink().intValue());
		addressValueDto.setTxtAplComments(addressDto.getPersAddrCmnts());
		if (ObjectUtils.isEmpty(addressDto.getIdPersonMerge()))
			addressValueDto.setIdPersonMerge(0);
		else
			addressValueDto.setIdPersonMerge(addressDto.getIdPersonMerge().intValue());
		addressValueDto.setDtLastUpdate(addressDto.getTsLastUpdate());
		return addressValueDto;
	}

}
