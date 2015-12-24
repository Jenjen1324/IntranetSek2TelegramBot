package no.northcode.jens.intranetsek2tg.commands;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import de.raysha.lib.telegram.bot.api.exception.BotException;
import de.raysha.lib.telegram.bot.api.model.Message;
import no.northcode.jens.intranetsek2.LoginException;
import no.northcode.jens.intranetsek2tg.MysqlHelper;
import no.northcode.jens.plustgbot.ICommandHandler;
import no.northcode.jens.plustgbot.PlusBot;

public class CommandLogin implements ICommandHandler {

	private static final String invalidMessage = "Eingabe war nicht korrekt. Bitte wie folgt eingeben:\n/login `benutzername` `passwort` `schulcode`";
	private static final String invalidCredMessage = "Deine Benutzerdaten waren nicht korrekt.  Bitte wie folgt eingeben:\n/login `benutzername` `passwort` `schulcode`";
	
	private MysqlHelper mysql;
	
	
	public CommandLogin(MysqlHelper mysql) {
		this.mysql = mysql;
	}
	
	@Override
	public ArrayList<String> getCommands() {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("login");
		return cmds;
	}

	@Override
	public void handleMessage(PlusBot bot, Message message) throws BotException, IOException, SQLException {
		String[] split = message.getText().split(" ");
		if(split.length < 4) {
			bot.normalReply(message, invalidMessage);
			return;
		}
		
		try {
			mysql.addLogin(message.getFrom().getId(), split[3], split[1], split[2]);
		} catch (LoginException e) {
			bot.normalReply(message, invalidCredMessage);
			return;
		}
		
	}

	@Override
	public void handleReply(PlusBot bot, Message message) throws BotException, Exception {
		// TODO Auto-generated method stub
		
	}

}
