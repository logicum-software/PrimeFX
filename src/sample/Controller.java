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

    private int nPrimes;
    private static final List<Integer> Dividends = new ArrayList<>();
    private static final List<Integer> Divisors = new ArrayList<>();

    Task<Integer> task = new Task<Integer>() {
        @Override protected Integer call() throws Exception {

            for (int current : Dividends) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    break;
                }
                updateProgress(current, 1000000);
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
            return nPrimes;
        }
    };

    public void onButtonBeenden(ActionEvent actionEvent) {
        Stage stage = (Stage) ButtonBeenden.getScene().getWindow();
        stage.close();
    }

    public void onButtonCompute(ActionEvent actionEvent) {
        for (int i = 3; i <= 1000000; i++)
            Dividends.add(i);

        for (int z = 2; z <= 500000; z++)
            Divisors.add(z);

        nPrimes = 1;
        ProgressBarCompute.progressProperty().bind(task.progressProperty());
        LabelNumber.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
}
