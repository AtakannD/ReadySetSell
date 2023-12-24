import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class User {
	private String name;
	private String surname;
	private String email;

	private String password;

	private String url = "jdbc:mysql://localhost:3306/auction_system";
	private String username = "root";
	private Scanner scanner;

	public User(String name, String surname, String email, String password) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
	}

	public String getName(){
		return name;
	}
	public String getSurname(){
		return surname;
	}
	public String getEmail(){
		return email;
	}
	public String getPassword() {return password; }

}
