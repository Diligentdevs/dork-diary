package com.enigmaticdevs.dorkdiary.ItemAdapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.enigmaticdevs.dorkdiary.Functionality.EditNote;
import com.enigmaticdevs.dorkdiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    List<String> title,note,date,key;
    Context context;
    ImageView item_delete;

    public ItemAdapter(List<String> title, List<String> date,List<String> note,List<String> key,Context context) {
        this.title = title;
        this.note  = note;
        this.date  = date;
        this.key = key;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleview,noteview,dateview;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleview = itemView.findViewById(R.id.item_title);
            noteview  = itemView.findViewById(R.id.item_note);
            dateview  = itemView.findViewById(R.id.item_date);
            item_delete = itemView.findViewById(R.id.item_delete);
            constraintLayout = itemView.findViewById(R.id.row_layout);
        }
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);
        final ViewHolder vh = new ViewHolder(v);
        vh.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditNote.class);
                intent.putExtra("Key",key.get(vh.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
        item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog=new AlertDialog.Builder(context)
                        .setMessage("Do you want to Delete this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key.get(vh.getAdapterPosition())).setValue(null);
                                FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key.get(vh.getAdapterPosition())).delete();
                                key.remove(vh.getAdapterPosition());
                                title.remove(vh.getAdapterPosition());
                                date.remove(vh.getAdapterPosition());
                                note.remove(vh.getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        })
                        .setTitle("Delete?")
                        .create();
                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT);
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#4169E1"));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4169E1"));
                    }
                });
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimaryDark);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        String titles = title.get(position);
        if(titles.length()>20){

            titles = titles.substring(0,20)+"...";
        }
        holder.titleview.setText(titles);
        holder.dateview.setText(date.get(position));
        String notes = note.get(position);
        if(notes.length()>30)
        {
            notes = notes.substring(0,30)+"...";
        }
        holder.noteview.setText(notes);
    }

    @Override
    public int getItemCount() {
        return title.size();
    }
}
