import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "CodexAnkit76@1";

    public static void main(String args[]){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        Scanner sc = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patients patient = new Patients(connection, sc);
            Doctors doctor = new Doctors(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                switch(choice){
                    case 1:
                       patient.addPatient();
                       System.out.println();
                       break;
                    case 2:
                       patient.viewPatient();
                       System.out.println();
                       break;
                    case 3:
                       doctor.viewDoctors();
                       System.out.println();
                       break;
                    case 4:
                        bookAppointment(patient, doctor, connection, sc);
                        System.out.println();
                        break;
                    case 5:
                       break;
                    default:
                       System.out.println("Enter valid choice!!");
                       System.out.println();
                }
            } 
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patients patients, Doctors doctors, Connection connection, Scanner sc){
        System.out.print("Enter Patient Id: ");
        int patientId = sc.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = sc.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = sc.next();

        if(patients.getPatientById(patientId) && doctors.getDoctorById(doctorId)){
            if(checkAvailability(doctorId, appointmentDate, connection)){
                String query = "INSERT INTO appointments(patient_id,doctor_id,appointment_date) VALUES(?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 1){
                        System.out.println("Appointment Booked Successfully!!");
                    }
                    else{
                        System.out.println("Failed to Book Appointment!!");
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static boolean checkAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }
                return false;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
