package DataManipulation;

import java.sql.*;

public class Library {

    final private String CONNECTION_DATABASE = "jdbc:mysql:library";
    final private String CONNECTION_AUTH_USER = "librarian";
    final private String CONNECTION_AUTH_PASSWORD = "booksrfun451";

    //should be in the specs, but isn't!
    private Connection connection;

    //also should be in the specs!
    Library(){
        try{
            connection = DriverManager.getConnection(CONNECTION_DATABASE, CONNECTION_AUTH_USER, CONNECTION_AUTH_PASSWORD);
        }
        catch(SQLException e) {
            System.out.println("SQL error encountered.");
            System.out.println(e.toString());
            System.exit(1);
        }
    }

    public void newUser(User user, String password, String answer) throws SQLException {
        createUser(user, password, answer);
    }

    public void newStudent(Student student) throws SQLException {
        createStudent(Student);
    }

    public void newBook(Book book) throws SQLException {
        createBook(book);
    }

    public void newRecord(Record record) throws SQLException {
        createRecord(record);
    }

    private void createUser(User user, String password, String answer) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (DEFAULT, ?, ?, ?, ?)");
        statement.setString(1, user.getName());
        statement.setString(2, password);
        statement.setInt(3, user.getSecurityQuestion());
        statement.setString(4, answer);
        statement.execute();
    }

    private void createStudent(Student student) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO students VALUES (DEFAULT, ?, ?, ?, ?)");
        statement.setString(1, student.getFirstName());
        statement.setString(2, student.getLastName());
        statement.setString(3, student.getEmail());
        statement.setString(4, student.getPhone());
        statement.execute();
    }

    private void createBook(Book book) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO books VALUES (DEFAULT, ?, ?, ?, ?, ?)");
        statement.setString(1, book.getTitle());
        statement.setInt(2, book.getEdition());
        statement.setString(3, book.getPublisher());
        statement.setInt(4, book.getPrice());
        statement.setInt(5, book.getPages());
        statement.execute();
    }

    private void createRecord(Record record) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO records VALUES (DEFAULT, ?, ?, ?, ?, ?)");
        statement.setInt(1, record.getBook().getBookID());
        statement.setInt(2, record.getStudent().getStudentID());
        statement.setInt(3, record.getUser().getUserID());
        statement.setDate(4, record.getCheckoutDate());
    }

    public User getUserByID(int userID) throws SQLException {
        fetchUser(userID);
    }

    public Student getStudentByID(int studentID) throws SQLException {
        fetchStudent(studentID);
    }

    public Book getBookByID(int bookID) throws SQLException {
        fetchBook(bookID, "bookID");
    }

    public Book getBookByTitle(String title) throws SQLException {
        fetchBook(title, "title");
    }

    public Record getRecordByID(int recordID) throws SQLException {
        fetchRecord(recordID, "recordID");
    }

    public User getUserbyName(String name) throws SQLException {
        fetchUser(name, "name");
    }



    private User fetchUser(String compare, String field) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users where ? = ?");
        statement.setString(1, field);
        statement.setString(2, compare);
        ResultSet result = statement.executeQuery();
        return new User(
                result.getInt("userID"),
                result.getString("name"),
                result.getString("password"),
                result.getInt("security_question"),
                result.getString("security_answer")
        );
    }

    private Student fetchStudent(int studentID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM  students WHERE studentID = ?");
        statement.setInt(1, studentID);
        ResultSet result = statement.executeQuery();
        return new Student(
                result.getInt("studentID"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("email"),
                result.getString("phone")
        );
    }

    private Book fetchBook(String compare, String field) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * from books WHERE ? = ?");
        statement.setString(1, field);
        statement.setString(2, compare);
        ResultSet result = statement.executeQuery();
        return new Book(
                result.getInt("bookID"),
                result.getString("title"),
                result.getInt("edition"),
                result.getString("publisher"),
                result.getDouble("price"),
                result.getInt("pages")
        );
    }

    private Record fetchRecord(String compare, String field) throws SQLException {
        //we are getting everything in one go for the sake of efficiency.
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM records\n" +
                        "JOIN books ON books.bookID = records.bookID\n" +
                        "JOIN students ON students.studentID = records.studentID\n" +
                        "JOIN users ON users.userID = records.userID\n" +
                        "WHERE ? = ?"
        );
        statement.setString(1, field);
        statement.setString(2, compare);
        ResultSet result = statement.executeQuery();
        return new Record(
                result.getInt("recordID"),
                new Book(
                        result.getInt("bookID"),
                        result.getString("title"),
                        result.getInt("edition"),
                        result.getString("publisher"),
                        result.getInt("price"),
                        result.getInt("pages")
                ),
                new Student(
                        result.getInt("studentID"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getString("email"),
                        result.getString("phone")
                ),
                new User(
                        result.getInt("userID"),
                        result.getString("name"),
                        result.getString("password"),
                        result.getInt("security_question"),
                        result.getString("security_answer")
                ),
                result.getDate("checkout_date"),
                result.getDate("return_date")
        );
    }




}
