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
    (gen_random_uuid(), '22222222-2222-2222-2222-222222222222', 'Alice', 'Smith', '1985-06-15', 'USA', 'Community A', 'Enjoys strategy games.', 1500, 350.0, 0.06, 3875, NULL),
    (gen_random_uuid(), '33333333-3333-3333-3333-333333333333', 'Bob', 'Johnson', '1992-08-24', 'Canada', 'Community B', 'Loves RPG and storytelling.', 1500, 350.0, 0.06, 0, 'https://firebasestorage.googleapis.com/v0/b/quagmire-smu.appspot.com/o/ProfileImages%2F33333333-3333-3333-3333-333333333333?alt=media&token=3afd8a03-ba8d-4e7d-8887-3b1c3eb875bc'),
    (gen_random_uuid(), '44444444-4444-4444-4444-444444444444', 'Catherine', 'Williams', '1990-03-30', 'UK', 'Community C', 'Competitive FPS player.', 1500, 350.0, 0.06, 0, NULL),
    (gen_random_uuid(), '55555555-5555-5555-5555-555555555555', 'David', 'Jones', '1988-11-12', 'Australia', 'Community D', 'Avid fan of open-world games.', 1500, 350.0, 0.06, 1980, NULL),
    (gen_random_uuid(), '66666666-6666-6666-6666-666666666666', 'Emily', 'Brown', '1993-07-21', 'New Zealand', 'Community E', 'Likes puzzle solving and mysteries.', 1500, 350.0, 0.06, 3675, NULL),
    (gen_random_uuid(), '77777777-7777-7777-7777-777777777777', 'Frank', 'Davis', '1980-01-10', 'Ireland', 'Community F', 'Historical game enthusiast.', 1500, 350.0, 0.06, 3100, NULL),
    (gen_random_uuid(), '88888888-8888-8888-8888-888888888888', 'Gina', 'Miller', '1989-04-17', 'South Africa', 'Community G', 'Sports games aficionado.', 1500, 350.0, 0.06, 4800, NULL),
    (gen_random_uuid(), '99999999-9999-9999-9999-999999999999', 'Harry', 'Wilson', '1995-12-05', 'India', 'Community H', 'Expert in chess and tactical games.', 1500, 350.0, 0.06, 4250, NULL),
    (gen_random_uuid(), 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Isabella', 'Moore', '1987-09-09', 'Germany', 'Community I', 'Fan of adventure and exploration games.', 1500, 350.0, 0.06, 3500, NULL),
    (gen_random_uuid(), 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Jack', 'Taylor', '1991-05-25', 'France', 'Community J', 'Enthusiast of simulation games.', 1500, 350.0, 0.06, 2900, NULL),
    (gen_random_uuid(), 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Kelly', 'Anderson', '1986-10-31', 'Brazil', 'Community K', 'Passionate about multiplayer online games.', 1500, 350.0, 0.06, 1500, NULL);