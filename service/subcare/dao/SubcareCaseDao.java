package us.tx.state.dfps.service.subcare.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.subcare.dto.SubcareChildContactDto;
import us.tx.state.dfps.service.subcare.dto.SubcareLegalEnrollDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dao class
 * for Subcare Case Management Tool form csc40o00 May 8, 2018- 2:47:57 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface SubcareCaseDao {

	/**
	 * Method Name: getTcmContact CSECB1D Method Description: This DAM retreives
	 * the most recent TCM contact
	 * 
	 * @param idStage
	 * @return Date
	 */
	public Date getTcmContact(Long idStage);

	/**
	 * Method Name: getPlcmntContact CSECB2D Method Description: This DAM finds
	 * the most recent placement contact.
	 * 
	 * @param idStage
	 * @return Date
	 */
	public Date getPlcmntContact(Long idStage);

	/**
	 * Method Name: getAppointment CSECB3D Method Description: This will find
	 * the latest medical/mental appointment based on reason for the assessment
	 * 
	 * @param idPerson
	 * @param cdProfAssmtApptRsn
	 * @return Date
	 */
	public Date getAppointment(Long idPerson, String cdProfAssmtApptRsn);

	/**
	 * Method Name: getRemoval CSECB4D Method Description:This DAM gets key
	 * information from the most recent conservators** hip removal for the PC in
	 * the subcare stage
	 * 
	 * @param idVictim
	 * @param idCase
	 * @return Date
	 */
	public SubcareChildContactDto getRemoval(Long idVictim, Long idCase);

	/**
	 * Method Name: getHearingDate CSECB5D Method Description: DAM returns the
	 * LAST REVIEW HEARING date
	 * 
	 * @param idStage
	 * @return SubcareChildContactDto
	 */
	public Date getHearingDate(Long idStage);

	/**
	 * Method Name: getLatestPpt CSECB7D Method Description: This finds the
	 * latest PPT for the stage.
	 * 
	 * @param idStage
	 * @return Date
	 */
	public Date getLatestPpt(Long idStage);

	/**
	 * Method Name: getChildFpos CSECB8D Method Description:This DAM finds the
	 * date of the most recent FPOS for the Primary Child
	 * 
	 * @param idPerson
	 * @return Date
	 */
	public Date getChildFpos(Long idPerson);

	/**
	 * Method Name: getChildPlan CSVC46D Method Description: Gets latest
	 * approved child plan
	 * 
	 * @param idPerson
	 * @return SubcareChildContactDto
	 */
	public SubcareChildContactDto getChildPlan(Long idPerson);

	/**
	 * Method Name: getGmthContact CSVC47D Method Description: Queries for most
	 * recent GMTH contact for a given stage, most recent based on date of
	 * contact (dt_contact_occurred)
	 * 
	 * @param idStage
	 * @return SubcareChildContactDto
	 */
	public SubcareChildContactDto getGmthContact(Long idStage);

	/**
	 * Method Name: getLegalStatus CSES78D Method Description:This dam retrieves
	 * a full row from LEGAL_STATUS.
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return SubcareLegalEnrollDto
	 */
	public SubcareLegalEnrollDto getLegalStatus(Long idCase, Long idPerson);

	/**
	 * Method Name: getStatusDeterm CLSS64D Method Description:This DAM will
	 * determine whether an Id_Person passed in has the Adoption Consumated Code
	 * as its most recent Legal Status.
	 * 
	 * @param idPerson
	 * @param cdLegalStatStatus
	 * @return SubcareLegalEnrollDto
	 */
	public SubcareLegalEnrollDto getStatusDeterm(Long idPerson, String cdLegalStatStatus);

	/**
	 * Method Name: getGreatestEnroll CSES33D Method Description: This Dam will
	 * do a full row retrieval of EDUCATION_HIST with the greatest enroll date
	 * 
	 * @param idPerson
	 * @return SubcareLegalEnrollDto
	 */
	public SubcareLegalEnrollDto getGreatestEnroll(Long idPerson);

	/**
	 * 
	 * Method Name: getSchoolProgramList Method Description: get school programs
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<String> getSchoolProgramList(Long idPerson);

	/**
	 * 
	 * Method Name: getConcurrentGoals Method Description: get concurrect goals
	 * 
	 * @param idCase
	 * @return
	 */
	public List<String> getConcurrentGoals(Long idCase);

	/**
	 * 
	 * Method Name: getCVSMonthlyList Method Description: get the current and
	 * last contact date
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return
	 */
	public List<Date> getCVSMonthlyList(Long idStage, Long idPerson);

	/**
	 * 
	 * Method Name: getNextDate Method Description: get the next hearing date
	 * 
	 * @param idStage
	 * @return
	 */
	public Date getNextDate(Long idStage);

	/**
	 * 
	 * Method Name: getToDoListType Method Description:get the to do list type
	 * 
	 * @param idStage
	 * @return
	 */
	public List<CaseInfoDto> getToDoListType(Long idCase);

	/**
	 * 
	 * Method Name: getIndication Method Description: To get portfolio
	 * indication
	 * 
	 * @param idPerson
	 * @return
	 */
	public String getIndication(Long idPerson);
}
