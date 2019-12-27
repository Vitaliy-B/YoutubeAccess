package onestory.youtubeaccess;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AccessibilityDialogFragment extends DialogFragment {

    public AccessibilityDialogFragment() {
        // Required empty public constructor
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.msg_please_enable_accessibility)
                .setPositiveButton(R.string.text_open_settings, (dialogInterface, button) ->
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)))
                .setNegativeButton(R.string.text_exit, (dialogInterface, i) -> onCancel(null))
                .setCancelable(false)
                .create();
    }

    @Override
    public void onCancel(@Nullable DialogInterface dialog) {
        if (dialog != null) {
            super.onCancel(dialog);
        }

        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
}
