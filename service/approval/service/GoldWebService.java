package us.tx.state.dfps.service.approval.service;

import org.w3c.dom.Document;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;

import java.sql.SQLException;

public interface GoldWebService {

    Long insertFormSendData(Long eventId);

    boolean updateFormSendData(GoldCommunicationDto goldCommunicationDto);

    GoldCommunicationDto sendAndSave(Long idEvent) throws SQLException;

}
