package egydio.camila.galeria;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
        @NonNull

        MainActivity mainActivity;
        List<String> photos;

        public MainAdapter(MainActivity mainActivity, List<String>photos) {
            this.mainActivity = mainActivity;
            this.photos = photos;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mainActivity);
            View v = inflater.inflate(R.layout.list_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);

            // dimensões das imagens
            int w = (int) mainActivity.getResources().getDimension(R.dimen.itemWidth);
            int h = (int) mainActivity.getResources().getDimension(R.dimen.itemHeight);

            // carrega a imagem para definir do tamanho da imageview
            Bitmap bitmap = Util.getBitmap(photos.get(position),w,h);
            imPhoto.setImageBitmap(bitmap);

            // define a função ao clicar na imagem
            imPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.startPhotoActivity(photos.get(position));
                }
            });

        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }
