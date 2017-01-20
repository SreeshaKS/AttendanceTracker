package com.sreesha.android.attendancetracker.DashBoardClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceInstance;
import com.sreesha.android.attendancetracker.DataHandlers.CursorRecyclerViewAdapter;
import com.sreesha.android.attendancetracker.R;

/**
 * Created by Sreesha on 19-01-2017.
 */

public class AttendanceAdapter extends CursorRecyclerViewAdapter<AttendanceAdapter.ViewHolder> {

    public AttendanceAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_attendee_item, parent, false);
        if (c != null)
            c = view.getContext();
        return new ViewHolder(view);
    }

    AttendanceInstance currentInstance;
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
    }

    static Context c;
    ContentValues currentObjectValues;
    InfiniteJobCruncher t = new InfiniteJobCruncher();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView userIDTV;
        TextView userNameTV;
        CheckBox IsLateCheckBox;

        Button presentB;
        Button absentB;
        Button unknownB;
        Button medicalB;


        Button noteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            c = itemView.getContext();
            userIDTV = (TextView) itemView.findViewById(R.id.userIDTV);
            userNameTV = (TextView) itemView.findViewById(R.id.userNameTV);
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
            /*itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);*/
        }

        private void showNoteDialog(Context c, String previousNote) {
            new MaterialDialog.Builder(c)
                    .title("Take Notes")
                    .content(previousNote)
                    .inputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                            InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .inputRange(2, 100)
                    .positiveText("Submit")
                    .input("Write some Funky Stuff", previousNote, new MaterialDialog.InputCallback() {
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

        View.OnClickListener checkBoxClickL = new View.OnClickListener() {
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

        View.OnClickListener mIVCLickListener
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

    private boolean canThreadRun = false;

    class InfiniteJobCruncher extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            for (; !isCancelled(); ) {
                Log.d("Infinite Thread", "Started and Looping");
                if (canThreadRun) {
                    Log.d("Thread Running", "User ID : " + currentInstance.getUserID() + "\nAttType" +
                            currentInstance.getAttendanceType());
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
