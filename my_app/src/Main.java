import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner input = new Scanner(System.in);
    static int loginId = 0;

    public static void main(String[] args) throws SQLException {
        Connector connector = new Connector();
        login(connector);
        boolean done = false;
        boolean isvalid = true;
        do {
            if (isvalid) {
                System.out.print("Main Menu\n" +
                                 "    1. Lookup user\n" +
                                 "    2. Follow user\n" +
                                 "    3. Post a post\n" +
                                 "    4. Cancel event\n" +
                                 "    5. boo\n" +
                                 "    6. Quit\n");
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
                        followuser(connector, loginId);
                        break;
                    case 3:
                        postPost(connector);
                        break;
                    case 4:
                        q4();
                        break;
                    case 5:
                        q5();
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
            }
        } while (!done);

        connector.close();
    }

    /**
     * "Login" without password for convenience
     */
    public static void login(Connector c) {
        System.out.println("Use test to test out the application");
        boolean exists = false;
        do {
            try {
                System.out.print("Login as: ");
                input = new Scanner(System.in);
                String username = input.nextLine();
                int temp = c.login(username);
                if (temp != -1) {
                    loginId = temp;
                    exists = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Username not found!");
            }
        } while (!exists);
    }

    public static void lookup(Connector c) {
        System.out.print("Username: ");
        try {
            input = new Scanner(System.in);
            String username = input.nextLine();
            c.q1(username);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
        }
    }

    public static void followuser(Connector c, int userid) {
        System.out.print("Enter user name: ");
        try {
            input = new Scanner(System.in);
            String username = input.nextLine();
            c.q2(userid, username, input);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
        }
    }

    public static void postPost(Connector c) {
        try {
            input = new Scanner(System.in);
            boolean valid = false;
            System.out.print("Enter caption: ");
            String caption = input.nextLine();
            String privacy;
            do {
                System.out.print("Privacy [PUB/PRV/FRO]: ");
                privacy = input.nextLine();
                if (!privacy.equalsIgnoreCase("PUB")
                        && !privacy.equalsIgnoreCase("PRV")
                        && !privacy.equalsIgnoreCase("FRO")) {
                    System.out.println("Invalid input!");
                } else {
                    privacy = privacy.toUpperCase();
                    valid = true;
                }
            } while (!valid);
            System.out.print("Enter filename: ");
            String filename = input.nextLine();
            System.out.print("Enter location: ");
            String location = input.nextLine();
            System.out.print("Enter tags: ");
            String tags = input.nextLine();
            c.q3(loginId, caption, privacy, filename, location, tags, input);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
        }
    }

    public static void q4() {
    }

    public static void q5() {
    }
}
