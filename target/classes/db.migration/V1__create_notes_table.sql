create table if not exists notes(
    id             serial      not null,
    note_name      varchar(30) not null,
    note_body      text,
    note_name_html text,
    note_body_html text
);


