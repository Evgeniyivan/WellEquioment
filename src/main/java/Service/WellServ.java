package Service;

import java.io.IOException;
import java.sql.SQLException;

public interface WellServ {
    void createTable() throws SQLException;

    void fillTable(String wellName, int amountEquipment) throws SQLException;

    void searchWell(String[] wellName) throws SQLException;

    void allWellXml(String xmlFileName) throws SQLException, IOException;

}
