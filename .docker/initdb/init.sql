-- Enable the pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create users table
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(72) NOT NULL,
    role VARCHAR(10) CHECK (role IN ('PLAYER', 'ADMIN')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username and email for faster searches
CREATE INDEX idx_users_username ON users (username);

CREATE INDEX idx_users_email ON users (email);

-- Create player_profiles table
CREATE TABLE player_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    user_id UUID UNIQUE NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    country VARCHAR(50) NOT NULL,
    bio TEXT NOT NULL,
    current_rating FLOAT DEFAULT 1500,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create index on user_id for faster searches
CREATE INDEX idx_player_profiles_user_id ON player_profiles (user_id);

-- Insert admin account into users table
INSERT INTO
    users (
        username,
        email,
        password_hash,
        role,
        created_at,
        updated_at
    )
VALUES (
        'Tuturu',
        'tuturu@gmail.com',
        '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG',
        'ADMIN',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Insert first player account into users table
INSERT INTO
    users (
        username,
        email,
        password_hash,
        role,
        created_at,
        updated_at
    )
VALUES (
        'player1',
        'player1@example.com',
        '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG',
        'PLAYER',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Insert second player account into users table
INSERT INTO
    users (
        username,
        email,
        password_hash,
        role,
        created_at,
        updated_at
    )
VALUES (
        'player2',
        'player2@example.com',
        '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG',
        'PLAYER',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Insert first player profile into player_profiles table
INSERT INTO
    player_profiles (
        user_id,
        first_name,
        last_name,
        date_of_birth,
        country,
        bio,
        created_at,
        updated_at
    )
VALUES (
        (
            SELECT user_id
            FROM users
            WHERE
                username = 'player1'
        ),
        'First1',
        'Last1',
        '1990-01-01',
        'Country1',
        'Bio for player1',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Insert second player profile into player_profiles table
INSERT INTO
    player_profiles (
        user_id,
        first_name,
        last_name,
        date_of_birth,
        country,
        bio,
        created_at,
        updated_at
    )
VALUES (
        (
            SELECT user_id
            FROM users
            WHERE
                username = 'player2'
        ),
        'First2',
        'Last2',
        '1992-02-02',
        'Country2',
        'Bio for player2',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );