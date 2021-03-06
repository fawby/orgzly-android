package com.orgzly.android.provider.actions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orgzly.android.NotePosition;
import com.orgzly.android.provider.DatabaseUtils;
import com.orgzly.android.provider.ProviderContract;
import com.orgzly.android.provider.models.DbNote;
import com.orgzly.android.ui.Placement;

public class PromoteNotesAction implements Action {
    private long bookId;
    private String ids;

    public PromoteNotesAction(ContentValues values) {
        bookId = values.getAsLong(ProviderContract.Promote.Param.BOOK_ID);
        ids = values.getAsString(ProviderContract.Promote.Param.IDS);
    }

    @Override
    public int run(SQLiteDatabase db) {
        int result = promote(db);

        DatabaseUtils.updateBookMtime(db, bookId);

        return result;
    }

    private int promote(SQLiteDatabase db) {
        Cursor cursor;
        NotePosition note;
        long parent;
        ContentValues values;


        /* Get note info. */
        cursor = db.query(DbNote.TABLE, null, DbNote.Column._ID + " IN (" + ids + ")", null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                note = DbNote.positionFromCursor(cursor);
            } else {
                return 0;
            }
        } finally {
            cursor.close();
        }

        /* Can't promote top level note. */
        if (note.getLevel() <= 1 || note.getParentId() <= 0) {
            return 0;
        }

        /* Cut note and all its descendants. */
        long batchId = System.currentTimeMillis();
        values = new ContentValues();
        values.put(DbNote.Column.IS_CUT, batchId);
        db.update(DbNote.TABLE, values, DatabaseUtils.whereDescendantsAndNote(bookId, note.getLft(), note.getRgt()), null);

        /* Paste below parent. */
        values = new ContentValues();
        values.put(ProviderContract.Paste.Param.BATCH_ID, batchId);
        values.put(ProviderContract.Paste.Param.BOOK_ID, bookId);
        values.put(ProviderContract.Paste.Param.NOTE_ID, note.getParentId());
        values.put(ProviderContract.Paste.Param.SPOT, Placement.BELOW.toString());
        new PasteNotesAction(values).run(db);

        DatabaseUtils.updateBookMtime(db, bookId);

        return 1;
    }

    @Override
    public void undo() {

    }
}
