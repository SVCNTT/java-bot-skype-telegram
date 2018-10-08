package com.snow.wolf.bot.telegram;

import com.snow.wolf.model.Answer;
import com.snow.wolf.process.ChatMessageProcess;
import com.snow.wolf.util.Util;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    private String apiToken = "662961249:AAFjcafQjZnPgiMT_RVrAH4aIVlP8r50uMk";
    private String botUsername = "momosdkbe_bot";
    private String chatId = "@momosdksyswarning";

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        System.out.println("hanlde chat");
        if (update.getChannelPost().hasText()) {
//            String chatText = update.getChannelPost().getText();
            String replyText = "";

//            Answer answer = new ChatMessageProcess(update.getChannelPost().getChat()).parseQuestion();
           /* if (chatText.contains("TID:")) {
                replyText = "The Transaction Id " + Util.getTransIdFromText(chatText).get(0) + " has been successed on my system.";
            } else {
                replyText = "Hi @" + update.getChannelPost().getChat().getUserName() + " ! \nMay I help you ?";
            }*/
            // Set variables
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chatId)
                    .setText(replyText);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return apiToken;
    }
}