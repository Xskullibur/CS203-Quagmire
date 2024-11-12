-- Insert admin account into users table
INSERT INTO users (user_id, username, email, password_hash, role, created_at, updated_at, email_verified, is_locked) VALUES
    ('11111111-1111-1111-1111-111111111111', 'tuturu', 'tuturu@gmail.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE);

-- Insert player accounts into users table
INSERT INTO users (user_id, username, email, password_hash, role, created_at, updated_at, email_verified, is_locked) VALUES
    ('22222222-2222-2222-2222-222222222222', 'player1', 'player1@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, FALSE),
    ('33333333-3333-3333-3333-333333333333', 'player2', 'player2@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('44444444-4444-4444-4444-444444444444', 'player3', 'player3@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('55555555-5555-5555-5555-555555555555', 'player4', 'player4@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('66666666-6666-6666-6666-666666666666', 'player5', 'player5@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('77777777-7777-7777-7777-777777777777', 'player6', 'player6@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('88888888-8888-8888-8888-888888888888', 'player7', 'player7@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('99999999-9999-9999-9999-999999999999', 'player8', 'player8@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'player9', 'player9@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'player10', 'player10@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'player11', 'player11@example.com', '$2y$12$Iv3tmmwU.E25hWl3GyIkJei3lJ/ehRX3LVxGTbb/pWShbdHSAcnRG', 'PLAYER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, FALSE);

-- Insert player profiles into player_profiles table
INSERT INTO player_profiles (profile_id, user_id, first_name, last_name, date_of_birth, country, community, bio, glicko_rating, rating_deviation, volatility, current_rating, profile_picture_path) VALUES
    ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'Alice', 'Smith', '1985-06-15', 'USA', 'Community A', 'Enjoys strategy games.', 1500, 350.0, 0.06, 3875, NULL),
    ('22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 'Bob', 'Johnson', '1992-08-24', 'Canada', 'Community B', 'Loves RPG and storytelling.', 1500, 350.0, 0.06, 0, 'https://firebasestorage.googleapis.com/v0/b/quagmire-smu.appspot.com/o/ProfileImages%2F33333333-3333-3333-3333-333333333333?alt=media'),
    ('33333333-3333-3333-3333-333333333333', '44444444-4444-4444-4444-444444444444', 'Catherine', 'Williams', '1990-03-30', 'UK', 'Community C', 'Competitive FPS player.', 1500, 350.0, 0.06, 0, NULL),
    ('44444444-4444-4444-4444-444444444444', '55555555-5555-5555-5555-555555555555', 'David', 'Jones', '1988-11-12', 'Australia', 'Community D', 'Avid fan of open-world games.', 1500, 350.0, 0.06, 1980, NULL),
    ('55555555-5555-5555-5555-555555555555', '66666666-6666-6666-6666-666666666666', 'Emily', 'Brown', '1993-07-21', 'New Zealand', 'Community E', 'Likes puzzle solving and mysteries.', 1500, 350.0, 0.06, 3675, NULL),
    ('66666666-6666-6666-6666-666666666666', '77777777-7777-7777-7777-777777777777', 'Frank', 'Davis', '1980-01-10', 'Ireland', 'Community F', 'Historical game enthusiast.', 1500, 350.0, 0.06, 3100, NULL),
    ('77777777-7777-7777-7777-777777777777', '88888888-8888-8888-8888-888888888888', 'Gina', 'Miller', '1989-04-17', 'South Africa', 'Community G', 'Sports games aficionado.', 1500, 350.0, 0.06, 4800, NULL),
    ('88888888-8888-8888-8888-888888888888', '99999999-9999-9999-9999-999999999999', 'Harry', 'Wilson', '1995-12-05', 'India', 'Community H', 'Expert in chess and tactical games.', 1500, 350.0, 0.06, 4250, NULL),
    ('99999999-9999-9999-9999-999999999999', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Isabella', 'Moore', '1987-09-09', 'Germany', 'Community I', 'Fan of adventure and exploration games.', 1500, 350.0, 0.06, 3500, NULL),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Jack', 'Taylor', '1991-05-25', 'France', 'Community J', 'Enthusiast of simulation games.', 1500, 350.0, 0.06, 2900, NULL),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Kelly', 'Anderson', '1986-10-31', 'Brazil', 'Community K', 'Passionate about multiplayer online games.', 1500, 350.0, 0.06, 1500, NULL);

-- Insert mock data for tournaments table
INSERT INTO tournaments (
    id, 
    name, 
    location, 
    start_date, 
    end_date, 
    deadline, 
    max_participants, 
    description, 
    status, 
    winner_id, 
    num_stages, 
    current_stage_index
) VALUES 
(
    '123e4567-e89b-12d3-a456-426614174000', 
    'Summer Chess Championship', 
    'New York City', 
    '2024-07-01 09:00:00', 
    '2024-07-05 18:00:00', 
    '2024-06-15 23:59:59', 
    64, 
    'Annual summer chess tournament featuring players from around the world.', 
    'SCHEDULED', 
    NULL, 
    3, 
    0
),
(
    '223e4567-e89b-12d3-a456-426614174001', 
    'Winter Tennis Open', 
    'Melbourne', 
    '2025-01-15 08:00:00', 
    '2025-01-30 20:00:00', 
    '2024-12-31 23:59:59', 
    128, 
    'International tennis tournament held during the Australian summer.', 
    'SCHEDULED', 
    NULL, 
    5, 
    0
),
(
    '323e4567-e89b-12d3-a456-426614174002', 
    'Global eSports League', 
    'Online', 
    '2024-09-01 12:00:00', 
    '2024-11-30 23:59:59', 
    '2024-08-15 23:59:59', 
    256, 
    'Worldwide online gaming tournament featuring multiple game titles.', 
    'SCHEDULED', 
    NULL, 
    4, 
    0
),
(
    '423e4567-e89b-12d3-a456-426614174003', 
    'City Marathon Challenge', 
    'Chicago', 
    '2024-10-10 07:00:00', 
    '2024-10-10 15:00:00', 
    '2024-09-30 23:59:59', 
    10000, 
    'Annual city marathon open to professional and amateur runners.', 
    'SCHEDULED', 
    NULL, 
    1, 
    0
),
(
    '523e4567-e89b-12d3-a456-426614174004', 
    'International Debate Tournament', 
    'London', 
    '2025-03-20 09:00:00', 
    '2025-03-25 18:00:00', 
    '2025-02-28 23:59:59', 
    32, 
    'Global debate competition for university students.', 
    'SCHEDULED', 
    NULL, 
    5, 
    0
);

INSERT INTO tournament_players (profile_id, tournament_id) VALUES
    ('11111111-1111-1111-1111-111111111111', '223e4567-e89b-12d3-a456-426614174001'),
    ('22222222-2222-2222-2222-222222222222', '223e4567-e89b-12d3-a456-426614174001'),
    ('33333333-3333-3333-3333-333333333333', '223e4567-e89b-12d3-a456-426614174001'),
    ('44444444-4444-4444-4444-444444444444', '223e4567-e89b-12d3-a456-426614174001'), 
    ('55555555-5555-5555-5555-555555555555', '223e4567-e89b-12d3-a456-426614174001'),
    ('66666666-6666-6666-6666-666666666666', '223e4567-e89b-12d3-a456-426614174001');

INSERT INTO achievements (criteria_count, id, criteria_type, description, name)
VALUES 
    (1, 1, 'PARTICIPATION', 'Awarded for first tournament participation', 'First Grip'),
    (5, 2, 'PARTICIPATION', 'Awarded for participating in five tournaments', 'Veteran'),
    (20, 4, 'RATING', 'Awarded for reaching a rating of 2000+', 'Elite Arm');

 
INSERT INTO player_achievements (profile_id, achievement_id) VALUES ('22222222-2222-2222-2222-222222222222', 1)