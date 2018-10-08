package com.snow.wolf.bot;

import com.snow.wolf.bot.config.MainConfig;
import com.snow.wolf.bot.telegram.TelegramBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class StartBot {
    public static void main(String[] args) {
        startTelegramBot();
    }

    private static void startTelegramBot() {
        try {
            new MainConfig("/app/mservice/sdk/backend/conf/momo.json");
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            botsApi.registerBot(new TelegramBot());
        } catch (Exception ex) {
        }

    }
}
