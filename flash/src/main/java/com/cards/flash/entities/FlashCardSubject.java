package com.cards.flash.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flashcards") // This simply tells Hibernate: "Hey, look at my existing MySQL table named 
							//flashcards and link this Java class directly to it."
public class FlashCardSubject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JdbcTypeCode(SqlTypes.JSON) // Tells Hibernate this is a MySQL JSON column
	@Column(name = "questionsandanswers", columnDefinition = "json")

	private List<QuestionAnswer> questionsAndAnswers = new ArrayList<>();

	// Standard Constructors, Getters, and Setters
	public FlashCardSubject() {
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<QuestionAnswer> getQuestionsAndAnswers() {
		return questionsAndAnswers;
	}

	public void setQuestionsAndAnswers(List<QuestionAnswer> questionsAndAnswers) {
		this.questionsAndAnswers = questionsAndAnswers;
	}
	
	

}