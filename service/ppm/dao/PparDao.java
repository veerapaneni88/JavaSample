package us.tx.state.dfps.service.ppm.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:dao methods
 * for Permanency planning May 29, 2018- 4:25:40 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PparDao {

	/**
	 * Method Name: getPPTParticipant CLSS05D Method Description: This DAM
	 * selects a full row with id_event as the input.
	 * 
	 * @param idEvent
	 * @return PRTParticipantDto
	 */

	public List<PRTParticipantDto> getPPTParticipant(Long idEvent);

	/**
	 * Method Name: getEventPerson Method Description: From DAM for csec22d This
	 * DAM joins the Event, Event Person Link, and Service Plan to retrieve the
	 * most recent Service plan for a given id stage and id person.
	 * 
	 * @param idPerson
	 * @return String
	 */
	public String getEventPerson(Long idPerson);

	/**
	 * Method Name: getEventPerson Method Description: From DAM for CSEC31D This
	 * dam will retrieve a PPT record previous to a given PPT record.
	 * 
	 * @param idStage
	 * @return PptDetailsOutDto
	 */
	public PptDetailsOutDto getEventPpt(Long idStage);

	/**
	 * Method Name: getUnitInfo Method Description: From DAM for CSEC19D This
	 * DAM joins the stage table and unit table given the ID STAGE.
	 * 
	 * @param idStage
	 * @return UnitDto
	 */
	public UnitDto getUnitInfo(Long idStage);

	/**
	 * Method Name: getPlcmt Method Description: From DAM for CSEC32D This DAM
	 * will retreive an Account Placement from the PLACEMENT table where ID
	 * PERSON = the host and Dt Plcmt Strt <= input date and input date =< Max
	 * and IND PLCMT ACT PLANNED = true
	 * 
	 * @param idPlcmtChild,dtLastUpdateDate
	 * @return PlacementAUDDto
	 */
	public PlacementAUDDto getPlcmt(Long idPlcmtChildId, Date dtLastUpdateDate);

}
