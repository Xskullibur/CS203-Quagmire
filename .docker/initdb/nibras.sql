


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
INSERT INTO player_profiles (profile_id, user_id, current_rating, date_of_birth, glicko_rating, rating_deviation, volatility, bio, community, country, first_name, last_name, profile_picture_path) VALUES
    ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 1500, '1990-01-01', 1500, 200, 0.06, 'A skilled player from community A', 'Community A', 'USA', 'John', 'Doe', '/images/profiles/john_doe.png'),
    ('11111111-1111-1111-1111-111111111112', '33333333-3333-3333-3333-333333333333', 1450, '1991-02-02', 1450, 220, 0.07, 'An experienced player from community B', 'Community B', 'UK', 'Jane', 'Smith', '/images/profiles/jane_smith.png'),
    ('11111111-1111-1111-1111-111111111113', '44444444-4444-4444-4444-444444444444', 1600, '1992-03-03', 1600, 190, 0.05, 'A rising star in community C', 'Community C', 'Canada', 'Emily', 'Johnson', '/images/profiles/emily_johnson.png'),
    ('11111111-1111-1111-1111-111111111114', '55555555-5555-5555-5555-555555555555', 1520, '1993-04-04', 1520, 210, 0.06, 'Dedicated player from community D', 'Community D', 'Australia', 'Michael', 'Brown', '/images/profiles/michael_brown.png'),
    ('11111111-1111-1111-1111-111111111115', '66666666-6666-6666-6666-666666666666', 1480, '1994-05-05', 1480, 230, 0.07, 'Enthusiastic player from community E', 'Community E', 'Germany', 'Sarah', 'Davis', '/images/profiles/sarah_davis.png'),
    ('11111111-1111-1111-1111-111111111116', '77777777-7777-7777-7777-777777777777', 1550, '1995-06-06', 1550, 195, 0.06, 'Skilled player from community F', 'Community F', 'France', 'James', 'Miller', '/images/profiles/james_miller.png'),
    ('11111111-1111-1111-1111-111111111117', '88888888-8888-8888-8888-888888888888', 1530, '1996-07-07', 1530, 205, 0.06, 'Strategic player from community G', 'Community G', 'Italy', 'Laura', 'Wilson', '/images/profiles/laura_wilson.png'),
    ('11111111-1111-1111-1111-111111111118', '99999999-9999-9999-9999-999999999999', 1490, '1997-08-08', 1490, 225, 0.07, 'Player from community H', 'Community H', 'Spain', 'Robert', 'Moore', '/images/profiles/robert_moore.png'),
    ('11111111-1111-1111-1111-111111111119', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1510, '1998-09-09', 1510, 215, 0.06, 'Focused player from community I', 'Community I', 'Netherlands', 'Sophia', 'Taylor', '/images/profiles/sophia_taylor.png'),
    ('11111111-1111-1111-1111-111111111120', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 1475, '1999-10-10', 1475, 235, 0.07, 'Tactical player from community J', 'Community J', 'Sweden', 'William', 'Anderson', '/images/profiles/william_anderson.png'),
    ('11111111-1111-1111-1111-111111111121', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 1500, '2000-11-11', 1500, 200, 0.06, 'A dedicated player from community K', 'Community K', 'Norway', 'Olivia', 'Thomas', '/images/profiles/olivia_thomas.png');


