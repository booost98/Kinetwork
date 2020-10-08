package com.homer.telemed;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.ImageViewHolder> {

    private Context context;
    private List<Therapist> therapists;
    private OnItemClickListener mListener;

    public TherapistAdapter(Context context, List<Therapist> therapists){
        this.context = context;
        this.therapists = therapists;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.therapist_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Therapist therapistCurrent = therapists.get(position);
        holder.textViewName.setText(therapistCurrent.getName());
        //add volley stringrequest here getting field, location, specialties, and clinic from therapists table
        Picasso.with(context)
                .load(therapistCurrent.getImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .resize(300, 300)
                //.fit()
                //.centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return therapists.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewName, tField, tLocation, tSpecialties, tClinic;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.therapist_name);
            tField = itemView.findViewById(R.id.tField);
            tLocation = itemView.findViewById(R.id.tLocation);
            tSpecialties = itemView.findViewById(R.id.tSpecialties);
            tClinic = itemView.findViewById(R.id.tClinic);
            imageView = itemView.findViewById(R.id.image_view_therapist);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem select_therapist = contextMenu.add(Menu.NONE, 1, 1, "Select This Therapist");
            MenuItem view_info = contextMenu.add(Menu.NONE, 2, 2, "View More Info");

            select_therapist.setOnMenuItemClickListener(this);
            view_info.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()) {
                        case 1:
                            mListener.onSelectClick(position);
                            return true;
                        case 2:
                            mListener.onViewClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);

        void onSelectClick(int position);

        void onViewClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
