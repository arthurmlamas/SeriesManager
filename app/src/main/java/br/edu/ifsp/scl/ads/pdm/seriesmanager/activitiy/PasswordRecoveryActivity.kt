package br.edu.ifsp.scl.ads.pdm.seriesmanager.activitiy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.edu.ifsp.scl.ads.pdm.seriesmanager.databinding.ActivityPasswordRecoveryBinding
import br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils.AuthenticationFirebase

class PasswordRecoveryActivity : AppCompatActivity() {
    private val activityPasswordRecoveryBinding: ActivityPasswordRecoveryBinding by lazy {
        ActivityPasswordRecoveryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityPasswordRecoveryBinding.root)
        supportActionBar?.subtitle = "Recuperar senha"

        with(activityPasswordRecoveryBinding) {
            sendEmailBt.setOnClickListener {
                val email = emailEt.text.toString()
                if (email.isNotEmpty()) {
                    AuthenticationFirebase.firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { dispatch ->
                        if (dispatch.isSuccessful) {
                            Toast.makeText(this@PasswordRecoveryActivity, "E-mail de recuperação enviado!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else {
                            Toast.makeText(this@PasswordRecoveryActivity, "Falha no envio do e-mail de recuperação!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}