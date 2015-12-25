package no.northcode.jens.intranetsek2tg.commands;

import de.raysha.lib.telegram.bot.api.exception.BotException;
import de.raysha.lib.telegram.bot.api.model.Update;
import no.northcode.jens.plustgbot.IGroupHandler;
import no.northcode.jens.plustgbot.PlusBot;

public class GroupHandler  implements IGroupHandler {

	private static final String noLogin = "*Der bot ist noch nicht eingeloggt*\n\nLoggen sie sich im Privatchat in den Bot ein und schreiben sie in der Gruppe /registrieren";
	
	@Override
	public void handleJoinGroup(PlusBot bot, Update update) throws BotException, Exception {
		
		
	}

	@Override
	public void handleLeaveGroup(PlusBot bot, Update update) throws BotException, Exception {
		// TODO Auto-generated method stub
		
	}

}
