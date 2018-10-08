package com.snow.wolf.process;

import com.snow.wolf.model.Answer;
import com.snow.wolf.model.Question;

public interface ChatMessageProcess {

    Question parseQuestion(Object chatData);
    Answer answerQuestion(Question question);
    Answer processMessage(Object chatData);
}
