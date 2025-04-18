package WorldCultura;

import WorldCultura.models.user;
import WorldCultura.services.userService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        userService userService = new userService();
        int choice;

        do {
            System.out.println("\n===== user MANAGEMENT MENU =====");
            System.out.println("1. Add user");
            System.out.println("2. Display All users");
            System.out.println("3. Update user");
            System.out.println("4. Delete user");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    user newuser = new user();
                    System.out.print("Enter Name: ");
                    newuser.setNom(scanner.nextLine());
                    System.out.print("Enter Surname: ");
                    newuser.setPrenom(scanner.nextLine());
                    System.out.print("Enter Email: ");
                    newuser.setEmail(scanner.nextLine());
                    System.out.print("Enter Password: ");
                    newuser.setPassword(scanner.nextLine());
                    System.out.print("Enter Role: ");
                    newuser.setRole(scanner.nextLine());

                    userService.add(newuser);
                    System.out.println("✅ user added successfully.");
                    break;

                case 2:
                    List<user> users = userService.getAll();
                    System.out.println("\n--- All users ---");
                    for (user user : users) {
                        System.out.println("ID: " + user.getId()
                                + " | Name: " + user.getNom()
                                + " | Surname: " + user.getPrenom()
                                + " | Email: " + user.getEmail()
                                + " | Role: " + user.getRole());
                    }
                    break;

                case 3:
                    System.out.print("Enter ID of the user to update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    user updateuser = new user();
                    updateuser.setId(updateId);
                    System.out.print("Enter New Name: ");
                    updateuser.setNom(scanner.nextLine());
                    System.out.print("Enter New Surname: ");
                    updateuser.setPrenom(scanner.nextLine());
                    System.out.print("Enter New Email: ");
                    updateuser.setEmail(scanner.nextLine());
                    System.out.print("Enter New Password: ");
                    updateuser.setPassword(scanner.nextLine());
                    /*System.out.print("Enter New Role: ");
                    updateuser.setRole(scanner.nextLine());*/

                    userService.update(updateuser);
                    System.out.println("✅ user updated successfully.");
                    break;

                case 4:
                    System.out.print("Enter ID of the user to delete: ");
                    int deleteId = scanner.nextInt();
                    scanner.nextLine(); // consume newline

                    user deleteuser = new user();
                    deleteuser.setId(deleteId);
                    userService.delete(deleteuser);
                    System.out.println("✅ user deleted successfully.");
                    break;

                case 0:
                    System.out.println("👋 Exiting program.");
                    break;

                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }

        } while (choice != 0);
    }
}
