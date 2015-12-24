package no.northcode.jens.intranetsek2tg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

// TODO: Auto-generated Javadoc
/**
 * The Class Config.
 *
 * @author Jens V.
 */
public class Config {

	/** The dbhost. */
	private String dbhost;
	
	/** The dbuser. */
	private String dbuser;
	
	/** The dbpassword. */
	private String dbpassword;
	
	/** The dbname. */
	private String dbname;
	
	/** The bot token. */
	private String botToken;
	
	/**
	 * Instantiates a new config.
	 *
	 * @param filename the filename
	 * @throws FileNotFoundException the file not found exception
	 */
	public Config(String filename) throws FileNotFoundException {
		
		Scanner scan = new Scanner(new File(filename));
	    this.dbhost = scan.nextLine();
	    this.dbuser = scan.nextLine();
	    this.dbpassword = scan.nextLine();
	    this.dbname = scan.nextLine();
	    this.botToken = scan.nextLine();
	    scan.close();
		
	}

	/**
	 * Gets the bot token.
	 *
	 * @return the bot token
	 */
	public String getBotToken() {
		return botToken;
	}

	/**
	 * Gets the dbhost.
	 *
	 * @return the dbhost
	 */
	public String getDbhost() {
		return dbhost;
	}

	/**
	 * Gets the dbname.
	 *
	 * @return the dbname
	 */
	public String getDbname() {
		return dbname;
	}

	/**
	 * Gets the dbpassword.
	 *
	 * @return the dbpassword
	 */
	public String getDbpassword() {
		return dbpassword;
	}
	
	/**
	 * Gets the dbuser.
	 *
	 * @return the dbuser
	 */
	public String getDbuser() {
		return dbuser;
	}
	
	/**
	 * Gets the jdbc url.
	 *
	 * @return the jdbc url
	 */
	public String getJdbcUrl() {
		return (new StringBuilder()).append("jdbc:mysql://").append(this.dbhost).append("/").append(this.dbname).toString();
	}
	
}
