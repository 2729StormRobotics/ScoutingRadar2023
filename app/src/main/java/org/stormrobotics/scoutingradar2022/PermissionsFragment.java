package org.stormrobotics.scoutingradar2022;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public abstract class PermissionsFragment extends Fragment {

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(), (isGranted) -> {
                        if (!isGranted.containsValue(false)) {
                            onPermissionsGranted();
                        }
                    }
            );


    private void showPermissionsExplanation() {
        // Build an alert dialog to explain why we need Bluetooth permission
        // If the user grants permission, request them from the system
        new AlertDialog.Builder(getActivity())
                .setMessage(getExplanationDialogMessage())
                .setTitle(getExplanationDialogTitle())
                .setPositiveButton(R.string.proceed,
                        (dialog, which) -> {
                            requestPermissionsLauncher.launch(
                                    getPermissionsToRequest());
                        })
                .setNegativeButton(R.string.cancel,
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .create()
                .show();
    }

    /**
     * Checks if the app has permission to access Bluetooth. If not, checks if a rationale should be
     * displayed. If so, displays a dialog to the user and then requests the permission. Then, if the
     * user has granted the permission, runs {@link #onPermissionsGranted()}.
     *
     * @param context
     */
    protected void checkPermissionsAndAct(@NonNull Context context) {
        // Ensure all permissions are granted
        boolean areAllPermissionsGranted = true;
        for (String permission : getPermissionsToRequest()) {
            areAllPermissionsGranted &= ContextCompat.checkSelfPermission(context, permission) ==
                                        PackageManager.PERMISSION_GRANTED;
        }

        if (areAllPermissionsGranted) {
            onPermissionsGranted();
        } else {
            // Check if any rationales are needed
            boolean shouldShowRationale = false;
            for (String permission : getPermissionsToRequest()) {
                shouldShowRationale |= shouldShowRequestPermissionRationale(permission);
            }

            // Show the rationale if needed
            if (shouldShowRationale) {
                showPermissionsExplanation();
            } else {
                requestPermissionsLauncher.launch(getPermissionsToRequest());
            }
        }
    }

    /**
     * Returns the permissions that the fragment needs to request.
     * @return The permissions that the fragment needs to request.
     */
    protected abstract String[] getPermissionsToRequest();

    /**
     * Called when the permissions have been granted.
     */
    protected abstract void onPermissionsGranted();

    protected abstract String getExplanationDialogTitle();

    protected abstract String getExplanationDialogMessage();
}