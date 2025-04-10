CREATE SCHEMA ulab_edu;

create table ulab_edu.person
(
    id        SERIAL PRIMARY KEY,
    full_name varchar(50) not null,
    title     varchar(50) not null,
    age       integer     not null
);

CREATE UNIQUE INDEX idx_ulab_edu_person_title on ulab_edu.person (title);

comment on table ulab_edu.person is 'Справочник используется для хранения баджей';
comment on column ulab_edu.person.id is 'Идентификатор пользователя';
comment on column ulab_edu.person.full_name is 'Полное имя';
comment on column ulab_edu.person.title is 'Должность';
comment on column ulab_edu.person.age is 'Возраст';
