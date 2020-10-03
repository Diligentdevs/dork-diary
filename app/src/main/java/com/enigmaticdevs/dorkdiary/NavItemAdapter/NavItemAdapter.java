package com.enigmaticdevs.dorkdiary.NavItemAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.enigmaticdevs.dorkdiary.Account.StartupActivity;
import com.enigmaticdevs.dorkdiary.MainActivity;
import com.enigmaticdevs.dorkdiary.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;


public class NavItemAdapter extends RecyclerView.Adapter<NavItemAdapter.ViewHolder> {
    List<String> menu;
    List<Integer> icons;
    Context context;
    SharedPreferences sharedPreferences;

    public NavItemAdapter(List<String> menu,List<Integer> icons, Context context) {
        this.menu = menu;
        this.icons = icons;
        this.context = context;
    }
    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView menu_name;
        ImageView menu_image;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menu_name = itemView.findViewById(R.id.menu_item_name);
            menu_image= itemView.findViewById(R.id.menu_item_image);
            constraintLayout = itemView.findViewById(R.id.item_layout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.menu_list_item,parent,false);
        final ViewHolder vh = new ViewHolder(v);
        sharedPreferences=context.getSharedPreferences("com.enigmaticdevs.dorkdiary",Context.MODE_PRIVATE);
        vh.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(menu.get(vh.getAdapterPosition())){
                    case "Logout":
                    {
                        final AlertDialog dialog=new AlertDialog.Builder(context)
                                .setMessage("Do you want to logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().signOut();
                                        sharedPreferences.edit().putString("passcode","").apply();
                                        Intent intent = new Intent(context, StartupActivity.class);
                                        context.startActivity(intent);
                                        ((MainActivity)context).finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                })
                                .setTitle("Logout?")
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
                       break;
                    }
                    case "Rate App":
                    {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.enigmaticdevs.dorkdiary")));
                        break;
                    }
                    case "Share":
                    {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Take a look at this awesome Dork Diary I found on play store https://play.google.com/store/apps/details?id=com.enigmaticdevs.dorkdiary ";
                        String shareSub = "Dork Diary";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        break;
                    }
                    }
                }

        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.menu_name.setText(menu.get(position));
       holder.menu_image.setImageResource(icons.get(position));
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }


}
