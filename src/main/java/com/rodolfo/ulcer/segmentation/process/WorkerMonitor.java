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

    private ReadOnlyStringWrapper directory;
    private ReadOnlyDoubleWrapper progress;
    private ReadOnlyBooleanWrapper idle;

    public void monitor(Task<Void> worker) {

        this.progress = new ReadOnlyDoubleWrapper();
        this.idle = new ReadOnlyBooleanWrapper();
        this.directory = new ReadOnlyStringWrapper(worker.getTitle());

        worker.stateProperty().addListener(new ChangeListener<Task.State>() {

            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {

                switch (newValue) {

                    case RUNNING:

                        idle.set(false);
                        progress.unbind();
                        progress.set(worker.progressProperty().get());
                        progress.bind(worker.progressProperty());

                    break;

                    case SUCCEEDED:

                        idle.set(true);
                        progress.unbind();
                        progress.set(0.0);

                    break;

                    case FAILED:

                        worker.stateProperty().removeListener(this);
                        idle.set(true);

                    break;
                
                    default:

                    break;
                }
            }
        });
    }

    public void monitor(List<Task<Void>> workers) {

        workers.stream().forEach(worker -> this.monitor(worker));
    }
}