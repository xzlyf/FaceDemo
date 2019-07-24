package dc.sg.zncard.com.camerademo2.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dc.sg.zncard.com.camerademo2.R;
import dc.sg.zncard.com.camerademo2.sql.LitePalUtils;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private Context mContext;
    private List<String> tokenlist = new ArrayList<>();

    public PhotoAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_photo, null));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder viewHolder, int i) {

        String realPath = "file://" + LitePalUtils.searchData(tokenlist.get(i));

        Picasso.get().load(Uri.parse(realPath)).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return tokenlist.size();
    }

    public void refresh(List<String> tokenlist) {
        this.tokenlist = tokenlist;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_view);
        }
    }
}
