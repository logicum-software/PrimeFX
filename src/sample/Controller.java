package sample;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    public ProgressBar ProgressBarCompute;
    public Label LabelNumber;
    public Label LabelDuration;
    public Button ButtonCompute;
    public Button ButtonBeenden;

    private static int nPrimes;
    private static final BooleanProperty running = new SimpleBooleanProperty();
    private static final DoubleProperty time = new SimpleDoubleProperty();
    private static List<Integer> Dividends = new ArrayList<>();
    private static List<Integer> Divisors = new ArrayList<>();
    private static Integer End = 100000;
    private static Integer Start = 3;

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
            if (Start < 3)
                nPrimes = 0;
            else
                nPrimes = 1;

            for (int current : Dividends) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                updateProgress(current, End - Start);
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
            for (int i = Start; i <= End; i++)
                Dividends.add(i);

            for (int z = 2; z <= End / 2; z++)
                Divisors.add(z);

            new Thread(task).start();
        }
    }
}