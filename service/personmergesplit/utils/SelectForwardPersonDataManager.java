package us.tx.state.dfps.service.personmergesplit.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.web.bean.PersonBean;
import us.tx.state.dfps.service.adoptionasstnc.service.AdoptionAsstncService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.errorWarning.ErrorListDto;
import us.tx.state.dfps.service.fce.service.FceService;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pcaeligibilitysummary.service.PcaEligibilitySummaryService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationalNeedDto;
import us.tx.state.dfps.service.person.dto.IncomeAndResourceDto;
import us.tx.state.dfps.service.person.dto.PersonCategoryDto;
import us.tx.state.dfps.service.person.service.PersonDetailService;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.personmergesplit.service.PersonMergeSplitService;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonDataDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean.AttributeValueBean;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Util class
 * for Select Forward Person Data> Jun 14, 2018- 5:31:36 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SelectForwardPersonDataManager {

	@Autowired
	SelectForwardPersonCommonDetail selectFwdPersDetail;
	@Autowired
	SelectForwardPersonOtherData selectFwdCommonData;
	@Autowired
	SelectForwardPersonData selectFwdPersData;
	@Autowired
	LookupDao lookupDao;

	@Autowired
	FceService fceService;

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	AdoptionAsstncService adoptionAsstncService;

	@Autowired
	PcaEligibilitySummaryService pcaEligibilitySummaryService;

	@Autowired
	PersonMergeSplitService personMergeSplitService;

	@Autowired
	PersonDao personDao;

	@Autowired
	PersonDetailService personDetailService;

	static final Logger Log = Logger.getLogger(SelectForwardPersonDataManager.class);

	/**
	 * Constructor initializes variables that are used by all the methods to
	 * form fields.
	 * 
	 */
	public SelectForwardPersonDataManager() {

	}

	/**
	 * Method Name: SelectForwardPersonValueBean Description: This method is
	 * called from following method: - displaySelectForwardPerson_xa This method
	 * fetches the different types of data needed for Select forward page,
	 * formats the data and decides on the default selections.
	 * 
	 * @param idPersonForward
	 *            - Forward Person to be merged
	 * @param idPersonClosed
	 *            - Closed Person to be merged
	 * 
	 * @return SelectForwardPersonValueBean - Value Bean used for display
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SelectForwardPersonValueBean getFieldsToDisplay(int idPersonForward, int idPersonClosed) {
		return getFieldsToDisplay(idPersonForward, idPersonClosed, 0, null);
	}

	/**
	 * Method Name: getFieldsToDisplay Description: This method is called from
	 * following methods: - displaySelectForwardPerson_xa (to display Select
	 * Forward Person page) - displayMergeSplit_xa (to display select Forward
	 * Person data in Post Merge page)
	 *
	 * This method fetches the different types of data needed for Select forward
	 * page, formats the data and decides on the default selections.
	 * 
	 * @param idPersonForward
	 *            - Forward Person to be merged
	 * @param idPersonClosed
	 *            - Closed Person to be merged
	 * @param idPersonMerge
	 *            - Id of the person merge record
	 * 
	 * @return SelectForwardPersonValueBean - Value Bean used for display
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SelectForwardPersonValueBean getFieldsToDisplay(int idPersonForward, int idPersonClosed, int idPersonMerge,
			Date dtPersonMerge) {
		Log.info("inside getFieldsToDisplay method in SelectForwardPersonDataManager class");
		// Clear Values stored in all attributes from previous session

		SelectForwardPersonValueBean selectForwardPersonValueBean = new SelectForwardPersonValueBean();
		PersonBean persClosedValueBean = null;
		PersonBean persForwardValueBean = null;

		SelectForwardPersonDataDto selectFwdPerDto = new SelectForwardPersonDataDto();
		// the idPersonMerge parameter into the class instance variable
		selectFwdPerDto.setIdPersonMerge(idPersonMerge);
		selectFwdPerDto.setDtPersonMerge(dtPersonMerge);
		// check if forward person is Employee , Former Employee or External
		// Staff
		chkFwdPrsnEmpOrFrmrEmpOrExtStaff(idPersonForward, selectFwdPerDto, selectForwardPersonValueBean);
		// check if forward person is in an Open Stage
		chkFwdPrsnPCInOpnSTG(idPersonForward, selectFwdPerDto);
		// check if forward person has Active Foster , Adoption Assistance or
		// Permanency Care Assistance Eligibility
		chkFwdPrsnEligibilities(idPersonForward, selectFwdPerDto, selectForwardPersonValueBean);
		// create all the field objects for select Forward Data Page
		createPersonMergeFields(selectForwardPersonValueBean);
		// retrieve person record for closed and forward person
		if (idPersonMerge == 0) {
			persClosedValueBean = mappingPersonBean(personDao.getPersonById((long) idPersonClosed));
			persForwardValueBean = mappingPersonBean(personDao.getPersonById((long) idPersonForward));
		} else {
			persClosedValueBean = personDao.getPersonDetails((long) idPersonClosed, (long) idPersonMerge,
					CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			persForwardValueBean = personDao.getPersonDetails((long) idPersonForward, (long) idPersonMerge,
					CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
		}
		selectFwdPerDto.setPersClosedValueBean(persClosedValueBean);
		selectFwdPerDto.setPersForwardValueBean(persForwardValueBean);
		// Get Person Id Component for ValueBean
		selectFwdPersDetail.setIdPerson(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Person Name Component for ValueBean
		selectFwdPersDetail.setPersonName(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Gender Component for ValueBean
		selectFwdPersDetail.setGender(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Martial Status Component for ValueBean
		selectFwdPersDetail.setMartialStatus(selectFwdPerDto, selectForwardPersonValueBean);
		// Get DOB Component for ValueBean
		selectFwdPersDetail.setDOB(selectFwdPerDto, selectForwardPersonValueBean);
		// Get SSN Number Component for ValueBean
		selectFwdPersDetail.setSSN(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Citizenship Status Component for ValueBean
		selectFwdCommonData.setCitizenship(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Language Component for ValueBean
		selectFwdCommonData.setLanguage(selectFwdPerDto, selectForwardPersonValueBean);
		// Get LivingArrang Component for ValueBean
		selectFwdCommonData.setLivingArrang(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Occupation Component for ValueBean
		selectFwdCommonData.setOccupation(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Religion Component for ValueBean
		selectFwdPersData.setReligion(selectFwdPerDto, selectForwardPersonValueBean);
		// Get DOD Component for ValueBean
		selectFwdPersData.setDOD(selectFwdPerDto, selectForwardPersonValueBean);
		// Get TDHSClientNo Component for ValueBean
		selectFwdCommonData.setTDHSClient(selectFwdPerDto, selectForwardPersonValueBean);
		// Get MedicaidNo Component for ValueBean
		selectFwdCommonData.setMedicaid(selectFwdPerDto, selectForwardPersonValueBean);
		// Get DrivLicNo Component for ValueBean
		selectFwdCommonData.setDrivLicNo(selectFwdPerDto, selectForwardPersonValueBean);
		// Get StatePhotoId Component for ValueBean
		selectFwdCommonData.setIdStatePhoto(selectFwdPerDto, selectForwardPersonValueBean);
		// Get AddressType Component for ValueBean
		selectFwdPersDetail.setAddress(selectFwdPerDto, selectForwardPersonValueBean);
		// Get Email Address Component for ValueBean
		selectFwdPersData.setEmail(selectFwdPerDto, selectForwardPersonValueBean);
		// Get PhoneNo Component for ValueBean
		selectFwdPersData.setPhone(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getRace Component for ValueBean(this can be multiple rows)
		selectFwdPersData.setRace(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getEthnicity Component for ValueBean(this can be multiple rows)
		selectFwdCommonData.setEthnicity(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getEthnicity Component for ValueBean(this can be multiple rows)
		setCurrIncmRsrc(selectFwdPerDto, selectForwardPersonValueBean);
		// get school name Component for ValueBean
		setSchool(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getAPS Component for ValueBean
		selectFwdPersData.setAPS(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getAPS Component for ValueBean
		selectFwdPersData.setChildInv(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getAPS Component for ValueBean
		selectFwdPersData.setParentCaretaker(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getAPS Component for ValueBean
		selectFwdPersData.setChildPlcmnt(selectFwdPerDto, selectForwardPersonValueBean);
		// Get getAPS Component for ValueBean
		selectFwdPersData.setRelationships(selectFwdPerDto, selectForwardPersonValueBean);
		// need to display the user selections as saved during person merge
		if (idPersonMerge > 0) {
			HashMap selectFieldMap = personMergeSplitService.getPersonMergeSelectFieldMap((long) idPersonMerge);
			resetSelectionAsSavedInDB(selectForwardPersonValueBean, selectFieldMap);
		}
		Log.info("outside getFieldsToDisplay method in SelectForwardPersonDataManager class");
		return selectForwardPersonValueBean;
	}

	/**
	 * Method Name: getCurrIncmRsrc Description: Builds the Current Income and
	 * resources attributes ( display text , enabled/disabled, default) for
	 * ValueBean used in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @param
	 * @return
	 */
	private void setCurrIncmRsrc(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setCurrIncmRsrc method in SelectForwardPersonDataManager class");
		ArrayList<IncomeAndResourceDto> persClosedIncomeResources;
		ArrayList<IncomeAndResourceDto> persForwardIncomeResources;
		// sorted with the latest start dates
		if (selectFwdPerDto.getIdPersonMerge() == 0) {
			persClosedIncomeResources = personDtlService.getIncomeAndResourceList(
					(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(), INCM_RSRC_ORDER_BY_HIBERNATE, true);
			persForwardIncomeResources = personDtlService.getIncomeAndResourceList(
					(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(), INCM_RSRC_ORDER_BY_HIBERNATE, true);
		} else {
			persClosedIncomeResources = personDtlService.getIncomeAndResourceList(
					(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(), INCM_RSRC_ORDER_BY, true,
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			persForwardIncomeResources = personDtlService.getIncomeAndResourceList(
					(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(), INCM_RSRC_ORDER_BY, true,
					(long) selectFwdPerDto.getIdPersonMerge(), CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
		}
		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/* Initialize forward attribute if not employee / former Employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			// build curr income resource display list and get attribute to be
			// used
			// in commonDefaultDisableChk
			forwardPersonAttribute = setUpCurrIncmRsrcDisplayValues(selectForwardPersonValueBean,
					persForwardIncomeResources, Boolean.TRUE);
		}
		// build curr income resource display list and get attribute to be used
		// in
		// commonDefaultDisableChk for closed person
		closedPersonAttribute = setUpCurrIncmRsrcDisplayValues(selectForwardPersonValueBean, persClosedIncomeResources,
				Boolean.FALSE);
		// call function to set disabled and default strings for forward
		// and closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// if both closed and forward are identical enable closed person
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp() && selectFwdPerDto.getFwdClsdPrsnHasSameVal()) {
			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
		}
		// date as default
		if (!selectFwdPerDto.getForwardPersonDisabled() && !selectFwdPerDto.getClosedPersonDisabled()) {
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.FALSEVAL);
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.TRUEVAL);
			// Get person with Latest Income and resource start dates
			Date forwardPersLatestStrtDt = personDtlService
					.getLatestActiveIncmRsrcStartDate((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
			Date closedPersLatestStrtDt = personDtlService
					.getLatestActiveIncmRsrcStartDate((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			if ((null != forwardPersLatestStrtDt) && (null != closedPersLatestStrtDt)
					&& (closedPersLatestStrtDt.after(forwardPersLatestStrtDt))) {
				selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
				selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
			}
		}
		// //set disabled and default attributes for closed and forward person
		// in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getCurrIncmRsrc().getForwardPerson(),
				selectForwardPersonValueBean.getCurrIncmRsrc().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setCurrIncmRsrc method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: setUpCurrIncmRsrcDisplayValues Description: Builds the
	 * Current Income and resource list for input attribute , initializes
	 * primary key used for dirty read for each record ,returns a string that is
	 * used to set default and disabled attribute.
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param ArrayList
	 * @return String
	 */
	private String setUpCurrIncmRsrcDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			ArrayList<IncomeAndResourceDto> incmRsrcList, Boolean isFwdPerson) {
		Log.info("inside setUpCurrIncmRsrcDisplayValues method in SelectForwardPersonDataManager class");
		SelectForwardPersonValueBean.AttributeValueBean incmRsrcAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayCurrIncmRsrc = new ArrayList();
		String displayTextValue = null;
		String attributeValue = null;
		if ((null != incmRsrcList) && !ObjectUtils.isEmpty(incmRsrcList)) {
			for (IncomeAndResourceDto incmRsrc : incmRsrcList) {
				// Build display value
				displayTextValue = lookupDao.decode(CodesConstant.CINCRSRC,
						StringUtil.getNonNullString(incmRsrc.getType())) + INCM_RSRC_$
						+ FormattingUtils.formatDouble(incmRsrc.getAmount(), 2) + INCM_RSRC_START_DATE
						+ FormattingUtils.formatDate(incmRsrc.getEffectiveFrom());
				displayCurrIncmRsrc.add(displayTextValue);
				// build string to be returned for default / disbaled comparison
				if (StringUtil.isValid(attributeValue))
					attributeValue = displayTextValue;
				else
					attributeValue += displayTextValue;
				// initialize primary key data for dirty read
				if (incmRsrc.getIdIncRsrc() != null) {
					selectFwdCommonData.initPersonPrKeyDataList(incmRsrcAttribute, incmRsrc.getIdIncRsrc().intValue(),
							incmRsrc.getDtLastUpdate());
				}
			}
		} else // set up empty string row if no data
		{
			displayTextValue = StringUtil.EMPTY_STRING;
			displayCurrIncmRsrc.add(displayTextValue);
		}
		// set display values for attribute
		incmRsrcAttribute.setDisplayValues(displayCurrIncmRsrc);
		if (isFwdPerson) {
			selectForwardPersonValueBean.getCurrIncmRsrc().setForwardPerson(incmRsrcAttribute);
		} else {
			selectForwardPersonValueBean.getCurrIncmRsrc().setClosedPerson(incmRsrcAttribute);
		}
		Log.info("outside setUpCurrIncmRsrcDisplayValues method in SelectForwardPersonDataManager class");
		return attributeValue;
	}

	/**
	 * Method Name: getSchool Description: Builds the School Component with
	 * attributes ( display text , enabled/disabled, default) for ValueBean used
	 * in SelectForwardPersonData Page
	 * 
	 * @param selectFwdPerDto
	 * @param selectForwardPersonValueBean
	 * @return
	 * @param
	 * @return
	 */
	private void setSchool(SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setSchool method in SelectForwardPersonDataManager class");
		// Get current Education History record and respective Education Needs
		// records for closed and forward persons
		EducationHistoryDto persClosedEducationHistoryDto = null;
		ArrayList<EducationalNeedDto> persClosedEducationNeeds = null;
		EducationHistoryDto persForwardEducationHistoryDto = null;
		ArrayList<EducationalNeedDto> persForwardEducationNeeds = null;
		if (ObjectUtils.isEmpty(selectFwdPerDto.getIdPersonMerge())
				|| selectFwdPerDto.getIdPersonMerge().intValue() == 0) {
			persClosedEducationHistoryDto = personDtlService
					.getCurrentEducationHistoryById((long) selectFwdPerDto.getPersClosedValueBean().getIdPerson());
			if (!ObjectUtils.isEmpty(persClosedEducationHistoryDto)) {
				persClosedEducationNeeds = personDtlService
						.getEducationalNeedListForHist(persClosedEducationHistoryDto.getIdEdHist());
			}
			persForwardEducationHistoryDto = personDtlService
					.getCurrentEducationHistoryById((long) selectFwdPerDto.getPersForwardValueBean().getIdPerson());
			if (!ObjectUtils.isEmpty(persForwardEducationHistoryDto)) {
				persForwardEducationNeeds = personDtlService
						.getEducationalNeedListForHist(persForwardEducationHistoryDto.getIdEdHist());
			}
		} else {
			persClosedEducationHistoryDto = personDtlService.getCurrentEducationHistory(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
			if (!ObjectUtils.isEmpty(persClosedEducationHistoryDto)) {
				persClosedEducationNeeds = (ArrayList) personDtlService.getEducationalNeedListForHist(
						(long) selectFwdPerDto.getPersClosedValueBean().getIdPerson(),
						persClosedEducationHistoryDto.getIdEdHist(), (long) selectFwdPerDto.getIdPersonMerge(),
						CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			}
			persForwardEducationHistoryDto = personDtlService.getCurrentEducationHistory(CodesConstant.CACTNTYP_100,
					CodesConstant.CSSPRDTY_B, (long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
					(long) selectFwdPerDto.getIdPersonMerge());
			if (!ObjectUtils.isEmpty(persForwardEducationHistoryDto)) {
				persForwardEducationNeeds = (ArrayList) personDtlService.getEducationalNeedListForHist(
						(long) selectFwdPerDto.getPersForwardValueBean().getIdPerson(),
						persForwardEducationHistoryDto.getIdEdHist(), (long) selectFwdPerDto.getIdPersonMerge(),
						CodesConstant.CACTNTYP_100, CodesConstant.CSSPRDTY_B);
			}
		}

		String forwardPersonAttribute = null;
		String closedPersonAttribute = null;
		/* Initialize forward attributes if not Emp or forme Employee */
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp()) {
			/*
			 * Initialize forward person school attributes and initialize string
			 * to be used in commonDefaultDisableChk
			 */
			forwardPersonAttribute = setUpSchoolDisplayValues(selectForwardPersonValueBean,
					persForwardEducationHistoryDto, Boolean.TRUE);
			// setup school programs if there is valid school information
			setUpSchoolPrgmsDisplayValues(selectForwardPersonValueBean.getSchool().getForwardPerson(),
					persForwardEducationNeeds, forwardPersonAttribute);
		}
		/* Initialize closed attributes */
		/*
		 * Initialize closed person school attributes and initialize string to
		 * be used in commonDefaultDisableChk
		 */
		closedPersonAttribute = setUpSchoolDisplayValues(selectForwardPersonValueBean, persClosedEducationHistoryDto,
				Boolean.FALSE);
		// setup school programs if there is valid school information
		setUpSchoolPrgmsDisplayValues(selectForwardPersonValueBean.getSchool().getClosedPerson(),
				persClosedEducationNeeds, closedPersonAttribute);
		// call function to set disabled and default strings for forward
		// and
		// closed person
		selectFwdPerDto.setClosedPersonAttribute(closedPersonAttribute);
		selectFwdPerDto.setForwardPersonAttribute(forwardPersonAttribute);
		selectFwdCommonData.commonDefaultDisableChk(true, selectFwdPerDto);
		// if both closed and forward are identical enable closed person
		if (!selectFwdPerDto.getFwdPrsnEmpOrFrmrEmp() && selectFwdPerDto.getFwdClsdPrsnHasSameVal()) {
			selectFwdPerDto.setClosedPersonDisabled(ServiceConstants.FALSEVAL);
		}
		if (!selectFwdPerDto.getForwardPersonDisabled() && !selectFwdPerDto.getClosedPersonDisabled()
				&& !ObjectUtils.isEmpty(persForwardEducationHistoryDto)
				&& !ObjectUtils.isEmpty(persClosedEducationHistoryDto)
				&& ObjectUtils.isEmpty(persForwardEducationHistoryDto.getDtEdHistEnrollDate())
				&& persClosedEducationHistoryDto.getDtEdHistEnrollDate()
						.after(persForwardEducationHistoryDto.getDtEdHistEnrollDate())) {
			selectFwdPerDto.setClosedPersonDefault(ServiceConstants.TRUEVAL);
			selectFwdPerDto.setForwardPersonDefault(ServiceConstants.FALSEVAL);
		}

		// set disabled and default attributes for closed and forward person in
		// Value bean
		selectFwdCommonData.setDefaultDisableAttr(selectForwardPersonValueBean.getSchool().getForwardPerson(),
				selectForwardPersonValueBean.getSchool().getClosedPerson(), selectFwdPerDto);
		Log.info("outside setSchool method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: setUpSchoolDisplayValues Description: Set's up the school
	 * attributes using the input education history Value bean, initializes
	 * primary key used for dirty read ,returns a string that is used to set
	 * default and disabled attribute.
	 * 
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param EducationHistoryDto
	 * @return String
	 */
	private String setUpSchoolDisplayValues(SelectForwardPersonValueBean selectForwardPersonValueBean,
			EducationHistoryDto educationHistValueBean, Boolean isFwdPerson) {
		Log.info("inside setUpSchoolDisplayValues method in SelectForwardPersonDataManager class");
		SelectForwardPersonValueBean.AttributeValueBean schoolAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displayValues = new ArrayList();
		String attributeValue = null;
		if (!ObjectUtils.isEmpty(educationHistValueBean)) {
			// form string used by calling function in setting default and
			// disbaled
			// attributes, here it is set to Enroll date
			attributeValue = FormattingUtils.formatDate(educationHistValueBean.getDtEdHistEnrollDate());
			// set School Name for display
			displayValues.add(StringUtil.getNonNullString(educationHistValueBean.getNmEdHistSchool()));
			// set Enroll Date for display
			displayValues.add(attributeValue);
			// set Grade for display
			displayValues.add(lookupDao.decode(CodesConstant.CSCHGRAD,
					StringUtil.getNonNullString(educationHistValueBean.getCdEdHistEnrollGrade())));
			// set primary key of education history record in prkey Data
			// attribute
			selectFwdCommonData.initPrKeyData(schoolAttribute, educationHistValueBean.getIdEdHist().intValue(),
					educationHistValueBean.getDtLastUpdate());
		} else // if no education record then setup up 3 empty rows
		{
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
			displayValues.add(StringUtil.EMPTY_STRING);
		}
		schoolAttribute.setDisplayValues(displayValues);
		if (isFwdPerson) {
			selectForwardPersonValueBean.getSchool().setForwardPerson(schoolAttribute);
		} else {
			selectForwardPersonValueBean.getSchool().setClosedPerson(schoolAttribute);
		}
		Log.info("outside setUpSchoolDisplayValues method in SelectForwardPersonDataManager class");
		return attributeValue;
	}

	/**
	 * Method Name: setUpSchoolPrgmsDisplayValues Description: Builds the School
	 * Programs Display list for schoolPrgmsAttribute
	 * 
	 * @param attributeValueBean
	 * @return
	 * @param SelectForwardPersonValueBean.AttributeValueBean
	 * @param ArrayList
	 * @return
	 */
	private void setUpSchoolPrgmsDisplayValues(AttributeValueBean schoolAttributeValue,
			ArrayList<EducationalNeedDto> educNeeds, String attributeValue) {
		Log.info("inside setUpSchoolPrgmsDisplayValues method in SelectForwardPersonDataManager class");
		SelectForwardPersonValueBean.AttributeValueBean schoolAttribute = new SelectForwardPersonValueBean.AttributeValueBean();
		ArrayList displaySchoolPrgms = schoolAttributeValue.getDisplayValues();
		String displayTextValue = null;
		// values in schoolPrgmsAttribute
		if (CollectionUtils.isNotEmpty(educNeeds)) {
			for (EducationalNeedDto educNeed : educNeeds) {
				if (educNeed.getCdEducationalNeed() != null) {
					displayTextValue = lookupDao.decode(CodesConstant.CEDUCNED, educNeed.getCdEducationalNeed());
					displaySchoolPrgms.add(displayTextValue);
				} else {
					displaySchoolPrgms.add("");
				}
			}
		} else {
			// if School present display NONE indicated
			if (StringUtil.isValid(attributeValue))
				displayTextValue = NONE_INDICATED;
			else
				// if no school present display blank row
				displayTextValue = StringUtil.EMPTY_STRING;
			displaySchoolPrgms.add(displayTextValue);
		}
		schoolAttribute.setDisplayValues(displaySchoolPrgms);
		Log.info("outside setUpSchoolPrgmsDisplayValues method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: chkFwdPrsnEmpOrFrmrEmpOrExtStaff Description:for the input
	 * person id check if person is external employee, employee or former
	 * employee
	 * 
	 * @param idPerson
	 * @param selectForwardPersonValueBean
	 * @return
	 */
	private void chkFwdPrsnEmpOrFrmrEmpOrExtStaff(int idPerson, SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		/* retrieve employee category and employee detail record for person */
		Log.info("inside chkFwdPrsnEmpOrFrmrEmpOrExtStaff method in SelectForwardPersonDataManager class");
		ArrayList<PersonCategoryDto> catgArr = (ArrayList<PersonCategoryDto>) personDtlService
				.getPersonCategoryList((long) idPerson);
		String msgFwdPrsnEmpOrFrmrEmp = StringUtil.EMPTY_STRING;
		Map employeeDetail = personDetailService.getEmployeeTypeDetail((long) idPerson);
		if (null != catgArr) {
			// employee
			for (int count = 0; count < catgArr.size(); count++) {
				PersonCategoryDto b = catgArr.get(count);
				// if employee setup display message with employee
				if (CodesConstant.CPSNDTCT_EMP.equals(b.getCdPersonCategory())) {
					msgFwdPrsnEmpOrFrmrEmp += lookupDao.decode(CodesConstant.CPSNDTCT, CodesConstant.CPSNDTCT_EMP);
				}
				// if former employee setup display message with former employee
				if (CodesConstant.CPSNDTCT_FEM.equals(b.getCdPersonCategory())) {
					if (StringUtil.isValid(msgFwdPrsnEmpOrFrmrEmp)) {
						msgFwdPrsnEmpOrFrmrEmp += "/"
								+ lookupDao.decode(CodesConstant.CPSNDTCT, CodesConstant.CPSNDTCT_FEM);
					} else
						msgFwdPrsnEmpOrFrmrEmp += lookupDao.decode(CodesConstant.CPSNDTCT, CodesConstant.CPSNDTCT_FEM);
				}
			}
		}
		/* check if person is external employee */
		if (employeeDetail != null && employeeDetail.get(CD_EXTERNAL_TYPE) != null) {
			// SIR 1008736, changed these lines to use view instead of codes
			// table.
			if (StringUtil.isValid(msgFwdPrsnEmpOrFrmrEmp)) {
				msgFwdPrsnEmpOrFrmrEmp += "/" + "EXTERNAL STAFF-EXST";
			} else
				msgFwdPrsnEmpOrFrmrEmp += lookupDao.decode(CodesConstant.CEMPJBCL, CodesConstant.CEMPJBCL_EXST);
		}
		// if person is employee, former employee or external staff
		if (StringUtil.isValid(msgFwdPrsnEmpOrFrmrEmp)) {
			// set boolean instance variable that will be used during formation
			// of display
			// fields
			selectFwdPerDto.setFwdPrsnEmpOrFrmrEmp(Boolean.TRUE);
			// set up message that will be displayed on Select Forward Data page
			String message = StringUtil.EMPTY_STRING;
			message = lookupDao.getMessage(ServiceConstants.MSG_FWD_PRSON_EMP_SEL);
			message = FormattingUtils.addMessageParameter(message, msgFwdPrsnEmpOrFrmrEmp);
			setUpMessages(message, ServiceConstants.MSG_FWD_PRSON_EMP_SEL, selectForwardPersonValueBean);
		}
		Log.info("outside chkFwdPrsnEmpOrFrmrEmpOrExtStaff method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method name: chkFwdPrsnPCInOpnSTG Description: check if person is PC in
	 * an OPEN STAGE of (SUB,ADO,PAD,PCA,PAL)
	 * 
	 * @param idPerson
	 * 
	 * @return
	 */
	private void chkFwdPrsnPCInOpnSTG(int idPerson, SelectForwardPersonDataDto selectFwdPerDto) {
		Log.info("inside chkFwdPrsnPCInOpnSTG method in SelectForwardPersonDataManager class");
		// boolean variable set to indicate if person is in OPEN Stage
		boolean fwdPrsnPCInOpnSTG = personDtlService.isPrimaryChildInOpenStage(idPerson, chkStages);
		selectFwdPerDto.setFwdPrsnHasPCInOpnSTG(fwdPrsnPCInOpnSTG);
		Log.info("outside chkFwdPrsnPCInOpnSTG method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: chkFwdPrsnEligibilities Description: check if person has an
	 * active FC , PCA or AA Eligibility
	 * 
	 * @param idPerson
	 * @param selectForwardPersonValueBean
	 * 
	 * @return
	 */
	private void chkFwdPrsnEligibilities(int idPerson, SelectForwardPersonDataDto selectFwdPerDto,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside chkFwdPrsnEligibilities method in SelectForwardPersonDataManager class");
		String message = StringUtil.EMPTY_STRING;
		ArrayList eligList = null;
		// check if Foster Care eligibilty exist for input person
		eligList = (ArrayList) fceService.fetchActiveFceList((long) idPerson);
		String msgPrsnHasEligDODChk = selectFwdPerDto.getMsgPrsnEligDODChk();
		if (CollectionUtils.isNotEmpty(eligList)) {
			// if FCE exists setup message to be displayed on page
			// and set an indicator that will be used during displaying of
			// fields
			message = lookupDao.getMessage(ServiceConstants.MSG_FWD_PRSON_FCAE_SEL);
			setUpMessages(message, ServiceConstants.MSG_FWD_PRSON_FCAE_SEL, selectForwardPersonValueBean);
			selectFwdPerDto.setFwdPrsnHasElig(Boolean.TRUE);
			// set up message that might be displayed if person foward has
			// eligibilty and no
			// DOD
			// but closed has DOD
			msgPrsnHasEligDODChk += lookupDao.decode(CodesConstant.CINVSRTP, CodesConstant.CINVSRTP_FSC) + " "
					+ lookupDao.decode(CodesConstant.CEVNTTYP, CodesConstant.CEVNTTYP_AST);
		}
		// check if Adoption Assistance eligibilty exist for input person
		eligList = (ArrayList) adoptionAsstncService.fetchActiveAdpList((long) idPerson);
		if (CollectionUtils.isNotEmpty(eligList)) {
			// if AAE exists setup message to be displayed on page
			// and set an indicator that will be used during displaying of
			// fields
			message = lookupDao.getMessage(ServiceConstants.MSG_FWD_PRSON_AAE_SEL);
			setUpMessages(message, ServiceConstants.MSG_FWD_PRSON_AAE_SEL, selectForwardPersonValueBean);
			selectFwdPerDto.setFwdPrsnHasElig(Boolean.TRUE);
			selectFwdPerDto.setFwdPrsnHasAAElig(Boolean.TRUE);
			// but closed has DOD
			if (StringUtil.isValid(msgPrsnHasEligDODChk))
				msgPrsnHasEligDODChk += "/" + lookupDao.decode(CodesConstant.CEVNTTYP, CodesConstant.CEVNTTYP_ADP);
			else
				msgPrsnHasEligDODChk += lookupDao.decode(CodesConstant.CEVNTTYP, CodesConstant.CEVNTTYP_ADP);
		}
		// check if Permanency Care eligibilty exist for input person
		eligList = (ArrayList) pcaEligibilitySummaryService.fetchActivePcaList((long) idPerson);
		if (CollectionUtils.isNotEmpty(eligList)) {
			// if PCAE exists setup message to be displayed on page
			// and set an indicator that will be used during displaying of
			// fields
			message = lookupDao.getMessage(ServiceConstants.MSG_FWD_PRSON_PCAE_SEL);
			setUpMessages(message, ServiceConstants.MSG_FWD_PRSON_PCAE_SEL, selectForwardPersonValueBean);
			selectFwdPerDto.setFwdPrsnHasElig(Boolean.TRUE);
			// but closed has DOD
			if (StringUtil.isValid(msgPrsnHasEligDODChk))
				msgPrsnHasEligDODChk += "/" + lookupDao.decode(CodesConstant.CEVNTTYP, CodesConstant.CEVNTTYP_PCA);
			else
				msgPrsnHasEligDODChk += lookupDao.decode(CodesConstant.CEVNTTYP, CodesConstant.CEVNTTYP_PCA);
		}
		Log.info("outside chkFwdPrsnEligibilities method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: setUpMessages Description: Add message that needs to be
	 * displayed on Select Forward Data page to messages ArrayList in
	 * SelectForwardPersonValueBean
	 * 
	 * @param selectForwardPersonValueBean
	 * 
	 * @param String
	 *            message
	 * 
	 * @return
	 */
	protected void setUpMessages(String message, int messageNbr,
			SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside setUpMessages method in SelectForwardPersonDataManager class");
		ErrorListDto errorListMessage = new ErrorListDto();
		ArrayList messages = selectForwardPersonValueBean.getMessages();
		if (null == messages)
			messages = new ArrayList();
		errorListMessage.setErrorMessage(message);
		errorListMessage.setMsgNumber(messageNbr);
		messages.add(errorListMessage);
		selectForwardPersonValueBean.setMessages(messages);
		Log.info("outside setUpMessages method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: createPersonMergeFields Description: For all the fields in
	 * SelectForwardPersonValue Bean that are of type MergePersons create
	 * MergePersons object with closed and forward person and assign to it's
	 * repective field in SelectForwardPersonValue Bean( Doen through Relection)
	 * 
	 * @param selectForwardPersonValueBean
	 * 
	 * @param
	 * @return
	 */
	private void createPersonMergeFields(SelectForwardPersonValueBean selectForwardPersonValueBean) {
		Log.info("inside createPersonMergeFields method in SelectForwardPersonDataManager class");
		// read all fields declared with public access type
		Field[] selectForwardPersonfields = SelectForwardPersonValueBean.class.getDeclaredFields();
		// Loop through and check if field is of type MergePersons
		for (int count = 0; count < selectForwardPersonfields.length; count++) {
			Field selectForwardPersonField = selectForwardPersonfields[count];
			if (selectForwardPersonField.getType() == SelectForwardPersonValueBean.MergePersons.class) {
				// create merge person object with closed and forward peson and
				// set
				// field in SelectForwardPersonValue bean
				SelectForwardPersonValueBean.MergePersons mergePersons = new SelectForwardPersonValueBean.MergePersons();
				mergePersons.setClosedPerson(new SelectForwardPersonValueBean.AttributeValueBean());
				mergePersons.setForwardPerson(new SelectForwardPersonValueBean.AttributeValueBean());
				try {
					selectForwardPersonField.set(selectForwardPersonValueBean, mergePersons);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Log.info(
							"inside createPersonMergeFields method in SelectForwardPersonDataManager class, exception : "
									+ e.getMessage());
				}
			}
		}
		Log.info("outside createPersonMergeFields method in SelectForwardPersonDataManager class");
	}

	/**
	 * Method Name: resetSelectionAsSavedInDB Description: This method is called
	 * when select forward person data is being loaded for display in Post
	 * person merge page. In this case we mark the selections as user selected
	 * them during person merge. Also we mark all the fields as disabled.
	 * 
	 * @param sfPersValeBean
	 *            (SelectForwardPersonValueBean containing data of both persons)
	 * @param selectFieldMap
	 *            (Map containing fields and their selection source (Closed or
	 *            Forward)
	 */
	private void resetSelectionAsSavedInDB(SelectForwardPersonValueBean sfPersValeBean, HashMap selectFieldMap) {
		Log.info("inside resetSelectionAsSavedInDB method in SelectForwardPersonDataManager class");
		// get all the fields from SelectForwardPersonValueBean using reflection
		Field[] selectForwardPersonfields = SelectForwardPersonValueBean.class.getDeclaredFields();
		// and save the data accordingly in PERSON_MERGE_SELECT_FIELD table
		for (int count = 0; count < selectForwardPersonfields.length; count++) {
			Field selectForwardPersonField = selectForwardPersonfields[count];
			if (selectForwardPersonField.getType() == SelectForwardPersonValueBean.MergePersons.class) {
				selectForwardPersonField.setAccessible(true);
				SelectForwardPersonValueBean.MergePersons mergePersonsValueBean = new SelectForwardPersonValueBean.MergePersons();
				try {
					mergePersonsValueBean = (SelectForwardPersonValueBean.MergePersons) selectForwardPersonField
							.get(sfPersValeBean);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Log.info(
							"inside resetSelectionAsSavedInDB method in SelectForwardPersonDataManager class, exception : "
									+ e.getMessage());
				}
				String fieldName = null;
				// For saving to database, we need to use standard predefined
				// field names
				// so we need to do the mapping
				fieldName = SelectForwardPersonValueBean.getfieldNameMappingForDB(selectForwardPersonField.getName());
				if (fieldName == null)
					continue;
				// initialize closed person selection
				if (null != mergePersonsValueBean.getClosedPerson()) {
					mergePersonsValueBean.getClosedPerson().setIsDisabled(ServiceConstants.TRUE);
					mergePersonsValueBean.getClosedPerson().setIsDefault(ServiceConstants.FALSE);
				}
				// initialize closed person selection
				if (null != mergePersonsValueBean.getForwardPerson()) {
					mergePersonsValueBean.getForwardPerson().setIsDisabled(ServiceConstants.TRUE);
					mergePersonsValueBean.getForwardPerson().setIsDefault(ServiceConstants.FALSE);
				}
				String selectionSource = (String) selectFieldMap.get(fieldName);
				if (CodesConstant.CMRGPERS_C.equals(selectionSource)) {
					if (null != mergePersonsValueBean.getClosedPerson())
						mergePersonsValueBean.getClosedPerson().setIsDefault(ServiceConstants.TRUE);
				} else if (CodesConstant.CMRGPERS_F.equals(selectionSource)) {
					if (null != mergePersonsValueBean.getForwardPerson())
						mergePersonsValueBean.getForwardPerson().setIsDefault(ServiceConstants.TRUE);
				}
			}
		}
		// end for loop
		Log.info("Outside resetSelectionAsSavedInDB method in SelectForwardPersonDataManager class");
	}

	// Create an array with an ordered list of strings to check PC in OPEN Stage
	private static final String[] chkStages = new String[] { CodesConstant.CSTAGES_ADO, CodesConstant.CSTAGES_SUB,
			CodesConstant.CSTAGES_PAD, CodesConstant.CSTAGES_PCA, CodesConstant.CSTAGES_PAL };

	private static final String INCM_RSRC_ORDER_BY = "DT_INC_RSRC_FROM DESC";

	private static final String INCM_RSRC_ORDER_BY_HIBERNATE = "dtIncRsrcFrom";

	private static final String INCM_RSRC_$ = " -$";

	private static final String INCM_RSRC_START_DATE = "- Start Date: ";

	private static final String CD_EXTERNAL_TYPE = "CD_EXTERNAL_TYPE";

	private static final String NONE_INDICATED = "None Indicated";

	public PersonBean mappingPersonBean(PersonDto personDto) {
		PersonBean personBean = new PersonBean();
		personBean.setIdPerson(personDto.getIdPerson().intValue());
		personBean.setCdReasonForDeath(personDto.getCdPersonDeath());
		personBean.setCdEthnicGroup(personDto.getCdPersonEthnicGroup());
		personBean.setCdPrimaryLanguage(personDto.getCdPersonLanguage());
		personBean.setCdMaritalStatus(personDto.getCdPersonMaritalStatus());
		personBean.setCdMannerOfDeath(personDto.getCdMannerDeath());
		personBean.setCdDeathReasonCps(personDto.getCdDeathRsnCps());
		personBean.setCdDeathCause(personDto.getCdDeathCause());
		personBean.setCdDeathAutopsyResult(personDto.getCdDeathAutpsyRslt());
		personBean.setCdDeathFinding(personDto.getCdDeathFinding());
		personBean.setFatalityDetails(personDto.getFatalityDetails());
		personBean.setCdReligion(personDto.getCdPersonReligion());
		personBean.setSex(personDto.getCdPersonSex());
		personBean.setCdActiveInactiveMergedStatus(personDto.getCdPersonStatus());
		personBean.setDtDateOfBirth(personDto.getDtPersonBirth());
		personBean.setDtOfDeath(personDto.getDtPersonDeath());
		if (personDto.getPersonAge() == null)
			personBean.setAge(0);
		else
			personBean.setAge(personDto.getPersonAge());
		personBean.setFullName(personDto.getNmPersonFull());
		personBean.setOccupation(personDto.getTxtPersonOccupation());
		personBean.setCdOccupation(personDto.getCdOccupation());
		if (StringUtils.isNotEmpty(personDto.getIndPersCancelHist())
				&& ServiceConstants.Y.equals(personDto.getIndPersCancelHist()))
			personBean.setIndCancelHist(Boolean.TRUE);
		else
			personBean.setIndCancelHist(Boolean.FALSE);
		personBean.setCdGuardianshipConservatorship(personDto.getCdPersGuardCnsrv());
		personBean.setDtPersonDetailTableLastUpdate(personDto.getDtLastUpdate());
		personBean.setCdLivingArrangement(personDto.getCdPersonLivArr());
		personBean.setCdPersonCharacteristics(personDto.getCdPersonChar());
		if (StringUtils.isNotEmpty(personDto.getIndPersonDobApprox())
				&& ServiceConstants.Y.equals(personDto.getIndPersonDobApprox()))
			personBean.setIsApproxDateOfBirth(Boolean.TRUE);
		else
			personBean.setIsApproxDateOfBirth(Boolean.FALSE);
		personBean.setDisasterRelief(personDto.getCdDisasterRlf());
		if (StringUtils.isNotEmpty(personDto.getIndEducationPortfolio())
				&& ServiceConstants.Y.equals(personDto.getIndEducationPortfolio()))
			personBean.setIndEducationPortfolio(Boolean.TRUE);
		else
			personBean.setIndEducationPortfolio(Boolean.FALSE);
		personBean.setCdTribeEligible(personDto.getCdTribeEligible());
		personBean.setCounty(personDto.getCdPersonCounty());
		personBean.setPhone(personDto.getPersonPhone());
		personBean.setFirstName(personDto.getNmPersonFirst());
		personBean.setLastName(personDto.getNmPersonLast());
		personBean.setMiddleName(personDto.getNmPersonMiddle());
		personBean.setNmSuffix(personDto.getCdPersonSuffix());
		return personBean;
	}
}
