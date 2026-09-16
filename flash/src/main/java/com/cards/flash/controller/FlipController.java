package com.cards.flash.controller;

import com.cards.flash.entities.QuestionAnswer;
import com.cards.flash.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class FlipController {

	@Autowired
	private CardService cardService;

	@GetMapping("/")
	public String showPage(Model model, HttpSession session) {
		// Clear out stale session parameters upon landing on the root page
		session.removeAttribute("currentQuestionsList");
		session.removeAttribute("shuffledQuestionsList"); // Clear shuffle cache
		session.removeAttribute("currentIndex");
		session.removeAttribute("selectedTopic");
		session.removeAttribute("selectedTopicB");
		session.setAttribute("isShuffled", false); // Reset shuffle state on root load

		model.addAttribute("myTextValue", "Please select a topic to begin.");
		model.addAttribute("myTextAnswer", "");
		model.addAttribute("cardCounter", "0/0");
		model.addAttribute("selectedTopic", null);
		model.addAttribute("selectedTopicB", null);
		model.addAttribute("isFlipped", false);
		model.addAttribute("isShuffled", false);
		return "index";
	}

	// 1. Fires when user selects a topic from either dropdown menu
	@PostMapping("/select-topic")
	public String selectTopic(@RequestParam("topic") String topic, HttpSession session, Model model) {
		List<QuestionAnswer> questions = cardService.getQuestionsByTopic(topic);

		session.setAttribute("currentQuestionsList", questions);
		session.setAttribute("shuffledQuestionsList", null); // Wipe out previous topic's shuffle history
		session.setAttribute("currentIndex", 0);
		session.setAttribute("isShuffled", false); // Reset toggle state for the new topic

		// --- MULTI-DROPDOWN SYNCHRONIZER (FIXED CASE MATCHING) ---
		String normalizedTopic = topic.toLowerCase();
		if (normalizedTopic.equals("verses[a]") || normalizedTopic.equals("atheism")
				|| normalizedTopic.equals("incarnation") || normalizedTopic.equals("soteriology")) {

			session.setAttribute("selectedTopic", normalizedTopic);
			session.setAttribute("selectedTopicB", null);
		} else {
			session.setAttribute("selectedTopic", null);
			session.setAttribute("selectedTopicB", normalizedTopic);
		}

		if (questions != null && !questions.isEmpty()) {
			model.addAttribute("myTextValue", questions.get(0).getQuestion());
			model.addAttribute("cardCounter", "1/" + questions.size());
		} else {
			model.addAttribute("myTextValue", "No questions found for this topic.");
			model.addAttribute("cardCounter", "0/0");
		}

		model.addAttribute("myTextAnswer", "");
		model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
		model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
		model.addAttribute("isFlipped", false);
		model.addAttribute("isShuffled", false);
		return "index";
	}

	// ==========================================================
	// NEW ENDPOINT: Toggles the Shuffle Order In-Memory on Render
	// ==========================================================
	@SuppressWarnings("unchecked")
	@PostMapping("/toggle-shuffle")
	public String toggleShuffle(HttpSession session, Model model) {
		List<QuestionAnswer> baseList = (List<QuestionAnswer>) session.getAttribute("currentQuestionsList");

		if (baseList == null || baseList.isEmpty()) {
			model.addAttribute("myTextValue", "Please select a topic first!");
			model.addAttribute("myTextAnswer", "");
			model.addAttribute("cardCounter", "0/0");
			return "index";
		}

		// Toggle state tracking variable
		Boolean isShuffled = (Boolean) session.getAttribute("isShuffled");
		if (isShuffled == null) {
			isShuffled = false;
		}
		isShuffled = !isShuffled;
		session.setAttribute("isShuffled", isShuffled);

		// Reset tracking index back to the beginning card
		session.setAttribute("currentIndex", 0);

		List<QuestionAnswer> activeWorkingList;
		if (isShuffled) {
			// Shallow clone copy to prevent destructive base sorting overrides
			List<QuestionAnswer> randomizedDeck = new ArrayList<>(baseList);
			Collections.shuffle(randomizedDeck);
			session.setAttribute("shuffledQuestionsList", randomizedDeck);
			activeWorkingList = randomizedDeck;
		} else {
			// Revert pointer directly to standard base list allocation
			session.setAttribute("shuffledQuestionsList", null);
			activeWorkingList = baseList;
		}

		// Display the first card text from your newly modified configuration
		model.addAttribute("myTextValue", activeWorkingList.get(0).getQuestion());
		model.addAttribute("myTextAnswer", "");
		model.addAttribute("cardCounter", "1 / " + activeWorkingList.size());

		model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
		model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
		model.addAttribute("isFlipped", false);
		model.addAttribute("isShuffled", isShuffled);
		return "index";
	}

	// 2. Fires when clicking "Next Question" (Modified to recognize active deck
	// state)
	@SuppressWarnings("unchecked")
	@PostMapping("/fetch-value")
	public String getNextQuestion(HttpSession session, Model model) {
		Boolean isShuffled = (Boolean) session.getAttribute("isShuffled");
		List<QuestionAnswer> activeDeck;

		if (isShuffled != null && isShuffled) {
			activeDeck = (List<QuestionAnswer>) session.getAttribute("shuffledQuestionsList");
		} else {
			activeDeck = (List<QuestionAnswer>) session.getAttribute("currentQuestionsList");
		}

		Integer currentIndex = (Integer) session.getAttribute("currentIndex");

		if (activeDeck != null && !activeDeck.isEmpty() && currentIndex != null) {
			currentIndex = (currentIndex + 1) % activeDeck.size();
			session.setAttribute("currentIndex", currentIndex);

			model.addAttribute("myTextValue", activeDeck.get(currentIndex).getQuestion());
			model.addAttribute("cardCounter", (currentIndex + 1) + " / " + activeDeck.size());
		} else {
			model.addAttribute("myTextValue", "Please select a topic first!");
			model.addAttribute("cardCounter", "0/0");
		}

		model.addAttribute("myTextAnswer", "");
		model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
		model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
		model.addAttribute("isFlipped", false);
		model.addAttribute("isShuffled", isShuffled != null && isShuffled);
		return "index";
	}

	// 3. Fires when clicking "Answer" (Modified to recognize active deck state)
	@SuppressWarnings("unchecked")
	@PostMapping("/fetch-answer")
	public String getAnswerForCurrentQuestion(HttpSession session, Model model) {
		Boolean isShuffled = (Boolean) session.getAttribute("isShuffled");
		List<QuestionAnswer> activeDeck;

		if (isShuffled != null && isShuffled) {
			activeDeck = (List<QuestionAnswer>) session.getAttribute("shuffledQuestionsList");
		} else {
			activeDeck = (List<QuestionAnswer>) session.getAttribute("currentQuestionsList");
		}

		Integer currentIndex = (Integer) session.getAttribute("currentIndex");

		if (activeDeck != null && !activeDeck.isEmpty() && currentIndex != null) {
			QuestionAnswer currentCard = activeDeck.get(currentIndex);

			model.addAttribute("myTextValue", currentCard.getQuestion());
			model.addAttribute("myTextAnswer", currentCard.getAnswer());
			model.addAttribute("cardCounter", (currentIndex + 1) + "/" + activeDeck.size());
		} else {
			model.addAttribute("myTextValue", "Please select a topic first!");
			model.addAttribute("myTextAnswer", "No question active to answer.");
			model.addAttribute("cardCounter", "0 / 0");
		}

		model.addAttribute("selectedTopic", session.getAttribute("selectedTopic"));
		model.addAttribute("selectedTopicB", session.getAttribute("selectedTopicB"));
		model.addAttribute("isFlipped", true);
		model.addAttribute("isShuffled", isShuffled != null && isShuffled);
		return "index";
	}

	// 4. Fires when clicking "Clear All"
	@PostMapping("/clear-all")
	public String clearAll(HttpSession session, Model model) {
		session.removeAttribute("currentQuestionsList");
		session.removeAttribute("shuffledQuestionsList");
		session.removeAttribute("currentIndex");
		session.removeAttribute("selectedTopic");
		session.removeAttribute("selectedTopicB");
		session.setAttribute("isShuffled", false);

		model.addAttribute("myTextValue", "Please select a topic to begin!");
		model.addAttribute("myTextAnswer", "");
		model.addAttribute("cardCounter", "0/0");
		model.addAttribute("selectedTopic", null);
		model.addAttribute("selectedTopicB", null);
		model.addAttribute("isFlipped", false);
		model.addAttribute("isShuffled", false);

		return "index";
	}

	// Loads cardmaker.html layout profile safely inside the file boundaries
	@GetMapping({ "/cardmaker", "/cardmaker/" })
	public String showCardMaker(Model model) {
		model.addAttribute("searchCardId", "");
		model.addAttribute("searchQuestion", "");
		model.addAttribute("searchAnswer", "");
		model.addAttribute("loadedCards", new java.util.ArrayList<>());
		return "cardmaker";
	}
}
