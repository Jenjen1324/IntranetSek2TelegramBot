package no.northcode.jens.intranetsek2tg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import pro.zackpollard.telegrambot.api.TelegramBot;

public class Program {

	public static void main(String[] args) throws IOException {
		String botKey = null;
		try {
			BufferedReader freader = new BufferedReader(new FileReader("botkey.txt"));
			botKey = freader.readLine();
			freader.close();
		} catch (IOException ex) {
			System.err.println("Couldn't read botkey.txt!");
			System.exit(-1);
		}
		TelegramBot tgBot = TelegramBot.login(botKey);
		
		if(tgBot == null) System.exit(-1);
		
		tgBot.getEventsManager().register(new TelegramListener(tgBot));
		tgBot.startUpdates(false);
		
	}
	
}
