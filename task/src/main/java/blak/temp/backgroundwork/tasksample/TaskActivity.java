package blak.temp.backgroundwork.tasksample;

import blak.temp.backgroundwork.R;
import blak.temp.backgroundwork.TaskApp;
import blak.temp.backgroundwork.task.Task;
import blak.temp.backgroundwork.task.TaskListener;
import butterknife.ButterKnife;
import butterknife.InjectView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class TaskActivity extends FragmentActivity {
    private static final int MAX = 6;
    private static final int PERIOD = 1000;

    @InjectView(R.id.bw__counter_counter)
    TextView mResultTextView;

    @InjectView(R.id.bw__counter_start_btn)
    View mStartButton;

    @InjectView(R.id.bw__counter_cancel_btn)
    View mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw__screen_counter);
        ButterKnife.inject(this);

        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String key = CounterTask.KEY;
        TaskApp.getTaskManager().addListener(key, mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TaskApp.getTaskManager().removeListener(CounterTask.KEY, mListener);
    }

    public void initListeners() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTask();
            }
        });
    }

    private void startTask() {
        CounterTask task = new CounterTask();
        TaskApp.getTaskManager().execute(task, null);
    }

    private void  cancelTask() {
        TaskApp.getTaskManager().cancel(CounterTask.KEY);
    }

    private TaskListener<Integer, String, Integer> mListener = new TaskListener<Integer, String, Integer>() {
        @Override
        public void onFinish(final Integer result, Task<Integer, String, Integer> task) {
            // todo implement
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = "Finished: " + result;
                    mResultTextView.setText(text);
                }
            });
        }

        @Override
        public void onProgress(final Integer progress, Task<Integer, String, Integer> task) {
            // todo implement
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = String.valueOf(progress);
                    mResultTextView.setText(text);
                }
            });
        }
    };

    private static class CounterTask extends Task<Integer, String, Integer> {
        static final String KEY = "Key";

        @Override
        public String getKey() {
            return KEY;
        }

        @Override
        public void run() {
            int result = 0;
            for (int i = 0; i < MAX; i++) {
                if (isCancelled()) {
                    return;
                }

                publishProgress(i);
                result = i;
                try {
                    Thread.sleep(PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            onFinish(result);
        }
    }
}
