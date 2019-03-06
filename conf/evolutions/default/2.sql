# --- !Ups

CREATE TABLE notes (
  id BIGSERIAL PRIMARY KEY,
  text TEXT,
  color TEXT
);

# --- !Downs

drop table if exists "notes";


