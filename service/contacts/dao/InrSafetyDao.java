package us.tx.state.dfps.service.contacts.dao;


import us.tx.state.dfps.service.admin.dto.ContactDetailSaveDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.InrSafetyFieldDto;
import java.util.List;

public interface InrSafetyDao {
    public List<InrSafetyFieldDto> getFollowUpList(ContactFieldDiDto contactFieldDiDto);
    public void updateFollowupDetails(ContactDetailSaveDiDto contactDetailSaveDiDto);
    public void deleteActionItemsByGroup(Long groupNum);

}
