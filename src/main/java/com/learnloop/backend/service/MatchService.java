package com.learnloop.backend.service;

import com.learnloop.backend.dto.MatchResponse;
import com.learnloop.backend.model.User;
import com.learnloop.backend.model.UserSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    private UserService userService;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<MatchResponse> getRecommendations(User currentUser) {
        List<User> otherUsers = userService.findAllExcept(currentUser.getId());
        if (otherUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // Construct request payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", currentUser.getId());
        requestBody.put("skills_teach", currentUser.getSkills().stream()
                .filter(UserSkill::isTeach)
                .map(UserSkill::getSkillName)
                .collect(Collectors.toList()));
        requestBody.put("skills_learn", currentUser.getSkills().stream()
                .filter(s -> !s.isTeach())
                .map(UserSkill::getSkillName)
                .collect(Collectors.toList()));

        List<Map<String, Object>> candidates = new ArrayList<>();
        for (User u : otherUsers) {
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("user_id", u.getId());
            candidate.put("skills_teach", u.getSkills().stream()
                    .filter(UserSkill::isTeach)
                    .map(UserSkill::getSkillName)
                    .collect(Collectors.toList()));
            candidate.put("skills_learn", u.getSkills().stream()
                    .filter(s -> !s.isTeach())
                    .map(UserSkill::getSkillName)
                    .collect(Collectors.toList()));
            candidates.add(candidate);
        }
        requestBody.put("candidates", candidates);

        try {
            String url = aiServiceUrl + "/match";
            List<Map<String, Object>> response = restTemplate.postForObject(url, requestBody, List.class);
            if (response == null) {
                return Collections.emptyList();
            }

            List<MatchResponse> matches = new ArrayList<>();
            for (Map<String, Object> item : response) {
                Long candidateId = ((Number) item.get("user_id")).longValue();
                double score = ((Number) item.get("score")).doubleValue();
                List<String> skillGaps = (List<String>) item.get("skill_gap");

                Optional<User> userOpt = userService.findById(candidateId);
                if (userOpt.isPresent()) {
                    matches.add(new MatchResponse(userOpt.get(), score, skillGaps));
                }
            }

            // Sort matches descending by compatibility score
            matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            return matches;
        } catch (Exception e) {
            System.err.println("Error calling AI service matching endpoint: " + e.getMessage());
            // Fallback: simple exact match if AI service is down
            return getLocalMatchingFallback(currentUser, otherUsers);
        }
    }

    private List<MatchResponse> getLocalMatchingFallback(User currentUser, List<User> otherUsers) {
        List<MatchResponse> matches = new ArrayList<>();
        Set<String> myLearn = currentUser.getSkills().stream()
                .filter(s -> !s.isTeach())
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> myTeach = currentUser.getSkills().stream()
                .filter(UserSkill::isTeach)
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        for (User u : otherUsers) {
            Set<String> uTeach = u.getSkills().stream()
                    .filter(UserSkill::isTeach)
                    .map(s -> s.getSkillName().toLowerCase())
                    .collect(Collectors.toSet());

            Set<String> uLearn = u.getSkills().stream()
                    .filter(s -> !s.isTeach())
                    .map(s -> s.getSkillName().toLowerCase())
                    .collect(Collectors.toSet());

            // Count overlaps
            long match1 = myLearn.stream().filter(uTeach::contains).count();
            long match2 = myTeach.stream().filter(uLearn::contains).count();

            double score = 0.0;
            if (!myLearn.isEmpty() || !uLearn.isEmpty()) {
                double term1 = myLearn.isEmpty() ? 100.0 : (match1 * 100.0 / myLearn.size());
                double term2 = uLearn.isEmpty() ? 100.0 : (match2 * 100.0 / uLearn.size());
                score = (term1 + term2) / 2.0;
            }

            List<String> gaps = currentUser.getSkills().stream()
                    .filter(s -> !s.isTeach())
                    .map(UserSkill::getSkillName)
                    .filter(s -> !uTeach.contains(s.toLowerCase()))
                    .collect(Collectors.toList());

            matches.add(new MatchResponse(u, score, gaps));
        }

        matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return matches;
    }
}
