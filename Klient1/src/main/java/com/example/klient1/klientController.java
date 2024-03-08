package com.example.klient1;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class klientController
{

    private int loggedUserID;
    int loggedBombsAmount,loggedWindowWidth,loggedWindowHeight,loggedBoardSize,loggedSelectedSettings;
    String loggedUserUsername,loggedUserEmail;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TableView<ObservableList<String>> tableScoresDisplay;
    @FXML
    private TableColumn<ObservableList<String>, String> usernameColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> gamesPlayedColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> bestScoreColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> bestTimeColumn;

    private ObservableList<ObservableList<String>> dataTabela;
    //tabela ustawien

    @FXML
    private TableView<ObservableList<Integer>> tabelaUstawien;
    @FXML
    private TableColumn<ObservableList<Integer>, Integer> widthColumn;

    @FXML
    private TableColumn<ObservableList<Integer>, Integer> heightColumn;

    @FXML
    private TableColumn<ObservableList<Integer>, Integer> bombsColumn;

    @FXML
    private TableColumn<ObservableList<Integer>, Integer> boardColumn;
    @FXML
    private TableColumn<ObservableList<Integer>, Integer> rowIdColumn;

    private ObservableList<ObservableList<Integer>> dataTabelaUstawien;

    //Koniec tabeli ustawien


    @FXML
    private TextField usernameLoginField,usernameRegisterField,emailRegisterField,addSettingsWidth,addSettingsHeight,addSettingsAmount,addSettingsSize;

    @FXML
    private PasswordField passwordLoginField,passwordRegisterField;

    @FXML
    private VBox widok1,widok2,mainMenuVBox,loginAndRegisterVBox,tableScoreBody,settingsBody,playBodyVBox;
    @FXML
    private Label resultRegisterDisplay,resultLoginDisplay;

    public void initialize()
    {
        // Inicjalizacja kolumn tabeli
        usernameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(0)));
        gamesPlayedColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(1)));
        bestScoreColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(2)));
        bestTimeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(3)));


        // Inicjalizacja danych tabeli
        dataTabela = FXCollections.observableArrayList();
        tableScoresDisplay.setItems(dataTabela);

        // Inicjalizacja kolumn tabeli
        widthColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(0)));
        widthColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        widthColumn.setEditable(true);

        heightColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(1)));
        heightColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        heightColumn.setEditable(true);

        bombsColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(2)));
        bombsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bombsColumn.setEditable(true);

        boardColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(3)));
        boardColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        boardColumn.setEditable(true);

        rowIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(4)));
        rowIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        rowIdColumn.setEditable(false);


        // Inicjalizacja danych tabeli
        dataTabelaUstawien = FXCollections.observableArrayList();
        tabelaUstawien.setItems(dataTabelaUstawien);
        //Lambda
        widthColumn.setOnEditCommit(event -> {
            onEditCommitWidthCell(event);
        });

        heightColumn.setOnEditCommit(event -> {
            onEditCommitHeightCell(event);
        });

        bombsColumn.setOnEditCommit(event -> {
            onEditCommitBombsCell(event);
        });

        boardColumn.setOnEditCommit(event -> {
            onEditCommitBoardCell(event);
        });
    }
    private void zmienSzerokoscOkna(int width, int height) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    private void changeSettings()
    {
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 7); // 7 oznacza żądanie otrzymania ustawien
            dane.put("userId", loggedUserID);

            // Pobierz indeks wiersza, który został edytowany
            int editedRowIndex = tabelaUstawien.getSelectionModel().getSelectedIndex();

            // Sprawdź, czy wybrano wiersz do edycji
            if (editedRowIndex >= 0) {
                ObservableList<Integer> editedRowData = tabelaUstawien.getItems().get(editedRowIndex);

                // Pobierz dane z edytowanego wiersza
                int windowWidth = editedRowData.get(0);
                int windowHeight = editedRowData.get(1);
                int bombsAmount = editedRowData.get(2);
                int boardSize = editedRowData.get(3);
                int rowId = editedRowData.get(4);
                dane.put("windowWidth", windowWidth);
                dane.put("windowHeight", windowHeight);
                dane.put("bombsAmount", bombsAmount);
                dane.put("boardSize", boardSize);
                dane.put("rowId", rowId);
                if(rowId==loggedSelectedSettings)
                {
                    loggedWindowWidth=windowWidth;
                    loggedWindowHeight=windowHeight;
                    if(windowWidth<400 && windowHeight<400)
                    {
                        zmienSzerokoscOkna(400,400);
                    }
                    else if(windowWidth>=400 && windowHeight<400)
                    {
                        zmienSzerokoscOkna(windowWidth,400);
                    }
                    //Mozliwa poprawka else if(windowWidth<400 && windowHeight >=400)
                    else if(windowWidth<400)
                    {
                        zmienSzerokoscOkna(400,windowHeight);
                    }
                    else
                    {
                        zmienSzerokoscOkna(windowWidth,windowHeight);
                    }
                }
            }
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Prośba o zmianę ustawień wysłana.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onEditCommitWidthCell(TableColumn.CellEditEvent<ObservableList<Integer>, Integer> event) {
        int rowIndex = event.getTablePosition().getRow();
        int newValue = event.getNewValue();
        if(newValue>=400 && newValue<=2000)
        {
            tabelaUstawien.getItems().get(rowIndex).set(0, newValue);
            changeSettings();
        }
        else
        {
            enterSettingsBody();
        }
    }


    @FXML
    private void onEditCommitHeightCell(TableColumn.CellEditEvent<ObservableList<Integer>, Integer> event) {
        int rowIndex = event.getTablePosition().getRow();
        int newValue = event.getNewValue();
        if(newValue>=400 && newValue<=2000)
        {
            tabelaUstawien.getItems().get(rowIndex).set(1, newValue);
            changeSettings();
        }
        else
        {
            enterSettingsBody();
        }
    }

    @FXML
    private void onEditCommitBombsCell(TableColumn.CellEditEvent<ObservableList<Integer>, Integer> event) {
        int rowIndex = event.getTablePosition().getRow();
        int newValue = event.getNewValue();
        if(newValue>=1 && newValue<=70)
        {
            tabelaUstawien.getItems().get(rowIndex).set(2, newValue);
            changeSettings();
        }
        else
        {
            enterSettingsBody();
        }
    }

    @FXML
    private void onEditCommitBoardCell(TableColumn.CellEditEvent<ObservableList<Integer>, Integer> event) {
        int rowIndex = event.getTablePosition().getRow();
        int newValue = event.getNewValue();
        if(newValue>=2 && newValue<=10)
        {
            tabelaUstawien.getItems().get(rowIndex).set(3, newValue);
            changeSettings();
        }
        else
        {
            enterSettingsBody();
        }
    }

    public void addRow(String username, String gamesPlayed, String bestScore, String bestTime) {
        ObservableList<String> rowData = FXCollections.observableArrayList();
        rowData.add(username);
        rowData.add(gamesPlayed);
        rowData.add(bestScore);
        rowData.add(bestTime);
        dataTabela.add(rowData);
    }
    public void addRowSettings(Integer width, Integer height, Integer bombs, Integer board,Integer rowId) {
        ObservableList<Integer> rowData = FXCollections.observableArrayList();
        rowData.add(width);
        rowData.add(height);
        rowData.add(bombs);
        rowData.add(board);
        rowData.add(rowId);
        dataTabelaUstawien.add(rowData);
    }

    @FXML
    private void onLogoutButtonClick()
    {
        loginAndRegisterVBox.setVisible(true);
        widok1.setVisible(true);
        mainMenuVBox.setVisible(false);
        usernameLoginField.setText("");
        passwordLoginField.setText("");
    }
    @FXML
    private GridPane gridPanelDoGry;

    @FXML
    private Label playBodyBombsLabel,gamePlayLabelError;
    private int[][] gameBoard;
    @FXML
            public Label gameTimeLabel,gameScoreLabel;
    private MyTimer timer;
    @FXML
    Button resetGameButton;
    @FXML
    private void enterPlayBody() throws IOException
    {
        clickedCells=0;
        resetGameButton.setVisible(false);
        gameTimeLabel.setText("Czas: 0");
        gameScoreLabel.setText("");
        gameScoreLabel.setTextFill(Color.GREEN);
        timer = new MyTimer(gameTimeLabel);
        timer.setGameTime(0);
        gamePlayLabelError.setText("");
        gridPanelDoGry.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        gridPanelDoGry.getChildren().clear(); // Wyczyść istniejące elementy w GridPane
//        loggedUserID=24;
        playBodyBombsLabel.setText("Bomby: ");
        playBodyVBox.setVisible(true);
        mainMenuVBox.setVisible(false);
        //Wysylamy prosbe o informacje dotyczace naszego usera
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 12); // 12 oznacza żądanie zwrócenia ustawien uzytkownika
            dane.put("userId", loggedUserID);
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Prośba o otrzymanie ustawień wysłana. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject odpowiedz = new JSONObject(lina);
            System.out.println("Otrzymano odpowiedź!");
            if (odpowiedz.getInt("messageType") == 4)// Sukces
            {
                loggedWindowHeight=odpowiedz.getInt("windowHeight");
                loggedWindowWidth=odpowiedz.getInt("windowWidth");
                loggedBoardSize=odpowiedz.getInt("boardSize");
                loggedBombsAmount=odpowiedz.getInt("bombsAmount");
                playBodyBombsLabel.setText("Bomby: "+loggedBombsAmount);

                //40 * liczba bomb + jakis zapas
                int bestWindowSize = loggedBoardSize * 50;
                if(loggedWindowWidth <= (bestWindowSize+100) && loggedWindowHeight <= (bestWindowSize+50))
                {
                    zmienSzerokoscOkna(bestWindowSize+100,bestWindowSize+50);
                }
                else if(loggedWindowWidth <= (bestWindowSize+100) && loggedWindowHeight > (bestWindowSize+50))
                {
                    zmienSzerokoscOkna(bestWindowSize+100,loggedWindowHeight);
                }
                else if(loggedWindowWidth > (bestWindowSize+100) && loggedWindowHeight <= (bestWindowSize+50))
                {
                    zmienSzerokoscOkna(loggedWindowWidth,bestWindowSize+50);
                }
                else
                {
                    zmienSzerokoscOkna(loggedWindowWidth,loggedWindowHeight);
                }
                if(loggedBoardSize*loggedBoardSize <= loggedBombsAmount)
                {
                    gamePlayLabelError.setText("Liczba bomb jest za duża na tą planszę! Zmień ustawienia!");
                    return;
                }
                gameBoard = new int[loggedBoardSize][loggedBoardSize];
                for(int i=0;i<loggedBoardSize;i++)
                {
                    for(int j=0;j<loggedBoardSize;j++)
                    {
                        gameBoard[i][j]=-1;
                    }
                }
                //Tutaj ustawiamy gridpanel
                for (int row = 0; row < loggedBoardSize; row++)
                {
                    for (int column = 0; column < loggedBoardSize; column++)
                    {
                        Button button = new Button("");
                        GridPane.setRowIndex(button, row);
                        GridPane.setColumnIndex(button, column);
                        gridPanelDoGry.getChildren().add(button);


                        // Dodanie klasy CSS
                        button.getStyleClass().add("game-button");

                        int tempRow = row;
                        int tempCol = column;
                        //Lambda
                        button.setOnAction(event ->
                        {
                            button.setVisible(false); // Ukrycie przycisku po kliknięciu
                            gameButtonClicked(tempRow,tempCol);
                        });
                    }
                }
                // Mamy tablice zatem losujemy dla niej pola pod warunkiem, ze liczba bomb jest mniejsza niz liczba pol!
            }
            if(odpowiedz.getInt("messageType") == 3)//    Blad zwrocony przez serwer
            {
                System.out.println(odpowiedz.getString("message"));
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
        // koniec
    }
    int clickedCells;
    private void endGame(int time,boolean didWin)
    {
        if(time<0)
        {
            throw new IllegalArgumentException("Czas musi być większy lub równy 0!");
        }
        if (time>0)
        {
            time--;
        }
        //Tutaj pobieramy wszystkie liczby, potem wykonujemy na nich obliczenia i zmieniamy na nowe wysylajac do serwera
        //typ wiadomosci nr 6 wysylamy
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 13); // 13 oznacza żądanie otrzymania statystyk gracza
            dane.put("userId", loggedUserID);
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();

            System.out.println("Prośba o statystyki wysłana. Oczekujemy na odpowiedź...");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject odpowiedz = new JSONObject(lina);
            if (odpowiedz.getInt("messageType") == 4) // Sukces
            {
                BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                JSONObject dane2 = new JSONObject();
                //Otrzymalismy statystyki, wiec wykonajmy potrzebne obliczenia i potem je wyslemy
                int gamesPlayed = odpowiedz.getInt("gamesPlayed");
                int gamesWon = odpowiedz.getInt("gamesWon");
                int gamesLost = odpowiedz.getInt("gamesLost");
                float averagePlayTime = odpowiedz.getFloat("averagePlayTime");
                int bestScore = odpowiedz.getInt("bestScore");
                String lastGameDate = odpowiedz.getString("lastGameDate");
                int lastScore = odpowiedz.getInt("lastScore");
                int bestTime = odpowiedz.getInt("bestTime");
                int lastTime2 = odpowiedz.getInt("lastTime");
                //Dzialania
                gamesPlayed++;
                dane2.put("gamesPlayed",gamesPlayed);
                if(didWin)
                {
                    gamesWon++;
                }
                else
                {
                    gamesLost++;
                }
                dane2.put("gamesWon",gamesWon);
                dane2.put("gamesLost",gamesLost);
                averagePlayTime = ((averagePlayTime * (gamesPlayed-1))+time)/(gamesPlayed);
                dane2.put("averagePlayTime",averagePlayTime);
                //Trzeba liczyc jakas formula wynik gry. Ja uzywam takiej. Robie takze lastScore
                dane2.put("bestScore",bestScore);

                int wynik=(int)(loggedBombsAmount*loggedBombsAmount * loggedBoardSize * (1/((float)time+1)));
                gameScoreLabel.setText("Wynik: "+wynik);
                if(didWin)
                {
                    //Nowy najlepszy wynik, o ile nasz jest lepszy
                    if(bestScore<wynik)
                    {
                        bestScore=wynik;
                    }
                    dane2.put("bestScore",bestScore);
                }
                lastScore=wynik;
                dane2.put("lastScore",lastScore);
                //Teraz ta data piekielna
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                lastGameDate = today.format(formatter);
                dane2.put("lastGameDate",lastGameDate);
                //Data zrobiona
                dane2.put("bestTime",bestTime);
                if(bestTime > time && didWin)
                {
                    bestTime=time;
                    dane2.put("bestTime",bestTime);
                }
                lastTime2=time;
                dane2.put("lastTime",lastTime2);

                //Dane obrobione, wiec je mozemy wysylac i wstawiac
                dane2.put("userId",loggedUserID);
                bw2.write(dane2.toString());
                bw2.newLine();
                bw2.flush();
            }
            if(odpowiedz.getInt("messageType") == 3)//    Brak jakichkolwiek wierszy w tabeli settings dla naszego usera
            {
                System.out.println(odpowiedz.getString("message"));
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
    }
    private void gameButtonClicked(int row, int col)
    {
        if(row<0 || col<0 || row>=loggedBoardSize || col>=loggedBoardSize)
        {
            return;
        }
        //  Tablica jest wypełniona wartościami -1. Jesli klikniety przycisk ma wartosc -1 to oznacza to, ze zostal kliknięty pierwszy raz więc
        //  generujemy bomby tak aby nie była na klikniętym przez nas miejscu
        if(gameBoard[row][col]==-1)//   Kliknelismy po raz pierwszy na tabele, wiec generujemy bomby tak aby nie były w klikniętym miejscu
        {
            clickedCells++;
            timer.start();
            Random random = new Random();

            int bombsCount = 0;

            while (bombsCount < loggedBombsAmount)
            {
                int x = random.nextInt(loggedBoardSize);
                int y = random.nextInt(loggedBoardSize);

                if ((gameBoard[x][y] == -1) && (x!=row && y!=col))
                {
                    gameBoard[x][y] = 10;  // Ustawienie wartości bomby
                    bombsCount++;
                }
            }
            for(int i=0;i<loggedBoardSize;i++)
            {
                for(int j=0;j<loggedBoardSize;j++)
                {
                    if(gameBoard[i][j]!=10)
                    {
                        gameBoard[i][j]=0;
                    }
                }
            }
            for(int i=0;i<loggedBoardSize;i++)
            {
                for(int j=0;j<loggedBoardSize;j++)
                {
                    if(gameBoard[i][j]!=10)
                    {
                        gameBoard[i][j]=countAdjacentBombs(gameBoard,i,j);
                    }
                }
            }
        }
        if(gameBoard[row][col]==10)//Kliknelismy w bombe, zatrzymaj gre
        {
            //tutaj zatrzymaj czas
            timer.koniec();
            //odkryj cala plansze
            for(int i=0;i<loggedBoardSize;i++)
            {
                for(int j=0;j<loggedBoardSize;j++)
                {
                    Label label1 = new Label();
                    if(gameBoard[i][j]>0 && gameBoard[i][j]<10)
                    {
                        label1.setText(String.valueOf(gameBoard[i][j]));
                    }
                    if(gameBoard[i][j]==10)
                    {
                        label1.setText("X");
                    }
                    GridPane.setRowIndex(label1, i);
                    GridPane.setColumnIndex(label1, j);
                    gridPanelDoGry.getChildren().add(label1);

                    // Ustawienie szerokości i wysokości etykiety
                    label1.setMinSize(40, 40);
                    label1.setMaxSize(40, 40);
                    label1.setPrefSize(40, 40);

                    // Dodanie klasy CSS
                    label1.getStyleClass().add("game-label");
                }
            }
            //wyswietl graczowi, ze przegral i pozwol zresetowac gre
            gamePlayLabelError.setText("Przegrałeś! Spróbuj ponownie!");
            gamePlayLabelError.setTextFill(Color.RED);
            resetGameButton.setVisible(true);
            //wyslij statystyki do serwera, ze gracz przegral  clickedCells++;
            endGame((int)timer.getGameTime(),false);
        }
        else
        {
            Label label1 = new Label();
            if (gameBoard[row][col] > 0) {
                label1.setText(String.valueOf(gameBoard[row][col]));
            }

            boolean labelExists = false;
            for (Node node : gridPanelDoGry.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col && node instanceof Label) {
                    labelExists = true;
                    break;
                }
            }

            if (!labelExists) {
                GridPane.setRowIndex(label1, row);
                GridPane.setColumnIndex(label1, col);
                gridPanelDoGry.getChildren().add(label1);
            }


            // Ustawienie szerokości i wysokości etykiety
            label1.setMinSize(40, 40);
            label1.setMaxSize(40, 40);
            label1.setPrefSize(40, 40);

            // Dodanie klasy CSS
            label1.getStyleClass().add("game-label");
            // Sprawdzilismy punkt, to teraz sprawdzmy czy to jest ostatni z mozliwych do klikniecia - jesli tak, to zakonczmy gre i wyslijmy statystyki



            //Robimy mala zamiane. Jesli nasze pole jest rowne 0, to zamieniamy je na -2, wtedy petla while puszczamy
            //sprawdzanie 0, jesli jest sasiadem -2 to wtedy jego tez ustawiamy na -2. Jak petla sie skonczy to wszystkie -2 ustawiamy na 0 i odkrywamy
            if(gameBoard[row][col]==0)
            {
                gameBoard[row][col]=-2;
                gameButtonClicked(row+1,col);
                gameButtonClicked(row,col+1);
                gameButtonClicked(row,col-1);
                gameButtonClicked(row-1,col);
                gameButtonClicked(row+1,col+1);
                gameButtonClicked(row-1,col-1);
                gameButtonClicked(row+1,col-1);
                gameButtonClicked(row-1,col+1);
            }
            if(checkAmountOfCellsLeft()==(loggedBoardSize*loggedBoardSize)-loggedBombsAmount)// Wygrana!
            {
                timer.koniec();
                gamePlayLabelError.setTextFill(Color.GREEN);
                gamePlayLabelError.setText("Wygrałeś!");
                //  Odkrywamy cala plansze
                for(int i=0;i<loggedBoardSize;i++)
                {
                    for(int j=0;j<loggedBoardSize;j++)
                    {
                        Label label2 = new Label();
                        if(gameBoard[i][j]>0 && gameBoard[i][j]<10)
                        {
                            label2.setText(String.valueOf(gameBoard[i][j]));
                        }
                        if(gameBoard[i][j]==10)
                        {
                            label2.setText("X");
                        }
                        GridPane.setRowIndex(label2, i);
                        GridPane.setColumnIndex(label2, j);
                        gridPanelDoGry.getChildren().add(label2);

                        // Ustawienie szerokości i wysokości etykiety
                        label2.setMinSize(40, 40);
                        label2.setMaxSize(40, 40);
                        label2.setPrefSize(40, 40);

                        // Dodanie klasy CSS
                        label2.getStyleClass().add("game-label");
                    }
                }
                //
                endGame((int)timer.getGameTime(),true);
            }
        }

    }
    private int checkAmountOfCellsLeft()
    {
        int clickedAmount = 0;

        for (int i = 0; i < loggedBoardSize; i++) {
            for (int j = 0; j < loggedBoardSize; j++) {
                for (Node node : gridPanelDoGry.getChildren()) {
                    if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == i && node instanceof Label) {
                        clickedAmount++;
                    }
                }
            }
        }
        return clickedAmount;
    }

    public static int countAdjacentBombs(int[][] gameBoard, int row, int col) {
        if(row<0 || col<0 || row>= gameBoard.length || col>= gameBoard.length)
        {
            throw new IllegalArgumentException("Wiersz lub kolumna poza granicami tablicy gameBoard");
        }
        int count = 0;
        int numRows = gameBoard.length;
        int numCols = gameBoard[0].length;

        for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, numRows - 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(col + 1, numCols - 1); j++) {
                if (!(i == row && j == col) && gameBoard[i][j] == 10) {
                    count++;
                }
            }
        }
        return count;
    }
    @FXML
    private void returnToMainMenuFromPlayBody()
    {
        playBodyVBox.setVisible(false);
        mainMenuVBox.setVisible(true);
        timer.koniec();
        gridPanelDoGry.getChildren().clear();
        if(loggedWindowWidth >= 400 && loggedWindowHeight>=400) {
            zmienSzerokoscOkna(loggedWindowWidth, loggedWindowHeight);
        }
        //mozliwa poprawka   else if(loggedWindowWidth >= 400 && loggedWindowHeight<400)
        else if(loggedWindowWidth >= 400)
        {
            zmienSzerokoscOkna(loggedWindowWidth,400);
        }
        //mozliwa poprawka      else if(loggedWindowWidth < 400 && loggedWindowHeight>=400)
        else if(loggedWindowHeight>=400)
        {
            zmienSzerokoscOkna(400,loggedWindowHeight);
        }
        else
        {
            zmienSzerokoscOkna(400,400);
        }
    }
    @FXML
    private void addSettings() throws IOException
    {
        if(addSettingsWidth.getText().equals(""))
        {
            addSettingsWidth.setText("0");
        }
        if(addSettingsHeight.getText().equals(""))
        {
            addSettingsHeight.setText("0");
        }
        if(addSettingsAmount.getText().equals(""))
        {
            addSettingsAmount.setText("0");
        }
        if(addSettingsSize.getText().equals(""))
        {
            addSettingsSize.setText("0");
        }
        int wartoscWidth =Integer.parseInt(addSettingsWidth.getText());
        int wartoscHeight = Integer.parseInt(addSettingsHeight.getText());
        int wartoscAmount = Integer.parseInt(addSettingsAmount.getText());
        int wartoscSize = Integer.parseInt(addSettingsSize.getText());


        if(wartoscWidth<400 || wartoscWidth>2000 || wartoscHeight<400 || wartoscHeight>2000 || wartoscAmount <1 || wartoscAmount >70 || wartoscSize <2 || wartoscSize >10)
        {
            setDeleteSettingsLabel.setText("Podano złe wartości!");
            setDeleteSettingsLabel.setTextFill(Color.RED);
            return;
        }
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 8); // 8 oznacza żądanie dodania nowych ustawien do tabeli settings
            dane.put("userId", loggedUserID);
            dane.put("width", addSettingsWidth.getText());
            dane.put("height", addSettingsHeight.getText());
            dane.put("amount", addSettingsAmount.getText());
            dane.put("size", addSettingsSize.getText());
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Prośba o dodanie ustawień wysłana. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject odpowiedz = new JSONObject(lina);
            System.out.println("Otrzymano odpowiedź!");
            if (odpowiedz.getInt("messageType") == 4)// Sukces
            {
                setDeleteSettingsLabel.setText("Pomyślnie dodano nowe ustawienia!");
                setDeleteSettingsLabel.setTextFill(Color.GREEN);
                System.out.println(odpowiedz.getString("message"));
                enterSettingsBody();
                addSettingsWidth.setText("");
                addSettingsHeight.setText("");
                addSettingsAmount.setText("");
                addSettingsSize.setText("");

            }
            if(odpowiedz.getInt("messageType") == 3)//    Brak jakichkolwiek wierszy w tabeli settings dla naszego usera
            {
                System.out.println(odpowiedz.getString("message"));
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }


    }
    @FXML
    private ChoiceBox<String> choiceBoxSettings;
    @FXML
    private Label setDeleteSettingsLabel;
    @FXML
    private void changeSelectedSettings()
    {
        setDeleteSettingsLabel.setText("");
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 9); // 9 oznacza żądanie zmiany ustawien
            dane.put("userId", loggedUserID);
            dane.put("settingsId", choiceBoxSettings.getValue());
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();

            System.out.println("Prośba o zmiane ustawień wysłana. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject odpowiedz = new JSONObject(lina);
            System.out.println("Otrzymano odpowiedź!");
            if (odpowiedz.getInt("messageType") == 4) // Sukces
            {
                //Tutaj trzeba dodac takze odbieranie nowych ustawien
                loggedWindowHeight=odpowiedz.getInt("newHeight");
                loggedWindowWidth=odpowiedz.getInt("newWidth");
                loggedBombsAmount=odpowiedz.getInt("bombsAmount");
                loggedBoardSize=odpowiedz.getInt("boardSize");
                setDeleteSettingsLabel.setText(odpowiedz.getString("message"));
                setDeleteSettingsLabel.setTextFill(Color.GREEN);
                if(loggedWindowWidth >= 435 && loggedWindowHeight>=400)
                {
                    zmienSzerokoscOkna(loggedWindowWidth, loggedWindowHeight);
                }
                //Mozliwa poprawka  else if(loggedWindowWidth >= 435 && loggedWindowHeight<400)

                else if(loggedWindowWidth >= 435)
                {
                    zmienSzerokoscOkna(loggedWindowWidth,400);
                }
                //mozliwa poprawka else if(loggedWindowWidth < 435 && loggedWindowHeight>=400)
                else if(loggedWindowHeight>=400)
                {
                    zmienSzerokoscOkna(435,loggedWindowHeight);
                }
                else
                {
                    zmienSzerokoscOkna(435,400);
                }
                loggedSelectedSettings= Integer.parseInt(choiceBoxSettings.getValue());
            }
            else if(odpowiedz.getInt("messageType") == 3)//    Blad w zmianie ustawien
            {
                setDeleteSettingsLabel.setText(odpowiedz.getString("message"));
                setDeleteSettingsLabel.setTextFill(Color.RED);
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
    }
    @FXML
    private void deleteSettings()
    {
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 11); // 11 oznacza żądanie usuniecia ustawien
            dane.put("userId", loggedUserID);
            dane.put("settingsId", choiceBoxSettings.getValue());
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();

            System.out.println("Prośba o usunięcie ustawień wysłana. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject odpowiedz = new JSONObject(lina);
            System.out.println("Otrzymano odpowiedź!");
            if (odpowiedz.getInt("messageType") == 4) // Sukces
            {
                enterSettingsBody();
                setDeleteSettingsLabel.setText(odpowiedz.getString("message"));
                setDeleteSettingsLabel.setTextFill(Color.GREEN);
            }
            else if(odpowiedz.getInt("messageType") == 3)//    Blad w zmianie ustawien
            {
                setDeleteSettingsLabel.setText(odpowiedz.getString("message"));
                setDeleteSettingsLabel.setTextFill(Color.RED);
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
    }
    @FXML
    private void enterSettingsBody()
    {
        setDeleteSettingsLabel.setText("");
//        loggedUserID=24;
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 6); // 6 oznacza żądanie otrzymania ustawien
            dane.put("userId", loggedUserID);
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Prośba o dane ustawień wysłana. Oczekujemy na odpowiedź...");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            JSONObject odpowiedz = new JSONObject(lina);
            System.out.println("Otrzymano odpowiedź!");
            if (odpowiedz.getInt("messageType") == 4) // Sukces
            {
                dataTabelaUstawien.clear();
                choiceBoxSettings.getItems().clear();
                JSONArray data = odpowiedz.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject row = data.getJSONObject(i);
                    Integer windowWidth = row.getInt("window_width");
                    Integer windowHeight = row.getInt("window_height");
                    Integer bombsAmount = row.getInt("bombs_amount");
                    Integer boardSize = row.getInt("board_size");
                    Integer rowId = row.getInt("row_id");

                    String idString = rowId.toString();
                    choiceBoxSettings.getItems().add(idString);

                    addRowSettings(windowWidth, windowHeight, bombsAmount, boardSize,rowId);
                }
            }
            if(odpowiedz.getInt("messageType") == 3)//    Brak jakichkolwiek wierszy w tabeli settings dla naszego usera
            {
                System.out.println(odpowiedz.getString("message"));
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
        //Kod do wybrania liczby aktualnie uzywanych ustawien (by wpisac do choicebox)

        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))
        {
            JSONObject dane = new JSONObject();
            dane.put("messageType", 10); // 10 oznacza żądanie otrzymania numeru aktualnie wybranych ustawien
            dane.put("userId", loggedUserID);
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();

            System.out.println("Prośba o dane aktualnie wybranych ustawień wysłana. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String linia = br.readLine();
            JSONObject odp = new JSONObject(linia);
            System.out.println("Otrzymano odpowiedź!");
            if (odp.getInt("messageType") == 4) // Sukces
            {
                choiceBoxSettings.setValue(String.valueOf(odp.getInt("current_settings_id")));
                loggedSelectedSettings = odp.getInt("current_settings_id");
                loggedWindowWidth=odp.getInt("windowWidth");
                loggedWindowHeight=odp.getInt("windowHeight");
                loggedBombsAmount=odp.getInt("bombs");
                loggedBoardSize=odp.getInt("board");

                if(loggedWindowWidth >= 400 && loggedWindowHeight>=400)
                {
                    zmienSzerokoscOkna(loggedWindowWidth, loggedWindowHeight);
                }
                //mozliwa poprawka else if(loggedWindowWidth >= 400 && loggedWindowHeight<400)
                else if(loggedWindowWidth >= 400)
                {
                    zmienSzerokoscOkna(loggedWindowWidth,400);
                }
                //mozliwa poprawka else if(loggedWindowWidth < 400 && loggedWindowHeight>=400)
                else if(loggedWindowHeight>=400)
                {
                    zmienSzerokoscOkna(400,loggedWindowHeight);
                }
                else
                {
                    zmienSzerokoscOkna(400,400);
                }
            }
            else if(odp.getInt("messageType") == 3)//    Problem w pobieraniu aktualnych ustawien danego usera!
            {
                System.out.println(odp.getString("message"));
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }

        settingsBody.setVisible(true);
        mainMenuVBox.setVisible(false);
    }
    @FXML
    private void returnToMainMenuFromScoresButton()
    {
        if(loggedWindowWidth >= 400 && loggedWindowHeight>=400) {
            zmienSzerokoscOkna(loggedWindowWidth, loggedWindowHeight);
        }
        //mozliwa poprawka   else if(loggedWindowWidth >= 400 && loggedWindowHeight<400)
        else if(loggedWindowWidth >= 400)
        {
            zmienSzerokoscOkna(loggedWindowWidth,400);
        }
        //mozliwa poprawka      else if(loggedWindowWidth < 400 && loggedWindowHeight>=400)
        else if(loggedWindowHeight>=400)
        {
            zmienSzerokoscOkna(400,loggedWindowHeight);
        }
        else
        {
            zmienSzerokoscOkna(400,400);
        }

        tableScoreBody.setVisible(false);
        mainMenuVBox.setVisible(true);
    }
    @FXML
    private void returnToMainMenuFromSettingsButton()
    {
        //300x300 the best
        if(loggedWindowWidth > 300 && loggedWindowHeight>300)
        {
            zmienSzerokoscOkna(loggedWindowWidth, loggedWindowHeight);
        }
        else if(loggedWindowWidth > 300 && loggedWindowHeight<300)
        {
            zmienSzerokoscOkna(loggedWindowWidth,300);
        }
        else if(loggedWindowWidth < 300 && loggedWindowHeight>300)
        {
            zmienSzerokoscOkna(300,loggedWindowHeight);
        }
        else
        {
            zmienSzerokoscOkna(300,300);
        }
        settingsBody.setVisible(false);
        mainMenuVBox.setVisible(true);
    }
    @FXML
    private void enterScoreBody()
    {
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            JSONObject dane = new JSONObject();
            dane.put("messageType", 5); // 5 oznacza żądanie statystyk
            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Prośba o dane tabeli wysłana. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lina = br.readLine();
            System.out.println("Odpowiedź otrzymana!");
            JSONObject odpowiedz = new JSONObject(lina);
            if (odpowiedz.getInt("messageType") == 4)// Sukces
            {
                dataTabela.clear();
                //470x300 perfekcyjne rozmiary
                zmienSzerokoscOkna(470,300);
                JSONArray listaDanych = new JSONArray(odpowiedz.getJSONArray("data"));
                for (int i = 0; i < listaDanych.length(); i++) {
                    JSONObject aktualnyObiekt = listaDanych.getJSONObject(i);
                    String username = aktualnyObiekt.getString("username");
                    String gamesPlayed = String.valueOf(aktualnyObiekt.getInt("games_played"));
                    String bestScore = String.valueOf(aktualnyObiekt.getInt("best_score"));
                    String bestTime = String.valueOf(aktualnyObiekt.getInt("best_time"));

                    addRow(username, gamesPlayed, bestScore, bestTime);
                }
            }
            if(odpowiedz.getInt("messageType") == 3)//    Brak jakichkolwiek wierszy
            {
                System.out.println(odpowiedz.getString("message"));
            }
        } catch (IOException e)
        {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
        if(loggedWindowWidth > 460 && loggedWindowHeight>300) {
            zmienSzerokoscOkna(loggedWindowWidth, loggedWindowHeight);
        }
        else if(loggedWindowWidth > 460 && loggedWindowHeight<300)
        {
            zmienSzerokoscOkna(loggedWindowWidth,300);
        }
        else if(loggedWindowWidth < 460 && loggedWindowHeight>300)
        {
            zmienSzerokoscOkna(460,loggedWindowHeight);
        }
        else
        {
            zmienSzerokoscOkna(460,300);
        }
        tableScoreBody.setVisible(true);
        mainMenuVBox.setVisible(false);
    }
    private boolean isValidEmail(String email)
    {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public boolean isValidUsername(String username)
    {
        if(username == null)
        {
            return false;
        }
        String usernameRegex = "^[A-Za-z0-9_]+$";

        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }
    public static boolean isValidPassword(String password)
    {
        if(password == null)
        {
            return false;
        }
        String passwordRegex = "^[a-zA-Z0-9!@#$%^&*()\\-=_+]+$";

        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
    @FXML
    protected void onLoginButtonClick()
    {
        String username = usernameLoginField.getText();
        String password = passwordLoginField.getText();
        if(!isValidUsername(username))
        {
            resultLoginDisplay.setText("Wpisz poprawny username! Dozwolone znaki to: małe znaki, duże znaki oraz \"-\"");
            resultLoginDisplay.setTextFill(Color.RED);
            return;
        }
        if(!isValidPassword(password))
        {
            resultLoginDisplay.setText("Wpisz poprawne hasło! Dozwolone są małe znaki, duże znaki oraz znaki specjalne.");
            resultLoginDisplay.setTextFill(Color.RED);
            return;
        }
        // Tutaj możesz dodać logikę weryfikacji użytkownika na podstawie danych logowania
        System.out.println("Wysyłam prośbę o logowanie do serwera...");
        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            JSONObject dane = new JSONObject();
            dane.put("messageType", 1); // 1 oznacza żądanie logowania
            dane.put("username", username);
            dane.put("password", password);

            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Dane wysłane do serwera. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String linia = br.readLine();
            JSONObject odp = new JSONObject(linia);
            System.out.println("Otrzymano odpowiedź!");
            int typWiadomosci = odp.getInt("messageType");
            String messageZwrotna = odp.getString("message");
            //Blad w logowaniu
            if(typWiadomosci==3)
            {
                resultLoginDisplay.setText(messageZwrotna);
                resultLoginDisplay.setTextFill(Color.RED);
            } else if(typWiadomosci==4)//Sukces w logowaniu
            {
                loggedUserID=odp.getInt("userId");
                loggedUserUsername=odp.getString("username");
                loggedUserEmail=odp.getString("email");
                //Tutaj zalogowany gracz
                loginAndRegisterVBox.setVisible(false);
                mainMenuVBox.setVisible(true);
            }
        } catch (IOException e)
        {
            resultLoginDisplay.setText(e.getMessage());
            resultLoginDisplay.setTextFill(Color.RED);
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }



    }
    @FXML
    protected void onRegisterButtonClick() throws IOException
    {
        String username = usernameRegisterField.getText();
        String email = emailRegisterField.getText();
        String password = passwordRegisterField.getText();
        if(!isValidEmail(email))
        {

            resultRegisterDisplay.setText("Podaj poprawny email!");
            resultRegisterDisplay.setTextFill(Color.RED);
            return;
        }
        if(!isValidUsername(username))
        {

            resultRegisterDisplay.setText("Podaj poprawny username! Dozwolone znaki to: małe znaki, duże znaki oraz \"-\"");
            resultRegisterDisplay.setTextFill(Color.RED);
            return;
        }
        if(!isValidPassword(password))
        {
            resultRegisterDisplay.setText("Podaj poprawne hasło! Dozwolone są małe znaki, duże znaki oraz znaki specjalne.");
            resultRegisterDisplay.setTextFill(Color.RED);
            return;
        }
        System.out.println("Wysyłam prośbę o rejestrację do serwera...");

        try (Socket socket = new Socket("127.0.0.1", 8932);
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            JSONObject dane = new JSONObject();
            dane.put("messageType", 2); // 2 oznacza żądanie rejestracji
            dane.put("username", username);
            dane.put("password", password);
            dane.put("email", email);

            bw.write(dane.toString());
            bw.newLine();
            bw.flush();
            System.out.println("Dane wysłane do serwera. Oczekujemy na odpowiedź...");

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String linia = br.readLine();
            JSONObject odp = new JSONObject(linia);
            System.out.println("Otrzymano odpowiedź!");
            int typWiadomosci = odp.getInt("messageType");
            String messageZwrotna = odp.getString("message");
            //Blad w rejestrowaniu
            if(typWiadomosci==3)
            {
                resultRegisterDisplay.setText(messageZwrotna);
                resultRegisterDisplay.setTextFill(Color.RED);
            } else if(typWiadomosci==4)//Sukces w rejestrowaniu
            {
                resultRegisterDisplay.setText(messageZwrotna);
                resultRegisterDisplay.setTextFill(Color.GREEN);
                //Przechodzimy na login
                widok1.setVisible(true);
                widok2.setVisible(false);
                usernameLoginField.setText(usernameRegisterField.getText());
                resultLoginDisplay.setText(messageZwrotna+"\nZaloguj się!");
                passwordLoginField.setText("");
                resultLoginDisplay.setTextFill(Color.GREEN);
            }
            System.out.println(messageZwrotna);
        } catch (IOException e) {
            System.err.println("Błąd podczas komunikacji z serwerem: " + e.getMessage());
        }
    }

    @FXML
    protected void onWidok1Click() {
        widok1.setVisible(true);
        widok2.setVisible(false);
        usernameLoginField.setText("");
        passwordLoginField.setText("");
        resultLoginDisplay.setText("");
    }

    @FXML
    protected void onWidok2Click()
    {
        widok1.setVisible(false);
        widok2.setVisible(true);
        emailRegisterField.setText("");
        usernameRegisterField.setText("");
        passwordRegisterField.setText("");
        resultRegisterDisplay.setText("");
    }

}
