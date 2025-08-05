package br.com.meli.jdabot.service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireService {
    public List<String> getQuestionnaireByFilters(List<String> filters) {
        // Aqui entra o acesso a API EXTERNA
        return List.of("Question 1", "Question 2", "Question 3", "Question 4", "Question 5");
    }
}
