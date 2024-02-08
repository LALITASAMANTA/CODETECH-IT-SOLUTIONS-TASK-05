import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

class Student {
    private String name;
    private int rollNumber;
    private String grade;

    public Student(String name, int rollNumber, String grade) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
    }

    // Getters and setters for attributes

    @Override
    public String toString() {
        return "Name: " + name + ", Roll Number: " + rollNumber + ", Grade: " + grade;
    }
}

public class StudentManagementSystem {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/My_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Lali@1234";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            createTableIfNotExists(connection);

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\nStudent Management System");
                System.out.println("1. Add Student");
                System.out.println("2. Remove Student");
                System.out.println("3. Search for Student");
                System.out.println("4. Display All Students");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        addStudent(connection, scanner);
                        break;
                    case 2:
                        removeStudent(connection, scanner);
                        break;
                    case 3:
                        searchStudent(connection, scanner);
                        break;
                    case 4:
                        displayAllStudents(connection);
                        break;
                    case 5:
                        System.out.println("Exiting Student Management System");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "rollNumber INT NOT NULL," +
                "grade VARCHAR(10) NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        }
    }

    private static void addStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter student name: ");
        String name = scanner.next();
        System.out.print("Enter student roll number: ");
        int rollNumber = scanner.nextInt();
        System.out.print("Enter student grade: ");
        String grade = scanner.next();

        String insertQuery = "INSERT INTO students (name, rollNumber, grade) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, rollNumber);
            preparedStatement.setString(3, grade);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student added successfully.");
            } else {
                System.out.println("Failed to add student.");
            }
        }
    }

    private static void removeStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter student roll number to remove: ");
        int rollNumber = scanner.nextInt();

        String deleteQuery = "DELETE FROM students WHERE rollNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, rollNumber);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student removed successfully.");
            } else {
                System.out.println("Student not found or failed to remove.");
            }
        }
    }

    private static void searchStudent(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter student roll number to search: ");
        int rollNumber = scanner.nextInt();

        String selectQuery = "SELECT * FROM students WHERE rollNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, rollNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Student student = new Student(
                            resultSet.getString("name"),
                            resultSet.getInt("rollNumber"),
                            resultSet.getString("grade"));
                    System.out.println("Student found: " + student);
                } else {
                    System.out.println("Student not found.");
                }
            }
        }
    }

    private static void displayAllStudents(Connection connection) throws SQLException {
        String selectAllQuery = "SELECT * FROM students";
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectAllQuery)) {

            while (resultSet.next()) {
                Student student = new Student(
                        resultSet.getString("name"),
                        resultSet.getInt("rollNumber"),
                        resultSet.getString("grade"));
                System.out.println(student);
            }
        }
    }
}
