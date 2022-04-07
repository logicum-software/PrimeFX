package sample;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.text.CompactNumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public ProgressBar ProgressBarCompute;
    public Label LabelNumber;
    public Label LabelDuration;
    public Button ButtonCompute;
    public Button ButtonBeenden;
    public TextField TextFieldStart;
    public TextField TextFieldEnd;

    private static int nPrimes;
    private static final BooleanProperty running = new SimpleBooleanProperty();
    private static final DoubleProperty time = new SimpleDoubleProperty();
    private static List<Integer> Dividends = new ArrayList<>();
    private static List<Integer> Divisors = new ArrayList<>();
    private static ObservableValue<String> Start =
            new SimpleIntegerProperty(3).asString();
    private static ObservableValue<String> End =
            new SimpleIntegerProperty(100000).asString();

    AnimationTimer timer = new AnimationTimer() {

        private long startTime ;

        @Override
        public void start() {
            startTime = System.currentTimeMillis();
            running.set(true);
            super.start();
        }

        @Override
        public void stop() {
            running.set(false);
            super.stop();
        }

        @Override
        public void handle(long timestamp) {
            long now = System.currentTimeMillis();
            time.set((now - startTime) / 1000.0);
        }
    };

    Task<Integer> task = new Task<>() {
        @Override protected Integer call() throws Exception {
            if (Integer.getInteger(Start.toString()) < 3)
                nPrimes = 0;
            else
                nPrimes = 1;

            for (int current : Dividends) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                updateProgress(current, Integer.getInteger(End.toString()) -
                        Integer.getInteger(Start.toString()));
                updateMessage(Integer.toString(nPrimes));

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
            timer.stop();
            ButtonCompute.setDisable(false);
            return nPrimes;
        }
    };

    public void onButtonBeenden(ActionEvent actionEvent) {
        Stage stage = (Stage) ButtonBeenden.getScene().getWindow();
        stage.close();
    }

    public void onButtonCompute(ActionEvent actionEvent) {

        TextFieldStart.textProperty().bindBidirectional((Property<String>) Start);
        TextFieldEnd.textProperty().bindBidirectional((Property<String>) End);
        ProgressBarCompute.progressProperty().bind(task.progressProperty());
        LabelNumber.textProperty().bind(task.messageProperty());
        LabelDuration.textProperty().bind(time.asString("%.3f Sekunden"));
        ButtonCompute.setDisable(true);

        if (running.get()) {
            timer.stop();
            running.set(false);
            task.cancel();
            ButtonCompute.setText("Berechnung starten");
        } else {
            running.set(true);
            timer.start();
            for (int i = Integer.getInteger(Start.toString());
                 i <= Integer.getInteger(End.toString()); i++)
                Dividends.add(i);

            for (int z = 2; z <= Integer.getInteger(End.toString()) / 2; z++)
                Divisors.add(z);

            new Thread(task).start();
        }
    }
}