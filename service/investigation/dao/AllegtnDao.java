package us.tx.state.dfps.service.investigation.dao;

import java.util.Date;
import java.util.List;
import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.request.DisplayAllegDtlReq;
import us.tx.state.dfps.service.common.response.DisplayAllegDtlRes;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.investigation.dto.PerpVictmDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 10:23:52 AM
 */
public interface AllegtnDao {
	/**
	 *
	 * Method Description : This method will return the child death details by personId.
	 *
	 * @param personId
	 * @return
	 */
	boolean hasChildDeathReportCompleted(Long personId);
	/**
	 * 
	 * Method Description: legacy service name - CINV68D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<AllegationDetailDto> getInvAdminStageAllegn(Long uidStage);

	/**
	 * Retrieves rows for the Allegation List Method Description:legacy service
	 * name - CINV90D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<AllegationDetailDto> getDeliveryStageAllegn(Long uidStage);

	/**
	 * 
	 * Method Description: legacy service name - CINV91D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<AllegationDetailDto> getIntakeStageAllegn(Long uidStage);

	/**
	 * this method is to update allegation record
	 * 
	 * @param allegation
	 * @return @
	 */
	Long updateAllegation(Allegation allegation, String operation, boolean indFlush);

	/**
	 * this method is to get id victim for the allegation
	 * 
	 * @param idAllegation
	 * @return @
	 */
	Long getpersonIdVictim(Long idAllegation);

	/**
	 * This methos is to get allegation details by stage id DAM - CINVF8D
	 * 
	 * @param displayAllegDtlReq
	 * @return
	 */
	List<AllegtnPrsnDto> getAllegtnDtlByIdStage(DisplayAllegDtlReq displayAllegDtlReq);

	/**
	 * This method is to get allegation details DAM - CINV06D
	 * 
	 * @param displayAllegDtlReq
	 * @return
	 */
	DisplayAllegDtlRes getAllegtnDtlByIdAlegtn(DisplayAllegDtlReq displayAllegDtlReq);

	/**
	 * Retrieves the number of allegations in the stage in which the person is
	 * named as victim, not including the current allegation DAM - CINVB2D
	 * 
	 * @return Integer @
	 */
	Integer getVictimCount(Long idPerson, Long idStage, Long idAllegtn);

	/**
	 * Retrieves the number of allegations in the stage in which the person is
	 * named as perpetrator, not including the current allegation DAM - CINVB3D
	 * 
	 * @return Integer @
	 */
	Integer getPerpCount(Long idPerson, Long idStage, Long idAllegtn);

	/**
	 * Determines the number of rows in ALLEGATION having the same "who did what
	 * to whom" of the row to be deleted. DAM - CINVA1D
	 * 
	 * @param allegationDetail
	 * @return @
	 */
	Boolean findDuplicates(AllegationDetailDto allegationDetail, String operation);

	/**
	 * This retrieves a list of the dispositions that have been assigned to the
	 * allegations in this stage of service. DAM - CINVA7D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<String> getDispositionsList(Long uidStage);

	/**
	 * This DAM selects all diad's from the allegation table within a particular
	 * stage- DAM - CINVB7D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<AllegtnPrsnDto> getVictimUnKnownPerp(Long uidStage);

	/**
	 * This DAM selects all victim id's in a stage and a list of distinct
	 * dispositions per victim. The list of victims does not include anyone who
	 * is both a victim and an alleged perpetrator within the stage. DAM -
	 * CINVA0D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<AllegtnPrsnDto> getVictimNonPerp(Long uidStage);

	/**
	 * This method selects all alleged perpetrator id's in a stage and a list of
	 * disinct dispositions per alleged perpetrator. The list of alleged
	 * perpetrators does not include anyone who is both a victim and an alleged
	 * perpetrator within the stage DAM - CINVA5D.
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<AllegtnPrsnDto> getPerpNonVictim(Long uidStage);

	/**
	 * This DAM selects all person id's from ALLEGATION for a given stage who
	 * are both victims and alleged perpetrators, along with all dispositions
	 * associated with the selected person id's. DAM - CINVA9D
	 * 
	 * @param uidStage
	 * @return @
	 */
	List<PerpVictmDto> getPerpAndVictim(Long uidStage);

	/**
	 * This DAM adds, updates, or deletes a full row in the ALLEGATION table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINV07D
	 * 
	 * @param idAdminReview
	 * @return @
	 */
	Allegation getAllegationById(Long idAllegation);

	/**
	 * This DAM is used by the CloseOpenStage common function (CCMN03U) to add a
	 * dummy row to the FACIL_ALLEG table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINVB4D
	 * 
	 * @param facilAlleg
	 * @
	 */
	void saveFacilAlleg(FacilAlleg facilAlleg);

	Date fetchDtIntakeForIdStage(Long ulIdStage);

	/**
	 * get List of Alleged Victims
	 * 
	 * @param idStage
	 * @return List<Person>
	 */
	public List<Person> getVictimsByIdStage(Long idStage);

	public List<Allegation> getAllegationsByIdStage(Long idStage);

	List<PerpVictmDto> getAllegationDetailHistoryList(DisplayAllegDtlReq displayAllegDtlReq);

    int getAllegationProblemCount(Long idAllegation);

	public Integer getInjuryAllegationCount(Long allegationId);


	void deleteSPSourceForAllegationByAllegationId(Long idStage, Long idAllegation);

	public boolean getValidAllegations(Long idCase);



}
