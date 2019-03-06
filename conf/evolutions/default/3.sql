# --- !Ups

CREATE TABLE labels (
  id BIGSERIAL PRIMARY KEY,
  label TEXT
);

# --- !Downs

drop table if exists "labels";
