package no.northcode.jens.intranetsek2tg.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.raysha.lib.telegram.bot.api.exception.BotException;
import de.raysha.lib.telegram.bot.api.model.Message;
import no.northcode.jens.intranetsek2.School;
import no.northcode.jens.plustgbot.ICommandHandler;
import no.northcode.jens.plustgbot.PlusBot;

public class CommandSchoolList implements ICommandHandler {

	private List<School> schools;
	
	public CommandSchoolList() throws IOException {
		schools = School.getSchoolList();
	}
	
	@Override
	public ArrayList<String> getCommands() {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("school");
		cmds.add("list");
		cmds.add("schule");
		cmds.add("schulen");
		cmds.add("liste");
		return cmds;
	}

	@Override
	public void handleMessage(PlusBot bot, Message message) throws BotException {
		StringBuilder msg = new StringBuilder();
		msg.append("*Liste Aller Schulen*\nBenutze den `code` beim login\n\n");
		for(School s : schools) {
			msg.append('`').append(s.getId()).append("` - ").append(s.getName()).append("\n");
		}
		bot.normalReply(message, msg.toString());
	}

	@Override
	public void handleReply(PlusBot bot, Message message) {
		// TODO Auto-generated method stub
		
	}

}
