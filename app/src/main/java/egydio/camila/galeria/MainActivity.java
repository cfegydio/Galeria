package egydio.camila.galeria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ActionMenuView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<String> photos = new ArrayList<>();
    static int RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;

    MainAdapter mainAdapter;
    String currentPhotoPath;
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
        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this,w);

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

    public void startPhotoActivity(String photoPath){

        // passado para photoactivity quando clica a foto
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }

    private void dispatchTakePictureIntent(){

        // criação de arquivo vazio dentro da pasta
        File f = null;
        try {
            f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // guarda o local da foto que esta manipulada
        currentPhotoPath = f.getAbsolutePath();

        // o URI é passado pra camera por via da intent e entao a app é iniciada
        if (f != null) {
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "egydio.camila.galeria.fileprovider", f);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }

    }

    private File createImageFile() throws IOException {

        // cria o arquivo que armazena a imagem e organiza de acordo com data e hora
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }

    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
          super.onActivityResult(requestCode, resultCode, data);
          // foto adicionada na lista e o adapter é atualizado
          if (requestCode == RESULT_TAKE_PICTURE) {
              if(resultCode == Activity.RESULT_OK){
                  photos.add(currentPhotoPath);
                  mainAdapter.notifyItemInserted(photos.size()-1);
              }

              // caso ela nao tenha sido tirada o arquivo é excluido
              else{
                  File f = new File(currentPhotoPath);
                  f.delete();
              }
          }
        }

        private void checkForPermissions(List<String> permissions) {

        // lista de permissoes
        List<String> permissionsNotGranted = new ArrayList<>();

        // uma lista das permissoes ainda nao autorizadas
        for (String permission : permissions){
            if( !hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(permissionsNotGranted.size() > 0) {
                // pedir permissoes ainda nao concedidas
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }

    }

    private boolean hasPermission(String permission) {
        // verifica se a permissao ja foi autorizada pelo usuario
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        // metodo chamado após conceder as permissoes
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        // verifica se cada permissao foi autorizada
        if(requestCode == RESULT_REQUEST_PERMISSION) {

            for(String permission : permissions) {
                if ( !hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        if (permissionsRejected.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0)));
            }

            // avisa o usuario que a permissao é necessaria para o funcionamento da app
            new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                // pede as permissoes novamente
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                }
            }).create().show();

        }
    }


}