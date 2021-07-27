package sample;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.beans.EventHandler;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller {
    public ProgressBar ProgressBarCompute;
    public Label LabelNumber;
    public Label LabelDuration;
    public Button ButtonCompute;
    public Button ButtonBeenden;

    private int nPrimes;
    private boolean IsStarted = false;
    private Instant StartTime, EndTime;
    private long Duration;
    private String DurString = "not calculated";
    private SimpleStringProperty ObsString = new SimpleStringProperty(DurString);
    private static final List<Integer> Dividends = new ArrayList<>();
    private static final List<Integer> Divisors = new ArrayList<>();

    Task<Integer> task = new Task<Integer>() {
        @Override protected Integer call() throws Exception {
            for (int current : Dividends) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                updateProgress(current, 100000);
                updateMessage(Integer.toString(nPrimes));
                EndTime = Instant.now();
                Duration = ChronoUnit.SECONDS.between(StartTime, EndTime);
                DurString = String.format("%d", Duration);

                for (int divisor : Divisors) {
                    if (divisor <= current / 2) {
                        if ((float) current % divisor == 0)
                            break;
                    } else {
                        nPrimes++;
                        break;
                    }
                }
            }
            return nPrimes;
        }
    };

    public void onButtonBeenden(ActionEvent actionEvent) {
        Stage stage = (Stage) ButtonBeenden.getScene().getWindow();
        stage.close();
    }

    public void onButtonCompute(ActionEvent actionEvent) {

        if (IsStarted) {
            IsStarted = false;
            task.cancel();
            ButtonCompute.setText("Berechnung starten");
        } else {
            StartTime = Instant.now();
            ProgressBarCompute.progressProperty().bind(task.progressProperty());
            LabelNumber.textProperty().bind(task.messageProperty());
            LabelDuration.textProperty().bind(ObsString);
            IsStarted = true;
            ButtonCompute.setText("Abbrechen");
            for (int i = 3; i <= 100000; i++)
                Dividends.add(i);

            for (int z = 2; z <= 50000; z++)
                Divisors.add(z);

            nPrimes = 1;
            new Thread(task).start();
        }
    }
}
