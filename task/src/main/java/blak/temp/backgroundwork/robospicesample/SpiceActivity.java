package blak.temp.backgroundwork.robospicesample;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;

import android.support.v4.app.FragmentActivity;

public class SpiceActivity extends FragmentActivity {
    protected SpiceManager mSpiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        mSpiceManager.start(this);
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }
}