-- Insert example tournaments into the tournament table with corrected status values
INSERT INTO tournaments (id, name, description, location, current_stage_index, max_participants, num_stages, deadline, start_date, end_date, status, winner_id, photo_url) VALUES
    ('10000000-0000-0000-0000-000000000001', 'Summer Championship', 'A high-level tournament held every summer with participants from all over the country.', 'New York, USA', 0, 64, 4, '2024-06-01', '2024-06-15', '2024-07-01', 'SCHEDULED', NULL, '/images/tournaments/summer_championship.png'),
    ('10000000-0000-0000-0000-000000000002', 'Winter Classic', 'An annual winter tournament for elite players.', 'Toronto, Canada', 0, 32, 3, '2024-12-01', '2024-12-10', '2024-12-25', 'SCHEDULED', NULL, '/images/tournaments/winter_classic.png'),
    ('10000000-0000-0000-0000-000000000003', 'Spring Open', 'A friendly competition for amateur and intermediate players.', 'San Francisco, USA', 0, 128, 5, '2024-03-01', '2024-03-15', '2024-04-01', 'SCHEDULED', NULL, '/images/tournaments/spring_open.png'),
    ('10000000-0000-0000-0000-000000000004', 'Autumn Invitational', 'An invitation-only event featuring top players from around the world.', 'Paris, France', 0, 16, 4, '2024-09-01', '2024-09-10', '2024-10-01', 'SCHEDULED', NULL, '/images/tournaments/autumn_invitational.png'),
    ('10000000-0000-0000-0000-000000000005', 'City League Tournament', 'A regional tournament for emerging players within the city league.', 'Berlin, Germany', 0, 64, 3, '2024-08-01', '2024-08-15', '2024-08-30', 'SCHEDULED', NULL, '/images/tournaments/city_league_tournament.png');

    -- Insert players into tournaments
INSERT INTO tournament_players (profile_id, tournament_id) VALUES
    -- Players for the "Summer Championship"
    ('11111111-1111-1111-1111-111111111111', '10000000-0000-0000-0000-000000000001'), -- John Doe
    ('11111111-1111-1111-1111-111111111112', '10000000-0000-0000-0000-000000000001'), -- Jane Smith
    ('11111111-1111-1111-1111-111111111113', '10000000-0000-0000-0000-000000000001'), -- Emily Johnson

    -- Players for the "Winter Classic"
    ('11111111-1111-1111-1111-111111111114', '10000000-0000-0000-0000-000000000002'), -- Michael Brown
    ('11111111-1111-1111-1111-111111111115', '10000000-0000-0000-0000-000000000002'), -- Sarah Davis

    -- Players for the "Spring Open"
    ('11111111-1111-1111-1111-111111111116', '10000000-0000-0000-0000-000000000003'), -- James Miller
    ('11111111-1111-1111-1111-111111111117', '10000000-0000-0000-0000-000000000003'), -- Laura Wilson
    ('11111111-1111-1111-1111-111111111118', '10000000-0000-0000-0000-000000000003'), -- Robert Moore

    -- Players for the "Autumn Invitational"
    ('11111111-1111-1111-1111-111111111119', '10000000-0000-0000-0000-000000000004'), -- Sophia Taylor
    ('11111111-1111-1111-1111-111111111120', '10000000-0000-0000-0000-000000000004'), -- William Anderson

    -- Players for the "City League Tournament"
    ('11111111-1111-1111-1111-111111111121', '10000000-0000-0000-0000-000000000005'), -- Olivia Thomas
    ('11111111-1111-1111-1111-111111111111', '10000000-0000-0000-0000-000000000005'), -- John Doe (again, if allowed in multiple tournaments)
    ('11111111-1111-1111-1111-111111111112', '10000000-0000-0000-0000-000000000005'); -- Jane Smith (again)


