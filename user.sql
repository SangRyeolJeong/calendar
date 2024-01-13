create table "user" 
(
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    password VARCHAR(40) NOT NULL,
    email VARCHAR(255),
    notification_channel VARCHAR(255) DEFAULT 'pop_up'
);