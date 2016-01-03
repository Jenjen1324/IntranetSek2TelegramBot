package no.northcode.jens.intranetsek2tg;

import java.io.IOException;

import pro.zackpollard.telegrambot.api.TelegramBot;

public class Program {

	public static void main(String[] args) throws IOException {
		TelegramBot tgBot = TelegramBot.login("");
		
		if(tgBot == null) System.exit(-1);
		
		tgBot.getEventsManager().register(new TelegramListener(tgBot));
		tgBot.startUpdates(false);
		
	}
	
}
