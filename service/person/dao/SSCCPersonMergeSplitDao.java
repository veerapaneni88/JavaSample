package us.tx.state.dfps.service.person.dao;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: DAO
 * Interface for SSCCPersonMergeSplit May 31, 2018- 11:19:25 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface SSCCPersonMergeSplitDao {

	/**
	 * 
	 * Method Name: updatePersonOnSSCCPlcmtRsrcLinkMC Method Description:update
	 * Person On SSCCPlcmtRsrcLinkMC
	 * 
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	void updatePersonOnSSCCPlcmtRsrcLinkMC(int idClosedPerson, int idFwdPerson);

	/**
	 * 
	 * Method Name: fetchSSCCReferralsForPersonInOpenStages Method
	 * Description:fetch SSCCReferrals For Person In Open Stages
	 * 
	 * @param idClosedPerson
	 * @param forMedicalConsenter
	 * @return
	 */
	List<BigDecimal> fetchSSCCReferralsForPersonInOpenStages(int idClosedPerson, boolean forMedicalConsenter);

	/**
	 * 
	 * Method Name: updatePersonOnSSCCRefFamily Method Description:update Person
	 * On SSCCRefFamily
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	void updatePersonOnSSCCRefFamily(int idSsccReferral, int idClosedPerson, int idFwdPerson);

	/**
	 * 
	 * Method Name: updatePersonOnSSCCPlcmtCircumstance Method
	 * Description:update Person On SSCCPlcmtCircumstance
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	void updatePersonOnSSCCPlcmtCircumstance(int idSsccReferral, int idClosedPerson, int idFwdPerson);

	/**
	 * 
	 * Method Name: updatePersonOnSSCCChildPlanParticipant Method
	 * Description:update Person On SSCCChildPlanParticipant
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	void updatePersonOnSSCCChildPlanParticipant(int idSsccReferral, int idClosedPerson, int idFwdPerson);

	/**
	 * 
	 * Method Name: updatePersonOnSSCCPlcmtMedConsenter Method
	 * Description:update Person On SSCCPlcmtMedConsenter
	 * 
	 * @param idSsccReferral
	 * @param idClosedPerson
	 * @param idFwdPerson
	 */
	void updatePersonOnSSCCPlcmtMedConsenter(int idSsccReferral, int idClosedPerson, int idFwdPerson);

}
