package us.tx.state.dfps.service.forms.dao;

import java.util.List;

import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormGroupsDto;
import us.tx.state.dfps.service.forms.dto.GroupBookmarkDto;

public interface BookmarkDao {

	public List<BookmarkDto> selectFormFieldsBookmarks(String formName);

	public List<GroupBookmarkDto> selectGroupBookmarks(String formName);

	List<FormGroupsDto> getFormGroupsByNmForm(String formName);

    List<FormGroupsDto> getFormGoupsJoinGroupLink(Long groupId);

	List<String> getNmBookMark(String formName);

	List<String> getNmBookMarkFromFormFields(String formDescription);
}
