package us.tx.state.dfps.service.personmergesplit.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.web.bean.PersonBean;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.CvsFaHomeDao;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.web.person.bean.CvsFaHomeValueBean;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonDataDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Utility
 * class for Select Forward Person page> May 30, 2018- 9:53:07 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Component
@Transactional
public class SelectForwardPersonOtherData {

	@Autowired
	LookupDao lookupDao;

	@Autowired
	CvsFaHomeDao cvsFaHomeDao;

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	PersonEthnicityDao personEthnicityDao;

	private static final String ETHNICITY_COMMA = ", ";
	private static final String NOT_PRESENT = "Not Present";

	static final Logger Log = Logger.getLogger(SelectForwardPersonOtherData.class);

	public SelectForwardPersonOtherData() {
		super();
	}

	/**
	 * Method Name:getCitizenship Description: Builds the Person Citizenship
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param
	 * 
	 * @return
	 */
	protected void setCitizenship(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		Log.info("inside setCitizenship method in SelectForwardPersonOtherData class");
		/* Initialize forward attributes if not employee or former employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// init forward person display values and get string that will be
			// used in
			// commonDefaultDisableChk
			forwardPersonAttribute = setUpCitizenshipDisplayValue(selectFwdPerDto.getPersForwardValueBean(),
					selectFwdPerDto);
			selectForwardPersonValueBean.getCitizenStat().setForwardPerson(forwardPersonAttribute);
		} else
			selectForwardPersonValueBean.getCitizenStat().setForwardPerson(null);
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpCitizenshipDisplayValue(selectFwdPerDto.getPersClosedValueBean(), selectFwdPerDto);
		selectForwardPersonValueBean.getCitizenStat().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(true, selectFwdPerDto);
		// closed person
		if (!selectFwdPerDto.getClosedPersonDisabled() && selectFwdPerDto.getFwdPrsnHasElig()) {
			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
		}
		// then closed person is the default
		if ((!selectFwdPerDto.getForwardPersonDisabled() && forwardPersonAttribute.getDisplayValue()
				.equals(lookupDao.decode(CodesConstant.CCTZNSTA, CodesConstant.CCTZNSTA_TMR)))
				&& (!selectFwdPerDto.getClosedPersonDisabled() && !closedPersonAttribute.getDisplayValue()
						.equals(lookupDao.decode(CodesConstant.CCTZNSTA, CodesConstant.CCTZNSTA_TMR)))) {
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getCitizenStat().getForwardPerson(),
				selectForwardPersonValueBean.getCitizenStat().getClosedPerson(), selectFwdPerDto);
		Log.info("Outside setCitizenship method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name:setUpCitizenshipDisplayValue Description:Set's up citizenship
	 * attributes , initializes primary key used for dirty read ,returns a
	 * string that is used to set default and disabled attribute,
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param personValueBean
	 * @param selectFwdPerDto
	 * @return String
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpCitizenshipDisplayValue(PersonBean personValueBean,
			SelectForwardPersonDataDto selectFwdPerDto) {
		Log.info("inside setUpCitizenshipDisplayValue method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean citizenShipAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		if (null != personValueBean) {
			// retrieve person detail record using CvsFaHm Bean
			CvsFaHomeValueBean cvsFaHomeValueBean = new CvsFaHomeValueBean();
			cvsFaHomeValueBean.setIdStage(0l);
			cvsFaHomeValueBean.setCdStage(NOT_PRESENT);
			cvsFaHomeValueBean.setIdPerson((long) personValueBean.getIdPerson());
			if (selectFwdPerDto.getIdPersonMerge() == 0) {
				cvsFaHomeValueBean = cvsFaHomeDao.displayCvsFaHome((long) personValueBean.getIdPerson());
			} else {
				cvsFaHomeValueBean = cvsFaHomeDao.getCvsFaHome(personValueBean.getIdPerson(),
						selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			}
			if (null != cvsFaHomeValueBean && !ObjectUtils.isEmpty(cvsFaHomeValueBean.getCdPersonBirthCitizenship())) {
				// set up string that will be used to determine whether field
				// will be
				// defaulted or disabled in commonDefaultDisableChk
				attributeValue = lookupDao.decode(CodesConstant.CCTZNSTA,
						StringUtil.getNonNullString(cvsFaHomeValueBean.getCdPersonBirthCitizenship()));
				// set up citizenship display value
				citizenShipAttribute.setDisplayValue(attributeValue);
				// init primary Key for Citizenship attribute used for dirty
				// read
				initPrKeyData(citizenShipAttribute, personValueBean.getIdPerson(),
						cvsFaHomeValueBean.getDtLastUpdate());
			}
		}
		Log.info("outside setUpCitizenshipDisplayValue method in SelectForwardPersonOtherData class");
		return citizenShipAttribute;

	}

	/**
	 * Method Name: getLanguage Description: Builds the Person Language
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setLanguage(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setLanguage method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/*
		 * Initialize forward Language attribute and get string used in
		 * commonDefaultDisableChk
		 */
		forwardPersonAttribute = setUpLanguageDisplayValue(selectFwdPerDto.getPersForwardValueBean());
		/*
		 * Initialize closed Language attribute and get string used in
		 * commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpLanguageDisplayValue(selectFwdPerDto.getPersClosedValueBean());
		selectForwardPersonValueBean.getLanguage().setForwardPerson(forwardPersonAttribute);
		selectForwardPersonValueBean.getLanguage().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		// parameter passed is set to false to indicate that employee/former
		// employee check is not needed
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(false, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getLanguage().getForwardPerson(),
				selectForwardPersonValueBean.getLanguage().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setLanguage method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: setUpLanguageDisplayValue Description: Set's up language
	 * attribute
	 * 
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk comparison
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpLanguageDisplayValue(PersonBean personValueBean) {
		Log.info("inside setUpLanguageDisplayValue method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean languageAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		// if valid value bean
		if (null != personValueBean && !ObjectUtils.isEmpty(personValueBean.getCdPrimaryLanguage())) {
			// This string is used in commonDefaultDisableChk
			attributeValue = lookupDao.decode(CodesConstant.CLANG,
					StringUtil.getNonNullString(personValueBean.getCdPrimaryLanguage()));
			// Initialize display text for language
			languageAttribute.setDisplayValue(attributeValue);
		}
		Log.info("outside setUpLanguageDisplayValue method in SelectForwardPersonOtherData class");
		return languageAttribute;
	}

	/**
	 * Method Name: getLivingArrang Description: Builds the Person LivingArrang
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setLivingArrang(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setLivingArrang method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/*
		 * Initialize forward attributes if forward person Employee or not
		 * Former Employee
		 */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			/*
			 * Initialize forward LivingArrang and get string used in
			 * commonDefaultDisableChk
			 */
			forwardPersonAttribute = setUpLivingArrangDisplayValue(selectFwdPerDto.getPersForwardValueBean());
			selectForwardPersonValueBean.getLivArrang().setForwardPerson(forwardPersonAttribute);
		} else
			selectForwardPersonValueBean.getLivArrang().setForwardPerson(null);
		/*
		 * Initialize closed LivingArrang and get string used in
		 * commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpLivingArrangDisplayValue(selectFwdPerDto.getPersClosedValueBean());
		selectForwardPersonValueBean.getLivArrang().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(true, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getLivArrang().getForwardPerson(),
				selectForwardPersonValueBean.getLivArrang().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setLivingArrang method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: setUpLivingArrangDisplayValue Description: Set's up
	 * livingArrang attribute
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param PersonValueBean
	 * @return String - used in commonDefaultDisableChk comparison
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpLivingArrangDisplayValue(
			PersonBean personValueBean) {
		Log.info("inside setUpLivingArrangDisplayValue method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean livingArrangAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		// if valid value bean
		if (null != personValueBean && !ObjectUtils.isEmpty(personValueBean.getCdLivingArrangement())) {
			// This string is used in commonDefaultDisableChk
			attributeValue = lookupDao.decode(CodesConstant.CLIVARR,
					StringUtil.getNonNullString(personValueBean.getCdLivingArrangement()));
			// Initialize display text for LivingArrang
			livingArrangAttribute.setDisplayValue(attributeValue);
		}
		Log.info("outside setUpLivingArrangDisplayValue method in SelectForwardPersonOtherData class");
		return livingArrangAttribute;
	}

	/**
	 * Method Name: commonDefaultDisableChk Description: Does the common check
	 * for Disabled and default attributes for all forward and closed fields.
	 * 
	 * @param selectFwdPerDto
	 * 
	 * @param boolean
	 *            chkEmpOrFrmrEmp - Input parm , if true check for Employee or
	 *            Former Employee
	 * 
	 * @return
	 */
	protected void commonDefaultDisableChk(boolean chkEmpOrFrmrEmp, SelectForwardPersonDataDto selectFwdPerDto) {
		Log.info("inside commonDefaultDisableChk method in SelectForwardPersonOtherData class");
		// init forward , closed disabled and default string var's
		selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.TRUEVAL);
		selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
		selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
		selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
		selectFwdPerDto.setFwdClsdPrsnHasSameVal(ServiceConstants.FALSEVAL);
		// if forward person is valid
		if (StringUtil.isValid(selectFwdPerDto.getForwardPersonAttribute())) {
			// Enable forward person attributes
			selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.FALSEVAL);
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.TRUEVAL);

			// exists and value <> forward person allow closed person to be
			// selected
			if (chkEmpOrFrmrEmp && !selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()
					&& StringUtil.isValid(selectFwdPerDto.getClosedPersonAttribute()) && (!selectFwdPerDto
							.getForwardPersonAttribute().equals(selectFwdPerDto.getClosedPersonAttribute())))
				selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
			// selected
			if (!chkEmpOrFrmrEmp && StringUtil.isValid(selectFwdPerDto.getClosedPersonAttribute()) && (!selectFwdPerDto
					.getForwardPersonAttribute().equals(selectFwdPerDto.getClosedPersonAttribute())))
				selectFwdPerDto.setForwardPersonDisabled(ServiceConstants.FALSEVAL);
			if (StringUtil.isValid(selectFwdPerDto.getClosedPersonAttribute())
					&& selectFwdPerDto.getForwardPersonAttribute().equals(selectFwdPerDto.getClosedPersonAttribute()))
				selectFwdPerDto.setFwdClsdPrsnHasSameVal(ServiceConstants.TRUEVAL);
		} else if (// if forward person is not valid and closed person is valid
		StringUtil.isValid(selectFwdPerDto.getClosedPersonAttribute())) {
			// Enable closed person attributes
			if (!chkEmpOrFrmrEmp || !selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
				selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
				selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
			}
		}
		Log.info("outside commonDefaultDisableChk method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: setDefaultDisableAttr Description: For the input attribute
	 * bean for closed and forward person set the default and disabled attribute
	 * These attributes are stored in
	 * forwardPersonDisabled,forwardPersonDefault,closedPersonDisabled
	 * closedPersonDefault instance variables
	 * 
	 * @param selectFwdPerDto
	 *            TODO
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 *            forwardAttribute,
	 *            SelectForwardPersonValueBean.AttributeValueBean
	 *            closedAttribute
	 * 
	 * @return
	 */
	protected void setDefaultDisableAttr(SelectForwardPersonValueBean.AttributeValueBean forwardAttribute,
			SelectForwardPersonValueBean.AttributeValueBean closedAttribute,
			SelectForwardPersonDataDto selectFwdPerDto) {
		Log.info("inside setDefaultDisableAttr method in SelectForwardPersonOtherData class");
		if (null != forwardAttribute) {
			forwardAttribute.setIsDisabled(selectFwdPerDto.getForwardPersonDisabled().toString());
			forwardAttribute.setIsDefault(selectFwdPerDto.getForwardPersonDefault().toString());
		}
		if (null != closedAttribute) {
			closedAttribute.setIsDisabled(selectFwdPerDto.getClosedPersonDisabled().toString());
			closedAttribute.setIsDefault(selectFwdPerDto.getClosedPersonDefault().toString());
		}
		Log.info("outside setDefaultDisableAttr method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: initPrKeyData Description: For the input attribute bean set
	 * the primary key id and the last date update field of
	 * PrimaryKeyDataValueBean contained in SelectForwardPersonValueBean , the
	 * input field will used to implement dirty read during save. This method is
	 * used when there is one to one mapping between the input attribute and
	 * it's repective database record.
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 *            idPersonAttribute, int IdKey, Date dtLastUpdate
	 * 
	 * @return
	 */
	protected void initPrKeyData(SelectForwardPersonValueBean.AttributeValueBean idPersonAttribute, int IdKey,
			Date dtLastUpdate) {
		Log.info("inside initPrKeyData method in SelectForwardPersonOtherData class");
		// set id person for display
		idPersonAttribute.setPrimaryKeyData(new SelectForwardPersonValueBean.PrimaryKeyDataValueBean());
		// set id person for update
		idPersonAttribute.getPrimaryKeyData().setIdKey(IdKey);
		// set Date of person record used for update
		idPersonAttribute.getPrimaryKeyData().setDtLastUpdate(dtLastUpdate);
		Log.info("outside initPrKeyData method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: initPersonPrKeyDataList Description: For the input attribute
	 * bean set the primary key id and the last date update field of
	 * PrimaryKeyDataValueBean contained in SelectForwardPersonValueBean , the
	 * input field will be used to implement dirty read during save. This method
	 * is used when input field represents multiple records in the database eg.
	 * Race / Ethnicity, Income and Resources , Education Needs, each entry is
	 * added to the arraylist PrimaryKeyDataList in SelectForwardPersonValueBean
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 *            idPersonAttribute, int IdKey, Date dtLastUpdate
	 * 
	 * @return
	 */
	protected void initPersonPrKeyDataList(SelectForwardPersonValueBean.AttributeValueBean idPersonAttribute, int idKey,
			Date dtLastUpdate) {
		if (null == idPersonAttribute.getPrimaryKeyDataList()) {
			idPersonAttribute.setPrimaryKeyDataList(new ArrayList());
		}
		SelectForwardPersonValueBean.PrimaryKeyDataValueBean primaryKeyData = new SelectForwardPersonValueBean.PrimaryKeyDataValueBean();
		primaryKeyData.setIdKey(idKey);
		primaryKeyData.setDtLastUpdate(dtLastUpdate);
		idPersonAttribute.getPrimaryKeyDataList().add(primaryKeyData);
	}

	/**
	 * Method Name: getTDHSClient Description: Builds the Person TDHS Client No.
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 * @param
	 * @return
	 */
	protected void setTDHSClient(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setTDHSClient method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/* Initialize forward attributes if not Employee or Former employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// init forward person display values and get string that will be
			// used in
			// commonDefaultDisableChk
			forwardPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_TDHS_CLIENT_NUMBER,
					selectFwdPerDto.getPersForwardIdentifiers(), true);
			selectForwardPersonValueBean.getTDHSClient().setForwardPerson(forwardPersonAttribute);
		}
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_TDHS_CLIENT_NUMBER,
				selectFwdPerDto.getPersClosedIdentifiers(), true);
		selectForwardPersonValueBean.getTDHSClient().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		if (!StringUtils.isEmpty(closedPersonAttribute.getDisplayValues())) {
			String displayValue = "";
			displayValue = closedPersonAttribute.getDisplayValues().stream().map(Object::toString)
					.collect(Collectors.joining(ServiceConstants.CONST_SPACE));
			selectFwdPerDto.setClosedPersonAttribute(displayValue);
		}
		if (!StringUtils.isEmpty(forwardPersonAttribute.getDisplayValues())) {
			String displayValue = "";
			displayValue = forwardPersonAttribute.getDisplayValues().stream().map(Object::toString)
					.collect(Collectors.joining(ServiceConstants.CONST_SPACE));
			selectFwdPerDto.setForwardPersonAttribute(displayValue);
		}
		commonDefaultDisableChk(true, selectFwdPerDto);
		// if person fwd PC in OPEN Stage(SUB,ADO,PAD,PAL,PCA) disable closed
		// person
		if (selectFwdPerDto.getFwdPrsnHasPCInOpnSTG()) {

			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getTDHSClient().getForwardPerson(),
				selectForwardPersonValueBean.getTDHSClient().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setTDHSClient method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: getMedicaid Description: Builds the Person MedicaidNo.
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 * @param
	 * @return
	 */
	protected void setMedicaid(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setMedicaid method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/* Initialize forward attributes if not Employee or Former employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// init forward person display values and get string that will be
			// used in
			// commonDefaultDisableChk
			forwardPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_MEDICAID_NUMBER,
					selectFwdPerDto.getPersForwardIdentifiers(), true);
			selectForwardPersonValueBean.getMedicaid().setForwardPerson(forwardPersonAttribute);
		}
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_MEDICAID_NUMBER,
				selectFwdPerDto.getPersClosedIdentifiers(), true);
		selectForwardPersonValueBean.getMedicaid().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(forwardPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(closedPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(true, selectFwdPerDto);
		// if person fwd PC in OPEN Stage(SUB,ADO,PAD,PAL,PCA) disable closed
		// person
		if (selectFwdPerDto.getFwdPrsnHasPCInOpnSTG()) {
			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getMedicaid().getForwardPerson(),
				selectForwardPersonValueBean.getMedicaid().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setMedicaid method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method name:getDrivLicNo Description: Builds the Person DrivLicNo.
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	protected void setDrivLicNo(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setDrivLicNo method in SelectForwardPersonOtherData class");
		/* Initialize forward attributes */
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/* Initialize forward attributes if not Employee or Former employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// init forward person display values and get string that will be
			// used in
			// commonDefaultDisableChk
			forwardPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_DRIVERS_LICENSE_NUMBER,
					selectFwdPerDto.getPersForwardIdentifiers(), false);
			selectForwardPersonValueBean.getDrivLicNo().setForwardPerson(forwardPersonAttribute);
		} else
			selectForwardPersonValueBean.getDrivLicNo().setForwardPerson(null);
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_DRIVERS_LICENSE_NUMBER,
				selectFwdPerDto.getPersClosedIdentifiers(), false);
		selectForwardPersonValueBean.getDrivLicNo().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(true, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getDrivLicNo().getForwardPerson(),
				selectForwardPersonValueBean.getDrivLicNo().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setDrivLicNo method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: getIdStatePhoto Description: Builds the Person StatePhotoId
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * 
	 * @return
	 * @param
	 * @return
	 */
	protected void setIdStatePhoto(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setIdStatePhoto method in SelectForwardPersonOtherData class");
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		/* Initialize forward attributes if not Employee or Former employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// init forward person display values and get string that will be
			// used in
			// commonDefaultDisableChk
			forwardPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_STATE_PHOTO_ID_NUMBER,
					selectFwdPerDto.getPersForwardIdentifiers(), false);
			selectForwardPersonValueBean.getStatePhotoId().setForwardPerson(forwardPersonAttribute);
		} else
			selectForwardPersonValueBean.getStatePhotoId().setForwardPerson(null);
		// init closed person display values and get string that will be used in
		// commonDefaultDisableChk
		closedPersonAttribute = setUpIdentifierDisplayValues(CodesConstant.CNUMTYPE_STATE_PHOTO_ID_NUMBER,
				selectFwdPerDto.getPersClosedIdentifiers(), false);
		selectForwardPersonValueBean.getStatePhotoId().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(true, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getStatePhotoId().getForwardPerson(),
				selectForwardPersonValueBean.getStatePhotoId().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setIdStatePhoto method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: setUpIdentifierDisplayValues Description: Set's up
	 * Identifier (TDHS,Medicaid,Drivers licence,State Photo ID) attributes ,
	 * initializes primary key used for dirty read ,returns a string that is
	 * used to set default and disabled attribute
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param String
	 *            identifierType
	 * @param ArrayList
	 *            identifierList
	 * @param boolean
	 *            identifierList
	 * @return String
	 */
	private SelectForwardPersonValueBean.AttributeValueBean setUpIdentifierDisplayValues(String identifierType,
			ArrayList identifierList, boolean setUpWithDesc) {
		SelectForwardPersonValueBean.AttributeValueBean identifierAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList identifierDisplayValues = new ArrayList();
		String attributeValue = null;
		// Get Identifier value bean from Arraylist retrieved in SSN method, for
		// a
		// particular Identfier Type( like TDHS, Photo ID etc)
		PersonIdentifiersDto personIdentifierValueBean = getPersonIdentifier(identifierType, identifierList);
		if (null != personIdentifierValueBean) {
			// Set up Id number for Identfier this is also used in setting
			// default
			// and disabled attributes
			attributeValue = personIdentifierValueBean.getPersonIdNumber();
			if (// descripton
			setUpWithDesc) {
				identifierDisplayValues.add(StringUtil.getNonNullString(attributeValue));
				identifierDisplayValues
						.add(StringUtil.getNonNullString(personIdentifierValueBean.getPersonIdDescription()));
			} else
				// only set up Identifier number
				identifierAttribute.setDisplayValue(StringUtil.getNonNullString(attributeValue));
			// set up primary key value for identifier for dirty read
			initPrKeyData(identifierAttribute, personIdentifierValueBean.getIdPersonId().intValue(),
					personIdentifierValueBean.getDtLastUpdated());
		} else {
			if (// TDHS Medicaid
			setUpWithDesc) {
				identifierDisplayValues.add(StringUtil.EMPTY_STRING);
				identifierDisplayValues.add(StringUtil.EMPTY_STRING);
			}
		}
		if (// set up dispaly values array list if multi value display like TDHS
		setUpWithDesc)
			// Medicaid
			identifierAttribute.setDisplayValues(identifierDisplayValues);
		return identifierAttribute;
	}

	/**
	 * Method Name: getPersonIdentifier Description: checks if personIdType like
	 * SSN, TDHS Client number is present in the person's Identifier List, this
	 * list was retrieved in the getSSN method , if present then the respective
	 * PersonIdentifiersDto is returned.
	 * 
	 * @param personIdType
	 * @param personIdentifierArr
	 * 
	 * @return PersonIdentifiersDto
	 */
	protected PersonIdentifiersDto getPersonIdentifier(String personIdType,
			ArrayList<PersonIdentifiersDto> personIdentifierArr) {
		if (null != personIdentifierArr) {
			// Check if identfier type present in identifier list
			for (int count = 0; count < personIdentifierArr.size(); count++) {
				PersonIdentifiersDto persIdentValueBean = personIdentifierArr.get(count);
				if (personIdType.equals(persIdentValueBean.getPersonIdType())) {
					return persIdentValueBean;
				}
			}
		}
		return null;
	}

	/**
	 * Method Name: getEthnicity Description: Builds the Ethnicity Component
	 * with attributes ( display text , enabled/disabled, default) for ValueBean
	 * used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param
	 * 
	 * @return
	 */
	protected void setEthnicity(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setEthnicity method in SelectForwardPersonOtherData class");
		ArrayList persClosedEthnicity;
		ArrayList persForwardEthnicity;

		// Get race records for closed and forward person
		if (selectFwdPerDto.getIdPersonMerge() == 0) {
			persClosedEthnicity = (ArrayList) personEthnicityDao
					.getPersonEthnicityList((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			persForwardEthnicity = (ArrayList) personEthnicityDao
					.getPersonEthnicityList((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
		} else {
			persClosedEthnicity = (ArrayList) personEthnicityDao.getPersonEthnicityList(
					(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			persForwardEthnicity = (ArrayList) personEthnicityDao.getPersonEthnicityList(
					(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
		}
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/* Initialize forward & closed Ethnicity attribute */
		/*
		 * Initialize forward Ethnicity attribute and get value to compare in
		 * commonDefaultDisableChk
		 */
		forwardPersonAttribute = setUpEthnicityDisplayValues(selectForwardPersonValueBean, persForwardEthnicity,
				Boolean.TRUE);
		/*
		 * Initialize Closed Ethnicity attribute and get value to compare in
		 * commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpEthnicityDisplayValues(selectForwardPersonValueBean, persClosedEthnicity,
				Boolean.FALSE);
		// call function to set disabled and default strings for forward
		// and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		commonDefaultDisableChk(true, selectFwdPerDto);
		// if forward person has Adoption Assistance eligibility disable closed
		if (selectFwdPerDto.getFwdPrsnHasAAElig()) {
			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
		}
		// closed to default
		if ((!selectFwdPerDto.getForwardPersonDisabled() && forwardPersonAttribute.equals(CodesConstant.CINDETHN_UT))
				&& (!selectFwdPerDto.getClosedPersonDisabled()
						&& !closedPersonAttribute.equals(CodesConstant.CINDETHN_UT))) {
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
		}
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getEthnicity().getForwardPerson(),
				selectForwardPersonValueBean.getEthnicity().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setEthnicity method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: setUpEthnicityDisplayValues Description: Builds the display
	 * values , primary key list and comaprison strings for forward or closed
	 * Ethnicity attribute
	 * 
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 *            personEthnicityAttribute (this could be either forward or
	 *            closed race attribute)
	 * @param ArrayList
	 *            personEthnicity
	 * @return String
	 */
	protected String setUpEthnicityDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			ArrayList<PersonEthnicityDto> personEthnicity, Boolean isFwdPerson) {
		SelectForwardPersonValueBean.AttributeValueBean personEthnicityAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayEthnicity = new ArrayList();
		ArrayList ethnicityCodes = new ArrayList();
		Object[] displayEthnicityText;
		String displayTextValue = StringUtil.EMPTY_STRING;
		String ethnicityCodeValues = null;
		PersonEthnicityDto personEthnicityValueBean = null;
		// key list for dirty read
		if ((null != personEthnicity) && !ObjectUtils.isEmpty(personEthnicity)) {
			for (int count = 0; count < personEthnicity.size(); count++) {
				// form display value and add to temp array
				personEthnicityValueBean = personEthnicity.get(count);
				displayEthnicity
						.add(lookupDao.decode(CodesConstant.CINDETHN, personEthnicityValueBean.getCdPersonEthnicity()));
				// add the primary key and date last update to the primary key
				// data list
				// of input attribute
				initPersonPrKeyDataList(personEthnicityAttribute,
						personEthnicityValueBean.getIdPersonEthnicity().intValue(),
						personEthnicityValueBean.getDtEthnicityUpdate());
				// add ethnicity codes to temp array to be sorted
				ethnicityCodes.add(personEthnicityValueBean.getCdPersonEthnicity());
			}
			// sort arrays by alphabetical order
			Collections.sort(displayEthnicity);
			Collections.sort(ethnicityCodes);
			displayEthnicityText = displayEthnicity.toArray();
			// of ethnic codes for forward and closed person comparison
			for (int count = 0; count < displayEthnicityText.length; count++) {
				if (count == 0) {
					displayTextValue = (String) displayEthnicityText[count];
					ethnicityCodeValues = (String) ethnicityCodes.get(count);
				} else {
					displayTextValue += ETHNICITY_COMMA + (String) displayEthnicityText[count];
					ethnicityCodeValues += (String) ethnicityCodes.get(count);
				}
			}
		}
		// add to display text in input attribute
		personEthnicityAttribute.setDisplayValue(displayTextValue);
		if (isFwdPerson) {
			selectForwardPersonValueBean.getEthnicity().setForwardPerson(personEthnicityAttribute);
		} else {
			selectForwardPersonValueBean.getEthnicity().setClosedPerson(personEthnicityAttribute);
		}
		return ethnicityCodeValues;
	}

	/**
	 * Method Name: getOccupation Description: Builds the Person Occupation
	 * Component with attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param
	 * 
	 * @return
	 */
	protected void setOccupation(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setOccupation method in SelectForwardPersonOtherData class");
		/* Initialize forward Occupation attributes */
		// set up diaplay value and init forward person attribute String that
		// will be
		// used in commonDefaultDisableChk
		SelectForwardPersonValueBean.AttributeValueBean forwardPersonAttribute = setUpOccupationDisplayValue(
				selectFwdPerDto.getPersForwardValueBean());
		/* Initialize closed Occupation attributes */
		// set up diaplay value and init closed person attribute String that
		// will be
		// used in commonDefaultDisableChk
		SelectForwardPersonValueBean.AttributeValueBean closedPersonAttribute = setUpOccupationDisplayValue(
				selectFwdPerDto.getPersClosedValueBean());
		selectForwardPersonValueBean.getOccupation().setForwardPerson(forwardPersonAttribute);
		selectForwardPersonValueBean.getOccupation().setClosedPerson(closedPersonAttribute);
		// call function to set disabled and default strings for forward and
		// closed person parameter passed is set to false to indicate that
		// employee/former
		// employee check is not needed
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute.getDisplayValue());
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute.getDisplayValue());
		commonDefaultDisableChk(false, selectFwdPerDto);
		// set disabled and default attributes for closed and forward person in
		// Value bean
		setDefaultDisableAttr(selectForwardPersonValueBean.getOccupation().getForwardPerson(),
				selectForwardPersonValueBean.getOccupation().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setOccupation method in SelectForwardPersonOtherData class");
	}

	/**
	 * Method Name: setUpOccupationDisplayValue Description: Set's up occupation
	 * attributes
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param personValueBean
	 * @return String
	 */
	protected SelectForwardPersonValueBean.AttributeValueBean setUpOccupationDisplayValue(PersonBean personValueBean) {
		SelectForwardPersonValueBean.AttributeValueBean occupationAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		String attributeValue = null;
		if (null != personValueBean && !ObjectUtils.isEmpty(personValueBean.getCdOccupation())) {
			// check if occupation value is in CdOccupation or in txtoccupation
			// field
			// in person record
			attributeValue = lookupDao.decode(CodesConstant.COCCUPTN,
					StringUtil.getNonNullString(personValueBean.getCdOccupation()));
			// Initialize display text
			occupationAttribute.setDisplayValue(attributeValue);
		}
		return occupationAttribute;
	}

}
