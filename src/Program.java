import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Program {

    private static DbConnection con;

    private static String name;
    private static String pwd;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            con = new DbConnection();

            System.out.println("Bitte Melden Sie sich an:");
            System.out.print("Name: ");
            //name = sc.next();
            String name = "Alex";
            System.out.print("Passwort: ");
            //pwd = sc.next();
            String pwd = "1234";

            while (true){
                boolean login = con.mitarbeiterLogin(name, pwd);
                if (login){
                    System.out.println("Sie haben sich erfolgreich angemeldet!");
                    break;
                }
                System.out.println("Fehler bei der Anmeldung bitte versuchen Sie es erneut!");

                System.out.print("Name: ");
                name = sc.next();
                System.out.print("Passwort: ");
                pwd = sc.next();

            }

            //New Code
            //Menu
            menu: while (true){
                System.out.println("--------- Menü ---------");
                System.out.println("Wählen Sie eine Option aus:");
                System.out.println("Testcenter Statistik anzeigen: 1");
                System.out.println("Tagestermine anzeigen: 2");
                System.out.println("Termin Löschen: 3");
                System.out.println("Test anlegen: 4");
                System.out.println("Testergebnis eintragen: 5");
                System.out.println("Termine erstellen: 6");
                System.out.println("Profil anzeigen: 7");
                System.out.println("Programm beenden: 8");

                try {
                    int eingabe = Integer.parseInt(sc.next());
                    if (eingabe < 1 || eingabe > 8) throw new NumberFormatException();
                    switch (eingabe){
                        case 1: zeigeStatistic(); break;
                        case 2: tagestermine(); break;
                        case 3: terminLoeschen(); break;    //TODO
                        case 4: testAnlegen(); break;       //TODO
                        case 5: testergEingeben(); break;   //TODO
                        case 6: termineErstellen(); break;
                        case 7: profilZeigen(); break;
                        case 8: break menu;
                        default:
                            System.out.println("Falsche Eingabe!");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Nur zahlen zwischen 1-8 eingeben!");
                }

            }

            System.out.println("Programm beendet!");

            //System.out.println("E: " + email + " P: " + pwd);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Verbindung mit der Datenbank fehlgeschlagen. Programm kann nicht ausgeführt werden!");
        }

    }

    private static void profilZeigen() {
        System.out.println("--- Profil anzeigen ---");
        try {
            con.profilZeigen(name, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void termineErstellen() {
        System.out.println("--- Termine Erstellen ---");
        try {
            con.termineErstellen();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void testergEingeben() {
        System.out.println("--- Testergebnis Eingeben ---");
    }

    private static void testAnlegen() {
        System.out.println("--- Test anlegen ---");
    }

    private static void terminLoeschen() {
        System.out.println("--- Termin Löschen ---");
    }

    private static void tagestermine() {
        System.out.println("--- Tagestermine anzeigen ---");
        try {
            con.tagestermine();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void zeigeStatistic() {
        System.out.println("--- Testcenter Statistik anzeigen ---");
        try {
            con.zeigeStatistic();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
