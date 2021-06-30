package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public ProgressBar ProgressBarCompute;
    public Label LabelNumber;
    public Label LabelDuration;
    public Button ButtonCompute;
    public Button ButtonBeenden;
    private static final List<Integer> Dividends = new ArrayList<>();
    private static final List<Integer> Divisors = new ArrayList<>();

    Task<Integer> task = new Task<Integer>() {
        @Override protected Integer call() throws Exception {
            int iterations;
            for (iterations = 0; iterations < 1000; iterations++) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                updateMessage("Iteration " + iterations);
                updateProgress(iterations, 1000);

                //Block the thread for a short time, but be sure
                //to check the InterruptedException for cancellation
                try {
                    Thread.sleep(10);
                } catch (InterruptedException interrupted) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                }
            }
            return iterations;
        }
    };

    public void onButtonBeenden(ActionEvent actionEvent) {
        Stage stage = (Stage) ButtonBeenden.getScene().getWindow();
        stage.close();
    }

    public void onButtonCompute(ActionEvent actionEvent) {
        for (int i = 3; i <= 100000; i++)
            Dividends.add(i);

        for (int z = 2; z <= 50000; z++)
            Divisors.add(z);

        ProgressBarCompute.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }
}
