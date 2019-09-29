package com.izv.dam.aadcurso1920app1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.izv.dam.aadcurso1920app1.data.Contacto;
import com.izv.dam.aadcurso1920app1.modelo.ContactReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "XYZZYX";
    private static final int PERMISO_CONTACTOS = 1;
    private static final int PERMISO_ARCHIVOS = 2;

    private Button btLeerArchivo, btEscribirArchivo;
    private EditText etNombreArchivo;
    private RadioGroup rgTipoArchivo;
    private TextView tvResultado, tvResultadoOperacion;

    private boolean write = false;
    private int option;
    private List<Contacto> lista;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case PERMISO_ARCHIVOS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readwriteFile();
                }
                break;
            case PERMISO_CONTACTOS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactsList();
                }
                break;
            default:
                ;
        }
    }

    private void initComponents() {
        btEscribirArchivo = findViewById(R.id.btEscribirArchivo);
        btLeerArchivo = findViewById(R.id.btLeerArchivo);
        etNombreArchivo = findViewById(R.id.etNombreArchivo);
        rgTipoArchivo = findViewById(R.id.rgTipoArchivo);
        tvResultado = findViewById(R.id.tvResultado);
        tvResultadoOperacion = findViewById(R.id.tvResultadoOperacion);
        assignButtonEvents();
    }

    private void assignButtonEvents() {
        btEscribirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = getSelectedOption();
                name = etNombreArchivo.getText().toString();
                if(option >= 0 && !name.isEmpty()) {
                    write = true;
                    checkReadContacts();
                }
            }
        });

        btLeerArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = getSelectedOption();
                name = etNombreArchivo.getText().toString();
                if(option >= 0 && !name.isEmpty()) {
                    write = false;
                    checkFile();
                }
            }
        });

        rgTipoArchivo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //otra forma de obtener la opción seleccionada
            }
        });
    }

    private void checkReadContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getContactsList();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                explainContacts();
            } else {
                //android 23
                //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISO_CONTACTOS);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISO_CONTACTOS);
            }
        } else {
            getContactsList();
        }
    }

    private void checkFile() {
        if(option == 2) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                readwriteFile();
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    explainFiles();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_ARCHIVOS);
                }
            } else {
                readwriteFile();
            }
        } else {
            readwriteFile();
        }
    }

    private void explainContacts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.contactosTitulo);
        builder.setMessage(R.string.contactosMensaje);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISO_CONTACTOS);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void explainFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.archivoTitulo);
        builder.setMessage(R.string.archivoMensaje);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_ARCHIVOS);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private int getSelectedOption() {
        int option = -1;
        int check = rgTipoArchivo.getCheckedRadioButtonId();
        switch (check) {
            case R.id.rbInterno:
                option = 0;
                break;
            case R.id.rbPrivado:
                option = 1;
                break;
            case R.id.rbPublico:
                option = 2;
                break;
            default:
                option = -1;
        }
        return option;
    }

    private void getContactsList() {
        ContactReader lector = new ContactReader(this);
        lista = lector.getContactsList(true);
        checkFile();
    }

    private void readwriteFile() {
        File file = getFileType();
        if(write) {
            File f=new File(file,name);
            Log.v(TAG, f.getAbsolutePath());
            FileWriter fw = null;
            try {
                fw = new FileWriter(f);
                fw.write("");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.v(TAG, e.toString());
            }
        } else {
            File f = new File(file, name);
            Log.v(TAG, f.getAbsolutePath());
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String linea;
                while ((linea = br.readLine()) != null) {
                }
                br.close();
            } catch (IOException e) {
                Log.v(TAG, e.toString());
            }
        }
    }

    private File getFileType() {
        File f = null;
        if(option == 0) {
            f = getFilesDir();
        } else if(option == 1) {
            f = getExternalFilesDir(null);
        } else if(option == 2) {
            f = Environment.getExternalStorageDirectory();
        }
        return f;
    }
}