-- Insert stages for Summer Championship (4 stages as specified)
INSERT INTO stages (stage_id, created_at, start_time, end_time, format, stage_name, status, winner_id, tournament)
VALUES 
    ('20000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '2024-06-01', '2024-06-05', 'SINGLE_ELIMINATION', 'Round of 64', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000001'),
    ('20000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP, '2024-06-06', '2024-06-10', 'SINGLE_ELIMINATION', 'Round of 32', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000001'),
    ('20000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP, '2024-06-11', '2024-06-13', 'SINGLE_ELIMINATION', 'Quarterfinals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000001'),
    ('20000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP, '2024-06-14', '2024-06-15', 'SINGLE_ELIMINATION', 'Finals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000001');

-- Insert stages for Winter Classic (3 stages as specified)
INSERT INTO stages (stage_id, created_at, start_time, end_time, format, stage_name, status, winner_id, tournament)
VALUES 
    ('20000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP, '2024-12-01', '2024-12-05', 'SINGLE_ELIMINATION', 'Round of 32', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000002'),
    ('20000000-0000-0000-0000-000000000006', CURRENT_TIMESTAMP, '2024-12-06', '2024-12-15', 'SINGLE_ELIMINATION', 'Quarterfinals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000002'),
    ('20000000-0000-0000-0000-000000000007', CURRENT_TIMESTAMP, '2024-12-16', '2024-12-25', 'SINGLE_ELIMINATION', 'Finals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000002');

-- Insert stages for Spring Open (5 stages as specified)
INSERT INTO stages (stage_id, created_at, start_time, end_time, format, stage_name, status, winner_id, tournament)
VALUES 
    ('20000000-0000-0000-0000-000000000008', CURRENT_TIMESTAMP, '2024-03-01', '2024-03-05', 'SINGLE_ELIMINATION', 'Round of 128', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000003'),
    ('20000000-0000-0000-0000-000000000009', CURRENT_TIMESTAMP, '2024-03-06', '2024-03-10', 'SINGLE_ELIMINATION', 'Round of 64', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000003'),
    ('20000000-0000-0000-0000-000000000010', CURRENT_TIMESTAMP, '2024-03-11', '2024-03-15', 'SINGLE_ELIMINATION', 'Round of 32', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000003'),
    ('20000000-0000-0000-0000-000000000011', CURRENT_TIMESTAMP, '2024-03-16', '2024-03-20', 'SINGLE_ELIMINATION', 'Quarterfinals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000003'),
    ('20000000-0000-0000-0000-000000000012', CURRENT_TIMESTAMP, '2024-03-21', '2024-04-01', 'SINGLE_ELIMINATION', 'Finals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000003');

-- Insert stages for Autumn Invitational (4 stages as specified)
INSERT INTO stages (stage_id, created_at, start_time, end_time, format, stage_name, status, winner_id, tournament)
VALUES 
    ('20000000-0000-0000-0000-000000000013', CURRENT_TIMESTAMP, '2024-09-01', '2024-09-03', 'SINGLE_ELIMINATION', 'Round of 16', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000004'),
    ('20000000-0000-0000-0000-000000000014', CURRENT_TIMESTAMP, '2024-09-04', '2024-09-07', 'SINGLE_ELIMINATION', 'Quarterfinals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000004'),
    ('20000000-0000-0000-0000-000000000015', CURRENT_TIMESTAMP, '2024-09-08', '2024-09-09', 'SINGLE_ELIMINATION', 'Semifinals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000004'),
    ('20000000-0000-0000-0000-000000000016', CURRENT_TIMESTAMP, '2024-09-10', '2024-10-01', 'SINGLE_ELIMINATION', 'Finals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000004');

-- Insert stages for City League Tournament (3 stages as specified)
INSERT INTO stages (stage_id, created_at, start_time, end_time, format, stage_name, status, winner_id, tournament)
VALUES 
    ('20000000-0000-0000-0000-000000000017', CURRENT_TIMESTAMP, '2024-08-01', '2024-08-10', 'SINGLE_ELIMINATION', 'Round of 64', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000005'),
    ('20000000-0000-0000-0000-000000000018', CURRENT_TIMESTAMP, '2024-08-11', '2024-08-20', 'SINGLE_ELIMINATION', 'Quarterfinals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000005'),
    ('20000000-0000-0000-0000-000000000019', CURRENT_TIMESTAMP, '2024-08-21', '2024-08-30', 'SINGLE_ELIMINATION', 'Finals', 'SCHEDULED', NULL, '10000000-0000-0000-0000-000000000005');


-- Changing the player base to the tournaemnt

DO $$
DECLARE
    highest_uuid UUID;
BEGIN
    -- Retrieve and store the highest UUID in the variable
    SELECT id INTO highest_uuid
    FROM tournaments
    ORDER BY id DESC
    LIMIT 1;

     INSERT INTO tournament_players (profile_id, tournament_id) VALUES
        ('11111111-1111-1111-1111-111111111111', highest_uuid), -- John Doe
        ('11111111-1111-1111-1111-111111111112', highest_uuid), -- Jane Smith
        ('11111111-1111-1111-1111-111111111113', highest_uuid), -- Emily Johnson
        ('11111111-1111-1111-1111-111111111114', highest_uuid), -- Michael Brown
        ('11111111-1111-1111-1111-111111111115', highest_uuid), -- Sarah Davis
        ('11111111-1111-1111-1111-111111111116', highest_uuid); -- James Miller
END $$;
