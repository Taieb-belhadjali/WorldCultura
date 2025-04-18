package project;

import project.models.event;
import project.models.participation;
import project.service.eventservice;
import project.service.serviceparticipation;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        eventservice eventService = new eventservice();
        serviceparticipation participationService = new serviceparticipation();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n📅 Event Management Menu:");
            System.out.println("1️⃣ Add Event");
            System.out.println("2️⃣ Update Event");
            System.out.println("3️⃣ Delete Event");
            System.out.println("4️⃣ View Event by ID");
            System.out.println("5️⃣ List All Events");
            System.out.println("6️⃣ Add Participation");
            System.out.println("7️⃣ Update Participation");
            System.out.println("8️⃣ Delete Participation");
            System.out.println("9️⃣ View Participation by ID");
            System.out.println("🔟 List All Participations");
            System.out.println("0️⃣ Exit");
            System.out.print("👉 Your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                // Event Operations
                case 1 -> {
                    System.out.print("Event Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Start Date (YYYY-MM-DD): ");
                    String start = scanner.nextLine();
                    System.out.print("End Date (YYYY-MM-DD): ");
                    String end = scanner.nextLine();
                    System.out.print("Description: ");
                    String desc = scanner.nextLine();
                    System.out.print("Image URL: ");
                    String img = scanner.nextLine();
                    event e = new event(name, start, end, desc, img);
                    eventService.add(e);
                }
                case 2 -> {
                    System.out.print("Event ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("New Name: ");
                    String name = scanner.nextLine();
                    System.out.print("New Start Date: ");
                    String start = scanner.nextLine();
                    System.out.print("New End Date: ");
                    String end = scanner.nextLine();
                    System.out.print("New Description: ");
                    String desc = scanner.nextLine();
                    System.out.print("New Image URL: ");
                    String img = scanner.nextLine();
                    event e = new event(id, name, start, end, desc, img);
                    eventService.update(e);
                }
                case 3 -> {
                    System.out.print("Event ID to delete: ");
                    int id = scanner.nextInt();
                    eventService.delete(id);
                }
                case 4 -> {
                    System.out.print("Event ID to view: ");
                    int id = scanner.nextInt();
                    event e = eventService.getById(id);
                    System.out.println(e != null ? e : "❌ Event not found.");
                }
                case 5 -> {
                    List<event> events = eventService.getAll();
                    if (events.isEmpty()) {
                        System.out.println("⚠️ No events found.");
                    } else {
                        events.forEach(System.out::println);
                    }
                }

                // Participation Operations
                case 6 -> {
                    System.out.print("Event ID for Participation: ");
                    int eventId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.print("Participant Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Participant Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Participant Telephone: ");
                    int telephone = scanner.nextInt();
                    participation p = new participation(telephone, email, name, eventId);
                    participationService.add(p);
                }
                case 7 -> {
                    System.out.print("Participation ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.print("New Event ID: ");
                    int eventId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.print("New Participant Name: ");
                    String name = scanner.nextLine();
                    System.out.print("New Participant Email: ");
                    String email = scanner.nextLine();
                    System.out.print("New Participant Telephone: ");
                    int telephone = scanner.nextInt();
                    participation p = new participation(id, eventId, name, email, telephone);
                    participationService.update(p);
                }
                case 8 -> {
                    System.out.print("Participation ID to delete: ");
                    int id = scanner.nextInt();
                    participationService.delete(id);
                }
                case 9 -> {
                    System.out.print("Participation ID to view: ");
                    int id = scanner.nextInt();
                    participation p = participationService.getById(id);
                    System.out.println(p != null ? p : "❌ Participation not found.");
                }
                case 10 -> {
                    List<participation> participations = participationService.getAll();
                    if (participations.isEmpty()) {
                        System.out.println("⚠️ No participations found.");
                    } else {
                        participations.forEach(System.out::println);
                    }
                }

                case 0 -> System.out.println("👋 Exiting...");
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        } while (choice != 0);

        scanner.close();
    }
}
