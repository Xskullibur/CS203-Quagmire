-- Enable the pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create users table
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    date_of_birth DATE,
    country VARCHAR(255),
    bio TEXT,
    current_rating FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create index on user_id for faster searches
CREATE INDEX idx_player_profiles_user_id ON player_profiles (user_id);

-- Insert admin account into users table
INSERT INTO users (user_id, username, email, password_hash, role, created_at, updated_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Tuturu', 'tuturu@gmail.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert player accounts into users table
INSERT INTO users (user_id, username, email, password_hash, role, created_at, updated_at) VALUES
    ('22222222-2222-2222-2222-222222222222', 'player1', 'player1@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'player2', 'player2@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('44444444-4444-4444-4444-444444444444', 'player3', 'player3@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('55555555-5555-5555-5555-555555555555', 'player4', 'player4@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('66666666-6666-6666-6666-666666666666', 'player5', 'player5@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('77777777-7777-7777-7777-777777777777', 'player6', 'player6@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('88888888-8888-8888-8888-888888888888', 'player7', 'player7@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('99999999-9999-9999-9999-999999999999', 'player8', 'player8@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'player9', 'player9@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'player10', 'player10@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'player11', 'player11@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert player profiles into player_profiles table
INSERT INTO player_profiles (profile_id, user_id, first_name, last_name, date_of_birth, country, bio, current_rating, created_at, updated_at) VALUES
    (gen_random_uuid(), '22222222-2222-2222-2222-222222222222', 'Alice', 'Smith', '1985-06-15', 'USA', 'Enjoys strategy games.', 3875, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '33333333-3333-3333-3333-333333333333', 'Bob', 'Johnson', '1992-08-24', 'Canada', 'Loves RPG and storytelling.', 4420, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '44444444-4444-4444-4444-444444444444', 'Catherine', 'Williams', '1990-03-30', 'UK', 'Competitive FPS player.', 2550, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '55555555-5555-5555-5555-555555555555', 'David', 'Jones', '1988-11-12', 'Australia', 'Avid fan of open-world games.', 1980, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '66666666-6666-6666-6666-666666666666', 'Emily', 'Brown', '1993-07-21', 'New Zealand', 'Likes puzzle solving and mysteries.', 3675, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '77777777-7777-7777-7777-777777777777', 'Frank', 'Davis', '1980-01-10', 'Ireland', 'Historical game enthusiast.', 3100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '88888888-8888-8888-8888-888888888888', 'Gina', 'Miller', '1989-04-17', 'South Africa', 'Sports games aficionado.', 4800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), '99999999-9999-9999-9999-999999999999', 'Harry', 'Wilson', '1995-12-05', 'India', 'Expert in chess and tactical games.', 4250, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Isabella', 'Moore', '1987-09-09', 'Germany', 'Fan of adventure and exploration games.', 3500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Jack', 'Taylor', '1991-05-25', 'France', 'Enthusiast of simulation games.', 2900, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Kelly', 'Anderson', '1986-10-31', 'Brazil', 'Passionate about multiplayer online games.', 1500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);