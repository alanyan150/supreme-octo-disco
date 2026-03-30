import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean done = false;
        boolean isvalid = true;
        do {
            if (isvalid) {
                System.out.print("""
                    Main Menu
                        1. Lookup user
                        2. bla
                        3. blabla
                        4. bye
                        5. boo
                        6. Quit
                    """);
            }
            int choice;
            try {
                System.out.print("Please enter your option: ");
                choice = input.nextInt();
                isvalid = true;
                switch (choice) {
                    case 1:
                        choice1();
                        break;
                    case 2:
                        System.out.println(2);
                        break;
                    case 3:
                        System.out.println(3);
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
                        break;
                }
            } catch (InputMismatchException e) {
                isvalid = false;
                input.next();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                break;
            }
        } while (!done);
    }

    public static void choice1() {
        System.out.println(1);
    }
}
