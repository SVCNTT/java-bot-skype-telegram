package com.snow.wolf.process;

import com.snow.wolf.model.Answer;
import com.snow.wolf.model.Question;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramMessageProcess implements ChatMessageProcess {

    QuestionProcess questionProcess;

    public TelegramMessageProcess() {
        questionProcess = new QuestionProcess();
    }

    @Override
    public Question parseQuestion(Object chatData) {
        Question question = new Question();
        if (chatData instanceof Update) {
            question.username = ((Update) chatData).getChannelPost().getChat().getUserName();
            question.chatId   = String.valueOf(((Update) chatData).getChannelPost().getChatId());
            question.text     = ((Update) chatData).getMessage().getText();
        }
        return question;
    }

    @Override
    public Answer answerQuestion(Question question) {
        Answer answer = new Answer(question);
        return null;
    }


    @Override
    public Answer processMessage(Object chatData) {
        Question question = parseQuestion(chatData);
        return answerQuestion(question);
    }
}
