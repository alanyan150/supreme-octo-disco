import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connector connector = new Connector();
        //connector.c1("Users");
        Scanner input = new Scanner(System.in);
        boolean done = false;
        boolean isvalid = true;
        do {
            if (isvalid) {
                System.out.print("""
                        Main Menu
                            1. Lookup user
                            2. Follow user
                            3. Interact with post
                            4. Cancel event
                            5. boo
                            6. Quit
                        """);
            }
            try {
                System.out.print("Please enter your option: ");
                int choice = input.nextInt();
                isvalid = true;
                switch (choice) {
                    case 1:
                        lookup(connector);
                        break;
                    case 2:
                        followuser();
                        break;
                    case 3:
                        interactpost();
                        break;
                    case 4:
                        System.out.println(4);
                        break;
                    case 5:
                        System.out.println(5);
                        break;
                    case 6:
                        System.out.println("Bye!");
                        done = true;
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                isvalid = false;
                input.next();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }
        } while (!done);

        connector.close();
    }

    public static void lookup(Connector c) {
        System.out.print("Username: ");
        try {
            Scanner input = new Scanner(System.in);
            String choice = input.nextLine();
            c.q1(choice);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
        }
    }

    public static void followuser() {
        System.out.print("Enter user name: ");
    }

    public static void interactpost() {
    }
}
