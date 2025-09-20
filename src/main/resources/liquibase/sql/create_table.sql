CREATE TABLE IF NOT EXISTS job
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255)        NOT NULL,
    link        VARCHAR(255) UNIQUE NOT NULL,
    date_posted DATE,
    expire_date DATE,
    area        VARCHAR(255),
    job_type    VARCHAR(255),
    salary      DECIMAL(10, 2),
    description LONGTEXT
);
