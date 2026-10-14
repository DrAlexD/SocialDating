package xelagurd.socialdating.client

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // the typed credentials are offered to be saved by the autofill service of the device, its dialog pauses
        // the tested screen and stays after the tests, so the autofill is disabled for the whole test activity
        window.decorView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }
}
