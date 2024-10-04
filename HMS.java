import java.sql.*;
import java.util.Scanner;

public class HMS {
    private static final String url="jdbc:mysql://localhost:3306/hotel_db";
    private static final String username="root";
    private static final String password="Admin@123";


    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {

        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection=DriverManager.getConnection(url,username,password);
            while (true){
                Scanner sc=new Scanner(System.in);


                System.out.println("=============================================================================================");
                System.out.println("\t\t\t\t\t\t\t\t\tHotel Management System");
                System.out.println("=============================================================================================");
                System.out.println("1. Reserve A Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("=============================================================================================");
                System.out.println("Select an option :");
                int choice=sc.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection,sc);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,sc);
                        break;
                    case 4:
                        updateReservation(connection,sc);
                        break;
                    case 5:
                        deleteReservation(connection,sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid Selection. Try again");

                }

            }

        }
        catch (SQLException e){
            System.out.println(e.getMessage());

        }
    }

    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i=10;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.print("!");
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

    public static void deleteReservation(Connection connection, Scanner sc) {
        try{
            System.out.println("Enter the reservation_id to delete: ");
            int reservationId=sc.nextInt();

            if (!reservationExits(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            else {
                String sql="delete from reservations where reservation_id='"+reservationId+"'";
                try(Statement statement= connection.createStatement();){
                    int affectedRows=statement.executeUpdate(sql);

                    if (affectedRows > 0) {
                        System.out.println("Reservation deleted successfully!");
                    } else {
                        System.out.println("Reservation deletion failed.");
                    }

                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void updateReservation(Connection connection, Scanner sc) {

        try{
            System.out.println("Enter the reservation_id to Update: ");
            int reservationId=sc.nextInt();

            if (!reservationExits(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            else {
                System.out.print("Enter new guest name: ");
                String newGuestName = sc.next();
                sc.nextLine();

                System.out.print("Enter new room number: ");
                int newRoomNumber = sc.nextInt();
                System.out.print("Enter new contact number: ");
                String newContactNumber = sc.next();
                String sql="update reservations set guest_name='"+newGuestName+"',room_no='"+newRoomNumber+"',contact_no='"+newContactNumber+"' where reservation_id='"+reservationId+"'";
                try(Statement statement= connection.createStatement();){
                    int affectedRows=statement.executeUpdate(sql);

                    if (affectedRows > 0) {
                        System.out.println("Reservation updated successfully!");
                    } else {
                        System.out.println("Reservation Update failed.");
                    }

                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean reservationExits(Connection connection, int reservationId) {

        try{
            String sql="select reservation_id from reservations where reservation_id='"+reservationId+"'";
            try(Statement statement=connection.createStatement(); ResultSet resultset= statement.executeQuery(sql)) {

                return resultset.next(); // If there's a result, the reservation exists
            }
        }catch (SQLException e){
                e.printStackTrace();
                return false;
        }
    }

    public static void getRoomNumber(Connection connection, Scanner sc) {

        try{
            System.out.println("Enter the Reservation Id:");
            int reservationId= sc.nextInt();
            System.out.println("Enter the Guest Name:");
            String guestName=sc.next();

            String sql="Select room_no from reservations where reservation_id= '" + reservationId + "' and guest_name='" + guestName + "'";
            try(Statement statement= connection.createStatement(); ResultSet resultSet=statement.executeQuery(sql)){
                if (resultSet.next()){
                    int roomNumber=resultSet.getInt("room_no");
                    System.out.println("The Room_no for the Reservation_Id "+reservationId+" and the Guest_Name "+guestName+" is :"+roomNumber);
                }
                else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewReservations(Connection connection) throws SQLException{
        String sql="select reservation_id,guest_name,room_no,contact_no,reservation_date  from reservations";
        try{
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(sql);

            System.out.println("Current Reservations :");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date         |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while (resultSet.next()){
                int reservationId=resultSet.getInt("reservation_id");
                String  guest_name=resultSet.getString("guest_name");
                int room_no=resultSet.getInt("room_no");
                String contact_no=resultSet.getString("contact_no");
                String reservation_date=resultSet.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                /*
                The format string: "| %-14d | %-15s | %-13d | %-20s | %-19s   |":
                The % symbols indicate placeholders for values.
                %d is a placeholder for an integer (reservationId, roomNumber).
                %s is a placeholder for a string (guestName, contactNumber, reservationDate).
                The numbers (14, 15, 13, 20, 19) represent the minimum width for each field. The - sign means left-justified.
                 */
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",reservationId,guest_name,room_no,contact_no,reservation_date);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void reserveRoom(Connection connection, Scanner sc) {

        try{
            System.out.println("Enter the guest name :");
            String guestName=sc.next();
            sc.nextLine();
            System.out.println("Enter room number:");
            int roomNumber=sc.nextInt();
            System.out.println("Enter contact number:");
            String contact=sc.next();

            String sql="INSERT INTO reservations(guest_name,room_no,contact_no) Values('" + guestName + "','" + roomNumber + "','" + contact + "')";

            try (Statement statement=connection.createStatement()){
                int affectedRows=statement.executeUpdate(sql);

                if (affectedRows>0){
                    System.out.println("Reservation Successful.");
                }
                else {
                    System.out.println("Reservation Failed.");
                }
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }


}
