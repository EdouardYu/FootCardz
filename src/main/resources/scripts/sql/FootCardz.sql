CREATE TABLE IF NOT EXISTS "user" (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR NOT NULL,
    CONSTRAINT check_role CHECK (role IN ('USER', 'ADMINISTRATOR'))
);

CREATE TABLE IF NOT EXISTS validation (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    code CHARACTER(6) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    user_id INTEGER NOT NULL,
    CONSTRAINT validation_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE IF NOT EXISTS refresh_token (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    value VARCHAR NOT NULL,
    expired_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS jwt (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    value VARCHAR NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    refresh_token_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT jwt_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT jwt_refresh_token_fk FOREIGN KEY (refresh_token_id) REFERENCES refresh_token(id)
);

CREATE TABLE IF NOT EXISTS nationality (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS league (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS team (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR NOT NULL,
    league_id INTEGER NOT NULL,
    CONSTRAINT team_league_fk FOREIGN KEY (league_id) REFERENCES league(id)
);

CREATE TABLE IF NOT EXISTS player (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR NOT NULL,
    position VARCHAR NOT NULL,
    nationality_id INTEGER NOT NULL,
    team_id INTEGER NOT NULL,
    CONSTRAINT valid_position CHECK (position IN (
        'ST', 'RW', 'LW', 'CAM', 'CM', 'CDM', 'RM', 'LM', 'CB', 'RB', 'LB', 'GK'
    )),
    CONSTRAINT player_nationality_fk FOREIGN KEY (nationality_id) REFERENCES nationality(id),
    CONSTRAINT player_team_fk FOREIGN KEY (team_id) REFERENCES team(id)
);


CREATE TABLE IF NOT EXISTS user_player_collection (
    user_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    acquired_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_player_collection_pk PRIMARY KEY (user_id, player_id),
    CONSTRAINT user_player_collection_user_fk FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT user_player_collection_player_fk FOREIGN KEY (player_id) REFERENCES player(id)
);
