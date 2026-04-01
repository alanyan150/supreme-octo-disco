import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner input = new Scanner(System.in);
    static int loginId = 0;

    public static void main(String[] args) {
        Connector connector = null;
        try {
            connector = new Connector();
            login(connector);
            boolean done = false;
            boolean showMenu = true;
            do {
                if (showMenu) {
                    System.out.print("\n=== Social Media Main Menu ===\n"
                            + "    1. Look up user\n"
                            + "    2. Follow a user\n"
                            + "    3. Create a post\n"
                            + "    4. Join a group\n"
                            + "    5. View private messages\n"
                            + "    6. Send a private message\n"
                            + "    7. Quit\n");
                }
                showMenu = true;
                System.out.print("Please enter your option: ");
                try {
                    int choice = Integer.parseInt(input.nextLine().trim());
                    switch (choice) {
                        case 1: lookup(connector);       break;
                        case 2: followUser(connector);   break;
                        case 3: createPost(connector);   break;
                        case 4: joinGroup(connector);    break;
                        case 5: viewMessages(connector); break;
                        case 6: sendMessage(connector);  break;
                        case 7:
                            System.out.println("Goodbye!");
                            done = true;
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter 1-7.");
                            showMenu = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    showMenu = false;
                }
            } while (!done);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connector != null) {
                try {
                    connector.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public static void login(Connector c) {
        System.out.println("Hint: try 'byule4' as the username to test the application.");
        boolean exists = false;
        do {
            System.out.print("Login as (username): ");
            String username = input.nextLine().trim();
            int temp = c.login(username);
            if (temp != -1) {
                loginId = temp;
                exists = true;
                System.out.println("Logged in as '" + username + "' (userid=" + loginId + ").");
            }
        } while (!exists);
    }

    public static void lookup(Connector c) {
        System.out.print("Enter username to look up: ");
        c.q1(input.nextLine().trim());
    }

    public static void followUser(Connector c) {
        System.out.print("Enter username to follow: ");
        c.q2(loginId, input.nextLine().trim(), input);
    }

    public static void createPost(Connector c) {
        System.out.print("Caption: ");
        String caption = input.nextLine();
        String privacy;
        do {
            System.out.print("Privacy [PUB/PRV/FRO]: ");
            privacy = input.nextLine().trim().toUpperCase();
            if (!privacy.equals("PUB") && !privacy.equals("PRV") && !privacy.equals("FRO"))
                System.out.println("Must be PUB, PRV, or FRO.");
        } while (!privacy.equals("PUB") && !privacy.equals("PRV") && !privacy.equals("FRO"));
        System.out.print("Filename: ");
        String filename = input.nextLine();
        System.out.print("Location: ");
        String location = input.nextLine();
        System.out.print("Tags: ");
        String tags = input.nextLine();
        c.q3(loginId, caption, privacy, filename, location, tags);
    }

    public static void joinGroup(Connector c) {
        c.q4(loginId, input);
    }

    public static void viewMessages(Connector c) {
        c.q5(loginId, input);
    }

    public static void sendMessage(Connector c) {
        System.out.print("Enter recipient username: ");
        String receiver = input.nextLine().trim();
        System.out.print("Enter message: ");
        String content = input.nextLine();
        c.q6(loginId, receiver, content, input);
    }
}
