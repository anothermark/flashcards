package com.cards.flash.controller;

import com.cards.flash.entities.QuestionAnswer;
import com.cards.flash.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class FlipController {

    @Autowired
    private CardService cardService; 

    @GetMapping("/")
    public String showPage(Model model, HttpSession session) {
        // Clear out stale session parameters upon landing on the root page
        session.removeAttribute("currentQuestionsList");
        session.removeAttribute("currentIndex");
        session.removeAttribute("selectedTopic");
        session.removeAttribute("selectedTopicB");

        model.addAttribute("myTextValue", "Please select a topic to begin!");
        model.addAttribute("myTextAnswer", "");
        model.addAttribute("cardCounter", "0 / 0");
        model.addAttribute("selectedTopic", null);
        model.addAttribute("selectedTopicB", null);
        model.addAttribute("isFlipped", false);
        return "index";
    }

    // 1. Fires when user selects a topic from either dropdown menu
    @PostMapping("/select-topic")
    public String selectTopic(@RequestParam("topic") String topic, HttpSession session, Model model) {
        List<QuestionAnswer> questions = cardService.getQuestionsByTopic(topic); 
        
        session.setAttribute("currentQuestionsList", questions);
        session.setAttribute("currentIndex", 0);
        
        // --- MULTI-DROPDOWN SYNCHRONIZER (FIXED CASE MATCHING) ---
        // Storing normalized lowercase strings ensures strict match equality with index.html
        String normalizedTopic = topic.toLowerCase();
        if (normalizedTopic.equals("math") || normalizedTopic.equals("history") || 
            normalizedTopic.equals("science") || normalizedTopic.equals("geography")) {
            
            session.setAttribute("selectedTopic", normalizedTopic);
            session.setAttribute("selectedTopicB", null);
        } else {
            session.setAttribute("selectedTopic", null);
            session.setAttribute("selectedTopicB", normalizedTopic);
        }

        if (questions != null && !questions.isEmpty()) {
            model.addAttribute("myTextValue", questions.get(0).getQuestion());
            model.addAttribute("cardCounter", "1 / " + questions.size());
        } else {
            model.addAttribute("myTextValue", "No questions found for this topic.");
            model.addAttribute("cardCounter", "0 / 0");
        }
        
        model.addAttribute("myTextAnswer", ""); 
        model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
        model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
        model.addAttribute("isFlipped", false);
        return "index";
    }

    // 2. Fires when clicking "Next Question"
    @SuppressWarnings("unchecked")
    @PostMapping("/fetch-value")
    public String getNextQuestion(HttpSession session, Model model) {
        List<QuestionAnswer> questions = (List<QuestionAnswer>) session.getAttribute("currentQuestionsList");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        if (questions != null && !questions.isEmpty() && currentIndex != null) {
            currentIndex = (currentIndex + 1) % questions.size();
            session.setAttribute("currentIndex", currentIndex);

            model.addAttribute("myTextValue", questions.get(currentIndex).getQuestion());
            model.addAttribute("cardCounter", (currentIndex + 1) + " / " + questions.size());
        } else {
            model.addAttribute("myTextValue", "Please select a topic first!");
            model.addAttribute("cardCounter", "0 / 0");
        }

        model.addAttribute("myTextAnswer", ""); 
        model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
        model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
        model.addAttribute("isFlipped", false);
        return "index";
    }

    // 3. Fires when clicking "Answer"
    @SuppressWarnings("unchecked")
    @PostMapping("/fetch-answer")
    public String getAnswerForCurrentQuestion(HttpSession session, Model model) {
        List<QuestionAnswer> questions = (List<QuestionAnswer>) session.getAttribute("currentQuestionsList");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        if (questions != null && !questions.isEmpty() && currentIndex != null) {
            QuestionAnswer currentCard = questions.get(currentIndex);
            
            model.addAttribute("myTextValue", currentCard.getQuestion()); 
            model.addAttribute("myTextAnswer", currentCard.getAnswer());   
            model.addAttribute("cardCounter", (currentIndex + 1) + " / " + questions.size());
        } else {
            model.addAttribute("myTextValue", "Please select a topic first!");
            model.addAttribute("myTextAnswer", "No question active to answer.");
            model.addAttribute("cardCounter", "0 / 0");
        }

        model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
        model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
        model.addAttribute("isFlipped", true);
        return "index";
    }
    
    // 4. Fires when clicking "Clear All"
    @PostMapping("/clear-all")
    public String clearAll(HttpSession session, Model model) {
        session.removeAttribute("currentQuestionsList");
        session.removeAttribute("currentIndex");
        session.removeAttribute("selectedTopic");
        session.removeAttribute("selectedTopicB");

        model.addAttribute("myTextValue", "Please select a topic to begin!");
        model.addAttribute("myTextAnswer", "");
        model.addAttribute("cardCounter", "0 / 0");
        model.addAttribute("selectedTopic", null); 
        model.addAttribute("selectedTopicB", null); 
        model.addAttribute("isFlipped", false);

        return "index";
    }
    
    // Loads cardmaker.html layout profile
    @GetMapping({"/cardmaker", "/cardmaker/"})
    public String showCardMaker(Model model) {
        model.addAttribute("searchCardId", "");
        model.addAttribute("searchQuestion", "");
        model.addAttribute("searchAnswer", "");
        model.addAttribute("loadedCards", new java.util.ArrayList<>());
        return "cardmaker"; 
    }
}