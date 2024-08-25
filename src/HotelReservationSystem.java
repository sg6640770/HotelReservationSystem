import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.sql.SQLException;


public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "12#@12Ab";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an Option: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, sc);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");

                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room Number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number)"
                    + "VALUES ('" + guestName + "'," + roomNumber + ", '" + contactNumber + "')";
            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation successfully!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection) throws SQLException {
        String sql = "SELECT reservations_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+----------------+------------------+----------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number    | Contact Number   | Reservation Date           |");
            System.out.println("+----------------+-----------------+----------------+------------------+----------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservations_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                //Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+-------------------+----------------------------+");

        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservations " + "WHERE reservations_id = " + reservationId + " AND guest_name = '" + guestName + "'";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is : " + roomNumber);
                } else {
                    System.out.println("Reservation is not found for the given Id and Guest Name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void updateReservation(Connection connection, Scanner sc) {
        try {
            System.out.println("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = sc.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservations_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated Successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void deleteReservation(Connection connection, Scanner sc){
        try{
            System.out.println("Enter Reservation ID to delete: ");
            int reservationId=sc.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;

            }

            String sql="DELETE FROM reservations WHERE reservations_id = " + reservationId;

            try(Statement statement=connection.createStatement()){
                int affectedRows=statement.executeUpdate(sql);

                if(affectedRows>0){
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservations_id FROM reservations WHERE reservations_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next();// if there is a result then reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void exit() throws InterruptedException{
        System.out.print("Existing System");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You for using Hotel Reservation System!!!");
    }
}