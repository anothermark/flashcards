package com.cards.flash.services;

import com.cards.flash.entities.QuestionAnswer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private JdbcTemplate jdbcTemplate; // ie. to create jdbc connection and using sql to call methods that return data from db

    private final ObjectMapper objectMapper = new ObjectMapper(); // for Json mapping using Jackson

    
    @Override
    public List<QuestionAnswer> getOrCreateSubject(Long id, String defaultSubjectName) {
        String sql = "SELECT questionsandanswers, studysubject FROM flashcards WHERE id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Database row " + id + " does not exist.");
        }

        Map<String, Object> row = rows.get(0);
        String jsonText = (String) row.get("questionsandanswers");
        String currentSubject = (String) row.get("studysubject");

        List<QuestionAnswer> cardList;

        if (jsonText == null) {
            cardList = new ArrayList<>();
            saveToDatabase(id, defaultSubjectName, cardList);
        } else {
            try {
                cardList = objectMapper.readValue(jsonText, new TypeReference<List<QuestionAnswer>>() {});
                if (currentSubject == null) {
                    jdbcTemplate.update("UPDATE flashcards SET studysubject = ? WHERE id = ?", defaultSubjectName, id);
                }
            } catch (Exception e) {
                cardList = new ArrayList<>();
            }
        }
        return cardList;
    }

    @Override
    public List<QuestionAnswer> addCard(Long subjectId, QuestionAnswer newItem) {
        // Fetch existing subject name from the row so we don't overwrite it with null
        String subjectName = getSubjectNameFromDb(subjectId);
        List<QuestionAnswer> cardList = getOrCreateSubject(subjectId, subjectName);
        
        // Auto-assign internal 1-based list index
        newItem.setId(cardList.size() + 1);
        cardList.add(newItem);

        saveToDatabase(subjectId, subjectName, cardList);
        return cardList;
    }

    @Override
    public List<QuestionAnswer> updateCard(Long subjectId, QuestionAnswer updatedItem) {
        String subjectName = getSubjectNameFromDb(subjectId);
        List<QuestionAnswer> cardList = getOrCreateSubject(subjectId, subjectName);

        for (QuestionAnswer item : cardList) {
            if (item.getId().equals(updatedItem.getId())) {
                item.setQuestion(updatedItem.getQuestion());
                item.setAnswer(updatedItem.getAnswer());
                break;
            }
        }

        saveToDatabase(subjectId, subjectName, cardList);
        return cardList;
    }

    @Override
    public List<QuestionAnswer> deleteCard(Long subjectId, Integer cardId) {
        String subjectName = getSubjectNameFromDb(subjectId);
        List<QuestionAnswer> cardList = getOrCreateSubject(subjectId, subjectName);

        cardList.removeIf(item -> item.getId().equals(cardId));

        int index = 1;
        for (QuestionAnswer item : cardList) {
            item.setId(index++);
        }

        saveToDatabase(subjectId, subjectName, cardList);
        return cardList;
    }

    private String getSubjectNameFromDb(Long id) {
        String sql = "SELECT studysubject FROM flashcards WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, id);
    }

    private void saveToDatabase(Long id, String subjectName, List<QuestionAnswer> list) {
        try {
            String jsonString = objectMapper.writeValueAsString(list);
            String sql = "UPDATE flashcards SET questionsandanswers = ?, studysubject = ? WHERE id = ?";
            jdbcTemplate.update(sql, jsonString, subjectName, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize flashcard arraylist to JSON", e);
        }
    }
    @Override
    public List<QuestionAnswer> getQuestionsByTopic(String topicName) {
        Long subjectId;

        // Map your HTML dropdown string values to your actual MySQL Row IDs (id)
        switch (topicName.toLowerCase()) {
            case "verses[a]":
                subjectId = 1L; // Maps to row ID 1 in your flashcards table
                break;
            case "atheism":
                subjectId = 2L; // Maps to row ID 2 in your flashcards table
                break;
            case "incarnation":
                subjectId = 3L; // Maps to row ID 3 in your flashcards table
                break;
            case "soteriology":
                subjectId = 4L; // Maps to row ID 4 in your flashcards table
                break;
            case "verses[b]":
                subjectId = 5L; // Maps to row ID 5 in your flashcards table
                break;
            case "trinity":
                subjectId = 6L; // Maps to row ID 6 in your flashcards table
                break;
            case "evolution":
                subjectId = 7L; // Maps to row ID 7 in your flashcards table
                break;
            case "bible verasity":
                subjectId = 8L; // Maps to row ID 8 in your flashcards table
                break;
                
                
            default:
                return new ArrayList<>(); // Returns an empty list if a topic doesn't match
        }

        // Reuse your existing logic that deserializes the JSON string into a Java List
        return getOrCreateSubject(subjectId, topicName);
    }
    
    
}