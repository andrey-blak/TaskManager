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

import java.lang.ref.WeakReference;

public class TaskActivity extends FragmentActivity {
    private static final int MAX = 6;
    private static final int PERIOD = 1000;

    @InjectView(R.id.bw__common_counter)
    TextView mCommonCounterView;

    @InjectView(R.id.bw__common_weak_counter)
    TextView mCommonWeakCounterView;

    @InjectView(R.id.bw__class_counter)
    TextView mClassCounterView;

    @InjectView(R.id.bw__class_weak_counter)
    TextView mClassWeakCounterView;

    @InjectView(R.id.bw__key_counter)
    TextView mKeyCounterView;

    @InjectView(R.id.bw__key_weak_counter)
    TextView mKeyWeakView;

    @InjectView(R.id.bw__counter_start_btn)
    View mStartButton;

    @InjectView(R.id.bw__counter_cancel_btn)
    View mCancelButton;

    private CounterListener mCommonListener;
    private CounterListener mClassListener;
    private CounterListener mKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw__screen_counter);
        ButterKnife.inject(this);

        initListeners();

        mCommonListener = new CounterListener(mCommonCounterView);
        mClassListener = new CounterListener(mClassCounterView);
        mKeyListener = new CounterListener(mKeyCounterView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TaskApp.getTaskManager().addListener(mCommonListener);
        WeakReference<CounterListener> commonListenerReference = new WeakReference(new CounterListener(mCommonWeakCounterView));
        TaskApp.getTaskManager().addListener(commonListenerReference);

        Class<CounterTask> taskClass = CounterTask.class;
        TaskApp.getTaskManager().addListener(taskClass, mClassListener);
        WeakReference<CounterListener> classListenerReference = new WeakReference(new CounterListener(mClassWeakCounterView));
        TaskApp.getTaskManager().addListener(taskClass, classListenerReference);

        String key = CounterTask.KEY;
        TaskApp.getTaskManager().addListener(key, mKeyListener);
        WeakReference<CounterListener> keyListenerReference = new WeakReference(new CounterListener(mKeyWeakView));
        TaskApp.getTaskManager().addListener(key, keyListenerReference);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TaskApp.getTaskManager().removeListener(mCommonListener);
        TaskApp.getTaskManager().removeListener(CounterTask.class, mClassListener);
        TaskApp.getTaskManager().removeListener(CounterTask.KEY, mKeyListener);
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
        TaskApp.getTaskManager().execute(task);
    }

    private void cancelTask() {
        TaskApp.getTaskManager().cancel(CounterTask.KEY);
    }

    private void showMessage(final TextView textView, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private class CounterListener implements TaskListener<Integer, String, Integer> {
        private TextView mTextView;

        public CounterListener(TextView textView) {
            mTextView = textView;
        }

        @Override
        public void onFinish(Integer result, Task<Integer, String, Integer> task) {
            String text = "Finished: " + result;
            showMessage(mTextView, text);
        }

        @Override
        public void onProgress(Integer progress, Task<Integer, String, Integer> task) {
            String text = String.valueOf(progress);
            showMessage(mTextView, text);
        }

        @Override
        public void onCanceled(Task<Integer, String, Integer> task) {
            showMessage(mTextView, "Canceled");
        }
    }

    private static class CounterTask extends Task<Integer, String, Integer> {
        static final String KEY = "Key";

        @Override
        public String getKey() {
            return KEY;
        }

        @Override
        protected void execute() {
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
