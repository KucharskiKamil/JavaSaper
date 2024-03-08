package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.*;

public class Watek extends Thread {
    private Socket socket;

    public Watek(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println("Klient: " + socket.getInetAddress().getHostAddress() + " nadesłał wiadomość.");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject zap = new JSONObject(lina);
            int typWiadomosci = zap.optInt("messageType");
            if (typWiadomosci == 1)
            {
                System.out.println("Odebrano żądanie login!");
                JSONObject odpowiedz = new JSONObject();
                Connection connection = DatabaseConnection.getInstance().getConnection();

                String username = zap.getString("username");
                String password = zap.getString("password");

                try {
                    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, username);
                    statement.setString(2, password);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next())
                    {
                        // Użytkownik o podanym username i haśle istnieje
                        int userId = resultSet.getInt("id");
                        String email=resultSet.getString("email");
                        odpowiedz.put("messageType", 4);
                        odpowiedz.put("message", "Zalogowano użytkownika");
                        odpowiedz.put("username", username);
                        odpowiedz.put("email", email);
                        odpowiedz.put("userId", userId);
                    } else
                    {
                        // Użytkownik o podanym username i haśle nie istnieje
                        odpowiedz.put("messageType", 3);
                        odpowiedz.put("message", "Brak takiego użytkownika w bazie danych!");
                    }
                } catch (SQLException e)
                {
                    // Błąd podczas wykonania zapytania
                    odpowiedz.put("messageType", 3);
                    odpowiedz.put("message", "Wystąpił błąd podczas sprawdzania użytkownika w bazie danych");
                    e.printStackTrace();
                }

