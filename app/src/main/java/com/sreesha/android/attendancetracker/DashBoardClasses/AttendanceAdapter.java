package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceDBHelper;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceInstance;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.R;
import com.sreesha.android.attendancetracker.Utility;

import java.util.ArrayList;

/**
 * Created by Sreesha on 19-01-2017.
 */

public class AttendanceAdapter extends CursorRecyclerViewAdapter<AttendanceAdapter.ViewHolder> {
    private ArrayList<AttendanceInstance> selectedList;
    private ArrayList<String> selectedObjectStringDescriptorList;

    public AttendanceAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        selectedList = new ArrayList<>();
        selectedObjectStringDescriptorList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_attendee_item, parent, false);
        if (c != null)
            c = view.getContext();
        return new ViewHolder(view);
    }

    private AttendanceInstance currentInstance;
    public boolean shouldCheckBeComited = false;

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        currentInstance = AttendanceInstance.getEventFromCursor(cursor);
        Log.d("Thread", "OnBindView : After Adapter Change : " + "Attendance Type " + currentInstance.getAttendanceType());
        holder.userIDTV.setText(currentInstance.getUserID());
        if (currentInstance.getIsLate() == 1) {
            shouldCheckBeComited = false;
            holder.IsLateCheckBox.setChecked(true);
        } else {
            shouldCheckBeComited = false;
            holder.IsLateCheckBox.setChecked(false);
        }
        if (currentInstance.getNote() != null
                && !currentInstance.getNote().isEmpty()) {
            holder.noteButton.setSelected(true);
        } else {
            holder.noteButton.setSelected(false);

        }
        Log.d("Attendance Type", "Attendance Type : " + currentInstance.getAttendanceType());
        switch (currentInstance.getAttendanceType()) {
            case AttendanceContract.InstanceAttendance.TYPE_PRESENT:
                Log.d("ButtonState", "Changing Button State");
                holder.presentB.setSelected(true);
                holder.absentB.setSelected(false);
                holder.unknownB.setSelected(false);
                holder.medicalB.setSelected(false);
                break;
            case AttendanceContract.InstanceAttendance.TYPE_ABSENT:
                holder.absentB.setSelected(true);
                holder.presentB.setSelected(false);
                holder.unknownB.setSelected(false);
                holder.medicalB.setSelected(false);
                break;
            case AttendanceContract.InstanceAttendance.TYPE_UNAVAILABLE:
                holder.unknownB.setSelected(true);
                holder.presentB.setSelected(false);
                holder.absentB.setSelected(false);
                holder.medicalB.setSelected(false);
                break;
            case AttendanceContract.InstanceAttendance.TYPE_MEDICAL_LEAVE:
                holder.medicalB.setSelected(true);
                holder.presentB.setSelected(false);
                holder.absentB.setSelected(false);
                holder.unknownB.setSelected(false);
                break;
        }
        if (selectedObjectStringDescriptorList.contains(currentInstance.toString())) {
            holder.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
        } else {
            holder.selectionIndicatorLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        Utility.loadImage(holder.itemView.getContext(), holder.userProfileIV, R.drawable.ic_face_profile_grey600_36dp);
    }

    private Context c;
    private ContentValues currentObjectValues;
    private InfiniteJobCruncher t = new InfiniteJobCruncher();

    private boolean isInSelectionMode = false;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView userIDTV;
        TextView userNameTV;
        CheckBox IsLateCheckBox;

        Button presentB;
        Button absentB;
        Button unknownB;
        Button medicalB;


        Button noteButton;

        ImageView userProfileIV;

        RelativeLayout selectionIndicatorLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            c = itemView.getContext();

            selectionIndicatorLayout
                    = (RelativeLayout) itemView.findViewById(R.id.backGroundLayout);

            userIDTV = (TextView) itemView.findViewById(R.id.userIDTV);
            userNameTV = (TextView) itemView.findViewById(R.id.userNameTV);
            userProfileIV = (ImageView) itemView.findViewById(R.id.userProfileIV);

            IsLateCheckBox = (CheckBox) itemView.findViewById(R.id.isLateCheckBox);

            noteButton = (Button) itemView.findViewById(R.id.noteButton);

            IsLateCheckBox.setOnClickListener(checkBoxClickL);

            presentB = (Button) itemView.findViewById(R.id.presentIV);
            absentB = (Button) itemView.findViewById(R.id.absentIV);
            unknownB = (Button) itemView.findViewById(R.id.unknownIV);
            medicalB = (Button) itemView.findViewById(R.id.medicalIV);

            presentB.setOnClickListener(mIVCLickListener);
            absentB.setOnClickListener(mIVCLickListener);
            unknownB.setOnClickListener(mIVCLickListener);
            medicalB.setOnClickListener(mIVCLickListener);
            noteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    String note = (AttendanceInstance.getEventFromCursor(c))
                            .getNote();
                    if (note == null)
                        note = " ";
                    showNoteDialog(view.getContext(), note);

                }
            });
            itemView.setOnClickListener(selectionClickListener);
            itemView.setOnLongClickListener(selectionLongClickListener);

        }


        private View.OnClickListener selectionClickListener
                = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Selection", "Triggering OnClick Selection Mode : " + isInSelectionMode);
                if (isInSelectionMode) {
                    Log.d("Selection", "In Selection Mode");
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    AttendanceInstance aI
                            = AttendanceInstance.getEventFromCursor(c);
                    if (selectedObjectStringDescriptorList.contains(aI.toString())) {
                        Log.d("Selection", "Removing Object");
                        removeFromSelectionList(aI);
                        ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.TRANSPARENT);
                        if (selectedList.isEmpty()) {
                            Log.d("Selection", "NO More Selections / Returning back to Non-Selection Mode");
                            isInSelectionMode = false;
                            if (mGlobalSelectionEventNotifier != null)
                                mGlobalSelectionEventNotifier.OnSelectionStateChanged(false);
                        }
                    } else if (selectedList.isEmpty()) {
                        Log.d("Selection", "NO More Selections / Returning back to Non-Selection Mode");
                        isInSelectionMode = false;
                        if (mGlobalSelectionEventNotifier != null)
                            mGlobalSelectionEventNotifier.OnSelectionStateChanged(false);
                    } else {
                        Log.d("Selection", "NOt Selected / Adding to selection");
                        ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
                        addToSelectionList(aI);
                    }
                } else {
                    Log.d("Selection", "Not In Selection Mode / Long Click First Please");
                }
            }
        };
        private View.OnLongClickListener selectionLongClickListener
                = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("Selection", "Triggering OnLongClick");
                if (!isInSelectionMode) {
                    if (mGlobalSelectionEventNotifier != null) {
                        mGlobalSelectionEventNotifier.OnSelectionStateChanged(true);
                    }
                    isInSelectionMode = true;
                    Log.d("Selection", "Changing to Selection mode Selection Mode : " + isInSelectionMode);
                    Cursor c = getCursor();
                    c.moveToPosition(getAdapterPosition());
                    addToSelectionList(
                            AttendanceInstance.getEventFromCursor(c)
                    );
                    ViewHolder.this.selectionIndicatorLayout.setBackgroundColor(Color.GREEN);
                } else {
                    Log.d("Selection", "Already in Selection mode");
                }
                return true;
            }
        };

        private void addToSelectionList(AttendanceInstance i) {
            selectedList.add(i);
            selectedObjectStringDescriptorList.add(i.toString());
            if (mGlobalSelectionEventNotifier != null)
                mGlobalSelectionEventNotifier.OnSelectionEventTriggered(selectedList.size());
        }

        private void removeFromSelectionList(AttendanceInstance instance) {
            AttendanceInstance temp = null;
            for (AttendanceInstance i : selectedList) {
                if (i.toString().equals(instance.toString())) {
                    temp = i;

                }
            }
            if (temp != null) {
                selectedList.remove(temp);
            }
            selectedObjectStringDescriptorList.remove(instance.toString());
            if (mGlobalSelectionEventNotifier != null)
                mGlobalSelectionEventNotifier.OnSelectionEventTriggered(selectedList.size());
        }


        private void showNoteDialog(Context c, String previousNote) {
            new MaterialDialog.Builder(c)
                    .title(R.string.take_notes)
                    .content(previousNote)
                    .inputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                            InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .inputRange(2, 100)
                    .positiveText(R.string.submit)
                    .input(c.getString(R.string.write_some_funky_stuff), previousNote, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            checkAndExecuteJobCruncher();

                            Cursor c = getCursor();
                            c.moveToPosition(getAdapterPosition());
                            currentInstance = AttendanceInstance.getEventFromCursor(c);
                            currentInstance
                                    .setNotes(input.toString());
                            Log.d("Thread", "Comiting Note Data : " + currentInstance.getNote());
                            currentObjectValues = AttendanceInstance.getContentValues(currentInstance);
                            canThreadRun = true;
                            c.moveToLast();
                        }
                    }).build().show();
        }

        private View.OnClickListener checkBoxClickL = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkAndExecuteJobCruncher();

                Cursor c = getCursor();

                c.moveToPosition(getAdapterPosition());
                currentInstance = AttendanceInstance.getEventFromCursor(c);
                if (IsLateCheckBox.isChecked()) {
                    currentInstance.setIsLate(1);
                } else {
                    currentInstance.setIsLate(0);
                }
                currentObjectValues = AttendanceInstance.getContentValues(currentInstance);
                canThreadRun = true;
            }
        };

        private View.OnClickListener mIVCLickListener
                = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = getCursor();

                checkAndExecuteJobCruncher();

                c.moveToPosition(getAdapterPosition());
                currentInstance = AttendanceInstance.getEventFromCursor(c);


                switch (view.getId()) {
                    case R.id.presentIV:
                        currentInstance
                                .setAttendanceType(AttendanceContract
                                        .InstanceAttendance
                                        .TYPE_PRESENT);
                        presentB.setSelected(true);
                        break;
                    case R.id.absentIV:
                        currentInstance
                                .setAttendanceType(AttendanceContract
                                        .InstanceAttendance
                                        .TYPE_ABSENT);
                        absentB.setSelected(true);
                        break;
                    case R.id.unknownIV:
                        currentInstance
                                .setAttendanceType(AttendanceContract
                                        .InstanceAttendance
                                        .TYPE_UNAVAILABLE);
                        unknownB.setSelected(true);
                        break;
                    case R.id.medicalIV:
                        currentInstance
                                .setAttendanceType(AttendanceContract
                                        .InstanceAttendance
                                        .TYPE_MEDICAL_LEAVE);
                        medicalB.setSelected(true);
                        break;

                }
                currentObjectValues = AttendanceInstance.getContentValues(currentInstance);
                Log.d("Thread", "OnClick" + "Attendance Type " + currentInstance.getAttendanceType());
                canThreadRun = true;
            }
        };

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }

        private void checkAndExecuteJobCruncher() {
            try {
                if (!t.isCancelled())
                    t.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SelectionEventNotifier mGlobalSelectionEventNotifier;

    public void clearSelectionList() {
        selectedList.clear();
        selectedObjectStringDescriptorList.clear();

        isInSelectionMode = false;
        if (mGlobalSelectionEventNotifier != null) {
            mGlobalSelectionEventNotifier.OnSelectionStateChanged(false);
            mGlobalSelectionEventNotifier.OnSelectedDeleteRequestComplete();
        }

        notifyDataSetChanged();
    }

    public void notifySelectionDeleteRequest(final Context c) {
        AsyncTask asyncTask = new AsyncTask<Object, Object, Object>() {
            SQLiteDatabase db;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AttendanceDBHelper a
                        = new AttendanceDBHelper(c);
                db = a.getReadableDatabase();
            }

            @Override
            protected String[] doInBackground(Object... params) {
                ArrayList<AttendanceInstance>
                        list = AttendanceAdapter.this.getSelectionList();

                for (AttendanceInstance i : list) {
                    db.delete(
                            AttendanceContract.InstanceAttendance.TABLE_INSTANCE_ATTENDANCE

                            , AttendanceContract.InstanceAttendance.column_eventId + " = ? AND "
                                    + AttendanceContract.InstanceAttendance.column_instanceId + " = ? AND "
                                    + AttendanceContract.InstanceAttendance.column_userId + " = ? "

                            , new String[]{
                                    i.getEventID()
                                    , i.getInstanceID()
                                    , i.getUserID()
                            }
                    );
                }
                list.clear();
                return null;
            }

            @Override
            protected void onPostExecute(Object s) {
                super.onPostExecute(s);
                db.close();
                clearSelectionList();
            }
        };
        asyncTask.execute(null, null, null);
    }

    public ArrayList<AttendanceInstance> getSelectionList() {
        return selectedList;
    }

    public void registerSelectionEventNotifier(SelectionEventNotifier mGlobalSelectionEventNotifier) {
        this.mGlobalSelectionEventNotifier = mGlobalSelectionEventNotifier;
    }

    public void unRegisterSelectionEventNotifier() {
        this.mGlobalSelectionEventNotifier = null;
    }

    interface SelectionEventNotifier {
        void OnSelectionEventTriggered(int count);

        void OnSelectionStateChanged(boolean isInSelectionMode);
        void OnSelectedDeleteRequestComplete();
    }

    private boolean canThreadRun = false;

    private class InfiniteJobCruncher extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            Log.d("Infinite Thread", "Started and Looping Code : " + this.hashCode());
            for (; !isCancelled(); ) {

                if (canThreadRun) {
                    c.getContentResolver()
                            .update(
                                    AttendanceContract.InstanceAttendance.CONTENT_URI
                                    , currentObjectValues
                                    , AttendanceContract.InstanceAttendance.column_eventId + " = ? AND "
                                            + AttendanceContract.InstanceAttendance.column_instanceId + " = ? AND "
                                            + AttendanceContract.InstanceAttendance.column_userId + " = ? "
                                    , new String[]{
                                            currentInstance.getEventID()
                                            , currentInstance.getInstanceID()
                                            , currentInstance.getUserID()
                                    }
                            );
                    canThreadRun = false;
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            c = null;
        }
    }

    public void stopJobCruncher() {
        if (t != null) {
            if (t.isCancelled())
                t = null;
            else {
                t.cancel(false);
                t = null;
            }
        }
    }
}
