package com.learnloop.backend.config;

import com.learnloop.backend.model.Course;
import com.learnloop.backend.model.User;
import com.learnloop.backend.model.UserSkill;
import com.learnloop.backend.repository.CourseRepository;
import com.learnloop.backend.repository.UserRepository;
import com.learnloop.backend.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println("[LearnLoop] Database already contains users, skipping seeding.");
            return;
        }

        System.out.println("[LearnLoop] Database seeder seeding profiles and courses...");

        String encodedPassword = passwordEncoder.encode("197300");

        List<SeedUser> seedUsers = Arrays.asList(
            new SeedUser("Aravind Kumar", "aravind.kumar@gmail.com", "Java, Spring Boot, REST APIs", "Machine Learning, AI"),
            new SeedUser("Swetha J", "swetha.j@gmail.com", "React.js, HTML, CSS", "Spring Boot, Microservices"),
            new SeedUser("Kavin Raj", "kavin.raj@gmail.com", "Python, Data Analysis", "Cloud Computing, AWS"),
            new SeedUser("Harini S", "harini.s@gmail.com", "UI/UX Design, Figma", "Frontend Development"),
            new SeedUser("Pradeep Kumar", "pradeep.kumar@gmail.com", "SQL, Database Management", "Data Science"),
            new SeedUser("Nivetha R", "nivetha.r@gmail.com", "Content Writing, Blogging", "Digital Marketing"),
            new SeedUser("Vigneshwaran M", "vignesh.m@gmail.com", "Android Development, Kotlin", "Flutter Development"),
            new SeedUser("Keerthana P", "keerthana.p@gmail.com", "Graphic Design, Canva", "Video Editing"),
            new SeedUser("Dinesh Kumar", "dinesh.kumar@gmail.com", "C++, Data Structures", "System Design"),
            new SeedUser("Abinaya S", "abinaya.s@gmail.com", "Public Speaking", "Leadership Skills"),
            new SeedUser("Bharath Kumar", "bharath.kumar@gmail.com", "Cybersecurity Basics", "Ethical Hacking"),
            new SeedUser("Janani R", "janani.r@gmail.com", "Excel, Power BI", "Python Automation"),
            new SeedUser("Surya Prakash", "surya.prakash@gmail.com", "DevOps, Docker", "Kubernetes"),
            new SeedUser("Pavithra K", "pavithra.k@gmail.com", "Social Media Marketing", "SEO"),
            new SeedUser("Saravanan M", "saravanan.m@gmail.com", "JavaScript, React.js", "Node.js"),
            new SeedUser("Dharshini V", "dharshini.v@gmail.com", "Communication Skills", "Project Management"),
            new SeedUser("Gokul Raj", "gokul.raj@gmail.com", "AWS Cloud Fundamentals", "AI & Generative AI"),
            new SeedUser("Monisha S", "monisha.s@gmail.com", "Video Editing", "Motion Graphics"),
            new SeedUser("Ashwin Kumar", "ashwin.kumar@gmail.com", "Machine Learning Basics", "Deep Learning"),
            new SeedUser("Revathi P", "revathi.p@gmail.com", "Human Resource Management", "Business Analytics")
        );

        for (SeedUser su : seedUsers) {
            User user = new User();
            user.setEmail(su.email);
            user.setPassword(encodedPassword);
            user.setFullName(su.name);
            user.setBio("Hi, my name is " + su.name + ". Let's learn together!");
            user.setExperienceLevel("INTERMEDIATE");
            user.setCreatedAt(LocalDateTime.now());
            user.setSkills(new ArrayList<>());
            
            user = userRepository.save(user);

            // Save Teach skills
            for (String sName : su.teach.split(",")) {
                String skillName = sName.trim();
                if (!skillName.isEmpty()) {
                    UserSkill skill = UserSkill.builder()
                        .user(user)
                        .skillName(skillName)
                        .isTeach(true)
                        .proficiency("ADVANCED")
                        .build();
                    userSkillRepository.save(skill);
                    user.getSkills().add(skill);

                    // Seed Course for this teach skill
                    Course course = new Course();
                    course.setId(UUID.randomUUID().toString());
                    course.setTitle("Mastering " + skillName);
                    course.setDescription("Comprehensive guide and 1-on-1 mentoring for " + skillName + ". Taught by " + su.name + ".");
                    course.setLanguage("English");
                    course.setCategory(getCategoryForSkill(skillName));
                    course.setTeacher(user);
                    course.setSessionType("LIVE");
                    course.setCreditsRequired(10);
                    course.setPricePerSession(0.0);
                    course.setIsApproved(true);
                    course.setRating(5.0);
                    course.setTotalRatings(1);
                    course.setCreatedAt(LocalDateTime.now());
                    courseRepository.save(course);
                }
            }

            // Save Learn skills
            for (String sName : su.learn.split(",")) {
                String skillName = sName.trim();
                if (!skillName.isEmpty()) {
                    UserSkill skill = UserSkill.builder()
                        .user(user)
                        .skillName(skillName)
                        .isTeach(false)
                        .proficiency("BEGINNER")
                        .build();
                    userSkillRepository.save(skill);
                    user.getSkills().add(skill);
                }
            }
        }

        System.out.println("[LearnLoop] Seed data loaded successfully!");
    }

    private String getCategoryForSkill(String skillName) {
        String skillLower = skillName.toLowerCase();
        if (skillLower.contains("java") || skillLower.contains("spring") || skillLower.contains("c++") || skillLower.contains("javascript") || skillLower.contains("kotlin") || skillLower.contains("python") || skillLower.contains("android") || skillLower.contains("react") || skillLower.contains("devops") || skillLower.contains("docker")) {
            return "Programming & Tech";
        } else if (skillLower.contains("design") || skillLower.contains("figma") || skillLower.contains("graphic") || skillLower.contains("canva") || skillLower.contains("motion") || skillLower.contains("video")) {
            return "Design & Creative";
        } else if (skillLower.contains("marketing") || skillLower.contains("seo") || skillLower.contains("blogging") || skillLower.contains("writing")) {
            return "Marketing & Writing";
        } else if (skillLower.contains("ml") || skillLower.contains("ai") || skillLower.contains("machine learning") || skillLower.contains("data") || skillLower.contains("excel") || skillLower.contains("sql") || skillLower.contains("power bi")) {
            return "Data Science & AI";
        } else if (skillLower.contains("cybersecurity") || skillLower.contains("hacking") || skillLower.contains("cloud") || skillLower.contains("aws")) {
            return "Cloud & Security";
        } else {
            return "Personal Development";
        }
    }

    private static class SeedUser {
        String name;
        String email;
        String teach;
        String learn;

        SeedUser(String name, String email, String teach, String learn) {
            this.name = name;
            this.email = email;
            this.teach = teach;
            this.learn = learn;
        }
    }
}