                // Wysyłamy odpowiedź
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();
            }
            else if (typWiadomosci == 2)// Rejestracja
            {
                System.out.println("Odebrano żądanie rejestracji!");
                String username = zap.getString("username");
                String email = zap.getString("email");
                String haslo = zap.getString("password");
                JSONObject odpowiedz = new JSONObject();
                // Wykonaj operacje na bazie danych za pomocą getConnection()
                Connection connection = DatabaseConnection.getInstance().getConnection();

                try {
                    // Sprawdź, czy użytkownik o podanym username lub email już istnieje w tabeli
                    String checkQuery = "SELECT * FROM users WHERE username = ? OR email = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setString(1, username);
                    checkStatement.setString(2, email);
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (resultSet.next()) {
                        // Użytkownik o podanym username lub email już istnieje
                        if (resultSet.getString("username").equals(username)) {
                            //3 oznacza error
                            odpowiedz.put("messageType", 3);
                            odpowiedz.put("message", "Taki username już jest zarejestrowany!");
                        } else if (resultSet.getString("email").equals(email)) {
                            odpowiedz.put("messageType", 3);
                            odpowiedz.put("message", "Taki email już jest zarejestrowany!");
                        }
                    } else
                    {
                        try {
                            // Dodaj rekord do tabeli users
                            String insertQuery = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
                            PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                            insertStatement.setString(1, email);
                            insertStatement.setString(2, username);
                            insertStatement.setString(3, haslo);
                            insertStatement.executeUpdate();

                            // Pobierz wygenerowane identyfikatory (id) nowo dodanego użytkownika
                            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                int userId = generatedKeys.getInt(1);

                                // Dodaj rekord do tabeli settings dla nowo dodanego użytkownika
                                String settingsQuery = "INSERT INTO settings (user_id, window_width, window_height, bombs_amount, board_size) VALUES (?, 800, 800, 10, 10)";
                                PreparedStatement settingsStatement = connection.prepareStatement(settingsQuery, Statement.RETURN_GENERATED_KEYS);
                                settingsStatement.setInt(1, userId);
                                settingsStatement.executeUpdate();

                                // Pobierz wygenerowane identyfikatory (id) nowo dodanych settings
                                ResultSet settingsGeneratedKeys = settingsStatement.getGeneratedKeys();
                                if (settingsGeneratedKeys.next()) {
                                    int settingsId = settingsGeneratedKeys.getInt(1);

                                    // Ustaw wartość "current_settings_id" dla nowo dodanego użytkownika
                                    String updateUserQuery = "UPDATE users SET current_settings_id = ? WHERE id = ?";
                                    PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery);
                                    updateUserStatement.setInt(1, settingsId);
                                    updateUserStatement.setInt(2, userId);
                                    updateUserStatement.executeUpdate();

                                    // Dodaj wiersz do tabeli statistics dla nowo dodanego użytkownika
                                    String statisticsQuery = "INSERT INTO statistics (id_user) VALUES (?)";
                                    PreparedStatement statisticsStatement = connection.prepareStatement(statisticsQuery);
                                    statisticsStatement.setInt(1, userId);
                                    statisticsStatement.executeUpdate();
                                }
                            }

                            //4 oznacza wiadomość pomyślną
                            odpowiedz.put("messageType", 4);
                            odpowiedz.put("message", "Użytkownik został zarejestrowany!");
                        } catch (SQLException e) {
                            //Error
                            odpowiedz.put("messageType", 3);
                            odpowiedz.put("message", "Wystąpił błąd podczas dodawania rekordu do bazy danych");
                            e.printStackTrace();
                        }






                    }

                } catch (SQLException e) {
                    //Error
                    odpowiedz.put("messageType", 3);
                    odpowiedz.put("message", "Wystąpił błąd podczas dodawania rekordu do bazy danych");
                    e.printStackTrace();
                }
                //Wysylamy odpowiedz
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();
            }else if(typWiadomosci==5)//Prosba o dane do tabeli
            {
                System.out.println("Odebrano żądanie danych z tabeli!");
                JSONObject odpowiedz = new JSONObject();
                Connection connection = DatabaseConnection.getInstance().getConnection();

                try {
                    String query = "SELECT s.games_played, s.best_score, s.best_time, u.username FROM statistics s JOIN users u ON s.id_user = u.id ORDER BY games_played DESC;";
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    JSONArray jsonArray = new JSONArray(); // Tablica JSON dla przechowywania wierszy

                    while (resultSet.next())
                    {
                        JSONObject jsonObject = new JSONObject();

                        // Pobieranie wartości z ResultSet i dodawanie ich do obiektu JSON
                        jsonObject.put("games_played", resultSet.getInt("games_played"));
                        jsonObject.put("best_score", resultSet.getInt("best_score"));
                        jsonObject.put("best_time", resultSet.getInt("best_time"));
                        jsonObject.put("username", resultSet.getString("username"));

                        // Dodawanie obiektu JSON do tablicy
                        jsonArray.put(jsonObject);
                    }
                    if (jsonArray.length() > 0)
                    {
                        odpowiedz.put("messageType", 4); // Sukces, przynajmniej 1 wiersz wiec wysylamy
                        odpowiedz.put("data", jsonArray); // Dodaj tablicę JSON z danymi do odpowiedzi
                    } else {
                        odpowiedz.put("messageType", 3); // Error przy wybieraniu wierszy (brak)
                        odpowiedz.put("message", "Brak danych w tabeli statistics!");
                    }
                } catch (SQLException e) {
                    odpowiedz.put("messageType", 3);
                    odpowiedz.put("message", "Wystąpił błąd podczas pobierania danych z tabeli statistics");
                    e.printStackTrace();
                }
                // Wysyłamy odpowiedź w formacie JSON
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();

            }
            else if(typWiadomosci==6)//Prosba o statystyki
            {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                int userIdOdebrany = zap.optInt("userId");
                String query = "SELECT * FROM settings WHERE user_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userIdOdebrany);
                ResultSet resultSet = statement.executeQuery();

                JSONArray rows = new JSONArray();

                while (resultSet.next()) {
                    JSONObject row = new JSONObject();
                    row.put(("row_id"),resultSet.getInt("id"));
                    row.put("window_width", resultSet.getInt("window_width"));
                    row.put("window_height", resultSet.getInt("window_height"));
                    row.put("bombs_amount", resultSet.getInt("bombs_amount"));
                    row.put("board_size", resultSet.getInt("board_size"));
                    rows.put(row);
                }

                JSONObject odpowiedz = new JSONObject();

                if (rows.length() > 0) {
                    odpowiedz.put("messageType", 4); // Sukces!
                    odpowiedz.put("data", rows);
                } else {
                    odpowiedz.put("messageType", 3); // Błąd
                    odpowiedz.put("message", "Brak takiego użytkownika w tabeli settings!");
                }

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();
            }
            else if(typWiadomosci==7)// Zmiana ustawien dla gracza
            {


                int userId = zap.getInt("userId");
                System.out.println("Doszła prośba o edycje ustawień gracza o id: "+userId);
                int windowWidth = zap.getInt("windowWidth");
                int windowHeight = zap.getInt("windowHeight");
                int bombsAmount = zap.getInt("bombsAmount");
                int boardSize = zap.getInt("boardSize");
                int rowId = zap.getInt("rowId");
                //Otrzymujemy tutaj dane, super!
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String sqlQuery = "UPDATE settings " +
                        "SET window_width = ?, " +
                        "    window_height = ?, " +
                        "    bombs_amount = ?, " +
                        "    board_size = ? " +
                        "WHERE id=?";

                try (PreparedStatement updateStatement = connection.prepareStatement(sqlQuery)) {
                    updateStatement.setInt(1, windowWidth);
                    updateStatement.setInt(2, windowHeight);
                    updateStatement.setInt(3, bombsAmount);
                    updateStatement.setInt(4, boardSize);
                    updateStatement.setInt(5, userId);
                    updateStatement.setInt(5, rowId);

                    int rowsAffected = updateStatement.executeUpdate();
                    System.out.println("Zaktualizowano " + rowsAffected + " wierszy.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else if (typWiadomosci==8)
            {
                int userId = zap.getInt("userId");
                int width = zap.getInt("width");
                int height = zap.getInt("height");
                int amount = zap.getInt("amount");
                int size = zap.getInt("size");

                JSONObject odpowiedz = new JSONObject();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                try {
                    Connection connection = DatabaseConnection.getInstance().getConnection();
                    String sqlQuery = "INSERT INTO settings (user_id, window_width, window_height, bombs_amount, board_size) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(sqlQuery);
                    insertStatement.setInt(1, userId);
                    insertStatement.setInt(2, width);
                    insertStatement.setInt(3, height);
                    insertStatement.setInt(4, amount);
                    insertStatement.setInt(5, size);
                    int rowsAffected = insertStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        odpowiedz.put("messageType", 4); // Sukces
                        odpowiedz.put("message", "Pomyślnie dodano wiersz do tabeli!");
                    } else {
                        odpowiedz.put("messageType", 3); // Błąd
                        odpowiedz.put("message", "Błąd w dodawaniu wiersza");
                    }
                } catch (SQLException e) {
                    odpowiedz.put("messageType", 3); // Błąd
                    odpowiedz.put("message", "Wystąpił błąd podczas dodawania rekordu do bazy danych");
                    e.printStackTrace();
                }

                try {
                    bw.write(odpowiedz.toString());
                    bw.newLine();
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(typWiadomosci==9)// Zmiana aktualnych ustawien, czyli wybranie ktorys z choiceBox w settings
            {
                int settingsId = zap.getInt("settingsId");
                int userId = zap.getInt("userId");

                Connection connection = DatabaseConnection.getInstance().getConnection();
                String updateUserQuery = "UPDATE users SET current_settings_id = ? WHERE id = ?";
                PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery);

                updateUserStatement.setInt(1, settingsId);
                updateUserStatement.setInt(2, userId);
                int rowsAffected = updateUserStatement.executeUpdate();
                JSONObject odpowiedz = new JSONObject();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (rowsAffected > 0)
                {
                    //Wykonaj to polecenie.
                    String selectUserData = "SELECT * FROM settings WHERE id = ?;";
                    PreparedStatement selectUserStatement = connection.prepareStatement(selectUserData);
                    selectUserStatement.setInt(1, settingsId);
                    ResultSet resultSet = selectUserStatement.executeQuery();
                    if (resultSet.next())
                    {
                        odpowiedz.put("messageType", 4); // Sukces
                        odpowiedz.put("message", "Pomyślnie zmieniono ustawienia!");
                        odpowiedz.put("newWidth", resultSet.getInt("window_width"));
                        odpowiedz.put("newHeight", resultSet.getInt("window_height"));
                        odpowiedz.put("bombsAmount", resultSet.getInt("bombs_amount"));
                        odpowiedz.put("boardSize", resultSet.getInt("board_size"));
                    }
                    else
                    {
                        odpowiedz.put("messageType", 3); // Blad
                        odpowiedz.put("message", "Błąd w sprawdzaniu danych aktualnie wybranego ustawienia!");
                    }
                } else
                {
                    odpowiedz.put("messageType", 3); // Blad
                    odpowiedz.put("message", "Błąd w zmienianiu ustawień!");
                }
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();
            }else if (typWiadomosci==10)//Prosba o aktualnie wybrane statystyki
            {
                int userId = zap.getInt("userId");
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String selectUserQuery = "SELECT current_settings_id FROM users WHERE id = ?";
                PreparedStatement selectUserStatement = connection.prepareStatement(selectUserQuery);

                selectUserStatement.setInt(1, userId);
                ResultSet resultSet = selectUserStatement.executeQuery();

                JSONObject odpowiedz = new JSONObject();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (resultSet.next())
                {
                    int currentSettingsId = resultSet.getInt("current_settings_id");

                    String anotherSelect = "SELECT * FROM settings WHERE id = ?;";
                    PreparedStatement selectUserStatement2 = connection.prepareStatement(anotherSelect);
                    selectUserStatement2.setInt(1, currentSettingsId);
                    ResultSet resultSet2 = selectUserStatement2.executeQuery();
                    if(resultSet2.next())
                    {
                        odpowiedz.put("messageType", 4); // Sukces
                        odpowiedz.put("current_settings_id", currentSettingsId);
                        odpowiedz.put("windowWidth", resultSet2.getInt("window_width"));
                        odpowiedz.put("windowHeight", resultSet2.getInt("window_height"));
                        odpowiedz.put("bombs", resultSet2.getInt("bombs_amount"));
                        odpowiedz.put("board", resultSet2.getInt("board_size"));
                    }
                    else
                    {
                        odpowiedz.put("messageType", 3); // Blad
                        odpowiedz.put("message", "Błąd w sprawdzaniu aktualnie wybranego ustawienia!");
                    }
                } else {
                    odpowiedz.put("messageType", 3); // Blad
                    odpowiedz.put("message", "Błąd w sprawdzaniu aktualnie wybranego ustawienia!");
                }

                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();

            }else if(typWiadomosci==11)// Prosba o usuniecie ustawien uzytkownika
            {
                int userId = zap.getInt("userId");
                int settingsId = zap.getInt("settingsId");
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String selectUserQuery = "SELECT current_settings_id FROM users WHERE id = ?";

                PreparedStatement selectUserStatement = connection.prepareStatement(selectUserQuery);
                selectUserStatement.setInt(1, userId);
                ResultSet resultSet = selectUserStatement.executeQuery();
                JSONObject odpowiedz = new JSONObject();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (resultSet.next())// Jesli sa ustawienia
                {
                    int currentSettingsId = resultSet.getInt("current_settings_id");
                    if(settingsId==currentSettingsId)
                    {
                        //wysylamy odpowiedz, ze to sa aktualnie wybrane ustawienia i nie mozna
                        odpowiedz.put("messageType", 3); // Blad
                        odpowiedz.put("message", "Ustawienia które aktualnie próujesz usunąć są ustawione jako wybrane!");
                    }
                    else
                    {
                        //usuwamy
                        String query = "DELETE FROM settings WHERE id = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setInt(1, settingsId);
                        int rowsAffected = statement.executeUpdate();
                        if (rowsAffected > 0)
                        {
                            odpowiedz.put("messageType", 4); // Sukces
                            odpowiedz.put("message", "Usunieto ustawienie!");
                        } else
                        {
                            odpowiedz.put("messageType", 3); // Blad
                            odpowiedz.put("message", "Nie znaleziono takiego uzytkownika!");
                        }
                    }
                }
                else
                {
                    odpowiedz.put("messageType", 3); // Blad
                    odpowiedz.put("message", "Blad, nie ma takiego uzytkownika!");
                }
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();

            }else if (typWiadomosci==12)
            {
                int userId = zap.getInt("userId");
                Connection connection = DatabaseConnection.getInstance().getConnection();

                String selectUserQuery = "SELECT current_settings_id FROM users WHERE id = ?";
                PreparedStatement selectUserStatement = connection.prepareStatement(selectUserQuery);
                selectUserStatement.setInt(1, userId);
                ResultSet resultSet = selectUserStatement.executeQuery();
                JSONObject odpowiedz = new JSONObject();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (resultSet.next())// Jesli sa ustawienia
                {
                    int currentSettingsId = resultSet.getInt("current_settings_id");
                    String select2 = "SELECT * FROM settings WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(select2);
                    statement.setInt(1, currentSettingsId);

                    ResultSet resultSet2 = statement.executeQuery();
                    if (resultSet2.next())//JESLI ZWROCILO JAKIS WIERSZ
                    {
                        odpowiedz.put("messageType", 4); // Sukces
                        odpowiedz.put("windowHeight", resultSet2.getInt("window_height"));
                        odpowiedz.put("windowWidth", resultSet2.getInt("window_width"));
                        odpowiedz.put("boardSize", resultSet2.getInt("board_size"));
                        odpowiedz.put("bombsAmount", resultSet2.getInt("bombs_amount"));
                    } else //JESLI NIE ZWROCILO WIERSZA
                    {
                        odpowiedz.put("messageType", 3); // Blad
                        odpowiedz.put("message", "Nie znaleziono takiego uzytkownika!");
                    }
                }
                else
                {
                    odpowiedz.put("messageType", 3); // Blad
                    odpowiedz.put("message", "Blad, nie ma takiego uzytkownika!");
                }
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();
            }
            else if(typWiadomosci==13)// Otrzymujemy dane po zakonczeniu gry
            {
                int userId = zap.getInt("userId");
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String selectUserQuery = "SELECT * FROM statistics WHERE id_user = ?";
                PreparedStatement selectUserStatement = connection.prepareStatement(selectUserQuery);
                selectUserStatement.setInt(1, userId);
                ResultSet resultSet = selectUserStatement.executeQuery();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                JSONObject odpowiedz = new JSONObject();

                if (resultSet.next())// Jesli sa statystyki
                {
                    int bestTime=resultSet.getInt("best_time");
                    if(bestTime==0)
                    {
                        bestTime=999999999;
                    }
                    odpowiedz.put("messageType", 4); // Sukces
                    odpowiedz.put("gamesPlayed", resultSet.getInt("games_played"));
                    odpowiedz.put("gamesWon", resultSet.getInt("games_won"));
                    odpowiedz.put("gamesLost", resultSet.getInt("games_lost"));
                    odpowiedz.put("averagePlayTime", resultSet.getFloat("average_play_time"));
                    odpowiedz.put("bestScore", resultSet.getInt("best_score"));

                    if (resultSet.getString("last_game_date") == null) {
                        odpowiedz.put("lastGameDate", "");
                    } else {
                        odpowiedz.put("lastGameDate", resultSet.getString("last_game_date"));
                    }
                    odpowiedz.put("lastScore", resultSet.getInt("last_score"));
                    odpowiedz.put("bestTime", bestTime);
                    odpowiedz.put("lastTime", resultSet.getInt("last_time"));
                }
                else
                {
                    odpowiedz.put("messageType", 3); // Problem ze znalezieniem statystyk
                    odpowiedz.put("message","Problem ze znalezieniem statystyk danego użytkownika!");
                }
                bw.write(odpowiedz.toString());
                bw.newLine();
                bw.flush();
                //Teraz oczekujemy na odpowiedz klienta z nowymi danymi do wstawienia



                BufferedReader br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String lina2 = br2.readLine();
                JSONObject odpowiedz2 = new JSONObject(lina2);
                int gamesPlayed = odpowiedz2.getInt("gamesPlayed");
                int userId2 = odpowiedz2.getInt("userId");
                int gamesWon = odpowiedz2.getInt("gamesWon");
                int gamesLost = odpowiedz2.getInt("gamesLost");
                float averagePlayTime = odpowiedz2.getFloat("averagePlayTime");
                int bestScore = odpowiedz2.getInt("bestScore");
                String lastGameDate = odpowiedz2.getString("lastGameDate");
                int lastScore = odpowiedz2.getInt("lastScore");
                int bestTime2 = odpowiedz2.getInt("bestTime");
                int lastTime = odpowiedz2.getInt("lastTime");

                String updateSettingsQuery = "UPDATE statistics SET games_played = ?, games_won = ?, games_lost = ?, average_play_time = ?, best_score = ?, last_game_date = ?, last_score = ?, best_time = ?, last_time = ? WHERE id_user = ?";
                PreparedStatement updateSettingsStatement = connection.prepareStatement(updateSettingsQuery);
                updateSettingsStatement.setInt(1, gamesPlayed);
                updateSettingsStatement.setInt(2, gamesWon);
                updateSettingsStatement.setInt(3, gamesLost);
                updateSettingsStatement.setFloat(4, averagePlayTime);
                updateSettingsStatement.setInt(5, bestScore);
                updateSettingsStatement.setString(6, lastGameDate);
                updateSettingsStatement.setInt(7, lastScore);
                updateSettingsStatement.setInt(8, bestTime2);
                updateSettingsStatement.setInt(9, lastTime);
                updateSettingsStatement.setInt(10, userId2);
                updateSettingsStatement.executeUpdate();
            }
            else
            {
                System.out.println("Otrzymaliśmy wiadomość ale nie jest ona żadnym ze znanych mi typów!");
            }
            socket.close();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
