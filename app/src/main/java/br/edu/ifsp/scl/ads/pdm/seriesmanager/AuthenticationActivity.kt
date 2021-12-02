package br.edu.ifsp.scl.ads.pdm.seriesmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityAuthenticationBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils.AuthenticationFirebase

class AuthenticationActivity : AppCompatActivity() {
    private val activityAuthenticationBinding: ActivityAuthenticationBinding by lazy {
        ActivityAuthenticationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityAuthenticationBinding.root)
        supportActionBar?.subtitle = "Autenticação"

        with(activityAuthenticationBinding) {
            userRegisterBt.setOnClickListener {
                startActivity(Intent(this@AuthenticationActivity, UserRegisterActivity::class.java))
            }

            recoveryPasswordBt.setOnClickListener {
                startActivity(Intent(this@AuthenticationActivity, PasswordRecoveryActivity::class.java))
            }

            loginBt.setOnClickListener {
                val email = emailEt.text.toString()
                val password = passwordEt.text.toString()
                AuthenticationFirebase.firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    Toast.makeText(this@AuthenticationActivity, "Usuário autenticado com sucesso!", Toast.LENGTH_SHORT).show()
                    startMainActivity()
                }.addOnFailureListener {
                    Toast.makeText(this@AuthenticationActivity, "Usuário/senha incorreto(s)!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this@AuthenticationActivity, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        if (AuthenticationFirebase.firebaseAuth.currentUser != null) {
            startMainActivity()
        }
    }
}