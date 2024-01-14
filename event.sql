CREATE TABLE "event" 
(
    event_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES "user"(user_id),
    eventname VARCHAR(40) NOT NULL,
    day DATE NOT NULL,
    starttime TIME NOT NULL,
    endtime TIME NOT NULL,
    place VARCHAR(40) NOT NULL,
    interval INTEGER NOT NULL,
    timeframe INTEGER NOT NULL
);