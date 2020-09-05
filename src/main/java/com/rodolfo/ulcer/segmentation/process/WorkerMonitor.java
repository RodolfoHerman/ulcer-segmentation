package com.rodolfo.ulcer.segmentation.process;

import java.util.List;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkerMonitor {

    private ReadOnlyStringWrapper directory = new ReadOnlyStringWrapper();
    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    private ReadOnlyBooleanWrapper idle = new ReadOnlyBooleanWrapper();

    public void monitor(Worker worker) {

        worker.stateProperty().addListener(new ChangeListener<Task.State>() {

            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {

                switch (newValue) {

                    case RUNNING:

                        progress.set(worker.progressProperty().get());
                        progress.bind(worker.progressProperty());

                    break;

                    case SUCCEEDED:

                        directory.unbind();
                        idle.set(true);
                        progress.unbind();
                        progress.set(0.0);

                    break;

                    case FAILED:

                        directory.unbind();
                        worker.stateProperty().removeListener(this);
                        idle.set(true);

                    break;

                    case SCHEDULED:

                        directory.unbind();
                        directory.set(worker.getTitle());
                        idle.set(false);
                        progress.unbind();

                    break;
                
                    default:

                    break;
                }
            }
        });
    }

    public void monitor(List<Worker> workers) {

        workers.stream().forEach(worker -> this.monitor(worker));
    }
}