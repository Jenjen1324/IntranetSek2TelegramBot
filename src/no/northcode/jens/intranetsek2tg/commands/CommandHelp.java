package no.northcode.jens.intranetsek2tg.commands;

import java.util.ArrayList;

import de.raysha.lib.telegram.bot.api.exception.BotException;
import de.raysha.lib.telegram.bot.api.model.Message;
import no.northcode.jens.plustgbot.ICommandHandler;
import no.northcode.jens.plustgbot.PlusBot;

public class CommandHelp implements ICommandHandler {
	
	private String getHelpText() {
		return (new StringBuilder())
				.append("*Willkommen zum Intranet Sek 2 bot*\n")
				.append("Dieser Bot lädt dir den neusten Stundenplan aus intranet.tam.ch automatisch runter, damit du nicht mehr durch dieses Weblogin gehen musst\n")
				.append("\n\n")
				.append("*Befehle*\n")
				.append("/hilfe - Zeigt dir diesen Text an\n")
				.append("/schulen - Listet dir die Schulcodes auf\n")
				.append("/login [username] [password [schulcode]\n")
				.append("\t--> Logg dich mit deinen Benutzerdaten ein\n")
				.append("/logout - Lösche alle deine Daten vom Bot\n")
				.append("/stundenplan (heute|morgen|YYYY-MM-DD)\n")
				.append("\t--> Zeige den stundenplan an von heute, morgen oder einem bestimmten Datum!")
				.toString();
	}
	
	@Override
	public ArrayList<String> getCommands() {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("help");
		cmds.add("start");
		return cmds;
	}

	@Override
	public void handleMessage(PlusBot bot, Message message) throws BotException {
		bot.normalReply(message, getHelpText());
	}

	@Override
	public void handleReply(PlusBot bot, Message message) {
		// TODO Auto-generated method stub
		
	}

}
