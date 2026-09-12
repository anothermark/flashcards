


package com.cards.flash.dao;

import com.cards.flash.entities.FlashCardSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<FlashCardSubject, Long> {
}