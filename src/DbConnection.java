import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DbConnection {

    private Connection con;

    public DbConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.OracleDriver");
        String kennung = "inf2871";
        String pwd = "12345678";
        String url="jdbc:oracle:thin:@studidb.gm.th-koeln.de:1521:vlesung";

        con = DriverManager.getConnection(url, kennung, pwd);
        //con.setAutoCommit(true);
    }

    public boolean mitarbeiterLogin(String name, String pwd) throws SQLException {
        String sql = "select * from employee where name = ? and password = ?";
        System.out.println("select * from employee where name = "+name+" and password = "+pwd);
        PreparedStatement pStmt = con.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setString(2, pwd);
        ResultSet rs = pStmt.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public void profilZeigen(String name, String pwd) throws SQLException {
        String sql = "select * from employee where name = ? and password = ?";
        PreparedStatement pStmt = con.prepareStatement(sql);
        pStmt.setString(1, name);
        pStmt.setString(2, pwd);
        ResultSet rs = pStmt.executeQuery();
        if (rs.next()) {
            String birthday = rs.getString(4);
            int plz = rs.getInt(5);
            int tcId = rs.getInt(6);
            System.out.println("Gebutstag: "+ birthday);
            System.out.println("PLZ: "+ plz);
            System.out.println("Testcenter Id: "+ tcId);
        } else {
            System.out.println("Benutzter nicht mehr vorhanden!!");
        }

    }

    public void zeigeStatistic() throws SQLException {
        String sql = "Select * from STATISTICS";
        PreparedStatement pStmt = con.prepareStatement(sql);
        ResultSet rs = pStmt.executeQuery();
        while (rs.next()){
            int tcId = rs.getInt(2);
            int positiv = rs.getInt(3);
            int negativ = rs.getInt(4);
            System.out.println("Testcenter: "+ tcId);
            System.out.println("Positive Tests: " + positiv + " Negative Tests: " + negativ);
        }
    }

    public void tagestermine() throws SQLException {
        String sql = "Select * from DAILY_CUSTOMER_APPOINTMENTS";
        PreparedStatement pStmt = con.prepareStatement(sql);
        ResultSet rs = pStmt.executeQuery();
        while (rs.next()){
            int kuId = rs.getInt(1);
            String datum = rs.getString(2);
            System.out.println("Kunde: " +kuId + " hat einen Termin am: " +datum);
        }
    }

    public void termineErstellen() throws SQLException {
        String sql = "{? = call create_appointments(?, ?)}";
        CallableStatement cStmt = con.prepareCall(sql);
        Scanner sc = new Scanner(System.in);
        System.out.println("Bitte geben Sie ein Datum ein im Format: yyyy/mm/dd");
        String datum = sc.next();
        try {
            Date d =  new SimpleDateFormat("yyyy/MM/dd").parse(datum);
            cStmt.setDate(2, new java.sql.Date(d.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("Bitte geben Sie die Testcenter Id ein: ");
        int tcId = sc.nextInt();
        cStmt.setInt(3, tcId);

        cStmt.registerOutParameter(1, Types.VARCHAR);
        cStmt.execute();
        String returnValue = cStmt.getString(1);
        System.out.println(returnValue);
    }
}
