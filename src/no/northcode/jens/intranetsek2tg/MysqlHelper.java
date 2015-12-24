package no.northcode.jens.intranetsek2tg;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import no.northcode.jens.intranetsek2.Login;
import no.northcode.jens.intranetsek2.LoginException;
import no.northcode.jens.intranetsek2tg.model.SekUser;


public class MysqlHelper {

	private Connection conn;
	
	private ArrayList<SekUser> logins;
	
	public ArrayList<SekUser> getLogins() {
		return logins;
	}
	
	
	public MysqlHelper(Config c) throws SQLException  {
		
		//Driver driver = new com.mysql.jdbc.Driver(); 
		System.out.println("Connecting to database...");
		this.conn = DriverManager.getConnection(c.getJdbcUrl(), c.getDbuser(), c.getDbpassword());
		System.out.println("Success!");
		
		this.loadLogins();
	}
	
	public void addLogin(int tg_userid, String school, String username, String password) throws IOException, SQLException, LoginException {
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
