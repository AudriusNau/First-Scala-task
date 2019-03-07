# --- !Ups

CREATE TABLE relation_tbl (
  note_id bigint,
  label_id bigint,
  FOREIGN KEY (note_id) REFERENCES notes (id),
  FOREIGN KEY (label_id) REFERENCES labels (id),
  PRIMARY KEY(note_id, label_id)
);


# --- !Downs

drop table if exists "relation_tbl";
