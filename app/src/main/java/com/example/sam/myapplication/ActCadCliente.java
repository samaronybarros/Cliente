package com.example.sam.myapplication;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sam.myapplication.database.DadosOpenHelper;
import com.example.sam.myapplication.dominio.entidade.Cliente;
import com.example.sam.myapplication.dominio.repositorio.ClienteRepositorio;

//Comentário para teste do GIT
public class ActCadCliente extends AppCompatActivity {
    private EditText edtNome;
    private EditText edtEndereco;
    private EditText edtEmail;
    private EditText edtTelefone;

    private ClienteRepositorio clienteRepositorio;

    private ConstraintLayout layoutContentActCadCliente;

    private SQLiteDatabase conexao;

    private DadosOpenHelper dadosOpenHelper;

    private Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtNome     = (EditText) findViewById(R.id.edtNome);
        edtEndereco = (EditText) findViewById(R.id.edtEndereco);
        edtEmail    = (EditText) findViewById(R.id.edtEmail);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);

        layoutContentActCadCliente = (ConstraintLayout) findViewById(R.id.layoutContentActCadCliente);

        criarConexao();

        verificaParametro();
    }

    private void verificaParametro() {
        Bundle bundle = getIntent().getExtras();

        cliente = new Cliente();

        if (bundle != null && bundle.containsKey("CLIENTE")) {
            cliente = (Cliente) bundle.getSerializable("CLIENTE");

            edtNome.setText(cliente.nome);
            edtEndereco.setText(cliente.endereco);
            edtEmail.setText(cliente.email);
            edtTelefone.setText(cliente.telefone);
        }
    }

    private void criarConexao() {
        try {
            dadosOpenHelper = new DadosOpenHelper(this);
            conexao = dadosOpenHelper.getWritableDatabase();

            Snackbar.make(layoutContentActCadCliente, R.string.message_conexao_banco_sucesso, Snackbar.LENGTH_SHORT)
                    .setAction("OK", null).show();

            clienteRepositorio = new ClienteRepositorio(conexao);
        } catch (SQLException ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Erro");
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK", null);
            dlg.show();

        }
    }

    private void confirmar() {
        if (!validaCampos()) {
            try {
                if (cliente.codigo == 0) {
                    clienteRepositorio.inserir(cliente);
                } else {
                    clienteRepositorio.alterar(cliente);
                }

                finish();
            } catch (SQLException ex) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("Erro");
                dlg.setMessage(ex.getMessage());
                dlg.setNeutralButton("OK", null);
                dlg.show();

            }
         }
    }

    private boolean validaCampos() {
        boolean res = false;

        String nome     = edtNome.getText().toString();
        String endereco = edtEndereco.getText().toString();
        String email    = edtEmail.getText().toString();
        String telefone = edtTelefone.getText().toString();

        cliente.nome     = nome;
        cliente.endereco = endereco;
        cliente.email    = email;
        cliente.telefone = telefone;

        if (res = isCampoVazio(nome)) { //res = true
            edtNome.requestFocus();
        } else if (res = isCampoVazio(endereco)) {
            edtEndereco.requestFocus();
        } else if (res = isEmailValido(email)) {
            edtEmail.requestFocus();
        } else if (res = isCampoVazio(telefone)) {
            edtTelefone.requestFocus();
        }

        if (res) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(R.string.title_aviso);
            dlg.setMessage(R.string.message_campos_invalidos_brancos);
            dlg.setNeutralButton(R.string.title_menu_ok, null);
            dlg.show();
        }

        return res;
    }

    private boolean isCampoVazio(String campo) {
        boolean ret = (TextUtils.isEmpty(campo) || campo.trim().isEmpty());

        return ret;
    }

    private boolean isEmailValido(String email) {
        boolean ret = (!isCampoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());

        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_cad_cliente, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_ok):
                confirmar();
                //Toast.makeText(this, "Botão OK", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_cancelar:
                Toast.makeText(this, "Botão Cancelar", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
