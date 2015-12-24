package no.northcode.jens.intranetsek2tg;


import no.northcode.jens.intranetsek2tg.commands.CommandHelp;
import no.northcode.jens.intranetsek2tg.commands.CommandLogin;
import no.northcode.jens.intranetsek2tg.commands.CommandSchoolList;
import no.northcode.jens.intranetsek2tg.commands.CommandTimetable;
import no.northcode.jens.plustgbot.PlusBot;

/**
 * The Main class
 *
 * @author Jens V.
 */
public class Main {
	
	/**
	 * The main method.
	 * Entry point
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		Config config = new Config("config.txt");
		MysqlHelper mysql = new MysqlHelper(config);
		
		PlusBot plusBot = new PlusBot(config.getBotToken());
		
		plusBot.registerCommand(new CommandHelp());
		plusBot.registerCommand(new CommandSchoolList());
		plusBot.registerCommand(new CommandLogin(mysql));
		plusBot.registerCommand(new CommandTimetable(mysql));
		plusBot.registerJoinGroupHandler(new CommandHelp());
		plusBot.listen();
		
	}	
	
}
