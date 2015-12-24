package no.northcode.jens.intranetsek2tg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Config {

	private String dbhost;
	private String dbuser;
	private String dbpassword;
	private String dbname;
	
	private String botToken;
	
	public String getDbhost() {
		return dbhost;
	}

	public String getDbuser() {
		return dbuser;
	}

	public String getDbpassword() {
		return dbpassword;
	}

	public String getDbname() {
		return dbname;
	}

	public String getBotToken() {
		return botToken;
	}
	
	public String getJdbcUrl() {
		return (new StringBuilder()).append("jdbc:mysql://").append(this.dbhost).append("/").append(this.dbname).toString();
	}
	
	public Config(String filename) throws FileNotFoundException {
		
		Scanner scan = new Scanner(new File(filename));
	    this.dbhost = scan.nextLine();
	    this.dbuser = scan.nextLine();
	    this.dbpassword = scan.nextLine();
	    this.dbname = scan.nextLine();
	    this.botToken = scan.nextLine();
	    scan.close();
		
	}
	
}
