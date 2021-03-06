package com.orgzly.android.ui;

import android.os.Bundle;
import android.view.View;

import com.orgzly.R;

import java.util.TreeSet;

public class Selection {
    private static final String SAVED_BUNDLE_KEY = "list_of_selected_ids";

    /** IDs of selected notes. */
    private TreeSet<Long> mSelectedIds = new TreeSet<>();

    public void updateView(View view, long noteId) {
        if (mSelectedIds.contains(noteId)) {
            view.setBackgroundResource(R.color.item_head_selected_bg);
        } else {
            view.setBackgroundResource(0);
        }
    }

    public int getCount() {
        return mSelectedIds.size();
    }

    public TreeSet<Long> getIds() {
        return mSelectedIds;
    }

    public boolean contains(long noteId) {
        return mSelectedIds.contains(noteId);
    }

    public void select(long noteId) {
        mSelectedIds.add(noteId);
    }

    public void deselect(long noteId) {
        mSelectedIds.remove(noteId);
    }

    public void toggle(View view, long noteId) {
        if (contains(noteId)) {
            deselect(noteId);
        } else {
            select(noteId);
        }

        if (view != null) {
            updateView(view, noteId);
        }
    }

    public void clearSelection() {
        mSelectedIds.clear();
    }

    /**
     * Save selected items.
     * Restored with {@link Selection#restoreIds(android.os.Bundle)}.
     */
    public void saveIds(Bundle bundle) {
        if (getCount() > 0) {
            long[] idsArray = new long[mSelectedIds.size()];
            int i = 0;
            for (long id: mSelectedIds) {
                idsArray[i++] = id;
            }
            bundle.putLongArray(SAVED_BUNDLE_KEY, idsArray);
        } else {
            bundle.remove(SAVED_BUNDLE_KEY);
        }
    }

    /**
     * Restore selected items.
     * Saved with {@link Selection#saveIds(android.os.Bundle)}.
     */
    public void restoreIds(Bundle bundle) {
        mSelectedIds.clear();

        if (bundle != null && bundle.containsKey(SAVED_BUNDLE_KEY)) {
            long[] ids = bundle.getLongArray(SAVED_BUNDLE_KEY);
            if (ids != null) {
                for (long id : ids) {
                    mSelectedIds.add(id);
                }
            }
        }
    }
}
