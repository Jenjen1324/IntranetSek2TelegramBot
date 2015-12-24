package no.northcode.jens.intranetsek2tg;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import no.northcode.jens.intranetsek2.Login;
import no.northcode.jens.intranetsek2.exception.IntranetException;
import no.northcode.jens.intranetsek2.exception.InvalidCredentialsException;
import no.northcode.jens.intranetsek2.exception.LoginException;
import no.northcode.jens.intranetsek2tg.model.SekUser;


/**
 * The Class MysqlHelper.
 *
 * @author Jens V.
 */
public class MysqlHelper {

	/** The conn. */
	private Connection conn;
	
	/** The logins. */
	private ArrayList<SekUser> logins;
	
	/**
	 * Instantiates a new mysql helper.
	 *
	 * @param c the Config
	 * @throws SQLException when connecting to the database fails
	 */
	public MysqlHelper(Config c) throws SQLException  {
		
		//Driver driver = new com.mysql.jdbc.Driver(); 
		System.out.println("Connecting to database...");
		this.conn = DriverManager.getConnection(c.getJdbcUrl(), c.getDbuser(), c.getDbpassword());
		System.out.println("Success!");
		
		this.loadLogins();
	}
	
	
	/**
	 * Adds the login.
	 *
	 * @param tg_userid the Telegram User ID
	 * @param school the school
	 * @param username the username
	 * @param password the password
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SQLException When inserting to the database fails
	 * @throws InvalidCredentialsException when the credentials are invalid
	 * @throws IntranetException when interaction with the intranet fails
	 * @throws LoginException when something goes wrong while logging in
	 */
	public void addLogin(int tg_userid, String school, String username, String password) throws IOException, SQLException, InvalidCredentialsException, IntranetException, LoginException {
		// Check if login is valid
		new Login(username, password, school);
		String sql = 
				(new StringBuilder()).append("INSERT INTO users (tg_userid,username,password,school) VALUES (")
				.append(tg_userid).append(",'")
				.append(username).append("','")
				.append(password).append("','")
				.append(school)
				.append("')").toString();
		System.out.println(sql);
		
		Statement stmt = this.conn.createStatement();
		stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		ResultSet keys = stmt.getGeneratedKeys();
		keys.next();
		int id = (int) keys.getLong(1);
		stmt.close();
		
		this.logins.add(new SekUser(id, tg_userid, username, password, school));
	}
	
	/**
	 * Gets the logins.
	 *
	 * @return the logins
	 */
	public ArrayList<SekUser> getLogins() {
		return logins;
	}
	
	/**
	 * Load logins.
	 *
	 * @throws SQLException the SQL exception
	 */
	private void loadLogins() throws SQLException {
		String sql = "SELECT * FROM users";
		
		Statement stmt = this.conn.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);
		
		logins = new ArrayList<SekUser>();
		
		while(resultSet.next()) {
			int id_users = resultSet.getInt("id_users");
			int tg_userid = resultSet.getInt("tg_userid");
			String username = resultSet.getString("username");
			String password = resultSet.getString("password");
			String school = resultSet.getString("school");
			SekUser su = new SekUser(id_users, tg_userid, username, password, school);
			logins.add(su);
		}
		
		stmt.close();
	}
}
