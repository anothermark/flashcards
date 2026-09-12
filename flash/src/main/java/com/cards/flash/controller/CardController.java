package com.cards.flash.controller;

import com.cards.flash.entities.QuestionAnswer;
import com.cards.flash.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/flashcards")
public class CardController {

    @Autowired
    private CardService cardService;

    // Baseline Home Route: Automatically redirects root URL visitors directly to the dashboard canvas
    @GetMapping("/")
    public String showHomePage(Model model) {
        // Pass placeholder values so the UI displays all forms and buttons on first load
        model.addAttribute("currentSubjectId", 1); // Defaults to Math view context layout
        model.addAttribute("currentSubjectName", "Math");
        
        // Fetch whatever is already inside the Math list to display below the form immediately
        List<QuestionAnswer> cardList = cardService.getOrCreateSubject(1L, "Math");
        model.addAttribute("loadedCards", cardList);
        
        return "cardmaker";
    }    
    
    

    // 1. GET - Triggered when clicking a sidebar button (e.g., Math)
    @GetMapping("/subject/{id}")
    public String getSubject(@PathVariable Long id, @RequestParam String name, Model model) {
        List<QuestionAnswer> cardList = cardService.getOrCreateSubject(id, name);
        
        model.addAttribute("loadedCards", cardList);
        model.addAttribute("currentSubjectId", id);
        model.addAttribute("currentSubjectName", name);
        System.out.println("jie589485495n");
        return "cardmaker"; // Simply updates your index file natively
    }

    // 2. GET - Searches for a card by ID inside the list and loads it into the form fields
    @GetMapping("/subject/{id}/find")
    public String findCard(@PathVariable Long id, 
                           @RequestParam String name,
                           @RequestParam(required = false) Integer cardId, 
                           Model model) {
        List<QuestionAnswer> cardList = cardService.getOrCreateSubject(id, name);
        
        if (cardId != null) {
            QuestionAnswer foundCard = cardList.stream()
                    .filter(c -> c.getId().equals(cardId))
                    .findFirst()
                    .orElse(new QuestionAnswer(cardId, "", ""));
            
            model.addAttribute("searchCardId", foundCard.getId());
            model.addAttribute("searchQuestion", foundCard.getQuestion());
            model.addAttribute("searchAnswer", foundCard.getAnswer());
        }
        
        model.addAttribute("loadedCards", cardList);
        model.addAttribute("currentSubjectId", id);
        model.addAttribute("currentSubjectName", name);
        return "cardmaker";
    }

    // 3. POST - Appends a flashcard to the array list inside your JSON column
    
    @PostMapping("/subject/{id}")
    public String addCard(@PathVariable Long id, 
                          @RequestParam String name,
                          @RequestParam(required = false) String question, 
                          @RequestParam(required = false) String answer, 
                          Model model) {
        
        if (question != null && answer != null && !question.trim().isEmpty()) {
            QuestionAnswer newItem = new QuestionAnswer(null, question, answer);
            cardService.addCard(id, newItem);
        }
        
        List<QuestionAnswer> cardList = cardService.getOrCreateSubject(id, name);
        
        model.addAttribute("loadedCards", cardList);
        model.addAttribute("currentSubjectId", id);
        model.addAttribute("currentSubjectName", name);
        return "cardmaker";
    }

    // 4. POST - Modifies an existing question and answer card at a targeted array position index
    @PostMapping("/subject/{id}/update")
    public String updateCard(@PathVariable Long id, 
                             @RequestParam String name,
                             @RequestParam(required = false) Integer cardId,
                             @RequestParam(required = false) String question, 
                             @RequestParam(required = false) String answer, 
                             Model model) {
        
        if (cardId != null && question != null && answer != null) {
        	QuestionAnswer updatedItem = new QuestionAnswer(cardId, question, answer);
            cardService.updateCard(id, updatedItem);
        }
        
        List<QuestionAnswer> cardList = cardService.getOrCreateSubject(id, name);
        model.addAttribute("loadedCards", cardList);
        model.addAttribute("currentSubjectId", id);
        model.addAttribute("currentSubjectName", name);
        return "cardmaker";
    }

    // 5. POST - Removes a flashcard from the JSON array and sequentially re-indexes the list positions
    @PostMapping("/subject/{id}/delete")
    public String deleteCard(@PathVariable Long id, 
                             @RequestParam String name,
                             @RequestParam(required = false) Integer cardId, 
                             Model model) {
        
        if (cardId != null) {
            cardService.deleteCard(id, cardId);
        }
        
        List<QuestionAnswer> cardList = cardService.getOrCreateSubject(id, name);
        model.addAttribute("loadedCards", cardList);
        model.addAttribute("currentSubjectId", id);
        model.addAttribute("currentSubjectName", name);
        return "cardmaker";
    }
      
    
    
    
    
}