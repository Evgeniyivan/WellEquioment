package Service;

import Util.util;
import com.thoughtworks.xstream.XStream;
import model.DbInfo;
import model.Equipment;
import model.Well;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WellServImpl implements WellServ {
    private static final Connection connection = util.getConn();

    @Override
    public void createTable() throws SQLException {
        Statement st = connection.createStatement();

        st.executeUpdate("CREATE TABLE IF NOT EXISTS Well (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32) NOT NULL UNIQUE)");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS Equipment (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32) NOT NULL UNIQUE, Well_id INTEGER, FOREIGN KEY(Well_id) REFERENCES Well(id))");

        st.close();
    }

    @Override
    public void fillTable(String wellName, int amountEquipment) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT id FROM Well WHERE name = ?");
        ps.setString(1, wellName);
        ResultSet resultSet = ps.executeQuery();
        int idWell = 0;
        while (resultSet.next())
            idWell = resultSet.getInt("id");

        if (idWell == 0) {
            PreparedStatement ps2 = connection.prepareStatement("INSERT INTO Well (name) VALUES (?)");
            ps2.setString(1, wellName);
            ps2.executeUpdate();

            PreparedStatement ps3 = connection.prepareStatement("SELECT id FROM Well WHERE name = ?");
            ps3.setString(1, wellName);
            resultSet = ps3.executeQuery();
            idWell = resultSet.getInt("id");

            ps2.close();
            ps3.close();
        }
        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT COUNT(id) AS count FROM Equipment");
        int countEq = resultSet.getInt("count");

        PreparedStatement ps4 = connection.prepareStatement("INSERT INTO Equipment (name, Well_id) VALUES (?, ?)");

        for (int i = 1; i <= amountEquipment; i++) {
            String equipmentName = "EQ" + String.format("%04d", countEq + i);
            ps4.setString(1, equipmentName);
            ps4.setInt(2, idWell);
            ps4.executeUpdate();
        }
        ps.close();
        ps4.close();
        statement.close();
        resultSet.close();
    }


    @Override
    public void searchWell(String[] wellName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT id FROM Well WHERE name = ?");
        PreparedStatement ps2 = connection.prepareStatement("SELECT COUNT(id) AS count FROM Equipment WHERE Well_id = ?");


        try {
            System.out.println("Имя скважины и количество оборудования");
            for (String well : wellName) {
                ps.setString(1, well);
                ResultSet resultSet = ps.executeQuery();
                int id = resultSet.getInt("id");
                ps2.setInt(1, id);
                resultSet = ps2.executeQuery();
                while (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    System.out.println("   " + well + "             " + count);
                }

                resultSet.close();
            }
        } catch (Exception e) {
            System.out.println("Запрашиваемой скважины нет");
        }

        ps.close();
        ps2.close();
    }

    @Override
    public void allWellXml(String xmlFileName) throws SQLException, IOException {
        Statement st = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT `id`, `name` FROM Equipment WHERE `Well_id` = ?");

        DbInfo dbInfo = new DbInfo();
        List<Well> wellList = new ArrayList<>();
        XStream xs = new XStream();
        xs.autodetectAnnotations(true);

        ResultSet resultSet = st.executeQuery("SELECT * FROM Well;");

        while (resultSet.next())
        {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");

            Well well = new Well();
            well.setWellId(id);
            well.setWellName(name);

            preparedStatement.setInt(1, id);
            ResultSet resultSet2 = preparedStatement.executeQuery();

            List<Equipment> equipmentList = new ArrayList<>();

            while (resultSet2.next())
            {
                int idEquipment = resultSet2.getInt("id");
                String nameEquipment = resultSet2.getString("name");

                Equipment equipment = new Equipment();
                equipment.setEquipmentId(idEquipment);
                equipment.setEquipmentName(nameEquipment);

                equipmentList.add(equipment);
            }

            well.setEquipmentList(equipmentList);
            wellList.add(well);
            resultSet2.close();
        }

        dbInfo.setWellList(wellList);
        String xml = xs.toXML(dbInfo);

        Path pathXMLFile = Paths.get("" + xmlFileName + ".xml");
        Files.write(pathXMLFile, xml.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        System.out.println("Файл " + xmlFileName + ".xml создан.");

        st.close();
        preparedStatement.close();
        resultSet.close();

    }
}
