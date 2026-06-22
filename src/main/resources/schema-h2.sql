-- LearnLoop H2 Schema
-- Tables created in dependency order (parents first)
DROP TABLE IF EXISTS resource_accesses;
DROP TABLE IF EXISTS shared_resources;

DROP TABLE IF EXISTS credit_transactions;
DROP TABLE IF EXISTS certificates;
DROP TABLE IF EXISTS session_feedbacks;
DROP TABLE IF EXISTS task_submissions;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS mentor_assessment_submissions;

DROP TABLE IF EXISTS mentor_assessments;
DROP TABLE IF EXISTS session_doubts;
DROP TABLE IF EXISTS assessments;

DROP TABLE IF EXISTS collaborative_notes;
DROP TABLE IF EXISTS chat_messages;
DROP TABLE IF EXISTS learning_sessions;
DROP TABLE IF EXISTS slots;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS user_skills;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255),
    bio             CLOB,
    avatar_url      VARCHAR(255),
    experience_level VARCHAR(50),
    average_rating  DOUBLE DEFAULT 0.0,
    created_at      TIMESTAMP,
    role            VARCHAR(255) NOT NULL DEFAULT 'LEARNER'
);

CREATE TABLE user_skills (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    skill_name  VARCHAR(255) NOT NULL,
    is_teach    BOOLEAN NOT NULL,
    proficiency VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_skills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE courses (
    id               VARCHAR(50) PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      CLOB,
    language         VARCHAR(50),
    category         VARCHAR(100),
    teacher_id       BIGINT NOT NULL,
    session_type     VARCHAR(50),
    credits_required INT DEFAULT 0,
    price_per_session DECIMAL(10,2) DEFAULT 0.0,
    is_approved      BOOLEAN DEFAULT FALSE,
    rating           DOUBLE DEFAULT 0.0,
    total_ratings    INT DEFAULT 0,
    created_at       TIMESTAMP,
    CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE slots (
    id            VARCHAR(50) PRIMARY KEY,
    course_id     VARCHAR(50) NOT NULL,
    teacher_id    BIGINT NOT NULL,
    slot_date     DATE NOT NULL,
    slot_time     VARCHAR(50) NOT NULL,
    is_booked     BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_slot_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_slot_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE learning_sessions (
    id               VARCHAR(100) PRIMARY KEY,
    course_id        VARCHAR(50),
    title            VARCHAR(255) NOT NULL,
    description      CLOB,
    scheduler_id     BIGINT NOT NULL,
    partner_id       BIGINT NOT NULL,
    scheduled_time   TIMESTAMP NOT NULL,
    status           VARCHAR(50) NOT NULL,
    payment_method   VARCHAR(50),
    credits_paid     INT DEFAULT 0,
    amount_paid      DECIMAL(10,2) DEFAULT 0.0,
    assessment_passed BOOLEAN DEFAULT FALSE,
    meeting_link     VARCHAR(255),
    recording_url    VARCHAR(255),
    CONSTRAINT fk_session_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_session_scheduler FOREIGN KEY (scheduler_id) REFERENCES users(id),
    CONSTRAINT fk_session_partner   FOREIGN KEY (partner_id)   REFERENCES users(id)
);

CREATE TABLE chat_messages (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id  VARCHAR(50),
    sender_id   BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content     CLOB NOT NULL,
    file_url    VARCHAR(255),
    is_read     BOOLEAN DEFAULT FALSE,
    timestamp   TIMESTAMP NOT NULL,
    CONSTRAINT fk_chat_session  FOREIGN KEY (session_id)  REFERENCES learning_sessions(id),
    CONSTRAINT fk_chat_sender   FOREIGN KEY (sender_id)   REFERENCES users(id),
    CONSTRAINT fk_chat_receiver FOREIGN KEY (receiver_id) REFERENCES users(id)
);

CREATE TABLE collaborative_notes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_a_id  BIGINT NOT NULL,
    user_b_id  BIGINT NOT NULL,
    content    CLOB,
    updated_at TIMESTAMP,
    CONSTRAINT fk_note_user_a FOREIGN KEY (user_a_id) REFERENCES users(id),
    CONSTRAINT fk_note_user_b FOREIGN KEY (user_b_id) REFERENCES users(id)
);

CREATE TABLE shared_resources (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id   VARCHAR(50) NOT NULL,
    uploader_id  BIGINT NOT NULL,
    title        VARCHAR(255) NOT NULL,
    type         VARCHAR(50),
    resource_url CLOB,
    file_path    CLOB,
    created_at   TIMESTAMP,
    CONSTRAINT fk_resource_session  FOREIGN KEY (session_id)  REFERENCES learning_sessions(id),
    CONSTRAINT fk_resource_uploader FOREIGN KEY (uploader_id) REFERENCES users(id)
);

CREATE TABLE assessments (
    id              VARCHAR(50) PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    course_id       VARCHAR(50) NOT NULL,
    type            VARCHAR(50) NOT NULL, -- teacher, learner
    score           DOUBLE NOT NULL,
    total_questions INT NOT NULL,
    passed          BOOLEAN NOT NULL,
    timestamp       TIMESTAMP,
    CONSTRAINT fk_assessment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_assessment_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE credit_transactions (
    id          VARCHAR(50) PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    amount      INT NOT NULL,
    type        VARCHAR(50) NOT NULL, -- earned, spent
    description CLOB,
    session_id  VARCHAR(50),
    timestamp   TIMESTAMP,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transaction_session FOREIGN KEY (session_id) REFERENCES learning_sessions(id)
);

-- NEW TABLES BELOW

CREATE TABLE session_doubts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id  VARCHAR(50) NOT NULL,
    learner_id  BIGINT NOT NULL,
    question    CLOB NOT NULL,
    answer      CLOB,
    status      VARCHAR(50) NOT NULL,
    timestamp   TIMESTAMP NOT NULL,
    CONSTRAINT fk_doubt_session FOREIGN KEY (session_id) REFERENCES learning_sessions(id),
    CONSTRAINT fk_doubt_learner FOREIGN KEY (learner_id) REFERENCES users(id)
);

CREATE TABLE mentor_assessments (
    id          VARCHAR(50) PRIMARY KEY,
    course_id   VARCHAR(50),
    mentor_id   BIGINT NOT NULL,
    title       VARCHAR(255) NOT NULL,
    type        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP,
    CONSTRAINT fk_ma_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_ma_mentor FOREIGN KEY (mentor_id) REFERENCES users(id)
);

CREATE TABLE mentor_assessment_questions (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    assessment_id    VARCHAR(50) NOT NULL,
    question_text    CLOB NOT NULL,
    options_json     CLOB,
    correct_answer   VARCHAR(255),
    CONSTRAINT fk_maq_assessment FOREIGN KEY (assessment_id) REFERENCES mentor_assessments(id) ON DELETE CASCADE
);

CREATE TABLE mentor_assessment_submissions (
    id               VARCHAR(50) PRIMARY KEY,
    assessment_id    VARCHAR(50) NOT NULL,
    learner_id       BIGINT NOT NULL,
    score            DOUBLE NOT NULL,
    feedback         CLOB,
    status           VARCHAR(50),
    timestamp        TIMESTAMP,
    CONSTRAINT fk_mas_assessment FOREIGN KEY (assessment_id) REFERENCES mentor_assessments(id) ON DELETE CASCADE,
    CONSTRAINT fk_mas_learner FOREIGN KEY (learner_id) REFERENCES users(id)
);

CREATE TABLE tasks (
    id               VARCHAR(50) PRIMARY KEY,
    session_id       VARCHAR(50),
    mentor_id        BIGINT NOT NULL,
    title            VARCHAR(255) NOT NULL,
    description      CLOB,
    deadline         TIMESTAMP,
    file_url         VARCHAR(255),
    created_at       TIMESTAMP,
    CONSTRAINT fk_task_session FOREIGN KEY (session_id) REFERENCES learning_sessions(id),
    CONSTRAINT fk_task_mentor FOREIGN KEY (mentor_id) REFERENCES users(id)
);

CREATE TABLE task_submissions (
    id               VARCHAR(50) PRIMARY KEY,
    task_id          VARCHAR(50) NOT NULL,
    learner_id       BIGINT NOT NULL,
    submission_file_url VARCHAR(255),
    status           VARCHAR(50) NOT NULL,
    feedback         CLOB,
    submitted_at     TIMESTAMP,
    CONSTRAINT fk_ts_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_ts_learner FOREIGN KEY (learner_id) REFERENCES users(id)
);

CREATE TABLE session_feedbacks (
    id          VARCHAR(50) PRIMARY KEY,
    session_id  VARCHAR(50) NOT NULL,
    learner_id  BIGINT NOT NULL,
    mentor_id   BIGINT NOT NULL,
    rating      INT NOT NULL,
    comment     CLOB,
    timestamp   TIMESTAMP,
    CONSTRAINT fk_fb_session FOREIGN KEY (session_id) REFERENCES learning_sessions(id),
    CONSTRAINT fk_fb_learner FOREIGN KEY (learner_id) REFERENCES users(id),
    CONSTRAINT fk_fb_mentor  FOREIGN KEY (mentor_id)  REFERENCES users(id)
);

CREATE TABLE certificates (
    id               VARCHAR(50) PRIMARY KEY,
    learner_id       BIGINT NOT NULL,
    mentor_id        BIGINT,
    course_id        VARCHAR(50),
    course_name      VARCHAR(255),
    issue_date       TIMESTAMP NOT NULL,
    certificate_url  VARCHAR(255),
    final_score      DOUBLE,
    verification_qr  TEXT,
    status           VARCHAR(50),
    certificate_type VARCHAR(50),
    mentor_signature VARCHAR(255),
    platform_signature VARCHAR(255),
    CONSTRAINT fk_cert_learner FOREIGN KEY (learner_id) REFERENCES users(id),
    CONSTRAINT fk_cert_mentor FOREIGN KEY (mentor_id) REFERENCES users(id),
    CONSTRAINT fk_cert_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE resource_accesses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    learner_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    accessed_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_res_access_learner FOREIGN KEY (learner_id) REFERENCES users(id),
    CONSTRAINT fk_res_access_resource FOREIGN KEY (resource_id) REFERENCES shared_resources(id)
);
