# Java Saper z Logowaniem i Bazą Danych

## Opis

Java Saper to klasyczna gra w sapera z dodatkową funkcjonalnością logowania i rejestracji użytkowników oraz zapisywania wyników każdej gry w bazie danych. Gra została napisana w języku Java, wykorzystując Swing do graficznego interfejsu użytkownika. Użytkownicy mogą zarejestrować się, zalogować, a następnie grać w sapera, zapisując swoje wyniki w bazie danych. Mogą oni także zmieniać ustawienia gry, które również przetrzymywane są w bazie danych.

## Funkcje

- Logowanie i rejestracja użytkowników.
- Zapisywanie wyników gry w bazie danych.
- Graficzny interfejs użytkownika do gry w sapera.
- Modyfikacja ustawień użytkownika (liczba bomb, rozmiar planszy, rozmiar ekranu).

## Technologie

- Java
- Swing dla GUI
- JDBC dla połączenia z bazą danych
- SQL do zarządzania bazą danych

## Wymagania

- Java JDK 1.8 lub nowszy
- Serwer baz danych (np. MySQL, PostgreSQL)

## Instalacja

1. Sklonuj repozytorium do swojego środowiska lokalnego.
2. git clone [link do repozytorium]
2. Utwórz bazę danych i tabelę zgodnie z załączonym skryptem SQL.
3. Zaktualizuj plik konfiguracyjny bazy danych w projekcie, aby odzwierciedlić Twoje ustawienia (np. nazwę użytkownika, hasło, URL bazy danych).

## Uruchamianie

1. Otwórz projekt w swoim środowisku IDE (np. IntelliJ IDEA, Eclipse).
2. Uruchom główną klasę aplikacji.

## Jak grać

- Po uruchomieniu gry, zarejestruj się lub zaloguj.
- Rozpocznij grę, poprzednio wybierając liczbę bomb oraz rozmiary planszy w ustawieniach.
- Klikaj na pola, aby odsłonić, co kryją.
- Twoje wyniki będą automatycznie zapisywane w bazie danych.


