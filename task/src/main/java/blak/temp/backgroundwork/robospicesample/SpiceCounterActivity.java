package blak.temp.backgroundwork.robospicesample;

import blak.temp.backgroundwork.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SpiceCounterActivity extends SpiceActivity {
    @InjectView(R.id.bw__counter_counter)
    TextView mResultTextView;

    @InjectView(R.id.bw__counter_start_btn)
    View mStartBtn;

    boolean mRestored;
    private RequestListener<Void> mRequestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bw__screen_counter);
        ButterKnife.inject(this);

        restoreInstanceState(savedInstanceState);

        initListeners();
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRestored = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSpiceManager().addListenerIfPending(Void.class, "cachekey", mRequestListener);
    }

    public void initListeners() {
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpiceRequest<Void> request = new VoidSpiceRequest();
                mRequestListener = new VoidRequestListener();
                getSpiceManager().execute(request, "cachekey", DurationInMillis.ALWAYS_EXPIRED, mRequestListener);
            }
        });
    }

    private class VoidRequestListener implements RequestListener<Void>, RequestProgressListener {
        @Override
        public void onRequestSuccess(Void unused) {
            mResultTextView.setText("Finished");
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mResultTextView.setText("Failed");
        }

        @Override
        public void onRequestProgressUpdate(RequestProgress progress) {
            String progressString = String.valueOf(progress.getProgress());
            mResultTextView.setText(progressString);
        }
    }

    private static class VoidSpiceRequest extends SpiceRequest<Void> {
        private static final int MAX = 25;
        private static final int PERIOD = 1000;

        public VoidSpiceRequest() {
            super(Void.class);
        }

        @Override
        public Void loadDataFromNetwork() throws Exception {
            for (int i = 0; i < MAX; i++) {
                publishProgress(i);
                try {
                    Thread.sleep(PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
