import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DbConnection {

    private Connection con;
    private int employee_id;

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
            employee_id = rs.getInt(1);
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

    public void testergEingeben() throws SQLException{
        String sql = "UPDATE tests SET result = ?, result_date = ? WHERE test_id = ?";

        PreparedStatement pStmt = con.prepareCall(sql);

        Scanner sc = new Scanner(System.in);
        System.out.print("Ergebnis eingeben: (p|n)");
        String erg = sc.next();
        if (erg.equalsIgnoreCase("p")){
            pStmt.setString(1, "positive");
        }else if (erg.equalsIgnoreCase("n")){
            pStmt.setString(1, "negative");
        }else {
            System.out.println("Falsche Eingabe!");
            return;
        }

        pStmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
        System.out.println("Bitte geben Sie eine Test Id ein: ");
        int tId = sc.nextInt();
        pStmt.setInt(3, tId);

        pStmt.executeQuery();
        System.out.println("Ergebnis eingefügt!");

    }

    public void testAnlegen() throws SQLException{
        String sql = "{? = call create_Test(?, ?, ?)}";
        CallableStatement cStmt = con.prepareCall(sql);
        Scanner sc = new Scanner(System.in);
        System.out.println("Bitte geben Sie eine Termin Id an:");
        int tId = sc.nextInt();
        cStmt.setInt(2, tId);
        cStmt.setString(3, "PCR");
        cStmt.setInt(4, employee_id);
        cStmt.registerOutParameter(1, Types.VARCHAR);

        cStmt.execute();

        System.out.println(cStmt.getString(1));


    }

    public void terminLoeschen() throws SQLException{
        Scanner sc = new Scanner(System.in);
        System.out.println("Geben Sie die Id vom Termin ein der gelöscht werden soll:");
        try{
            int del = Integer.parseInt(sc.next());

            String sql = "Delete from appointments where APPOINTMENTS_ID = ?";
            PreparedStatement pStmt = con.prepareStatement(sql);
            pStmt.setInt(1, del);
            pStmt.executeQuery();
            System.out.println("Termin gelöscht");

        } catch (NumberFormatException e) {
            System.out.println("Falsche Eingabe!");
        }

    }

    public void alleTermine() throws SQLException{

        Scanner sc = new Scanner(System.in);
        System.out.println("Alle Termine(a) oder nur Gebuchte(g)?");
        String auswahl = sc.next();
        String sql = "";
        if (auswahl.equalsIgnoreCase("g")){
             sql = "select * from appointments where CUSTOMER_ID IS NOT NULL ORDER by APPOINTMENTS_ID";
        }else{
             sql = "select * from appointments ORDER by APPOINTMENTS_ID";
        }

        PreparedStatement pStmt = con.prepareStatement(sql);
        ResultSet rs = pStmt.executeQuery();

        ResultSet count = con.prepareStatement(sql.replace("*", "count(*)")).executeQuery();
        count.next();
        System.out.println("Gefundene Termine: " + count.getInt(1));

        int counter = 0;
        while (rs.next()){
            System.out.println("ID: " + rs.getInt(1) + ", Kunde: " + rs.getInt(2)+ ", Datum: " + rs.getString(3));
            counter++;
            if(counter%10 == 0){
                while (true){
                    System.out.print("Nächste anzeigen(y|n):");
                    String in = sc.next();
                    if (in.equalsIgnoreCase("y")){
                        break;
                    }else if(in.equalsIgnoreCase("n")){
                        return;
                    }else{
                        System.out.println("Bitte nur y oder n eingeben");
                    }
                }
            }
        }
    }
}
