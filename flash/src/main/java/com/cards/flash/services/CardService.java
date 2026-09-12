package com.cards.flash.services;

import com.cards.flash.entities.QuestionAnswer;
import java.util.List;


public interface CardService {
    List<QuestionAnswer> getOrCreateSubject(Long id, String defaultSubjectName);
    List<QuestionAnswer> addCard(Long subjectId, QuestionAnswer newItem);
    List<QuestionAnswer> updateCard(Long subjectId, QuestionAnswer updatedItem);
    List<QuestionAnswer> deleteCard(Long subjectId, Integer cardId);
    List<QuestionAnswer> getQuestionsByTopic(String topicName);
}