package onestory.youtubeaccess;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "o_ya";
    private static final String PACKAGE_YOUTUBE = "com.google.android.youtube";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout testYoutube = findViewById(R.id.linlay_youtube);
        testYoutube.setOnClickListener(view -> {
            Intent acsSrv = new Intent(YoutubeAccessibilityService.ACTION_RESET, null,
                    this, YoutubeAccessibilityService.class);
            startService(acsSrv);

            Intent intentOpenYoutube = getPackageManager().getLaunchIntentForPackage(PACKAGE_YOUTUBE);
            if (intentOpenYoutube == null) {
                Toast.makeText(this, R.string.msg_youtube_not_installed, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            startActivity(intentOpenYoutube);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isAccessibilityServiceEnabled()) {
            new AccessibilityDialogFragment().show(getSupportFragmentManager(),
                    AccessibilityDialogFragment.class.getSimpleName());
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        String acsServices = getAccessibilityServicesFromManager();
        if (acsServices.isEmpty()) {
            acsServices = getAccessibilityServicesFromSettings();
        }

        if (acsServices.contains(getPackageName())) {
            Log.i(TAG, "Accessibility Service enabled");
            return true;
        } else {
            Log.d(TAG, getPackageName() + " NOT in " + acsServices);
            return false;
        }
    }

    @NonNull
    private String getAccessibilityServicesFromManager() {
        AccessibilityManager acsMngr = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (acsMngr != null) {
            List<AccessibilityServiceInfo> acsSrvsList = acsMngr.getEnabledAccessibilityServiceList(
                    AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

            Log.d(TAG, "AccessibilityServiceList: " + acsSrvsList);
            if (acsSrvsList.isEmpty()) {
                return "";
            }

            StringBuilder idsStr = new StringBuilder();
            for (AccessibilityServiceInfo asi : acsSrvsList) {
                idsStr.append(asi.getId()).append(" ");
            }

            return idsStr.toString();
        }

        return "";
    }

    @NonNull
    private String getAccessibilityServicesFromSettings() {
        try {
            int acsEnabledCode = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);

            if (acsEnabledCode == 1) { // enabled
                String servicesStr = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

                Log.d(TAG, "ENABLED_ACCESSIBILITY_SERVICES: " + servicesStr);

                return (servicesStr == null) ? "" : servicesStr;
            }
        } catch (Settings.SettingNotFoundException snfe) {
            Log.w(TAG, snfe);
        }

        return "";
    }
}
