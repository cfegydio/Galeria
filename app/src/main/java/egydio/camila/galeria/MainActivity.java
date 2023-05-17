package egydio.camila.galeria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<String> photos = new ArrayList<>();

    MainAdapter mainAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // obtem tbMain e indica que deve ser a ActionBar padrão
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        // acesso ao diretório para ler as fotos e adicionar a lista
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++){
            photos.add(files[i].getAbsolutePath());
        }

        // cria o mainadapter e seta no recycleview
        mainAdapter = new MainAdapter(MainActivity.this,photos);
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        // calcula quantas fotos cabem na coluna
        float w = getResources().getDimension(R.id.itemWidth);
        int numberOfColumns = Utils.calculateNoOfColumns(MainActivity.this,w);

        // exibe as fotos repeitando o espaço calculado
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);
        rvGallery.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // cria as opçoes de menu e adiciona no menu da activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // executa a camera no celular assim que o toolbar da camera for selecionado
        switch (item.getItemId()) {
            case R.id.opCamera:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}