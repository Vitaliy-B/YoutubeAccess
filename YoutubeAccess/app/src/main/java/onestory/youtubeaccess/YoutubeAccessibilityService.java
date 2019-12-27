package onestory.youtubeaccess;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class YoutubeAccessibilityService extends AccessibilityService {
    public static final String ACTION_RESET = "ACTION_RESET";

    private static final String TAG = "o_ya";

    private boolean searchClicked;
    private boolean searchInput;
    private boolean searchStarted;
    private boolean backPressed;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!searchClicked) {
            clickSearch(accessibilityEvent);
        } else if (!searchInput) {
            inputSearch(accessibilityEvent);
        } else if (!searchStarted) {
            startSearch(accessibilityEvent);
        } else if (!backPressed) {
            backPressed = true;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            performGlobalAction(GLOBAL_ACTION_BACK);
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    private void clickSearch(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo ani = accessibilityEvent.getSource();
        if (ani == null) {
            return;
        }

        AccessibilityNodeInfo aniParent = ani.getParent();
        if (aniParent == null || !ViewGroup.class.getName().contentEquals(aniParent.getClassName())
                || aniParent.getChildCount() < 3) {

            return;
        }

        for (int child = 0; child < 3; child++) {
            AccessibilityNodeInfo aniChild = aniParent.getChild(child);
            if (aniChild == null || !ImageView.class.getName().contentEquals(aniChild.getClassName())) {
                return;
            }

            aniChild.recycle();
        }

        Log.d(TAG, "clickSearch");
        Log.d(TAG, "ContentDescription: " + aniParent.getChild(2).getContentDescription());
        Log.d(TAG, "performAction: "
                + aniParent.getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK));

        ani.recycle();
        aniParent.recycle();

        searchClicked = true;
    }

    private void inputSearch(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "inputSearch");

        if (accessibilityEvent.getEventType() != AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            return;
        }

        AccessibilityNodeInfo ani = accessibilityEvent.getSource();
        if (ani == null || !EditText.class.getName().contentEquals(ani.getClassName())) {
            return;
        }

        Bundle bundle = new Bundle(1);
        bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "test");
        ani.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);

        ani.recycle();

        searchInput = true;
    }

    private void startSearch(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "startSearch");

        AccessibilityNodeInfo ani = accessibilityEvent.getSource();
        if (ani == null || !ListView.class.getName().contentEquals(ani.getClassName())
                || ani.getChildCount() < 1) {
            return;
        }

        AccessibilityNodeInfo aniChild = ani.getChild(0);
        Log.d(TAG, "aniChild: " + aniChild);
        aniChild.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        ani.recycle();
        aniChild.recycle();

        searchStarted = true;
    }

    @Override
    public void onInterrupt() {
        Log.w(TAG, "onInterrupt");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_RESET.equals(intent.getAction())) {
            Log.i(TAG, "ACTION_RESET");
            searchClicked = false;
            searchInput = false;
            searchStarted = false;
            backPressed = false;
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
