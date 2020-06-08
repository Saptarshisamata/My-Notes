package com.saptarshi.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.saptarshi.mynotes.data.notesContract.HIGH_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.LOW_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.MEDIUM_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry.COLUMN_DATE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry.COLUMN_IMPORTANCE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry.COLUMN_NOTE;
import static com.saptarshi.mynotes.data.notesContract.notesEntry._ID;

public class noteAdapter extends RecyclerView.Adapter<noteAdapter.notesViewHolder> {

    private OnItemClickListener  mListener ;


    public interface  OnItemClickListener{
        void onItemCLick(int position,long id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private Context context;
    private Cursor mCursor ;

    public noteAdapter(Context context,Cursor cursor){
        this.context = context;
        this.mCursor = cursor;
    }


    public static class notesViewHolder extends RecyclerView.ViewHolder {

        TextView noteText;
        TextView dateText;
        TextView impText;
        long id;
        public notesViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);

            noteText = itemView.findViewById(R.id.note);
            dateText = itemView.findViewById(R.id.date);
            impText = itemView.findViewById(R.id.imp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position  = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemCLick(position,id);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public notesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new notesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes,parent,false),mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull notesViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)){
            return;
        }
        String note = mCursor.getString(mCursor.getColumnIndex(COLUMN_NOTE));
        String date = "Due date:" + mCursor.getString(mCursor.getColumnIndex(COLUMN_DATE));
        int imp = mCursor.getInt(mCursor.getColumnIndex(COLUMN_IMPORTANCE));
        long id_ = mCursor.getLong(mCursor.getColumnIndex(_ID));



        String sImp = null ;
        if (imp == LOW_IMPORTANCE) sImp = "Priority: Low";
        if (imp == MEDIUM_IMPORTANCE) sImp = "Priority: Mid";
        if (imp == HIGH_IMPORTANCE) sImp = "Priority: High";

        holder.noteText.setText(note);
        holder.dateText.setText(date);
        holder.impText.setText(sImp);

        holder.id = id_;
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        if (mCursor != null){
            mCursor.close();
        }
        mCursor = cursor ;
        if (cursor != null){
            notifyDataSetChanged();
        }
    }

}
