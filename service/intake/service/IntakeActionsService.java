package us.tx.state.dfps.service.intake.service;

import us.tx.state.dfps.service.common.response.IntNarrBlobRes;
import us.tx.state.dfps.service.common.response.RetrvCallEntryRes;
import us.tx.state.dfps.service.common.response.RtrvAllegRes;

public interface IntakeActionsService {
	public RetrvCallEntryRes getCallEntrynDecsn(Long idPerson, Long idStage);

	public IntNarrBlobRes getIntNarrBlobOutRec(Long idPerson);

	public RtrvAllegRes getAllegations(Long idStage);
}
