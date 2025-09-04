CREATE TABLE job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    link VARCHAR(255) UNIQUE,
    date_posted VARCHAR(255),
    expire_date VARCHAR(255),
    area VARCHAR(255),
    job_type VARCHAR(255),
    salary VARCHAR(255),
    description TEXT
);
