package org.com.example.mp3player;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MusicFile file);
    }

    private List<MusicFile> musicFiles;
    private OnItemClickListener listener;

    private int selectedItem = -1;

    public MusicAdapter(List<MusicFile> musicFiles, OnItemClickListener listener) {
        this.musicFiles = musicFiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicFile musicFile = musicFiles.get(position);
        holder.musicTitle.setText(musicFile.getName());
        holder.itemView.setSelected(selectedItem == position);

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Get the current position
            if (currentPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedItem);
                selectedItem = currentPosition;
                notifyItemChanged(selectedItem);
                listener.onItemClick(musicFiles.get(currentPosition));
            }
        });

        if(selectedItem == position){
            holder.musicTitle.setBackgroundColor(Color.GREEN);
        } else {
            holder.musicTitle.setBackgroundColor(Color.BLACK);
        }
    }


    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView musicTitle;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            musicTitle = itemView.findViewById(R.id.musicTitle);
        }
    }

}
