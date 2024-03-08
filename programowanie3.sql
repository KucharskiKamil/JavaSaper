-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Czas generowania: 13 Cze 2023, 18:41
-- Wersja serwera: 10.4.27-MariaDB
-- Wersja PHP: 8.0.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `programowanie3`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `settings`
--

CREATE TABLE `settings` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `window_width` int(11) DEFAULT NULL,
  `window_height` int(11) DEFAULT NULL,
  `bombs_amount` int(11) DEFAULT NULL,
  `board_size` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `settings`
--

INSERT INTO `settings` (`id`, `user_id`, `window_width`, `window_height`, `bombs_amount`, `board_size`) VALUES
(1, 1, 2000, 5, 4, 3),
(2, 22, 800, 800, 10, 10),
(3, 23, 800, 800, 10, 10);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `statistics`
--

CREATE TABLE `statistics` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `games_played` int(11) DEFAULT NULL,
  `games_won` int(11) DEFAULT NULL,
  `games_lost` int(11) DEFAULT NULL,
  `average_play_time` float DEFAULT NULL,
  `best_score` int(11) DEFAULT NULL,
  `last_game_date` date DEFAULT NULL,
  `last_score` int(11) DEFAULT NULL,
  `best_time` int(11) DEFAULT NULL,
  `last_time` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `statistics`
--

INSERT INTO `statistics` (`id`, `id_user`, `games_played`, `games_won`, `games_lost`, `average_play_time`, `best_score`, `last_game_date`, `last_score`, `best_time`, `last_time`) VALUES
(1, 1, 1, 1, 0, 3600, 2137, '2023-06-08', 3600, 12345, 231451),
(2, 2, 2137, 1666, 471, 1234, 4321, '2023-06-09', 1010, 2111, 5412),
(3, 3, 5, 3, 2, 1800, 1500, '2023-06-07', 1200, 800, 900),
(4, 4, 10, 8, 2, 2000, 2500, '2023-06-09', 2300, 1500, 1800),
(5, 5, 2, 0, 2, 600, 400, '2023-06-05', 350, 400, 600),
(6, 6, 7, 5, 2, 1500, 1800, '2023-06-08', 1600, 1200, 1400),
(7, 7, 3, 1, 2, 900, 600, '2023-06-06', 550, 700, 800),
(8, 8, 4, 4, 0, 1200, 800, '2023-06-07', 700, 1000, 1100),
(9, 9, 6, 2, 4, 1600, 1200, '2023-06-08', 1100, 1500, 1600),
(10, 10, 8, 6, 2, 1900, 2200, '2023-06-09', 2100, 1800, 2000),
(11, 11, 3, 2, 1, 1000, 700, '2023-06-06', 650, 900, 1000),
(12, 12, 9, 8, 1, 2200, 2800, '2023-06-09', 2600, 2000, 2200),
(13, 13, 4, 1, 3, 1100, 900, '2023-06-07', 800, 1000, 1200),
(14, 14, 5, 3, 2, 1300, 1500, '2023-06-08', 1400, 1200, 1300),
(15, 15, 2, 2, 0, 800, 600, '2023-06-06', 550, 700, 800),
(16, 16, 7, 5, 2, 1700, 2000, '2023-06-08', 1800, 1600, 1700),
(17, 17, 3, 0, 3, 1000, 400, '2023-06-06', 350, 600, 900),
(18, 18, 6, 4, 2, 1500, 1800, '2023-06-08', 1600, 1300, 1400),
(19, 19, 8, 7, 1, 2000, 2400, '2023-06-09', 2200, 1900, 2100),
(20, 20, 4, 3, 1, 1200, 900, '2023-06-07', 800, 1100, 1200);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `current_settings_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `email`, `current_settings_id`) VALUES
(1, 'kamilson', 'kam22', 'kamilek@o2.pl', 1),
(2, 'edison', 'thomas', 'edison@o2.pl', 1),
(3, 'johnsmith', 'password123', 'johnsmith@example.com', NULL),
(4, 'maryjones', 'mary123', 'maryjones@example.com', NULL),
(5, 'davidbrown', 'david456', 'davidbrown@example.com', NULL),
(6, 'sarahwilson', 'sarah789', 'sarahwilson@example.com', NULL),
(7, 'michaelthomas', 'michael321', 'michaelthomas@example.com', NULL),
(8, 'jenniferlee', 'jennifer123', 'jenniferlee@example.com', NULL),
(9, 'robertmartin', 'robert456', 'robertmartin@example.com', NULL),
(10, 'laurasmith', 'laura789', 'laurasmith@example.com', NULL),
(11, 'williamjones', 'william123', 'williamjones@example.com', NULL),
(12, 'ameliaclark', 'amelia456', 'ameliaclark@example.com', NULL),
(13, 'charlesbrown', 'charles789', 'charlesbrown@example.com', NULL),
(14, 'emilywilson', 'emily123', 'emilywilson@example.com', NULL),
(15, 'josephharris', 'joseph456', 'josephharris@example.com', NULL),
(16, 'oliviamartin', 'olivia789', 'oliviamartin@example.com', NULL),
(17, 'danielthompson', 'daniel123', 'danielthompson@example.com', NULL),
(18, 'sophiawalker', 'sophia456', 'sophiawalker@example.com', NULL),
(19, 'alexanderdavis', 'alexander789', 'alexanderdavis@example.com', NULL),
(20, 'madisonharris', 'madison123', 'madisonharris@example.com', NULL),
(21, 'kamilkuch', '123', 'kamilkuch@o2.pl', NULL),
(22, 'kam', 'kam', 'kam@o2.pl', NULL),
(23, 'ola', 'ola', 'ola@o2.pl', 3);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeksy dla tabeli `statistics`
--
ALTER TABLE `statistics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_users_settings` (`current_settings_id`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `settings`
--
ALTER TABLE `settings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `statistics`
--
ALTER TABLE `statistics`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT dla tabeli `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `settings`
--
ALTER TABLE `settings`
  ADD CONSTRAINT `settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Ograniczenia dla tabeli `statistics`
--
ALTER TABLE `statistics`
  ADD CONSTRAINT `statistics_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
