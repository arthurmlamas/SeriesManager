package br.edu.ifsp.scl.ads.pdm.seriesmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityUserRegisterBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils.AuthenticationFirebase

class UserRegisterActivity : AppCompatActivity() {
    private val activityUserRegisterBinding: ActivityUserRegisterBinding by lazy {
        ActivityUserRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityUserRegisterBinding.root)
        supportActionBar?.subtitle = "Cadastrar usuário"

        with(activityUserRegisterBinding) {
            registerUserBt.setOnClickListener {
                val email = emailEt.text.toString()
                val password = passwordEt.text.toString()
                val replicatePassword = replicatePasswordEt.text.toString()
                if (password == replicatePassword) {
                    // Register user
                    AuthenticationFirebase.firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        // User registered with success
                        Toast.makeText(this@UserRegisterActivity, "Usuário $email cadastrado!", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        // Fail registering user
                        Toast.makeText(this@UserRegisterActivity, "Falha no cadastro!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@UserRegisterActivity, "Senhas não coincidem!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}