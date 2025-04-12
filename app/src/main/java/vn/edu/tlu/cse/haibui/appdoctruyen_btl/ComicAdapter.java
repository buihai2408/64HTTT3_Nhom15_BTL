package vn.edu.tlu.cse.haibui.appdoctruyen_btl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ComicVH> implements Filterable {
    ArrayList<Comic> comics;
    ArrayList<Comic> comicold;
    private int currentUserId;
    private Context context;

    public ComicAdapter(ArrayList<Comic> comics, int userId, Context context) {
        this.comics = comics;
        this.comicold = comics;
        this.currentUserId = userId;
        this.context = context;
    }

    @NonNull
    @Override
    public ComicVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_row, parent, false);
        return new ComicVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicVH holder, int position) {
        ComicVH Myviewholder = (ComicVH) holder;
        Comic comic = comics.get(position);
        Glide.with(holder.imgComic.getContext()).load(comic.getImageLink()).into(holder.imgComic);
        holder.txName.setText(comic.getName());
        holder.txAuthor.setText(comic.getAuthor());
        holder.txDesc.setText(comic.getDescription());

        DbHelper db = new DbHelper(holder.imgComic.getContext());
        boolean isFavorite = db.isFavoriteComic(currentUserId, comic.getId());
        holder.imgFav.setImageResource(isFavorite ? R.drawable.like : R.drawable.unlike);

        holder.txName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Chitiet.class);
                intent.putExtra("id", comic.getId());
                view.getContext().startActivity(intent);
            }
        });

        holder.imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper db = new DbHelper(view.getContext());
                boolean isFavorite = db.isFavoriteComic(currentUserId, comic.getId());

                if (isFavorite) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                    alertDialog.setTitle("Xác nhận");
                    alertDialog.setMessage("Bạn có muốn xóa truyện khỏi yêu thích?");
                    alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.removeFavoriteComic(currentUserId, comic.getId());
                            holder.imgFav.setImageResource(R.drawable.unlike);
                            notifyDataSetChanged();
                        }
                    });
                    alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                } else {
                    db.addFavoriteComic(currentUserId, comic.getId());
                    holder.imgFav.setImageResource(R.drawable.like);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    class ComicVH extends RecyclerView.ViewHolder {
        ImageView imgComic, imgFav;
        TextView txName, txAuthor, txDesc;

        public ComicVH(@NonNull View itemView) {
            super(itemView);
            imgComic = itemView.findViewById(R.id.imgComic);
            imgFav = itemView.findViewById(R.id.imgFav);
            txName = itemView.findViewById(R.id.txName);
            txAuthor = itemView.findViewById(R.id.txAuthor);
            txDesc = itemView.findViewById(R.id.txDesc);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    comics = comicold;
                } else if (strSearch.equals("FAVORITE")) {
                    ArrayList<Comic> list = new ArrayList<>();
                    DbHelper db = new DbHelper(context);
                    for (Comic comic : comicold) {
                        if (db.isFavoriteComic(currentUserId, comic.getId())) {
                            list.add(comic);
                        }
                    }
                    comics = list;
                } else {
                    ArrayList<Comic> list = new ArrayList<>();
                    for (Comic comic : comicold) {
                        if (comic.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(comic);
                        }
                    }
                    comics = list;
                }
                FilterResults filterresults = new FilterResults();
                filterresults.values = comics;
                return filterresults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                comics = (ArrayList<Comic>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
